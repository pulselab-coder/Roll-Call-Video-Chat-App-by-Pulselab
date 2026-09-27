package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.AmberCrown
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun LeaderboardScreen(
    userProfile: UserProfileEntity,
    leaderboardEntries: List<LeaderboardEntryEntity>,
    onSimulateActiveTimeBoost: () -> Unit,
    onWatchRewardedAd: () -> Unit,
    onOpenPointsStore: () -> Unit,
    onNavigateBackHome: () -> Unit
) {
    BackHandler {
        onNavigateBackHome()
    }

    val isUnlocked = userProfile.isLeaderboardUnlocked // Minimum 200 points required

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight),
        contentAlignment = Alignment.TopCenter
    ) {
        if (!isUnlocked) {
            // Minimum 200 Points Required Gate Screen
            val pointsNeeded = (200 - userProfile.totalPoints).coerceAtLeast(0)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                    border = BorderStroke(1.5.dp, NeonCoral),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NeonCoral.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Leaderboard Locked",
                                tint = NeonCoral,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "200 Points Required to Unlock Rankings",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Roll Call requires at least 200 Points (2 hours of active in-app time @ 100 pts/hr, or a Point Boost) to enter the Weekly Global Leaderboard.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryMuted,
                            textAlign = TextAlign.Center
                        )

                        LinearProgressIndicator(
                            progress = { (userProfile.totalPoints / 200f).coerceIn(0f, 1f) },
                            color = AmberCrown,
                            trackColor = DeepSlateSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(50))
                        )

                        Text(
                            text = "Current Balance: ${userProfile.totalPoints} / 200 PTS ($pointsNeeded more needed)",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberCrown
                        )

                        Button(
                            onClick = onSimulateActiveTimeBoost,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = ObsidianNight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("leaderboard_gate_sim_time_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Accrue Active Time"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Log +30m Active Time (+50 Pts Now)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!userProfile.isPremiumPro) {
                            OutlinedButton(
                                onClick = onWatchRewardedAd,
                                border = BorderStroke(1.dp, NeonCoral),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("leaderboard_gate_rewarded_ad_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Watch Rewarded Ad",
                                    tint = NeonCoral
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Watch Rewarded Ad (+25 Bonus Pts)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onOpenPointsStore,
                            border = BorderStroke(1.dp, AmberCrown),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("leaderboard_gate_open_store_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Open Store",
                                tint = AmberCrown
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Boost Points Directly in Store",
                                style = MaterialTheme.typography.labelLarge,
                                color = AmberCrown
                            )
                        }
                    }
                }
            }
            return@Box
        }

        // Unlocked Leaderboard View (>= 200 Points)
        val sorted = leaderboardEntries.sortedByDescending { it.points }
        val myRankIndex = sorted.indexOfFirst { it.isCurrentUser }.let { if (it == -1) sorted.size else it + 1 }
        val nextHigherEntry = if (myRankIndex > 1) sorted.getOrNull(myRankIndex - 2) else null
        val pointsToNextRank = if (nextHigherEntry != null) {
            (nextHigherEntry.points - userProfile.totalPoints + 1).coerceAtLeast(1)
        } else {
            0
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.5.dp, AmberCrown.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Weekly Global Leaderboard",
                                    tint = AmberCrown,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Weekly Global Leaderboard",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Cadence: Resets Weekly (Sun 00:00 UTC) • Top 3 Earn Priority Radar & Crown",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }
                        }

                        // User's Own Rank & Points-to-Next-Rank Indicator
                        Surface(
                            color = ElevatedCardSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ElectricCyan)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "YOUR GLOBAL RANK: #$myRankIndex",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ElectricCyan
                                    )
                                    Text(
                                        text = if (pointsToNextRank > 0) {
                                            "$pointsToNextRank PTS needed to overtake #${myRankIndex - 1} (${nextHigherEntry?.username})"
                                        } else {
                                            "You are #1 Worldwide this week!"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }

                                Button(
                                    onClick = onOpenPointsStore,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AmberCrown,
                                        contentColor = ObsidianNight
                                    ),
                                    modifier = Modifier.testTag("leaderboard_boost_rank_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Boost Rank",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Boost",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            itemsIndexed(sorted, key = { _, item -> item.entryId }) { index, entry ->
                val rank = index + 1
                val isTop3 = rank <= 3
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (entry.isCurrentUser) {
                            ElectricCyan.copy(alpha = 0.15f)
                        } else {
                            ElevatedCardSurface
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        when {
                            entry.isCurrentUser -> ElectricCyan
                            rank == 1 -> AmberCrown
                            rank == 2 -> Color(0xFFC0C8E0)
                            rank == 3 -> NeonCoral
                            else -> GlassBorderColor
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("leaderboard_row_$rank")
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
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (rank) {
                                            1 -> AmberCrown
                                            2 -> Color(0xFFC0C8E0)
                                            3 -> NeonCoral
                                            else -> DeepSlateSurface
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#$rank",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isTop3) ObsidianNight else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${entry.countryFlag} ${entry.username}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (entry.isCurrentUser) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = ElectricCyan.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "YOU",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ElectricCyan,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (entry.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified User",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${"%.1f".format(entry.activeHours)}h active • 🔥 ${entry.streakDays}d streak" +
                                        if (entry.isProSubscriber) " • PRO CROWN" else "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (entry.isProSubscriber) AmberCrown else TextSecondaryMuted
                                )
                            }
                        }

                        Surface(
                            color = DeepSlateSurface,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(
                                1.dp,
                                if (isTop3) AmberCrown else GlassBorderColor
                            )
                        ) {
                            Text(
                                text = "${entry.points} PTS",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isTop3) AmberCrown else EmeraldShield,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
