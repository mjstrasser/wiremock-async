package mjs.wiremock

import java.time.Instant
import java.util.*

data class ContractRequest(
        val correlationId: String,
        val payload: String,
        val callbackUrl: String,
        val timestamp: String,
        val expires: String
)

data class ContractResponse(
        val correlationId: String = UUID.randomUUID().toString(),
        val message: String,
        val timestamp: String = Instant.now().toString()
)
