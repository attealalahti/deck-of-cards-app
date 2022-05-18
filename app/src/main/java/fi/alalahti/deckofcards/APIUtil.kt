package fi.alalahti.deckofcards

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.concurrent.thread

class APIUtil {
    companion object {
        fun createDeck(callback: (Deck) -> Unit) {
            try {
                APIService.getInstance().createDeck().enqueue(APICallback<Deck>(callback))
            } catch (e: Exception) {
                Log.d("api", e.message.toString())
            }
        }
        fun drawCard(deck_id: String, callback: (Draw) -> Unit) {
            try {
                APIService.getInstance().drawCard(deck_id).enqueue(APICallback<Draw>(callback))
            } catch (e: Exception) {
                Log.d("api", e.message.toString())
            }
        }
    }
}