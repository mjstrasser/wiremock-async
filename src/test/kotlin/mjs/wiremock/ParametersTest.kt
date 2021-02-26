package mjs.wiremock

import com.github.tomakehurst.wiremock.extension.Parameters
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ParametersTest : DescribeSpec({
    describe("Parameters getDoubleValue extension function") {
        it("returns the default value if no value is found for the key") {
            val default = 500.0
            Parameters().getDoubleValue("median", default) shouldBe default
        }
        it("returns the value for the key if it is Double") {
            val median: Any = Parameters().also { it["median"] = 1000.0 }.getDoubleValue("median", 2000.0)
            median.shouldBeInstanceOf<Double>()
            median shouldBe 1000.0
        }
        it("returns the value for the key if it can be cast to Double") {
            val median: Any = Parameters().also { it["median"] = 1000 }.getDoubleValue("median", 2000.0)
            median.shouldBeInstanceOf<Double>()
            median shouldBe 1000.0
        }
    }
})