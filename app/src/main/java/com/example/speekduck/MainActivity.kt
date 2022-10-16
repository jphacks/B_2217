package com.example.speekduck

import android.Manifest.permission.RECORD_AUDIO
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.File

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
        val button = findViewById<ToggleButton>(R.id.button)
        RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS
        button.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
            }else{
                speechRecognizer?.stopListening()
            }
        }
        /*
        saveFile("temp.txt","")
        val button2 = findViewById<Button>(R.id.button2)
        val textView2 = findViewById<TextView>(R.id.textView2)
        button2.setOnClickListener(View.OnClickListener {
            val buf: BufferedReader = readFile("temp.txt")
            val result = buf.use{it.readText()}
            textView2.text = result
        })
         */

    }

    private fun createRecognitionListenerStringStream(onResult: (String)->Unit): RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) { /** 今回は特に利用しない */ }
            override fun onReadyForSpeech(params: Bundle) {  }
            override fun onBufferReceived(buffer: ByteArray) { }
            override fun onPartialResults(partialResults: Bundle) { val stringArray = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                onResult(stringArray.toString())}
            override fun onEvent(eventType: Int, params: Bundle) {  }
            override fun onBeginningOfSpeech() {  }
            override fun onEndOfSpeech() {  }
            override fun onError(error: Int) { }
            override fun onResults(results: Bundle) {
                val stringArray = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val tmpfile = "tmp.txt"
                val memotmp = stringArray.toString()
                saveFile(tmpfile,memotmp)
                onResult(memotmp)
            }
        }
    }
    private fun saveFile(file: String, str: String) {
        applicationContext.openFileOutput(file, Context.MODE_PRIVATE).use {
            it.write(str.toByteArray())
        }
    }

    private fun readFile(file: String): BufferedReader {
        val readFile = File(applicationContext.filesDir, file)
        return readFile.bufferedReader()
    }


    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}