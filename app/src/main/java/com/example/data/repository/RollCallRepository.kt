package com.example.data.repository

import com.example.data.local.ChatMessageEntity
import com.example.data.local.GenderOption
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.MatchedPeerEntity
import com.example.data.local.MediaAttachmentType
import com.example.data.local.PointTransactionEntity
import com.example.data.local.RollCallDao
import com.example.data.local.SafetyReportEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class RollCallRepository(private val dao: RollCallDao) {

    val userProfileFlow: Flow<UserProfileEntity?> = dao.observeUserProfile()
    val activePeersFlow: Flow<List<MatchedPeerEntity>> = dao.observeActivePeers()
    val blockedPeersFlow: Flow<List<MatchedPeerEntity>> = dao.observeBlockedPeers()
    val vaultedMediaFlow: Flow<List<ChatMessageEntity>> = dao.observeVaultedMedia()
    val leaderboardFlow: Flow<List<LeaderboardEntryEntity>> = dao.observeLeaderboard()
    val safetyReportsFlow: Flow<List<SafetyReportEntity>> = dao.observeSafetyReports()
    val pointTransactionsFlow: Flow<List<PointTransactionEntity>> = dao.observePointTransactions()

    fun observeMessagesForPeer(peerId: String): Flow<List<ChatMessageEntity>> =
        dao.observeMessagesForPeer(peerId)

    suspend fun ensureSeedData() {
        val existingProfile = dao.getUserProfileOnce()
        if (existingProfile == null) {
            val initialProfile = UserProfileEntity(
                id = 1,
                displayName = "NovaRider",
                birthYear = 2000,
                isAge18Verified = false,
                isBiometricIdVerified = false,
                onboardingCompleted = false,
                myGender = GenderOption.MALE.name,
                preferredMatchGender = GenderOption.ANY.name,
                interestsCsv = "Music,Late Night Talks,Gaming,Cyberpunk",
                activeSecondsAccrued = 5400L, // 1.5 hours accrued = 150 points
                earnedPoints = 150,
                purchasedPoints = 0,
                dailyStreakDays = 4,
                streakClaimedToday = false,
                isPremiumPro = false,
                proSubscriptionType = "NONE",
                readReceiptsEnabled = true,
                autoTranslateEnabled = true
            )
            dao.upsertUserProfile(initialProfile)

            val now = System.currentTimeMillis()
            val seedPeers = listOf(
                MatchedPeerEntity(
                    peerId = "peer_sora_jp",
                    displayName = "Sora Takahashi",
                    age = 24,
                    gender = GenderOption.FEMALE.name,
                    countryName = "Tokyo, Japan",
                    countryFlag = "🇯🇵",
                    nativeLanguage = "Japanese",
                    interestsCsv = "Synthwave,Night Photography,Anime,Coffee",
                    isIdVerified = true,
                    avatarKey = "female",
                    statusBio = "Exploring Shibuya neon nights • Down for 1:1 voice or video!",
                    lastMatchedAtMillis = now - 120_000L
                ),
                MatchedPeerEntity(
                    peerId = "peer_mateo_es",
                    displayName = "Mateo Navarro",
                    age = 26,
                    gender = GenderOption.MALE.name,
                    countryName = "Barcelona, Spain",
                    countryFlag = "🇪🇸",
                    nativeLanguage = "Spanish",
                    interestsCsv = "Indie Rock,Architecture,Surfing,Late Night Talks",
                    isIdVerified = true,
                    avatarKey = "male",
                    statusBio = "Acoustic guitar & global conversations",
                    lastMatchedAtMillis = now - 600_000L
                ),
                MatchedPeerEntity(
                    peerId = "peer_elena_br",
                    displayName = "Elena Costa",
                    age = 23,
                    gender = GenderOption.FEMALE.name,
                    countryName = "São Paulo, Brazil",
                    countryFlag = "🇧🇷",
                    nativeLanguage = "Portuguese",
                    interestsCsv = "Electronic Music,Digital Art,Travel,Gaming",
                    isIdVerified = true,
                    avatarKey = "female",
                    statusBio = "Practicing English & meeting cool people worldwide",
                    lastMatchedAtMillis = now - 1_800_000L
                ),
                MatchedPeerEntity(
                    peerId = "peer_liam_ca",
                    displayName = "Liam Vance",
                    age = 27,
                    gender = GenderOption.MALE.name,
                    countryName = "Toronto, Canada",
                    countryFlag = "🇨🇦",
                    nativeLanguage = "English",
                    interestsCsv = "Film Scoring,Sci-Fi,Fitness,Snowboarding",
                    isIdVerified = false,
                    avatarKey = "male",
                    statusBio = "Late night studio session break",
                    lastMatchedAtMillis = now - 3_600_000L
                ),
                MatchedPeerEntity(
                    peerId = "peer_chloe_fr",
                    displayName = "Chloé Laurent",
                    age = 25,
                    gender = GenderOption.FEMALE.name,
                    countryName = "Lyon, France",
                    countryFlag = "🇫🇷",
                    nativeLanguage = "French",
                    interestsCsv = "Cinema,Vinyl Records,Street Art,Travel",
                    isIdVerified = true,
                    avatarKey = "female",
                    statusBio = "Vinyl collector • Let's chat in FR or EN!",
                    lastMatchedAtMillis = now - 7_200_000L
                )
            )
            dao.insertPeers(seedPeers)

            // Seed initial 1:1 conversation with Sora including an unopened 60s ephemeral photo
            val seedMessages = listOf(
                ChatMessageEntity(
                    peerId = "peer_sora_jp",
                    senderIsMe = false,
                    textContent = "こんばんは! Great matching with you on Roll Call from Tokyo!",
                    translatedContent = "[Translated from Japanese] Good evening! Great matching with you on Roll Call from Tokyo!",
                    sentAtMillis = now - 300_000L
                ),
                ChatMessageEntity(
                    peerId = "peer_sora_jp",
                    senderIsMe = true,
                    textContent = "Hey Sora! Awesome connection quality. How is Tokyo tonight?",
                    sentAtMillis = now - 240_000L
                ),
                ChatMessageEntity(
                    peerId = "peer_sora_jp",
                    senderIsMe = false,
                    textContent = "Sent an ephemeral snapshot of the Shibuya neon skyline (60s auto-delete on view).",
                    mediaType = MediaAttachmentType.IMAGE.name,
                    mediaTitle = "Shibuya_Rain_Neon_View.jpg",
                    mediaPreviewStyle = "CYBER_TOKYO",
                    sentAtMillis = now - 90_000L,
                    firstViewedAtMillis = null,
                    expiresAtMillis = null,
                    isExpired = false,
                    isKeptForeverInVault = false
                ),
                ChatMessageEntity(
                    peerId = "peer_mateo_es",
                    senderIsMe = false,
                    textContent = "¡Hola! Ready for another jam session call whenever you're free.",
                    translatedContent = "[Translated from Spanish] Hello! Ready for another jam session call whenever you're free.",
                    sentAtMillis = now - 500_000L
                )
            )
            dao.insertMessages(seedMessages)

            // Seed point transaction history
            dao.insertPointTransaction(
                PointTransactionEntity(
                    title = "Active Time Accrual (1.5 hrs @ 100 pts/hr)",
                    pointsDelta = 150,
                    priceUsd = "FREE",
                    transactionType = "TIME_ACCRUAL",
                    timestampMillis = now - 1800_000L
                )
            )
        }

        if (dao.getLeaderboardCount() == 0) {
            val profile = dao.getUserProfileOnce()
            val userPts = profile?.totalPoints ?: 150
            val entries = listOf(
                LeaderboardEntryEntity(
                    entryId = "lb_1",
                    username = "Kaito_Vortex",
                    countryFlag = "🇯🇵",
                    gender = "MALE",
                    points = 1840,
                    activeHours = 14.4,
                    streakDays = 21,
                    isVerified = true,
                    isProSubscriber = true
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_2",
                    username = "ValkyrieLuna",
                    countryFlag = "🇸🇪",
                    gender = "FEMALE",
                    points = 1620,
                    activeHours = 12.2,
                    streakDays = 18,
                    isVerified = true,
                    isProSubscriber = true
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_3",
                    username = "ZephyrPulse",
                    countryFlag = "🇧🇷",
                    gender = "MALE",
                    points = 1390,
                    activeHours = 11.9,
                    streakDays = 14,
                    isVerified = true,
                    isProSubscriber = true
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_4",
                    username = "AriaSolstice",
                    countryFlag = "🇰🇷",
                    gender = "FEMALE",
                    points = 980,
                    activeHours = 9.8,
                    streakDays = 11,
                    isVerified = true,
                    isProSubscriber = false
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_5",
                    username = "Dante_Milano",
                    countryFlag = "🇮🇹",
                    gender = "MALE",
                    points = 740,
                    activeHours = 7.4,
                    streakDays = 9,
                    isVerified = true,
                    isProSubscriber = true
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_6",
                    username = "Nyx_Aethel",
                    countryFlag = "🇩🇪",
                    gender = "FEMALE",
                    points = 510,
                    activeHours = 5.1,
                    streakDays = 7,
                    isVerified = false,
                    isProSubscriber = false
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_7",
                    username = "OrionDrift",
                    countryFlag = "🇨🇦",
                    gender = "MALE",
                    points = 320,
                    activeHours = 3.2,
                    streakDays = 5,
                    isVerified = true,
                    isProSubscriber = false
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_8",
                    username = "Sora_Tokyo",
                    countryFlag = "🇯🇵",
                    gender = "FEMALE",
                    points = 240,
                    activeHours = 2.4,
                    streakDays = 6,
                    isVerified = true,
                    isProSubscriber = false
                ),
                LeaderboardEntryEntity(
                    entryId = "lb_me",
                    username = profile?.displayName ?: "NovaRider",
                    countryFlag = "🌐",
                    gender = profile?.myGender ?: "MALE",
                    points = userPts,
                    activeHours = (profile?.activeSecondsAccrued ?: 5400L) / 3600.0,
                    streakDays = profile?.dailyStreakDays ?: 4,
                    isVerified = profile?.isBiometricIdVerified ?: true,
                    isProSubscriber = profile?.isPremiumPro ?: false,
                    isCurrentUser = true
                )
            )
            dao.insertLeaderboardEntries(entries)
        }
    }

    suspend fun completeOnboarding(
        displayName: String,
        birthYear: Int,
        myGender: GenderOption,
        preferredGender: GenderOption,
        interestsCsv: String,
        isIdVerified: Boolean
    ) {
        val current = dao.getUserProfileOnce() ?: UserProfileEntity()
        val updated = current.copy(
            displayName = displayName.ifBlank { "NovaRider" },
            birthYear = birthYear,
            isAge18Verified = true,
            isBiometricIdVerified = isIdVerified,
            onboardingCompleted = true,
            myGender = myGender.name,
            preferredMatchGender = preferredGender.name,
            interestsCsv = interestsCsv
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)
    }

    suspend fun updatePreferences(
        myGender: GenderOption? = null,
        preferredGender: GenderOption? = null,
        readReceipts: Boolean? = null,
        autoTranslate: Boolean? = null,
        interestsCsv: String? = null
    ) {
        val current = dao.getUserProfileOnce() ?: return
        val updated = current.copy(
            myGender = myGender?.name ?: current.myGender,
            preferredMatchGender = preferredGender?.name ?: current.preferredMatchGender,
            readReceiptsEnabled = readReceipts ?: current.readReceiptsEnabled,
            autoTranslateEnabled = autoTranslate ?: current.autoTranslateEnabled,
            interestsCsv = interestsCsv ?: current.interestsCsv
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)
    }

    /**
     * Accrues active in-app seconds.
     * Formula: 1 hour (3,600 seconds) = 100 points => 1 point every 36 seconds.
     */
    suspend fun accrueActiveTimeSeconds(deltaSeconds: Long, logTransaction: Boolean = false) {
        val current = dao.getUserProfileOnce() ?: return
        val newTotalSeconds = current.activeSecondsAccrued + deltaSeconds
        val recalculatedEarnedPoints = ((newTotalSeconds * 100L) / 3600L).toInt() +
            (if (current.streakClaimedToday) 25 else 0)
        val deltaPoints = (recalculatedEarnedPoints - current.earnedPoints).coerceAtLeast(0)
        val updated = current.copy(
            activeSecondsAccrued = newTotalSeconds,
            earnedPoints = recalculatedEarnedPoints
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)

        if (logTransaction && deltaPoints > 0) {
            dao.insertPointTransaction(
                PointTransactionEntity(
                    title = "Active App Time (+${deltaSeconds / 60} mins @ 100 pts/hr)",
                    pointsDelta = deltaPoints,
                    priceUsd = "FREE",
                    transactionType = "TIME_ACCRUAL"
                )
            )
        }
    }

    suspend fun claimDailyStreakBonus(): Boolean {
        val current = dao.getUserProfileOnce() ?: return false
        if (current.streakClaimedToday) return false
        val bonusPts = 25
        val updated = current.copy(
            dailyStreakDays = current.dailyStreakDays + 1,
            streakClaimedToday = true,
            earnedPoints = current.earnedPoints + bonusPts
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)
        dao.insertPointTransaction(
            PointTransactionEntity(
                title = "Day ${updated.dailyStreakDays} Streak Bonus",
                pointsDelta = bonusPts,
                priceUsd = "FREE",
                transactionType = "STREAK_BONUS"
            )
        )
        return true
    }

    suspend fun activateRollCallPro(planCode: String, priceLabel: String) {
        val current = dao.getUserProfileOnce() ?: return
        val welcomeBonusPts = 100
        val updated = current.copy(
            isPremiumPro = true,
            proSubscriptionType = planCode,
            purchasedPoints = current.purchasedPoints + welcomeBonusPts
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)
        dao.insertPointTransaction(
            PointTransactionEntity(
                title = "Roll Call Pro ($planCode) + 100 Welcome Points",
                pointsDelta = welcomeBonusPts,
                priceUsd = priceLabel,
                transactionType = "PRO_TIER"
            )
        )
    }

    suspend fun purchasePointPack(packTitle: String, points: Int, priceUsd: String) {
        val current = dao.getUserProfileOnce() ?: return
        val updated = current.copy(
            purchasedPoints = current.purchasedPoints + points
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)
        dao.insertPointTransaction(
            PointTransactionEntity(
                title = packTitle,
                pointsDelta = points,
                priceUsd = priceUsd,
                transactionType = "PLAY_BILLING_PACK"
            )
        )
    }

    suspend fun grantRewardedAdBonusPoints(points: Int, adUnitId: String) {
        val current = dao.getUserProfileOnce() ?: return
        if (current.isPremiumPro) return // Premium users have no ads
        val updated = current.copy(
            earnedPoints = current.earnedPoints + points
        )
        dao.upsertUserProfile(updated)
        syncUserToLeaderboard(updated)
        dao.insertPointTransaction(
            PointTransactionEntity(
                title = "Rewarded Video Ad ($adUnitId)",
                pointsDelta = points,
                priceUsd = "AD_REWARD",
                transactionType = "REWARDED_AD"
            )
        )
    }

    private suspend fun syncUserToLeaderboard(profile: UserProfileEntity) {
        dao.insertLeaderboardEntries(
            listOf(
                LeaderboardEntryEntity(
                    entryId = "lb_me",
                    username = profile.displayName,
                    countryFlag = "🌐",
                    gender = profile.myGender,
                    points = profile.totalPoints,
                    activeHours = profile.activeSecondsAccrued / 3600.0,
                    streakDays = profile.dailyStreakDays,
                    isVerified = profile.isBiometricIdVerified,
                    isProSubscriber = profile.isPremiumPro,
                    isCurrentUser = true
                )
            )
        )
    }

    suspend fun findMatchByGenderPreference(
        preferredGender: GenderOption,
        excludePeerId: String? = null
    ): MatchedPeerEntity? {
        val available = dao.getAvailablePeersOnce()
        val genderFiltered = available.filter { peer ->
            preferredGender == GenderOption.ANY || peer.gender == preferredGender.name
        }
        val pool = genderFiltered.filter { it.peerId != excludePeerId }.ifEmpty { genderFiltered }
        return pool.randomOrNull()
    }

    suspend fun send1on1TextMessage(peerId: String, text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        dao.insertMessage(
            ChatMessageEntity(
                peerId = peerId,
                senderIsMe = true,
                textContent = clean,
                sentAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun send1on1EphemeralMedia(
        peerId: String,
        senderIsMe: Boolean,
        mediaType: MediaAttachmentType,
        mediaTitle: String,
        previewStyle: String
    ): Long {
        val now = System.currentTimeMillis()
        // If sent by me, recipient opens it right away so 60s countdown starts immediately;
        // if received from peer, 60s countdown starts when the user taps "Open Ephemeral Media".
        val firstViewed = if (senderIsMe) now else null
        val expiresAt = if (senderIsMe) now + 60_000L else null
        return dao.insertMessage(
            ChatMessageEntity(
                peerId = peerId,
                senderIsMe = senderIsMe,
                textContent = if (mediaType == MediaAttachmentType.IMAGE) {
                    "Shared a 60s ephemeral photo ($mediaTitle)"
                } else {
                    "Shared a 60s ephemeral video clip ($mediaTitle)"
                },
                mediaType = mediaType.name,
                mediaTitle = mediaTitle,
                mediaPreviewStyle = previewStyle,
                sentAtMillis = now,
                firstViewedAtMillis = firstViewed,
                expiresAtMillis = expiresAt,
                isExpired = false,
                isKeptForeverInVault = false
            )
        )
    }

    /**
     * Triggers the 60-second ephemeral countdown on first view.
     */
    suspend fun openEphemeralMedia(messageId: Long) {
        val msg = dao.getMessageById(messageId) ?: return
        if (msg.isExpired) return
        if (msg.firstViewedAtMillis == null) {
            val now = System.currentTimeMillis()
            dao.updateMessage(
                msg.copy(
                    firstViewedAtMillis = now,
                    expiresAtMillis = now + 60_000L
                )
            )
        }
    }

    /**
     * Roll Call Pro ($19.99) feature: Keep active sent OR received media forever in encrypted Vault.
     * Cannot retroactively restore already-expired media.
     */
    suspend fun toggleKeepMediaForever(messageId: Long): Boolean {
        val profile = dao.getUserProfileOnce() ?: return false
        if (!profile.isPremiumPro) return false
        val msg = dao.getMessageById(messageId) ?: return false
        if (msg.isExpired) return false
        val newKeptState = !msg.isKeptForeverInVault
        dao.updateMessage(
            msg.copy(
                isKeptForeverInVault = newKeptState
            )
        )
        return true
    }

    suspend fun sweepExpiredMediaNow() {
        dao.sweepExpiredMedia(System.currentTimeMillis())
    }

    suspend fun reportAndBlockPeer(
        peerId: String,
        peerName: String,
        reason: String,
        triggerSource: String
    ) {
        dao.blockPeerById(peerId)
        dao.deleteMessagesForPeer(peerId)
        val hashSample = "PDNA-" + UUID.randomUUID().toString().take(12).uppercase()
        dao.insertSafetyReport(
            SafetyReportEntity(
                peerId = peerId,
                peerName = peerName,
                reasonCategory = reason,
                triggerSource = triggerSource,
                photoDnaHashSample = hashSample,
                ncmecEscalated = reason.contains("CSAM", ignoreCase = true) ||
                    reason.contains("Minor", ignoreCase = true)
            )
        )
    }

    suspend fun unblockPeer(peerId: String) {
        dao.unblockPeerById(peerId)
    }
}
