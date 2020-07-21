package mjs.wiremock

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object CallbackSpek : Spek({

    describe("Callback") {
        it("works OK") {
            assertThat("this").isNotEqualTo("that")
        }
    }

})