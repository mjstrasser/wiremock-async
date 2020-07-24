package mjs.wiremock

import java.util.*

data class CallbackContext(val request: ContractRequest, val callbackId: String = UUID.randomUUID().toString())

fun callback(context: CallbackContext, delayMillis: Long) {
    TODO("Not yet implemented")
}

