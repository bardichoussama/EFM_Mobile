package com.example.emfmobile.api

import com.example.emfmobile.model.Inscription
import retrofit2.http.*

interface InscriptionApi {

    @GET("insriptions")
    suspend fun getInscription(): List<Inscription>

    @POST("insriptions")
    suspend fun addInscription(@Body inscription: Inscription): Inscription

    @DELETE("insriptions/{id}")
    suspend fun deleteInscription(@Path("id") id: Int)

    @PUT("insriptions{id}")
    suspend fun updateInscription(@Path("id") id: Int, @Body inscription: Inscription): Inscription
}