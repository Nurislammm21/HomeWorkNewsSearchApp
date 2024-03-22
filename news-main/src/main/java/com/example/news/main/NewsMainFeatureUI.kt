package com.example.news.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

@Composable
 fun NewsMain( ){
        NewsMain(viewModel = viewModel())
}


@Composable
internal fun NewsMain(viewModel: NewsMainVIewModel){
  val state by viewModel.state.collectAsState()
  when(val currentState = state){
      is State.Success -> Articles(currentState)
      is State.Error -> TODO()
      is State.Loading -> TODO()
      State.None -> TODO()
  }

}

@Composable
    private fun Articles(state: State.Success){
        LazyColumn {
           items(state.articles){article ->
               key(article.id){
                        Article(article = article)
               }
           }


        }

    }

@Composable
 internal fun Article(@PreviewParameter(ArticlePreviewProvider::class)article: ArticleUI
 ) {
Column{
    Text(text = article.title, style = MaterialTheme.typography.headlineMedium, maxLines = 1)
    Spacer(modifier = Modifier.size(8.dp))
    Text(text = article.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
}


 }

private class ArticlePreviewProvider: PreviewParameterProvider<ArticleUI>{
    override val values = sequenceOf(
        ArticleUI(1,"Android Studio Iguana is Stable!",
            "New stable version on Android IDE has been realized",
            imageUrl = null,
            url = "",
            ),

        ArticleUI(2,"Gemini 1.5 Release!",
            "Upgraded version of Google AI is available",
            imageUrl = null,
            url = "",
        ),

        ArticleUI(3,"Shape animations (10 min)",
            "How to use shape transform animations in Compose",
            imageUrl = null,
            url = "",
        ),
    )

}