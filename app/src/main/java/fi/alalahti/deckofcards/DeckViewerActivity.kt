package fi.alalahti.deckofcards

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import kotlin.concurrent.thread

class DeckViewerActivity : AppCompatActivity() {

    lateinit var cardImageView: ImageView
    lateinit var remainingCounter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        cardImageView = findViewById(R.id.cardImageView)
        remainingCounter = findViewById(R.id.remainingCounter)

        // Initialize how many cards remain in the deck
        var remaining = intent.getIntExtra("remaining", -1)
        if (remaining != -1) {
            updateRemainingCounter(remaining)
        }
    }

    fun drawCard(button: View) {
        val deck_id = intent.getStringExtra("deck_id")
        if (deck_id != null) {
            APIService.getInstance().drawCard(deck_id).enqueue(APICallback {
                Log.d("api", it.toString())

                // Update imageView with the drawn card's image
                thread {
                    try {
                        val stream = java.net.URL(it.cards[0].image).openStream()
                        val newImage = BitmapFactory.decodeStream(stream)
                        runOnUiThread {
                            cardImageView.setImageBitmap(newImage)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Update counter with how many cards remain in the deck
                updateRemainingCounter(it.remaining)

                // Disable after all cards have been drawn
                if (it.remaining.toInt() <= 0) {
                    button.isEnabled = false
                }
            })
        }
    }

    fun updateRemainingCounter(count: Number) {
        runOnUiThread {
            remainingCounter.text = "Cards remaining: ${count}"
        }
    }

}