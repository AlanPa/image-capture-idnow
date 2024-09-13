package fr.idnow.imagecapture.data.source.remote

import fr.idnow.imagecapture.data.dto.QuoteDTO
import retrofit2.http.GET

interface DummyJsonApiService {
    @GET("quotes/random")
    suspend fun getSingleQuote(): QuoteDTO
}