package mjs.wiremock

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DelayedCallbackSpek : Spek({

    describe("DelayedCallback") {
        it("is called DelayedCallback") {
            assertThat(DelayedCallback().name).isEqualTo("DelayedCallback")
        }
    }

})