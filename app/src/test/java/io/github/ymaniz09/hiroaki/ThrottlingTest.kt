package io.github.ymaniz09.hiroaki

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import io.github.ymaniz09.hiroaki.data.datasource.JacksonNewsNetworkDataSource
import io.github.ymaniz09.hiroaki.internal.MockServerRule
import io.github.ymaniz09.hiroaki.models.fileBody
import io.github.ymaniz09.hiroaki.models.success
import io.github.ymaniz09.hiroaki.models.throttle
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class ThrottlingTest {

    private lateinit var dataSource: JacksonNewsNetworkDataSource
    @get:Rule
    val rule: MockServerRule = MockServerRule()

    @Before
    fun setup() {
        dataSource = JacksonNewsNetworkDataSource(
            rule.server.retrofitService(JacksonConverterFactory.create())
        )
    }

    @Test
    fun throttlesMockResponse() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(
                success(jsonBody = fileBody("GetNews.json"))
                    .throttle(1024, 1000)
            )

        val beforeTheQuery = System.currentTimeMillis()
        runBlocking { dataSource.getNews() }
        val afterTheQuery = System.currentTimeMillis()

        assertTrue(afterTheQuery - beforeTheQuery > 1000)
    }

    @Test
    fun throttlesMockResponseBody() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(
                success(jsonBody = fileBody("GetNews.json"))
                    .throttleBody(1024, 1, TimeUnit.SECONDS)
            )

        val beforeTheQuery = System.currentTimeMillis()
        runBlocking { dataSource.getNews() }
        val afterTheQuery = System.currentTimeMillis()

        assertTrue(afterTheQuery - beforeTheQuery > 1000)
    }

    @Test
    fun respondsInstantlyForNonThrottledResponse() {
        rule.server.whenever(Method.GET, "v2/top-headlines")
            .thenRespond(success(jsonBody = fileBody("GetNews.json")))

        val beforeTheQuery = System.currentTimeMillis()
        runBlocking { dataSource.getNews() }
        val afterTheQuery = System.currentTimeMillis()

        assertTrue(afterTheQuery - beforeTheQuery < 1000)
    }
}
