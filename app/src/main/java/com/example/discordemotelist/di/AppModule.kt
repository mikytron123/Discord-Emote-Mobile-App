package com.example.discordemotelist.di

import android.content.Context
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import me.tatarka.android.apngrs.coil.ApngDecoderDecoder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideshttpclient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                jackson()
            }
        }
    }

    @Provides
    @Singleton
    fun providesImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(ApngDecoderDecoder.Factory())
            }
            .build()
    }
}
