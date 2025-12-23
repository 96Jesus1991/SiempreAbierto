package com.siempreabierto.data.repository

import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.dao.PlaceDao
import com.siempreabierto.data.dao.ContributionDao
import com.siempreabierto.data.entities.*
import com.siempreabierto.utils.LocationUtils
import com.siempreabierto.utils.BoundingBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio para gestionar lugares de la comunidad
 * Capa de abstracción entre ViewModels y DAOs
 */
class PlaceRepository(
    private val placeDao: PlaceDao = DatabaseProvider.placeDao(),
    private val contributionDao: ContributionDao = DatabaseProvider.contributionDao()
) {

    // ==================== CONSULTAS BÁSICAS ====================

    /**
     * Obtener todos los lugares activos
     */
    fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAllActive()
    }

    /**
     * Obtener lugar por ID
     */
    suspend fun getPlaceById(id: Long): Place? {
        return placeDao.getById(id)
    }

    /**
     * Obtener lugar por ID como Flow
     */
    fun getPlaceByIdFlow(id: Long): Flow<Place?> {
        return placeDao.getByIdFlow(id)
    }

    // ==================== CONSULTAS POR CATEGORÍA ====================

    /**
     * Obtener lugares por categoría
     */
    fun getPlacesByCategory(category: String): Flow<List<Place>> {
        return placeDao.getByCategory(category)
    }

    /**
     * Obtener lugares por categoría y región
     */
    fun getPlacesByCategoryAndRegion(category: String, region: String): Flow<List<Place>> {
        return placeDao.getByCategoryAndRegion(category, region)
    }

    /**
     * Obtener lugares 24h
     */
    fun get24hPlaces(): Flow<List<Place>> {
        return placeDao.get24hPlaces()
    }

    /**
     * Obtener lugares 24h por categoría
     */
    fun get24hPlacesByCategory(category: String): Flow<List<Place>> {
        return placeDao.get24hByCategory(category)
    }

    // ==================== CONSULTAS POR UBICACIÓN ====================

    /**
     * Obtener lugares en un área
     */
    fun getPlacesInArea(boundingBox: BoundingBox): Flow<List<Place>> {
        return placeDao.getInBoundingBox(
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        )
    }

    /**
     * Obtener lugares cercanos a una ubicación
     */
    fun getNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<Place>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        return getPlacesInArea(boundingBox)
    }

    /**
     * Obtener lugares cercanos ordenados por distancia
     */
    fun getNearbyPlacesSorted(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<PlaceWithDistance>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        
        return placeDao.getInBoundingBox(
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        ).map { places ->
            places.map { place ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    place.latitude, place.longitude
                )
                PlaceWithDistance(place, distance)
            }.filter { it.distance <= radiusKm }
             .sortedBy { it.distance }
        }
    }

    /**
     * Obtener lugares cercanos por categoría
     */
    fun getNearbyPlacesByCategory(
        category: String,
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<PlaceWithDistance>> {
        val boundingBox = LocationUtils.calculateBoundingBox(latitude, longitude, radiusKm)
        
        return placeDao.getInBoundingBoxByCategory(
            category,
            boundingBox.minLat,
            boundingBox.maxLat,
            boundingBox.minLon,
            boundingBox.maxLon
        ).map { places ->
            places.map { place ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    place.latitude, place.longitude
                )
                PlaceWithDistance(place, distance)
            }.filter { it.distance <= radiusKm }
             .sortedBy { it.distance }
        }
    }

    // ==================== CONSULTAS PARA VEHÍCULOS GRANDES ====================

    /**
     * Obtener lugares aptos para camiones
     */
    fun getTruckFriendlyPlaces(): Flow<List<Place>> {
        return placeDao.getTruckFriendly()
    }

    /**
     * Obtener lugares aptos para autobuses
     */
    fun getBusFriendlyPlaces(): Flow<List<Place>> {
        return placeDao.getBusFriendly()
    }

    /**
     * Obtener lugares aptos para tráilers
     */
    fun getTrailerFriendlyPlaces(): Flow<List<Place>> {
        return placeDao.getTrailerFriendly()
    }

    /**
     * Obtener lugares cercanos aptos para camiones
     */
    fun getNearbyTruckFriendlyPlaces(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Flow<List<PlaceWithDistance>> {
        return getTruckFriendlyPlaces().map { places ->
            places.map { place ->
                val distance = LocationUtils.calculateDistance(
                    latitude, longitude,
                    place.latitude, place.longitude
                )
                PlaceWithDistance(place, distance)
            }.filter { it.distance <= radiusKm }
             .sortedBy { it.distance }
        }
    }

    // ==================== BÚSQUEDA ====================

    /**
     * Buscar lugares por texto
     */
    fun searchPlaces(query: String, limit: Int = 50): Flow<List<Place>> {
        return placeDao.search(query, limit)
    }

    /**
     * Buscar lugares por texto y categoría
     */
    fun searchPlacesByCategory(
        query: String,
        category: String,
        limit: Int = 50
    ): Flow<List<Place>> {
        return placeDao.searchByCategory(query, category, limit)
    }

    // ==================== CREAR / ACTUALIZAR ====================

    /**
     * Añadir un nuevo lugar
     */
    suspend fun addPlace(
        place: Place,
        userId: String,
        userName: String? = null
    ): Long {
        // Insertar lugar
        val placeId = placeDao.insert(place.copy(
            contributedBy = userId,
            source = "community",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ))

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.PLACE,
            targetId = placeId,
            targetName = place.name,
            action = ContributionActions.CREATE,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)

        return placeId
    }

    /**
     * Actualizar un lugar existente
     */
    suspend fun updatePlace(
        place: Place,
        userId: String,
        userName: String? = null,
        fieldChanged: String? = null,
        oldValue: String? = null,
        newValue: String? = null
    ) {
        // Actualizar lugar
        placeDao.update(place.copy(
            updatedAt = System.currentTimeMillis()
        ))

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.PLACE,
            targetId = place.id,
            targetName = place.name,
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
     * Confirmar que un lugar sigue activo
     */
    suspend fun confirmPlace(
        placeId: Long,
        placeName: String,
        userId: String,
        userName: String? = null
    ) {
        // Actualizar confirmación
        placeDao.confirmPlace(placeId, userId)

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.PLACE,
            targetId = placeId,
            targetName = placeName,
            action = ContributionActions.CONFIRM,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    /**
     * Reportar un problema con un lugar
     */
    suspend fun reportPlace(
        placeId: Long,
        placeName: String,
        reason: String,
        userId: String,
        userName: String? = null
    ) {
        // Incrementar contador de reportes
        placeDao.incrementReportCount(placeId)

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.PLACE,
            targetId = placeId,
            targetName = placeName,
            action = ContributionActions.REPORT,
            notes = reason,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    /**
     * Desactivar un lugar (marcar como cerrado)
     */
    suspend fun deactivatePlace(
        placeId: Long,
        placeName: String,
        userId: String,
        userName: String? = null,
        reason: String? = null
    ) {
        // Desactivar lugar
        placeDao.setActive(placeId, false)

        // Registrar contribución
        val contribution = Contribution(
            targetType = ContributionTargets.PLACE,
            targetId = placeId,
            targetName = placeName,
            action = ContributionActions.DELETE,
            notes = reason,
            userId = userId,
            userName = userName
        )
        contributionDao.insert(contribution)
    }

    // ==================== ESTADÍSTICAS ====================

    /**
     * Contar lugares activos
     */
    suspend fun countActivePlaces(): Int {
        return placeDao.countActive()
    }

    /**
     * Contar lugares por categoría
     */
    suspend fun countPlacesByCategory(category: String): Int {
        return placeDao.countByCategory(category)
    }

    /**
     * Contar lugares por región
     */
    suspend fun countPlacesByRegion(region: String): Int {
        return placeDao.countByRegion(region)
    }

    /**
     * Contar lugares añadidos por un usuario
     */
    suspend fun countPlacesByContributor(userId: String): Int {
        return placeDao.countByContributor(userId)
    }

    /**
     * Obtener todas las categorías con lugares
     */
    suspend fun getAllCategoriesWithPlaces(): List<String> {
        return placeDao.getAllCategories()
    }

    /**
     * Obtener todas las regiones con lugares
     */
    suspend fun getAllRegionsWithPlaces(): List<String> {
        return placeDao.getAllRegionsWithPlaces()
    }

    // ==================== LUGARES RECIENTES ====================

    /**
     * Obtener lugares añadidos recientemente
     */
    fun getRecentlyAddedPlaces(limit: Int = 20): Flow<List<Place>> {
        return placeDao.getRecentlyAdded(limit)
    }

    /**
     * Obtener lugares actualizados recientemente
     */
    fun getRecentlyUpdatedPlaces(limit: Int = 20): Flow<List<Place>> {
        return placeDao.getRecentlyUpdated(limit)
    }

    /**
     * Obtener lugares de un contribuidor
     */
    fun getPlacesByContributor(userId: String): Flow<List<Place>> {
        return placeDao.getByContributor(userId)
    }

    // ==================== GESTIÓN DE DATOS ====================

    /**
     * Eliminar todos los lugares de una región
     */
    suspend fun deletePlacesByRegion(region: String) {
        placeDao.deleteByRegion(region)
    }

    /**
     * Eliminar todos los lugares
     */
    suspend fun deleteAllPlaces() {
        placeDao.deleteAll()
    }

    /**
     * Insertar múltiples lugares (para importación)
     */
    suspend fun insertPlaces(places: List<Place>) {
        placeDao.insertAll(places)
    }
}

/**
 * Lugar con distancia calculada
 */
data class PlaceWithDistance(
    val place: Place,
    val distance: Double  // en kilómetros
) {
    /**
     * Obtener distancia formateada
     */
    fun getFormattedDistance(): String {
        return LocationUtils.formatDistance(distance)
    }

    /**
     * Obtener tiempo estimado de llegada
     */
    fun getETA(speedKmh: Double = 60.0): String {
        val minutes = LocationUtils.calculateETA(distance, speedKmh)
        return LocationUtils.formatETA(minutes)
    }
}
