package com.example.wordmemory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.data.local.room.WordBundleEntity
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.ui.viewmodel.WordViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    repository: WordRepository,
    viewModel: WordViewModel,
    onNavigateToNewBundle: () -> Unit,
    onNavigateToStudy: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val bundles by repository.getAllBundles().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewBundle) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("My Word Bundles", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(bundles, key = { it.id }) { bundle ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = { onNavigateToStudy(bundle.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(bundle.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                            
                            Row {
                                IconButton(onClick = { 
                                    scope.launch { repository.deleteBundle(bundle) } 
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                                TextButton(onClick = { onNavigateToEdit(bundle.id) }) {
                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
