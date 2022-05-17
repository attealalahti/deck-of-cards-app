package fi.alalahti.deckofcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun createDeck(button : View) {
        API.createDeck {
            val intent = Intent(this, DeckViewerActivity::class.java)
            intent.putExtra("deck_id", it.deck_id)
            startActivity(intent)
        }
    }
}