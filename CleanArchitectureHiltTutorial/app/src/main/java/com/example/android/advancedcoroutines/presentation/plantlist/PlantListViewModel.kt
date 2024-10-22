/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.advancedcoroutines.presentation.plantlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.advancedcoroutines.domain.model.GrowZone
import com.example.android.advancedcoroutines.domain.model.NoGrowZone
import com.example.android.advancedcoroutines.domain.model.Plant
import com.example.android.advancedcoroutines.domain.usecases.GetPlantsUseCase
import com.example.android.advancedcoroutines.domain.usecases.UpdatePlantsCacheUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * The [ViewModel] for fetching a list of [Plant]s.
 */
@HiltViewModel
class PlantListViewModel @Inject constructor(
    private val getPlantsUseCase: GetPlantsUseCase,
    private val updatePlantsCacheUseCase: UpdatePlantsCacheUseCase
) : ViewModel() {

    /**
     * Request a snackbar to display a string.
     *
     * This variable is private because we don't want to expose [MutableLiveData].
     *
     * MutableLiveData allows anyone to set a value, and [PlantListViewModel] is the only
     * class that should be setting values.
     */
    private val _snackbar = MutableLiveData<String?>()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)
    /**
     * Show a loading spinner if true
     */
    val spinner: LiveData<Boolean>
        get() = _spinner

    /**
     * The current growZone selection.
     */
    private val growZoneFlow = MutableStateFlow(NoGrowZone)

    /**
     * A list of plants that updates based on the current filter.
     */
    val plantsFlow: LiveData<List<Plant>> = growZoneFlow
        .flatMapLatest { getPlantsUseCase(it) }
        .asLiveData()

    init {
        // When creating a new ViewModel, clear the grow zone and perform any related udpates
        clearGrowZoneNumber()
        loadDataFor(growZoneFlow) {
            updatePlantsCacheUseCase(it)
        }
    }

    /**
     * Filter the list to this grow zone.
     *
     * In the starter code version, this will also start a network request. After refactoring,
     * updating the grow zone will automatically kickoff a network request.
     */
    fun setGrowZoneNumber(num: Int) {
        growZoneFlow.value = GrowZone(num)
    }

    /**
     * Clear the current filter of this plants list.
     *
     * In the starter code version, this will also start a network request. After refactoring,
     * updating the grow zone will automatically kickoff a network request.
     */
    fun clearGrowZoneNumber() {
        growZoneFlow.value = NoGrowZone
    }

    /**
     * Return true iff the current list is filtered.
     */
    fun isFiltered() = growZoneFlow.value != NoGrowZone

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackbar.value = null
    }

    /**
     * Helper function to call a data load function with a loading spinner; errors will trigger a
     * snackbar.
     *
     * By marking [block] as [suspend] this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the viewModelScope. Before calling
     *              the lambda, the loading spinner will display. After completion or error, the
     *              loading spinner will stop.
     */
    private fun <T> loadDataFor(source: StateFlow<T>, block: suspend (T) -> Unit) {
        source.mapLatest {
            _spinner.value = true
            block(it)
        }
            .onEach { _spinner.value = false }
            .catch { throwable -> _snackbar.value = throwable.message }
            .launchIn(viewModelScope)
    }
}
