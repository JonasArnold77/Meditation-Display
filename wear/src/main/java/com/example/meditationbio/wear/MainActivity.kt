package com.example.meditationbio.wear

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.wearable.Wearable
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private var messageCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearTestScreen(
                onSendClick = { sendJsonMessage() }
            )
        }
    }

    private fun sendJsonMessage() {
        messageCounter++

        val json = JSONObject().apply {
            put("type", "meditation_test")
            put("sessionId", "session_001")
            put("timestamp", System.currentTimeMillis())
            put("messageCounter", messageCounter)
            put("heartRate", 68)
            put("motionScore", 0.12)
            put("stillnessPercent", 91)
            put("durationSec", 30)
            put("source", "galaxy_watch_4")
        }

        val payload = json.toString().toByteArray(Charsets.UTF_8)

        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                Log.d("WearSend", "Gefundene Nodes: ${nodes.size}")

                if (nodes.isEmpty()) {
                    Log.d("WearSend", "Kein verbundenes Handy gefunden")
                }

                nodes.forEach { node ->
                    Log.d("WearSend", "Sende JSON an ${node.displayName}: $json")

                    Wearable.getMessageClient(this)
                        .sendMessage(node.id, "/bio", payload)
                        .addOnSuccessListener {
                            Log.d("WearSend", "JSON erfolgreich gesendet")
                        }
                        .addOnFailureListener { e ->
                            Log.e("WearSend", "Senden fehlgeschlagen: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("WearSend", "connectedNodes fehlgeschlagen: ${e.message}", e)
            }
    }
}

@Composable
fun WearTestScreen(onSendClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onSendClick) {
            Text("JSON senden")
        }
    }
}