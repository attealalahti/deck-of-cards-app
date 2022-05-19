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

    val SPADE_CODES = "AS,2S,3S,4S,5S,6S,7S,8S,9S,0S,JS,QS,KS"
    val DIAMOND_CODES = "AD,2D,3D,4D,5D,6D,7D,8D,9D,0D,JD,QD,KD"
    val CLUB_CODES = "AC,2C,3C,4C,5C,6C,7C,8C,9C,0C,JC,QC,KC"
    val HEART_CODES = "AH,2H,3H,4H,5H,6H,7H,8H,9H,0H,JH,QH,KH"
    val JOKER_CODES = "X1,X2"

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
        val settings : MutableMap<String, String> = mutableMapOf()
        settings["cards"] = "${if (spades) SPADE_CODES else ""},${if (diamonds) DIAMOND_CODES else ""},${if (clubs) CLUB_CODES else ""},${if (hearts) HEART_CODES else ""},${if (jokers) JOKER_CODES else ""}"
        settings["jokers_enabled"] = jokers.toString()

        APIService.getInstance().createDeck(settings).enqueue(APICallback {
            val intent = Intent(this, DeckViewerActivity::class.java)
            intent.putExtra("deck_id", it.deck_id)
            intent.putExtra("remaining", it.remaining.toInt())
            startActivity(intent)
        })
    }
}