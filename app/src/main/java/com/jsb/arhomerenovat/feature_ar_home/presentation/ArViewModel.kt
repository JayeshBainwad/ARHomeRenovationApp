package com.jsb.arhomerenovat.feature_ar_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsb.arhomerenovat.feature_ar_home.data.local.ModelEntity
import com.jsb.arhomerenovat.feature_ar_home.data.repository.ModelRepositoryImpl
import kotlinx.coroutines.launch

class ArViewModel(private val repository: ModelRepositoryImpl) : ViewModel() {

    fun saveLayout(layoutName: String, models: List<ModelEntity>) {
        viewModelScope.launch {
            repository.saveLayoutWithModels(layoutName, models)
        }
    }
}
