package com.example.mystoryapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystoryapp.data.StoryRepository
import com.example.mystoryapp.di.Injection
import com.example.mystoryapp.response.ListStoryItem

class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun story(token: String):LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories(token).cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}