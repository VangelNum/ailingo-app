package org.ailingo.app.features.lectures.presentation

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView


data class GrammarLecture(
    val title: String,
    val summary: String,
    val source: String,
    val url: String,
    val color: Color
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun LectureScreen() {
    var selectedUrl by remember { mutableStateOf<String?>(null) }

    val grammarLectures = remember {
        listOf(
            GrammarLecture(
                title = "13 Most Common Grammar Mistakes",
                summary = "Learn how to avoid the most frequent errors made by English learners.",
                source = "Source: englex.ru",
                url = "https://englex.ru/13-most-common-grammar-mistakes/",
                color = Color(0xFFE1BEE7) // Light Purple
            ),
            GrammarLecture(
                title = "3 Difficult English Grammar Topics for Russians",
                summary = "Explore challenging grammar points that often pose problems for Russian speakers.",
                source = "Source: skyeng.ru",
                url = "https://skyeng.ru/magazine/3-slozhnye-temy-anglijskoj-grammatiki-s-kotorymi-u-russkih-voznikayut-problemy/",
                color = Color(0xFFC8E6C9) // Light Green
            ),
            GrammarLecture(
                title = "25 Frequent Mistakes at Intermediate Level",
                summary = "Identify and correct common mistakes typically made by students at the Intermediate level.",
                source = "Source: ils-school.com",
                url = "https://ils-school.com/blog/show/25-chastykh-oshibok-na-urovne-Intermediate",
                color = Color(0xFFFFF9C4) // Light Yellow
            ),
            GrammarLecture(
                title = "Typical Mistakes at Pre-Intermediate Level",
                summary = "Discover and fix the common errors encountered by learners at the Pre-Intermediate stage.",
                source = "Source: lhlib.ru",
                url = "https://lhlib.ru/tipichnye-oshibki-v-anglijskom-urovnya-pre-in/",
                color = Color(0xFFB2EBF2) // Light Cyan
            ),
            GrammarLecture(
                title = "Past Simple Tense",
                summary = "Understand the rules and usage of the Past Simple tense in English.",
                source = "Source: skysmart.ru",
                url = "https://skysmart.ru/articles/english/past-simple-tense",
                color = Color(0xFFF8BBD0) // Light Pink
            )
        )
    }

    if (selectedUrl == null) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grammarLectures) { lecture ->
                GrammarLectureCard(lecture = lecture) { clickedUrl ->
                    selectedUrl = clickedUrl
                }
            }
        }
    } else {
        //BackHandler(enabled = selectedUrl != null) {
        //    selectedUrl = null
        //}
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }
                },
                update = { webView ->
                    selectedUrl?.let { url -> // Use let to safely access selectedUrl
                        webView.loadUrl(url)
                    }
                }
            )

            Button(
                onClick = { selectedUrl = null },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
fun GrammarLectureCard(lecture: GrammarLecture, onCardClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(lecture.url) },
        colors = CardDefaults.cardColors(containerColor = lecture.color),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lecture.title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lecture.summary,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lecture.source,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lecture.url,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    }
}