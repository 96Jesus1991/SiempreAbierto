package com.siempreabierto.utils

/**
 * Constantes globales de Siempre Abierto
 * App 100% OFFLINE para conductores
 */
object Constants {

    // ==================== APP INFO ====================
    const val APP_NAME = "Siempre Abierto"
    const val APP_VERSION = "1.0.0"
    const val APP_VERSION_CODE = 1
    const val PACKAGE_NAME = "com.siempreabierto"

    // ==================== DATABASE ====================
    const val DATABASE_NAME = "siempreabierto.db"
    const val DATABASE_VERSION = 1

    // ==================== MAPAS ====================
    object Maps {
        // Zoom levels
        const val ZOOM_MIN = 4f
        const val ZOOM_MAX = 19f
        const val ZOOM_DEFAULT = 12f
        const val ZOOM_CITY = 14f
        const val ZOOM_STREET = 17f
        const val ZOOM_COUNTRY = 6f

        // Centro de España (por defecto)
        const val DEFAULT_LAT = 40.4168  // Madrid
        const val DEFAULT_LON = -3.7038

        // Límites de España
        const val SPAIN_MIN_LAT = 27.6
        const val SPAIN_MAX_LAT = 43.8
        const val SPAIN_MIN_LON = -18.2
        const val SPAIN_MAX_LON = 4.4

        // Extensión de archivos de mapa
        const val MBTILES_EXTENSION = ".mbtiles"
    }

    // ==================== BÚSQUEDA ====================
    object Search {
        const val DEFAULT_RADIUS_KM = 20.0
        const val MAX_RADIUS_KM = 100.0
        const val MIN_RADIUS_KM = 1.0
        const val DEFAULT_RESULTS_LIMIT = 50
        const val SEARCH_DEBOUNCE_MS = 300L
    }

    // ==================== RESTRICCIONES ====================
    object Restrictions {
        // Alertas de restricción
        const val ALERT_DISTANCE_DEFAULT_KM = 5
        const val ALERT_DISTANCE_MIN_KM = 1
        const val ALERT_DISTANCE_MAX_KM = 20

        // Valores típicos de vehículos
        const val CAR_HEIGHT = 1.5
        const val VAN_HEIGHT = 2.2
        const val TRUCK_SMALL_HEIGHT = 2.8
        const val TRUCK_MEDIUM_HEIGHT = 3.2
        const val TRUCK_LARGE_HEIGHT = 4.0
        const val BUS_HEIGHT = 3.2
        const val BUS_LARGE_HEIGHT = 3.8

        const val TRUCK_MAX_WEIGHT = 40.0  // toneladas
        const val TRUCK_MAX_WIDTH = 2.55   // metros
        const val TRUCK_MAX_LENGTH = 16.5  // metros
    }

    // ==================== COMUNIDAD ====================
    object Community {
        // Contribuciones
        const val MIN_CONTRIBUTION_INTERVAL_MS = 60_000L  // 1 minuto entre contribuciones
        const val REPORT_THRESHOLD = 3  // Reportes para revisar
        const val CONFIRMATION_EXPIRY_DAYS = 90  // Días hasta que expire confirmación

        // Ayudantes
        const val HELPER_COVERAGE_DEFAULT_KM = 20
        const val HELPER_COVERAGE_MIN_KM = 5
        const val HELPER_COVERAGE_MAX_KM = 50
        const val HELPER_INACTIVE_DAYS = 30  // Días sin actividad
    }

    // ==================== SINCRONIZACIÓN ====================
    object Sync {
        const val SYNC_INTERVAL_HOURS = 24
        const val BATCH_SIZE = 100
        const val MAX_RETRY_ATTEMPTS = 3
        const val RETRY_DELAY_MS = 5000L
    }

    // ==================== UI ====================
    object UI {
        // Tamaños de botones (para uso en carretera)
        const val BUTTON_HEIGHT_NORMAL = 48
        const val BUTTON_HEIGHT_LARGE = 56
        const val BUTTON_HEIGHT_EMERGENCY = 72

        // Tamaños de texto
        const val TEXT_SIZE_SMALL = 12
        const val TEXT_SIZE_NORMAL = 16
        const val TEXT_SIZE_LARGE = 18
        const val TEXT_SIZE_TITLE = 20
        const val TEXT_SIZE_HEADLINE = 24

        // Animaciones
        const val ANIMATION_DURATION_SHORT = 150
        const val ANIMATION_DURATION_NORMAL = 300
        const val ANIMATION_DURATION_LONG = 500

