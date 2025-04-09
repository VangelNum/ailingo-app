package org.ailingo.app.features.dictionary.examples.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WordInfoItem(
    val meanings: List<Meaning>,
    val phonetics: List<Phonetic>? = emptyList(),
    val word: String
)