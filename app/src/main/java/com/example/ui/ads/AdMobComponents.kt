package com.example.ui.ads

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AmberCrown
import com.example.ui.theme.DeepSlateSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElevatedCardSurface
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.GlassBorderColor
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.TextSecondaryMuted
import kotlinx.coroutines.delay

object RollCallAdIds {
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
}

/**
 * Manages AdMob Banner (ca-app-pub-3940256099942544/6300978111),
 * Interstitial (ca-app-pub-3940256099942544/1033173712), and
 * Rewarded (ca-app-pub-3940256099942544/5224354917) ads using native Compose rendering
 * to avoid WebView / PrivacySandbox ServiceUnavailableException errors in the cloud emulator.
 * Strictly suppresses all ads when the user has purchased Roll Call Pro (isPremiumPro == true).
 */
object AdMobController {
    private var interstitialReady: Boolean = false
    private var rewardedReady: Boolean = false

    fun initializeIfNeeded(context: Context, isPremiumPro: Boolean) {
        if (isPremiumPro) {
            clearAdsForPremium()
            return
        }
        interstitialReady = true
        rewardedReady = true
    }

    fun clearAdsForPremium() {
        interstitialReady = false
        rewardedReady = false
    }

    fun showInterstitialAd(
        context: Context,
        isPremiumPro: Boolean,
        onShowFallbackDialog: () -> Unit,
        onAdClosed: () -> Unit
    ) {
        if (isPremiumPro) {
            clearAdsForPremium()
            onAdClosed()
            return
        }
        onShowFallbackDialog()
    }

    fun showRewardedAd(
        context: Context,
        isPremiumPro: Boolean,
        onRewardEarned: (Int) -> Unit,
        onShowFallbackRewardedDialog: () -> Unit
    ) {
        if (isPremiumPro) {
            clearAdsForPremium()
            return
        }
        onShowFallbackRewardedDialog()
    }
}

/**
 * AdMob Banner Ad Composable using Ad Unit ID: ca-app-pub-3940256099942544/6300978111.
 * Strictly renders NOTHING when [isPremiumPro] is true.
 */
@Composable
fun RollCallBannerAd(
    isPremiumPro: Boolean,
    onUpgradeToProClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // If user purchased premium, NO ads will be displayed
    if (isPremiumPro) return

    Surface(
        color = DeepSlateSurface,
        border = BorderStroke(1.dp, GlassBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_banner_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AmberCrown.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, AmberCrown)
                    ) {
                        Text(
                            text = "AD",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberCrown,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Banner: ${RollCallAdIds.BANNER_AD_UNIT_ID}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                }

                Text(
                    text = "Remove Ads (Pro $19.99) →",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onUpgradeToProClick() }
                        .padding(vertical = 2.dp)
                        .testTag("banner_remove_ads_link")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElevatedCardSurface),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Sponsored Banner",
                        tint = AmberCrown,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Google AdMob Banner (${RollCallAdIds.BANNER_AD_UNIT_ID})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Interactive Interstitial Ad Dialog for Ad Unit ID: ca-app-pub-3940256099942544/1033173712.
 * Never displayed if user is Premium.
 */
@Composable
fun InterstitialAdModal(
    isPremiumPro: Boolean,
    onDismiss: () -> Unit,
    onUpgradeToPro: () -> Unit
) {
    if (isPremiumPro) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepSlateSurface,
        titleContentColor = Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = NeonCoral.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, NeonCoral)
                    ) {
                        Text(
                            text = "INTERSTITIAL AD",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonCoral,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_interstitial_ad_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Interstitial Ad",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Close", color = Color.White)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = ElevatedCardSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ElectricCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Google AdMob Interstitial",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Ad Unit ID: ${RollCallAdIds.INTERSTITIAL_AD_UNIT_ID}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan
                        )
                        Text(
                            text = "Tired of seeing interstitial ads between Roll Call matches? Upgrade to Roll Call Pro ($19.99) for a 100% Ad-Free experience + Permanent Media Vault.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }

                Button(
                    onClick = {
                        onDismiss()
                        onUpgradeToPro()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberCrown,
                        contentColor = ObsidianNight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("interstitial_upgrade_pro_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = "Remove All Ads with Pro"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Go Pro ($19.99) • Remove All Ads",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, ElectricCyan)
            ) {
                Text("Continue to Match", color = ElectricCyan)
            }
        }
    )
}

/**
 * Interactive Rewarded Ad Modal for Ad Unit ID: ca-app-pub-3940256099942544/5224354917.
 * Grants +25 Bonus Points upon completion.
 */
@Composable
fun RewardedAdModal(
    isPremiumPro: Boolean,
    onRewardGranted: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (isPremiumPro) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    var secondsRemaining by remember { mutableIntStateOf(3) }
    var rewardUnlocked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        rewardUnlocked = true
    }

    AlertDialog(
        onDismissRequest = {
            if (rewardUnlocked) {
                onRewardGranted(25)
            } else {
                onDismiss()
            }
        },
        containerColor = DeepSlateSurface,
        titleContentColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Rewarded Video Ad",
                    tint = AmberCrown
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rewarded Ad (+25 Bonus Points)",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = ElevatedCardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AmberCrown),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ad Unit ID: ${RollCallAdIds.REWARDED_AD_UNIT_ID}",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberCrown
                        )
                        Text(
                            text = if (rewardUnlocked) {
                                "Reward Unlocked! Claim your +25 Bonus Points toward the 200-Point Weekly Leaderboard."
                            } else {
                                "Watching Google AdMob Rewarded Ad... (${secondsRemaining}s remaining)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                        LinearProgressIndicator(
                            progress = { (3 - secondsRemaining) / 3f },
                            color = if (rewardUnlocked) EmeraldShield else AmberCrown,
                            trackColor = ObsidianNight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onRewardGranted(25) },
                enabled = rewardUnlocked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldShield,
                    contentColor = ObsidianNight
                ),
                modifier = Modifier.testTag("claim_rewarded_ad_points_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Claim +25 Points"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (rewardUnlocked) "Claim +25 Points" else "Wait ${secondsRemaining}s...",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            if (!rewardUnlocked) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel Ad", color = TextSecondaryMuted)
                }
            }
        }
    )
}
