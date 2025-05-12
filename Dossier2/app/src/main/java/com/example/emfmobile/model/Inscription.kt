package com.example.emfmobile.model

data class Inscription(
    val id: Int,
    val nom: String,
    val statut: String, // "En cours", "Terminé", etc.
    val priorite: String
)
