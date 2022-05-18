package fi.alalahti.deckofcards

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    var spades = false
    var diamonds = false
    var clubs = false
    var hearts = false
    var jokers = false
    var enabledColor = Color.parseColor("#d166ff")

    lateinit var createDeckButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createDeckButton = findViewById(R.id.createDeckButton)
    }

    fun selectSetting(button: View) {
        var unrecognizedID = false
        // Update settings, return updated value
        val settingEnabled : Boolean = when(button.id) {
            R.id.spadeButton -> {
                spades = !spades
                spades
            }
            R.id.diamondButton -> {
                diamonds = !diamonds
                diamonds
            }
            R.id.clubButton -> {
                clubs = !clubs
                clubs
            }
            R.id.heartButton -> {
                hearts = !hearts
                hearts
            }
            R.id.jokerButton -> {
                jokers = !jokers
                jokers
            }
            else -> {
                unrecognizedID = true
                false
            }
        }
        // Change button color if it had one of the recognized ids
        if (!unrecognizedID) {
            // The only way I found to change the color of an ImageButton without destroying it's existing styling
            if (settingEnabled) {
                (button as ImageButton).background.setColorFilter(enabledColor, PorterDuff.Mode.SRC)
            } else {
                (button as ImageButton).background.clearColorFilter()
            }
        }

        // Disable createDeckButton if no settings are enabled
        createDeckButton.isEnabled = spades || diamonds || clubs || hearts || jokers
    }

    fun createDeck(button : View) {
        APIService.getInstance().createDeck().enqueue(APICallback {
            val intent = Intent(this, DeckViewerActivity::class.java)
            intent.putExtra("deck_id", it.deck_id)
            intent.putExtra("remaining", it.remaining.toInt())
            startActivity(intent)
        })
    }
}