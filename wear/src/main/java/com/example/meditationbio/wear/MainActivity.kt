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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearTestScreen(this)
        }
    }
}

@Composable
fun WearTestScreen(activity: ComponentActivity) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val payload = "Hallo vom Wear-Modul".toByteArray(Charsets.UTF_8)

            Wearable.getNodeClient(activity).connectedNodes
                .addOnSuccessListener { nodes ->
                    Log.d("WearSend", "Gefundene Nodes: ${nodes.size}")

                    if (nodes.isEmpty()) {
                        Log.d("WearSend", "Kein verbundenes Handy gefunden")
                    }

                    nodes.forEach { node ->
                        Log.d(
                            "WearSend",
                            "Sende an Node: ${node.displayName}, id=${node.id}, nearby=${node.isNearby}"
                        )

                        Wearable.getMessageClient(activity)
                            .sendMessage(node.id, "/bio", payload)
                            .addOnSuccessListener {
                                Log.d("WearSend", "Nachricht erfolgreich gesendet")
                            }
                            .addOnFailureListener { e ->
                                Log.e("WearSend", "Senden fehlgeschlagen: ${e.message}", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("WearSend", "connectedNodes fehlgeschlagen: ${e.message}", e)
                }
        }) {
            Text("Test senden")
        }
    }
}