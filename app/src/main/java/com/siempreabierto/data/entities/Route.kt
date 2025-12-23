package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Route - Rutas guardadas y recomendadas
 * 
 * Rutas offline para viajeros, camioneros y conductores de bus.
 * Incluye rutas recomendadas por la comunidad.
 */
@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Información básica
    val name: String,
    val description: String? = null,
    
    // Puntos de ruta (JSON con lista de coordenadas)
    val waypoints: String,            // JSON: [{lat, lon, name?}, ...]
    
    // Origen y destino
    val originName: String,
    val originLat: Double,
    val originLon: Double,
    val destinationName: String,
    val destinationLat: Double,
    val destinationLon: Double,
    
    // Información de la ruta
    val distanceKm: Double,
    val estimatedMinutes: Int,        // Sin tráfico
    val tollRoads: Boolean = false,   // Tiene peajes
    val tollCost: Double? = null,     // Coste estimado peajes
    
    // Tipo de vehículo recomendado
    val vehicleType: String,          // car, truck, bus, camper, any
    val suitableForTruck: Boolean = true,
    val suitableForBus: Boolean = true,
    val suitableForCamper: Boolean = true,
    
    // Restricciones en la ruta
    val hasHeightRestrictions: Boolean = false,
    val minHeightOnRoute: Double? = null,
    val hasWeightRestrictions: Boolean = false,
    val maxWeightOnRoute: Double? = null,
    val restrictionNotes: String? = null,
    
    // Puntos de interés en la ruta (JSON)
    val pointsOfInterest: String? = null, // JSON con lugares útiles
    
    // Comunidad
    val isPublic: Boolean = false,    // Compartida con la comunidad
    val isCommunityRecommended: Boolean = false,
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val communityNotes: String? = null,
    
    // Trazabilidad
    val contributedBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Estado
    val isFavorite: Boolean = false,
    val isActive: Boolean = true,
    val usageCount: Int = 0,          // Veces que se ha usado
    val lastUsedAt: Long? = null
)

/**
 * Tipos de vehículo para rutas
 */
object RouteVehicleTypes {
    const val CAR = "car"
    const val TRUCK = "truck"
    const val BUS = "bus"
    const val CAMPER = "camper"
    const val ANY = "any"
}

/**
 * Waypoint de una ruta
 */
data class Waypoint(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val isStop: Boolean = false,      // Es una parada planificada
    val stopDurationMinutes: Int? = null
)

/**
 * Punto de interés en ruta
 */
data class RoutePointOfInterest(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val type: String,                 // gas_station, rest_area, parking, etc.
    val distanceFromStartKm: Double,
    val notes: String? = null
)

/**
 * Resumen de ruta para mostrar en listas
 */
data class RouteSummary(
    val id: Long,
    val name: String,
    val originName: String,
    val destinationName: String,
    val distanceKm: Double,
    val estimatedMinutes: Int,
    val vehicleType: String,
    val isFavorite: Boolean,
    val isCommunityRecommended: Boolean
)

/**
 * Filtros para buscar rutas
 */
data class RouteFilter(
    val vehicleType: String? = null,
    val maxDistanceKm: Double? = null,
    val avoidTolls: Boolean = false,
    val suitableForTruck: Boolean? = null,
    val suitableForBus: Boolean? = null,
    val onlyFavorites: Boolean = false,
    val onlyCommunityRecommended: Boolean = false,
    val region: String? = null
)

/**
 * Extensión para calcular tiempo formateado
 */
fun Route.getFormattedDuration(): String {
    val hours = estimatedMinutes / 60
    val minutes = estimatedMinutes % 60
    
    return when {
        hours == 0 -> "${minutes}min"
        minutes == 0 -> "${hours}h"
        else -> "${hours}h ${minutes}min"
    }
}

/**
 * Extensión para calcular distancia formateada
 */
fun Route.getFormattedDistance(): String {
    return if (distanceKm < 1) {
        "${(distanceKm * 1000).toInt()}m"
    } else {
        "${String.format("%.1f", distanceKm)}km"
    }
}

/**
 * Extensión para obtener icono de vehículo
 */
fun Route.getVehicleTypeDisplay(): String {
    return when (vehicleType) {
        RouteVehicleTypes.CAR -> "🚗 Coche"
        RouteVehicleTypes.TRUCK -> "🚛 Camión"
        RouteVehicleTypes.BUS -> "🚌 Autobús"
        RouteVehicleTypes.CAMPER -> "🚐 Camper"
        RouteVehicleTypes.ANY -> "🚗 Todos"
        else -> vehicleType
    }
}

/**
 * Extensión para verificar si es apta para un vehículo
 */
fun Route.isSuitableFor(vehicleType: String): Boolean {
    return when (vehicleType) {
        RouteVehicleTypes.CAR -> true // Siempre apto para coche
        RouteVehicleTypes.TRUCK -> suitableForTruck
        RouteVehicleTypes.BUS -> suitableForBus
        RouteVehicleTypes.CAMPER -> suitableForCamper
        else -> true
    }
}
