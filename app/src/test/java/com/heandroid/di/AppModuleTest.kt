//package com.heandroid.di
//
//import android.app.Application
//import android.content.Context
//import com.heandroid.data.error.errorUsecase.ErrorManager
//import com.heandroid.data.error.mapper.ErrorMapper
//import com.heandroid.utils.common.SessionManager
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.android.testing.HiltTestApplication
//import dagger.hilt.components.SingletonComponent
//import dagger.hilt.testing.TestInstallIn
//import javax.inject.Singleton
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [AppModule::class]
//)
//class AppModuleTest {
//
//    @Singleton
//    @Provides
//    internal fun provideContext(application: HiltTestApplication): Context {
//        return application
//    }
//
//    @Singleton
//    @Provides
//    internal fun provideSessionManager(context: Context): SessionManager {
////        class TestSessionManager : SessionManager() {
////
////        }
//
//
//
//        return SessionManager(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideErrorManager(errorMapper: ErrorMapper): ErrorManager {
//        return ErrorManager(errorMapper)
//    }
//    @Provides
//    @Singleton
//    fun provideErrorMapper(@ApplicationContext context: Context): ErrorMapper {
//        return ErrorMapper(context)
//    }
//}