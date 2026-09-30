package com.example.nasaapod.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getApodList(
        @Query("api_key") apiKey: String,
        @Query("date_from") startDate: String,
        @Query("date_to") endDate: String
    ): List<ApodItem>

    companion object {
        private const val BASE_URL = "https://science.nasa.gov/"

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val instance: NasaApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NasaApiService::class.java)
        }
    }
}
