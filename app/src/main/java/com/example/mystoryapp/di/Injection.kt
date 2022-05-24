package com.example.mystoryapp.di

import android.content.Context
import com.example.mystoryapp.data.StoryRepository
import com.example.mystoryapp.database.StoriesDatabase
import com.example.mystoryapp.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository{
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(apiService)
    }
}