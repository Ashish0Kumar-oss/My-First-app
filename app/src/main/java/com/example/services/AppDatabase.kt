package com.example.services

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.models.PdfDocumentEntity

@Database(entities = [PdfDocumentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pdfDao(): PdfDao
}
