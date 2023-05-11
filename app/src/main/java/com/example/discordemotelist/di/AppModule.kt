package com.example.discordemotelist.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.example.discordemotelist.Data.DownloadServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import me.tatarka.android.apngrs.coil.ApngDecoderDecoder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideshttpclient():HttpClient {
        return HttpClient(Android){
            install(ContentNegotiation) {
                jackson()
            }
        }
    }

    @Provides
    @Singleton
    fun providesImageLoader(@ApplicationContext context: Context):ImageLoader{
        return  ImageLoader.Builder(context)
            .components {
                add(ApngDecoderDecoder.Factory())
            }
            .build()
    }


}