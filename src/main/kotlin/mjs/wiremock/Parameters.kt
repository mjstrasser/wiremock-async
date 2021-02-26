package mjs.wiremock

import com.github.tomakehurst.wiremock.extension.Parameters

/**
 * Extension function for WireMock [Parameters] class to handle [Double] values.
 */
fun Parameters.getDoubleValue(key: String, default: Double) = if (key in this)
    when (val value = get(key)) {
        is Double -> value
        is Int -> value.toDouble()
        else -> default
    }
else default