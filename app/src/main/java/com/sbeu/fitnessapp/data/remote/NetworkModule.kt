package com.sbeu.fitnessapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    const val BASE_URL = "https://ref.test.kolsa.ru/"

    val api: FitnessApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FitnessApi::class.java)
    }
}