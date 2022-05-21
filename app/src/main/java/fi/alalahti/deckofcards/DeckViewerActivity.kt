package fi.alalahti.deckofcards

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.concurrent.thread

class DeckViewerActivity : AppCompatActivity(), SensorEventListener {

    lateinit var layout: View
    lateinit var cardImageView: ImageView
    lateinit var remainingCounter: TextView
    lateinit var cardNameView: TextView
    lateinit var shuffleButton: Button
    lateinit var drawButton: Button
    lateinit var pseudo3DSwitch: SwitchCompat
    var deck_id: String? = null
    lateinit var sensorManager: SensorManager
    lateinit var rotationSensor: Sensor
    val sensorSamplingPeriod = 100000
    var cardImage: Bitmap? = null
    var remainingCards = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        layout = findViewById(R.id.deckViewerActivityLayout)
        cardImageView = findViewById(R.id.cardImageView)
        remainingCounter = findViewById(R.id.remainingCounter)
        cardNameView = findViewById(R.id.cardNameView)
        shuffleButton = findViewById(R.id.shuffleButton)
        drawButton = findViewById(R.id.drawButton)
        pseudo3DSwitch = findViewById(R.id.pseudo3DSwitch)
        deck_id = intent.getStringExtra("deck_id")

        sensorManager  = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        // Initialize how many cards remain in the deck
        remainingCards = intent.getIntExtra("remaining", -1)
        if (remainingCards != -1) {
            updateRemainingCounter(remainingCards)
        }

        pseudo3DSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                registerRotationSensor()
            } else {
                unregisterRotationSensor()
                cardImageView.rotationX = 0f
                cardImageView.rotationY = 0f
            }
        }
    }

    fun registerRotationSensor() {
        sensorManager.registerListener(this, rotationSensor, sensorSamplingPeriod)
    }
    fun unregisterRotationSensor() {
        sensorManager.unregisterListener(this, rotationSensor)
    }

    override fun onPause() {
        super.onPause()
        if (pseudo3DSwitch.isChecked) {
            unregisterRotationSensor()
        }
    }

    override fun onResume() {
        super.onResume()
        if (pseudo3DSwitch.isChecked) {
            registerRotationSensor()
        }
    }

    fun drawCard(button: View) {
        if (deck_id != null) {
            APIService.getInstance().drawCard(deck_id!!).enqueue(APICallback(layout) {
                if (it.success) {
                    val card = it.cards[0]

                    // Update imageView with the drawn card's image
                    thread {
                        try {
                            val stream = java.net.URL(card.image).openStream()
                            val newImage = BitmapFactory.decodeStream(stream)
                            cardImage = newImage
                            runOnUiThread {
                                cardImageView.setImageBitmap(newImage)
                                cardNameView.text = getCardName(card.value, card.suit)
                                cardImageView.contentDescription = getCardName(card.value, card.suit)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Update counter with how many cards remain in the deck
                    updateRemainingCounter(it.remaining)
                    remainingCards = it.remaining.toInt()
                } else {
                    ErrorMessenger.showErrorMessage(layout)
                }
            })
        }
    }

    fun shuffle(button: View) {
        if (deck_id != null) {
            APIService.getInstance().shuffleDeck(deck_id!!).enqueue(APICallback(layout) {
                if (it.success) {
                    // Show a little pop-up when shuffling is complete
                    Toast.makeText(applicationContext, getString(R.string.shuffled_message), Toast.LENGTH_SHORT).show()
                } else {
                    ErrorMessenger.showErrorMessage(layout)
                }
            })
        }
    }

    fun updateRemainingCounter(count: Number) {
        runOnUiThread {
            remainingCounter.text = "${getString(R.string.cards_remaining)} $count"
            // Disable shuffle and draw after all cards have been drawn
            if (count.toInt() <= 0) {
                drawButton.isEnabled = false
                shuffleButton.isEnabled = false
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                cardImageView.rotationX = event.values[0] * 90
                cardImageView.rotationY = event.values[1] * -90
            } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                cardImageView.rotationX = event.values[1] * -90
                cardImageView.rotationY = event.values[0] * -90
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}

    fun getCardName(value: String, suit: String): String {
        if (value == "JOKER") {
            return "Joker"
        } else {
            // Make value and suit lowercase and capitalize their first letters
            return "${value.lowercase().replaceFirstChar { it.uppercase() }} of ${suit.lowercase().replaceFirstChar { it.uppercase() }}"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (cardImage != null) {
            // Compress image bitmap to a bytearray so it can be put into the bundle
            val stream = ByteArrayOutputStream()
            cardImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            outState.putByteArray("cardImage", byteArray)
        }
        outState.putString("cardName", cardNameView.text.toString())
        outState.putString("cardImageDescription", cardImageView.contentDescription.toString())
        outState.putInt("remainingCards", remainingCards)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val cardImageByteArray = savedInstanceState.getByteArray("cardImage")
        if (cardImageByteArray != null) {
            // Decode bytearray back to an image
            cardImage = BitmapFactory.decodeByteArray(cardImageByteArray, 0, cardImageByteArray.size)
            cardImageView.setImageBitmap(cardImage)
        }
        cardNameView.text = savedInstanceState.getString("cardName")
        cardImageView.contentDescription = savedInstanceState.getString("cardImageDescription")
        remainingCards = savedInstanceState.getInt("remainingCards", -1)
        if (remainingCards != -1) {
            updateRemainingCounter(remainingCards)
        }
    }
}