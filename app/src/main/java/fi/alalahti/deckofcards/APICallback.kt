package fi.alalahti.deckofcards

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class APICallback<T>(val callback: (T) -> Unit) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response?.body() != null) {
            callback(response.body() as T)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        TODO("Not yet implemented")
    }
}
