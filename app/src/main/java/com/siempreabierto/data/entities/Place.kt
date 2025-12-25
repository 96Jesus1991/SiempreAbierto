package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val type: String,          // gas_station, parking, workshop, restaurant, etc.
    val lat: Double,
    val lon: Double,
    val address: String? = null,
    
    // Detalles específicos para transportistas
    val maxHeight: Double? = null, // Altura máxima del techo (ej: parking cubierto)
    val hasShower: Boolean = false,
    val hasToilet: Boolean = false,
    val is24h: Boolean = false,
    val hasSecurity: Boolean = false, // Vigilancia nocturna
    val truckParkingSpots: Int? = null, // Número de plazas para camión
    
    // Valoraciones
    val rating: Float = 0f,
    val verifiedByCommunity: Boolean = false,
    
    val source: String = "user", // "user" o "osm" (OpenStreetMap)
    val lastUpdated: Long = System.currentTimeMillis()
)
