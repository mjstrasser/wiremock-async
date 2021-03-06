package mjs.wiremock

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

val objectMapper = jacksonObjectMapper()

private val logger = KotlinLogging.logger {}

fun ContractRequest.callback() {

    val okClient = OkHttpClient()

    val body = objectMapper.writeValueAsString(ContractResponse(correlationId, "All processing complete"))
    val request = Request.Builder()
        .url(callbackUrl)
        .post(body.toRequestBody("application/json".toMediaType()))
        .build()

    okClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful)
            logger.error("Error calling back: ${response.message}")
        else
            logger.info("Callback successful: ${response.message}")
    }
}
