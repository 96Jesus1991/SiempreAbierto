package com.siempreabierto.data.dao

import androidx.room.*
import com.siempreabierto.data.entities.Place
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder a lugares de la comunidad
 */
@Dao
interface PlaceDao {
    
    // ==================== INSERCIONES ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: Place): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<Place>)
    
    // ==================== ACTUALIZACIONES ====================
    
    @Update
    suspend fun update(place: Place)
    
    @Query("UPDATE places SET lastConfirmedAt = :timestamp, lastConfirmedBy = :userId, updatedAt = :timestamp WHERE id = :placeId")
    suspend fun confirmPlace(placeId: Long, userId: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE places SET reportCount = reportCount + 1, updatedAt = :timestamp WHERE id = :placeId")
    suspend fun incrementReportCount(placeId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE places SET isActive = :isActive, updatedAt = :timestamp WHERE id = :placeId")
    suspend fun setActive(placeId: Long, isActive: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ==================== ELIMINACIONES ====================
    
    @Delete
    suspend fun delete(place: Place)
    
    @Query("DELETE FROM places WHERE id = :placeId")
    suspend fun deleteById(placeId: Long)
    
    @Query("DELETE FROM places WHERE region = :region")
    suspend fun deleteByRegion(region: String)
    
    @Query("DELETE FROM places")
    suspend fun deleteAll()
    
    // ==================== CONSULTAS BÁSICAS ====================
    
    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getById(placeId: Long): Place?
    
    @Query("SELECT * FROM places WHERE id = :placeId")
    fun getByIdFlow(placeId: Long): Flow<Place?>
    
    @Query("SELECT * FROM places WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<Place>>
    
    @Query("SELECT * FROM places ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<Place>>
    
    // ==================== CONSULTAS POR CATEGORÍA ====================
    
    @Query("SELECT * FROM places WHERE category = :category AND isActive = 1 ORDER BY name ASC")
    fun getByCategory(category: String): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE category = :category AND region = :region AND isActive = 1 ORDER BY name ASC")
    fun getByCategoryAndRegion(category: String, region: String): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE subcategory = :subcategory AND isActive = 1 ORDER BY name ASC")
    fun getBySubcategory(subcategory: String): Flow<List<Place>>
    
    // ==================== CONSULTAS POR REGIÓN ====================
    
    @Query("SELECT * FROM places WHERE region = :region AND isActive = 1 ORDER BY name ASC")
    fun getByRegion(region: String): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE province = :province AND isActive = 1 ORDER BY name ASC")
    fun getByProvince(province: String): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE city = :city AND isActive = 1 ORDER BY name ASC")
    fun getByCity(city: String): Flow<List<Place>>
    
    // ==================== CONSULTAS POR UBICACIÓN ====================
    
    @Query("""
        SELECT * FROM places 
        WHERE isActive = 1 
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY name ASC
    """)
    fun getInBoundingBox(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<Place>>
    
    @Query("""
        SELECT * FROM places 
        WHERE category = :category 
        AND isActive = 1 
        AND latitude BETWEEN :minLat AND :maxLat 
        AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY name ASC
    """)
    fun getInBoundingBoxByCategory(
        category: String,
        minLat: Double, 
        maxLat: Double, 
        minLon: Double, 
        maxLon: Double
    ): Flow<List<Place>>
    
    // ==================== CONSULTAS PARA VEHÍCULOS GRANDES ====================
    
    @Query("SELECT * FROM places WHERE fitsTruck = 1 AND isActive = 1 ORDER BY name ASC")
    fun getTruckFriendly(): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE fitsBus = 1 AND isActive = 1 ORDER BY name ASC")
    fun getBusFriendly(): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE fitsTrailer = 1 AND isActive = 1 ORDER BY name ASC")
    fun getTrailerFriendly(): Flow<List<Place>>
    
    @Query("""
        SELECT * FROM places 
        WHERE category = :category 
        AND fitsTruck = 1 
        AND isActive = 1 
        AND region = :region
        ORDER BY name ASC
    """)
    fun getTruckFriendlyByCategoryAndRegion(category: String, region: String): Flow<List<Place>>
    
    // ==================== CONSULTAS 24H ====================
    
    @Query("SELECT * FROM places WHERE is24h = 1 AND isActive = 1 ORDER BY name ASC")
    fun get24hPlaces(): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE is24h = 1 AND category = :category AND isActive = 1 ORDER BY name ASC")
    fun get24hByCategory(category: String): Flow<List<Place>>
    
    // ==================== BÚSQUEDA ====================
    
    @Query("""
        SELECT * FROM places 
        WHERE isActive = 1 
        AND (name LIKE '%' || :query || '%' 
             OR address LIKE '%' || :query || '%' 
             OR city LIKE '%' || :query || '%')
        ORDER BY name ASC
        LIMIT :limit
    """)
    fun search(query: String, limit: Int = 50): Flow<List<Place>>
    
    @Query("""
        SELECT * FROM places 
        WHERE isActive = 1 
        AND category = :category
        AND (name LIKE '%' || :query || '%' 
             OR address LIKE '%' || :query || '%' 
             OR city LIKE '%' || :query || '%')
        ORDER BY name ASC
        LIMIT :limit
    """)
    fun searchByCategory(query: String, category: String, limit: Int = 50): Flow<List<Place>>
    
    // ==================== ESTADÍSTICAS ====================
    
    @Query("SELECT COUNT(*) FROM places WHERE isActive = 1")
    suspend fun countActive(): Int
    
    @Query("SELECT COUNT(*) FROM places WHERE category = :category AND isActive = 1")
    suspend fun countByCategory(category: String): Int
    
    @Query("SELECT COUNT(*) FROM places WHERE region = :region AND isActive = 1")
    suspend fun countByRegion(region: String): Int
    
    @Query("SELECT COUNT(*) FROM places WHERE contributedBy = :userId")
    suspend fun countByContributor(userId: String): Int
    
    @Query("SELECT DISTINCT category FROM places WHERE isActive = 1")
    suspend fun getAllCategories(): List<String>
    
    @Query("SELECT DISTINCT region FROM places WHERE isActive = 1")
    suspend fun getAllRegionsWithPlaces(): List<String>
    
    // ==================== ÚLTIMAS ACTUALIZACIONES ====================
    
    @Query("SELECT * FROM places WHERE isActive = 1 ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentlyUpdated(limit: Int = 20): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE isActive = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentlyAdded(limit: Int = 20): Flow<List<Place>>
    
    @Query("SELECT * FROM places WHERE contributedBy = :userId ORDER BY createdAt DESC")
    fun getByContributor(userId: String): Flow<List<Place>>
}
