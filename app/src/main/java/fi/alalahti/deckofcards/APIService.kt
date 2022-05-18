package fi.alalahti.deckofcards

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

val BASE_URL = "https://deckofcardsapi.com/api/"

data class Deck(
    val success: Boolean,
    val deck_id: String,
    val shuffled: Boolean,
    val remaining: Number
)
data class Draw(
    val success: Boolean,
    val cards: List<Card>,
    val deck_id: String,
    val remaining: Number
)
data class Card(
    val image: String,
    val value: String,
    val suit: String,
    val code: String
)

interface APIService {
    @GET("deck/new/shuffle/?deck_count=1")
    fun createDeck(): Call<Deck>

    @GET("deck/{deck_id}/draw/?count=1")
    fun drawCard(@Path("deck_id") deck_id: String): Call<Draw>

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