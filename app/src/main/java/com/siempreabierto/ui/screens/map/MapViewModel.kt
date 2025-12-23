package com.siempreabierto.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.entities.Place
import com.siempreabierto.data.entities.Restriction
import com.siempreabierto.data.entities.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Mapa
 */
class MapViewModel : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // DAOs
    private val placeDao = DatabaseProvider.placeDao()
    private val restrictionDao = DatabaseProvider.restrictionDao()
    private val userSettingsDao = DatabaseProvider.userSettingsDao()

    init {
        loadInitialData()
    }

    /**
     * Cargar datos iniciales
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Cargar configuración del usuario
                val settings = userSettingsDao.getSettings()
                
                _uiState.value = _uiState.value.copy(
                    userSettings = settings,
                    selectedVehicleType = settings?.vehicleType ?: "car",
                    downloadedRegions = settings?.getDownloadedRegionsList() ?: emptyList()
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * Cargar lugares en el área visible del mapa
     */
    fun loadPlacesInArea(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingPlaces = true)

                placeDao.getInBoundingBox(minLat, maxLat, minLon, maxLon).collect { places ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlaces = false,
                        visiblePlaces = places
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingPlaces = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Cargar lugares por categoría en el área visible
     */
    fun loadPlacesByCategoryInArea(
        category: String,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingPlaces = true)

                placeDao.getInBoundingBoxByCategory(
                    category, minLat, maxLat, minLon, maxLon
                ).collect { places ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlaces = false,
                        visiblePlaces = places
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingPlaces = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Cargar restricciones en el área visible
     */
    fun loadRestrictionsInArea(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ) {
        viewModelScope.launch {
            try {
                restrictionDao.getInBoundingBox(minLat, maxLat, minLon, maxLon).collect { restrictions ->
                    _uiState.value = _uiState.value.copy(
                        visibleRestrictions = restrictions
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
     * Cargar restricciones relevantes para el vehículo del usuario
     */
    fun loadRestrictionsForUserVehicle(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ) {
        viewModelScope.launch {
            try {
                val settings = _uiState.value.userSettings ?: return@launch
                
                val height = settings.vehicleHeight ?: return@launch
                val width = settings.vehicleWidth ?: 2.5
                val weight = settings.vehicleWeight ?: 3.5

                restrictionDao.getHeightRestrictionsInArea(
                    height, minLat, maxLat, minLon, maxLon
                ).collect { restrictions ->
                    _uiState.value = _uiState.value.copy(
                        relevantRestrictions = restrictions
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
     * Cambiar tipo de vehículo seleccionado
     */
    fun setVehicleType(vehicleType: String) {
        _uiState.value = _uiState.value.copy(
            selectedVehicleType = vehicleType
        )
    }

    /**
     * Cambiar categoría de filtro
     */
    fun setFilterCategory(category: String?) {
        _uiState.value = _uiState.value.copy(
            filterCategory = category
        )
    }

    /**
     * Seleccionar un lugar
     */
    fun selectPlace(place: Place?) {
        _uiState.value = _uiState.value.copy(
            selectedPlace = place
        )
    }

    /**
     * Seleccionar una restricción
     */
    fun selectRestriction(restriction: Restriction?) {
        _uiState.value = _uiState.value.copy(
            selectedRestriction = restriction
        )
    }

    /**
     * Actualizar posición del usuario
     */
    fun updateUserLocation(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(
            userLatitude = latitude,
            userLongitude = longitude,
            hasUserLocation = true
        )
    }

    /**
     * Actualizar nivel de zoom
     */
    fun updateZoom(zoom: Float) {
        _uiState.value = _uiState.value.copy(
            currentZoom = zoom
        )
    }

    /**
     * Toggle mostrar restricciones
     */
    fun toggleShowRestrictions() {
        _uiState.value = _uiState.value.copy(
            showRestrictions = !_uiState.value.showRestrictions
        )
    }

    /**
     * Buscar en el mapa
     */
    fun searchOnMap(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            try {
                placeDao.search(query, 20).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = results
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
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Estado de la UI del Mapa
 */
data class MapUiState(
    // Loading states
    val isLoadingPlaces: Boolean = false,
    val error: String? = null,

    // User
    val userSettings: UserSettings? = null,
    val hasUserLocation: Boolean = false,
    val userLatitude: Double = 40.4168,  // Madrid por defecto
    val userLongitude: Double = -3.7038,

    // Map state
    val currentZoom: Float = 12f,
    val selectedVehicleType: String = "car",
    val filterCategory: String? = null,
    val showRestrictions: Boolean = true,

    // Downloaded regions
    val downloadedRegions: List<String> = emptyList(),

    // Visible elements
    val visiblePlaces: List<Place> = emptyList(),
    val visibleRestrictions: List<Restriction> = emptyList(),
    val relevantRestrictions: List<Restriction> = emptyList(),

    // Selected elements
    val selectedPlace: Place? = null,
    val selectedRestriction: Restriction? = null,

    // Search
    val searchResults: List<Place> = emptyList()
)
