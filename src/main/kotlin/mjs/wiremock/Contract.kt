package mjs.wiremock

import java.time.Instant

data class ContractRequest(
    val correlationId: String,
    val payload: String,
    val callbackUrl: String,
)

data class ContractResponse(
    val correlationId: String,
    val message: String,
    val timestamp: String = Instant.now().toString(),
)
