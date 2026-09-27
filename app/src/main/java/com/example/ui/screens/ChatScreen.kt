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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FileDownloadOff
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ForwardToInbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MatchedPeerEntity
import com.example.data.local.MediaAttachmentType
import com.example.data.local.UserProfileEntity
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

@Composable
fun ChatScreen(
    userProfile: UserProfileEntity,
    activePeers: List<MatchedPeerEntity>,
    selectedPeerId: String,
    messages: List<ChatMessageEntity>,
    isPeerTyping: Boolean,
    activeMediaViewerId: Long?,
    nowTickerMillis: Long,
    onSelectPeer: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onSendEphemeralMedia: (MediaAttachmentType, String, String) -> Unit,
    onOpenMediaViewer: (Long) -> Unit,
    onCloseMediaViewer: () -> Unit,
    onToggleKeepForever: (Long) -> Unit,
    onBlockedActionAttempt: (String) -> Unit,
    onToggleReadReceipts: (Boolean) -> Unit,
    onToggleAutoTranslate: (Boolean) -> Unit,
    onReportAndBlockPeer: (MatchedPeerEntity, String) -> Unit,
    onStartDirectCall: () -> Unit,
    onNavigateBackHome: () -> Unit
) {
    BackHandler {
        if (activeMediaViewerId != null) {
            onCloseMediaViewer()
        } else {
            onNavigateBackHome()
        }
    }

    // Scope FLAG_SECURE to active ephemeral media viewer so screenshots of expiring media are blocked,
    // while allowing screenshots in normal text chat per Section 3 of the specification.
    val isViewingExpiringMedia = activeMediaViewerId != null
    SecureWindowEffect(
        flagSecureEnabled = isViewingExpiringMedia,
        voiceRecordingBlockEnabled = false
    )

    var messageInput by remember { mutableStateOf("") }
    var showReportModal by remember { mutableStateOf(false) }

    val currentPeer = activePeers.find { it.peerId == selectedPeerId } ?: activePeers.firstOrNull()

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
        ) {
            // Top 1:1 Enforcement & Peer Switcher Header
            Surface(
                color = DeepSlateSurface,
                border = BorderStroke(1.dp, GlassBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Strictly 1:1 Chat",
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1:1 DIRECT ONLY • NO GROUP CHATS • 60S MEDIA TTL",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = userProfile.readReceiptsEnabled,
                                onClick = { onToggleReadReceipts(!userProfile.readReceiptsEnabled) },
                                label = {
                                    Text(
                                        text = if (userProfile.readReceiptsEnabled) "Receipts ON" else "Receipts OFF",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = "Read Receipts Toggle",
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier.testTag("toggle_read_receipts_chip")
                            )

                            FilterChip(
                                selected = userProfile.autoTranslateEnabled,
                                onClick = { onToggleAutoTranslate(!userProfile.autoTranslateEnabled) },
                                label = {
                                    Text(
                                        text = "Translate",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = "Auto Translate Toggle",
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                modifier = Modifier.testTag("toggle_auto_translate_chip")
                            )
                        }
                    }

                    // 1:1 Matched Peer Selector Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(activePeers, key = { it.peerId }) { peer ->
                            val isSelected = peer.peerId == currentPeer?.peerId
                            Surface(
                                color = if (isSelected) {
                                    ElectricCyan.copy(alpha = 0.2f)
                                } else {
                                    ElevatedCardSurface
                                },
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ElectricCyan else GlassBorderColor
                                ),
                                modifier = Modifier
                                    .clickable { onSelectPeer(peer.peerId) }
                                    .testTag("chat_peer_tab_${peer.peerId}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${peer.countryFlag} ${peer.displayName}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) ElectricCyan else Color.White
                                    )
                                    if (peer.isIdVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Active Peer Bar with Call & Report/Block Actions
                    if (currentPeer != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${currentPeer.displayName} (${currentPeer.countryName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Interests: ${currentPeer.interestsCsv}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted,
                                    maxLines = 1
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                IconButton(
                                    onClick = onStartDirectCall,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(ElectricCyan.copy(alpha = 0.18f))
                                        .testTag("chat_start_call_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = "Start 1:1 Video Call",
                                        tint = ElectricCyan
                                    )
                                }

                                IconButton(
                                    onClick = { showReportModal = true },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(CrimsonPanic.copy(alpha = 0.18f))
                                        .testTag("chat_report_peer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flag,
                                        contentDescription = "Report & Block Peer",
                                        tint = CrimsonPanic
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageBubble(
                        message = msg,
                        nowTickerMillis = nowTickerMillis,
                        readReceiptsEnabled = userProfile.readReceiptsEnabled,
                        autoTranslateEnabled = userProfile.autoTranslateEnabled,
                        isProUser = userProfile.isPremiumPro,
                        onOpenEphemeralMedia = { onOpenMediaViewer(msg.id) },
                        onToggleKeepForever = { onToggleKeepForever(msg.id) },
                        onBlockedSaveOrForward = onBlockedActionAttempt
                    )
                }

                if (isPeerTyping && currentPeer != null) {
                    item {
                        Surface(
                            color = ElevatedCardSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${currentPeer.displayName} is typing...",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Ephemeral Media Quick-Send Bar + Message Composer
            Surface(
                color = DeepSlateSurface,
                border = BorderStroke(1.dp, GlassBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Ephemeral Media Senders + Blocked Forward Notice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSendEphemeralMedia(
                                    MediaAttachmentType.IMAGE,
                                    "Snap_60s_${System.currentTimeMillis() % 1000}.jpg",
                                    "CYBER_TOKYO"
                                )
                            },
                            border = BorderStroke(1.dp, NeonCoral),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("send_ephemeral_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Send 60s Ephemeral Photo",
                                tint = NeonCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "60s Photo",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onSendEphemeralMedia(
                                    MediaAttachmentType.VIDEO,
                                    "Clip_60s_${System.currentTimeMillis() % 1000}.mp4",
                                    "NEON_VIDEO"
                                )
                            },
                            border = BorderStroke(1.dp, ElectricCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("send_ephemeral_video_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Send 60s Ephemeral Video",
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "60s Video",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = {
                                Text(
                                    "Send 1:1 message (no forwarding)...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderColor,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_message_input")
                        )

                        Button(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    onSendMessage(messageInput)
                                    messageInput = ""
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = ObsidianNight
                            ),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Message"
                            )
                        }
                    }
                }
            }
        }
    }

    // Full-Screen Ephemeral Media Viewer Modal (Enforces FLAG_SECURE while open)
    val viewedMsg = messages.find { it.id == activeMediaViewerId }
    if (viewedMsg != null) {
        val remainingSeconds = if (viewedMsg.isKeptForeverInVault) {
            999
        } else if (viewedMsg.expiresAtMillis != null) {
            ((viewedMsg.expiresAtMillis - nowTickerMillis) / 1000L).toInt().coerceAtLeast(0)
        } else {
            60
        }

        AlertDialog(
            onDismissRequest = onCloseMediaViewer,
            containerColor = DeepSlateSurface,
            titleContentColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "FLAG_SECURE Protected Media",
                            tint = EmeraldShield,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewedMsg.mediaTitle ?: "Ephemeral Media",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = ObsidianNight,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, NeonCoral)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (viewedMsg.mediaPreviewStyle == "CYBER_TOKYO") {
                                        R.drawable.img_hero_globe
                                    } else {
                                        R.drawable.img_match_avatar_female
                                    }
                                ),
                                contentDescription = "Ephemeral Media Content",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = ObsidianNight.copy(alpha = 0.82f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, NeonCoral),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (viewedMsg.isKeptForeverInVault) {
                                        "PRO VAULTED • PERMANENT"
                                    } else {
                                        "EXPIRES IN ${remainingSeconds}S"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (viewedMsg.isKeptForeverInVault) AmberCrown else NeonCoral,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "FLAG_SECURE is active on this media window: screenshots, saving to device gallery, and forwarding are strictly blocked.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldShield
                    )

                    // Roll Call Pro Keep Forever Button
                    Button(
                        onClick = { onToggleKeepForever(viewedMsg.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewedMsg.isKeptForeverInVault) {
                                EmeraldShield
                            } else {
                                AmberCrown
                            },
                            contentColor = ObsidianNight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("viewer_keep_forever_button")
                    ) {
                        Icon(
                            imageVector = if (viewedMsg.isKeptForeverInVault) {
                                Icons.Default.BookmarkAdded
                            } else {
                                Icons.Default.BookmarkBorder
                            },
                            contentDescription = "Keep Media Forever in Pro Vault"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (viewedMsg.isKeptForeverInVault) {
                                "Saved in Pro Vault (Tap to Remove)"
                            } else if (userProfile.isPremiumPro) {
                                "Keep Media Forever in Pro Vault"
                            } else {
                                "Keep Forever (Requires Roll Call Pro $19.99)"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onCloseMediaViewer) {
                    Text("Close Viewer", color = ElectricCyan)
                }
            }
        )
    }

    if (showReportModal && currentPeer != null) {
        val reasons = listOf(
            "Non-consensual or Explicit Media",
            "Harassment / Threats in 1:1 Chat",
            "Suspected Underage User / CSAM Alert",
            "Spam / Scam Links"
        )
        AlertDialog(
            onDismissRequest = { showReportModal = false },
            containerColor = ElevatedCardSurface,
            titleContentColor = Color.White,
            textContentColor = TextSecondaryMuted,
            title = { Text("Report & Block ${currentPeer.displayName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Blocking immediately deletes this 1:1 chat and logs a moderation report:")
                    reasons.forEach { r ->
                        OutlinedButton(
                            onClick = {
                                showReportModal = false
                                onReportAndBlockPeer(currentPeer, r)
                            },
                            border = BorderStroke(1.dp, CrimsonPanic),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(r, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReportModal = false }) {
                    Text("Cancel", color = ElectricCyan)
                }
            }
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessageEntity,
    nowTickerMillis: Long,
    readReceiptsEnabled: Boolean,
    autoTranslateEnabled: Boolean,
    isProUser: Boolean,
    onOpenEphemeralMedia: () -> Unit,
    onToggleKeepForever: () -> Unit,
    onBlockedSaveOrForward: (String) -> Unit
) {
    val isMedia = message.mediaType != MediaAttachmentType.NONE.name
    val remainingSeconds = if (message.isKeptForeverInVault) {
        60
    } else if (message.expiresAtMillis != null) {
        ((message.expiresAtMillis - nowTickerMillis) / 1000L).toInt().coerceAtLeast(0)
    } else {
        60
    }
    val effectivelyExpired = message.isExpired ||
        (!message.isKeptForeverInVault && message.expiresAtMillis != null && remainingSeconds <= 0)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.senderIsMe) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.senderIsMe) 18.dp else 4.dp,
                bottomEnd = if (message.senderIsMe) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.senderIsMe) {
                    ElectricCyan.copy(alpha = 0.16f)
                } else {
                    ElevatedCardSurface
                }
            ),
            border = BorderStroke(
                1.dp,
                when {
                    message.isKeptForeverInVault -> AmberCrown
                    isMedia && !effectivelyExpired -> NeonCoral
                    message.senderIsMe -> ElectricCyan.copy(alpha = 0.45f)
                    else -> GlassBorderColor
                }
            ),
            modifier = Modifier.widthIn(max = 330.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isMedia) {
                    if (effectivelyExpired) {
                        // Purged / Auto-Deleted 60s Ephemeral Media State
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Expired Media",
                                tint = TextSecondaryMuted
                            )
                            Column {
                                Text(
                                    text = "Ephemeral ${message.mediaType} Expired (60s TTL)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextSecondaryMuted
                                )
                                Text(
                                    text = "Cryptographically purged from storage.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        // Active Ephemeral Media Card with Countdown Ring
                        Surface(
                            color = ObsidianNight,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                if (message.isKeptForeverInVault) AmberCrown else NeonCoral
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenEphemeralMedia() }
                                .testTag("ephemeral_media_card_${message.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                progress = {
                                                    if (message.isKeptForeverInVault) 1f
                                                    else (remainingSeconds / 60f).coerceIn(0f, 1f)
                                                },
                                                color = if (message.isKeptForeverInVault) {
                                                    AmberCrown
                                                } else {
                                                    NeonCoral
                                                },
                                                trackColor = DeepSlateSurface,
                                                strokeWidth = 3.dp,
                                                modifier = Modifier.size(40.dp)
                                            )
                                            Text(
                                                text = if (message.isKeptForeverInVault) {
                                                    "∞"
                                                } else if (message.firstViewedAtMillis == null) {
                                                    "60s"
                                                } else {
                                                    "${remainingSeconds}s"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = message.mediaTitle ?: "60s Ephemeral Media",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = Color.White
                                            )
                                            Text(
                                                text = when {
                                                    message.isKeptForeverInVault ->
                                                        "Saved Forever in Roll Call Pro Vault"
                                                    message.firstViewedAtMillis == null ->
                                                        "Tap to open • Starts 60s self-destruct timer"
                                                    else ->
                                                        "Auto-deletes in ${remainingSeconds}s"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (message.isKeptForeverInVault) {
                                                    AmberCrown
                                                } else {
                                                    NeonCoral
                                                }
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = Icons.Default.RemoveRedEye,
                                        contentDescription = "Tap to View Ephemeral Media",
                                        tint = ElectricCyan
                                    )
                                }

                                // Strict Anti-Save / Anti-Forward + Pro Keep Forever Action Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = onToggleKeepForever,
                                        modifier = Modifier.testTag("keep_forever_btn_${message.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (message.isKeptForeverInVault) {
                                                Icons.Default.BookmarkAdded
                                            } else {
                                                Icons.Default.BookmarkBorder
                                            },
                                            contentDescription = "Keep Forever in Vault",
                                            tint = AmberCrown,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (message.isKeptForeverInVault) {
                                                "Kept in Pro Vault"
                                            } else if (isProUser) {
                                                "Keep Forever (Pro)"
                                            } else {
                                                "Keep (Pro $19.99)"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmberCrown
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                onBlockedSaveOrForward("Save Media to Device Gallery")
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("blocked_save_btn_${message.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FileDownloadOff,
                                                contentDescription = "Saving to Gallery Disabled",
                                                tint = TextSecondaryMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                onBlockedSaveOrForward("Forward Media to Another User")
                                            },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("blocked_forward_btn_${message.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ForwardToInbox,
                                                contentDescription = "Forwarding Disabled",
                                                tint = TextSecondaryMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = message.textContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                AnimatedVisibility(
                    visible = autoTranslateEnabled && !message.translatedContent.isNullOrBlank()
                ) {
                    Surface(
                        color = ObsidianNight.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = message.translatedContent ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricCyan,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (message.senderIsMe) "You" else "Peer",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                    if (message.senderIsMe && readReceiptsEnabled) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read Receipt",
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Read",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }
    }
}
