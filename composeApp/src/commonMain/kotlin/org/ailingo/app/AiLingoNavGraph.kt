package org.ailingo.app

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.exit
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.navigation.NavigationHandler
import org.ailingo.app.core.presentation.snackbar.ObserveAsEvents
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.topappbar.TopAppBarCenter
import org.ailingo.app.core.presentation.topappbar.TopAppBarWithProfile
import org.ailingo.app.features.achievements.presentation.AchievementsEvent
import org.ailingo.app.features.achievements.presentation.AchievementsScreen
import org.ailingo.app.features.achievements.presentation.AchievementsViewModel
import org.ailingo.app.features.additional.presentation.AdditionalScreen
import org.ailingo.app.features.analysis.presentation.AnalysisScreen
import org.ailingo.app.features.analysis.presentation.AnalysisViewModel
import org.ailingo.app.features.buns.presentation.BunsScreen
import org.ailingo.app.features.chat.presentation.ChatScreen
import org.ailingo.app.features.chat.presentation.ChatViewModel
import org.ailingo.app.features.chathistory.presentation.ChatHistoryScreen
import org.ailingo.app.features.chathistory.presentation.ChatHistoryViewModel
import org.ailingo.app.features.dailybonus.presentation.DailyBonusScreen
import org.ailingo.app.features.dailybonus.presentation.DailyBonusViewModel
import org.ailingo.app.features.dictionary.main.presentation.DictionaryScreen
import org.ailingo.app.features.dictionary.main.presentation.DictionaryViewModel
import org.ailingo.app.features.favouritewords.presentation.FavouriteScreen
import org.ailingo.app.features.favouritewords.presentation.FavouriteWordsViewModel
import org.ailingo.app.features.leaderboard.presentation.LeaderboardScreen
import org.ailingo.app.features.leaderboard.presentation.LeaderboardViewModel
import org.ailingo.app.features.login.presentation.LoginEvent
import org.ailingo.app.features.login.presentation.LoginScreen
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.features.profile.presentation.ProfileScreen
import org.ailingo.app.features.profileupdate.presentation.ProfileUpdateEvent
import org.ailingo.app.features.profileupdate.presentation.ProfileUpdateScreen
import org.ailingo.app.features.profileupdate.presentation.ProfileUpdateViewModel
import org.ailingo.app.features.registration.presentation.RegisterUserViewModel
import org.ailingo.app.features.registration.presentation.RegistrationEvent
import org.ailingo.app.features.registration.presentation.RegistrationScreen
import org.ailingo.app.features.registration.presentation.email_verification.VerifyEmailScreen
import org.ailingo.app.features.shop.presentation.ShopEvent
import org.ailingo.app.features.shop.presentation.ShopScreen
import org.ailingo.app.features.shop.presentation.ShopViewModel
import org.ailingo.app.features.topics.presentation.DEFAULT_IMAGE_URL
import org.ailingo.app.features.topics.presentation.TopicViewModel
import org.ailingo.app.features.topics.presentation.TopicsScreen
import org.ailingo.app.features.updateavatar.presentation.UpdateAvatarScreen
import org.ailingo.app.features.updateavatar.presentation.UpdateAvatarViewModel
import org.ailingo.app.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class, ExperimentalEncodingApi::class)
@Composable
fun AiLingoNavGraph(
    navController: NavHostController
) {
    val loginViewModel: LoginViewModel = koinViewModel<LoginViewModel>()
    val loginState = loginViewModel.loginState.collectAsStateWithLifecycle().value
    val registrationViewModel: RegisterUserViewModel = koinViewModel<RegisterUserViewModel>()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val customNavSuiteType = with(adaptiveInfo) {
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
            NavigationSuiteType.NavigationDrawer
        } else {
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
        }
    }
    NavigationHandler(navController = navController, loginViewModel = loginViewModel)
    val navigationSuiteState = rememberNavigationSuiteScaffoldState(initialValue = NavigationSuiteScaffoldValue.Hidden)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    val routesWithNavigationDrawer = listOf(
        ChatPage::class,
        TopicsPage::class,
        DictionaryPage::class,
        ProfilePage::class,
        ProfileUpdatePage::class,
        FavouriteWordsPage::class,
        LeaderboardPage::class,
        ChatHistoryPage::class,
        AdditionalPage::class,
        AchievementsPage::class,
        AnalysisPage::class,
        DailyBonusPage::class,
        ShopPage::class,
    )

    val isNavigationDrawerVisible = currentDestination?.let { dest ->
        routesWithNavigationDrawer.any { routeClass ->
            dest.hasRoute(routeClass)
        }
    } ?: false

    LaunchedEffect(isNavigationDrawerVisible) {
        scope.launch {
            if (isNavigationDrawerVisible) {
                navigationSuiteState.show()
            } else {
                navigationSuiteState.hide()
            }
        }
    }

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (isNavigationDrawerVisible) {
                    TopAppBarWithProfile(loginState = loginState)
                } else {
                    TopAppBarCenter()
                }
            }
        ) { innerPadding ->
            NavigationSuiteScaffold(
                state = navigationSuiteState,
                navigationSuiteColors = NavigationSuiteDefaults.colors(
                    navigationBarContainerColor = Color.White,
                    navigationDrawerContainerColor = Color.White,
                    navigationRailContainerColor = Color.White
                ),
                modifier = Modifier.padding(PaddingValues(top = innerPadding.calculateTopPadding())),
                navigationSuiteItems = {
                    val screensToShow: List<ScreenInfo> = if (adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                        screenForLargePortrait
                    } else {
                        screenForCompactPortrait
                    }
                    screensToShow.forEach {
                        item(
                            icon = {
                                Icon(
                                    it.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    stringResource(it.label),
                                    textAlign = TextAlign.Center
                                )
                            },
                            selected = currentDestination?.hasRoute(it.route::class) == true,
                            onClick = {
                                navController.navigate(it.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(LoginPage) {
                                        saveState = true
                                    }
                                }
                            }
                        )
                    }
                    if (adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
                        || adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
                    ) {
                        item(
                            icon = {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                            },
                            label = {
                                Text(stringResource(Res.string.exit))
                            },
                            selected = false,
                            onClick = {
                                loginViewModel.onEvent(LoginEvent.OnBackToEmptyState)
                                navController.navigate(LoginPage) {
                                    popUpTo(0)
                                }
                            }
                        )
                    }
                },
                layoutType = customNavSuiteType
            ) {
                NavHost(navController, startDestination = LoginPage) {
                    composable<LoginPage> {
                        LoginScreen(
                            loginState = loginState, onNavigateToHomeScreen = {
                                navController.navigate(TopicsPage) {
                                    popUpTo(0)
                                }
                            }, onNavigateToRegisterScreen = {
                                navController.navigate(RegistrationPage)
                            }, onEvent = { event ->
                                loginViewModel.onEvent(event)
                            }
                        )
                    }
                    composable<TopicsPage> {
                        val topicsViewModel = koinViewModel<TopicViewModel>()
                        val topicsUiState = topicsViewModel.topicState.collectAsStateWithLifecycle().value
                        val currentUserXp = if (loginState is UiState.Success) {
                            loginState.data.xp
                        } else {
                            -1
                        }
                        TopicsScreen(
                            topicsUiState = topicsUiState,
                            currentUserXp = currentUserXp,
                            currentUserCoins = if (loginState is UiState.Success) loginState.data.coins else -1,
                            onTopicClick = { topicName, topicImage ->
                                navController.navigate(
                                    ChatPage(
                                        topicName = topicName,
                                        topicImage = topicImage
                                    )
                                )
                            },
                            onClickCustomTopic = { topicIdea ->
                                navController.navigate(
                                    ChatPage(
                                        topicIdea = topicIdea,
                                        topicImage = null
                                    )
                                )
                            },
                            onGoToShopClick = {
                                navController.navigate(ShopPage)
                            }
                        )
                    }
                    composable<ChatPage> { backStackEntry ->
                        val args = backStackEntry.toRoute<ChatPage>()
                        val chatViewModel: ChatViewModel = koinViewModel { parametersOf(args.topicName, args.chatId, args.topicIdea) }
                        val chatUiState = chatViewModel.chatState.collectAsStateWithLifecycle().value
                        val messagesState = chatViewModel.messages.collectAsStateWithLifecycle().value
                        val translateState = chatViewModel.translateState.collectAsStateWithLifecycle().value
                        val singleMessageCheckState = chatViewModel.singleMessageCheckState.collectAsStateWithLifecycle().value
                        ChatScreen(
                            topicName = args.topicName ?: args.topicIdea ?: "empty?",
                            topicImage = args.topicImage?: DEFAULT_IMAGE_URL,
                            chatUiState = chatUiState,
                            messagesState = messagesState,
                            translateState = translateState,
                            singleMessageCheckState = singleMessageCheckState,
                            onEvent = { event ->
                                chatViewModel.onEvent(event)
                            },
                            userAvatar = if (loginState is UiState.Success) loginState.data.avatar else null,
                            onNavigateToAnalyzeConversation = {
                                navController.navigate(AnalysisPage(chatViewModel.conversationId))
                            }
                        )

                        LaunchedEffect(chatViewModel.conversationId) {
                            if (chatViewModel.conversationId != "") {
                                loginViewModel.onEvent(LoginEvent.OnRefreshUserInfo)
                            }
                        }
                    }
                    composable<ProfilePage> {
                        ProfileScreen(
                            loginState = loginState,
                            onExit = {
                                loginViewModel.onEvent(LoginEvent.OnBackToEmptyState)
                                navController.navigate(LoginPage) {
                                    popUpTo(0)
                                }
                            }, onNavigateProfileChange = { name, email, avatar ->
                                navController.navigate(
                                    ProfileUpdatePage(
                                        name, email, avatar
                                    )
                                )
                            }
                        )
                    }
                    composable<ProfileUpdatePage> {
                        if (loginState is UiState.Success) {
                            val profileUpdateViewModel = koinViewModel<ProfileUpdateViewModel>()
                            val profileUpdateUiState = profileUpdateViewModel.profileUpdateUiState.collectAsStateWithLifecycle().value
                            val uploadAvatarState = profileUpdateViewModel.uploadAvatarState.collectAsStateWithLifecycle().value
                            ProfileUpdateScreen(
                                profileUpdateUiState = profileUpdateUiState,
                                uploadAvatarState = uploadAvatarState,
                                onProfileUpdate = {
                                    profileUpdateViewModel.onEvent(
                                        ProfileUpdateEvent.OnUpdateProfile(
                                            it
                                        )
                                    )
                                },
                                onReLoginUser = { newLogin, currentPassword, newPassword, passwordChanged ->
                                    val passwordToUse = if (passwordChanged) {
                                        newPassword
                                    } else currentPassword
                                    loginViewModel.onEvent(
                                        LoginEvent.OnLoginUser(
                                            newLogin,
                                            passwordToUse
                                        )
                                    )
                                },
                                onNavigateProfileScreen = {
                                    navController.navigate(ProfilePage)
                                },
                                onBackToEmptyState = {
                                    profileUpdateViewModel.onEvent(ProfileUpdateEvent.OnBackToEmptyState)
                                },
                                name = loginState.data.name,
                                email = loginState.data.email,
                                avatar = loginState.data.avatar,
                                onUploadNewAvatar = {
                                    profileUpdateViewModel.onEvent(
                                        ProfileUpdateEvent.OnUploadAvatar(
                                            it
                                        )
                                    )
                                }
                            )
                        }
                    }
                    composable<FavouriteWordsPage> {
                        val favouriteWordsViewModel = koinViewModel<FavouriteWordsViewModel>()
                        val favouriteWordsState = favouriteWordsViewModel.favoriteWords.collectAsStateWithLifecycle().value
                        FavouriteScreen(
                            favouriteWordsState = favouriteWordsState,
                            onNavigateToDictionaryScreen = { word ->
                                navController.navigate(DictionaryPage(word))
                            },
                            onEvent = { event ->
                                favouriteWordsViewModel.onEvent(event)
                            }
                        )
                    }
                    composable<DictionaryPage> { backStackEntry ->
                        val args = backStackEntry.toRoute<DictionaryPage>()
                        val dictionaryViewModel: DictionaryViewModel = koinViewModel { parametersOf(args.word) }
                        val dictionaryState = dictionaryViewModel.dictionaryUiState.collectAsStateWithLifecycle().value
                        val searchHistoryState = dictionaryViewModel.historyOfDictionaryState.collectAsStateWithLifecycle().value
                        val favoriteDictionaryState = dictionaryViewModel.favouriteWordsState.collectAsStateWithLifecycle().value
                        val predictorState = dictionaryViewModel.predictorState.collectAsStateWithLifecycle().value
                        DictionaryScreen(
                            dictionaryState,
                            searchHistoryState,
                            favoriteDictionaryState,
                            predictorState,
                            onEvent = { event ->
                                dictionaryViewModel.onEvent(event)
                            }
                        )
                    }
                    composable<RegistrationPage> {
                        val pendingRegistrationState = registrationViewModel.pendingRegistrationUiState.collectAsStateWithLifecycle().value
                        RegistrationScreen(
                            onNavigateToLoginPage = {
                                navController.navigate(LoginPage)
                            },
                            onNavigateToVerifyEmail = { email, password ->
                                registrationViewModel.onEvent(RegistrationEvent.OnBackToEmptyState)
                                navController.navigate(
                                    VerifyEmailPage(
                                        email = email,
                                        password = password
                                    )
                                )
                            },
                            pendingRegistrationState = pendingRegistrationState,
                            onEvent = { event ->
                                registrationViewModel.onEvent(event)
                            }
                        )
                    }
                    composable<VerifyEmailPage> { backStack ->
                        val args = backStack.toRoute<VerifyEmailPage>()
                        val registrationState = registrationViewModel.registrationUiState.collectAsStateWithLifecycle().value

                        VerifyEmailScreen(
                            email = args.email,
                            registrationState = registrationState,
                            onCodeCheck = { code ->
                                registrationViewModel.onEvent(RegistrationEvent.OnVerifyEmail(args.email, code))
                            },
                            onNavigateToUpdateAvatar = {
                                loginViewModel.onEvent(LoginEvent.OnLoginUser(args.email, args.password))
                            },
                            onNavigateBack = {
                                navController.navigate(RegistrationPage)
                            }
                        )

                        LaunchedEffect(loginState) {
                            if (loginState is UiState.Success) {
                                navController.navigate(UpdateAvatarPage) {
                                    popUpTo(0)
                                }
                            }
                        }
                    }
                    composable<UpdateAvatarPage> { backStack ->
                        val updateAvatarViewModel = koinViewModel<UpdateAvatarViewModel>()
                        val uploadAvatarState = updateAvatarViewModel.uploadAvatarState.collectAsState().value
                        val updateAvatarState = updateAvatarViewModel.updateAvatarState.collectAsState().value
                        val generatedAvatarsState by updateAvatarViewModel.generatedAvatarsState.collectAsState()
                        UpdateAvatarScreen(
                            uploadAvatarState = uploadAvatarState,
                            updateAvatarState = updateAvatarState,
                            generatedAvatarsState = generatedAvatarsState,
                            onEvent = { event ->
                                updateAvatarViewModel.onEvent(event)
                            },
                            onNavigateToBunsScreen = {
                                navController.navigate(BunsPage) {
                                    popUpTo(0)
                                }
                            }
                        )

                        LaunchedEffect(updateAvatarState) {
                            if (updateAvatarState is UiState.Success) {
                                loginViewModel.onEvent(LoginEvent.OnRefreshUserInfo)
                            }
                        }
                    }
                    composable<BunsPage> {
                        BunsScreen(onNavigateToHomeScreen = {
                            navController.navigate(TopicsPage) {
                                popUpTo(0)
                            }
                        })
                    }
                    composable<LeaderboardPage> {
                        val leaderboardViewModel = koinViewModel<LeaderboardViewModel>()
                        val leaderboardState = leaderboardViewModel.leaderboardState.collectAsStateWithLifecycle().value
                        LeaderboardScreen(leaderboardState)
                    }
                    composable<AdditionalPage> {
                        AdditionalScreen(
                            onNavigateToProfile = {
                                navController.navigate(ProfilePage)
                            },
                            onNavigateToLeaderboard = {
                                navController.navigate(LeaderboardPage)
                            },
                            onNavigateToAchievements = {
                                navController.navigate(AchievementsPage)
                            },
                            onNavigateToDailyBonus = {
                                navController.navigate(DailyBonusPage)
                            },
                            onNavigateToShop = {
                                navController.navigate(ShopPage)
                            },
                            onNavigateToFavouriteWords = {
                                navController.navigate(FavouriteWordsPage)
                            }
                        )
                    }
                    composable<ChatHistoryPage> {
                        val chatHistoryViewModel = koinViewModel<ChatHistoryViewModel>()
                        val chatHistoryState = chatHistoryViewModel.chatHistoryState.collectAsStateWithLifecycle().value
                        ChatHistoryScreen(chatHistoryState, onNavigateToSelectedChat = { chatId, topicName, topicImage ->
                            navController.navigate(
                                ChatPage(
                                    chatId,
                                    topicName,
                                    topicImage
                                )
                            )
                        })
                    }
                    composable<AchievementsPage> {
                        val achievementViewModel = koinViewModel<AchievementsViewModel>()
                        val achievementUiState = achievementViewModel.achievementsState.collectAsStateWithLifecycle().value
                        val claimAchievementState = achievementViewModel.claimAchievementsState.collectAsStateWithLifecycle().value
                        AchievementsScreen(achievementUiState, claimAchievementState, onEvent = { event->
                            achievementViewModel.onEvent(event)
                        })
                        LaunchedEffect(claimAchievementState) {
                            if (claimAchievementState is UiState.Success) {
                                achievementViewModel.onEvent(AchievementsEvent.OnGetAchievementsInfo)
                                loginViewModel.onEvent(LoginEvent.OnRefreshUserInfo)
                            }
                        }
                    }
                    composable<AnalysisPage> { backStack ->
                        val args = backStack.toRoute<AnalysisPage>()
                        val analysisViewModel = koinViewModel<AnalysisViewModel>()
                        val analysisState = analysisViewModel.analysisState.collectAsStateWithLifecycle().value
                        AnalysisScreen(
                            conversationId = args.conversationId,
                            onEvent = { event ->
                                analysisViewModel.onEvent(event)
                            },
                            analysisState = analysisState
                        )
                    }
                    composable<DailyBonusPage> {
                        val dailyBonusViewModel = koinViewModel<DailyBonusViewModel>()
                        val dailyBonusInfoState = dailyBonusViewModel.dailyBonusInfoState.collectAsStateWithLifecycle().value
                        val claimDailyBonusState = dailyBonusViewModel.claimDailyBonusInfoState.collectAsStateWithLifecycle().value
                        DailyBonusScreen(dailyBonusInfoState, claimDailyBonusState, onEvent = {
                            dailyBonusViewModel.onEvent(it)
                        }, onRefreshUserInfo = {
                            loginViewModel.onEvent(LoginEvent.OnRefreshUserInfo)
                        })
                    }
                    composable<ShopPage> {
                        val shopViewModel = koinViewModel<ShopViewModel>()
                        val availableItemsState = shopViewModel.availableItemsState.collectAsStateWithLifecycle().value

                        ShopScreen(
                            availableItemsState = availableItemsState,
                            onClaim = { itemId ->
                                shopViewModel.onEvent(ShopEvent.OnPurchaseCoins(itemId))
                            }
                        )

                        LaunchedEffect(availableItemsState) {
                            if (availableItemsState is UiState.Success) {
                                val anyItemPurchasedSuccessfully = availableItemsState.data.any {
                                    it.purchaseUiState is UiState.Success
                                }
                                if (anyItemPurchasedSuccessfully) {
                                    loginViewModel.onEvent(LoginEvent.OnRefreshUserInfo)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
