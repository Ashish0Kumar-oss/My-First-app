package com.example

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.models.CompressionType
import com.example.models.PdfDocumentEntity
import com.example.services.AppDatabase
import com.example.services.PdfGeneratorService
import com.example.services.PdfRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "pdf-database"
    ).build()

    private val repository = PdfRepository(db.pdfDao())

    val allPdfs: StateFlow<List<PdfDocumentEntity>> = repository.allPdfs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritePdfs: StateFlow<List<PdfDocumentEntity>> = repository.favoritePdfs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages = _selectedImages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()
    
    private val _compressionType = MutableStateFlow(CompressionType.HIGH)
    val compressionType = _compressionType.asStateFlow()

    fun setCompressionType(type: CompressionType) {
        _compressionType.value = type
    }

    fun addImages(uris: List<Uri>) {
        val current = _selectedImages.value.toMutableList()
        current.addAll(uris)
        _selectedImages.value = current
    }

    fun removeImage(uri: Uri) {
        val current = _selectedImages.value.toMutableList()
        current.remove(uri)
        _selectedImages.value = current
    }
    
    fun clearImages() {
        _selectedImages.value = emptyList()
    }
    
    fun moveImage(fromIndex: Int, toIndex: Int) {
        val current = _selectedImages.value.toMutableList()
        if (fromIndex in current.indices && toIndex in current.indices) {
            val item = current.removeAt(fromIndex)
            current.add(toIndex, item)
            _selectedImages.value = current
        }
    }

    fun generatePdf(fileName: String, onComplete: (File?) -> Unit) {
        if (_selectedImages.value.isEmpty()) return
        
        viewModelScope.launch {
            _isProcessing.value = true
            val file = PdfGeneratorService.createPdfFromImages(
                context = getApplication(),
                imageUris = _selectedImages.value,
                fileName = fileName,
                compressionQuality = _compressionType.value.quality
            )
            
            if (file != null) {
                val entity = PdfDocumentEntity(
                    id = UUID.randomUUID().toString(),
                    fileName = file.name,
                    filePath = file.absolutePath,
                    fileSize = file.length(),
                    pageCount = _selectedImages.value.size
                )
                repository.insert(entity)
            }
            
            _isProcessing.value = false
            onComplete(file)
        }
    }
    
    fun toggleFavorite(pdf: PdfDocumentEntity) {
        viewModelScope.launch {
            repository.update(pdf.copy(isFavorite = !pdf.isFavorite))
        }
    }
    
    fun deletePdf(pdf: PdfDocumentEntity) {
        viewModelScope.launch {
            repository.deleteById(pdf.id)
            val file = File(pdf.filePath)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
