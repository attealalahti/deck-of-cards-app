package fi.alalahti.deckofcards

import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

// Implementation of Retrofit's Callback interface for easier use.
// If the response was successful, call the callback with the body of the response.
// Otherwise, display an error message.
// Layout View object is needed for the error message.
class APICallback<T>(val layout: View, val callback: (T) -> Unit) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            callback(response.body() as T)
        } catch (e: Exception) {
            ErrorMessenger.showErrorMessage(layout)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        ErrorMessenger.showErrorMessage(layout)
    }
}
