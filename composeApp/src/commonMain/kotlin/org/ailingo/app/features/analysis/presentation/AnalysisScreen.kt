package org.ailingo.app.features.analysis.presentation

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
import co.touchlab.kermit.Logger
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.data.model.AnalysisInfo
import org.ailingo.app.features.analysis.data.model.IssuesMessage
import org.ailingo.app.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                LoadingScreen()
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
                    var currentOffset = 0
                    val originalTextLength = analysisInfo.originalText.length
                    val issues = analysisInfo.issues?.sortedBy { it.startOffset } ?: emptyList()

                    for (issue in issues) {
                        if (issue.startOffset < 0 || issue.endOffset > originalTextLength || issue.startOffset > issue.endOffset) {
                            Logger.e("Invalid issue offsets: $issue in text: ${analysisInfo.originalText}")
                            continue
                        }

                        if (issue.startOffset > currentOffset) {
                            append(analysisInfo.originalText.substring(currentOffset, issue.startOffset))
                        }

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(analysisInfo.originalText.substring(issue.startOffset, issue.endOffset))
                        }

                        currentOffset = issue.endOffset

                        if (currentOffset > originalTextLength){
                            currentOffset = originalTextLength
                            break
                        }
                    }

                    if (currentOffset < originalTextLength) {
                        append(analysisInfo.originalText.substring(currentOffset, originalTextLength))
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
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Space between issues
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
            color = MaterialTheme.colorScheme.error // Highlight the error word
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
                color = MaterialTheme.colorScheme.primary // Suggestion in a positive color
            )
        }
    }
}


// --- Previews ---

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
                    suggestion = "went",
                    startOffset = 3,
                    endOffset = 5
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
                    suggestion = "have",
                    startOffset = 2,
                    endOffset = 5
                )
            )
        )
    )

    AppTheme { // Wrap preview in your theme
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
        AnalysisInfo( // Add one with issues for variety
            messageId = "111",
            originalText = "They is here.",
            analysisType = "Basic Grammar",
            issues = listOf(
                IssuesMessage(
                    type = "Subject-Verb Agreement",
                    text = "is",
                    description = "Incorrect verb agreement. Should be 'are'.",
                    suggestion = "are",
                    startOffset = 5,
                    endOffset = 7
                )
            )
        )
    )

    AppTheme { // Wrap preview in your theme
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
                basicGrammarState = UiState.Success(emptyList()), // Empty list
                onEvent = {}
            )
        }
    }
}