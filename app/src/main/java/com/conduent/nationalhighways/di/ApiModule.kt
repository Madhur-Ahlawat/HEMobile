package com.conduent.nationalhighways.di

import android.content.Context
import com.conduent.nationalhighways.BuildConfig
import com.conduent.nationalhighways.data.remote.*
import com.conduent.nationalhighways.ui.loader.RetryCallback
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
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
    fun provideRetryListener(): RetryCallback {
        return RetryCallback.RetryListenerImpl()
    }

    @Provides
    @Singleton
    fun provideHeaderInterceptor(
        @Singleton sessionManager: SessionManager,
        context: Context,
        retryListener: RetryCallback,
    ): HeaderInterceptor {
        return HeaderInterceptor(sessionManager, context)
    }


    @Provides
    @Singleton
    fun provideNetworkConnectionInterceptor(context: Context): NetworkConnectionInterceptor {
        return NetworkConnectionInterceptor(context)
    }

    @Provides
    @Singleton
    fun provideNetworkRetryInterceptor(
        context: Context, retryListener: RetryCallback,
    ): TimeoutRetryInterceptor {
        return TimeoutRetryInterceptor(context, retryListener)
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
        @ApplicationContext context: Context,
        logging: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor,
        timeOutRetryInterceptor: TimeoutRetryInterceptor,
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        responseInterceptor: ResponseInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) builder.addInterceptor(logging)
        builder.addInterceptor(headerInterceptor)
            .addInterceptor(timeOutRetryInterceptor)
            .callTimeout(0, TimeUnit.SECONDS)
            .connectTimeout(
                Constants.TIME_OUT_SEC, TimeUnit.SECONDS
            )
            .readTimeout(
                Constants.TIME_OUT_SEC, TimeUnit.SECONDS
            )
            .writeTimeout(
                Constants.TIME_OUT_SEC, TimeUnit.SECONDS
            )
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