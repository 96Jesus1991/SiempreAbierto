package com.siempreabierto.ui.screens.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.entities.Place
import com.siempreabierto.data.entities.Helper
import com.siempreabierto.data.entities.HelpRequest
import com.siempreabierto.data.entities.PlaceCategories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Emergencia
 * "ME QUEDÉ TIRADO"
 */
class EmergencyViewModel : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    // DAOs
    private val placeDao = DatabaseProvider.placeDao()
    private val helperDao = DatabaseProvider.helperDao()
    private val userSettingsDao = DatabaseProvider.userSettingsDao()

    /**
     * Seleccionar tipo de problema
     */
    fun selectProblem(problemType: String) {
        _uiState.value = _uiState.value.copy(
            selectedProblem = problemType
        )
    }

    /**
     * Buscar ayuda cercana basada en la ubicación del usuario
     */
    fun searchNearbyHelp(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSearching = true,
                    userLatitude = latitude,
                    userLongitude = longitude
                )

                // Definir área de búsqueda (aproximadamente 20km)
                val delta = 0.18 // ~20km
                val minLat = latitude - delta
                val maxLat = latitude + delta
                val minLon = longitude - delta
                val maxLon = longitude + delta

                // Buscar talleres cercanos
                placeDao.getInBoundingBoxByCategory(
                    PlaceCategories.WORKSHOP,
                    minLat, maxLat, minLon, maxLon
                ).collect { workshops ->
                    _uiState.value = _uiState.value.copy(
                        nearbyWorkshops = workshops
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = e.message
                )
            }
        }

        // Buscar talleres 24h
        viewModelScope.launch {
            try {
                val delta = 0.18
                val minLat = latitude - delta
                val maxLat = latitude + delta
                val minLon = longitude - delta
                val maxLon = longitude + delta

                placeDao.getInBoundingBoxByCategory(
                    PlaceCategories.WORKSHOP_24H,
                    minLat, maxLat, minLon, maxLon
                ).collect { workshops24h ->
                    _uiState.value = _uiState.value.copy(
                        nearbyWorkshops24h = workshops24h
                    )
                }
            } catch (e: Exception) {
                // Ignorar error secundario
            }
        }

        // Buscar grúas cercanas
        viewModelScope.launch {
            try {
                val delta = 0.18
                val minLat = latitude - delta
                val maxLat = latitude + delta
                val minLon = longitude - delta
                val maxLon = longitude + delta

                placeDao.getInBoundingBoxByCategory(
                    PlaceCategories.TOW_TRUCK,
                    minLat, maxLat, minLon, maxLon
                ).collect { towTrucks ->
                    _uiState.value = _uiState.value.copy(
                        nearbyTowTrucks = towTrucks
                    )
                }
            } catch (e: Exception) {
                // Ignorar error secundario
            }
        }

        // Buscar gasolineras cercanas (para problema de combustible)
        viewModelScope.launch {
            try {
                val delta = 0.18
                val minLat = latitude - delta
                val maxLat = latitude + delta
                val minLon = longitude - delta
                val maxLon = longitude + delta

                placeDao.getInBoundingBoxByCategory(
                    PlaceCategories.GAS_STATION,
                    minLat, maxLat, minLon, maxLon
                ).collect { gasStations ->
                    _uiState.value = _uiState.value.copy(
                        nearbyGasStations = gasStations
                    )
                }
            } catch (e: Exception) {
                // Ignorar error secundario
            }
        }

        // Buscar ayudantes de la comunidad
        viewModelScope.launch {
            try {
                val delta = 0.18
                val minLat = latitude - delta
                val maxLat = latitude + delta
                val minLon = longitude - delta
                val maxLon = longitude + delta

                helperDao.getAvailableHelpersInArea(
                    minLat, maxLat, minLon, maxLon
                ).collect { helpers ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        nearbyHelpers = helpers
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Buscar ayudantes que pueden ayudar con un tipo específico de problema
     */
    fun searchHelpersForProblem(problemType: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val helpType = mapProblemToHelpType(problemType)
                
                val delta = 0.18
                val minLat = latitude - delta
                val maxLat = latitude + delta
                val minLon = longitude - delta
                val maxLon = longitude + delta

                helperDao.getAvailableHelpersInArea(
                    minLat, maxLat, minLon, maxLon
                ).collect { allHelpers ->
                    // Filtrar por tipo de ayuda
                    val filteredHelpers = allHelpers.filter { helper ->
                        helper.canHelpWith.contains(helpType)
                    }
                    _uiState.value = _uiState.value.copy(
                        nearbyHelpers = filteredHelpers
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Mapear tipo de problema a tipo de ayuda
     */
    private fun mapProblemToHelpType(problemType: String): String {
        return when (problemType) {
            "battery" -> "jump_start"
            "flat_tire" -> "flat_tire"
            "fuel" -> "fuel"
            "wont_start" -> "jump_start"
            "locked" -> "tools"
            else -> "company"
        }
    }

    /**
     * Crear solicitud de ayuda
     */
    fun createHelpRequest(
        problemType: String,
        description: String?,
        latitude: Double,
        longitude: Double,
        locationDescription: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isCreatingRequest = true)

                val settings = userSettingsDao.getSettings()
                
                val request = HelpRequest(
                    requesterId = settings?.localUserId ?: "anonymous",
                    requesterName = settings?.nickname,
                    latitude = latitude,
                    longitude = longitude,
                    locationDescription = locationDescription,
                    problemType = problemType,
                    problemDescription = description,
                    vehicleType = settings?.vehicleType,
                    vehicleDescription = settings?.vehicleDescription,
                    status = "pending"
                )

                val requestId = helperDao.insertRequest(request)

                _uiState.value = _uiState.value.copy(
                    isCreatingRequest = false,
                    helpRequestCreated = true,
                    currentRequestId = requestId
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingRequest = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Cancelar solicitud de ayuda
     */
    fun cancelHelpRequest() {
        viewModelScope.launch {
            try {
                val requestId = _uiState.value.currentRequestId ?: return@launch
                helperDao.cancelRequest(requestId)
                
                _uiState.value = _uiState.value.copy(
                    helpRequestCreated = false,
                    currentRequestId = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Seleccionar un lugar para ver detalles
     */
    fun selectPlace(place: Place?) {
        _uiState.value = _uiState.value.copy(
            selectedPlace = place
        )
    }

    /**
     * Seleccionar un ayudante para ver detalles
     */
    fun selectHelper(helper: Helper?) {
        _uiState.value = _uiState.value.copy(
            selectedHelper = helper
        )
    }

    /**
     * Reiniciar búsqueda
     */
    fun resetSearch() {
        _uiState.value = EmergencyUiState()
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Estado de la UI de Emergencia
 */
data class EmergencyUiState(
    // Estados de carga
    val isSearching: Boolean = false,
    val isCreatingRequest: Boolean = false,
    val error: String? = null,

    // Ubicación del usuario
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,

    // Problema seleccionado
    val selectedProblem: String? = null,

    // Resultados de búsqueda
    val nearbyWorkshops: List<Place> = emptyList(),
    val nearbyWorkshops24h: List<Place> = emptyList(),
    val nearbyTowTrucks: List<Place> = emptyList(),
    val nearbyGasStations: List<Place> = emptyList(),
    val nearbyHelpers: List<Helper> = emptyList(),

    // Selección actual
    val selectedPlace: Place? = null,
    val selectedHelper: Helper? = null,

    // Solicitud de ayuda
    val helpRequestCreated: Boolean = false,
    val currentRequestId: Long? = null
)
