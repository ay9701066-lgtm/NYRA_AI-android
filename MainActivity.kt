package com.nyra.ai

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var chat: LinearLayout
    private lateinit var scroll: ScrollView
    private lateinit var input: EditText
    private lateinit var tts: TextToSpeech
    private var ttsReady = false

    private val purple = Color.rgb(104, 70, 210)
    private val dark = Color.rgb(25, 23, 39)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(248, 245, 255), Color.rgb(235, 229, 255))
            )
        }
        // Android 15 (targetSdk 35) draws apps edge-to-edge, so keep content clear of the
        // status bar, navigation bar and keyboard.
        val basePadding = dp(16)
        root.setPadding(basePadding, basePadding, basePadding, basePadding)
        root.setOnApplyWindowInsetsListener { view, insets ->
            @Suppress("DEPRECATION")
            view.setPadding(
                basePadding + insets.systemWindowInsetLeft,
                basePadding + insets.systemWindowInsetTop,
                basePadding + insets.systemWindowInsetRight,
                basePadding + insets.systemWindowInsetBottom
            )
            insets
        }

        val title = TextView(this).apply {
            text = "NYRA AI  ✦"
            textSize = 27f
            setTextColor(dark)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(3))
        }
        val subtitle = TextView(this).apply {
            text = "Tumhari Hindi voice assistant 💜"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(12))
        }
        chat = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(10), dp(10), dp(10))
        }
        scroll = ScrollView(this).apply {
            isFillViewport = true
            background = rounded(Color.WHITE, 20)
            addView(chat)
        }
        input = EditText(this).apply {
            hint = "Yahan likho ya mic dabao…"
            textSize = 16f
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_SEND
            background = rounded(Color.WHITE, 16)
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEND) {
                    sendMessage()
                    true
                } else {
                    false
                }
            }
        }
        val quick = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        quick.addView(makeButton("YouTube", purple) { openYouTube() }, weightedParams())
        quick.addView(
            makeButton("Google Search", Color.rgb(65, 92, 180)) { searchGoogle(input.text.toString()) },
            weightedParams()
        )
        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        controls.addView(input, LinearLayout.LayoutParams(0, dp(52), 1f))
        controls.addView(makeButton("🎙", purple) { listen() }, LinearLayout.LayoutParams(dp(56), dp(50)))
        controls.addView(makeButton("Send", dark) { sendMessage() }, LinearLayout.LayoutParams(dp(76), dp(50)))

        root.addView(title)
        root.addView(subtitle)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(quick, LinearLayout.LayoutParams(-1, dp(54)))
        root.addView(controls, LinearLayout.LayoutParams(-1, dp(58)))
        setContentView(root)

        addMessage(
            "NYRA",
            "Hello Aman! 💜 Main NYRA hoon. Hindi mein message likho ya mic dabakar bolo. " +
                "Main basic commands offline samajh sakti hoon."
        )
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.forLanguageTag("hi-IN"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun sendMessage() {
        val text = input.text.toString().trim()
        if (text.isEmpty()) return
        addMessage("Tum", text)
        input.setText("")
        val reply = answer(text)
        addMessage("NYRA", reply)
        speak(reply)
    }

    private fun speak(reply: String) {
        if (!ttsReady) return
        // Drop emoji and symbols so the voice does not try to read them out.
        val spoken = reply.replace(Regex("[^\\p{L}\\p{M}\\p{N}\\p{P}\\s]"), "").trim()
        if (spoken.isNotEmpty()) tts.speak(spoken, TextToSpeech.QUEUE_FLUSH, null, "nyra_reply")
    }

    private fun answer(text: String): String {
        val q = text.lowercase(Locale.ROOT)
        val words = q.split(Regex("[^\\p{L}\\p{M}\\p{N}]+")).filter { it.isNotEmpty() }.toSet()
        fun hasWord(vararg options: String) = options.any { it in words }
        fun hasPhrase(vararg options: String) = options.any { q.contains(it) }

        return when {
            hasWord("youtube") && hasWord("khol", "kholo", "open", "chalao") -> {
                openYouTube()
                "YouTube khol rahi hoon."
            }
            hasWord("search", "google") -> {
                searchGoogle(text)
                "Google par search khol diya."
            }
            hasPhrase("kaise ho", "how are you", "kaisi ho") ->
                "Main badhiya hoon! Tum kaise ho, Aman? 😊"
            hasWord("hello", "hlo", "hi", "hey", "namaste", "नमस्ते") ->
                "Hello bestie! 💜 Batao, kya help chahiye?"
            hasWord("time", "samay", "समय") ->
                "Abhi samay hai " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) + "."
            hasWord("help", "madad", "मदद") ->
                "Tum mujhse chat kar sakte ho, mic se bol sakte ho, YouTube khol sakte ho " +
                    "ya Google search kar sakte ho."
            else ->
                "Abhi main basic offline assistant hoon. Is baat ka smart AI jawab dene ke liye " +
                    "baad mein AI service connect karni hogi."
        }
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "NYRA se bolo…")
        }
        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_SPEECH)
        } catch (e: ActivityNotFoundException) {
            addMessage(
                "NYRA",
                "Is phone par speech recognition service available nahi hai. Google app/voice typing check karo."
            )
        }
    }

    @Deprecated("Deprecated by Android, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SPEECH && resultCode == RESULT_OK) {
            val words = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!words.isNullOrEmpty()) {
                input.setText(words[0])
                input.setSelection(input.text.length)
            }
        }
    }

    private fun openYouTube() {
        openUrl("https://www.youtube.com", "YouTube kholne ke liye koi browser nahi mila.")
    }

    private fun searchGoogle(query: String) {
        val q = if (query.isBlank()) "Google search" else query
        openUrl("https://www.google.com/search?q=" + Uri.encode(q), "Search kholne ke liye koi browser nahi mila.")
    }

    private fun openUrl(url: String, errorMessage: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            addMessage("NYRA", errorMessage)
        }
    }

    private fun addMessage(who: String, message: String) {
        val bubble = TextView(this).apply {
            text = "$who\n$message"
            textSize = 15f
            setTextColor(if (who == "NYRA") dark else Color.WHITE)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = rounded(if (who == "NYRA") Color.rgb(241, 237, 255) else purple, 16)
        }
        val params = LinearLayout.LayoutParams(-1, -2).apply {
            bottomMargin = dp(8)
        }
        chat.addView(bubble, params)
        scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun makeButton(label: String, color: Int, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 12f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(color, 14)
            setOnClickListener { action() }
            setPadding(dp(4), 0, dp(4), 0)
        }
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun weightedParams() = LinearLayout.LayoutParams(0, dp(46), 1f).apply {
        marginStart = dp(4)
        marginEnd = dp(4)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private companion object {
        const val REQUEST_SPEECH = 41
    }
}
