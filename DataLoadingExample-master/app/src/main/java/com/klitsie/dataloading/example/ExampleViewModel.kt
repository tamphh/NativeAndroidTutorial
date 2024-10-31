package com.klitsie.dataloading.example

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klitsie.dataloading.DataLoader
import com.klitsie.dataloading.LoadingResult
import com.klitsie.dataloading.RefreshTrigger
import com.klitsie.dataloading.loading
import com.klitsie.dataloading.loadingSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Result.Companion.success

@Immutable
sealed interface ExampleEvent {

	data object ShowRefreshFailure : ExampleEvent

}

@Stable
class ExampleViewModel(
	exampleDataLoader: ExampleDataLoader = DefaultExampleDataLoader(),
	private val exampleDataMapper: ExampleDataMapper = ExampleDataMapper,
	private val refreshTrigger: RefreshTrigger = RefreshTrigger(),
) : ViewModel() {

	private val _event = MutableStateFlow<ExampleEvent?>(null)
	val event = _event.asStateFlow()

/*
	private val data = exampleDataLoader.loadAndObserveData(
		coroutineScope = viewModelScope,
		refreshTrigger = refreshTrigger,
		onRefreshFailure = { throwable ->
			println(throwable)
			_event.update { ExampleEvent.ShowRefreshFailure }
		},
	)
*/
	private val data = MutableStateFlow(LoadingResult.Loading)

	val screenState = data.map { exampleDataMapper.map(it) }.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(),
		initialValue = exampleDataMapper.map(data.value),
	)

	init {
		val dataLoader = DataLoader<Int>()

		viewModelScope.launch {
			dataLoader.loadAndObserveData(
				initialData = loading(),
				observeData = {v -> flowOf(2 + v,3 + v) },
				fetchData = { success(10) },
			).collect {
				println(it)
				Log.e("tamphh", it.toString())
				/* Prints:
                Loading
                Success(value = 1, isLoading = false),
                Success(value = 2, isLoading = false),
                Success(value = 3, isLoading = false),
                */
			}

			Log.e("tamphh", "----------------------------------------------")
			dataLoader.loadAndObserveData(
				initialData = loading(),
				fetchData = { success(1) },
			).collect {
				println(it)
				Log.e("tamphh", it.toString())
				/* Prints:
                Loading
                Success(value = 1, isLoading = false),
                Success(value = 2, isLoading = false),
                Success(value = 3, isLoading = false),
                */
			}
			/*dataLoader.loadAndObserveData(
				initialData = loadingSuccess(1),
				observeData = { flowOf(2,3) },
				fetchData = { success(5) },
			).collect {
				println(it)
				Log.e("tamphh", it.toString())
				*//* Prints:
                Success(value = 1, isLoading = false),
                Success(value = 2, isLoading = false),
                Success(value = 3, isLoading = false),
                *//*
			}*/
		}
	}

	fun refresh() {
		viewModelScope.launch {
			refreshTrigger.refresh()
		}
	}

	fun consumeEvent() {
		_event.update { null }
	}


}