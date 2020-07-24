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
import java.lang.Math.exp
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class DelayedCallback : ResponseTransformer() {

    companion object {
        @JvmStatic
        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(10)
        val objectMapper = jacksonObjectMapper()
    }

    override fun getName() = "DelayedCallback"

    override fun applyGlobally() = false

    override fun transform(request: Request, response: Response, files: FileSource, parameters: Parameters): Response {
        val context = CallbackContext(objectMapper.readValue(request.bodyAsString))
        val delayMillis = callbackDelayMillis(parameters)

        executor.schedule({ callback(context, delayMillis) }, delayMillis, TimeUnit.MILLISECONDS)

        val result = ContractResponse(
                context.callbackId,
                "Acknowledged the request. Will call back after $delayMillis milliseconds"
        )
        return Response.Builder.like(response)
                .status(200)
                .body(objectMapper.writeValueAsString(result))
                .headers(HttpHeaders(HttpHeader.httpHeader("Content-Type", "application/json")))
                .build()
    }

    private fun callbackDelayMillis(parameters: Parameters?) =
            parameters?.let {
                val median = it.getValueOrDefault("median", 1000).toDouble()
                val sigma = it.getValueOrDefault("sigma", 0.1)
                randomLogNormal(median, sigma)
            } ?: 0L

    private fun randomLogNormal(median: Double, sigma: Double) =
            (exp(ThreadLocalRandom.current().nextGaussian() * sigma) * median).roundToLong()
}

/**
 * Get the value from a [Parameters] object if present, otherwise the specified default.
 */
inline fun <reified T> Parameters.getValueOrDefault(key: String, default: T) = when (val value = getValue(key)) {
    is T -> value
    else -> default
}
