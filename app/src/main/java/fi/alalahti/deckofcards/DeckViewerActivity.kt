package fi.alalahti.deckofcards

import android.content.Context
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.lang.Exception
import kotlin.concurrent.thread

class DeckViewerActivity : AppCompatActivity(), SensorEventListener {

    lateinit var cardImageView: ImageView
    lateinit var remainingCounter: TextView
    lateinit var shuffleButton: Button
    lateinit var drawButton: Button
    lateinit var pseudo3DSwitch: SwitchCompat
    var deck_id: String? = null
    lateinit var sensorManager: SensorManager
    lateinit var rotationSensor: Sensor
    val sensorSamplingPeriod = 100000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_viewer)
        cardImageView = findViewById(R.id.cardImageView)
        remainingCounter = findViewById(R.id.remainingCounter)
        shuffleButton = findViewById(R.id.shuffleButton)
        drawButton = findViewById(R.id.drawButton)
        pseudo3DSwitch = findViewById(R.id.pseudo3DSwitch)
        deck_id = intent.getStringExtra("deck_id")

        sensorManager  = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        // Initialize how many cards remain in the deck
        val remaining = intent.getIntExtra("remaining", -1)
        if (remaining != -1) {
            updateRemainingCounter(remaining)
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            cardImageView.rotationX = event.values[0] * 90
            cardImageView.rotationY = event.values[1] * -90
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {

    }
}