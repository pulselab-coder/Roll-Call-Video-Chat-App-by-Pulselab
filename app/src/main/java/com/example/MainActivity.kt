package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ads.AdMobController
import com.example.ui.ads.InterstitialAdModal
import com.example.ui.ads.RewardedAdModal
import com.example.ui.ads.RollCallBannerAd
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeAndCallScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SafetyAndSpecScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.AmberCrown
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.viewmodel.CallMode
import com.example.ui.viewmodel.MatchmakingStatus
import com.example.ui.viewmodel.ROUTE_CHAT
import com.example.ui.viewmodel.ROUTE_HOME
import com.example.ui.viewmodel.ROUTE_LEADERBOARD
import com.example.ui.viewmodel.ROUTE_SAFETY_SPEC
import com.example.ui.viewmodel.ROUTE_STORE
import com.example.ui.viewmodel.RollCallViewModel

data class NavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RollCallAppRoot()
            }
        }
    }
}

@Composable
fun RollCallAppRoot(
    rollCallViewModel: RollCallViewModel = viewModel()
) {
    val context = LocalContext.current
    val userProfile by rollCallViewModel.userProfile.collectAsStateWithLifecycle()
    val currentRoute by rollCallViewModel.currentRoute.collectAsStateWithLifecycle()
    val callState by rollCallViewModel.callState.collectAsStateWithLifecycle()
    val activePeers by rollCallViewModel.activePeers.collectAsStateWithLifecycle()
    val blockedPeers by rollCallViewModel.blockedPeers.collectAsStateWithLifecycle()
    val selectedChatPeerId by rollCallViewModel.selectedChatPeerId.collectAsStateWithLifecycle()
    val currentPeerMessages by rollCallViewModel.currentPeerMessages.collectAsStateWithLifecycle()
    val isPeerTyping by rollCallViewModel.isPeerTyping.collectAsStateWithLifecycle()
    val activeMediaViewerId by rollCallViewModel.activeMediaViewerId.collectAsStateWithLifecycle()
    val nowTickerMillis by rollCallViewModel.nowTickerMillis.collectAsStateWithLifecycle()
    val vaultedMedia by rollCallViewModel.vaultedMedia.collectAsStateWithLifecycle()
    val leaderboard by rollCallViewModel.leaderboard.collectAsStateWithLifecycle()
    val safetyReports by rollCallViewModel.safetyReports.collectAsStateWithLifecycle()
    val pointTransactions by rollCallViewModel.pointTransactions.collectAsStateWithLifecycle()
    val statusBannerMessage by rollCallViewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val showInterstitialModal by rollCallViewModel.showInterstitialModal.collectAsStateWithLifecycle()
    val showRewardedModal by rollCallViewModel.showRewardedModal.collectAsStateWithLifecycle()

    LaunchedEffect(userProfile.isPremiumPro) {
        AdMobController.initializeIfNeeded(context, userProfile.isPremiumPro)
    }

    if (!userProfile.onboardingCompleted) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = ObsidianNight
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OnboardingScreen(
                    onCompleteOnboarding = { name, birthYear, myGender, prefGender, interests, idVerified ->
                        rollCallViewModel.completeOnboarding(
                            displayName = name,
                            birthYear = birthYear,
                            myGender = myGender,
                            preferredGender = prefGender,
                            interestsCsv = interests,
                            isIdVerified = idVerified
                        )
                    }
                )
            }
        }
        return
    }

    val destinations = listOf(
        NavDestination(ROUTE_HOME, "Roll Call", Icons.Default.Public, "nav_tab_home"),
        NavDestination(ROUTE_CHAT, "1:1 Chat", Icons.AutoMirrored.Filled.Chat, "nav_tab_chat"),
        NavDestination(ROUTE_LEADERBOARD, "Rankings", Icons.Default.EmojiEvents, "nav_tab_leaderboard"),
        NavDestination(ROUTE_STORE, "Store", Icons.Default.ShoppingCart, "nav_tab_store"),
        NavDestination(ROUTE_SAFETY_SPEC, "Safety", Icons.Default.GppGood, "nav_tab_safety")
    )

    val isFullScreenCall = currentRoute == ROUTE_HOME && (
        callState.status == MatchmakingStatus.SEARCHING_QUEUE ||
            callState.status == MatchmakingStatus.IN_ACTIVE_CALL
        )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight)
    ) {
        val isExpandedScreen = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = ObsidianNight,
            bottomBar = {
                if (!isExpandedScreen && !isFullScreenCall) {
                    NavigationBar(
                        containerColor = DeepSlateSurface,
                        contentColor = Color.White
                    ) {
                        destinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { rollCallViewModel.navigateTo(dest.route) },
                                icon = {
                                    if (dest.route == ROUTE_LEADERBOARD) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = if (userProfile.isLeaderboardUnlocked) {
                                                        EmeraldShield
                                                    } else {
                                                        NeonCoral
                                                    }
                                                ) {
                                                    Text(
                                                        text = if (userProfile.isLeaderboardUnlocked) "ON" else "200",
                                                        color = ObsidianNight
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = dest.icon,
                                                contentDescription = dest.label
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = dest.label
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = dest.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianNight,
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = TextSecondaryMuted,
                                    unselectedTextColor = TextSecondaryMuted
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen && !isFullScreenCall) {
                    NavigationRail(
                        containerColor = DeepSlateSurface,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        destinations.forEach { dest ->
                            val selected = currentRoute == dest.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { rollCallViewModel.navigateTo(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianNight,
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = TextSecondaryMuted,
                                    unselectedTextColor = TextSecondaryMuted
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Dismissible Live Notification Banner
                    AnimatedVisibility(visible = statusBannerMessage != null) {
                        Surface(
                            color = ElevatedCardSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ElectricCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .clickable { rollCallViewModel.dismissBanner() }
                                .testTag("status_feedback_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = statusBannerMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DISMISS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (currentRoute) {
                            ROUTE_HOME -> {
                                HomeAndCallScreen(
                                    userProfile = userProfile,
                                    callState = callState,
                                    activePeers = activePeers,
                                    onSelectPreferredGender = {
                                        rollCallViewModel.setPreferredMatchGender(it)
                                    },
                                    onStartRollCall = { mode ->
                                        rollCallViewModel.startMatchmaking(mode)
                                    },
                                    onFlipCamera = { rollCallViewModel.toggleCameraLens() },
                                    onToggleMute = { rollCallViewModel.toggleMicMute() },
                                    onSwitchCallMode = {
                                        rollCallViewModel.switchCallModeInSession()
                                    },
                                    onSkipNext = { rollCallViewModel.skipToNextMatch(context) },
                                    onEndCall = { rollCallViewModel.endCall(context) },
                                    onTriggerPanicButton = {
                                        rollCallViewModel.triggerPanicButton()
                                    },
                                    onReportAndBlockPeer = { peer, reason ->
                                        rollCallViewModel.reportAndBlockPeer(
                                            peer = peer,
                                            reason = reason,
                                            source = "IN_CALL_REPORT"
                                        )
                                    },
                                    onOpen1on1ChatWithPeer = { peerId ->
                                        rollCallViewModel.selectChatPeer(peerId)
                                        rollCallViewModel.navigateTo(ROUTE_CHAT)
                                    },
                                    onOpenLeaderboard = {
                                        rollCallViewModel.navigateTo(ROUTE_LEADERBOARD)
                                    },
                                    onOpenStore = {
                                        rollCallViewModel.navigateTo(ROUTE_STORE)
                                    },
                                    onSimulateActiveTimeBoost = {
                                        rollCallViewModel.simulateActiveTimeBoost30Mins()
                                    },
                                    onClaimDailyStreak = {
                                        rollCallViewModel.claimDailyStreak()
                                    },
                                    onWatchRewardedAd = {
                                        rollCallViewModel.requestRewardedAd(context)
                                    }
                                )
                            }

                            ROUTE_CHAT -> {
                                ChatScreen(
                                    userProfile = userProfile,
                                    activePeers = activePeers,
                                    selectedPeerId = selectedChatPeerId,
                                    messages = currentPeerMessages,
                                    isPeerTyping = isPeerTyping,
                                    activeMediaViewerId = activeMediaViewerId,
                                    nowTickerMillis = nowTickerMillis,
                                    onSelectPeer = { rollCallViewModel.selectChatPeer(it) },
                                    onSendMessage = { rollCallViewModel.sendTextMessage(it) },
                                    onSendEphemeralMedia = { type, title, style ->
                                        rollCallViewModel.sendEphemeralMedia(type, title, style)
                                    },
                                    onOpenMediaViewer = {
                                        rollCallViewModel.openEphemeralMediaViewer(it)
                                    },
                                    onCloseMediaViewer = {
                                        rollCallViewModel.closeEphemeralMediaViewer()
                                    },
                                    onToggleKeepForever = {
                                        rollCallViewModel.toggleKeepMediaForever(it)
                                    },
                                    onBlockedActionAttempt = {
                                        rollCallViewModel.notifyBlockedSaveOrForwardAttempt(it)
                                    },
                                    onToggleReadReceipts = {
                                        rollCallViewModel.toggleReadReceipts(it)
                                    },
                                    onToggleAutoTranslate = {
                                        rollCallViewModel.toggleAutoTranslate(it)
                                    },
                                    onReportAndBlockPeer = { peer, reason ->
                                        rollCallViewModel.reportAndBlockPeer(
                                            peer = peer,
                                            reason = reason,
                                            source = "IN_CHAT_REPORT"
                                        )
                                    },
                                    onStartDirectCall = {
                                        rollCallViewModel.navigateTo(ROUTE_HOME)
                                        rollCallViewModel.startMatchmaking(CallMode.VIDEO)
                                    },
                                    onNavigateBackHome = {
                                        rollCallViewModel.navigateTo(ROUTE_HOME)
                                    }
                                )
                            }

                            ROUTE_LEADERBOARD -> {
                                LeaderboardScreen(
                                    userProfile = userProfile,
                                    leaderboardEntries = leaderboard,
                                    onSimulateActiveTimeBoost = {
                                        rollCallViewModel.simulateActiveTimeBoost30Mins()
                                    },
                                    onWatchRewardedAd = {
                                        rollCallViewModel.requestRewardedAd(context)
                                    },
                                    onOpenPointsStore = {
                                        rollCallViewModel.navigateTo(ROUTE_STORE)
                                    },
                                    onNavigateBackHome = {
                                        rollCallViewModel.navigateTo(ROUTE_HOME)
                                    }
                                )
                            }

                            ROUTE_STORE -> {
                                StoreScreen(
                                    userProfile = userProfile,
                                    vaultedMedia = vaultedMedia,
                                    transactions = pointTransactions,
                                    onSubscribePro = { planCode, priceLabel ->
                                        rollCallViewModel.subscribeToPro(planCode, priceLabel)
                                    },
                                    onPurchasePointPack = { title, points, price ->
                                        rollCallViewModel.buyPointsPackage(title, points, price)
                                    },
                                    onWatchRewardedAd = {
                                        rollCallViewModel.requestRewardedAd(context)
                                    },
                                    onTriggerInterstitialAd = {
                                        rollCallViewModel.requestInterstitialAd(context)
                                    },
                                    onNavigateBackHome = {
                                        rollCallViewModel.navigateTo(ROUTE_HOME)
                                    }
                                )
                            }

                            ROUTE_SAFETY_SPEC -> {
                                SafetyAndSpecScreen(
                                    blockedPeers = blockedPeers,
                                    safetyReports = safetyReports,
                                    onUnblockPeer = { rollCallViewModel.unblockPeer(it) },
                                    onNavigateBackHome = {
                                        rollCallViewModel.navigateTo(ROUTE_HOME)
                                    }
                                )
                            }
                        }
                    }

                    // Persistent Banner Ad (ca-app-pub-3940256099942544/6300978111)
                    // Automatically hidden if user purchased Premium (isPremiumPro == true) or is in a full-screen call
                    if (!isFullScreenCall && !userProfile.isPremiumPro) {
                        RollCallBannerAd(
                            isPremiumPro = userProfile.isPremiumPro,
                            onUpgradeToProClick = {
                                rollCallViewModel.navigateTo(ROUTE_STORE)
                            }
                        )
                    }
                }
            }
        }

        if (showInterstitialModal && !userProfile.isPremiumPro) {
            InterstitialAdModal(
                isPremiumPro = userProfile.isPremiumPro,
                onDismiss = { rollCallViewModel.dismissInterstitialModal() },
                onUpgradeToPro = {
                    rollCallViewModel.dismissInterstitialModal()
                    rollCallViewModel.navigateTo(ROUTE_STORE)
                }
            )
        }

        if (showRewardedModal && !userProfile.isPremiumPro) {
            RewardedAdModal(
                isPremiumPro = userProfile.isPremiumPro,
                onRewardGranted = { pts ->
                    rollCallViewModel.completeRewardedAdBonus(pts)
                },
                onDismiss = { rollCallViewModel.dismissRewardedModal() }
            )
        }
    }
}
