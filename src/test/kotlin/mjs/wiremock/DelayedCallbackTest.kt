package mjs.wiremock

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DelayedCallbackTest : DescribeSpec({
    describe("DelayedCallback") {
        it("is called DelayedCallback") {
            DelayedCallback().name shouldBe "DelayedCallback"
        }
        it("does not apply globally") {
            DelayedCallback().applyGlobally() shouldBe false
        }
    }
})
