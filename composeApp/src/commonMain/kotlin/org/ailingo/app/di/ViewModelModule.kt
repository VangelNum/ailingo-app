package org.ailingo.app.di

import org.ailingo.app.features.achievements.presentation.AchievementsViewModel
import org.ailingo.app.features.analysis.presentation.AnalysisViewModel
import org.ailingo.app.features.chat.presentation.ChatViewModel
import org.ailingo.app.features.chathistory.presentation.ChatHistoryViewModel
import org.ailingo.app.features.dailybonus.presentation.DailyBonusViewModel
import org.ailingo.app.features.dictionary.main.presentation.DictionaryViewModel
import org.ailingo.app.features.favouritewords.presentation.FavouriteWordsViewModel
import org.ailingo.app.features.leaderboard.presentation.LeaderboardViewModel
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.features.profileupdate.presentation.ProfileUpdateViewModel
import org.ailingo.app.features.registration.presentation.RegisterUserViewModel
import org.ailingo.app.features.topics.presentation.TopicViewModel
import org.ailingo.app.features.updateavatar.presentation.UpdateAvatarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterUserViewModel(get(), get()) }
    factory { (topicName: String, chatId: String) -> ChatViewModel(get(), get(), topicName, chatId) }
    factory { (word: String) ->
        DictionaryViewModel(
            get(),
            get(),
            get(),
            get(),
            word
        )
    }
    viewModel { TopicViewModel(get()) }
    viewModel { ProfileUpdateViewModel(get(), get()) }
    viewModel { FavouriteWordsViewModel(get()) }
    viewModel { UpdateAvatarViewModel(get(), get()) }
    viewModel { LeaderboardViewModel(get()) }
    viewModel { ChatHistoryViewModel(get()) }
    viewModel { AnalysisViewModel(get()) }
    viewModel { DailyBonusViewModel(get()) }
    viewModel { AchievementsViewModel(get()) }
}