package com.example.news.data

import com.example.database.NewsDatabase
import com.example.database.models.ArticleDBO
import com.example.news.data.model.Article
import com.example.newsapi.NewsApi
import com.example.newsapi.models.ArticleDTO
import com.example.newsapi.models.ResponseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach


class ArticleRepository(
    private val database: NewsDatabase,
    private val api: NewsApi,
    private val requestResponseMergeStrategy: RequestResponseMergeStrategy<List<Article>>,
) {
        fun getAll(): Flow<RequestResult<List<Article>>> {
           val cachedAllArticles: Flow<RequestResult<List<Article>>> = getAllFromDatabase()
               .map { result ->
                   result.map { articleDbos ->
                       articleDbos.map { it.toArticle() }
                   }
               }
            val remoteArticles = getAllFromServer()
                .map { result: RequestResult<ResponseDTO<ArticleDTO>> ->
                    result.map { response ->
                        response.articles.map { it.toArticle() }
                    }
                }
              return cachedAllArticles.combine(remoteArticles){ dbos, dtos ->
                  requestResponseMergeStrategy.merge(dbos,dtos)
              }

        }

    private fun getAllFromServer(): Flow<RequestResult<ResponseDTO<ArticleDTO>>>{
      val apiRequest = flow{emit(api.everything())}
          .onEach { result ->
              if(result.isSuccess){
                  saveNetResponseToCache(checkNotNull(result.getOrThrow()).articles)
              }
          }
          .map { it.toRequestResult() }
        val start = flowOf<RequestResult<ResponseDTO<ArticleDTO>>>(RequestResult.InProgress())
        return merge(apiRequest,start)
    }

    private suspend fun saveNetResponseToCache(data: List<ArticleDTO>){
        val dbos = data.map { articleDto ->  articleDto.toArticleDbo() }
        database.articleDao.insert(dbos)
    }

    private fun getAllFromDatabase(): Flow<RequestResult<List<ArticleDBO>>>{
        val dbRequest: Flow<RequestResult<List<ArticleDBO>>> = database.articleDao
            .getAll().map { RequestResult.Success(it) }
        val start = flowOf<RequestResult<List<ArticleDBO>>>(RequestResult.InProgress())
        return merge(start,dbRequest)
    }



    suspend fun search(query: String): Flow<Article> {
        api.everything()
        TODO("Not implemented")
    }
}






