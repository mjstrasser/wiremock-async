package mjs.wiremock

import com.benasher44.uuid.uuid4
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

val objectMapper = jacksonObjectMapper()

data class CallbackContext(val request: ContractRequest, val callbackId: String = uuid4().toString())

private val logger = KotlinLogging.logger {}

fun callback(context: CallbackContext) {

    val okClient = OkHttpClient()

    val body = objectMapper.writeValueAsString(ContractResponse(context.callbackId, "All processing complete"))
    val request = Request.Builder()
            .url(context.request.callbackUrl)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()

    okClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful)
            logger.error("Error calling back: ${response.message}")
        else
            logger.info("Callback successful: ${response.message}")
    }
}
