package com.siempreabierto.data.dao

import androidx.room.*
import com.siempreabierto.data.entities.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder a la configuración del usuario
 * Toda la configuración se guarda LOCALMENTE - Sin login, sin tracking
 */
@Dao
interface UserSettingsDao {
    
    // ==================== INSERCIÓN / ACTUALIZACIÓN ====================
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: UserSettings)
    
    @Update
    suspend fun update(settings: UserSettings)
    
    @Query("UPDATE user_settings SET updatedAt = :timestamp WHERE id = 1")
    suspend fun touch(timestamp: Long = System.currentTimeMillis())
    
    // ==================== CONSULTAS ====================
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getSettings(): UserSettings?
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<UserSettings?>
    
    @Query("SELECT localUserId FROM user_settings WHERE id = 1")
    suspend fun getLocalUserId(): String?
    
    // ==================== VEHÍCULO ====================
    
    @Query("""
        UPDATE user_settings SET 
            vehicleType = :vehicleType,
            vehicleHeight = :height,
            vehicleWidth = :width,
            vehicleWeight = :weight,
            vehicleLength = :length,
            updatedAt = :timestamp
        WHERE id = 1
    """)
    suspend fun updateVehicle(
        vehicleType: String,
        height: Double?,
        width: Double?,
        weight: Double?,
        length: Double?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("UPDATE user_settings SET vehicleType = :vehicleType, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateVehicleType(vehicleType: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET vehicleHeight = :height, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateVehicleHeight(height: Double?, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET vehicleWidth = :width, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateVehicleWidth(width: Double?, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET vehicleWeight = :weight, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateVehicleWeight(weight: Double?, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET vehicleDescription = :description, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateVehicleDescription(description: String?, timestamp: Long = System.currentTimeMillis())
    
    // ==================== APARIENCIA ====================
    
    @Query("UPDATE user_settings SET darkMode = :darkMode, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateDarkMode(darkMode: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT darkMode FROM user_settings WHERE id = 1")
    suspend fun getDarkMode(): Boolean?
    
    @Query("SELECT darkMode FROM user_settings WHERE id = 1")
    fun getDarkModeFlow(): Flow<Boolean?>
    
    @Query("UPDATE user_settings SET mapZoomDefault = :zoom, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateMapZoom(zoom: Float, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET distanceUnit = :unit, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateDistanceUnit(unit: String, timestamp: Long = System.currentTimeMillis())
    
    // ==================== PREFERENCIAS DE MAPA ====================
    
    @Query("UPDATE user_settings SET showRestrictionsOnMap = :show, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateShowRestrictions(show: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET showHelpersOnMap = :show, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateShowHelpers(show: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ==================== PREFERENCIAS DE RUTAS ====================
    
    @Query("UPDATE user_settings SET avoidTolls = :avoid, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateAvoidTolls(avoid: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET avoidHighways = :avoid, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateAvoidHighways(avoid: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET preferTruckRoutes = :prefer, updatedAt = :timestamp WHERE id = 1")
    suspend fun updatePreferTruckRoutes(prefer: Boolean, timestamp: Long = System.currentTimeMillis())
    
    // ==================== NOTIFICACIONES ====================
    
    @Query("UPDATE user_settings SET notifyRestrictions = :notify, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateNotifyRestrictions(notify: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET notifyNearbyHelpers = :notify, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateNotifyNearbyHelpers(notify: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET restrictionAlertDistance = :distanceKm, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateRestrictionAlertDistance(distanceKm: Int, timestamp: Long = System.currentTimeMillis())
    
    // ==================== HELPER (AYUDANTE) ====================
    
    @Query("UPDATE user_settings SET isHelper = :isHelper, helperProfileId = :profileId, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateHelperStatus(isHelper: Boolean, profileId: Long?, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT isHelper FROM user_settings WHERE id = 1")
    suspend fun isHelper(): Boolean?
    
    @Query("SELECT helperProfileId FROM user_settings WHERE id = 1")
    suspend fun getHelperProfileId(): Long?
    
    // ==================== REGIONES DESCARGADAS ====================
    
    @Query("UPDATE user_settings SET downloadedRegions = :regions, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateDownloadedRegions(regions: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT downloadedRegions FROM user_settings WHERE id = 1")
    suspend fun getDownloadedRegions(): String?
    
    @Query("SELECT downloadedRegions FROM user_settings WHERE id = 1")
    fun getDownloadedRegionsFlow(): Flow<String?>
    
    // ==================== SINCRONIZACIÓN ====================
    
    @Query("UPDATE user_settings SET lastSyncAt = :timestamp, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateLastSync(timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET autoSync = :autoSync, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateAutoSync(autoSync: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT lastSyncAt FROM user_settings WHERE id = 1")
    suspend fun getLastSyncTime(): Long?
    
    // ==================== ESTADÍSTICAS ====================
    
    @Query("""
        UPDATE user_settings SET 
            totalContributions = totalContributions + 1,
            updatedAt = :timestamp
        WHERE id = 1
    """)
    suspend fun incrementTotalContributions(timestamp: Long = System.currentTimeMillis())
    
    @Query("""
        UPDATE user_settings SET 
            placesAdded = placesAdded + 1,
            totalContributions = totalContributions + 1,
            updatedAt = :timestamp
        WHERE id = 1
    """)
    suspend fun incrementPlacesAdded(timestamp: Long = System.currentTimeMillis())
    
    @Query("""
        UPDATE user_settings SET 
            confirmationsMade = confirmationsMade + 1,
            totalContributions = totalContributions + 1,
            updatedAt = :timestamp
        WHERE id = 1
    """)
    suspend fun incrementConfirmations(timestamp: Long = System.currentTimeMillis())
    
    @Query("""
        UPDATE user_settings SET 
            helpProvided = helpProvided + 1,
            updatedAt = :timestamp
        WHERE id = 1
    """)
    suspend fun incrementHelpProvided(timestamp: Long = System.currentTimeMillis())
    
    // ==================== LEGAL ====================
    
    @Query("UPDATE user_settings SET acceptedTermsAt = :timestamp, updatedAt = :timestamp WHERE id = 1")
    suspend fun acceptTerms(timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_settings SET acceptedPrivacyAt = :timestamp, updatedAt = :timestamp WHERE id = 1")
    suspend fun acceptPrivacy(timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT acceptedTermsAt FROM user_settings WHERE id = 1")
    suspend fun getAcceptedTermsTime(): Long?
    
    @Query("SELECT acceptedPrivacyAt FROM user_settings WHERE id = 1")
    suspend fun getAcceptedPrivacyTime(): Long?
    
    // ==================== NICKNAME ====================
    
    @Query("UPDATE user_settings SET nickname = :nickname, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateNickname(nickname: String?, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT nickname FROM user_settings WHERE id = 1")
    suspend fun getNickname(): String?
    
    // ==================== ELIMINACIÓN ====================
    
    @Query("DELETE FROM user_settings")
    suspend fun deleteAll()
    
    // ==================== VERIFICACIÓN ====================
    
    @Query("SELECT COUNT(*) FROM user_settings WHERE id = 1")
    suspend fun exists(): Int
}
