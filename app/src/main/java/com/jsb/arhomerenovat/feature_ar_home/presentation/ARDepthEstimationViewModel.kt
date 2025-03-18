package com.jsb.arhomerenovat.feature_ar_home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jsb.arhomerenovat.feature_ar_home.domain.ModelData
import io.github.sceneview.ar.node.ArModelNode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "ARDepthViewModel"

class ARDepthEstimationViewModel : ViewModel() {

    // Holds the currently selected model
    private val _selectedModel = MutableStateFlow<String?>(null)
    val selectedModel: StateFlow<String?> = _selectedModel

    // List of models currently added to the AR scene
    private val _addedModels = MutableStateFlow<List<ArModelNode>>(emptyList())
    val addedModels: StateFlow<List<ArModelNode>> = _addedModels

    // List of saved models (for saving layout functionality)
    private val _savedModels = MutableStateFlow<List<ArModelNode>>(emptyList())
    val savedModels: StateFlow<List<ArModelNode>> = _savedModels

    // Track rotation value for each model
    private val modelRotations = mutableMapOf<ArModelNode, Float>()

    private val _selectedModelNode = MutableStateFlow<ArModelNode?>(null)
    val selectedModelNode: StateFlow<ArModelNode?> = _selectedModelNode

    /**
     * Selects a specific model for rotation
     */
    fun selectModelForRotation(modelNode: ArModelNode) {
        _selectedModelNode.value = modelNode
        Log.d(TAG, "🎯 Selected model for rotation: ${modelNode.name}")
    }

    /**
     * Updates the rotation of the selected model
     */
    fun updateSelectedModelRotation(newRotation: Float) {
        _selectedModelNode.value?.let { modelNode ->
            modelNode.modelRotation = modelNode.modelRotation.copy(y = newRotation)
            Log.d(TAG, "🔄 Rotation applied to selected model: ${modelNode.name} - ${newRotation}°")
        }
    }

    /**
     * Updates the currently selected model's file name
     */
    fun selectedModel(modelFileName: String) {
        _selectedModel.value = modelFileName
        Log.d(TAG, "✅ Model selected in ViewModel: $modelFileName")
    }

    /**
     * Adds the given model node to the list of added models
     */
    fun addModelToList(modelNode: ArModelNode) {
        _addedModels.update { currentModels -> currentModels + modelNode }
        modelRotations[modelNode] = 0f // Initialize rotation to 0°
        Log.d(TAG, "✅ Model added to list: ${modelNode.name}")
    }

    /**
     * Updates the rotation value for a specific model
     */
    fun updateModelRotation(modelNode: ArModelNode, newRotation: Float) {
        val currentRotation = modelRotations[modelNode] ?: 0f

        // Calculate delta using quaternion logic to prevent flipping
        val deltaRotation = getShortestRotationPath(currentRotation, newRotation)

        val updatedRotation = (currentRotation + deltaRotation) % 360f
        modelRotations[modelNode] = updatedRotation

        // Ensure smooth rotation using Quaternion
        modelNode.modelRotation = modelNode.modelRotation.copy(y = newRotation)

        Log.d(TAG, "🔄 Smooth Rotation applied: ${updatedRotation}°")
    }


    /**
     * Calculates the shortest rotation path to prevent sudden jumps.
     */
    private fun getShortestRotationPath(currentRotation: Float, targetRotation: Float): Float {
        val delta = (targetRotation - currentRotation) % 360f

        // Ensure no sudden jump by maintaining shortest path logic
        return when {
            delta > 180f -> delta - 360f
            delta < -180f -> delta + 360f
            else -> delta
        }
    }


    /**
     * Saves the current layout (stores the list of added models)
     */
    fun saveCurrentLayout() {
        _savedModels.value = _addedModels.value.toList()
        Log.d(TAG, "💾 Layout saved with ${_savedModels.value.size} models")
    }

    /**
     * Clears the list of added models
     */
    fun clearModels() {
        _addedModels.value = emptyList()
        modelRotations.clear()
        Log.d(TAG, "🗑️ All models cleared from ViewModel")
    }

    /**
     * Removes a specific model from the list
     */
    fun removeModel(modelNode: ArModelNode) {
        _addedModels.update { currentModels -> currentModels.filter { it != modelNode } }
        modelRotations.remove(modelNode)
        Log.d(TAG, "❌ Model removed: ${modelNode.name}")
    }
}
