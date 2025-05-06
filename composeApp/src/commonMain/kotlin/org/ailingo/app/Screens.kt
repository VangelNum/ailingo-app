package org.ailingo.app

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.additional
import ailingo.composeapp.generated.resources.chat_history
import ailingo.composeapp.generated.resources.dictionary
import ailingo.composeapp.generated.resources.profile
import ailingo.composeapp.generated.resources.shop
import ailingo.composeapp.generated.resources.topics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Topic
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

data class ScreenInfo(
    val route: Any,
    val label: StringResource,
    val icon: ImageVector
)

val screenForLargePortrait = listOf(
    ScreenInfo(TopicsPage, Res.string.topics, Icons.Filled.Topic),
    ScreenInfo(ChatHistoryPage, Res.string.chat_history, Icons.Filled.History),
    ScreenInfo(ShopPage, Res.string.shop, Icons.Filled.ShoppingCart),
    ScreenInfo(DictionaryPage(), Res.string.dictionary, Icons.Filled.Book),
    ScreenInfo(ProfilePage, Res.string.profile, Icons.Filled.Person),
    /*
        Replaced with additional screen
        ScreenInfo(FavouriteWordsPage, Res.string.favourite_words, Icons.Filled.Favorite),
        ScreenInfo(DailyBonusPage, Res.string.daily_bonus, Icons.Filled.MonetizationOn),
        ScreenInfo(AchievementsPage, Res.string.achievements, Icons.Filled.Star),
        ScreenInfo(LeaderboardPage, Res.string.leaderboard, Icons.Filled.Leaderboard),
     */
    ScreenInfo(AdditionalPage, Res.string.additional, Icons.AutoMirrored.Filled.More)
)

val screenForCompactPortrait = listOf(
    ScreenInfo(TopicsPage, Res.string.topics, Icons.Filled.Topic),
    ScreenInfo(ChatHistoryPage, Res.string.chat_history, Icons.Filled.History),
    ScreenInfo(DictionaryPage(), Res.string.dictionary, Icons.Filled.Book),
    ScreenInfo(AdditionalPage, Res.string.additional, Icons.AutoMirrored.Filled.More)
)

@Serializable
object LoginPage

@Serializable
data class ChatPage(
    val chatId: String? = null,
    val topicName: String? = null,
    val topicImage: String? = null,
    val topicIdea: String? = null
)

@Serializable
object RegistrationPage

@Serializable
object TopicsPage

@Serializable
data class DictionaryPage(
    val word: String = ""
)

@Serializable
object ProfilePage

@Serializable
data class ProfileUpdatePage(
    val name: String,
    val email: String,
    var avatar: String?
)

@Serializable
object FavouriteWordsPage

@Serializable
data class VerifyEmailPage(
    val email: String,
    val password: String,
)

@Serializable
object UpdateAvatarPage

@Serializable
object BunsPage

@Serializable
object LeaderboardPage

@Serializable
object AdditionalPage

@Serializable
object ChatHistoryPage

@Serializable
object AchievementsPage

@Serializable
object LecturePage

@Serializable
object DailyBonusPage

@Serializable
object ShopPage

@Serializable
data class AnalysisPage(
    val conversationId: String
)