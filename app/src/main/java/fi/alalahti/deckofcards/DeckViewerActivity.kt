package fi.alalahti.deckofcards

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import kotlin.concurrent.thread

class DeckViewerActivity : AppCompatActivity() {

    lateinit var cardImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        cardImageView = findViewById(R.id.cardImageView)
    }

    fun drawCard(button: View) {
        val deck_id = intent.getStringExtra("deck_id")
        if (deck_id != null) {
            APIService.getInstance().drawCard(deck_id).enqueue(APICallback {
                Log.d("api", it.toString())

                // Update imageView with the drawn card's image
                var newImage: Bitmap? = null
                thread {
                    try {
                        val stream = java.net.URL(it.cards[0].image).openStream()
                        newImage = BitmapFactory.decodeStream(stream)
                        runOnUiThread {
                            cardImageView.setImageBitmap(newImage)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

}