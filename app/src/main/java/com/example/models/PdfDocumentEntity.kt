package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdfs")
data class PdfDocumentEntity(
    @PrimaryKey val id: String, // UUID or file path
    val fileName: String,
    val filePath: String,
    val fileSize: Long,
    val pageCount: Int,
    val creationTime: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
