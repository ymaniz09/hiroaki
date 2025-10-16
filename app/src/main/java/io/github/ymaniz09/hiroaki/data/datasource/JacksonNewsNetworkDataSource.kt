package io.github.ymaniz09.hiroaki.data.datasource

import io.github.ymaniz09.hiroaki.data.networkdto.toArticle
import io.github.ymaniz09.hiroaki.data.networkdto.toJacksonDto
import io.github.ymaniz09.hiroaki.data.service.JacksonNewsApiService
import io.github.ymaniz09.hiroaki.model.Article
import java.io.IOException

/**
 * Basic sample class to request data from network.
 */
class JacksonNewsNetworkDataSource(private val service: JacksonNewsApiService) {

    @Throws(IOException::class)
    fun getNews(): List<Article> {
        val response = service.getNews().execute()
        if (response.isSuccessful) {
            return response.body()!!.articles.map { it.toArticle() }
        } else throw IOException("Coins not found.")
    }

    /**
     * This is a no-op method on the real news API, just created for testing purposes.
     */
    @Throws(IOException::class)
    fun publishHeadline(article: Article) {
        val response = service.publishHeadline(article.toJacksonDto()).execute()
        if (!response.isSuccessful) {
            throw IOException("Coins not found.")
        }
    }
}
