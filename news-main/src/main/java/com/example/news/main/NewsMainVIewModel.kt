package com.example.news.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.data.ArticleRepository
import com.example.news.data.RequestResult
import com.example.news.data.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class NewsMainVIewModel(
    private val getAllArticlesUseCase: GetAllArticlesUseCase,
    private val repository: ArticleRepository
): ViewModel()
{
val state: StateFlow<State> = getAllArticlesUseCase()
    .map { it.toState() }
    .stateIn(viewModelScope, SharingStarted.Lazily,State.None)

    fun forceUpdate(){

    }


    private fun RequestResult<List<Article>>.toState(): State{
        return when(this){
            is RequestResult.Error -> State.Error()
            is RequestResult.InProgress -> State.Loading(data)
            is RequestResult.Success -> State.Success(checkNotNull(data))
        }
    }

}



sealed class State{

    object None : State()

    class Loading(val articles : List<Article>? = null) : State()

    class Error(val articles : List<Article>? = null) : State()

    class Success(val articles : List<Article>) : State()

}