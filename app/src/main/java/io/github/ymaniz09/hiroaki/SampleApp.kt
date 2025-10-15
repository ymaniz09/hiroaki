package io.github.ymaniz09.hiroaki

import android.app.Activity
import androidx.multidex.MultiDexApplication
import io.github.ymaniz09.hiroaki.data.service.MoshiNewsApiService
import io.github.ymaniz09.hiroaki.di.provideNewsService

open class SampleApp : MultiDexApplication() {

    open fun newsService(): MoshiNewsApiService =
            provideNewsService()
}

fun Activity.getApp() = application as SampleApp
