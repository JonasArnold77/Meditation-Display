package com.example.meditationbio

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MobileWearService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("MobileWearService", "Service erstellt")
    }

    override fun onMessageReceived(event: MessageEvent) {
        Log.d(
            "MobileWearService",
            "Nachricht empfangen: path=${event.path}, size=${event.data.size}"
        )

        val text = String(event.data, Charsets.UTF_8)
        Log.d("MobileWearService", "Payload: $text")
    }
}