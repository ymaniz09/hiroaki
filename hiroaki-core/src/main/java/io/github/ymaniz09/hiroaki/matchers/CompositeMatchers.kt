package io.github.ymaniz09.hiroaki.matchers

import io.github.ymaniz09.hiroaki.Headers
import io.github.ymaniz09.hiroaki.Method
import io.github.ymaniz09.hiroaki.QueryParams
import io.github.ymaniz09.hiroaki.json.fileContentAsString
import io.github.ymaniz09.hiroaki.json.fromJson
import io.github.ymaniz09.hiroaki.models.Body
import io.github.ymaniz09.hiroaki.models.Body.JsonBody
import io.github.ymaniz09.hiroaki.models.Body.JsonBodyFile
import io.github.ymaniz09.hiroaki.models.Body.Json
import io.github.ymaniz09.hiroaki.models.Body.JsonArray
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

/**
 * Creator function that provides highly configurable matchers for MockRequests.
 */
fun <T : Any> T.matches(
    sentToPath: String,
    queryParams: QueryParams? = null,
    jsonBody: Body? = null,
    headers: Headers? = null,
    method: Method? = null
): Matcher<RecordedRequest> {
    val matchers = mutableListOf(isSentToPath("/$sentToPath"))

    queryParams?.let {
        matchers.add(hasQueryParams(queryParams))
    }
    jsonBody?.let { body ->
        when (body) {
            is JsonBody -> matchers.add(
                    hasBody(
                            body.jsonBody,
                            body.jsonBody.fromJson()
                    )
            )
            is JsonBodyFile -> {
                val fileStringBody = fileContentAsString(body.jsonBodyResFile)
                matchers.add(
                        hasBody(
                                fileStringBody,
                                fileStringBody.fromJson()
                        )
                )
            }
            is Json -> matchers.add(
                    hasBody(
                            body.toJsonString(),
                            body.toJsonString().fromJson()
                    )
            )
            is JsonArray<*> -> matchers.add(
                    hasBody(
                            body.array.joinToString(),
                            body.toJsonString().fromJson()
                    )
            )
        }
    }
    headers?.let {
        matchers.add(hasHeaders(it))
    }
    method?.let {
        matchers.add(hasMethod(method))
    }

    return allOf(matchers)
}
