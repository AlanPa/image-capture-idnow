package fr.idnow.imagecapture.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import fr.idnow.imagecapture.R
import fr.idnow.imagecapture.databinding.FragmentFirstBinding
import fr.idnow.imagecapture.domain.usecases.GetSingleQuoteUseCase
import fr.idnow.imagecapture.presentation.viewmodels.QuoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val quoteViewModel: QuoteViewModel = QuoteViewModel(GetSingleQuoteUseCase(), Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        lifecycleScope.launch {
            quoteViewModel.quoteUiState.collect { uiState ->
                when (uiState) {
                    is QuoteViewModel.QuoteUiState.Success -> binding.tvQuote.text =
                        uiState.quote.text

                    is QuoteViewModel.QuoteUiState.Error -> binding.tvQuote.text =
                        uiState.exception.localizedMessage
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}