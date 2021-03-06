package mjs.wiremock

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseTransformer
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response
import mu.KotlinLogging
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.math.exp
import kotlin.math.roundToLong

class DelayedCallback : ResponseTransformer() {

    private val logger = KotlinLogging.logger {}

    companion object {
        @JvmStatic
        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(10)
        val objectMapper = jacksonObjectMapper()
    }

    override fun getName() = "DelayedCallback"

    override fun applyGlobally() = false

    override fun transform(request: Request, response: Response, files: FileSource, parameters: Parameters): Response {

        val contractRequest = try {
            objectMapper.readValue<ContractRequest>(request.bodyAsString)
        } catch (ex: Exception) {
            logger.error("Exception reading contract request", ex)
            return Response.Builder.like(response)
                .status(400)
                .body(ex.message)
                .build()
        }

        val delayMillis = callbackDelayMillis(parameters)
        logger.info("Callback will be made after $delayMillis ms")

        executor.schedule({ contractRequest.callback() }, delayMillis, TimeUnit.MILLISECONDS)

        val result = ContractResponse(
            contractRequest.correlationId,
            "Acknowledged the request. Will call back after $delayMillis ms",
        )
        return Response.Builder.like(response)
            .status(200)
            .body(objectMapper.writeValueAsString(result))
            .headers(HttpHeaders(HttpHeader.httpHeader("Content-Type", "application/json")))
            .build()
    }

    private fun callbackDelayMillis(parameters: Parameters?) =
        parameters?.let {
            val median = it.getDoubleValue("median", 1000.0)
            val sigma = it.getDoubleValue("sigma", 0.1)
            randomLogNormalMillis(median, sigma)
        } ?: 1000L

    private fun randomLogNormalMillis(median: Double, sigma: Double) =
        (exp(ThreadLocalRandom.current().nextGaussian() * sigma) * median).roundToLong()
}
