package mjs.wiremock

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import wiremock.javax.servlet.http.HttpServletRequest
import wiremock.javax.servlet.http.HttpServletResponse
import wiremock.org.apache.http.HttpStatus
import wiremock.org.eclipse.jetty.server.Server
import wiremock.org.eclipse.jetty.server.handler.AbstractHandler
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

// Assumption: WireMock listens at http://wiremock:8080
const val WIREMOCK_SERVER = "wiremock"
const val WIREMOCK_PORT = 8080

// Assumption: This code runs in a container set from Batect Java bundle.
const val CALLBACK_SERVER = "java-build-env"
const val CALLBACK_PORT = 8090

/**
 * Functional test of the WireMock [DelayedCallback] extension.
 *
 * It is designed to be run by the Batect `callbackTest` task, where
 * WireMock runs in a container called `wiremock` and this test runs
 * in one called `java-build-env`.
 *
 * See `batect.yml` for details.
 */
@ExperimentalTime
@Suppress("BlockingMethodInNonBlockingContext") // Does not matter in these tests
class DelayedCallbackTest : DescribeSpec({

    val objectMapper = jacksonObjectMapper()
    val okClient = OkHttpClient()
    val server = Server(CALLBACK_PORT)
    var acknowledgement: String

    beforeSpec {
        // Start the callback server with that handler that stores the request body.
        server.handler = SimpleHandler { ack -> acknowledgement = ack }
        server.start()
    }

    afterSpec {
        server.stop()
    }

    describe("Callback is made after a fixed delay") {
        val correlationId = UUID.randomUUID().toString()

        val contractRequest = objectMapper.writeValueAsString(
            ContractRequest(correlationId, "Payload", "http://$CALLBACK_SERVER:$CALLBACK_PORT")
        )
        val request = Request.Builder()
            .url("http://$WIREMOCK_SERVER:$WIREMOCK_PORT/fixed-1000")
            .post(contractRequest.toRequestBody("application/json".toMediaType()))
            .build()

        acknowledgement = "None"

        // Make the request and verify the immediate response.
        okClient.newCall(request).execute().use { response ->
            response.code shouldBe 200
            val responseBody = response.body?.string() ?: "{}"
            val callResponse = objectMapper.readValue<ContractResponse>(responseBody)
            callResponse.correlationId shouldBe correlationId
            callResponse.message shouldBe "Acknowledged the request. Will call back after 1000 ms"
        }

        // Allow an extra second to verify the callback.
        eventually(2.seconds) {
            acknowledgement shouldNotBe "None"
            val ackResponse = objectMapper.readValue<ContractResponse>(acknowledgement)
            ackResponse.correlationId shouldBe correlationId
            ackResponse.message shouldBe "All processing complete"
        }
    }
})

/**
 * Simple Jetty [AbstractHandler] class that calls the [acknowledge]
 * function with the body of the request.
 */
private class SimpleHandler(val acknowledge: (String) -> Unit) : AbstractHandler() {
    override fun handle(
        target: String,
        baseRequest: wiremock.org.eclipse.jetty.server.Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        acknowledge(request.reader.readText())
        response.status = HttpStatus.SC_OK
        response.writer.write("OK")
        baseRequest.isHandled = true
    }
}
