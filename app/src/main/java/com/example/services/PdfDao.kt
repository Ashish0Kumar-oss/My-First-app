package com.example.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.models.PdfDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDao {
    @Query("SELECT * FROM pdfs ORDER BY creationTime DESC")
    fun getAllPdfs(): Flow<List<PdfDocumentEntity>>

    @Query("SELECT * FROM pdfs WHERE isFavorite = 1 ORDER BY creationTime DESC")
    fun getFavoritePdfs(): Flow<List<PdfDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPdf(pdf: PdfDocumentEntity)

    @Update
    suspend fun updatePdf(pdf: PdfDocumentEntity)

    @Query("DELETE FROM pdfs WHERE id = :id")
    suspend fun deletePdfById(id: String)
}
