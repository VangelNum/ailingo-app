package org.ailingo.app.features.analysis.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.loadingstate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.data.model.AnalysisInfo
import org.ailingo.app.features.analysis.data.model.IssuesMessage
import org.ailingo.app.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

@Composable
fun AnalysisScreen(
    conversationId: String,
    basicGrammarState: UiState<List<AnalysisInfo>>,
    onEvent: (AnalysisEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (basicGrammarState) {
            is UiState.Error -> {
                ErrorScreen(errorMessage = basicGrammarState.message)
            }

            is UiState.Idle -> {
                Button(
                    onClick = {
                        onEvent(AnalysisEvent.OnCheckBasicGrammar(conversationId))
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Check Basic Grammar")
                }
            }

            is UiState.Loading -> {
                LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate, loadingText = "It takes some time...")
            }

            is UiState.Success -> {
                val analysisInfoList = basicGrammarState.data
                AnalysisResultsList(analysisInfoList = analysisInfoList)
            }
        }
    }
}

@Composable
fun AnalysisResultsList(analysisInfoList: List<AnalysisInfo>) {
    if (analysisInfoList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No analysis results available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(analysisInfoList, key = { it.messageId }) { analysisInfo ->
                AnalysisResultCard(analysisInfo = analysisInfo)
            }
        }
    }
}

@Composable
fun AnalysisResultCard(analysisInfo: AnalysisInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Original Text:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = buildAnnotatedString {
                    val originalText = analysisInfo.originalText
                    val issues = analysisInfo.issues ?: emptyList()

                    val highlightRanges = issues.flatMap { issue ->
                        val ranges = mutableListOf<IntRange>()
                        if (issue.text.isNotEmpty()) {
                            var startIndex = originalText.indexOf(issue.text, 0)
                            while (startIndex != -1) {
                                ranges.add(startIndex until startIndex + issue.text.length)
                                startIndex = originalText.indexOf(issue.text, startIndex + 1)
                            }
                        }
                        ranges
                    }.sortedBy { it.first }

                    var currentOffset = 0

                    for (range in highlightRanges) {
                        val start = range.first
                        val end = range.last + 1

                        if (start > currentOffset) {
                            append(originalText.substring(currentOffset, start))
                        }

                        val effectiveStart = max(currentOffset, start)
                        if (effectiveStart < end) {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(originalText.substring(effectiveStart, end))
                            }
                        }

                        currentOffset = max(currentOffset, end)
                    }

                    if (currentOffset < originalText.length) {
                        append(originalText.substring(currentOffset))
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            val issues = analysisInfo.issues
            if (issues.isNullOrEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "No issues",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Looks good!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Issues found:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    issues.forEach { issue ->
                        IssueDetails(issue = issue)
                    }
                }
            }
        }
    }
}

@Composable
fun IssueDetails(issue: IssuesMessage) {
    Column {
        Text(
            text = "Type: ${issue.type}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Error: \"${issue.text}\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
        if (issue.description != null) {
            Text(
                text = "Description: ${issue.description}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (issue.suggestion != null) {
            Text(
                text = "Suggestion: ${issue.suggestion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun AnalysisScreenSuccessPreview() {
    val mockAnalysisInfoList = listOf(
        AnalysisInfo(
            messageId = "123",
            originalText = "He go to the store yesterday.",
            analysisType = "Basic Grammar",
            issues = listOf(
                IssuesMessage(
                    type = "Verb Form",
                    text = "go",
                    description = "Incorrect verb tense. Should be 'went'.",
                    suggestion = "went"
                )
            )
        ),
        AnalysisInfo(
            messageId = "456",
            originalText = "I has a dog.",
            analysisType = "Basic Grammar",
            issues = listOf(
                IssuesMessage(
                    type = "Subject-Verb Agreement",
                    text = "has",
                    description = "Incorrect verb agreement. Should be 'have'.",
                    suggestion = "have"
                )
            )
        ),
        AnalysisInfo(
            messageId = "789",
            originalText = "i have two apple and go to store",
            analysisType = "Basic Grammar",
            issues = listOf(
                IssuesMessage(type = "Capitalization", text = "i", description = "Pronoun 'I' needs capitalization.", suggestion = "I"),
                IssuesMessage(type = "Plural", text = "apple", description = "Countable noun 'apple' should be plural.", suggestion = "apples"),
                IssuesMessage(type = "Verb Form", text = "go", description = "Use correct tense for 'go'.", suggestion = "went or going?")
            )
        )
    )

    AppTheme {
        Surface {
            AnalysisScreen(
                conversationId = "mockConversationId",
                basicGrammarState = UiState.Success(mockAnalysisInfoList),
                onEvent = {}
            )
        }
    }
}

@Preview
@Composable
fun AnalysisScreenSuccessNoIssuesPreview() {
    val mockAnalysisInfoList = listOf(
        AnalysisInfo(
            messageId = "789",
            originalText = "I went to the store yesterday.",
            analysisType = "Basic Grammar",
            issues = null
        ),
        AnalysisInfo(
            messageId = "101",
            originalText = "She has a cat.",
            analysisType = "Basic Grammar",
            issues = emptyList()
        ),
        AnalysisInfo(
            messageId = "111",
            originalText = "They is here.",
            analysisType = "Basic Grammar",
            issues = listOf(
                IssuesMessage(
                    type = "Subject-Verb Agreement",
                    text = "is",
                    description = "Incorrect verb agreement. Should be 'are'.",
                    suggestion = "are"
                )
            )
        )
    )

    AppTheme {
        Surface {
            AnalysisScreen(
                conversationId = "mockConversationId",
                basicGrammarState = UiState.Success(mockAnalysisInfoList),
                onEvent = {}
            )
        }
    }
}

@Preview
@Composable
fun AnalysisScreenSuccessEmptyListPreview() {
    AppTheme {
        Surface {
            AnalysisScreen(
                conversationId = "mockConversationId",
                basicGrammarState = UiState.Success(emptyList()),
                onEvent = {}
            )
        }
    }
}