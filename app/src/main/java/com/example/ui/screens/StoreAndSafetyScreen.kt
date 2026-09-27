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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MatchedPeerEntity
import com.example.data.local.PointTransactionEntity
import com.example.data.local.SafetyReportEntity
import com.example.data.local.UserProfileEntity
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

data class PointStorePackage(
    val id: String,
    val points: Int,
    val priceUsd: String,
    val subtitle: String,
    val discountBadge: String?
)

@Composable
fun StoreScreen(
    userProfile: UserProfileEntity,
    vaultedMedia: List<ChatMessageEntity>,
    transactions: List<PointTransactionEntity>,
    onSubscribePro: (planCode: String, priceLabel: String) -> Unit,
    onPurchasePointPack: (title: String, points: Int, priceUsd: String) -> Unit,
    onWatchRewardedAd: () -> Unit,
    onTriggerInterstitialAd: () -> Unit,
    onNavigateBackHome: () -> Unit
) {
    BackHandler {
        onNavigateBackHome()
    }

    val pointPackages = listOf(
        PointStorePackage(
            id = "pts_100",
            points = 100,
            priceUsd = "$1.49",
            subtitle = "Base Rate ($0.0149 / pt) • Instant +100 Rank Pts",
            discountBadge = null
        ),
        PointStorePackage(
            id = "pts_200",
            points = 200,
            priceUsd = "$2.49",
            subtitle = "Unlocks 200-Pt Leaderboard Gate Immediately",
            discountBadge = "SAVE 16%"
        ),
        PointStorePackage(
            id = "pts_500",
            points = 500,
            priceUsd = "$5.49",
            subtitle = "Volume Tier 2 ($0.0110 / pt) • Equivalent to 5h Active Time",
            discountBadge = "SAVE 26%"
        ),
        PointStorePackage(
            id = "pts_1000",
            points = 1000,
            priceUsd = "$9.99",
            subtitle = "Volume Tier 3 ($0.0099 / pt) • Top-5 Contender Boost",
            discountBadge = "SAVE 33%"
        ),
        PointStorePackage(
            id = "pts_2500",
            points = 2500,
            priceUsd = "$19.99",
            subtitle = "Apex Podium Pack ($0.0080 / pt) • #1 Global Contender",
            discountBadge = "BEST VALUE • SAVE 46%"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Roll Call Pro ($19.99/mo Subscription Tier) Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.5.dp, AmberCrown),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = "Roll Call Pro",
                                    tint = AmberCrown,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Roll Call Pro ($19.99/mo)",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "100% Ad-Free + Media Vault + Rank Boosts",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmberCrown
                                    )
                                }
                            }

                            Surface(
                                color = if (userProfile.isPremiumPro) {
                                    EmeraldShield.copy(alpha = 0.2f)
                                } else {
                                    AmberCrown.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    1.dp,
                                    if (userProfile.isPremiumPro) EmeraldShield else AmberCrown
                                )
                            ) {
                                Text(
                                    text = if (userProfile.isPremiumPro) "NO ADS • PRO" else "UPGRADE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (userProfile.isPremiumPro) EmeraldShield else AmberCrown,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "• 100% Ad-Free Experience: Immediately disables all Banner, Interstitial, and Rewarded Ads across the entire app.\n" +
                                "• Keep Media Forever: Bypasses the 60-second expiry for both sent & received active media inside your encrypted in-app Pro Vault.\n" +
                                "• Direct Leaderboard Point Purchases: Unlocks à la carte Point Packs below + 100 Bonus Welcome Points.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onSubscribePro("MONTHLY_19_99", "$19.99/mo") },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberCrown,
                                    contentColor = ObsidianNight
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("subscribe_pro_monthly_button")
                            ) {
                                Text(
                                    text = if (userProfile.isPremiumPro) {
                                        "Pro Active (No Ads)"
                                    } else {
                                        "Subscribe $19.99/mo"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onSubscribePro("ANNUAL_149_99", "$149.99/yr") },
                                border = BorderStroke(1.dp, ElectricCyan),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("subscribe_pro_annual_button")
                            ) {
                                Text(
                                    text = "Annual $149.99/yr",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ElectricCyan
                                )
                            }
                        }
                    }
                }
            }

            // Free-Tier AdMob Rewarded & Interstitial Card (Hidden when Premium is purchased)
            if (!userProfile.isPremiumPro) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                        border = BorderStroke(1.dp, NeonCoral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Free Tier • Google AdMob Rewards & Ads",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "• Banner ID: ca-app-pub-3940256099942544/6300978111\n" +
                                    "• Interstitial ID: ca-app-pub-3940256099942544/1033173712\n" +
                                    "• Rewarded ID: ca-app-pub-3940256099942544/5224354917\n" +
                                    "Subscribe to Roll Call Pro ($19.99) above to disable all ads permanently.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = onWatchRewardedAd,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCoral,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("store_watch_rewarded_ad_button")
                                ) {
                                    Text(
                                        text = "Rewarded Ad (+25 Pts)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                OutlinedButton(
                                    onClick = onTriggerInterstitialAd,
                                    border = BorderStroke(1.dp, ElectricCyan),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("store_test_interstitial_ad_button")
                                ) {
                                    Text(
                                        text = "Show Interstitial",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ElectricCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // À La Carte Points Store (Google Play Billing Pricing Curve)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Points & Currency Store (Google Play Billing)",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = if (userProfile.isPremiumPro) {
                            "Pro Subscriber Privilege Active: Purchase point packs below to boost your leaderboard rank directly."
                        } else {
                            "Note: Point Pack purchases require an active Roll Call Pro ($19.99) membership (or tap any pack after activating Pro)."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (userProfile.isPremiumPro) EmeraldShield else TextSecondaryMuted
                    )
                }
            }

            items(pointPackages, key = { it.id }) { pkg ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                    border = BorderStroke(
                        1.dp,
                        if (pkg.discountBadge != null) ElectricCyan.copy(alpha = 0.5f) else GlassBorderColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = "Points Package",
                                    tint = AmberCrown,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${pkg.points} Points",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                if (pkg.discountBadge != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = NeonCoral.copy(alpha = 0.22f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, NeonCoral)
                                    ) {
                                        Text(
                                            text = pkg.discountBadge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NeonCoral,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pkg.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                if (!userProfile.isPremiumPro) {
                                    // Auto-bundle or prompt Pro requirement
                                    onSubscribePro("MONTHLY_19_99", "$19.99/mo")
                                }
                                onPurchasePointPack(
                                    "${pkg.points} Points Pack (Play Billing)",
                                    pkg.points,
                                    pkg.priceUsd
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = ObsidianNight
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("buy_pack_${pkg.points}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Buy Points",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pkg.priceUsd,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Encrypted Pro Media Vault Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdded,
                                contentDescription = "Permanent Media Vault",
                                tint = AmberCrown
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Encrypted Pro Media Vault (${vaultedMedia.size} Kept)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White
                                )
                                Text(
                                    text = "Media kept via Roll Call Pro bypasses the 60s expiry inside the app but can never be saved to device gallery or shared externally.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        if (vaultedMedia.isEmpty()) {
                            Surface(
                                color = ElevatedCardSurface,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No media vaulted yet. Open a 1:1 Chat and tap 'Keep Forever (Pro)' on any active 60s photo or video.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        } else {
                            vaultedMedia.forEach { media ->
                                Surface(
                                    color = ElevatedCardSurface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, AmberCrown.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = media.mediaTitle ?: "Vaulted Media",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${media.mediaType} • ${if (media.senderIsMe) "Sent by You" else "Received"} • Permanent Pro Vault",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AmberCrown
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Encrypted In-App Only",
                                            tint = EmeraldShield
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recent Point & Billing Ledger
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Points Ledger",
                                tint = ElectricCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Points & Play Billing Ledger",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }

                        transactions.take(8).forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${tx.transactionType} • ${tx.priceUsd}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                                Text(
                                    text = "+${tx.pointsDelta} PTS",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = EmeraldShield
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SafetyAndSpecScreen(
    blockedPeers: List<MatchedPeerEntity>,
    safetyReports: List<SafetyReportEntity>,
    onUnblockPeer: (String) -> Unit,
    onNavigateBackHome: () -> Unit
) {
    BackHandler {
        onNavigateBackHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianNight),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trust, Safety & CSAM/PhotoDNA Compliance Pipeline
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.5.dp, EmeraldShield),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GppGood,
                                contentDescription = "Trust & Safety Shield",
                                tint = EmeraldShield,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Trust, Safety & Play Policy Shield",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Real-time AI Moderation • PhotoDNA CSAM Hash Check • NCMEC Reporting",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldShield
                                )
                            }
                        }

                        Text(
                            text = "• Mandatory 18+ Verification: Enforced at onboarding with biometric ID liveness token validation.\n" +
                                "• Real-Time Content Moderation: Live video frames & shared media pass through Cloud Vision / Rekognition nudity & violence filters before rendering.\n" +
                                "• CSAM Detection & Reporting: Perceptual hash-matching (PhotoDNA) scans all media uploads with automated NCMEC CyberTipline escalation.\n" +
                                "• Anti-Recording Enforcement: WindowManager.LayoutParams.FLAG_SECURE + AudioAttributes.ALLOW_CAPTURE_BY_NONE block screenshots, screen recording, and external audio capture during calls.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }

            // Documented Resolution of Section 10 Open Design Decisions
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedCardSurface),
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Policy,
                                contentDescription = "Resolved Product Decisions",
                                tint = ElectricCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Resolved Specification & Architecture Decisions",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }

                        val decisions = listOf(
                            "1. $19.99 Premium Tier Model" to
                                "Configured as 'Roll Call Pro' ($19.99/mo subscription or $149.99/yr annual). Pro unlocks the Permanent Media Vault AND unlocks the ability to buy à la carte Point Packs ($1.49–$19.99) to boost rank directly.",
                            "2. 'Keep Media Forever' Scope & Retroactivity" to
                                "Applies to both sent and received media that are currently active (unexpired) or sent/received after subscribing. Expired media is cryptographically shredded after 60s and never retroactively recoverable. Kept media stays strictly inside the encrypted in-app Vault (never exported to device MediaStore).",
                            "3. 60-Second Ephemeral Media Trigger" to
                                "Triggered On-View (60 seconds from the moment the recipient opens the media, with a live circular countdown ring) plus a 24h unopened server TTL. FLAG_SECURE is scoped to active calls AND the chat media viewer.",
                            "4. Leaderboard Reset Cadence & Rewards" to
                                "Resets Weekly (every Sunday at 00:00 UTC). Minimum 200 points (2h active time @ 100 pts/hr) required to enter. Top 3 earn Verified Crown Badges and priority matchmaking queue routing.",
                            "5. Moderation Data Retention & Regional Compliance" to
                                "User-facing media auto-deletes after 60s, while encrypted perceptual hashes (PhotoDNA) and safety report metadata are retained for 90 days for legal/moderation audits. Geo-compliance blocks restricted jurisdictions."
                        )

                        decisions.forEach { (title, body) ->
                            Surface(
                                color = DeepSlateSurface,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = body,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Blocked Users Management
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Blocked Users",
                                tint = NeonCoral
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Blocked Users (${blockedPeers.size})",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }

                        if (blockedPeers.isEmpty()) {
                            Text(
                                text = "No users currently blocked. Use the in-call Panic Button or Report button to immediately block abusive matches.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        } else {
                            blockedPeers.forEach { peer ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${peer.countryFlag} ${peer.displayName} (${peer.countryName})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                    OutlinedButton(
                                        onClick = { onUnblockPeer(peer.peerId) },
                                        border = BorderStroke(1.dp, ElectricCyan),
                                        modifier = Modifier.testTag("unblock_peer_${peer.peerId}")
                                    ) {
                                        Text("Unblock", color = ElectricCyan)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Moderation & Safety Audit Queue
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    border = BorderStroke(1.dp, GlassBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Report,
                                contentDescription = "Safety Audit Log",
                                tint = CrimsonPanic
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Moderation & PhotoDNA Audit Queue (${safetyReports.size})",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }

                        if (safetyReports.isEmpty()) {
                            Text(
                                text = "0 safety incidents recorded in this session.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryMuted
                            )
                        } else {
                            safetyReports.forEach { report ->
                                Surface(
                                    color = ElevatedCardSurface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, CrimsonPanic.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "${report.triggerSource} • Peer: ${report.peerName}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = CrimsonPanic
                                        )
                                        Text(
                                            text = "Reason: ${report.reasonCategory}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Hash Audit ID: ${report.photoDnaHashSample} • NCMEC Escalated: ${if (report.ncmecEscalated) "YES" else "STANDARD REVIEW"}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondaryMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
