package com.siempreabierto.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import kotlin.math.*

/**
 * Utilidades de ubicación para Siempre Abierto
 * Cálculos offline sin necesidad de APIs externas
 */
object LocationUtils {

    // Radio de la Tierra en kilómetros
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calcular distancia entre dos puntos usando la fórmula de Haversine
     * @return Distancia en kilómetros
     */
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_KM * c
    }

    /**
     * Calcular distancia formateada para mostrar
     */
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 0.1 -> "${(distanceKm * 1000).toInt()} m"
            distanceKm < 1 -> "${(distanceKm * 1000).toInt()} m"
            distanceKm < 10 -> String.format("%.1f km", distanceKm)
            else -> "${distanceKm.toInt()} km"
        }
    }

    /**
     * Calcular tiempo estimado de llegada
     * @param distanceKm Distancia en kilómetros
     * @param speedKmh Velocidad en km/h (por defecto 60 km/h para ciudad)
     * @return Tiempo en minutos
     */
    fun calculateETA(distanceKm: Double, speedKmh: Double = 60.0): Int {
        return ((distanceKm / speedKmh) * 60).toInt()
    }

    /**
     * Formatear tiempo estimado
     */
    fun formatETA(minutes: Int): String {
        return when {
            minutes < 1 -> "< 1 min"
            minutes < 60 -> "$minutes min"
            else -> {
                val hours = minutes / 60
                val mins = minutes % 60
                if (mins == 0) "${hours}h" else "${hours}h ${mins}min"
            }
        }
    }

    /**
     * Calcular bounding box alrededor de un punto
     * @param centerLat Latitud del centro
     * @param centerLon Longitud del centro
     * @param radiusKm Radio en kilómetros
     * @return BoundingBox con min/max lat/lon
     */
    fun calculateBoundingBox(
        centerLat: Double,
        centerLon: Double,
        radiusKm: Double
    ): BoundingBox {
        // Aproximación: 1 grado de latitud ≈ 111 km
        val latDelta = radiusKm / 111.0
        
        // 1 grado de longitud varía según la latitud
        val lonDelta = radiusKm / (111.0 * cos(Math.toRadians(centerLat)))

        return BoundingBox(
            minLat = centerLat - latDelta,
            maxLat = centerLat + latDelta,
            minLon = centerLon - lonDelta,
            maxLon = centerLon + lonDelta
        )
    }

    /**
     * Verificar si un punto está dentro de un bounding box
     */
    fun isPointInBoundingBox(
        lat: Double,
        lon: Double,
        boundingBox: BoundingBox
    ): Boolean {
        return lat >= boundingBox.minLat &&
                lat <= boundingBox.maxLat &&
                lon >= boundingBox.minLon &&
                lon <= boundingBox.maxLon
    }

    /**
     * Calcular el rumbo (bearing) entre dos puntos
     * @return Ángulo en grados (0-360)
     */
    fun calculateBearing(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val y = sin(dLon) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) -
                sin(lat1Rad) * cos(lat2Rad) * cos(dLon)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360

        return bearing
    }

    /**
     * Convertir bearing a dirección cardinal
     */
    fun bearingToCardinal(bearing: Double): String {
        val directions = listOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
        val index = ((bearing + 22.5) / 45).toInt() % 8
        return directions[index]
    }

    /**
     * Convertir bearing a dirección en español
     */
    fun bearingToDirection(bearing: Double): String {
        return when {
            bearing < 22.5 || bearing >= 337.5 -> "Norte"
            bearing < 67.5 -> "Noreste"
            bearing < 112.5 -> "Este"
            bearing < 157.5 -> "Sureste"
            bearing < 202.5 -> "Sur"
            bearing < 247.5 -> "Suroeste"
            bearing < 292.5 -> "Oeste"
            else -> "Noroeste"
        }
    }

    /**
     * Verificar si la app tiene permiso de ubicación
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Obtener la región de España basada en coordenadas
     */
    fun getRegionFromCoordinates(lat: Double, lon: Double): String? {
        // Bounding boxes aproximados de las comunidades autónomas
        return when {
            // Andalucía
            lat in 36.0..38.8 && lon in -7.5..-1.6 -> "andalucia"
            // Aragón
            lat in 39.8..42.9 && lon in -2.2..0.8 -> "aragon"
            // Asturias
            lat in 42.9..43.7 && lon in -7.2..-4.5 -> "asturias"
            // Baleares
            lat in 38.6..40.1 && lon in 1.2..4.4 -> "baleares"
            // Canarias
            lat in 27.6..29.5 && lon in -18.2..-13.3 -> "canarias"
            // Cantabria
            lat in 42.8..43.5 && lon in -4.9..-3.1 -> "cantabria"
            // Castilla-La Mancha
            lat in 38.4..41.2 && lon in -5.4..-0.9 -> "castilla_la_mancha"
            // Castilla y León
            lat in 40.1..43.2 && lon in -7.1..-1.8 -> "castilla_y_leon"
            // Cataluña
            lat in 40.5..42.9 && lon in 0.2..3.3 -> "cataluna"
            // Extremadura
            lat in 38.0..40.5 && lon in -7.5..-4.6 -> "extremadura"
            // Galicia
            lat in 41.8..43.8 && lon in -9.3..-6.7 -> "galicia"
            // La Rioja
            lat in 41.9..42.6 && lon in -3.1..-1.7 -> "la_rioja"
            // Madrid
            lat in 39.9..41.2 && lon in -4.6..-3.1 -> "madrid"
            // Murcia
            lat in 37.4..38.8 && lon in -2.4..-0.6 -> "murcia"
            // Navarra
            lat in 41.9..43.3 && lon in -2.5..-0.7 -> "navarra"
            // País Vasco
            lat in 42.4..43.5 && lon in -3.5..-1.7 -> "pais_vasco"
            // Comunidad Valenciana
            lat in 37.8..40.8 && lon in -1.5..0.6 -> "valenciana"
            else -> null
        }
    }

    /**
     * Ordenar lista de elementos por distancia
     */
    fun <T> sortByDistance(
        items: List<T>,
        userLat: Double,
        userLon: Double,
        getLatitude: (T) -> Double,
        getLongitude: (T) -> Double
    ): List<T> {
        return items.sortedBy { item ->
            calculateDistance(
                userLat, userLon,
                getLatitude(item), getLongitude(item)
            )
        }
    }

    /**
     * Filtrar elementos dentro de un radio
     */
    fun <T> filterByRadius(
        items: List<T>,
        centerLat: Double,
        centerLon: Double,
        radiusKm: Double,
        getLatitude: (T) -> Double,
        getLongitude: (T) -> Double
    ): List<T> {
        return items.filter { item ->
            calculateDistance(
                centerLat, centerLon,
                getLatitude(item), getLongitude(item)
            ) <= radiusKm
        }
    }
}

/**
 * Bounding Box para búsquedas por área
 */
data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
) {
    /**
     * Obtener el centro del bounding box
     */
    fun getCenter(): Pair<Double, Double> {
        return Pair(
            (minLat + maxLat) / 2,
            (minLon + maxLon) / 2
        )
    }

    /**
     * Expandir el bounding box
     */
    fun expand(factor: Double): BoundingBox {
        val latDelta = (maxLat - minLat) * (factor - 1) / 2
        val lonDelta = (maxLon - minLon) * (factor - 1) / 2

        return BoundingBox(
            minLat = minLat - latDelta,
            maxLat = maxLat + latDelta,
            minLon = minLon - lonDelta,
            maxLon = maxLon + lonDelta
        )
    }
}

/**
 * Extensión para Location de Android
 */
fun Location.distanceTo(lat: Double, lon: Double): Double {
    return LocationUtils.calculateDistance(
        this.latitude, this.longitude,
        lat, lon
    )
}

/**
 * Extensión para formatear distancia desde Location
 */
fun Location.formattedDistanceTo(lat: Double, lon: Double): String {
    return LocationUtils.formatDistance(distanceTo(lat, lon))
}
