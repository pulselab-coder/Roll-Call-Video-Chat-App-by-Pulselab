package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RollCallDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)

    // --- 1:1 Matched Peers ---
    @Query("SELECT * FROM matched_peers WHERE isBlocked = 0 ORDER BY lastMatchedAtMillis DESC")
    fun observeActivePeers(): Flow<List<MatchedPeerEntity>>

    @Query("SELECT * FROM matched_peers WHERE isBlocked = 1 ORDER BY lastMatchedAtMillis DESC")
    fun observeBlockedPeers(): Flow<List<MatchedPeerEntity>>

    @Query("SELECT * FROM matched_peers WHERE isBlocked = 0")
    suspend fun getAvailablePeersOnce(): List<MatchedPeerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeers(peers: List<MatchedPeerEntity>)

    @Update
    suspend fun updatePeer(peer: MatchedPeerEntity)

    @Query("UPDATE matched_peers SET isBlocked = 1 WHERE peerId = :peerId")
    suspend fun blockPeerById(peerId: String)

    @Query("UPDATE matched_peers SET isBlocked = 0 WHERE peerId = :peerId")
    suspend fun unblockPeerById(peerId: String)

    // --- 1:1 Chat & Ephemeral Media ---
    @Query("SELECT * FROM chat_messages WHERE peerId = :peerId ORDER BY sentAtMillis ASC")
    fun observeMessagesForPeer(peerId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE mediaType != 'NONE' ORDER BY sentAtMillis DESC")
    fun observeAllMediaMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE isKeptForeverInVault = 1 ORDER BY sentAtMillis DESC")
    fun observeVaultedMedia(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE id = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: Long): ChatMessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Query(
        """
        UPDATE chat_messages 
        SET isExpired = 1 
        WHERE mediaType != 'NONE' 
          AND isKeptForeverInVault = 0 
          AND isExpired = 0 
          AND expiresAtMillis IS NOT NULL 
          AND expiresAtMillis <= :nowMillis
        """
    )
    suspend fun sweepExpiredMedia(nowMillis: Long): Int

    @Query("DELETE FROM chat_messages WHERE peerId = :peerId")
    suspend fun deleteMessagesForPeer(peerId: String)

    // --- Leaderboard ---
    @Query("SELECT * FROM leaderboard_entries ORDER BY points DESC")
    fun observeLeaderboard(): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT COUNT(*) FROM leaderboard_entries")
    suspend fun getLeaderboardCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

    // --- Safety Reports ---
    @Query("SELECT * FROM safety_reports ORDER BY createdAtMillis DESC")
    fun observeSafetyReports(): Flow<List<SafetyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafetyReport(report: SafetyReportEntity)

    // --- Point Transactions ---
    @Query("SELECT * FROM point_transactions ORDER BY timestampMillis DESC")
    fun observePointTransactions(): Flow<List<PointTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointTransaction(tx: PointTransactionEntity)
}
