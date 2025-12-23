package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Contribution - Historial de contribuciones
 * 
 * Trazabilidad de todas las aportaciones de la comunidad.
 * Quién, cuándo y qué se modificó.
 */
@Entity(tableName = "contributions")
data class Contribution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Qué se modificó
    val targetType: String,          // place, restriction, route, helper
    val targetId: Long,              // ID del elemento modificado
    val targetName: String? = null,  // Nombre para referencia rápida
    
    // Tipo de contribución
    val action: String,              // create, update, confirm, report, delete
    
    // Detalles del cambio
    val fieldChanged: String? = null, // "schedule", "phone", "maxHeight"
    val oldValue: String? = null,
    val newValue: String? = null,
    val notes: String? = null,
    
    // Quién lo hizo
    val userId: String,              // ID local del usuario
    val userName: String? = null,    // Nombre opcional
    
    // Cuándo
    val createdAt: Long = System.currentTimeMillis(),
    
    // Estado de sincronización
    val isSynced: Boolean = false,
    val syncedAt: Long? = null
)

/**
 * Tipos de objetivo de contribución
 */
object ContributionTargets {
    const val PLACE = "place"
    const val RESTRICTION = "restriction"
    const val ROUTE = "route"
    const val HELPER = "helper"
}

/**
 * Acciones de contribución
 */
object ContributionActions {
    const val CREATE = "create"           // Crear nuevo elemento
    const val UPDATE = "update"           // Actualizar información
    const val CONFIRM = "confirm"         // Confirmar que sigue activo/correcto
    const val REPORT = "report"           // Reportar error o cierre
    const val DELETE = "delete"           // Marcar como eliminado/cerrado
    const val UPVOTE = "upvote"           // Votar como útil
    const val DOWNVOTE = "downvote"       // Votar como incorrecto
}

/**
 * Resumen de contribuciones de un usuario
 */
data class ContributionSummary(
    val totalContributions: Int = 0,
    val placesCreated: Int = 0,
    val placesUpdated: Int = 0,
    val confirmations: Int = 0,
    val reports: Int = 0,
    val restrictionsAdded: Int = 0,
    val lastContributionAt: Long? = null
)

/**
 * Extensión para obtener descripción legible de la acción
 */
fun Contribution.getActionDescription(): String {
    return when (action) {
        ContributionActions.CREATE -> "Añadió"
        ContributionActions.UPDATE -> "Actualizó"
        ContributionActions.CONFIRM -> "Confirmó"
        ContributionActions.REPORT -> "Reportó"
        ContributionActions.DELETE -> "Eliminó"
        ContributionActions.UPVOTE -> "Votó útil"
        ContributionActions.DOWNVOTE -> "Votó incorrecto"
        else -> "Modificó"
    }
}

/**
 * Extensión para obtener descripción completa
 */
fun Contribution.getFullDescription(): String {
    val actionText = getActionDescription()
    val targetText = when (targetType) {
        ContributionTargets.PLACE -> "lugar"
        ContributionTargets.RESTRICTION -> "restricción"
        ContributionTargets.ROUTE -> "ruta"
        ContributionTargets.HELPER -> "ayudante"
        else -> "elemento"
    }
    
    return if (targetName != null) {
        "$actionText $targetText: $targetName"
    } else {
        "$actionText $targetText #$targetId"
    }
}
