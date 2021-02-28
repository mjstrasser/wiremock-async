package mjs.wiremock

import com.github.tomakehurst.wiremock.extension.Parameters
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ParametersTest : DescribeSpec({
    describe("Parameters getDoubleValue extension function") {
        it("returns the default if no value is found for the key") {
            val parameters = Parameters()
            val default = 500.0
            parameters.getDoubleValue("median", default) shouldBe default
        }
        it("returns the value for the key if it is Double") {
            val parameters = Parameters().also { it["median"] = 1000.0 }
            val median = parameters.getDoubleValue("median", 2000.0)
            median.shouldBeInstanceOf<Double>()
            median shouldBe 1000.0
        }
        it("returns the value for the key if it can be cast to Double") {
            val parameters = Parameters().also { it["median"] = 1000 }
            val median = parameters.getDoubleValue("median", 2000.0)
            median.shouldBeInstanceOf<Double>()
            median shouldBe 1000.0
        }
        it("returns the value for the key if a string value can be parsed as Double") {
            val parameters = Parameters().also { it["median"] = "1000.0" }
            val median = parameters.getDoubleValue("median", 2000.0)
            median.shouldBeInstanceOf<Double>()
            median shouldBe 1000.0
        }
        it("returns the default if a string value cannot be parsed as Double") {
            val parameters = Parameters().also { it["median"] = "Not a double" }
            val default = 1500.0
            parameters.getDoubleValue("median", default) shouldBe default
        }
    }
})