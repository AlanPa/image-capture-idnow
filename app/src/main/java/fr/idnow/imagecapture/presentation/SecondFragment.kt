package fr.idnow.imagecapture.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import fr.idnow.imagecapture.MainActivity
import fr.idnow.imagecapture.R
import fr.idnow.imagecapture.databinding.FragmentSecondBinding
import fr.idnow.imagecapture.presentation.viewmodels.QuoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var quoteViewModel: QuoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val appContainer = (activity as MainActivity).appContainer

        quoteViewModel = QuoteViewModel(appContainer.getSingleQuoteUseCase, Dispatchers.IO)

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
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