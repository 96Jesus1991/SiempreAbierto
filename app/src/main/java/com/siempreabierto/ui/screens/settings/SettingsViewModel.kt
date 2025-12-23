package com.siempreabierto.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.entities.UserSettings
import com.siempreabierto.data.entities.UserVehicleTypes
import com.siempreabierto.data.entities.defaultVehicleProfiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Ajustes
 * Configuración local - Sin tracking, sin servidores
 */
class SettingsViewModel : ViewModel() {

    // Estado de la UI
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // DAOs
    private val userSettingsDao = DatabaseProvider.userSettingsDao()
    private val placeDao = DatabaseProvider.placeDao()
    private val contributionDao = DatabaseProvider.contributionDao()

    init {
        loadSettings()
    }

    /**
     * Cargar configuración
     */
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val settings = userSettingsDao.getSettings()

                // Calcular estadísticas de almacenamiento
                val totalPlaces = placeDao.countActive()
                val totalContributions = contributionDao.countAll()
                val unsyncedContributions = contributionDao.countUnsynced()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    settings = settings,
                    darkMode = settings?.darkMode ?: true,
                    vehicleType = settings?.vehicleType ?: "car",
                    vehicleHeight = settings?.vehicleHeight,
                    vehicleWidth = settings?.vehicleWidth,
                    vehicleWeight = settings?.vehicleWeight,
                    vehicleLength = settings?.vehicleLength,
                    avoidTolls = settings?.avoidTolls ?: false,
                    avoidHighways = settings?.avoidHighways ?: false,
                    showRestrictions = settings?.showRestrictionsOnMap ?: true,
                    showHelpers = settings?.showHelpersOnMap ?: true,
                    notifyRestrictions = settings?.notifyRestrictions ?: true,
                    restrictionAlertDistance = settings?.restrictionAlertDistance ?: 5,
                    autoSync = settings?.autoSync ?: false,
                    downloadedRegions = settings?.getDownloadedRegionsList() ?: emptyList(),
                    totalPlaces = totalPlaces,
                    totalContributions = totalContributions,
                    unsyncedContributions = unsyncedContributions,
                    lastSyncTime = settings?.lastSyncAt
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Actualizar modo oscuro
     */
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateDarkMode(enabled)
                _uiState.value = _uiState.value.copy(darkMode = enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar tipo de vehículo
     */
    fun setVehicleType(vehicleType: String) {
        viewModelScope.launch {
            try {
                // Obtener perfil predefinido para autocompletar dimensiones
                val profile = defaultVehicleProfiles.find { it.type == vehicleType }

                userSettingsDao.updateVehicle(
                    vehicleType = vehicleType,
                    height = profile?.defaultHeight,
                    width = profile?.defaultWidth,
                    weight = profile?.defaultWeight,
                    length = profile?.defaultLength
                )

                _uiState.value = _uiState.value.copy(
                    vehicleType = vehicleType,
                    vehicleHeight = profile?.defaultHeight,
                    vehicleWidth = profile?.defaultWidth,
                    vehicleWeight = profile?.defaultWeight,
                    vehicleLength = profile?.defaultLength
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar dimensiones del vehículo
     */
    fun setVehicleDimensions(
        height: Double?,
        width: Double?,
        weight: Double?,
        length: Double?
    ) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateVehicle(
                    vehicleType = _uiState.value.vehicleType,
                    height = height,
                    width = width,
                    weight = weight,
                    length = length
                )

                _uiState.value = _uiState.value.copy(
                    vehicleHeight = height,
                    vehicleWidth = width,
                    vehicleWeight = weight,
                    vehicleLength = length
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar preferencia de evitar peajes
     */
    fun setAvoidTolls(avoid: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateAvoidTolls(avoid)
                _uiState.value = _uiState.value.copy(avoidTolls = avoid)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar preferencia de evitar autopistas
     */
    fun setAvoidHighways(avoid: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateAvoidHighways(avoid)
                _uiState.value = _uiState.value.copy(avoidHighways = avoid)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar mostrar restricciones en mapa
     */
    fun setShowRestrictions(show: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateShowRestrictions(show)
                _uiState.value = _uiState.value.copy(showRestrictions = show)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar mostrar ayudantes en mapa
     */
    fun setShowHelpers(show: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateShowHelpers(show)
                _uiState.value = _uiState.value.copy(showHelpers = show)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar notificaciones de restricciones
     */
    fun setNotifyRestrictions(notify: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateNotifyRestrictions(notify)
                _uiState.value = _uiState.value.copy(notifyRestrictions = notify)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar distancia de alerta de restricciones
     */
    fun setRestrictionAlertDistance(distanceKm: Int) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateRestrictionAlertDistance(distanceKm)
                _uiState.value = _uiState.value.copy(restrictionAlertDistance = distanceKm)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Actualizar sincronización automática
     */
    fun setAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userSettingsDao.updateAutoSync(enabled)
                _uiState.value = _uiState.value.copy(autoSync = enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Sincronizar datos manualmente
     */
    fun syncData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSyncing = true)

                // TODO: Implementar sincronización real con servidor
                // Por ahora solo actualizar timestamp
                
                kotlinx.coroutines.delay(1500) // Simular sincronización
                
                val timestamp = System.currentTimeMillis()
                userSettingsDao.updateLastSync(timestamp)
                contributionDao.markAllAsSynced(timestamp)

                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    lastSyncTime = timestamp,
                    unsyncedContributions = 0,
                    syncSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Borrar todos los datos locales
     */
    fun clearAllData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isClearing = true)

                // Borrar datos de todas las tablas excepto settings
                DatabaseProvider.placeDao().deleteAll()
                DatabaseProvider.restrictionDao().deleteAll()
                DatabaseProvider.routeDao().deleteAll()
                DatabaseProvider.contributionDao().deleteAll()
                DatabaseProvider.helperDao().deleteAllHelpers()
                DatabaseProvider.helperDao().deleteAllRequests()

                // Resetear estadísticas en settings
                val settings = userSettingsDao.getSettings()
                if (settings != null) {
                    userSettingsDao.update(
                        settings.copy(
                            totalContributions = 0,
                            placesAdded = 0,
                            confirmationsMade = 0,
                            helpProvided = 0,
                            downloadedRegions = "[]",
                            lastSyncAt = null,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isClearing = false,
                    totalPlaces = 0,
                    totalContributions = 0,
                    unsyncedContributions = 0,
                    downloadedRegions = emptyList(),
                    dataCleared = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isClearing = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Aceptar términos y condiciones
     */
    fun acceptTerms() {
        viewModelScope.launch {
            try {
                userSettingsDao.acceptTerms()
                _uiState.value = _uiState.value.copy(termsAccepted = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Aceptar política de privacidad
     */
    fun acceptPrivacy() {
        viewModelScope.launch {
            try {
                userSettingsDao.acceptPrivacy()
                _uiState.value = _uiState.value.copy(privacyAccepted = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Refrescar configuración
     */
    fun refresh() {
        loadSettings()
    }

    /**
     * Limpiar flag de sincronización exitosa
     */
    fun clearSyncSuccess() {
        _uiState.value = _uiState.value.copy(syncSuccess = false)
    }

    /**
     * Limpiar flag de datos borrados
     */
    fun clearDataCleared() {
        _uiState.value = _uiState.value.copy(dataCleared = false)
    }

    /**
     * Limpiar error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * Estado de la UI de Ajustes
 */
data class SettingsUiState(
    // Estados de carga
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val isClearing: Boolean = false,
    val error: String? = null,

    // Settings completos
    val settings: UserSettings? = null,

    // Apariencia
    val darkMode: Boolean = true,

    // Vehículo
    val vehicleType: String = "car",
    val vehicleHeight: Double? = null,
    val vehicleWidth: Double? = null,
    val vehicleWeight: Double? = null,
    val vehicleLength: Double? = null,

    // Preferencias de rutas
    val avoidTolls: Boolean = false,
    val avoidHighways: Boolean = false,

    // Preferencias de mapa
    val showRestrictions: Boolean = true,
    val showHelpers: Boolean = true,

    // Notificaciones
    val notifyRestrictions: Boolean = true,
    val restrictionAlertDistance: Int = 5,

    // Sincronización
    val autoSync: Boolean = false,
    val lastSyncTime: Long? = null,
    val unsyncedContributions: Int = 0,

    // Almacenamiento
    val downloadedRegions: List<String> = emptyList(),
    val totalPlaces: Int = 0,
    val totalContributions: Int = 0,

    // Legal
    val termsAccepted: Boolean = false,
    val privacyAccepted: Boolean = false,

    // Flags de acción
    val syncSuccess: Boolean = false,
    val dataCleared: Boolean = false
)
