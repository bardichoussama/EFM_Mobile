package com.example.todoappefm.api

import com.example.todoappefm.model.Livraison
import retrofit2.http.*

interface LivraisonApi {

    @GET("livraisons")
    suspend fun getLivraisons(): List<Livraison>

    @POST("livraisons")
    suspend fun addLivraison(@Body livraison: Livraison): Livraison

    @DELETE("livraisons/{id}")
    suspend fun deleteLivraison(@Path("id") id: Int)

    @PUT("livraisons/{id}")
    suspend fun updateLivraison(@Path("id") id: Int, @Body livraison: Livraison): Livraison
}