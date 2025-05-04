package org.ailingo.app.features.analysis.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.basic_grammar_analysis_title
import ailingo.composeapp.generated.resources.beginner_errors_analysis_title
import ailingo.composeapp.generated.resources.clarity_style_analysis_title
import ailingo.composeapp.generated.resources.description_label
import ailingo.composeapp.generated.resources.issues_found_label
import ailingo.composeapp.generated.resources.loading_analysis_text
import ailingo.composeapp.generated.resources.no_issues_found
import ailingo.composeapp.generated.resources.original_text_label
import ailingo.composeapp.generated.resources.suggestion_label
import ailingo.composeapp.generated.resources.vocabulary_phrasing_analysis_title
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.analysis.data.model.AnalysisInfo
import org.ailingo.app.features.analysis.data.model.IssuesMessage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    conversationId: String,
    analysisState: AnalysisState,
    onEvent: (AnalysisEvent) -> Unit,
) {
    var showAnalysisTypes by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analysis") },
                navigationIcon = {
                    if (!showAnalysisTypes) {
                        IconButton(onClick = { showAnalysisTypes = true }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = Pair(analysisState, showAnalysisTypes),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(300))
                }, label = "AnalysisScreenStateAnimation"
            ) { (targetState, showTypes) ->
                when {
                    targetState.isLoading() -> {
                        AnalysisLoadingScreen(
                            modifier = Modifier.fillMaxSize(),
                            loadingText = stringResource(Res.string.loading_analysis_text)
                        )
                    }

                    targetState.isError() -> {
                        AnalysisErrorScreen(
                            modifier = Modifier.fillMaxSize(),
                            errorMessage = targetState.getErrorMessage(),
                            onBack = { showAnalysisTypes = true }
                        )
                    }

                    targetState.isSuccess() && !showTypes -> {
                        AnalysisSuccessContent(analysisState = targetState)
                    }

                    showTypes -> {
                        IdleAnalysisContent(onEvent = onEvent, conversationId = conversationId, onAnalysisStarted = { showAnalysisTypes = false })
                    }

                    else -> {
                        IdleAnalysisContent(onEvent = onEvent, conversationId = conversationId, onAnalysisStarted = { showAnalysisTypes = false })
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisLoadingScreen(
    modifier: Modifier = Modifier,
    loadingText: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SettingsSuggest,
            contentDescription = loadingText,
            modifier = Modifier
                .size(64.dp)
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = loadingText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(0.8f))
    }
}

@Composable
fun AnalysisErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(32.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Analysis Failed",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back to Analysis Types")
        }
    }
}

@Composable
fun IdleAnalysisContent(onEvent: (AnalysisEvent) -> Unit, conversationId: String, onAnalysisStarted: () -> Unit) {

    val analysisTypes = listOf(
        AnalysisType.BasicGrammar,
        AnalysisType.BeginnerErrors,
        AnalysisType.ClarityStyle,
        AnalysisType.VocabularyPhrasing
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(analysisTypes) { item ->
            AnalysisTypeCard(
                type = item,
                onClick = {
                    onAnalysisStarted()
                    when (item) {
                        AnalysisType.BasicGrammar -> onEvent(AnalysisEvent.OnCheckBasicGrammar(conversationId))
                        AnalysisType.BeginnerErrors -> onEvent(AnalysisEvent.OnCheckBeginnerErrors(conversationId))
                        AnalysisType.ClarityStyle -> onEvent(AnalysisEvent.OnCheckClarityStyle(conversationId))
                        AnalysisType.VocabularyPhrasing -> onEvent(AnalysisEvent.OnCheckVocabularyPhrasing(conversationId))
                    }
                }
            )
        }
    }
}


