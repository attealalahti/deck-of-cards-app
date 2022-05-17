package fi.alalahti.deckofcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createDeck(button : View) {
        val intent = Intent(this, DeckViewerActivity::class.java)
        startActivity(intent)
    }
}