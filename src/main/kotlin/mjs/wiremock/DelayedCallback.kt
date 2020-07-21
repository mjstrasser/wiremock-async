package mjs.wiremock

import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.extension.Parameters
import com.github.tomakehurst.wiremock.extension.ResponseTransformer
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.http.Response

class DelayedCallback : ResponseTransformer() {

    override fun getName() = "DelayedCallback"

    override fun transform(request: Request, response: Response, files: FileSource, parameters: Parameters): Response {

        return response
    }
}