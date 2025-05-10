package com.example.todoappefm.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappefm.model.Livraison
import com.example.todoappefm.network.RetrofitClient
import com.example.todoappefm.utils.showNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LivraisonViewModel : ViewModel() {

    private val _livraisons = MutableStateFlow<List<Livraison>>(emptyList())
    val livraisons: StateFlow<List<Livraison>> = _livraisons

    private val _filterPriority = MutableStateFlow<String?>(null)
    val filterPriority: StateFlow<String?> = _filterPriority

    private val api = RetrofitClient.livraisonApi

    init {
        fetchLivraisons()
    }

    // API GET
    fun fetchLivraisons() {
        viewModelScope.launch {
            try {
                val response = api.getLivraisons()
                _livraisons.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Priorité dynamique
    fun setPriorityFilter(priority: String?) {
        _filterPriority.value = priority
    }

    fun getFilteredLivraisons(): List<Livraison> {
        val filter = filterPriority.value
        return if (filter.isNullOrEmpty()) {
            livraisons.value
        } else {
            livraisons.value.filter { it.priorite.equals(filter, ignoreCase = true) }
        }
    }

    // API POST
    fun addLivraison(nom: String, priorite: String, context: Context) {
        if (nom.isBlank() || priorite.isBlank()) {
            showNotification(context, "Veuillez remplir tous les champs.")
            return
        }

        viewModelScope.launch {
            try {
                val newLivraison = Livraison(
                    id = 0,
                    nom = nom,
                    statut = "En cours",
                    priorite = priorite
                )
                api.addLivraison(newLivraison)
                fetchLivraisons()
                showNotification(context, "Livraison ajoutée : $nom")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // API DELETE
    fun deleteLivraison(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                api.deleteLivraison(id)
                fetchLivraisons()
                showNotification(context, "Livraison supprimée")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // API PUT : changement de statut
    fun toggleLivraison(livraison: Livraison) {
        viewModelScope.launch {
            try {
                val newStatut = when (livraison.statut) {
                    "En cours" -> "Terminé"
                    "Terminé" -> "Annulé"
                    "Annulé" -> "En cours"
                    else -> "En cours"
                }

                val updatedLivraison = livraison.copy(statut = newStatut)
                api.updateLivraison(livraison.id, updatedLivraison)
                fetchLivraisons()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // API PUT : modification complète
    fun updateLivraison(id: Int, nom: String, priorite: String, statut: String, context: Context) {
        if (nom.isBlank() || priorite.isBlank()) {
            showNotification(context, "Veuillez remplir tous les champs.")
            return
        }

        viewModelScope.launch {
            try {
                val updated = Livraison(id, nom, statut, priorite)
                api.updateLivraison(id, updated)
                fetchLivraisons()
                showNotification(context, "Livraison modifiée : $nom")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
