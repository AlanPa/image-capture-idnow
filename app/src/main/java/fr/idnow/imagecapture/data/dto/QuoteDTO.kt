package fr.idnow.imagecapture.data.dto

import android.util.Log
import fr.idnow.imagecapture.domain.entities.Quote

data class QuoteDTO(val id: Int, val quote: String, val author: String) {

    fun toEntity(): Quote{
        if(quote.isEmpty()) Log.w("QuoteDTO", "The quote is empty")
        return Quote(quote)
    }
}