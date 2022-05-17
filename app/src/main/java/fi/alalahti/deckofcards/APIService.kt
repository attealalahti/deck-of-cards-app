package fi.alalahti.deckofcards

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

val BASE_URL = "https://deckofcardsapi.com/api/"

data class Deck(
    val success: Boolean,
    val deck_id: String,
    val shuffled: Boolean,
    val remaining: Number
)

interface APIService {
    @GET("deck/new/shuffle/?deck_count=1")
    fun createDeck(): Call<Deck>

    companion object {
        var apiService: APIService? = null
        fun getInstance(): APIService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}