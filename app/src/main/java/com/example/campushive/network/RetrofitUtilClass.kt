package com.example.campushive.network


import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitUtilClass {
    companion object {
        private lateinit var retrofit: Retrofit

        val API_BASE_URL_PROD = "http://localhost:4000"


        fun getRetrofit(): Retrofit {

            if (!::retrofit.isInitialized) {

                retrofit = Retrofit.Builder()
                    .baseUrl(API_BASE_URL_PROD)
                    .addConverterFactory(
                        GsonConverterFactory.create()
                    ).client(OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            chain.proceed(chain.request().newBuilder().also {
                            }.build())
                        }.also { client ->
//                            val logging = HttpLoggingInterceptor()
//                            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//                            client.addInterceptor(logging);
                        }.build()
                    ).build()
                return retrofit
            }
            return retrofit
        }

    }
}