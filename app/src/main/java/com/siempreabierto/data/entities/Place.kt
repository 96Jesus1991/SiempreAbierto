package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Place - Lugares de la comunidad
 * 
 * Talleres, gasolineras, parkings, lavaderos, etc.
 * Toda la información es aportada por usuarios.
 */
@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Información básica
    val name: String,
    val category: String,           // workshop, gas_station, parking, etc.
    val subcategory: String? = null, // workshop_24h, truck_wash, etc.
    
    // Ubicación
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val city: String? = null,
    val province: String? = null,
    val region: String,             // Comunidad autónoma
    
    // Contacto
    val phone: String? = null,
    val phone2: String? = null,
    
    // Horarios (orientativos)
    val schedule: String? = null,    // "L-V: 8-20h, S: 9-14h"
    val is24h: Boolean = false,
    val usuallyOpen: String? = null, // "suele abrir temprano"
    
    // Información para vehículos grandes
    val fitsTrailer: Boolean? = null,
    val fitsBus: Boolean? = null,
    val fitsTruck: Boolean? = null,
    val maxHeight: Double? = null,   // metros
    val maxWidth: Double? = null,    // metros
    val maxWeight: Double? = null,   // toneladas
    
    // Precios y servicios
    val priceRange: String? = null,  // "€", "€€", "€€€" o precio aprox
    val services: String? = null,    // Lista de servicios separados por coma
    val notes: String? = null,       // Notas adicionales de la comunidad
    
    // Trazabilidad (OBLIGATORIO)
    val contributedBy: String,       // ID o nombre del contribuidor
    val source: String = "community", // community, business, import
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastConfirmedAt: Long? = null,
    val lastConfirmedBy: String? = null,
    
    // Estado
    val isVerified: Boolean = false,
    val isActive: Boolean = true,
    val reportCount: Int = 0
)

/**
 * Categorías de lugares
 */
object PlaceCategories {
    const val WORKSHOP = "workshop"
    const val WORKSHOP_24H = "workshop_24h"
    const val TOW_TRUCK = "tow_truck"
    const val GAS_STATION = "gas_station"
    const val CAR_WASH = "car_wash"
    const val TRUCK_WASH = "truck_wash"
    const val VACUUM = "vacuum"
    const val PARKING = "parking"
    const val TRUCK_PARKING = "truck_parking"
    const val REST_AREA = "rest_area"
    const val SHOWERS = "showers"
    const val SUPERMARKET = "supermarket"
    const val SUPERMARKET_24H = "supermarket_24h"
    const val BAR = "bar"
    const val CAFE = "cafe"
    const val RESTAURANT = "restaurant"
}

/**
 * Regiones de España
 */
object Regions {
    const val ANDALUCIA = "andalucia"
    const val ARAGON = "aragon"
    const val ASTURIAS = "asturias"
    const val BALEARES = "baleares"
    const val CANARIAS = "canarias"
    const val CANTABRIA = "cantabria"
    const val CASTILLA_LA_MANCHA = "castilla_la_mancha"
    const val CASTILLA_Y_LEON = "castilla_y_leon"
    const val CATALUNA = "cataluna"
    const val EXTREMADURA = "extremadura"
    const val GALICIA = "galicia"
    const val LA_RIOJA = "la_rioja"
    const val MADRID = "madrid"
    const val MURCIA = "murcia"
    const val NAVARRA = "navarra"
    const val PAIS_VASCO = "pais_vasco"
    const val VALENCIANA = "valenciana"
    
    val ALL = listOf(
        ANDALUCIA, ARAGON, ASTURIAS, BALEARES, CANARIAS,
        CANTABRIA, CASTILLA_LA_MANCHA, CASTILLA_Y_LEON,
        CATALUNA, EXTREMADURA, GALICIA, LA_RIOJA, MADRID,
        MURCIA, NAVARRA, PAIS_VASCO, VALENCIANA
    )
}
