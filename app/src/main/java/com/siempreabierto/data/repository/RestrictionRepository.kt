package com.siempreabierto.data.repository

import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.dao.RestrictionDao
import com.siempreabierto.data.dao.ContributionDao
import com.siempreabierto.data.entities.*
import com.siempreabierto.utils.LocationUtils
import com.siempreabierto.utils.BoundingBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio para gestionar puntos de restricción
 * Alturas, anchos, pesos máximos en carreteras
 */
class RestrictionRepository(
    private val restrictionDao: RestrictionDao = DatabaseProvider.restrictionDao(),
    private val contributionDao: ContributionDao = DatabaseProvider.contributionDao()
) {

    // ==================== CONSULTAS BÁSICAS ====================

    /**
     * Obtener todas las restricciones activas
     */
    fun getAllRestrictions(): Flow<List<Restriction>> {
        return restrictionDao.getAllActive()
    }

    /**
     * Obtener restricción por ID
     */
    suspend fun getRestrictionById(id: Long): Restriction? {
        return restrictionDao.getById(id)
    }

    /**
     * Obtener restricción por ID como Flow
     */
    fun getRestrictionByIdFlow(id: Long): Flow<Restriction?> {
        return restrictionDao.getByIdFlow(id)
    }

    // ==================== CONSULTAS POR TIPO ====================

    /**
     * Obtener restricciones por tipo
     */
    fun getRestrictionsByType(type: String): Flow<List<Restriction>> {
        return restrictionDao.getByType(type)
    }

    /**
     * Obtener restricciones de altura
     */
    fun getHeightRestrictions(): Flow<List<Restriction>> {
        return restrictionDao.getAllHeightRestrictions()
    }

    /**
     * Obtener restricciones de peso
     */
    fun getWeightRestrictions(): Flow<List<Restriction>> {
        return restrictionDao.getAllWeightRestrictions()
    }

    /**
     * Obtener restricciones de ancho
     */
    fun getWidthRestrictions(): Flow<List<Restriction>> {
        return restrictionDao.getAllWidthRestrictions()
    }

    // ==================== CONSULTAS POR UBICACIÓN ====================

    /**
     * Obtener restricciones en un área
     */
    fun getRestrictionsInArea(boundingBox: BoundingBox): Flow<List<Restriction>> {
        return restrictionDao.getInBoundingBox(
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        )
    }

    /**
     * Obtener restricciones cercanas a una ubicación
     */
    fun getNearbyRestrictions(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<Restriction>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        return getRestrictionsInArea(boundingBox)
    }

    /**
     * Obtener restricciones cercanas ordenadas por distancia
     */
    fun getNearbyRestrictionsSorted(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<RestrictionWithDistance>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        
        return restrictionDao.getInBoundingBox(
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        ).map { restrictions ->
            restrictions.map { restriction ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    restriction.latitude, restriction.longitude
                )
                RestrictionWithDistance(restriction, distance)
            }.filter { it.distance <= radiusKm }
             .sortedBy { it.distance }
        }
    }

    // ==================== CONSULTAS PARA VEHÍCULOS ====================

    /**
     * Obtener restricciones que afectan a un vehículo específico
     */
    fun getRestrictionsForVehicle(
        height: Double,
        width: Double,
        weight: Double
    ): Flow<List<Restriction>> {
        return restrictionDao.getRestrictionsForVehicle(height, width, weight)
    }

    /**
     * Obtener restricciones que afectan a un vehículo en una región
     */
    fun getRestrictionsForVehicleInRegion(
        height: Double,
        width: Double,
        weight: Double,
        region: String
    ): Flow<List<Restriction>> {
        return restrictionDao.getRestrictionsForVehicleInRegion(height, width, weight, region)
    }

    /**
     * Obtener restricciones de altura en un área para un vehículo
     */
    fun getHeightRestrictionsInArea(
        vehicleHeight: Double,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<RestrictionWithDistance>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        
        return restrictionDao.getHeightRestrictionsInArea(
            vehicleHeight,
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        ).map { restrictions ->
            restrictions.map { restriction ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    restriction.latitude, restriction.longitude
                )
                RestrictionWithDistance(restriction, distance)
            }.sortedBy { it.distance }
        }
    }

    /**
     * Obtener restricciones de peso en un área para un vehículo
     */
    fun getWeightRestrictionsInArea(
        vehicleWeight: Double,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<RestrictionWithDistance>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        
        return restrictionDao.getWeightRestrictionsInArea(
            vehicleWeight,
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        ).map { restrictions ->
            restrictions.map { restriction ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    restriction.latitude, restriction.longitude
                )
                RestrictionWithDistance(restriction, distance)
            }.sortedBy { it.distance }
        }
    }

    /**
     * Verificar si hay restricciones que afecten a un vehículo en un área
     */
    fun checkRestrictionsForVehicle(
        vehicleHeight: Double?,
        vehicleWidth: Double?,
        vehicleWeight: Double?,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Flow<List<RestrictionAlert>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        
        return restrictionDao.getInBoundingBox(
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        ).map { restrictions ->
            restrictions.mapNotNull { restriction ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    restriction.latitude, restriction.longitude
                )
                
                // Verificar si afecta al vehículo
                val alerts = mutableListOf<String>()
                
                vehicleHeight?.let { vh ->
                    restriction.maxHeight?.let { maxH ->
                        if (vh > maxH) {
                            alerts.add("Altura: ${vh}m > máx ${maxH}m")
                        }
                    }
                }
                
                vehicleWidth?.let { vw ->
                    restriction.maxWidth?.let { maxW ->
                        if (vw > maxW) {
                            alerts.add("Ancho: ${vw}m > máx ${maxW}m")
                        }
                    }
                }
                
                vehicleWeight?.let { vwt ->
                    restriction.maxWeight?.let { maxWt ->
                        if (vwt > maxWt) {
                            alerts.add("Peso: ${vwt}t > máx ${maxWt}t")
                        }
                    }
                }
                
                if (alerts.isNotEmpty()) {
                    RestrictionAlert(
                        restriction = restriction,
                        distance = distance,
                        alerts = alerts
                    )
                } else null
            }.sortedBy { it.distance }
        }
    }

    // ==================== CONSULTAS POR CARRETERA ====================

    /**
     * Obtener restricciones en una carretera
     */
    fun getRestrictionsByRoad(roadName: String): Flow<List<Restriction>> {
        return restrictionDao.getByRoad(roadName)
    }

    /**
     * Buscar restricciones por carretera
     */
    fun searchRestrictionsByRoad(query: String): Flow<List<Restriction>> {
        return restrictionDao.searchByRoad(query)
    }

    // ==================== BÚSQUEDA ====================

    /**
     * Buscar restricciones por texto
     */
    fun searchRestrictions(query: String, limit: Int = 50): Flow<List<Restriction>> {
        return restrictionDao.search(query, limit)
    }

    // ==================== CREAR / ACTUALIZAR ====================

    /**
     * Añadir una nueva restricción
     */
    suspend fun addRestriction(
        restriction: Restriction,
        userId: String,
        userName: String? = null
    ): Long {
        // Insertar restricción
        val restrictionId = restrictionDao.insert(restriction.copy(
            contributedBy = userId,
            source = "community",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ))

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.RESTRICTION,
            targetId = restrictionId,
            targetName = restriction.name,
            action = ContributionActions.CREATE,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)

        return restrictionId
    }

    /**
     * Actualizar una restricción existente
     */
    suspend fun updateRestriction(
        restriction: Restriction,
        userId: String,
        userName: String? = null,
        fieldChanged: String? = null,
        oldValue: String? = null,
        newValue: String? = null
    ) {
        // Actualizar restricción
        restrictionDao.update(restriction.copy(
            updatedAt = System.currentTimeMillis()
        ))

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.RESTRICTION,
            targetId = restriction.id,
            targetName = restriction.name,
            action = ContributionActions.UPDATE,
            fieldChanged = fieldChanged,
            oldValue = oldValue,
            newValue = newValue,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    /**
     * Confirmar que una restricción sigue vigente
     */
    suspend fun confirmRestriction(
        restrictionId: Long,
        restrictionName: String,
        userId: String,
        userName: String? = null
    ) {
        // Actualizar confirmación
        restrictionDao.confirmRestriction(restrictionId, userId)

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.RESTRICTION,
            targetId = restrictionId,
            targetName = restrictionName,
            action = ContributionActions.CONFIRM,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    /**
     * Reportar un problema con una restricción
     */
    suspend fun reportRestriction(
        restrictionId: Long,
        restrictionName: String,
        reason: String,
        userId: String,
        userName: String? = null
    ) {
        // Incrementar contador de reportes
        restrictionDao.incrementReportCount(restrictionId)

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.RESTRICTION,
            targetId = restrictionId,
            targetName = restrictionName,
            action = ContributionActions.REPORT,
            notes = reason,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    /**
     * Desactivar una restricción
     */
    suspend fun deactivateRestriction(
        restrictionId: Long,
        restrictionName: String,
        userId: String,
        userName: String? = null,
        reason: String? = null
    ) {
        // Desactivar restricción
        restrictionDao.setActive(restrictionId, false)

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.RESTRICTION,
            targetId = restrictionId,
            targetName = restrictionName,
            action = ContributionActions.DELETE,
            notes = reason,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    // ==================== ESTADÍSTICAS ====================

    /**
     * Contar restricciones activas
     */
    suspend fun countActiveRestrictions(): Int {
        return restrictionDao.countActive()
    }

    /**
     * Contar restricciones por tipo
     */
    suspend fun countRestrictionsByType(type: String): Int {
        return restrictionDao.countByType(type)
    }

    /**
     * Contar restricciones por región
     */
    suspend fun countRestrictionsByRegion(region: String): Int {
        return restrictionDao.countByRegion(region)
    }

    /**
     * Obtener todas las carreteras con restricciones
     */
    suspend fun getAllRoadsWithRestrictions(): List<String> {
        return restrictionDao.getAllRoadsWithRestrictions()
    }

    // ==================== RESTRICCIONES RECIENTES ====================

    /**
     * Obtener restricciones añadidas recientemente
     */
    fun getRecentlyAddedRestrictions(limit: Int = 20): Flow<List<Restriction>> {
        return restrictionDao.getRecentlyAdded(limit)
    }

    /**
     * Obtener restricciones actualizadas recientemente
     */
    fun getRecentlyUpdatedRestrictions(limit: Int = 20): Flow<List<Restriction>> {
        return restrictionDao.getRecentlyUpdated(limit)
    }

    /**
     * Obtener restricciones de un contribuidor
     */
    fun getRestrictionsByContributor(userId: String): Flow<List<Restriction>> {
        return restrictionDao.getByContributor(userId)
    }

    // ==================== GESTIÓN DE DATOS ====================

    /**
     * Eliminar todas las restricciones de una región
     */
    suspend fun deleteRestrictionsByRegion(region: String) {
        restrictionDao.deleteByRegion(region)
    }

    /**
     * Eliminar todas las restricciones
     */
    suspend fun deleteAllRestrictions() {
        restrictionDao.deleteAll()
    }

    /**
     * Insertar múltiples restricciones (para importación)
     */
    suspend fun insertRestrictions(restrictions: List<Restriction>) {
        restrictionDao.insertAll(restrictions)
    }
}

/**
 * Restricción con distancia calculada
 */
data class RestrictionWithDistance(
    val restriction: Restriction,
    val distance: Double  // en kilómetros
) {
    /**
     * Obtener distancia formateada
     */
    fun getFormattedDistance(): String {
        return LocationUtils.formatDistance(distance)
    }

    /**
     * Obtener texto de la restricción
     */
    fun getRestrictionText(): String {
        return restriction.getRestrictionText()
    }
}

/**
 * Alerta de restricción para un vehículo
 */
data class RestrictionAlert(
    val restriction: Restriction,
    val distance: Double,
    val alerts: List<String>
) {
    /**
     * Obtener distancia formateada
     */
    fun getFormattedDistance(): String {
        return LocationUtils.formatDistance(distance)
    }

    /**
     * Obtener mensaje de alerta
     */
    fun getAlertMessage(): String {
        return "⚠️ ${restriction.name}: ${alerts.joinToString(", ")}"
    }

    /**
     * Obtener severidad de la alerta
     */
    fun getSeverity(): AlertSeverity {
        return when {
            distance < 1 -> AlertSeverity.CRITICAL
            distance < 3 -> AlertSeverity.HIGH
            distance < 5 -> AlertSeverity.MEDIUM
            else -> AlertSeverity.LOW
        }
    }
}

/**
 * Severidad de alerta
 */
enum class AlertSeverity {
    LOW,      // > 5km
    MEDIUM,   // 3-5km
    HIGH,     // 1-3km
    CRITICAL  // < 1km
}
```

---

**Copia, pega en GitHub y Commit.**

🎉 **¡FELICIDADES!** 🎉

Ya tienes la estructura completa del proyecto **Siempre Abierto**. 

---

## 📋 RESUMEN DE LO CREADO:

| Categoría | Archivos |
|-----------|----------|
| **Configuración** | `build.gradle`, `settings.gradle`, `gradle.properties`, `.gitignore`, `proguard-rules.pro` |
| **Manifest** | `AndroidManifest.xml` |
| **Recursos** | `strings.xml`, `colors.xml`, `themes.xml`, `network_security_config.xml` |
| **App** | `SiempreAbiertoApp.kt`, `MainActivity.kt` |
| **Theme** | `Color.kt`, `Theme.kt`, `Type.kt` |
| **Navigation** | `AppNavigation.kt` |
| **Screens** | `HomeScreen.kt`, `MapScreen.kt`, `EmergencyScreen.kt`, `CommunityScreen.kt`, `SettingsScreen.kt` |
| **ViewModels** | `HomeViewModel.kt`, `MapViewModel.kt`, `EmergencyViewModel.kt`, `CommunityViewModel.kt`, `SettingsViewModel.kt` |
| **Entities** | `Place.kt`, `Restriction.kt`, `Contribution.kt`, `Helper.kt`, `Route.kt`, `UserSettings.kt` |
| **DAOs** | `PlaceDao.kt`, `RestrictionDao.kt`, `ContributionDao.kt`, `HelperDao.kt`, `RouteDao.kt`, `UserSettingsDao.kt` |
| **Database** | `AppDatabase.kt` |
| **Repositories** | `PlaceRepository.kt`, `RestrictionRepository.kt` |
| **Utils** | `LocationUtils.kt`, `DateUtils.kt`, `Constants.kt` |

---

## 📱 SIGUIENTE PASO:

Ahora solo falta añadir los archivos **.mbtiles** de los mapas a cada módulo. Por ejemplo:
```
app/maps_madrid/src/main/assets/madrid.mbtiles
app/maps_valenciana/src/main/assets/valenciana.mbtiles
