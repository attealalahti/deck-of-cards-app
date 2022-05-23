package fi.alalahti.deckofcards

import android.content.Intent
import android.content.res.Configuration
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat

// Activity that the app starts in.
// Has five buttons for selecting which cards should
// be included in a created deck and
// a button that creates that deck
// and opens the Deck Viewer Activity.
class DeckCreatorActivity : AppCompatActivity() {

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

    lateinit var layout: View
    lateinit var createDeckButton: Button

    lateinit var spadeButton: ImageButton
    lateinit var diamondButton: ImageButton
    lateinit var clubButton: ImageButton
    lateinit var heartButton: ImageButton
    lateinit var jokerButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.mainActivityLayout)
        createDeckButton = findViewById(R.id.createDeckButton)
        spadeButton = findViewById(R.id.spadeButton)
        diamondButton = findViewById(R.id.diamondButton)
        clubButton = findViewById(R.id.clubButton)
        heartButton = findViewById(R.id.heartButton)
        jokerButton = findViewById(R.id.jokerButton)
    }

    // nameId is the id of the string in string resources that should be used
    inner class Setting(val nameId: Int, val checked: Boolean)

    // Triggered when clicking one of the five settings buttons.
    // Toggles the button's setting, changes its color and
    // enables/disables the Create Deck button when appropriate.
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
            changeButtonColor(button as ImageButton, setting.checked, getString(setting.nameId))
        }

        // Update whether or not the Create Deck button should be disabled.
        updateCreateDeckButton()
    }

    // Changes the color of a button based on if it should be checked or not.
    // Updates the content description of the button to reflect the change.
    fun changeButtonColor(button: ImageButton, checked: Boolean, buttonName: String) {
        if (checked) {
            // Check device is in night mode or not, choose color based on that
            val colorId: Int = if(resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                R.color.purple_200 else R.color.purple_500
            val enabledColor = ContextCompat.getColor(applicationContext, colorId)
            // The only way I found to change the color of an ImageButton without destroying it's existing styling
            button.background.setColorFilter(enabledColor, PorterDuff.Mode.SRC)
            button.contentDescription = "$buttonName ${getString(R.string.button_selected)}"
        } else {
            button.background.clearColorFilter()
            button.contentDescription = "$buttonName ${getString(R.string.button_unselected)}"
        }
    }

    // Disables Create Deck button if no settings are checked, otherwise enables it.
    fun updateCreateDeckButton() {
        createDeckButton.isEnabled = spades || diamonds || clubs || hearts || jokers
    }

    // Triggered by the Create Deck button.
    // Opens the Deck Viewer Activity with the currently chosen settings.
    fun createDeck(button : View) {
        // Create the settings map with card codes of the different suits based on the chosen settings.
        val settings : MutableMap<String, String> = mutableMapOf()
        settings["cards"] = "${if (spades) SPADE_CODES else ""},${if (diamonds) DIAMOND_CODES else ""},${if (clubs) CLUB_CODES else ""},${if (hearts) HEART_CODES else ""},${if (jokers) JOKER_CODES else ""}"
        settings["jokers_enabled"] = jokers.toString()
        // The settings map is used as query parameters for the API call.
        APIService.getInstance().createDeck(settings).enqueue(APICallback(layout) {
            if (it.success) {
                val intent = Intent(this, DeckViewerActivity::class.java)
                // Open Deck Viewer with the received deck id and how many cards are in the deck
                intent.putExtra("deck_id", it.deck_id)
                intent.putExtra("remaining", it.remaining.toInt())
                startActivity(intent)
            } else {
                ErrorMessenger.showErrorMessage(layout)
            }
        })
    }

    // Saves the state of the settings so they can be restored if the activity is destroyed,
    // like when device orientation changes.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("spades", spades)
        outState.putBoolean("diamonds", diamonds)
        outState.putBoolean("clubs", clubs)
        outState.putBoolean("hearts", hearts)
        outState.putBoolean("jokers", jokers)
        super.onSaveInstanceState(outState)
    }

    // Restores settings and changes button colors to match them.
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