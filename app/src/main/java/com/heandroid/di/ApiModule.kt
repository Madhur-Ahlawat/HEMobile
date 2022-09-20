package com.heandroid.di

import android.content.Context
import com.heandroid.BuildConfig
import com.heandroid.data.remote.*
import com.heandroid.utils.common.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(@Singleton sessionManager: SessionManager): HeaderInterceptor {
        return HeaderInterceptor(sessionManager)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectionInterceptor(context: Context): NetworkConnectionInterceptor {
        return NetworkConnectionInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideResponseConnectionInterceptor(context: Context): ResponseInterceptor {
        return ResponseInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        @ApplicationContext context: Context
    ) = TokenAuthenticator(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        responseInterceptor: ResponseInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) builder.addInterceptor(logging)
        builder.addInterceptor(headerInterceptor)
            .addInterceptor(networkConnectionInterceptor)
//            .addInterceptor(responseInterceptor)
            .authenticator(tokenAuthenticator)
            .callTimeout(0, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}