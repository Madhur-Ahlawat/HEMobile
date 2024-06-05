package com.conduent.nationalhighways.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.conduent.nationalhighways.data.error.errorUsecase.ErrorManager
import com.conduent.nationalhighways.data.error.mapper.ErrorMapper
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideEncryptedSharedPref(@ApplicationContext context: Context): SharedPreferences {
       return Utils.returnSharedPreference(context)
    }

    @Singleton
    @Provides
    internal fun provideSessionManager(sharedPreferences: SharedPreferences): SessionManager {
        return SessionManager(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideErrorManager(errorMapper: ErrorMapper): ErrorManager {
        return ErrorManager(errorMapper)
    }
    @Provides
    @Singleton
    fun provideErrorMapper(@ApplicationContext context: Context): ErrorMapper {
        return ErrorMapper(context)
    }
}