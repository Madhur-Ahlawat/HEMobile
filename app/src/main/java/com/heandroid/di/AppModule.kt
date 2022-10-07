package com.heandroid.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.heandroid.data.error.errorUsecase.ErrorManager
import com.heandroid.data.error.mapper.ErrorMapper
import com.heandroid.utils.common.SessionManager
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
        val masterAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "HE_MOBILE",
            masterAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
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