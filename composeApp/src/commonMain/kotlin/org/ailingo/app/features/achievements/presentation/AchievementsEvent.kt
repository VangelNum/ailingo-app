package org.ailingo.app.features.achievements.presentation

sealed class AchievementsEvent {
    object OnGetAchievementsInfo : AchievementsEvent()
    data class OnClaimAchievement(val achievementId: Int) : AchievementsEvent()
}