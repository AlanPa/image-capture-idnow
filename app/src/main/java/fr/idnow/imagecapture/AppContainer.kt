package fr.idnow.imagecapture

import fr.idnow.imagecapture.data.repositories.RemoteQuoteRepository
import fr.idnow.imagecapture.data.source.remote.DummyJsonApiService
import fr.idnow.imagecapture.domain.usecases.GetSingleQuoteUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {

    private val dummyJsonUrl = "https://dummyjson.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(dummyJsonUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DummyJsonApiService::class.java)

    private val remoteQuoteRepository = RemoteQuoteRepository(retrofit)
    val getSingleQuoteUseCase = GetSingleQuoteUseCase(remoteQuoteRepository)
}