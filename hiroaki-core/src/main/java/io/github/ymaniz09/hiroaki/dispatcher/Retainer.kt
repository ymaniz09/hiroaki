package io.github.ymaniz09.hiroaki.dispatcher

import okhttp3.mockwebserver.RecordedRequest

interface Retainer {
    fun dispatchedRequests(): List<RecordedRequest>

    @Throws(Exception::class)
    fun <T : Any> fileContentAsString(fileName: String, receiver: T): String
}
