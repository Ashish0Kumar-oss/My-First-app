package com.example.services

import com.example.models.PdfDocumentEntity
import kotlinx.coroutines.flow.Flow

class PdfRepository(private val pdfDao: PdfDao) {
    val allPdfs: Flow<List<PdfDocumentEntity>> = pdfDao.getAllPdfs()
    val favoritePdfs: Flow<List<PdfDocumentEntity>> = pdfDao.getFavoritePdfs()

    suspend fun insert(pdf: PdfDocumentEntity) = pdfDao.insertPdf(pdf)
    suspend fun update(pdf: PdfDocumentEntity) = pdfDao.updatePdf(pdf)
    suspend fun deleteById(id: String) = pdfDao.deletePdfById(id)
}
