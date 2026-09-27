package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.GenderOption
import com.example.data.local.MatchedPeerEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.components.CameraPreviewBox
import com.example.ui.security.SecureWindowEffect
import com.example.ui.theme.AmberCrown
import com.example.ui.theme.CrimsonPanic
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.viewmodel.ActiveCallUiState
import com.example.ui.viewmodel.CallMode
import com.example.ui.viewmodel.MatchmakingStatus

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeAndCallScreen(
    userProfile: UserProfileEntity,
    callState: ActiveCallUiState,
    activePeers: List<MatchedPeerEntity>,
    onSelectPreferredGender: (GenderOption) -> Unit,
    onStartRollCall: (CallMode) -> Unit,
    onFlipCamera: () -> Unit,
    onToggleMute: () -> Unit,
    onSwitchCallMode: () -> Unit,
    onSkipNext: () -> Unit,
    onEndCall: () -> Unit,
    onTriggerPanicButton: () -> Unit,
    onReportAndBlockPeer: (MatchedPeerEntity, String) -> Unit,
    onOpen1on1ChatWithPeer: (String) -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenStore: () -> Unit,
    onSimulateActiveTimeBoost: () -> Unit,
    onClaimDailyStreak: () -> Unit,
    onWatchRewardedAd: () -> Unit
) {
    val isInCallOrSearching = callState.status == MatchmakingStatus.SEARCHING_QUEUE ||
        callState.status == MatchmakingStatus.IN_ACTIVE_CALL

    // Enforce FLAG_SECURE and block audio stream recording whenever in an active call
    SecureWindowEffect(
        flagSecureEnabled = isInCallOrSearching,
        voiceRecordingBlockEnabled = callState.status == MatchmakingStatus.IN_ACTIVE_CALL
    )

    if (isInCallOrSearching) {
        BackHandler {
            onEndCall()
        }
        ActiveCallExperience(
            callState = callState,
            onFlipCamera = onFlipCamera,
            onToggleMute = onToggleMute,
            onSwitchCallMode = onSwitchCallMode,
            onSkipNext = onSkipNext,
            onEndCall = onEndCall,
            onTriggerPanicButton = onTriggerPanicButton,
            onReportAndBlockPeer = onReportAndBlockPeer,
            onOpenChatWithCurrentPeer = {
                val peerId = callState.matchedPeer?.peerId
                onEndCall()
                if (peerId != null) {
                    onOpen1on1ChatWithPeer(peerId)
                }
            }
        )
        return
    }

    val preferredGender = runCatching {
        GenderOption.valueOf(userProfile.preferredMatchGender)
    }.getOrDefault(GenderOption.ANY)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Status Bar: User Identity + Points Pill + Pro Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricCyan, NeonCoral)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.displayName.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = ObsidianNight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            if (userProfile.isBiometricIdVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "18+ ID Verified",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = if (userProfile.isPremiumPro) {
                                "Roll Call Pro ($19.99) • Vault Active"
                            } else {
                                "Free Tier • 1:1 Global Radar"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (userProfile.isPremiumPro) AmberCrown else TextSecondaryMuted
                        )
                    }
                }

                // Points Pill
                Surface(
                    color = ElevatedCardSurface,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, AmberCrown.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { onOpenStore() }
                        .testTag("home_points_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Total Points",
                            tint = AmberCrown,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${userProfile.totalPoints} PTS",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberCrown
                        )
                    }
                }
            }

            // Hero Radar Globe Card + Gender Match Filter + Start Roll Call CTAs
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_globe),
                            contentDescription = "Worldwide Matchmaking Radar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            ObsidianNight.copy(alpha = 0.2f),
                                            DeepSlateSurface
                                        )
                                    )
                                )
                        )

                        // Top overlay badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = ObsidianNight.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, EmeraldShield)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Public,
                                        contentDescription = "Global Queue",
                                        tint = EmeraldShield,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GLOBAL QUEUE • NO REGION LOCK",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                color = ObsidianNight.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, ElectricCyan)
                            ) {
                                Text(
                                    text = "1:1 ONLY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Global 1-on-1 Matchmaking",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Text(
                                text = "FLAG_SECURE anti-recording & PhotoDNA AI shield enabled on all calls.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Gender-Filtered Random Matching Selector
                        Text(
                            text = "Select Preferred Match Gender Filter:",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GenderOption.entries.forEach { option ->
                                val selected = preferredGender == option
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSelectPreferredGender(option) },
                                    label = {
                                        Text(
                                            text = "Match: ${option.label}",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricCyan.copy(alpha = 0.22f),
                                        selectedLabelColor = ElectricCyan
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selected,
                                        borderColor = GlassBorderColor,
                                        selectedBorderColor = ElectricCyan
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("filter_gender_${option.name.lowercase()}")
                                )
                            }
                        }

                        // Rate limit banner if active
                        AnimatedVisibility(
                            visible = callState.status == MatchmakingStatus.RATE_LIMITED_COOLDOWN
                        ) {
                            Surface(
                                color = CrimsonPanic.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, CrimsonPanic),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Anti-Bot Queue Cooldown Active: Wait ${callState.rateLimitRemainingSeconds}s before re-entering queue.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // Primary CTA: Start Roll Call (Video) + Voice-Only Call
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onStartRollCall(CallMode.VIDEO) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = ObsidianNight
                                ),
                                modifier = Modifier
                                    .weight(1.35f)
                                    .height(56.dp)
                                    .testTag("start_roll_call_video_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Start Video Call"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Roll Call",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onStartRollCall(CallMode.VOICE_ONLY) },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, NeonCoral),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("start_roll_call_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneInTalk,
                                    contentDescription = "Start Voice-Only Call",
                                    tint = NeonCoral
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Voice Only",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            // Gamified Points & Leaderboard Gate Card (1 hr = 100 pts, 200 pts min for Leaderboard)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                border = BorderStroke(1.dp, GlassBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Points & Ranking Engine",
                                tint = AmberCrown
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Points & Ranking Engine",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = "1 Hour Active Time = 100 Points • 200 Pts Unlocks Rankings",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        Surface(
                            color = if (userProfile.isLeaderboardUnlocked) {
                                EmeraldShield.copy(alpha = 0.2f)
                            } else {
                                NeonCoral.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(
                                1.dp,
                                if (userProfile.isLeaderboardUnlocked) EmeraldShield else NeonCoral
                            )
                        ) {
                            Text(
                                text = if (userProfile.isLeaderboardUnlocked) "RANKED UNLOCKED" else "LOCKED (<200 PTS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (userProfile.isLeaderboardUnlocked) EmeraldShield else NeonCoral,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    val progressTo200 = (userProfile.totalPoints / 200f).coerceIn(0f, 1f)
                    val activeMinutes = userProfile.activeSecondsAccrued / 60
                    LinearProgressIndicator(
                        progress = { progressTo200 },
                        color = if (userProfile.isLeaderboardUnlocked) EmeraldShield else AmberCrown,
                        trackColor = DeepSlateSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Active Time: ${activeMinutes}m (${userProfile.earnedPoints} earned + ${userProfile.purchasedPoints} store pts)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = "${userProfile.totalPoints} / 200 PTS",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberCrown
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onSimulateActiveTimeBoost,
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("simulate_active_time_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Simulate 30m Active Time",
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+30m Time (+50 Pts)",
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricCyan
                            )
                        }

                        OutlinedButton(
                            onClick = onClaimDailyStreak,
                            enabled = !userProfile.streakClaimedToday,
                            border = BorderStroke(
                                1.dp,
                                if (!userProfile.streakClaimedToday) AmberCrown else GlassBorderColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("claim_streak_button")
                        ) {
                            Text(
                                text = if (userProfile.streakClaimedToday) {
                                    "🔥 ${userProfile.dailyStreakDays}d Streak Claimed"
                                } else {
                                    "🔥 Claim ${userProfile.dailyStreakDays + 1}d (+25 Pts)"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = if (!userProfile.streakClaimedToday) AmberCrown else TextSecondaryMuted
                            )
                        }
                    }

                    // Rewarded Ad Button (only shown when user has NOT purchased Premium)
                    if (!userProfile.isPremiumPro) {
                        OutlinedButton(
                            onClick = onWatchRewardedAd,
                            border = BorderStroke(1.dp, NeonCoral),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("home_watch_rewarded_ad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Watch Rewarded Ad",
                                tint = NeonCoral,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Watch Rewarded Ad (+25 Bonus Points)",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }

                    Button(
                        onClick = onOpenLeaderboard,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (userProfile.isLeaderboardUnlocked) {
                                AmberCrown
                            } else {
                                DeepSlateSurface
                            },
                            contentColor = if (userProfile.isLeaderboardUnlocked) {
                                ObsidianNight
                            } else {
                                Color.White
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("home_leaderboard_access_button")
                    ) {
                        Icon(
                            imageVector = if (userProfile.isLeaderboardUnlocked) {
                                Icons.Default.EmojiEvents
                            } else {
                                Icons.Default.Lock
                            },
                            contentDescription = "Leaderboard Access"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (userProfile.isLeaderboardUnlocked) {
                                "Enter Weekly Global Leaderboard (${userProfile.totalPoints} Pts)"
                            } else {
                                "Need ${200 - userProfile.totalPoints} More Points to Unlock Leaderboard"
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Recent 1:1 Global Matches Quick Roster
            Text(
                text = "Recent 1:1 Global Matches (Tap to Message or Call)",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            activePeers.forEach { peer ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpen1on1ChatWithPeer(peer.peerId) }
                        .testTag("recent_peer_card_${peer.peerId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (peer.avatarKey == "female") {
                                        R.drawable.img_match_avatar_female
                                    } else {
                                        R.drawable.img_match_avatar_male
                                    }
                                ),
                                contentDescription = "${peer.displayName} Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, ElectricCyan, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${peer.countryFlag} ${peer.displayName}, ${peer.age}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White
                                    )
                                    if (peer.isIdVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified 18+ Peer",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${peer.countryName} • ${peer.gender} • ${peer.nativeLanguage}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan
                                )
                                Text(
                                    text = peer.statusBio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted,
                                    maxLines = 1
                                )
                            }
                        }

                        IconButton(
                            onClick = { onOpen1on1ChatWithPeer(peer.peerId) },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("open_chat_btn_${peer.peerId}")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Open 1:1 Ephemeral Chat",
                                tint = ElectricCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveCallExperience(
    callState: ActiveCallUiState,
    onFlipCamera: () -> Unit,
    onToggleMute: () -> Unit,
    onSwitchCallMode: () -> Unit,
    onSkipNext: () -> Unit,
    onEndCall: () -> Unit,
    onTriggerPanicButton: () -> Unit,
    onReportAndBlockPeer: (MatchedPeerEntity, String) -> Unit,
    onOpenChatWithCurrentPeer: () -> Unit
) {
    var showReportDialog by remember { mutableStateOf(false) }
    val peer = callState.matchedPeer

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight)
    ) {
        if (callState.status == MatchmakingStatus.SEARCHING_QUEUE || peer == null) {
            // Queue Searching State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = ElectricCyan,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(68.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Scanning Global 1:1 Matchmaking Queue...",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FLAG_SECURE Engaged • PhotoDNA & Cloud Vision AI Shield Active",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldShield,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                OutlinedButton(
                    onClick = onEndCall,
                    border = BorderStroke(1.dp, CrimsonPanic),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("cancel_queue_button")
                ) {
                    Text("Leave Queue", color = CrimsonPanic)
                }
            }
        } else {
            // Full-Bleed Remote Stream (Video or Voice-Only Mode)
            if (callState.callMode == CallMode.VIDEO) {
                Image(
                    painter = painterResource(
                        id = if (peer.avatarKey == "female") {
                            R.drawable.img_match_avatar_female
                        } else {
                            R.drawable.img_match_avatar_male
                        }
                    ),
                    contentDescription = "Remote Peer Video Stream",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    ObsidianNight.copy(alpha = 0.85f),
                                    Color.Transparent,
                                    ObsidianNight.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            } else {
                // Voice-Only Call Mode Visualizer
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = if (peer.avatarKey == "female") {
                                R.drawable.img_match_avatar_female
                            } else {
                                R.drawable.img_match_avatar_male
                            }
                        ),
                        contentDescription = "Voice Call Peer Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(3.dp, NeonCoral, CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Live Voice Stream",
                            tint = ElectricCyan
                        )
                        Text(
                            text = "VOICE-ONLY ENCRYPTED STREAM",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan
                        )
                    }
                }
            }

            // Top Security Banner + Peer Info + 1-Tap PANIC BUTTON
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Strict Platform Anti-Recording Enforcement Strip
                Surface(
                    color = ObsidianNight.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EmeraldShield)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "FLAG_SECURE Active",
                                tint = EmeraldShield,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FLAG_SECURE • NO SCREENSHOTS / NO SCREEN OR VOICE REC",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldShield
                            )
                        }
                        val mins = callState.callDurationSeconds / 60
                        val secs = callState.callDurationSeconds % 60
                        Text(
                            text = "%02d:%02d".format(mins, secs),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }

                // Peer Identity Card + 1-Tap PANIC SAFETY BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = ObsidianNight.copy(alpha = 0.82f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, GlassBorderColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${peer.countryFlag} ${peer.displayName}, ${peer.age}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                if (peer.isIdVerified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "18+ Verified",
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${peer.countryName} • ${peer.gender}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                peer.interestsCsv.split(",").take(3).forEach { tag ->
                                    Surface(
                                        color = ElevatedCardSurface,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = tag.trim(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // ONE-TAP PANIC / SAFETY BUTTON
                    Button(
                        onClick = onTriggerPanicButton,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonPanic,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("in_call_panic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = "Panic Button: End Call & Report Immediately"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PANIC",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Local CameraX PiP Window (when in Video mode)
            if (callState.callMode == CallMode.VIDEO) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp, bottom = 90.dp)
                        .width(118.dp)
                        .height(168.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(2.dp, ElectricCyan, RoundedCornerShape(18.dp))
                ) {
                    CameraPreviewBox(
                        isFrontCamera = callState.isFrontCamera,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = ObsidianNight.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(bottomEnd = 10.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = if (callState.isFrontCamera) "YOU (FRONT)" else "YOU (BACK)",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Bottom Call Control Dock (Flip Camera, Mute, Mode Switch, Chat, Report, Skip, End)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // AI Moderation Live Status Strip
                Surface(
                    color = ObsidianNight.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "AI Content Moderation",
                            tint = EmeraldShield,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = callState.aiModerationStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DeepSlateSurface.copy(alpha = 0.95f)
                    ),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Flip Front/Back Camera
                            IconButton(
                                onClick = onFlipCamera,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedCardSurface)
                                    .testTag("in_call_flip_camera_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = "Flip Front/Back Camera",
                                    tint = ElectricCyan
                                )
                            }

                            // Toggle Mic Mute
                            IconButton(
                                onClick = onToggleMute,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (callState.isMicMuted) CrimsonPanic.copy(alpha = 0.3f)
                                        else ElevatedCardSurface
                                    )
                                    .testTag("in_call_mute_button")
                            ) {
                                Icon(
                                    imageVector = if (callState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Toggle Microphone Mute",
                                    tint = if (callState.isMicMuted) CrimsonPanic else Color.White
                                )
                            }

                            // Switch Video / Voice Mode
                            IconButton(
                                onClick = onSwitchCallMode,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedCardSurface)
                                    .testTag("in_call_mode_switch_button")
                            ) {
                                Icon(
                                    imageVector = if (callState.callMode == CallMode.VIDEO) {
                                        Icons.Default.Videocam
                                    } else {
                                        Icons.Default.VideocamOff
                                    },
                                    contentDescription = "Switch Video/Voice Mode",
                                    tint = NeonCoral
                                )
                            }

                            // Open 1:1 Ephemeral Chat with this Peer
                            IconButton(
                                onClick = onOpenChatWithCurrentPeer,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedCardSurface)
                                    .testTag("in_call_open_chat_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Open 1:1 Ephemeral Chat",
                                    tint = ElectricCyan
                                )
                            }

                            // Report / Block Modal Trigger
                            IconButton(
                                onClick = { showReportDialog = true },
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedCardSurface)
                                    .testTag("in_call_report_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Report or Block Peer",
                                    tint = AmberCrown
                                )
                            }
                        }

                        // Skip / Next Match & End Call Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onSkipNext,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = ObsidianNight
                                ),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(50.dp)
                                    .testTag("in_call_skip_next_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Skip to Next Match"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Skip / Next Match",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onEndCall,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CrimsonPanic,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("in_call_end_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "End Call"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "End Call",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog && peer != null) {
        val reasons = listOf(
            "Inappropriate Nudity / Explicit Content",
            "Harassment / Abusive Behavior",
            "Suspected Underage User (<18) / CSAM Alert",
            "Spam / Bot Behavior"
        )
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = ElevatedCardSurface,
            titleContentColor = Color.White,
            textContentColor = TextSecondaryMuted,
            title = { Text("Report & Block ${peer.displayName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Select violation category. This immediately terminates the call, blocks ${peer.displayName}, and submits a PhotoDNA/Moderation log:"
                    )
                    reasons.forEach { reason ->
                        OutlinedButton(
                            onClick = {
                                showReportDialog = false
                                onReportAndBlockPeer(peer, reason)
                            },
                            border = BorderStroke(1.dp, CrimsonPanic),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(reason, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = ElectricCyan)
                }
            }
        )
    }
}
