package com.parth.pestotest.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.parth.pestotest.network.firebase.FirebaseDataListen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
            "App_Shared_Pref",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun firebaseData(preferences: SharedPreferences) =
        FirebaseDataListen(preferences)
}