package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GenderOption(val label: String) {
    MALE("Male"),
    FEMALE("Female"),
    ANY("Any")
}

enum class MediaAttachmentType {
    NONE,
    IMAGE,
    VIDEO
}

/**
 * Singleton-row user profile entity (id = 1).
 * Enforces 18+ age verification, gender preferences, active time point accrual (1 hr = 100 pts),
 * and Roll Call Pro ($19.99/mo) subscription state.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String = "NovaRider",
    val birthYear: Int = 2001,
    val isAge18Verified: Boolean = false,
    val isBiometricIdVerified: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val myGender: String = GenderOption.MALE.name,
    val preferredMatchGender: String = GenderOption.ANY.name,
    val interestsCsv: String = "Music,Late Night Talks,Gaming,Travel",
    val activeSecondsAccrued: Long = 5220L, // Starts at 1h 27m (145 pts) so user is close to 200-pt gate
    val earnedPoints: Int = 145,
    val purchasedPoints: Int = 0,
    val dailyStreakDays: Int = 4,
    val streakClaimedToday: Boolean = false,
    val isPremiumPro: Boolean = false,
    val proSubscriptionType: String = "NONE", // NONE, MONTHLY_19_99, ANNUAL_149_99
    val readReceiptsEnabled: Boolean = true,
    val autoTranslateEnabled: Boolean = true
) {
    val totalPoints: Int
        get() = earnedPoints + purchasedPoints

    val isLeaderboardUnlocked: Boolean
        get() = totalPoints >= 200
}

/**
 * 1:1 Matched Peer Entity.
 * Strictly 1-on-1 user profiles worldwide — no group or channel entities exist.
 */
@Entity(tableName = "matched_peers")
data class MatchedPeerEntity(
    @PrimaryKey val peerId: String,
    val displayName: String,
    val age: Int,
    val gender: String, // MALE or FEMALE
    val countryName: String,
    val countryFlag: String,
    val nativeLanguage: String,
    val interestsCsv: String,
    val isIdVerified: Boolean,
    val avatarKey: String, // "female" or "male"
    val statusBio: String,
    val isBlocked: Boolean = false,
    val lastMatchedAtMillis: Long = System.currentTimeMillis()
)

/**
 * Strictly 1-on-1 Chat & Ephemeral Media Message Entity.
 * Enforces 1:1 conversations via single peerId (no group/channel IDs).
 * Media expires 60 seconds after being opened/viewed unless vaulted by a Roll Call Pro user.
 */
@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val peerId: String,
    val senderIsMe: Boolean,
    val textContent: String,
    val translatedContent: String? = null,
    val mediaType: String = MediaAttachmentType.NONE.name,
    val mediaTitle: String? = null,
    val mediaPreviewStyle: String = "NEON_SUNSET",
    val sentAtMillis: Long = System.currentTimeMillis(),
    val firstViewedAtMillis: Long? = null,
    val expiresAtMillis: Long? = null,
    val isExpired: Boolean = false,
    val isKeptForeverInVault: Boolean = false,
    val isReadByPeer: Boolean = true
)

/**
 * Weekly Global Leaderboard Entry.
 * Minimum 200 points required for user to enter and compete.
 */
@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntryEntity(
    @PrimaryKey val entryId: String,
    val username: String,
    val countryFlag: String,
    val gender: String,
    val points: Int,
    val activeHours: Double,
    val streakDays: Int,
    val isVerified: Boolean,
    val isProSubscriber: Boolean,
    val isCurrentUser: Boolean = false
)

/**
 * Trust & Safety Moderation / Report Audit Record.
 * Tracks in-call Panic Button triggers, PhotoDNA/CSAM hash checks, and user blocks.
 */
@Entity(tableName = "safety_reports")
data class SafetyReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val peerId: String,
    val peerName: String,
    val reasonCategory: String,
    val triggerSource: String, // PANIC_BUTTON, IN_CALL_REPORT, IN_CHAT_REPORT, AI_VISION_SHIELD
    val photoDnaHashSample: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val ncmecEscalated: Boolean = false
)

/**
 * Points & Google Play Billing Ledger Entry.
 */
@Entity(tableName = "point_transactions")
data class PointTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val pointsDelta: Int,
    val priceUsd: String,
    val transactionType: String, // TIME_ACCRUAL, STREAK_BONUS, PLAY_BILLING_PACK, PRO_TIER
    val timestampMillis: Long = System.currentTimeMillis()
)
