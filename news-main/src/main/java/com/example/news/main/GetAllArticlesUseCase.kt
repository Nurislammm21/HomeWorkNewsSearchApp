package com.example.news.main

import com.example.news.data.ArticleRepository
import com.example.news.data.model.Article
import kotlinx.coroutines.flow.Flow

class GetAllArticlesUseCase(private val repository: ArticleRepository) {

    operator  fun invoke(): Flow<Article> {
        return repository.getAll()
    }
}