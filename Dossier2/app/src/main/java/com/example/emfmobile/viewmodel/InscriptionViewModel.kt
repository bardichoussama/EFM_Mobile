package com.example.emfmobile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emfmobile.model.Inscription
import com.example.emfmobile.network.RetrofitClient
import com.example.emfmobile.utils.showNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InscriptionViewModel : ViewModel() {

    private val _inscriptions = MutableStateFlow<List<Inscription>>(emptyList())
    val inscriptions: StateFlow<List<Inscription>> = _inscriptions

    private val _filterPriority = MutableStateFlow<String?>(null)
    val filterPriority: StateFlow<String?> = _filterPriority

    private val api = RetrofitClient.inscriptionApi

    init {
        fetchInscription()
    }

    // API GET
    fun fetchInscription() {
        viewModelScope.launch {
            try {
                val response = api.getInscription()
                _inscriptions.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Priorité dynamique
    fun setPriorityFilter(priority: String?) {
        _filterPriority.value = priority
    }

    fun getFilteredInscriptions(): List<Inscription> {
        val filter = filterPriority.value
        return if (filter.isNullOrEmpty()) {
            inscriptions.value
        } else {
            inscriptions.value.filter { it.priorite.equals(filter, ignoreCase = true) }
        }
    }

    // API POST
    fun addInscription(nom: String, priorite: String, context: Context) {
        if (nom.isBlank() || priorite.isBlank()) {
            showNotification(context, "Veuillez remplir tous les champs.")
            return
        }

        viewModelScope.launch {
            try {
                val newInscription = Inscription(
                    id = 0,
                    nom = nom,
                    statut = "En cours",
                    priorite = priorite
                )
                api.addInscription(newInscription)
                fetchInscription()
                showNotification(context, "Inscription ajoutée : $nom")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // API DELETE
    fun deleteInscription(id: Int, context: Context) {
        viewModelScope.launch {
            try {
                api.deleteInscription(id)
                fetchInscription()
                showNotification(context, "Inscription supprimée")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // API PUT : changement de statut
    fun toggleInscription(inscription: Inscription) {
        viewModelScope.launch {
            try {
                val newStatut = when (inscription.statut) {
                    "En cours" -> "Terminé"
                    "Terminé" -> "Annulé"
                    "Annulé" -> "En cours"
                    else -> "En cours"
                }

                val updatedLivraison = inscription.copy(statut = newStatut)
                api.updateInscription(inscription.id, updatedLivraison)
                fetchInscription()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // API PUT : modification complète
    fun updateInscription(id: Int, nom: String, priorite: String, statut: String, context: Context) {
        if (nom.isBlank() || priorite.isBlank()) {
            showNotification(context, "Veuillez remplir tous les champs.")
            return
        }

        viewModelScope.launch {
            try {
                val updated = Inscription(id, nom, statut, priorite)
                api.updateInscription(id, updated)
                fetchInscription()
                showNotification(context, "Inscription modifiée : $nom")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
