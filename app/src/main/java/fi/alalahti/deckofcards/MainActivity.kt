package fi.alalahti.deckofcards

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
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

    lateinit var spadeButton: ImageButton
    lateinit var diamondButton: ImageButton
    lateinit var clubButton: ImageButton
    lateinit var heartButton: ImageButton
    lateinit var jokerButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createDeckButton = findViewById(R.id.createDeckButton)
        spadeButton = findViewById(R.id.spadeButton)
        diamondButton = findViewById(R.id.diamondButton)
        clubButton = findViewById(R.id.clubButton)
        heartButton = findViewById(R.id.heartButton)
        jokerButton = findViewById(R.id.jokerButton)
    }

    inner class Setting(val nameId: Int, val enabled: Boolean)

    fun selectSetting(button: View) {
        var unrecognizedID = false
        // Update settings, return updated value and the name of the pressed button
        val setting : Setting = when(button.id) {
            R.id.spadeButton -> {
                spades = !spades
                Setting(R.string.spade_button_name, spades)
            }
            R.id.diamondButton -> {
                diamonds = !diamonds
                Setting(R.string.diamond_button_name, diamonds)
            }
            R.id.clubButton -> {
                clubs = !clubs
                Setting(R.string.club_button_name, clubs)
            }
            R.id.heartButton -> {
                hearts = !hearts
                Setting(R.string.heart_button_name, hearts)
            }
            R.id.jokerButton -> {
                jokers = !jokers
                Setting(R.string.joker_button_name, jokers)
            }
            else -> {
                unrecognizedID = true
                Setting(R.string.unknown_button_name, false)
            }
        }

        // Change button color if it had one of the recognized ids
        if (!unrecognizedID) {
            changeButtonColor(button as ImageButton, setting.enabled, getString(setting.nameId))
        }

        updateCreateDeckButton()
    }

    fun changeButtonColor(button: ImageButton, enabled: Boolean, buttonName: String) {
        // The only way I found to change the color of an ImageButton without destroying it's existing styling
        if (enabled) {
            button.background.setColorFilter(enabledColor, PorterDuff.Mode.SRC)
            button.contentDescription = "$buttonName ${getString(R.string.button_selected)}"
        } else {
            button.background.clearColorFilter()
            button.contentDescription = "$buttonName ${getString(R.string.button_unselected)}"
        }
    }

    fun updateCreateDeckButton() {
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("spades", spades)
        outState.putBoolean("diamonds", diamonds)
        outState.putBoolean("clubs", clubs)
        outState.putBoolean("hearts", hearts)
        outState.putBoolean("jokers", jokers)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        spades = savedInstanceState.getBoolean("spades", false)
        diamonds = savedInstanceState.getBoolean("diamonds", false)
        clubs = savedInstanceState.getBoolean("clubs", false)
        hearts = savedInstanceState.getBoolean("hearts", false)
        jokers = savedInstanceState.getBoolean("jokers", false)
        changeButtonColor(spadeButton, spades, getString(R.string.spade_button_name))
        changeButtonColor(diamondButton, diamonds, getString(R.string.diamond_button_name))
        changeButtonColor(clubButton, clubs, getString(R.string.club_button_name))
        changeButtonColor(heartButton, hearts, getString(R.string.heart_button_name))
        changeButtonColor(jokerButton, jokers, getString(R.string.joker_button_name))
        updateCreateDeckButton()
    }
}