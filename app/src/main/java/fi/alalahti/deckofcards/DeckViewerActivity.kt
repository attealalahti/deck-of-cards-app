package fi.alalahti.deckofcards

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import kotlin.concurrent.thread

class DeckViewerActivity : AppCompatActivity() {

    lateinit var cardImageView: ImageView
    lateinit var remainingCounter: TextView
    lateinit var shuffleButton: Button
    lateinit var drawButton: Button
    var deck_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        cardImageView = findViewById(R.id.cardImageView)
        remainingCounter = findViewById(R.id.remainingCounter)
        shuffleButton = findViewById(R.id.shuffleButton)
        drawButton = findViewById(R.id.drawButton)
        deck_id = intent.getStringExtra("deck_id")

        // Initialize how many cards remain in the deck
        val remaining = intent.getIntExtra("remaining", -1)
        if (remaining != -1) {
            updateRemainingCounter(remaining)
        }
    }

    fun drawCard(button: View) {
        if (deck_id != null) {
            APIService.getInstance().drawCard(deck_id!!).enqueue(APICallback {
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

                // Disable shuffle and draw after all cards have been drawn
                if (it.remaining.toInt() <= 0) {
                    drawButton.isEnabled = false
                    shuffleButton.isEnabled = false
                }
            })
        }
    }

    fun shuffle(button: View) {
        if (deck_id != null) {
            APIService.getInstance().shuffleDeck(deck_id!!).enqueue(APICallback {
                // Show a little pop-up when shuffling is complete
                Toast.makeText(applicationContext, "Shuffled!", Toast.LENGTH_SHORT).show()
            })
        }
    }

    fun updateRemainingCounter(count: Number) {
        runOnUiThread {
            remainingCounter.text = "Cards remaining: ${count}"
        }
    }

}