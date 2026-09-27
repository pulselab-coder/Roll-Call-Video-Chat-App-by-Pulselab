package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatMessageEntity
import com.example.data.local.GenderOption
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.MatchedPeerEntity
import com.example.data.local.MediaAttachmentType
import com.example.data.local.PointTransactionEntity
import com.example.data.local.RollCallDatabase
import com.example.data.local.SafetyReportEntity
import com.example.data.local.UserProfileEntity
import com.example.data.repository.RollCallRepository
import com.example.ui.ads.AdMobController
import com.example.ui.ads.RollCallAdIds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val ROUTE_HOME = "home_radar"
const val ROUTE_CHAT = "one_on_one_chat"
const val ROUTE_LEADERBOARD = "leaderboard_rankings"
const val ROUTE_STORE = "points_and_pro_store"
const val ROUTE_SAFETY_SPEC = "trust_safety_and_spec"

enum class CallMode {
    VIDEO,
    VOICE_ONLY
}

enum class MatchmakingStatus {
    IDLE,
    SEARCHING_QUEUE,
    IN_ACTIVE_CALL,
    RATE_LIMITED_COOLDOWN
}

data class ActiveCallUiState(
    val status: MatchmakingStatus = MatchmakingStatus.IDLE,
    val callMode: CallMode = CallMode.VIDEO,
    val matchedPeer: MatchedPeerEntity? = null,
    val isFrontCamera: Boolean = true,
    val isMicMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val callDurationSeconds: Int = 0,
    val aiModerationStatus: String = "PhotoDNA + Vision AI Shield: CLEAR",
    val rateLimitRemainingSeconds: Int = 0,
    val recentSkipTimestamps: List<Long> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class RollCallViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RollCallRepository = RollCallRepository(
        RollCallDatabase.getInstance(application).rollCallDao()
    )

    private val _currentRoute = MutableStateFlow(ROUTE_HOME)
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    private val _selectedChatPeerId = MutableStateFlow("peer_sora_jp")
    val selectedChatPeerId: StateFlow<String> = _selectedChatPeerId.asStateFlow()

    private val _callState = MutableStateFlow(ActiveCallUiState())
    val callState: StateFlow<ActiveCallUiState> = _callState.asStateFlow()

    private val _isPeerTyping = MutableStateFlow(false)
    val isPeerTyping: StateFlow<Boolean> = _isPeerTyping.asStateFlow()

    private val _activeMediaViewerId = MutableStateFlow<Long?>(null)
    val activeMediaViewerId: StateFlow<Long?> = _activeMediaViewerId.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _nowTickerMillis = MutableStateFlow(System.currentTimeMillis())
    val nowTickerMillis: StateFlow<Long> = _nowTickerMillis.asStateFlow()

    // AdMob Interstitial & Rewarded Ad state (strictly disabled if userProfile.isPremiumPro is true)
    private val _showInterstitialModal = MutableStateFlow(false)
    val showInterstitialModal: StateFlow<Boolean> = _showInterstitialModal.asStateFlow()

    private val _showRewardedModal = MutableStateFlow(false)
    val showRewardedModal: StateFlow<Boolean> = _showRewardedModal.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfileFlow
        .combine(flowOf(Unit)) { profile, _ ->
            profile ?: UserProfileEntity()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileEntity()
        )

    val activePeers: StateFlow<List<MatchedPeerEntity>> = repository.activePeersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val blockedPeers: StateFlow<List<MatchedPeerEntity>> = repository.blockedPeersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentPeerMessages: StateFlow<List<ChatMessageEntity>> = _selectedChatPeerId
        .flatMapLatest { peerId ->
            repository.observeMessagesForPeer(peerId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val vaultedMedia: StateFlow<List<ChatMessageEntity>> = repository.vaultedMediaFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val leaderboard: StateFlow<List<LeaderboardEntryEntity>> = repository.leaderboardFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val safetyReports: StateFlow<List<SafetyReportEntity>> = repository.safetyReportsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pointTransactions: StateFlow<List<PointTransactionEntity>> = repository.pointTransactionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
        }

        // Real-time 1-second loop for:
        // 1) Ephemeral 60-second media expiration sweeps
        // 2) Active in-app time point accrual (1 hour = 100 points)
        // 3) Live call duration & rate-limit countdowns
        viewModelScope.launch {
            var sessionSecondCounter = 0
            while (true) {
                delay(1000L)
                val now = System.currentTimeMillis()
                _nowTickerMillis.value = now
                repository.sweepExpiredMediaNow()

                sessionSecondCounter++
                if (sessionSecondCounter >= 10) {
                    repository.accrueActiveTimeSeconds(10L, logTransaction = false)
                    sessionSecondCounter = 0
                }

                val currentCall = _callState.value
                if (currentCall.status == MatchmakingStatus.IN_ACTIVE_CALL) {
                    _callState.value = currentCall.copy(
                        callDurationSeconds = currentCall.callDurationSeconds + 1
                    )
                } else if (currentCall.status == MatchmakingStatus.RATE_LIMITED_COOLDOWN) {
                    val nextRem = (currentCall.rateLimitRemainingSeconds - 1).coerceAtLeast(0)
                    _callState.value = if (nextRem == 0) {
                        currentCall.copy(
                            status = MatchmakingStatus.IDLE,
                            rateLimitRemainingSeconds = 0
                        )
                    } else {
                        currentCall.copy(rateLimitRemainingSeconds = nextRem)
                    }
                }
            }
        }
    }

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun showBanner(message: String) {
        _statusBannerMessage.value = message
    }

    fun dismissBanner() {
        _statusBannerMessage.value = null
    }

    fun completeOnboarding(
        displayName: String,
        birthYear: Int,
        myGender: GenderOption,
        preferredGender: GenderOption,
        interestsCsv: String,
        isIdVerified: Boolean
    ) {
        viewModelScope.launch {
            repository.completeOnboarding(
                displayName = displayName,
                birthYear = birthYear,
                myGender = myGender,
                preferredGender = preferredGender,
                interestsCsv = interestsCsv,
                isIdVerified = isIdVerified
            )
            showBanner("18+ Verification & Profile Saved • Welcome to Roll Call!")
        }
    }

    fun setPreferredMatchGender(gender: GenderOption) {
        viewModelScope.launch {
            repository.updatePreferences(preferredGender = gender)
        }
    }

    fun setMyGender(gender: GenderOption) {
        viewModelScope.launch {
            repository.updatePreferences(myGender = gender)
        }
    }

    fun toggleReadReceipts(enabled: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences(readReceipts = enabled)
            showBanner(
                if (enabled) "Read Receipts enabled for 1:1 chats"
                else "Read Receipts hidden for privacy"
            )
        }
    }

    fun toggleAutoTranslate(enabled: Boolean) {
        viewModelScope.launch {
            repository.updatePreferences(autoTranslate = enabled)
            showBanner(
                if (enabled) "Cross-language instant translation enabled"
                else "Instant translation disabled"
            )
        }
    }

    /**
     * Simulates 30 minutes of active in-app time (+50 points @ 100 pts/hr)
     * so users can immediately test crossing the 200-point Leaderboard gate.
     */
    fun simulateActiveTimeBoost30Mins() {
        viewModelScope.launch {
            repository.accrueActiveTimeSeconds(1800L, logTransaction = true)
            showBanner("+30m Active Time logged (+50 Points accrued @ 100 pts/hr)!")
        }
    }

    fun claimDailyStreak() {
        viewModelScope.launch {
            val claimed = repository.claimDailyStreakBonus()
            if (claimed) {
                showBanner("Daily Streak Bonus Claimed! +25 Points added.")
            } else {
                showBanner("Today's streak bonus was already claimed.")
            }
        }
    }

    // --- AdMob Interstitial & Rewarded Ad Actions (Disabled when isPremiumPro == true) ---
    fun requestInterstitialAd(context: Context, onAfterAd: () -> Unit = {}) {
        if (userProfile.value.isPremiumPro) {
            AdMobController.clearAdsForPremium()
            onAfterAd()
            return
        }
        AdMobController.showInterstitialAd(
            context = context,
            isPremiumPro = false,
            onShowFallbackDialog = {
                _showInterstitialModal.value = true
                onAfterAd()
            },
            onAdClosed = onAfterAd
        )
    }

    fun dismissInterstitialModal() {
        _showInterstitialModal.value = false
    }

    fun requestRewardedAd(context: Context) {
        if (userProfile.value.isPremiumPro) {
            showBanner("Roll Call Pro Active: All ads are disabled!")
            return
        }
        AdMobController.showRewardedAd(
            context = context,
            isPremiumPro = false,
            onRewardEarned = { pts ->
                completeRewardedAdBonus(pts)
            },
            onShowFallbackRewardedDialog = {
                _showRewardedModal.value = true
            }
        )
    }

    fun completeRewardedAdBonus(points: Int) {
        _showRewardedModal.value = false
        viewModelScope.launch {
            repository.grantRewardedAdBonusPoints(
                points = points,
                adUnitId = RollCallAdIds.REWARDED_AD_UNIT_ID
            )
            showBanner("Rewarded Ad Completed! +$points Bonus Points credited.")
        }
    }

    fun dismissRewardedModal() {
        _showRewardedModal.value = false
    }

    /**
     * Starts global matchmaking filtered by selected gender preference,
     * with anti-bot rate-limiting on rapid re-queuing.
     */
    fun startMatchmaking(callMode: CallMode, excludePeerId: String? = null) {
        val now = System.currentTimeMillis()
        val current = _callState.value
        if (current.status == MatchmakingStatus.RATE_LIMITED_COOLDOWN) {
            showBanner("Rate limit active: Please wait ${current.rateLimitRemainingSeconds}s before re-queuing.")
            return
        }

        // Prune skip timestamps older than 15 seconds
        val recentSkips = current.recentSkipTimestamps.filter { now - it < 15_000L }
        if (recentSkips.size >= 4) {
            _callState.value = current.copy(
                status = MatchmakingStatus.RATE_LIMITED_COOLDOWN,
                rateLimitRemainingSeconds = 10,
                matchedPeer = null,
                recentSkipTimestamps = emptyList()
            )
            showBanner("Anti-bot rate limit triggered (too many rapid skips). Cooldown: 10s.")
            return
        }

        viewModelScope.launch {
            _callState.value = current.copy(
                status = MatchmakingStatus.SEARCHING_QUEUE,
                callMode = callMode,
                callDurationSeconds = 0,
                recentSkipTimestamps = recentSkips
            )
            delay(900L)
            val pref = runCatching {
                GenderOption.valueOf(userProfile.value.preferredMatchGender)
            }.getOrDefault(GenderOption.ANY)

            val matched = repository.findMatchByGenderPreference(
                preferredGender = pref,
                excludePeerId = excludePeerId
            )
            if (matched != null) {
                _selectedChatPeerId.value = matched.peerId
                _callState.value = _callState.value.copy(
                    status = MatchmakingStatus.IN_ACTIVE_CALL,
                    matchedPeer = matched,
                    callDurationSeconds = 0,
                    aiModerationStatus = "PhotoDNA + Cloud Vision AI: SAFE (0 Violations)"
                )
            } else {
                _callState.value = _callState.value.copy(status = MatchmakingStatus.IDLE)
                showBanner("No unblocked peers match filter (${pref.label}). Unblock peers in Safety tab or select 'Any'.")
            }
        }
    }

    fun skipToNextMatch(context: Context) {
        val current = _callState.value
        val now = System.currentTimeMillis()
        val updatedSkips = current.recentSkipTimestamps + now
        _callState.value = current.copy(recentSkipTimestamps = updatedSkips)
        // Show Interstitial Ad on match skip for Free-tier users (never for Premium users)
        requestInterstitialAd(context) {
            startMatchmaking(
                callMode = current.callMode,
                excludePeerId = current.matchedPeer?.peerId
            )
        }
    }

    fun endCall(context: Context? = null) {
        val hadActivePeer = _callState.value.matchedPeer != null
        _callState.value = _callState.value.copy(
            status = MatchmakingStatus.IDLE,
            matchedPeer = null,
            callDurationSeconds = 0
        )
        if (hadActivePeer && context != null && !userProfile.value.isPremiumPro) {
            requestInterstitialAd(context)
        }
    }

    fun toggleCameraLens() {
        val current = _callState.value
        _callState.value = current.copy(isFrontCamera = !current.isFrontCamera)
    }

    fun toggleMicMute() {
        val current = _callState.value
        _callState.value = current.copy(isMicMuted = !current.isMicMuted)
    }

    fun switchCallModeInSession() {
        val current = _callState.value
        val nextMode = if (current.callMode == CallMode.VIDEO) CallMode.VOICE_ONLY else CallMode.VIDEO
        _callState.value = current.copy(callMode = nextMode)
    }

    /**
     * One-tap Panic / Safety Button:
     * Immediately terminates the call, blocks the matched user, purges their chat history,
     * and logs a high-priority moderation + PhotoDNA/NCMEC audit report.
     */
    fun triggerPanicButton() {
        val peer = _callState.value.matchedPeer ?: return
        endCall(context = null) // Never show ad on emergency Panic Button
        viewModelScope.launch {
            repository.reportAndBlockPeer(
                peerId = peer.peerId,
                peerName = peer.displayName,
                reason = "EMERGENCY PANIC BUTTON: Instant Call Termination & Safety Escalation",
                triggerSource = "PANIC_BUTTON"
            )
            val remaining = activePeers.value.firstOrNull { it.peerId != peer.peerId }
            if (remaining != null) {
                _selectedChatPeerId.value = remaining.peerId
            }
            showBanner("PANIC TRIGGERED: Call ended, ${peer.displayName} blocked, and Safety Team alerted.")
        }
    }

    fun reportAndBlockPeer(peer: MatchedPeerEntity, reason: String, source: String) {
        if (_callState.value.matchedPeer?.peerId == peer.peerId) {
            endCall(context = null)
        }
        viewModelScope.launch {
            repository.reportAndBlockPeer(
                peerId = peer.peerId,
                peerName = peer.displayName,
                reason = reason,
                triggerSource = source
            )
            val remaining = activePeers.value.firstOrNull { it.peerId != peer.peerId }
            if (remaining != null) {
                _selectedChatPeerId.value = remaining.peerId
            }
            showBanner("Reported & blocked ${peer.displayName}. Case logged to Moderation Queue.")
        }
    }

    fun unblockPeer(peerId: String) {
        viewModelScope.launch {
            repository.unblockPeer(peerId)
            showBanner("User unblocked and restored to matchmaking pool.")
        }
    }

    fun selectChatPeer(peerId: String) {
        _selectedChatPeerId.value = peerId
    }

    fun sendTextMessage(text: String) {
        val peerId = _selectedChatPeerId.value
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.send1on1TextMessage(peerId, text)
            simulatePeerReply(peerId)
        }
    }

    fun sendEphemeralMedia(mediaType: MediaAttachmentType, title: String, style: String) {
        val peerId = _selectedChatPeerId.value
        viewModelScope.launch {
            repository.send1on1EphemeralMedia(
                peerId = peerId,
                senderIsMe = true,
                mediaType = mediaType,
                mediaTitle = title,
                previewStyle = style
            )
            showBanner("Sent 60s ephemeral ${mediaType.name.lowercase()} • Saving & forwarding disabled.")
            simulatePeerMediaReply(peerId)
        }
    }

    private fun simulatePeerReply(peerId: String) {
        viewModelScope.launch {
            _isPeerTyping.value = true
            delay(1400L)
            _isPeerTyping.value = false
            val peer = activePeers.value.find { it.peerId == peerId } ?: return@launch
            val reply = when (peer.countryFlag) {
                "🇯🇵" -> Pair(
                    "すごいですね! Let's hop on a Roll Call video chat soon!",
                    "[Translated from Japanese] That's awesome! Let's hop on a Roll Call video chat soon!"
                )
                "🇪🇸" -> Pair(
                    "¡Qué genial! Me encanta cómo funciona el chat efímero aquí.",
                    "[Translated from Spanish] How cool! I love how the ephemeral chat works here."
                )
                "🇧🇷" -> Pair(
                    "Muito legal! Vamos subir no ranking semanal hoje!",
                    "[Translated from Portuguese] Very cool! Let's climb the weekly leaderboard today!"
                )
                "🇫🇷" -> Pair(
                    "Carrément ! C'est super sécurisé avec le mode anti-capture.",
                    "[Translated from French] Totally! It's super secure with the anti-screenshot mode."
                )
                else -> Pair(
                    "Sounds great! Love that everything here is strictly 1-on-1 and ephemeral.",
                    null
                )
            }
            val dao = RollCallDatabase.getInstance(getApplication()).rollCallDao()
            dao.insertMessage(
                ChatMessageEntity(
                    peerId = peerId,
                    senderIsMe = false,
                    textContent = reply.first,
                    translatedContent = reply.second,
                    sentAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    private fun simulatePeerMediaReply(peerId: String) {
        viewModelScope.launch {
            _isPeerTyping.value = true
            delay(1800L)
            _isPeerTyping.value = false
            repository.send1on1EphemeralMedia(
                peerId = peerId,
                senderIsMe = false,
                mediaType = MediaAttachmentType.IMAGE,
                mediaTitle = "Live_Moment_Reply_60s.jpg",
                previewStyle = "NEON_PORTRAIT"
            )
        }
    }

    fun openEphemeralMediaViewer(messageId: Long) {
        _activeMediaViewerId.value = messageId
        viewModelScope.launch {
            repository.openEphemeralMedia(messageId)
        }
    }

    fun closeEphemeralMediaViewer() {
        _activeMediaViewerId.value = null
    }

    fun toggleKeepMediaForever(messageId: Long) {
        viewModelScope.launch {
            if (!userProfile.value.isPremiumPro) {
                showBanner("Roll Call Pro ($19.99/mo) required to keep media permanently in Vault!")
                return@launch
            }
            val ok = repository.toggleKeepMediaForever(messageId)
            if (ok) {
                showBanner("Updated Roll Call Pro Vault status (bypasses 60s expiry inside encrypted Vault).")
            } else {
                showBanner("Expired media is permanently purged and cannot be recovered retroactively.")
            }
        }
    }

    fun notifyBlockedSaveOrForwardAttempt(actionName: String) {
        showBanner("SECURITY POLICY: '$actionName' is strictly blocked. No saving to gallery or forwarding allowed.")
    }

    fun subscribeToPro(planCode: String, priceLabel: String) {
        viewModelScope.launch {
            AdMobController.clearAdsForPremium()
            _showInterstitialModal.value = false
            _showRewardedModal.value = false
            repository.activateRollCallPro(planCode, priceLabel)
            showBanner("Roll Call Pro Activated! All Ads Disabled + Permanent Media Vault & Point Boosts Unlocked.")
        }
    }

    fun buyPointsPackage(packTitle: String, points: Int, priceUsd: String) {
        viewModelScope.launch {
            repository.purchasePointPack(packTitle, points, priceUsd)
            showBanner("Google Play Billing: +$points Points credited to your Leaderboard score!")
        }
    }
}
