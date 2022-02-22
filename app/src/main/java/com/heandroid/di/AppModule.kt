package com.heandroid.di

import android.app.Application
import android.content.Context
import com.heandroid.utils.common.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    internal fun provideContext(application: Application): Context {
        return application
    }

    @Singleton
    @Provides
    internal fun provideSessionManager(context: Context): SessionManager {
        return SessionManager(context)
    }
}