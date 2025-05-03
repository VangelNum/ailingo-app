package org.ailingo.app.features.analysis.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.basic_grammar
import ailingo.composeapp.generated.resources.basic_grammar_analysis_title
import ailingo.composeapp.generated.resources.beginner_errors
import ailingo.composeapp.generated.resources.beginner_errors_analysis_title
import ailingo.composeapp.generated.resources.clarity_style
import ailingo.composeapp.generated.resources.clarity_style_analysis_title
import ailingo.composeapp.generated.resources.vocabulary_phrasing
import ailingo.composeapp.generated.resources.vocabulary_phrasing_analysis_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

sealed class AnalysisType(val title: StringResource, val image: DrawableResource) {
    object BasicGrammar : AnalysisType(Res.string.basic_grammar_analysis_title, Res.drawable.basic_grammar)
    object BeginnerErrors : AnalysisType(Res.string.beginner_errors_analysis_title, Res.drawable.beginner_errors)
    object ClarityStyle : AnalysisType(Res.string.clarity_style_analysis_title, Res.drawable.clarity_style)
    object VocabularyPhrasing : AnalysisType(Res.string.vocabulary_phrasing_analysis_title, Res.drawable.vocabulary_phrasing)
}