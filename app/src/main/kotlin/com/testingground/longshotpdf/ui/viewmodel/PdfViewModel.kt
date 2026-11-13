package com.testingground.longshotpdf.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.testingground.longshotpdf.domain.ConversionResult
import com.testingground.longshotpdf.domain.ImageMetadata
import com.testingground.longshotpdf.domain.PdfConverterEngine
import com.testingground.longshotpdf.domain.PdfMode
import com.testingground.longshotpdf.domain.QualityPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PdfUiState(
    val selectedUris: List<Uri> = emptyList(),
    val metadataList: List<ImageMetadata> = emptyList(),
    val documentTitle: String = "LongShot_Export",
    val mode: PdfMode = PdfMode.SEAMLESS_SINGLE_PAGE,
    val quality: QualityPreset = QualityPreset.ORIGINAL_LOSSLESS,
    val isConverting: Boolean = false,
    val progress: Float = 0f,
    val result: ConversionResult? = null,
    val errorMessage: String? = null
)

class PdfViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = PdfConverterEngine(application)

    private val _uiState = MutableStateFlow(PdfUiState())
    val uiState: StateFlow<PdfUiState> = _uiState.asStateFlow()

    fun selectImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            val newUris = (_uiState.value.selectedUris + uris).distinct()
            val metaList = newUris.mapNotNull { uri -> engine.inspectImage(uri) }
            _uiState.update {
                it.copy(
                    selectedUris = newUris,
                    metadataList = metaList,
                    errorMessage = null
                )
            }
        }
    }

    fun removeImage(index: Int) {
        val currentUris = _uiState.value.selectedUris.toMutableList()
        val currentMeta = _uiState.value.metadataList.toMutableList()
        if (index in currentUris.indices) {
            currentUris.removeAt(index)
            if (index in currentMeta.indices) currentMeta.removeAt(index)
            _uiState.update {
                it.copy(
                    selectedUris = currentUris,
                    metadataList = currentMeta
                )
            }
        }
    }

    fun clearAllImages() {
        _uiState.update {
            it.copy(
                selectedUris = emptyList(),
                metadataList = emptyList(),
                result = null,
                errorMessage = null
            )
        }
    }

    fun setMode(mode: PdfMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun setQuality(quality: QualityPreset) {
        _uiState.update { it.copy(quality = quality) }
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(documentTitle = title) }
    }

    fun dismissResult() {
        _uiState.update { it.copy(result = null) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun generatePdf() {
        val state = _uiState.value
        if (state.selectedUris.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please select at least one screenshot") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isConverting = true, progress = 0f, errorMessage = null) }
            val outcome = engine.convertToPdf(
                imageUris = state.selectedUris,
                title = state.documentTitle,
                mode = state.mode,
                quality = state.quality,
                onProgress = { p ->
                    _uiState.update { it.copy(progress = p) }
                }
            )

            outcome.fold(
                onSuccess = { res ->
                    _uiState.update {
                        it.copy(isConverting = false, result = res, progress = 1f)
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isConverting = false,
                            errorMessage = err.localizedMessage ?: "Failed to convert PDF"
                        )
                    }
                }
            )
        }
    }
}