@Composable
fun AnalysisTypeCard(type: AnalysisType, onClick: () -> Unit) {

    val gradient = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)),
        start = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY),
        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, 0f)
    )

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.8f else 1f, label = "pressScale")

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(type.image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(type.title).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    textAlign = TextAlign.Center,
                )

                Card(shape = MaterialTheme.shapes.large) {
                    Image(
                        painter = painterResource(type.image),
                        modifier = Modifier.size(118.dp),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}


@Composable
fun AnalysisSuccessContent(analysisState: AnalysisState) {
    val allAnalysisResults = mutableStateListOf<AnalysisInfo>()
    if (analysisState.basicGrammarState is UiState.Success) allAnalysisResults.addAll(analysisState.basicGrammarState.data)
    if (analysisState.vocabularyPhrasingState is UiState.Success) allAnalysisResults.addAll(analysisState.vocabularyPhrasingState.data)
    if (analysisState.clarityStyleState is UiState.Success) allAnalysisResults.addAll(analysisState.clarityStyleState.data)
    if (analysisState.beginnerErrorsState is UiState.Success) allAnalysisResults.addAll(analysisState.beginnerErrorsState.data)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        items(allAnalysisResults, key = { it.messageId }) { analysisInfo ->
            AnalysisResultCard(analysisInfo = analysisInfo)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun String.toUserFacingAnalysisTypeName(): String {
    return when (this) {
        "basic grammar" -> stringResource(Res.string.basic_grammar_analysis_title)
        "beginner errors" -> stringResource(Res.string.beginner_errors_analysis_title)
        "clarity" -> stringResource(Res.string.clarity_style_analysis_title)
        "vocabulary phrasing" -> stringResource(Res.string.vocabulary_phrasing_analysis_title)
        else -> this
    }
}

@Composable
fun AnalysisResultCard(analysisInfo: AnalysisInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = analysisInfo.analysisType,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = analysisInfo.analysisType.toUserFacingAnalysisTypeName().uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column {
                Text(
                    text = stringResource(Res.string.original_text_label),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val originalText = analysisInfo.originalText
                val highlightedText = buildAnnotatedString {
                    append(originalText)
                    analysisInfo.issues?.forEach { issue ->
                        val errorText = issue.text
                        val startIndex = originalText.indexOf(errorText)
                        if (startIndex != -1) {
                            val endIndex = startIndex + errorText.length
                            addStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                ),
                                start = startIndex,
                                end = endIndex
                            )
                        }
                    }
                }

                Text(
                    text = highlightedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (analysisInfo.issues.isNullOrEmpty()) {
                NoIssuesForThisTypeMessage()
            } else {
                Text(
                    text = stringResource(Res.string.issues_found_label),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    analysisInfo.issues.forEach { issue ->
                        IssueMessageDisplay(issueMessage = issue)
                    }
                }
            }
        }
    }
}

@Composable
fun IssueMessageDisplay(issueMessage: IssuesMessage) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "issueArrowRotation")

    val enterExitTransition = remember {
        fadeIn(animationSpec = tween(200, delayMillis = 50, easing = LinearOutSlowInEasing)) +
                expandVertically(expandFrom = Alignment.Top, animationSpec = tween(200, easing = LinearOutSlowInEasing)) togetherWith
                fadeOut(animationSpec = tween(200, easing = LinearOutSlowInEasing)) +
                shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = tween(200, easing = LinearOutSlowInEasing))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300, easing = LinearOutSlowInEasing))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = issueMessage.type,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = issueMessage.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse details" else "Expand details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.rotate(rotationAngle)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = enterExitTransition.targetContentEnter,
            exit = enterExitTransition.initialContentExit
        ) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                issueMessage.description?.let { desc ->
                    if (desc.isNotBlank()) {
                        Text(
                            text = stringResource(Res.string.description_label),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                issueMessage.suggestion?.let { suggestion ->
                    if (suggestion.isNotBlank()) {
                        Text(
                            text = stringResource(Res.string.suggestion_label),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}


@Composable
fun NoIssuesForThisTypeMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(8.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(Res.string.no_issues_found),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun AnalysisState.isLoading() = basicGrammarState is UiState.Loading ||
        vocabularyPhrasingState is UiState.Loading ||
        clarityStyleState is UiState.Loading ||
        beginnerErrorsState is UiState.Loading

private fun AnalysisState.isError() = basicGrammarState is UiState.Error ||
        vocabularyPhrasingState is UiState.Error ||
        clarityStyleState is UiState.Error ||
        beginnerErrorsState is UiState.Error

private fun AnalysisState.isSuccess() = basicGrammarState is UiState.Success ||
        vocabularyPhrasingState is UiState.Success ||
        clarityStyleState is UiState.Success ||
        beginnerErrorsState is UiState.Success

private fun AnalysisState.getErrorMessage(): String {
    return when {
        basicGrammarState is UiState.Error -> basicGrammarState.message
        vocabularyPhrasingState is UiState.Error -> vocabularyPhrasingState.message
        clarityStyleState is UiState.Error -> clarityStyleState.message
        beginnerErrorsState is UiState.Error -> beginnerErrorsState.message
        else -> "An unknown error occurred."
    }
}