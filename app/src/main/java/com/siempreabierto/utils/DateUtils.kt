package com.siempreabierto.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utilidades de fecha y hora para Siempre Abierto
 * Formato español, sin dependencias externas
 */
object DateUtils {

    // Formatos de fecha
    private val formatDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
    private val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    private val formatTime = SimpleDateFormat("HH:mm", Locale("es", "ES"))
    private val formatDayMonth = SimpleDateFormat("dd MMM", Locale("es", "ES"))
    private val formatFull = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))

    /**
     * Formatear timestamp a fecha y hora
     */
    fun formatDateTime(timestamp: Long): String {
        return formatDateTime.format(Date(timestamp))
    }

    /**
     * Formatear timestamp a solo fecha
     */
    fun formatDate(timestamp: Long): String {
        return formatDate.format(Date(timestamp))
    }

    /**
     * Formatear timestamp a solo hora
     */
    fun formatTime(timestamp: Long): String {
        return formatTime.format(Date(timestamp))
    }

    /**
     * Formatear timestamp a día y mes
     */
    fun formatDayMonth(timestamp: Long): String {
        return formatDayMonth.format(Date(timestamp))
    }

    /**
     * Formatear timestamp a fecha completa
     */
    fun formatFullDate(timestamp: Long): String {
        return formatFull.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
    }

    /**
     * Obtener tiempo relativo (hace X minutos/horas/días)
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Ahora mismo"
            minutes < 2 -> "Hace 1 minuto"
            minutes < 60 -> "Hace $minutes minutos"
            hours < 2 -> "Hace 1 hora"
            hours < 24 -> "Hace $hours horas"
            days < 2 -> "Ayer"
            days < 7 -> "Hace $days días"
            days < 14 -> "Hace 1 semana"
            days < 30 -> "Hace ${days / 7} semanas"
            days < 60 -> "Hace 1 mes"
            days < 365 -> "Hace ${days / 30} meses"
            else -> "Hace más de 1 año"
        }
    }

    /**
     * Obtener tiempo relativo corto
     */
    fun getRelativeTimeShort(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 60 -> "${minutes}min"
            hours < 24 -> "${hours}h"
            days < 7 -> "${days}d"
            days < 30 -> "${days / 7}sem"
            else -> "${days / 30}mes"
        }
    }

    /**
     * Verificar si un timestamp es de hoy
     */
    fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Verificar si un timestamp es de ayer
     */
    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Verificar si un timestamp es de esta semana
     */
    fun isThisWeek(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }

        return now.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * Obtener inicio del día actual
     */
    fun getStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Obtener inicio de la semana actual
     */
    fun getStartOfWeek(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Obtener inicio del mes actual
     */
    fun getStartOfMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Formatear última actualización de forma inteligente
     */
    fun formatLastUpdate(timestamp: Long?): String {
        if (timestamp == null) return "Sin datos"

        return when {
            isToday(timestamp) -> "Hoy a las ${formatTime(timestamp)}"
            isYesterday(timestamp) -> "Ayer a las ${formatTime(timestamp)}"
            isThisWeek(timestamp) -> formatDayMonth(timestamp)
            else -> formatDate(timestamp)
        }
    }

    /**
     * Formatear última confirmación
     */
    fun formatLastConfirmation(timestamp: Long?): String {
        if (timestamp == null) return "Nunca confirmado"

        val days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - timestamp)

        return when {
            days < 1 -> "Confirmado hoy"
            days < 2 -> "Confirmado ayer"
            days < 7 -> "Confirmado hace $days días"
            days < 30 -> "Confirmado hace ${days / 7} semanas"
            days < 90 -> "Confirmado hace ${days / 30} meses"
            else -> "Confirmado el ${formatDate(timestamp)}"
        }
    }

    /**
     * Obtener día de la semana en español
     */
    fun getDayOfWeek(timestamp: Long): String {
        val days = listOf(
            "Domingo", "Lunes", "Martes", "Miércoles",
            "Jueves", "Viernes", "Sábado"
        )
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }

    /**
     * Obtener día de la semana abreviado
     */
    fun getDayOfWeekShort(timestamp: Long): String {
        val days = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
    }

    /**
     * Obtener mes en español
     */
    fun getMonth(timestamp: Long): String {
        val months = listOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return months[calendar.get(Calendar.MONTH)]
    }

    /**
     * Verificar si es horario nocturno (22:00 - 06:00)
     */
    fun isNightTime(timestamp: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour >= 22 || hour < 6
    }

    /**
     * Verificar si es fin de semana
     */
    fun isWeekend(timestamp: Long = System.currentTimeMillis()): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    /**
     * Calcular edad de un dato (para mostrar frescura)
     */
    fun getDataFreshness(timestamp: Long): DataFreshness {
        val days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - timestamp)

        return when {
            days < 7 -> DataFreshness.FRESH
            days < 30 -> DataFreshness.RECENT
            days < 90 -> DataFreshness.OLD
            else -> DataFreshness.STALE
        }
    }
}

/**
 * Nivel de frescura de los datos
 */
enum class DataFreshness {
    FRESH,    // < 7 días
    RECENT,   // 7-30 días
    OLD,      // 30-90 días
    STALE     // > 90 días
}

/**
 * Extensión para Long (timestamp)
 */
fun Long.toRelativeTime(): String = DateUtils.getRelativeTime(this)
fun Long.toFormattedDate(): String = DateUtils.formatDate(this)
fun Long.toFormattedDateTime(): String = DateUtils.formatDateTime(this)
fun Long.toFormattedTime(): String = DateUtils.formatTime(this)
