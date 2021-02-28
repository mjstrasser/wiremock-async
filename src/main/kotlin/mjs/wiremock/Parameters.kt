package mjs.wiremock

import com.github.tomakehurst.wiremock.extension.Parameters

/**
 * Extension function for WireMock [Parameters] class to handle [Double] values.
 */
fun Parameters.getDoubleValue(key: String, default: Double) = if (key in this)
    when (val value = get(key)) {
        is Double -> value
        is Int -> value.toDouble()
        is String -> value.toDoubleOrNull() ?: default
        else -> default
    }
else default
