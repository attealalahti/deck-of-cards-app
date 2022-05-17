package fi.alalahti.deckofcards

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.concurrent.thread

class API {
    companion object {
        fun createDeck(callback: (Deck) -> Unit) {
            thread {
                try {
                    APIService.getInstance().createDeck().enqueue(object:
                        Callback<Deck> {
                        override fun onResponse(call: Call<Deck>?, response: Response<Deck>?) {
                            if (response?.body() != null) {
                                callback(response.body() as Deck)
                            }
                        }
                        override fun onFailure(call: Call<Deck>, t: Throwable) {
                            TODO("Not yet implemented")
                        }
                    })
                } catch (e: Exception) {
                    Log.d("api", e.message.toString())
                }
            }
        }
    }
}