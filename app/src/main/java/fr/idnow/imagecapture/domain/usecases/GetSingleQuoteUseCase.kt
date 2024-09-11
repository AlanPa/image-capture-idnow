package fr.idnow.imagecapture.domain.usecases

import fr.idnow.imagecapture.domain.entities.Quote
import fr.idnow.imagecapture.domain.repositories.QuoteRepository
import fr.idnow.imagecapture.presentation.viewmodels.QuoteViewModel.QuoteUiState
import kotlinx.coroutines.flow.MutableStateFlow

class GetSingleQuoteUseCase {
    //val quoteRepository: QuoteRepository

    fun getSingleQuote(): MutableStateFlow<Quote>{
        //quoteRepository.getSingleQuote()
        return MutableStateFlow(Quote("This is the use case quote"))
    }
}