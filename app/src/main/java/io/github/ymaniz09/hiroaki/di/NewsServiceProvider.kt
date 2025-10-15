package io.github.ymaniz09.hiroaki.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.ymaniz09.hiroaki.data.service.MoshiNewsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

fun provideNewsService(client: OkHttpClient = provideOkHttpClient()): MoshiNewsApiService =
        Retrofit.Builder().baseUrl("https://newsapi.org")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(provideMoshi())).build()
                .create(MoshiNewsApiService::class.java)
