package com.siempreabierto.data.dao

import androidx.room.*
import com.siempreabierto.data.entities.Restriction
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder a puntos de restricción
 * Alturas, anchos, pesos máximos en carreteras
 */
@Dao
interface RestrictionDao {
    
    // ==================== INSERCIONES ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(restriction: Restriction): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(restrictions: List<Restriction>)
    
    // ==================== ACTUALIZACIONES ====================
    
    @Update
    suspend fun update(restriction: Restriction)
    
    @Query("UPDATE restrictions SET lastConfirmedAt = :timestamp, lastConfirmedBy = :userId, updatedAt = :timestamp WHERE id = :restrictionId")
    suspend fun confirmRestriction(restrictionId: Long, userId: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE restrictions SET reportCount = reportCount + 1, updatedAt = :timestamp WHERE id = :restrictionId")
    suspend fun incrementReportCount(restrictionId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE restrictions SET isActive = :isActive, updatedAt = :timestamp WHERE id = :restrictionId")
    suspend fun setActive(restrictionId: Long, isActive: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ==================== ELIMINACIONES ====================
    
    @Delete
    suspend fun delete(restriction: Restriction)
    
    @Query("DELETE FROM restrictions WHERE id = :restrictionId")
    suspend fun deleteById(restrictionId: Long)
    
    @Query("DELETE FROM restrictions WHERE region = :region")
    suspend fun deleteByRegion(region: String)
    
    @Query("DELETE FROM restrictions")
    suspend fun deleteAll()
    
    // ==================== CONSULTAS BÁSICAS ====================
    
    @Query("SELECT * FROM restrictions WHERE id = :restrictionId")
    suspend fun getById(restrictionId: Long): Restriction?
    
    @Query("SELECT * FROM restrictions WHERE id = :restrictionId")
    fun getByIdFlow(restrictionId: Long): Flow<Restriction?>
    
    @Query("SELECT * FROM restrictions WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR TIPO ====================
    
    @Query("SELECT * FROM restrictions WHERE type = :type AND isActive = 1 ORDER BY name ASC")
    fun getByType(type: String): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE type = :type AND region = :region AND isActive = 1 ORDER BY name ASC")
    fun getByTypeAndRegion(type: String, region: String): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR REGIÓN ====================
    
    @Query("SELECT * FROM restrictions WHERE region = :region AND isActive = 1 ORDER BY name ASC")
    fun getByRegion(region: String): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE province = :province AND isActive = 1 ORDER BY name ASC")
    fun getByProvince(province: String): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR UBICACIÓN ====================
    
    @Query("""
        SELECT * FROM restrictions 
        WHERE isActive = 1 
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY name ASC
    """)
    fun getInBoundingBox(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR RESTRICCIÓN DE ALTURA ====================
    
    @Query("SELECT * FROM restrictions WHERE maxHeight IS NOT NULL AND isActive = 1 ORDER BY maxHeight ASC")
    fun getAllHeightRestrictions(): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE maxHeight IS NOT NULL AND maxHeight <= :height AND isActive = 1 ORDER BY maxHeight ASC")
    fun getHeightRestrictionsBelow(height: Double): Flow<List<Restriction>>
    
    @Query("""
        SELECT * FROM restrictions 
        WHERE maxHeight IS NOT NULL 
        AND maxHeight <= :height 
        AND isActive = 1
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY maxHeight ASC
    """)
    fun getHeightRestrictionsInArea(
        height: Double,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR RESTRICCIÓN DE PESO ====================
    
    @Query("SELECT * FROM restrictions WHERE maxWeight IS NOT NULL AND isActive = 1 ORDER BY maxWeight ASC")
    fun getAllWeightRestrictions(): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE maxWeight IS NOT NULL AND maxWeight <= :weight AND isActive = 1 ORDER BY maxWeight ASC")
    fun getWeightRestrictionsBelow(weight: Double): Flow<List<Restriction>>
    
    @Query("""
        SELECT * FROM restrictions 
        WHERE maxWeight IS NOT NULL 
        AND maxWeight <= :weight 
        AND isActive = 1
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY maxWeight ASC
    """)
    fun getWeightRestrictionsInArea(
        weight: Double,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR RESTRICCIÓN DE ANCHO ====================
    
    @Query("SELECT * FROM restrictions WHERE maxWidth IS NOT NULL AND isActive = 1 ORDER BY maxWidth ASC")
    fun getAllWidthRestrictions(): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE maxWidth IS NOT NULL AND maxWidth <= :width AND isActive = 1 ORDER BY maxWidth ASC")
    fun getWidthRestrictionsBelow(width: Double): Flow<List<Restriction>>
    
    // ==================== CONSULTAS COMBINADAS PARA VEHÍCULOS ====================
    
    @Query("""
        SELECT * FROM restrictions 
        WHERE isActive = 1
        AND (
            (maxHeight IS NOT NULL AND maxHeight <= :height)
            OR (maxWidth IS NOT NULL AND maxWidth <= :width)
            OR (maxWeight IS NOT NULL AND maxWeight <= :weight)
        )
        ORDER BY name ASC
    """)
    fun getRestrictionsForVehicle(
        height: Double,
        width: Double,
        weight: Double
    ): Flow<List<Restriction>>
    
    @Query("""
        SELECT * FROM restrictions 
        WHERE isActive = 1
        AND region = :region
        AND (
            (maxHeight IS NOT NULL AND maxHeight <= :height)
            OR (maxWidth IS NOT NULL AND maxWidth <= :width)
            OR (maxWeight IS NOT NULL AND maxWeight <= :weight)
        )
        ORDER BY name ASC
    """)
    fun getRestrictionsForVehicleInRegion(
        height: Double,
        width: Double,
        weight: Double,
        region: String
    ): Flow<List<Restriction>>
    
    // ==================== CONSULTAS POR CARRETERA ====================
    
    @Query("SELECT * FROM restrictions WHERE roadName = :roadName AND isActive = 1 ORDER BY kmPoint ASC")
    fun getByRoad(roadName: String): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE roadName LIKE '%' || :query || '%' AND isActive = 1 ORDER BY roadName ASC")
    fun searchByRoad(query: String): Flow<List<Restriction>>
    
    // ==================== BÚSQUEDA ====================
    
    @Query("""
        SELECT * FROM restrictions 
        WHERE isActive = 1 
        AND (name LIKE '%' || :query || '%' 
             OR roadName LIKE '%' || :query || '%' 
             OR description LIKE '%' || :query || '%')
        ORDER BY name ASC
        LIMIT :limit
    """)
    fun search(query: String, limit: Int = 50): Flow<List<Restriction>>
    
    // ==================== ESTADÍSTICAS ====================
    
    @Query("SELECT COUNT(*) FROM restrictions WHERE isActive = 1")
    suspend fun countActive(): Int
    
    @Query("SELECT COUNT(*) FROM restrictions WHERE type = :type AND isActive = 1")
    suspend fun countByType(type: String): Int
    
    @Query("SELECT COUNT(*) FROM restrictions WHERE region = :region AND isActive = 1")
    suspend fun countByRegion(region: String): Int
    
    @Query("SELECT COUNT(*) FROM restrictions WHERE contributedBy = :userId")
    suspend fun countByContributor(userId: String): Int
    
    @Query("SELECT DISTINCT region FROM restrictions WHERE isActive = 1")
    suspend fun getAllRegionsWithRestrictions(): List<String>
    
    @Query("SELECT DISTINCT roadName FROM restrictions WHERE roadName IS NOT NULL AND isActive = 1 ORDER BY roadName ASC")
    suspend fun getAllRoadsWithRestrictions(): List<String>
    
    // ==================== ÚLTIMAS ACTUALIZACIONES ====================
    
    @Query("SELECT * FROM restrictions WHERE isActive = 1 ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentlyUpdated(limit: Int = 20): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE isActive = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentlyAdded(limit: Int = 20): Flow<List<Restriction>>
    
    @Query("SELECT * FROM restrictions WHERE contributedBy = :userId ORDER BY createdAt DESC")
    fun getByContributor(userId: String): Flow<List<Restriction>>
}
