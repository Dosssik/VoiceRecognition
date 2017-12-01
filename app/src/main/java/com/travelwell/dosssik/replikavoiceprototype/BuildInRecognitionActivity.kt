package com.travelwell.dosssik.replikavoiceprototype


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.build_in_activity.*


/**
 * Source: http://www.truiton.com/2014/06/android-speech-recognition-without-dialog-custom-activity/
 */

class BuildInRecognitionActivity : Activity(), RecognitionListener {

    private var returnedText: TextView? = null
    private var toggleButton: ToggleButton? = null
    private var progressBar: ProgressBar? = null
    private var speech: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private val LOG_TAG = "RecognitionActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.build_in_activity)
        returnedText = findViewById<View>(R.id.final_result_tv) as TextView
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        toggleButton = findViewById<View>(R.id.toggleButton) as ToggleButton

        progressBar!!.visibility = View.INVISIBLE
        initRecognizer()

    }

    private fun initRecognizer() {
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        speech!!.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
                "en_US")
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, true)

        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

        recognizerIntent!!.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en_US"))


        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.packageName)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
    }


    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#b5b8bc")
        }

        toggleButton!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                progressBar!!.visibility = View.VISIBLE
                progressBar!!.isIndeterminate = true
                partial_result_tv.text = ""
                final_result_tv.text = ""
                speech!!.startListening(recognizerIntent)
            } else {
                progressBar!!.isIndeterminate = false
                progressBar!!.visibility = View.INVISIBLE
                speech!!.stopListening()
            }
        }

        initRecognizer()
    }

    override fun onPause() {
        super.onPause()
        if (speech != null) {
            speech!!.destroy()
            Log.i(LOG_TAG, "destroy")
        }

    }

    override fun onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech")
        progressBar!!.isIndeterminate = false
        progressBar!!.max = 10
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer)
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech")
        progressBar!!.isIndeterminate = true
        toggleButton!!.isChecked = false
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.d(LOG_TAG, "FAILED " + errorMessage)
        returnedText!!.text = "ERROR: " + errorMessage
        toggleButton!!.isChecked = false

    }

    override fun onEvent(arg0: Int, arg1: Bundle) {
        Log.i(LOG_TAG, "onEvent")
    }

    override fun onPartialResults(arg0: Bundle) {
        val results = arg0.getStringArrayList("results_recognition")
        val language = arg0.getString("results_language")


        var text = ""
        results.forEach {
            if (it.isNotEmpty()) {
                text = text + it
            }
        }
        if (text.isEmpty()) return

        val lang = if (language != null) "Detected language $language " else ""

        val textForPublish = partial_result_tv.text.toString() + text + "\n" + "\n"
        partial_result_tv.text = textForPublish

        Log.i(LOG_TAG, "onPartialResults. $lang Text:$text")
    }

    override fun onReadyForSpeech(arg0: Bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech")
    }

    override fun onResults(results: Bundle) {
        Log.i(LOG_TAG, "onResults")
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

        var text = ""

        matches.forEachIndexed { index: Int, value: String? ->
            text = text + "${index +1 }) " + value + ". CONFIDENCE_SCORES: " + scores[index] + "\n" + "\n"
        }

        returnedText!!.text = text
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB)
        progressBar!!.progress = rmsdB.toInt()
    }

    companion object {

        fun getErrorText(errorCode: Int): String {
            val message: String
            when (errorCode) {
                SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> message = "error from server"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
                else -> message = "Didn't understand, please try again."
            }
            return message
        }
    }

}