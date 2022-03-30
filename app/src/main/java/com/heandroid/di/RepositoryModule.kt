package com.heandroid.di

import com.heandroid.data.remote.ApiService
import com.heandroid.data.repository.account.AccountCreationRepository
import com.heandroid.data.repository.auth.ForgotEmailRepository
import com.heandroid.data.repository.auth.ForgotPasswordRepository
import com.heandroid.data.repository.auth.LoginRepository
import com.heandroid.data.repository.auth.LogoutRepository
import com.heandroid.data.repository.contactdartcharge.ContactDartChargeRepository
import com.heandroid.data.repository.dashboard.DashBoardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginRepository(apiService: ApiService): LoginRepository {
        return LoginRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideLogoutRepository(apiService: ApiService): LogoutRepository {
        return LogoutRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideForgotPasswordRepository(apiService: ApiService): ForgotPasswordRepository {
        return ForgotPasswordRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideForgotEmailRepository(apiService: ApiService): ForgotEmailRepository {
        return ForgotEmailRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideAccountCreationRepository(apiService: ApiService): AccountCreationRepository {
        return AccountCreationRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideAccountContactDartChargeRepository(apiService: ApiService): ContactDartChargeRepository {
        return ContactDartChargeRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideDashboardRepository(apiService: ApiService):DashBoardRepository {
        return DashBoardRepository(apiService)
    }

}