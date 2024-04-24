package com.example.jetbizcard

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun createImageProfile(size: Dp, imageUrl: String = "", onclick: (Boolean, Boolean) -> Unit) {
    Surface(
        modifier = Modifier
            .size(size)
            .padding(4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onclick(true, false)
                    },
                    onDoubleTap = {
                        onclick(false, true)
                    }
                )
            },
        shape = CircleShape,
        tonalElevation = 10.dp
    ) {

        AsyncImage(
            model = if (imageUrl == "") "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQag8HJlcPrRvMACQNFi0bfejMxgaouk9sHvA&s" else imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun profileInfo() {
    Column(
        modifier = Modifier
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        SelectionContainer {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Green)) {
                        append("--")
                    }
                    append(" Priyanshu Prajapati ")

                    withStyle(style = SpanStyle(color = Color.Green)) {
                        append("--")
                    }
                },
                style = TextStyle(
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
            )
        }
        Text(
            text = "Android Compose Developer",
            modifier = Modifier.padding(3.dp)
        )
        Text(
            text = "@Composable",
            modifier = Modifier.padding(3.dp),
            style = TextStyle(
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                fontWeight = FontWeight.Normal
            )
        )

        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Normal
                )
            ) {
                append("Click ")
            }
            pushStringAnnotation(
                tag = "URL",
                annotation = "https://developer.android.com/develop/ui/compose/documentation"
            )
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF009FF5), fontWeight = FontWeight.Bold
                )
            ) {
                append("here")
            }
            pop()
        }


        val uriHandler = LocalUriHandler.current
        ClickableText(text = annotatedText, onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = "URL", start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                Log.d("Clicked URL", annotation.item)
                uriHandler.openUri(annotation.item)
            }
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun portFolio(data: List<Project>) {
    LazyColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        items(data) { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    createImageProfile(80.dp, item.imageUrl) { i, it -> }
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(alignment = Alignment.CenterVertically),
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.description + ".",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}