package com.siempreabierto.data.dao

import androidx.room.*
import com.siempreabierto.data.entities.Helper
import com.siempreabierto.data.entities.HelpRequest
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder a ayudantes de la comunidad
 * Usuarios que ofrecen ayuda voluntaria a otros conductores
 */
@Dao
interface HelperDao {
    
    // ==================== HELPERS - INSERCIONES ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHelper(helper: Helper): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHelpers(helpers: List<Helper>)
    
    // ==================== HELPERS - ACTUALIZACIONES ====================
    
    @Update
    suspend fun updateHelper(helper: Helper)
    
    @Query("UPDATE helpers SET isAvailable = :isAvailable, updatedAt = :timestamp WHERE id = :helperId")
    suspend fun setAvailability(helperId: Long, isAvailable: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE helpers SET lastActiveAt = :timestamp WHERE id = :helperId")
    suspend fun updateLastActive(helperId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE helpers SET helpCount = helpCount + 1, updatedAt = :timestamp WHERE id = :helperId")
    suspend fun incrementHelpCount(helperId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE helpers SET latitude = :lat, longitude = :lon, updatedAt = :timestamp WHERE id = :helperId")
    suspend fun updateLocation(helperId: Long, lat: Double, lon: Double, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE helpers SET isActive = :isActive, updatedAt = :timestamp WHERE id = :helperId")
    suspend fun setActive(helperId: Long, isActive: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ==================== HELPERS - ELIMINACIONES ====================
    
    @Delete
    suspend fun deleteHelper(helper: Helper)
    
    @Query("DELETE FROM helpers WHERE id = :helperId")
    suspend fun deleteHelperById(helperId: Long)
    
    @Query("DELETE FROM helpers WHERE localUserId = :localUserId")
    suspend fun deleteHelperByLocalUser(localUserId: String)
    
    @Query("DELETE FROM helpers")
    suspend fun deleteAllHelpers()
    
    // ==================== HELPERS - CONSULTAS BÁSICAS ====================
    
    @Query("SELECT * FROM helpers WHERE id = :helperId")
    suspend fun getHelperById(helperId: Long): Helper?
    
    @Query("SELECT * FROM helpers WHERE id = :helperId")
    fun getHelperByIdFlow(helperId: Long): Flow<Helper?>
    
    @Query("SELECT * FROM helpers WHERE localUserId = :localUserId")
    suspend fun getHelperByLocalUser(localUserId: String): Helper?
    
    @Query("SELECT * FROM helpers WHERE localUserId = :localUserId")
    fun getHelperByLocalUserFlow(localUserId: String): Flow<Helper?>
    
    @Query("SELECT * FROM helpers WHERE isActive = 1 AND isAvailable = 1 ORDER BY lastActiveAt DESC")
    fun getAllAvailableHelpers(): Flow<List<Helper>>
    
    @Query("SELECT * FROM helpers WHERE isActive = 1 ORDER BY nickname ASC")
    fun getAllActiveHelpers(): Flow<List<Helper>>
    
    @Query("SELECT * FROM helpers ORDER BY updatedAt DESC")
    fun getAllHelpers(): Flow<List<Helper>>
    
    // ==================== HELPERS - CONSULTAS POR REGIÓN ====================
    
    @Query("SELECT * FROM helpers WHERE region = :region AND isActive = 1 AND isAvailable = 1 ORDER BY lastActiveAt DESC")
    fun getAvailableHelpersByRegion(region: String): Flow<List<Helper>>
    
    @Query("SELECT * FROM helpers WHERE province = :province AND isActive = 1 AND isAvailable = 1 ORDER BY lastActiveAt DESC")
    fun getAvailableHelpersByProvince(province: String): Flow<List<Helper>>
    
    // ==================== HELPERS - CONSULTAS POR UBICACIÓN ====================
    
    @Query("""
        SELECT * FROM helpers 
        WHERE isActive = 1 AND isAvailable = 1
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY lastActiveAt DESC
    """)
    fun getAvailableHelpersInArea(
        minLat: Double, 
        maxLat: Double, 
        minLon: Double, 
        maxLon: Double
    ): Flow<List<Helper>>
    
    // ==================== HELPERS - CONSULTAS POR TIPO DE AYUDA ====================
    
    @Query("SELECT * FROM helpers WHERE isActive = 1 AND isAvailable = 1 AND canHelpWith LIKE '%' || :helpType || '%' ORDER BY lastActiveAt DESC")
    fun getHelpersByHelpType(helpType: String): Flow<List<Helper>>
    
    @Query("""
        SELECT * FROM helpers 
        WHERE isActive = 1 AND isAvailable = 1 
        AND canHelpWith LIKE '%' || :helpType || '%'
        AND region = :region
        ORDER BY lastActiveAt DESC
    """)
    fun getHelpersByHelpTypeAndRegion(helpType: String, region: String): Flow<List<Helper>>
    
    @Query("SELECT * FROM helpers WHERE isActive = 1 AND isAvailable = 1 AND hasJumpCables = 1 ORDER BY lastActiveAt DESC")
    fun getHelpersWithJumpCables(): Flow<List<Helper>>
    
    @Query("SELECT * FROM helpers WHERE isActive = 1 AND isAvailable = 1 AND hasTowRope = 1 ORDER BY lastActiveAt DESC")
    fun getHelpersWithTowRope(): Flow<List<Helper>>
    
    @Query("SELECT * FROM helpers WHERE isActive = 1 AND isAvailable = 1 AND hasTools = 1 ORDER BY lastActiveAt DESC")
    fun getHelpersWithTools(): Flow<List<Helper>>
    
    // ==================== HELPERS - ESTADÍSTICAS ====================
    
    @Query("SELECT COUNT(*) FROM helpers WHERE isActive = 1")
    suspend fun countActiveHelpers(): Int
    
    @Query("SELECT COUNT(*) FROM helpers WHERE isActive = 1 AND isAvailable = 1")
    suspend fun countAvailableHelpers(): Int
    
    @Query("SELECT COUNT(*) FROM helpers WHERE region = :region AND isActive = 1")
    suspend fun countHelpersByRegion(region: String): Int
    
    @Query("SELECT SUM(helpCount) FROM helpers")
    suspend fun getTotalHelpsProvided(): Int?
    
    // ==================== HELP REQUESTS - INSERCIONES ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: HelpRequest): Long
    
    // ==================== HELP REQUESTS - ACTUALIZACIONES ====================
    
    @Update
    suspend fun updateRequest(request: HelpRequest)
    
    @Query("UPDATE help_requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Long, status: String)
    
    @Query("UPDATE help_requests SET status = 'accepted', helperId = :helperId, helperName = :helperName, acceptedAt = :timestamp WHERE id = :requestId")
    suspend fun acceptRequest(requestId: Long, helperId: Long, helperName: String?, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE help_requests SET status = 'completed', completedAt = :timestamp WHERE id = :requestId")
    suspend fun completeRequest(requestId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE help_requests SET status = 'cancelled' WHERE id = :requestId")
    suspend fun cancelRequest(requestId: Long)
    
    @Query("UPDATE help_requests SET wasHelpful = :wasHelpful, rating = :rating, feedback = :feedback WHERE id = :requestId")
    suspend fun rateRequest(requestId: Long, wasHelpful: Boolean, rating: Int?, feedback: String?)
    
    // ==================== HELP REQUESTS - ELIMINACIONES ====================
    
    @Delete
    suspend fun deleteRequest(request: HelpRequest)
    
    @Query("DELETE FROM help_requests WHERE id = :requestId")
    suspend fun deleteRequestById(requestId: Long)
    
    @Query("DELETE FROM help_requests WHERE requesterId = :requesterId")
    suspend fun deleteRequestsByRequester(requesterId: String)
    
    @Query("DELETE FROM help_requests")
    suspend fun deleteAllRequests()
    
    // ==================== HELP REQUESTS - CONSULTAS ====================
    
    @Query("SELECT * FROM help_requests WHERE id = :requestId")
    suspend fun getRequestById(requestId: Long): HelpRequest?
    
    @Query("SELECT * FROM help_requests WHERE id = :requestId")
    fun getRequestByIdFlow(requestId: Long): Flow<HelpRequest?>
    
    @Query("SELECT * FROM help_requests WHERE requesterId = :requesterId ORDER BY createdAt DESC")
    fun getRequestsByRequester(requesterId: String): Flow<List<HelpRequest>>
    
    @Query("SELECT * FROM help_requests WHERE helperId = :helperId ORDER BY createdAt DESC")
    fun getRequestsByHelper(helperId: Long): Flow<List<HelpRequest>>
    
    @Query("SELECT * FROM help_requests WHERE status = :status ORDER BY createdAt DESC")
    fun getRequestsByStatus(status: String): Flow<List<HelpRequest>>
    
    @Query("SELECT * FROM help_requests WHERE status = 'pending' ORDER BY createdAt ASC")
    fun getPendingRequests(): Flow<List<HelpRequest>>
    
    @Query("""
        SELECT * FROM help_requests 
        WHERE status = 'pending'
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY createdAt ASC
    """)
    fun getPendingRequestsInArea(
        minLat: Double, 
        maxLat: Double, 
        minLon: Double, 
        maxLon: Double
    ): Flow<List<HelpRequest>>
    
    // ==================== HELP REQUESTS - ESTADÍSTICAS ====================
    
    @Query("SELECT COUNT(*) FROM help_requests WHERE status = 'pending'")
    suspend fun countPendingRequests(): Int
    
    @Query("SELECT COUNT(*) FROM help_requests WHERE status = 'completed'")
    suspend fun countCompletedRequests(): Int
    
    @Query("SELECT COUNT(*) FROM help_requests WHERE helperId = :helperId AND status = 'completed'")
    suspend fun countCompletedByHelper(helperId: Long): Int
    
    @Query("SELECT AVG(rating) FROM help_requests WHERE helperId = :helperId AND rating IS NOT NULL")
    suspend fun getAverageRatingForHelper(helperId: Long): Float?
}
