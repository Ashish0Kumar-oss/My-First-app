package com.example.features.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.MainViewModel
import com.example.features.home.PdfItemRow
import com.example.models.PdfDocumentEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onPdfClick: (PdfDocumentEntity) -> Unit
) {
    val favoritePdfs by viewModel.favoritePdfs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") }
            )
        }
    ) { padding ->
        if (favoritePdfs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorite PDFs", color = MaterialTheme.colorScheme.tertiary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoritePdfs) { pdf ->
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
