package fi.alalahti.deckofcards

import android.view.View
import com.google.android.material.snackbar.Snackbar

class ErrorMessenger {
    companion object {
        fun showErrorMessage(layout: View) {
            Snackbar.make(layout, R.string.error_message, Snackbar.LENGTH_SHORT).show()
        }
    }
}