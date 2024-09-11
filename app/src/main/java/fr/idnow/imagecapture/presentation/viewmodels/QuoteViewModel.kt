package fr.idnow.imagecapture.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.idnow.imagecapture.domain.entities.Quote
import fr.idnow.imagecapture.domain.usecases.GetSingleQuoteUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(
    private val getSingleQuoteUseCase: GetSingleQuoteUseCase,
    private val dispatcher: CoroutineDispatcher
): ViewModel() {
   private val _quoteUiState: MutableStateFlow<QuoteUiState> = MutableStateFlow(QuoteUiState.Success(Quote("This is the initial quote")))
   val quoteUiState: StateFlow<QuoteUiState> = _quoteUiState.asStateFlow()

    init {
        getSingleQuote()
    }

    private fun getSingleQuote() {
        viewModelScope.launch(dispatcher) {
            getSingleQuoteUseCase.getSingleQuote()
                .collect { quote ->
                    _quoteUiState.value = QuoteUiState.Success(quote)
                }
        }
    }

    sealed class QuoteUiState {
        data class Success(val quote: Quote): QuoteUiState()
        data class Error(val exception: Throwable): QuoteUiState()
    }
}