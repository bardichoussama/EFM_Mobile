package com.example.todoappefm.model

data class Livraison(
    val id: Int,
    val nom: String,
    val statut: String, // "En cours", "Terminé", etc.
    val priorite: String
)
