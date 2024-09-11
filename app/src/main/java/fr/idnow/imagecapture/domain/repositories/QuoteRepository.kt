package fr.idnow.imagecapture.domain.repositories

import fr.idnow.imagecapture.domain.entities.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {

    fun getSingleQuote(): Flow<Quote>
}