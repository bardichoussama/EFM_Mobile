package com.example.todoappefm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoappefm.model.Livraison
import com.example.todoappefm.viewmodel.LivraisonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LivraisonApp()
        }
    }
}

@Composable
fun LivraisonApp(viewModel: LivraisonViewModel = viewModel()) {
    val livraisons by viewModel.livraisons.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var nom by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    val priorities = listOf("Haute", "Moyenne", "Basse")
    var expandedPriority by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    var selectedPriorityFilter by remember { mutableStateOf<String?>("Toutes") }
    var expandedFilter by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Champs d'ajout
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Priority selection dropdown
            Box(
                modifier = Modifier
                    .weight(1f)
                    .zIndex(1f)
            ) {
                OutlinedTextField(
                    value = selectedPriority ?: "Choisir priorité",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priorité") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Menu",
                            modifier = Modifier.clickable { expandedPriority = !expandedPriority }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedPriority = !expandedPriority }
                )

                DropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false },
                    offset = DpOffset(0.dp, 0.dp),
                    properties = PopupProperties(focusable = true),
                    modifier = Modifier.width(with(LocalDensity.current) {
                        (LocalDensity.current.density * 180).toInt().dp
                    })
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                selectedPriority = priority
                                expandedPriority = false
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (nom.isNotBlank() && selectedPriority != null) {
                viewModel.addLivraison(nom, selectedPriority!!, context)
                nom = ""
                selectedPriority = null
                error = false
            } else {
                error = true
            }
        }) {
            Text("Ajouter")
        }

        if (error) {
            Text("Veuillez remplir tous les champs", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            OutlinedTextField(
                value = selectedPriorityFilter ?: "Toutes",
                onValueChange = {},
                readOnly = true,
                label = { Text("Filtrer par priorité") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Menu",
                        modifier = Modifier.clickable { expandedFilter = !expandedFilter }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedFilter = !expandedFilter }
            )

            DropdownMenu(
                expanded = expandedFilter,
                onDismissRequest = { expandedFilter = false },
                offset = DpOffset(0.dp, 0.dp),
                properties = PopupProperties(focusable = true),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                val filterOptions = listOf("Toutes") + priorities
                filterOptions.forEach { priority ->
                    DropdownMenuItem(
                        text = { Text(priority) },
                        onClick = {
                            selectedPriorityFilter = priority
                            val filterValue = if (priority == "Toutes") null else priority
                            viewModel.setPriorityFilter(filterValue)
                            expandedFilter = false
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Filtered list updated reactively
        val filteredLivraisons by remember(livraisons, selectedPriorityFilter) {
            derivedStateOf {
                livraisons.filter {
                    selectedPriorityFilter == "Toutes" || selectedPriorityFilter == null || it.priorite == selectedPriorityFilter
                }
            }
        }

        LazyColumn {
            items(filteredLivraisons) { livraison ->
                LivraisonItem(livraison, viewModel)
            }
        }
    }
}

@Composable
fun LivraisonItem(livraison: Livraison, viewModel: LivraisonViewModel) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = livraison.nom, style = MaterialTheme.typography.titleLarge)
            Text(text = "Priorité: ${livraison.priorite}")
            Text(text = "Statut: ${livraison.statut}")
        }

        Row {
            IconButton(onClick = {
                viewModel.toggleLivraison(livraison)
            }) {
                Icon(
                    imageVector = when (livraison.statut) {
                        "Terminé" -> Icons.Default.CheckCircle
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = "Changer statut"
                )
            }

            IconButton(onClick = {
                viewModel.deleteLivraison(livraison.id, context)
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer"
                )
            }
        }
    }
}
