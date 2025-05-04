package org.ailingo.app.features.dailybonus.presentation

sealed class DailyBonusEvent {
    object OnGetDailyBonusInfo : DailyBonusEvent()
    object OnClaimDailyBonus : DailyBonusEvent()
}