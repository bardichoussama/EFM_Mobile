package com.example.emfmobile.network


import com.example.emfmobile.api.InscriptionApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://681f86e272e59f922ef68322.mockapi.io/"

    val inscriptionApi: InscriptionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InscriptionApi::class.java)
    }
}