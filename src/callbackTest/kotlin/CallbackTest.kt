import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import mjs.wiremock.ContractRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import wiremock.javax.servlet.http.HttpServletRequest
import wiremock.javax.servlet.http.HttpServletResponse
import wiremock.org.apache.http.HttpStatus
import wiremock.org.eclipse.jetty.server.Server
import wiremock.org.eclipse.jetty.server.ServerConnector
import wiremock.org.eclipse.jetty.server.handler.AbstractHandler
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

const val WIREMOCK_SERVER = "wiremock"
const val WIREMOCK_PORT = 8080
const val CALLBACK_SERVER = "java-build-env" // From Batect Java bundle, where this code will run
const val CALLBACK_PORT = 8090
const val CALLBACK_ACK = "Called back"

@ExperimentalTime
class CallbackTest : DescribeSpec({

    val objectMapper = jacksonObjectMapper()
    val okClient = OkHttpClient()
    val server = Server(CALLBACK_PORT)
    var acknowledgement: String

    beforeSpec {
        startCallbackServer(server, SimpleHandler { ack -> acknowledgement = ack })
    }

    afterSpec {
        server.stop()
    }

    describe("Callback is made after a fixed delay") {
        val body = objectMapper.writeValueAsString(
            ContractRequest(
                UUID.randomUUID().toString(),
                "Payload",
                "http://$CALLBACK_SERVER:$CALLBACK_PORT",
            )
        )
        val request = Request.Builder()
            .url("http://$WIREMOCK_SERVER:$WIREMOCK_PORT/fixed-1000")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

        acknowledgement = "None"
        okClient.newCall(request).execute().use { response ->
            response.code shouldBe 200
        }
        eventually(2.seconds) {
            acknowledgement shouldBe CALLBACK_ACK
        }
    }
})

class SimpleHandler(val acknowledge: (String) -> Unit) : AbstractHandler() {
    override fun handle(
        target: String,
        baseRequest: wiremock.org.eclipse.jetty.server.Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        acknowledge(CALLBACK_ACK)
        response.status = HttpStatus.SC_OK
        response.writer.write("OK")
        baseRequest.isHandled = true
    }
}

fun startCallbackServer(server: Server, handler: AbstractHandler) {
    server.handler = handler
    server.start()
}
