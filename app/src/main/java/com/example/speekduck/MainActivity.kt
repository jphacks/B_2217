package com.example.speekduck

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private var speechRecognizer : SpeechRecognizer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val granted = ContextCompat.checkSelfPermission(/* context = */ this, /* permission = */
            RECORD_AUDIO)
        if (granted != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO),1)
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        val textMessage = findViewById<TextView>(R.id.textView)
        speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream { textMessage.text = it })
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))}
    }

    private fun createRecognitionListenerStringStream(onResult: (String)->Unit): RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) { /** 今回は特に利用しない */ }
            override fun onReadyForSpeech(params: Bundle) { onResult("onReadyForSpeech") }
            override fun onBufferReceived(buffer: ByteArray) { onResult("onBufferReceived") }
            override fun onPartialResults(partialResults: Bundle) { onResult("onPartialResults") }
            override fun onEvent(eventType: Int, params: Bundle) { onResult("onEvent") }
            override fun onBeginningOfSpeech() { onResult("onBeginningOfSpeech") }
            override fun onEndOfSpeech() { onResult("onEndOfSpeech") }
            override fun onError(error: Int) { onResult("onError") }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                onResult(stringArray.toString())
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}