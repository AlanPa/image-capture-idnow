package fr.idnow.imagecapture.domain.usecases

import fr.idnow.imagecapture.domain.entities.Quote
import fr.idnow.imagecapture.domain.repositories.QuoteRepository
import kotlinx.coroutines.flow.Flow

class GetSingleQuoteUseCase(
    private val quoteRepository: QuoteRepository
) {

    fun getSingleQuote(): Flow<Quote> {
        return quoteRepository.getSingleQuote()
    }
}