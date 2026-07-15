package com.example.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PdfGeneratorService {

    suspend fun createPdfFromImages(
        context: Context,
        imageUris: List<Uri>,
        fileName: String,
        compressionQuality: Int = 100 // 0-100
    ): File? = withContext(Dispatchers.IO) {
        if (imageUris.isEmpty()) return@withContext null

        val pdfDocument = PdfDocument()

        try {
            imageUris.forEachIndexed { index, uri ->
                val bitmap = decodeBitmapFromUri(context, uri, compressionQuality)
                if (bitmap != null) {
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    
                    val canvas = page.canvas
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    
                    pdfDocument.finishPage(page)
                    bitmap.recycle()
                }
            }

            val docsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)
            if (docsDir != null && !docsDir.exists()) {
                docsDir.mkdirs()
            }
            
            val safeFileName = if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf"
            val file = File(docsDir, safeFileName)
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            return@withContext file
            
        } catch (e: Exception) {
            Log.e("PdfGenerator", "Error creating PDF", e)
            return@withContext null
        } finally {
            pdfDocument.close()
        }
    }

    private fun decodeBitmapFromUri(context: Context, uri: Uri, quality: Int): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (quality < 100 && originalBitmap != null) {
                // Compress bitmap
                val out = java.io.ByteArrayOutputStream()
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                val decoded = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
                originalBitmap.recycle()
                decoded
            } else {
                originalBitmap
            }
        } catch (e: Exception) {
            Log.e("PdfGenerator", "Error decoding bitmap", e)
            null
        }
    }
}
