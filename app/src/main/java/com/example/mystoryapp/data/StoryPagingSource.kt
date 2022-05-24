package com.example.mystoryapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystoryapp.response.ListStoryItem
import com.example.mystoryapp.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String): PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPostition ->
            val anchorPage = state.closestPageToPosition(anchorPostition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoriesWithPaging("Bearer $token", position, params.loadSize)

            Log.d("ResponseData", responseData.toString())

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position -1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else position +1
            )
        } catch (exception: Exception){
            return LoadResult.Error(exception)
        }
    }

    companion object{
        const val INITIAL_PAGE_INDEX = 1
        const val TOKEN = "token"
    }
}