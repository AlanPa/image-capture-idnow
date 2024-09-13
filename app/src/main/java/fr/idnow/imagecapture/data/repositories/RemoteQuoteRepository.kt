package fr.idnow.imagecapture.data.repositories

import fr.idnow.imagecapture.data.source.remote.DummyJsonApiService
import fr.idnow.imagecapture.domain.entities.Quote
import fr.idnow.imagecapture.domain.repositories.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteQuoteRepository(
    private val dummyJsonApiService: DummyJsonApiService
): QuoteRepository {
    override fun getSingleQuote(): Flow<Quote> {
        return flow {
            emit(dummyJsonApiService.getSingleQuote().toEntity())
        }
    }
}