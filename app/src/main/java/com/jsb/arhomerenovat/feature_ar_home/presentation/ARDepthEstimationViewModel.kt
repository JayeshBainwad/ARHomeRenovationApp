package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.domain.repository.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "ARDepthScreen"

@HiltViewModel
class ARDepthEstimationViewModel @Inject constructor(
    private val repository: ModelRepository
) : ViewModel() {

    /**
     * Flow that holds all saved geo models and automatically updates the UI
     * when data changes in the database.
     */
    val savedGeoModels = repository.getAllGeoModels() // Property "savedGeoModels" is never used
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Holds the currently selected model's file name (if any)
    private val _selectedGeoModel = MutableStateFlow<String?>(null)

    /**
     * Sets the selected geo model and logs the selection.
     */
    fun selectedGeoModel(modelFileName: String) {
        _selectedGeoModel.value = modelFileName
        Log.d(TAG, "✅ Model selected: $modelFileName")
    }

    /**
     * Saves a new geospatial model to the database with its position & orientation.
     * Ensures validation before inserting into Room database.
     */
    fun saveGeoModelToDatabase(
        modelName: String,
        posX: Float,
        posY: Float,
        posZ: Float,
        qx: Float,
        qy: Float,
        qz: Float,
        qw: Float,
        latitude: Double,
        longitude: Double,
        altitude: Double
    ) {
        viewModelScope.launch {
            try {
                // Validation checks
                require(modelName.isNotBlank()) { "Model name cannot be empty!" }
                require(latitude in -90.0..90.0) { "Invalid latitude value!" }
                require(longitude in -180.0..180.0) { "Invalid longitude value!" }

                val modelEntity = ModelEntity(
                    modelName = modelName,
                    posX = posX,
                    posY = posY,
                    posZ = posZ,
                    qx = qx,
                    qy = qy,
                    qz = qz,
                    qw = qw,
                    latitude = latitude,
                    longitude = longitude,
                    altitude = altitude
                )

                Log.d(TAG, "💾 Saving model to DB: $modelEntity")
                repository.insertGeoModel(modelEntity)

                Log.d(TAG, "✅ Geo Model saved: $modelName at ($latitude, $longitude, $altitude)")

            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "🚨 Validation error: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error saving geo model: ${e.message}", e)
            }
        }
    }

    /**
     * Fetches all saved geo models from the database and provides the result via a callback.
     * This is useful when we want to manually get the latest data without relying on StateFlow.
     */
    fun fetchSavedGeoModels(onResult: (List<ModelEntity>) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "🔄 Fetching saved geo models from database...")

                val models = repository.getAllGeoModels().first() // Collect first value from Flow

                Log.d(TAG, "✅ Models fetched: ${models.size} models found.")
                onResult(models) // Pass the list to the callback
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error fetching models: ${e.message}")
                onResult(emptyList()) // Return an empty list on failure
            }
        }
    }

    /**
     * Clears all saved geospatial models from the database and logs the action.
     */
    fun clearGeoModels() {
        viewModelScope.launch {
            try {
                repository.clearAllGeoModels()
                Log.d(TAG, "🗑️ All geospatial models cleared from Room database")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error clearing geo models: ${e.message}", e)
            }
        }
    }
}
