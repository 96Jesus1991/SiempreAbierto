package com.siempreabierto.data.dao

import androidx.room.*
import com.siempreabierto.data.entities.Contribution
import com.siempreabierto.data.entities.ContributionSummary
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder al historial de contribuciones
 * Trazabilidad de todos los cambios de la comunidad
 */
@Dao
interface ContributionDao {
    
    // ==================== INSERCIONES ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contribution: Contribution): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contributions: List<Contribution>)
    
    // ==================== ACTUALIZACIONES ====================
    
    @Update
    suspend fun update(contribution: Contribution)
    
    @Query("UPDATE contributions SET isSynced = 1, syncedAt = :timestamp WHERE id = :contributionId")
    suspend fun markAsSynced(contributionId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE contributions SET isSynced = 1, syncedAt = :timestamp WHERE isSynced = 0")
    suspend fun markAllAsSynced(timestamp: Long = System.currentTimeMillis())
    
    // ==================== ELIMINACIONES ====================
    
    @Delete
    suspend fun delete(contribution: Contribution)
    
    @Query("DELETE FROM contributions WHERE id = :contributionId")
    suspend fun deleteById(contributionId: Long)
    
    @Query("DELETE FROM contributions WHERE userId = :userId")
    suspend fun deleteByUser(userId: String)
    
    @Query("DELETE FROM contributions WHERE targetType = :targetType AND targetId = :targetId")
    suspend fun deleteByTarget(targetType: String, targetId: Long)
    
    @Query("DELETE FROM contributions")
    suspend fun deleteAll()
    
    // ==================== CONSULTAS BÁSICAS ====================
    
    @Query("SELECT * FROM contributions WHERE id = :contributionId")
    suspend fun getById(contributionId: Long): Contribution?
    
    @Query("SELECT * FROM contributions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 50): Flow<List<Contribution>>
    
    // ==================== CONSULTAS POR USUARIO ====================
    
    @Query("SELECT * FROM contributions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUser(userId: String): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions WHERE userId = :userId ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentByUser(userId: String, limit: Int = 20): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions WHERE userId = :userId AND action = :action ORDER BY createdAt DESC")
    fun getByUserAndAction(userId: String, action: String): Flow<List<Contribution>>
    
    // ==================== CONSULTAS POR OBJETIVO ====================
    
    @Query("SELECT * FROM contributions WHERE targetType = :targetType AND targetId = :targetId ORDER BY createdAt DESC")
    fun getByTarget(targetType: String, targetId: Long): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions WHERE targetType = :targetType ORDER BY createdAt DESC")
    fun getByTargetType(targetType: String): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions WHERE targetType = :targetType ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentByTargetType(targetType: String, limit: Int = 20): Flow<List<Contribution>>
    
    // ==================== CONSULTAS POR ACCIÓN ====================
    
    @Query("SELECT * FROM contributions WHERE action = :action ORDER BY createdAt DESC")
    fun getByAction(action: String): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions WHERE action = :action ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentByAction(action: String, limit: Int = 20): Flow<List<Contribution>>
    
    // ==================== CONSULTAS DE SINCRONIZACIÓN ====================
    
    @Query("SELECT * FROM contributions WHERE isSynced = 0 ORDER BY createdAt ASC")
    fun getUnsyncedContributions(): Flow<List<Contribution>>
    
    @Query("SELECT * FROM contributions WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getUnsyncedContributionsList(): List<Contribution>
    
    @Query("SELECT COUNT(*) FROM contributions WHERE isSynced = 0")
    suspend fun countUnsynced(): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE isSynced = 0")
    fun countUnsyncedFlow(): Flow<Int>
    
    // ==================== ESTADÍSTICAS DE USUARIO ====================
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId")
    suspend fun countByUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId AND action = :action")
    suspend fun countByUserAndAction(userId: String, action: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId AND targetType = :targetType")
    suspend fun countByUserAndTargetType(userId: String, targetType: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId AND action = 'create' AND targetType = 'place'")
    suspend fun countPlacesCreatedByUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId AND action = 'update'")
    suspend fun countUpdatesbyUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId AND action = 'confirm'")
    suspend fun countConfirmationsByUser(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE userId = :userId AND action = 'report'")
    suspend fun countReportsByUser(userId: String): Int
    
    @Query("SELECT MAX(createdAt) FROM contributions WHERE userId = :userId")
    suspend fun getLastContributionTime(userId: String): Long?
    
    // ==================== ESTADÍSTICAS GENERALES ====================
    
    @Query("SELECT COUNT(*) FROM contributions")
    suspend fun countAll(): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE action = :action")
    suspend fun countByAction(action: String): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE targetType = :targetType")
    suspend fun countByTargetType(targetType: String): Int
    
    @Query("SELECT COUNT(DISTINCT userId) FROM contributions")
    suspend fun countUniqueContributors(): Int
    
    @Query("SELECT COUNT(*) FROM contributions WHERE createdAt >= :since")
    suspend fun countSince(since: Long): Int
    
    // ==================== HISTORIAL DE UN ELEMENTO ====================
    
    @Query("""
        SELECT * FROM contributions 
        WHERE targetType = :targetType AND targetId = :targetId 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun getHistoryForTarget(targetType: String, targetId: Long, limit: Int = 50): Flow<List<Contribution>>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE targetType = :targetType AND targetId = :targetId AND fieldChanged = :field
        ORDER BY createdAt DESC
    """)
    fun getFieldHistory(targetType: String, targetId: Long, field: String): Flow<List<Contribution>>
    
    // ==================== ACTIVIDAD RECIENTE ====================
    
    @Query("""
        SELECT * FROM contributions 
        WHERE createdAt >= :since 
        ORDER BY createdAt DESC 
        LIMIT :limit
    """)
    fun getActivitySince(since: Long, limit: Int = 100): Flow<List<Contribution>>
    
    @Query("""
        SELECT * FROM contributions 
        WHERE userId = :userId AND createdAt >= :since 
        ORDER BY createdAt DESC
    """)
    fun getUserActivitySince(userId: String, since: Long): Flow<List<Contribution>>
    
    // ==================== TOP CONTRIBUIDORES ====================
    
    @Query("""
        SELECT userId, COUNT(*) as count 
        FROM contributions 
        GROUP BY userId 
        ORDER BY count DESC 
        LIMIT :limit
    """)
    suspend fun getTopContributors(limit: Int = 10): List<ContributorCount>
    
    @Query("""
        SELECT userId, COUNT(*) as count 
        FROM contributions 
        WHERE createdAt >= :since
        GROUP BY userId 
        ORDER BY count DESC 
        LIMIT :limit
    """)
    suspend fun getTopContributorsSince(since: Long, limit: Int = 10): List<ContributorCount>
}

/**
 * Clase auxiliar para contar contribuciones por usuario
 */
data class ContributorCount(
    val userId: String,
    val count: Int
)
