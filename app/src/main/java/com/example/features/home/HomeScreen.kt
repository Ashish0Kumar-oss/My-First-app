package com.example.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.models.PdfDocumentEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToConverter: () -> Unit,
    onNavigateToRecent: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onPdfClick: (PdfDocumentEntity) -> Unit
) {
    val recentPdfs by viewModel.allPdfs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Image to PDF\nConverter",
            fontSize = 30.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.5).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "High quality conversion, instantly.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            onClick = onNavigateToConverter,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f), RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Images", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Images", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "JPG • PNG • WEBP • HEIC",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionCard(
                title = "Recent Files",
                icon = Icons.Default.Description,
                onClick = onNavigateToRecent,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            QuickActionCard(
                title = "Favorites",
                icon = Icons.Default.PictureAsPdf,
                onClick = onNavigateToFavorites,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT FILES",
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "View All",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onNavigateToRecent() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (recentPdfs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent PDFs", color = MaterialTheme.colorScheme.tertiary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentPdfs.take(5)) { pdf ->
                    PdfItemRow(
                        pdf = pdf, 
                        onClick = { onPdfClick(pdf) },
                        onFavoriteClick = { viewModel.toggleFavorite(pdf) },
                        onDeleteClick = { viewModel.deletePdf(pdf) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PdfItemRow(pdf: PdfDocumentEntity, onClick: () -> Unit, onFavoriteClick: (PdfDocumentEntity) -> Unit = {}, onDeleteClick: (PdfDocumentEntity) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(pdf.fileName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            val date = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(pdf.creationTime))
            val sizeKb = pdf.fileSize / 1024
            Text("$sizeKb KB • $date", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary)
        }
        IconButton(onClick = { onFavoriteClick(pdf) }) {
            Icon(
                imageVector = if (pdf.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (pdf.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
            )
        }
        IconButton(onClick = { onDeleteClick(pdf) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
