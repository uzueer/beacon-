package com.example.meshsosapp.nearby

import android.content.Context
import android.util.Log
import com.example.meshsosapp.models.ChatMessage
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.nio.charset.StandardCharsets

object NearbyManager {

    private const val TAG = "NearbyManager"
    private const val SERVICE_ID = "com.example.meshsosapp.CHAT"
    private val STRATEGY = Strategy.P2P_CLUSTER

    private lateinit var connectionsClient: ConnectionsClient
    private val discoveredEndpoints = mutableMapOf<String, String>()
    private val connectedEndpoints = mutableMapOf<String, String>()
    private val gson = Gson()

    // FIX: Add a variable to hold the local user's name
    private var localUserName: String = "Chatter" // Default name

    // Callback to send received messages back to the UI
    var onMessageReceivedListener: ((ChatMessage) -> Unit)? = null

    fun initialize(context: Context) {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    // FIX: Add a function to allow MainActivity to set the username
    fun setLocalUserName(name: String) {
        localUserName = name
    }

    fun start() {
        startAdvertising()
        startDiscovery()
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(
            // FIX: Use the dynamic localUserName instead of a hardcoded string
            localUserName,
            SERVICE_ID,
            connectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            Log.d(TAG, "Advertising started successfully as '$localUserName'")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Advertising failed: ", e)
        }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(
            SERVICE_ID,
            endpointDiscoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            Log.d(TAG, "Discovery started successfully.")
        }.addOnFailureListener { e ->
            Log.e(TAG, "Discovery failed: ", e)
        }
    }

    fun sendMessage(message: ChatMessage) {
        val messageJson = gson.toJson(message)
        val payload = Payload.fromBytes(messageJson.toByteArray(StandardCharsets.UTF_8))

        // Send the payload to all currently connected endpoints
        connectionsClient.sendPayload(connectedEndpoints.keys.toList(), payload)
        Log.d(TAG, "Message sent to ${connectedEndpoints.size} devices.")
    }

    fun disconnectAll() {
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()
        connectionsClient.stopAllEndpoints()
        connectedEndpoints.clear()
        discoveredEndpoints.clear()
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Found endpoint: ${info.endpointName}")
            discoveredEndpoints[endpointId] = info.endpointName
            // Automatically request a connection to the discovered endpoint
            connectionsClient.requestConnection(
                // FIX: Use the dynamic localUserName instead of a hardcoded string
                localUserName,
                endpointId,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "Connection request sent to ${info.endpointName}")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Connection request failed: ", e)
            }
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "Lost endpoint: $endpointId")
            discoveredEndpoints.remove(endpointId)
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d(TAG, "Connection initiated with ${connectionInfo.endpointName}. Accepting...")
            // Automatically accept the connection on both sides.
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    val endpointName = discoveredEndpoints[endpointId] ?: "Unknown"
                    connectedEndpoints[endpointId] = endpointName
                    Log.d(TAG, "Connected to $endpointName.")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(TAG, "Connection rejected.")
                }
                else -> {
                    Log.d(TAG, "Connection failed: ${result.status.statusMessage}")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected from $endpointId.")
            connectedEndpoints.remove(endpointId)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val receivedBytes = payload.asBytes() ?: return
                val messageJson = String(receivedBytes, StandardCharsets.UTF_8)
                try {
                    val chatMessage = gson.fromJson(messageJson, ChatMessage::class.java)
                    // Pass the message to the UI
                    onMessageReceivedListener?.invoke(chatMessage)
                } catch (e: JsonSyntaxException) {
                    Log.e(TAG, "Failed to parse JSON: ", e)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Not used for simple messages, but important for files/streams
        }
    }
}