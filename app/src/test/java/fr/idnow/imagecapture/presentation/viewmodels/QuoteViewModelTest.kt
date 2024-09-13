import fr.idnow.imagecapture.domain.entities.Quote
import fr.idnow.imagecapture.domain.usecases.GetSingleQuoteUseCase
import fr.idnow.imagecapture.presentation.viewmodels.QuoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class QuoteViewModelTest {

    private val getSingleQuoteUseCase: GetSingleQuoteUseCase =
        mock(GetSingleQuoteUseCase::class.java)

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: QuoteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun reset() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial quote is emitted correctly`() = runTest {
        viewModel = QuoteViewModel(getSingleQuoteUseCase, testDispatcher)

        val expected = QuoteViewModel.QuoteUiState.Success(Quote("This is the initial quote"))
        val result = viewModel.quoteUiState.value

        assertEquals(expected, result)
    }

    @Test
    fun `test success state is emitted when use case returns a quote`() = runTest {

        val mockQuote = Quote("Test Quote")
        val expected = QuoteViewModel.QuoteUiState.Success(mockQuote)

        `when`(getSingleQuoteUseCase.getSingleQuote()).thenReturn(flow {
            emit(mockQuote)
        })

        viewModel = QuoteViewModel(getSingleQuoteUseCase, testDispatcher)
        advanceUntilIdle()

        val result = viewModel.quoteUiState.value

        assertEquals(expected, result)
    }

    @Test
    fun `test error state is emitted when use case throws an exception`() = runTest {
        val exception = RuntimeException("Something went wrong")
        `when`(getSingleQuoteUseCase.getSingleQuote()).thenReturn(flow {
            throw exception
        })

        viewModel = QuoteViewModel(getSingleQuoteUseCase, testDispatcher)
        advanceUntilIdle()

        val expected = QuoteViewModel.QuoteUiState.Error(exception)
        val result = viewModel.quoteUiState.value

        assertEquals(expected, result)
    }
}
