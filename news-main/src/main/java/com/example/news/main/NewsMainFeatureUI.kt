package com.example.news.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.news.uikit.NewsTheme

@Composable
fun NewsMainScreen() {
    NewsMainScreen(viewModel = viewModel())
}

@Composable
internal fun NewsMainScreen(viewModel: NewsMainVIewModel) {
    val state by viewModel.state.collectAsState()
    val currentState = state
    if (state != State.None) {
        NewsMainContent(currentState = currentState)
    }
}

@Composable
private fun NewsMainContent(currentState: State) {
    Column {
        if (currentState is State.Error) {
            ErrorMessage(currentState)
        }
        if (currentState is State.Loading) {
            ProgressIndicator(currentState)
        }

        if (currentState.articles != null) {
            Articles(articles = currentState.articles)
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun ErrorMessage(state: State.Error) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(NewsTheme.colorScheme.error)
            .padding(8.dp),
        contentAlignment = Alignment.Center

    ) {
        Text(text = "Error during update", color = NewsTheme.colorScheme.onError)
    }
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun ProgressIndicator(state: State.Loading) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun Articles(
    @PreviewParameter(ArticlesPreviewProvider::class, limit = 1)articles: List<ArticleUI>,
) {
    LazyColumn {
        items(articles) { article ->
            key(article.id) {
                Article(article = article)
            }
        }
    }
}

@Preview
@Composable
internal fun Article(
    @PreviewParameter(ArticlePreviewProvider::class, limit = 1)article: ArticleUI,
) {
    Row(Modifier.padding(bottom = 4.dp)) {
        article.imageUrl?.let { imageUrl ->
            var isImageVisible by remember { mutableStateOf(true) }

            if (isImageVisible) {
                AsyncImage(
                    model = imageUrl,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            isImageVisible = false
                        }
                    },
                    contentDescription = stringResource(R.string.content_desc_item_article_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(150.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(4.dp))
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = article.title ?: "No TITLE",
                style = NewsTheme.typography.headlineMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = article.description,
                style = NewsTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}

private class ArticlePreviewProvider : PreviewParameterProvider<ArticleUI> {
    override val values = sequenceOf(
        ArticleUI(
            1,
            "Android Studio Iguana is Stable!",
            "New stable version on Android IDE has been realized",
            imageUrl = null,
            url = "",
        ),

        ArticleUI(
            2,
            "Gemini 1.5 Release!",
            "Upgraded version of Google AI is available",
            imageUrl = null,
            url = "",
        ),

        @Suppress("MagicNumber")
        ArticleUI(
            3,
            "Shape animations (10 min)",
            "How to use shape transform animations in Compose",
            imageUrl = null,
            url = "",
        ),
    )
}

private class ArticlesPreviewProvider : PreviewParameterProvider<List<ArticleUI>> {

    private val articleProvider = ArticlePreviewProvider()

    override val values = sequenceOf(
        articleProvider.values.toList()
    )
}
