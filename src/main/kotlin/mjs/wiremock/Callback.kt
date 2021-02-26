package mjs.wiremock

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

val objectMapper = jacksonObjectMapper()

private val logger = KotlinLogging.logger {}

data class CallbackContext(
    val contractRequest: ContractRequest,
    val callbackId: String = UUID.randomUUID().toString()
)

fun CallbackContext.callback() {

    val okClient = OkHttpClient()

    val body = objectMapper.writeValueAsString(ContractResponse(callbackId, "All processing complete"))
    val request = Request.Builder()
        .url(contractRequest.callbackUrl)
        .post(body.toRequestBody("application/json".toMediaType()))
        .build()

    okClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful)
            logger.error("Error calling back: ${response.message}")
        else
            logger.info("Callback successful: ${response.message}")
    }
}
