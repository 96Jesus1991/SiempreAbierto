package com.siempreabierto.data.dao

import androidx.room.*
import com.siempreabierto.data.entities.Route
import com.siempreabierto.data.entities.RouteSummary
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder a rutas guardadas y recomendadas
 * Rutas offline para viajeros, camioneros y conductores de bus
 */
@Dao
interface RouteDao {
    
    // ==================== INSERCIONES ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: Route): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<Route>)
    
    // ==================== ACTUALIZACIONES ====================
    
    @Update
    suspend fun update(route: Route)
    
    @Query("UPDATE routes SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id = :routeId")
    suspend fun setFavorite(routeId: Long, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE routes SET usageCount = usageCount + 1, lastUsedAt = :timestamp WHERE id = :routeId")
    suspend fun incrementUsage(routeId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE routes SET upvotes = upvotes + 1, updatedAt = :timestamp WHERE id = :routeId")
    suspend fun upvote(routeId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE routes SET downvotes = downvotes + 1, updatedAt = :timestamp WHERE id = :routeId")
    suspend fun downvote(routeId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE routes SET isPublic = :isPublic, updatedAt = :timestamp WHERE id = :routeId")
    suspend fun setPublic(routeId: Long, isPublic: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE routes SET isActive = :isActive, updatedAt = :timestamp WHERE id = :routeId")
    suspend fun setActive(routeId: Long, isActive: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ==================== ELIMINACIONES ====================
    
    @Delete
    suspend fun delete(route: Route)
    
    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteById(routeId: Long)
    
    @Query("DELETE FROM routes WHERE contributedBy = :userId")
    suspend fun deleteByUser(userId: String)
    
    @Query("DELETE FROM routes")
    suspend fun deleteAll()
    
    // ==================== CONSULTAS BÁSICAS ====================
    
    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getById(routeId: Long): Route?
    
    @Query("SELECT * FROM routes WHERE id = :routeId")
    fun getByIdFlow(routeId: Long): Flow<Route?>
    
    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<Route>>
    
    // ==================== CONSULTAS DE FAVORITOS ====================
    
    @Query("SELECT * FROM routes WHERE isFavorite = 1 AND isActive = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE isFavorite = 1 AND isActive = 1 ORDER BY lastUsedAt DESC")
    fun getFavoritesByLastUsed(): Flow<List<Route>>
    
    // ==================== CONSULTAS POR TIPO DE VEHÍCULO ====================
    
    @Query("SELECT * FROM routes WHERE vehicleType = :vehicleType AND isActive = 1 ORDER BY name ASC")
    fun getByVehicleType(vehicleType: String): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE suitableForTruck = 1 AND isActive = 1 ORDER BY name ASC")
    fun getTruckSuitableRoutes(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE suitableForBus = 1 AND isActive = 1 ORDER BY name ASC")
    fun getBusSuitableRoutes(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE suitableForCamper = 1 AND isActive = 1 ORDER BY name ASC")
    fun getCamperSuitableRoutes(): Flow<List<Route>>
    
    // ==================== CONSULTAS DE COMUNIDAD ====================
    
    @Query("SELECT * FROM routes WHERE isPublic = 1 AND isActive = 1 ORDER BY upvotes DESC")
    fun getPublicRoutes(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE isCommunityRecommended = 1 AND isActive = 1 ORDER BY upvotes DESC")
    fun getCommunityRecommended(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE isPublic = 1 AND isActive = 1 ORDER BY upvotes DESC LIMIT :limit")
    fun getTopRatedPublicRoutes(limit: Int = 20): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE isPublic = 1 AND vehicleType = :vehicleType AND isActive = 1 ORDER BY upvotes DESC")
    fun getPublicRoutesByVehicleType(vehicleType: String): Flow<List<Route>>
    
    // ==================== CONSULTAS POR UBICACIÓN ====================
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1
        AND (
            (originLat BETWEEN :minLat AND :maxLat AND originLon BETWEEN :minLon AND :maxLon)
            OR (destinationLat BETWEEN :minLat AND :maxLat AND destinationLon BETWEEN :minLon AND :maxLon)
        )
        ORDER BY name ASC
    """)
    fun getRoutesInArea(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<Route>>
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1
        AND originLat BETWEEN :minLat AND :maxLat 
        AND originLon BETWEEN :minLon AND :maxLon
        ORDER BY name ASC
    """)
    fun getRoutesFromArea(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<Route>>
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1
        AND destinationLat BETWEEN :minLat AND :maxLat 
        AND destinationLon BETWEEN :minLon AND :maxLon
        ORDER BY name ASC
    """)
    fun getRoutesToArea(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<Route>>
    
    // ==================== CONSULTAS POR RESTRICCIONES ====================
    
    @Query("SELECT * FROM routes WHERE hasHeightRestrictions = 0 AND isActive = 1 ORDER BY name ASC")
    fun getRoutesWithoutHeightRestrictions(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE hasWeightRestrictions = 0 AND isActive = 1 ORDER BY name ASC")
    fun getRoutesWithoutWeightRestrictions(): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE tollRoads = 0 AND isActive = 1 ORDER BY name ASC")
    fun getRoutesWithoutTolls(): Flow<List<Route>>
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1
        AND (minHeightOnRoute IS NULL OR minHeightOnRoute >= :height)
        ORDER BY name ASC
    """)
    fun getRoutesSuitableForHeight(height: Double): Flow<List<Route>>
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1
        AND (maxWeightOnRoute IS NULL OR maxWeightOnRoute >= :weight)
        ORDER BY name ASC
    """)
    fun getRoutesSuitableForWeight(weight: Double): Flow<List<Route>>
    
    // ==================== CONSULTAS POR DISTANCIA ====================
    
    @Query("SELECT * FROM routes WHERE distanceKm <= :maxKm AND isActive = 1 ORDER BY distanceKm ASC")
    fun getRoutesShorterThan(maxKm: Double): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE distanceKm >= :minKm AND isActive = 1 ORDER BY distanceKm ASC")
    fun getRoutesLongerThan(minKm: Double): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE distanceKm BETWEEN :minKm AND :maxKm AND isActive = 1 ORDER BY distanceKm ASC")
    fun getRoutesInDistanceRange(minKm: Double, maxKm: Double): Flow<List<Route>>
    
    // ==================== BÚSQUEDA ====================
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1 
        AND (name LIKE '%' || :query || '%' 
             OR originName LIKE '%' || :query || '%' 
             OR destinationName LIKE '%' || :query || '%'
             OR description LIKE '%' || :query || '%')
        ORDER BY name ASC
        LIMIT :limit
    """)
    fun search(query: String, limit: Int = 50): Flow<List<Route>>
    
    @Query("""
        SELECT * FROM routes 
        WHERE isActive = 1 
        AND isPublic = 1
        AND (name LIKE '%' || :query || '%' 
             OR originName LIKE '%' || :query || '%' 
             OR destinationName LIKE '%' || :query || '%')
        ORDER BY upvotes DESC
        LIMIT :limit
    """)
    fun searchPublicRoutes(query: String, limit: Int = 50): Flow<List<Route>>
    
    // ==================== RUTAS RECIENTES ====================
    
    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY lastUsedAt DESC LIMIT :limit")
    fun getRecentlyUsed(limit: Int = 10): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentlyCreated(limit: Int = 10): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE contributedBy = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun getByContributor(userId: String): Flow<List<Route>>
    
    // ==================== ESTADÍSTICAS ====================
    
    @Query("SELECT COUNT(*) FROM routes WHERE isActive = 1")
    suspend fun countActive(): Int
    
    @Query("SELECT COUNT(*) FROM routes WHERE isFavorite = 1 AND isActive = 1")
    suspend fun countFavorites(): Int
    
    @Query("SELECT COUNT(*) FROM routes WHERE isPublic = 1 AND isActive = 1")
    suspend fun countPublic(): Int
    
    @Query("SELECT COUNT(*) FROM routes WHERE contributedBy = :userId")
    suspend fun countByContributor(userId: String): Int
    
    @Query("SELECT COUNT(*) FROM routes WHERE vehicleType = :vehicleType AND isActive = 1")
    suspend fun countByVehicleType(vehicleType: String): Int
    
    @Query("SELECT SUM(usageCount) FROM routes")
    suspend fun getTotalUsageCount(): Int?
    
    @Query("SELECT AVG(distanceKm) FROM routes WHERE isActive = 1")
    suspend fun getAverageDistance(): Double?
    
    // ==================== RUTAS MÁS USADAS ====================
    
    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY usageCount DESC LIMIT :limit")
    fun getMostUsed(limit: Int = 10): Flow<List<Route>>
    
    @Query("SELECT * FROM routes WHERE isPublic = 1 AND isActive = 1 ORDER BY usageCount DESC LIMIT :limit")
    fun getMostUsedPublic(limit: Int = 10): Flow<List<Route>>
}