        // Snackbar
        const val SNACKBAR_DURATION_SHORT = 2000
        const val SNACKBAR_DURATION_NORMAL = 4000
        const val SNACKBAR_DURATION_LONG = 6000
    }

    // ==================== REGIONES ====================
    object Regions {
        val ALL = listOf(
            "andalucia" to "Andalucía",
            "aragon" to "Aragón",
            "asturias" to "Asturias",
            "baleares" to "Islas Baleares",
            "canarias" to "Canarias",
            "cantabria" to "Cantabria",
            "castilla_la_mancha" to "Castilla-La Mancha",
            "castilla_y_leon" to "Castilla y León",
            "cataluna" to "Cataluña",
            "extremadura" to "Extremadura",
            "galicia" to "Galicia",
            "la_rioja" to "La Rioja",
            "madrid" to "Madrid",
            "murcia" to "Murcia",
            "navarra" to "Navarra",
            "pais_vasco" to "País Vasco",
            "valenciana" to "C. Valenciana"
        )

        fun getDisplayName(code: String): String {
            return ALL.find { it.first == code }?.second ?: code
        }

        fun getCode(displayName: String): String? {
            return ALL.find { it.second == displayName }?.first
        }
    }

    // ==================== CATEGORÍAS ====================
    object Categories {
        val ALL = listOf(
            "workshop" to "Talleres",
            "workshop_24h" to "Talleres 24h",
            "tow_truck" to "Grúas",
            "gas_station" to "Gasolineras",
            "car_wash" to "Lavaderos Coche",
            "truck_wash" to "Lavaderos Camión",
            "vacuum" to "Aspiradores",
            "parking" to "Parkings",
            "truck_parking" to "Parkings Camión",
            "rest_area" to "Zonas Descanso",
            "showers" to "Duchas",
            "supermarket" to "Supermercados",
            "supermarket_24h" to "Supermercados 24h",
            "bar" to "Bares",
            "cafe" to "Cafeterías",
            "restaurant" to "Restaurantes"
        )

        fun getDisplayName(code: String): String {
            return ALL.find { it.first == code }?.second ?: code
        }

        fun getCode(displayName: String): String? {
            return ALL.find { it.second == displayName }?.first
        }

        // Categorías relevantes para emergencias
        val EMERGENCY_CATEGORIES = listOf(
            "workshop",
            "workshop_24h",
            "tow_truck",
            "gas_station"
        )

        // Categorías para vehículos grandes
        val TRUCK_CATEGORIES = listOf(
            "truck_wash",
            "truck_parking",
            "rest_area",
            "showers"
        )
    }

    // ==================== PROBLEMAS DE EMERGENCIA ====================
    object EmergencyProblems {
        val ALL = listOf(
            "battery" to "Batería descargada",
            "flat_tire" to "Pinchazo",
            "wont_start" to "No arranca",
            "overheating" to "Sobrecalentamiento",
            "brakes" to "Problema de frenos",
            "fuel" to "Sin combustible",
            "locked" to "Llaves dentro",
            "other" to "Otro problema"
        )

        fun getDisplayName(code: String): String {
            return ALL.find { it.first == code }?.second ?: code
        }
    }

    // ==================== TIPOS DE AYUDA ====================
    object HelpTypes {
        val ALL = listOf(
            "jump_start" to "Puente de batería",
            "flat_tire" to "Ayuda con pinchazo",
            "fuel" to "Llevar combustible",
            "tow_short" to "Remolque corto",
            "tools" to "Prestar herramientas",
            "company" to "Hacer compañía",
            "directions" to "Guiar por la zona",
            "call_help" to "Llamar ayuda profesional",
            "transport" to "Llevar a algún sitio"
        )

        fun getDisplayName(code: String): String {
            return ALL.find { it.first == code }?.second ?: code
        }
    }

    // ==================== LEGAL ====================
    object Legal {
        const val DISCLAIMER = """
            INFORMACIÓN ORIENTATIVA
            
            • Los datos son aportados por la comunidad de usuarios.
            • No garantizamos horarios, precios ni disponibilidad.
            • Verifica siempre la señalización vial oficial.
            • No somos responsables de acuerdos entre usuarios.
            • Plataforma de contacto comunitario sin garantía de asistencia.
        """

        const val PRIVACY_SUMMARY = """
            TU PRIVACIDAD ES NUESTRA PRIORIDAD
            
            • No recopilamos datos personales.
            • No requerimos registro ni email.
            • No usamos cookies de seguimiento.
            • No mostramos publicidad.
            • Todos los datos se guardan localmente en tu dispositivo.
        """

        const val OSM_ATTRIBUTION = "Mapas © OpenStreetMap contributors"
    }
}
