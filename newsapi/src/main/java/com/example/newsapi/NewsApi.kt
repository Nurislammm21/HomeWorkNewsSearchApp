package com.example.newsapi

import androidx.annotation.IntRange
import com.example.newsapi.models.Article
import com.example.newsapi.models.Language
import com.example.newsapi.models.Response
import com.example.newsapi.models.SortBy
import com.example.newsapi.utils.NewsApiKeyInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.Date

interface NewsApi {

        @GET("/everything")
       suspend fun everything(
            @Header("X-Api-Key") apiKey: String,
            @Query("q") query: String? = null,
            @Query("from") from: Date? = null,
            @Query("to") to: Date? = null,
            @Query("languages") languages: List<Language>? = null,
            @Query("sortBy") sortBy: SortBy? = null,
            @Query("pageSize") @IntRange(from =  0, to = 100) pageSize: Int = 100,
            @Query("page") @IntRange(from = 1) page: Int = 1

        ): Result<Response<Article>>

}


fun newsApi(
    baseUrl: String,
    apiKey: String,
    okHttpClient: OkHttpClient? = null,
    json: Json = Json
): NewsApi{

    return retrofit(baseUrl,apiKey,okHttpClient,json).create()
}

@Suppress("SuspiciousIndentation")
private fun retrofit(baseUrl: String,
                     apiKey: String,
                     okHttpClient: OkHttpClient? = null,
                     json: Json,
) : Retrofit {

    val jsonConverterFactory = Json.asConverterFactory(MediaType.get("application/json"))

    val modifiedOkHttpClient: OkHttpClient =  (okHttpClient?.newBuilder() ?: OkHttpClient.Builder()).
    addInterceptor(NewsApiKeyInterceptor(apiKey)).build()


    return Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(jsonConverterFactory)
        .addCallAdapterFactory(ResultCallAdapterFactory.create())
        .client(modifiedOkHttpClient).
        build()
}



