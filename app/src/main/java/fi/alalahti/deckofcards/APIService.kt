package fi.alalahti.deckofcards

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

val BASE_URL = "https://deckofcardsapi.com/api/"

// Object returned by the API after creating or manipulating a deck
data class Deck(
    val success: Boolean,
    val deck_id: String,
    val shuffled: Boolean,
    val remaining: Number
)
// Object returned by the API after drawing a card
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

// Interface for making API calls using Retrofit
interface APIService {
    // Asks the API to create a new deck.
    // The settings map holds query parameters for the call,
    // telling it which cards should be in the created deck.
    // Returns information about the created deck.
    @GET("deck/new")
    fun createDeck(@QueryMap settings: Map<String, String>): Call<Deck>

    // Draw a card from a specified deck.
    // Returns information about the draw event,
    // including which card was drawn.
    @GET("deck/{deck_id}/draw/?count=1")
    fun drawCard(@Path("deck_id") deck_id: String): Call<Draw>

    // Shuffle the rest of the cards in a specified deck.
    // Returns information about the shuffled deck.
    @GET("deck/{deck_id}/shuffle/?remaining=true")
    fun shuffleDeck(@Path("deck_id") deck_id: String): Call<Deck>

    companion object {
        var apiService: APIService? = null
        // Get an instance of this interface.
        // If an instance doesn't exist already, build one with Retrofit
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