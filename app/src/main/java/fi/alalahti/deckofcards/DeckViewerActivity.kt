package fi.alalahti.deckofcards

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DeckViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        val deck_id = intent.getStringExtra("deck_id")
        findViewById<TextView>(R.id.textView).text = deck_id
    }
}