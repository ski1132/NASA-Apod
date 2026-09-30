package com.example.nasaapod.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaapod.data.ApodItem
import com.example.nasaapod.data.NasaApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class ApodUiState(
    val selectedYear: Int = YearMonth.now().year,
    val selectedMonth: Int = YearMonth.now().month.value,
    val isLoading: Boolean = false,
    val apodList: List<ApodItem> = emptyList(),
    val errorMessage: String? = null
)

class ApodViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    private val _selectedItem = MutableStateFlow<ApodItem?>(null)
    val selectedItem: StateFlow<ApodItem?> = _selectedItem.asStateFlow()

    private val apiKey = "ywkZAuhJjvPrBhx45POmEn2iwgeExs8FsEwI3iDk"

    init {
        loadApodData(YearMonth.now().year, YearMonth.now().month.value)
    }

    fun selectItem(item: ApodItem?) {
        _selectedItem.value = item
    }

    fun onYearMonthSelected(year: Int, month: Int) {
        _uiState.update { it.copy(selectedYear = year, selectedMonth = month) }
        loadApodData(year, month)
    }

    fun loadApodData(year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val yearMonth = YearMonth.of(year, month)
                val startDate = "$year-${String.format("%02d", month)}-01"

                val today = LocalDate.now()
                val lastDayOfMonth = yearMonth.atEndOfMonth()
                val endDateObj = if (yearMonth == YearMonth.from(today)) {
                    today
                } else if (yearMonth.isAfter(YearMonth.from(today))) {
                    today
                } else {
                    lastDayOfMonth
                }
                val endDate = endDateObj.toString()

                if (LocalDate.parse(startDate).isAfter(today)) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            apodList = emptyList(),
                            errorMessage = "Cannot select a future date."
                        )
                    }
                    return@launch
                }

                Log.d(
                    "NASA_APOD",
                    "Requesting APOD data for start_date=$startDate, end_date=$endDate"
                )
                val startDateFormat = startDate.drop(2).replace("-", "")
                val ebdDateFormat = endDate.drop(2).replace("-", "")
                val response = NasaApiService.instance.getApodList(
                    apiKey = apiKey,
                    startDate = startDateFormat,
                    endDate = ebdDateFormat,
                )

                Log.d("NASA_APOD", "Response received: count=${response.size}")
                Log.d("NASA_APOD", "Response body: $response")

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        apodList = response.sortedBy { item -> item.date }
                    )
                }
            } catch (e: Exception) {
                Log.e("NASA_APOD", "Error fetching APOD data", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to load APOD data"
                    )
                }
            }
        }
    }
}
