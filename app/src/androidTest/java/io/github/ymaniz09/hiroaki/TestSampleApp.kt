package io.github.ymaniz09.hiroaki

import io.github.ymaniz09.hiroaki.data.service.MoshiNewsApiService

class TestSampleApp : SampleApp() {

    lateinit var service: MoshiNewsApiService

    override fun newsService(): MoshiNewsApiService = service
}
