package com.example.counter

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.googlecompat.GoogleCompatEmojiProvider
import timber.log.Timber

@HiltAndroidApp
class CounterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val emojis =
            EmojiManager.install(
                GoogleCompatEmojiProvider(
                    EmojiCompat.init(
                        FontRequestEmojiCompatConfig(
                            this,
                            FontRequest(
                                "com.google.android.gms.fonts",
                                "com.google.android.gms",
                                "Noto Color Emoji Compat",
                                R.array.com_google_android_gms_fonts_certs,
                            )
                        ).setReplaceAll(true)
                    )
                )
            )
    }


}


