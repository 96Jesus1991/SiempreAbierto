package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad UserSettings - Configuración local del usuario
 * 
 * Toda la configuración se guarda LOCALMENTE.
 * Sin login, sin cuenta, sin tracking.
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,                  // Solo hay un registro de settings
    
    // Identificador local (generado automáticamente)
    val localUserId: String,          // UUID único del dispositivo
    val nickname: String? = null,     // Apodo opcional para la comunidad
    
    // Vehículo del usuario
    val vehicleType: String = "car",  // car, truck, bus, camper
    val vehicleHeight: Double? = null, // metros
    val vehicleWidth: Double? = null,  // metros
    val vehicleWeight: Double? = null, // toneladas
    val vehicleLength: Double? = null, // metros
    val vehiclePlate: String? = null,  // Matrícula (opcional, local)
    val vehicleDescription: String? = null,
    
    // Preferencias de visualización
    val darkMode: Boolean = true,
    val mapZoomDefault: Float = 12f,
    val showRestrictionsOnMap: Boolean = true,
    val showHelpersOnMap: Boolean = true,
    val distanceUnit: String = "km",   // km, mi
    
    // Preferencias de rutas
    val avoidTolls: Boolean = false,
    val avoidHighways: Boolean = false,
    val preferTruckRoutes: Boolean = false,
    
    // Preferencias de notificaciones (locales)
    val notifyRestrictions: Boolean = true,
    val notifyNearbyHelpers: Boolean = false,
    val restrictionAlertDistance: Int = 5, // km antes de restricción
    
    // Ayudante comunitario
    val isHelper: Boolean = false,
    val helperProfileId: Long? = null,
    
    // Regiones descargadas (JSON array)
    val downloadedRegions: String = "[]",
    
    // Estadísticas locales
    val totalContributions: Int = 0,
    val placesAdded: Int = 0,
    val confirmationsMade: Int = 0,
    val helpProvided: Int = 0,
    
    // Sincronización
    val lastSyncAt: Long? = null,
    val autoSync: Boolean = false,    // Sincronizar cuando hay WiFi
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Legal
    val acceptedTermsAt: Long? = null,
    val acceptedPrivacyAt: Long? = null
)

/**
 * Tipos de vehículo del usuario
 */
object UserVehicleTypes {
    const val CAR = "car"
    const val VAN = "van"
    const val TRUCK_SMALL = "truck_small"   // Camión pequeño < 3.5t
    const val TRUCK_MEDIUM = "truck_medium" // Camión mediano 3.5-12t
    const val TRUCK_LARGE = "truck_large"   // Camión grande > 12t
    const val TRUCK_TRAILER = "truck_trailer" // Tráiler
    const val BUS = "bus"
    const val BUS_LARGE = "bus_large"       // Autocar grande
    const val CAMPER = "camper"
    const val CAMPER_LARGE = "camper_large" // Autocaravana grande
    const val MOTORCYCLE = "motorcycle"
    
    fun getDisplayName(type: String): String {
        return when (type) {
            CAR -> "Coche"
            VAN -> "Furgoneta"
            TRUCK_SMALL -> "Camión pequeño (<3.5t)"
            TRUCK_MEDIUM -> "Camión mediano (3.5-12t)"
            TRUCK_LARGE -> "Camión grande (>12t)"
            TRUCK_TRAILER -> "Tráiler"
            BUS -> "Autobús"
            BUS_LARGE -> "Autocar grande"
            CAMPER -> "Camper/Autocaravana"
            CAMPER_LARGE -> "Autocaravana grande"
            MOTORCYCLE -> "Moto"
            else -> type
        }
    }
    
    fun needsRestrictionAlerts(type: String): Boolean {
        return type in listOf(
            TRUCK_SMALL, TRUCK_MEDIUM, TRUCK_LARGE, TRUCK_TRAILER,
            BUS, BUS_LARGE, CAMPER, CAMPER_LARGE
        )
    }
}

/**
 * Unidades de distancia
 */
object DistanceUnits {
    const val KILOMETERS = "km"
    const val MILES = "mi"
}

/**
 * Perfiles predefinidos de vehículos
 */
data class VehicleProfile(
    val type: String,
    val name: String,
    val defaultHeight: Double?,
    val defaultWidth: Double?,
    val defaultWeight: Double?,
    val defaultLength: Double?
)

val defaultVehicleProfiles = listOf(
    VehicleProfile(UserVehicleTypes.CAR, "Coche", 1.5, 1.8, 2.0, 4.5),
    VehicleProfile(UserVehicleTypes.VAN, "Furgoneta", 2.2, 2.0, 3.0, 5.5),
    VehicleProfile(UserVehicleTypes.TRUCK_SMALL, "Camión pequeño", 2.8, 2.2, 3.5, 6.0),
    VehicleProfile(UserVehicleTypes.TRUCK_MEDIUM, "Camión mediano", 3.2, 2.5, 12.0, 8.0),
    VehicleProfile(UserVehicleTypes.TRUCK_LARGE, "Camión grande", 4.0, 2.5, 26.0, 12.0),
    VehicleProfile(UserVehicleTypes.TRUCK_TRAILER, "Tráiler", 4.0, 2.55, 40.0, 16.5),
    VehicleProfile(UserVehicleTypes.BUS, "Autobús", 3.2, 2.5, 18.0, 12.0),
    VehicleProfile(UserVehicleTypes.BUS_LARGE, "Autocar grande", 3.8, 2.55, 24.0, 15.0),
    VehicleProfile(UserVehicleTypes.CAMPER, "Camper", 2.8, 2.2, 3.5, 6.5),
    VehicleProfile(UserVehicleTypes.CAMPER_LARGE, "Autocaravana grande", 3.2, 2.35, 5.0, 8.0)
)

/**
 * Extensión para verificar si necesita alertas de restricción
 */
fun UserSettings.needsRestrictionAlerts(): Boolean {
    return UserVehicleTypes.needsRestrictionAlerts(vehicleType)
}

/**
 * Extensión para obtener regiones descargadas como lista
 */
fun UserSettings.getDownloadedRegionsList(): List<String> {
    return try {
        downloadedRegions
            .removeSurrounding("[", "]")
            .split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Extensión para verificar si tiene una región descargada
 */
fun UserSettings.hasRegionDownloaded(region: String): Boolean {
    return getDownloadedRegionsList().contains(region)
}
