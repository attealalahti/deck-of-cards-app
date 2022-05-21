package fi.alalahti.deckofcards

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class APICallback<T>(val layout: View, val callback: (T) -> Unit) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response?.body() != null) {
            callback(response.body() as T)
        } else {
            ErrorMessenger.showErrorMessage(layout)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        ErrorMessenger.showErrorMessage(layout)
    }
}
