package fi.alalahti.deckofcards

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeckViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        findViewById<TextView>(R.id.textView).text = intent.getStringExtra("deck_id")
    }

    fun drawCard(button: View) {
        val deck_id = intent.getStringExtra("deck_id")
        if (deck_id != null) {
            APIService.getInstance().drawCard(deck_id).enqueue(APICallback {
                Log.d("api", it.toString())
            })
        }
    }

}