package mjs.wiremock

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.github.tomakehurst.wiremock.extension.Parameters
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DelayedCallbackSpek : Spek({

    describe("DelayedCallback") {
        it("is called DelayedCallback") {
            assertThat(DelayedCallback().name).isEqualTo("DelayedCallback")
        }
        it("does not apply globally") {
            assertThat(DelayedCallback().applyGlobally()).isFalse()
        }
    }

    describe("Parameters getDoubleValue extension function") {
        it("returns the default value if no value is found for the key") {
            val default = 500.0
            assertThat(Parameters().getDoubleValue("median", default)).isEqualTo(default)
        }
        it("returns the value for the key if it is Double") {
            val median: Any = Parameters().also { it["median"] = 1000.0 }.getDoubleValue("median", 2000.0)
            assertThat(median is Double).isTrue()
            assertThat(median).isEqualTo(1000.0)
        }
        it("returns the value for the key if it can be cast to Double") {
            val median: Any = Parameters().also { it["median"] = 1000 }.getDoubleValue("median", 2000.0)
            assertThat(median is Double).isTrue()
            assertThat(median).isEqualTo(1000.0)
        }
    }

})
