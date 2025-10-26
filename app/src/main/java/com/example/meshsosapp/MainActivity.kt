package com.example.meshsosapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater // Import LayoutInflater
import android.view.View
// import android.widget.Button // No longer needed for direct access
// import android.widget.EditText // No longer needed for direct access
// import android.widget.ImageButton // No longer needed for direct access
// import android.widget.LinearLayout // No longer needed for direct access
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
// import androidx.recyclerview.widget.RecyclerView // No longer needed for direct access
import com.example.meshsosapp.databinding.ActivityMainBinding // Import the generated binding class
import com.example.meshsosapp.models.ChatMessage
import com.example.meshsosapp.utils.LocationHelper
import com.example.meshsosapp.utils.TimeHelper
import com.example.meshsosapp.nearby.NearbyManager

class MainActivity : AppCompatActivity() {

    // Declare the binding variable
    private lateinit var binding: ActivityMainBinding

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private var userName: String = ""

    // UI Elements are now accessed via binding, so direct declarations are not strictly necessary
    // unless you prefer to keep them for clarity, but they would be initialized from binding.
    // For instance:
    // private lateinit var loginLayout: LinearLayout
    // private lateinit var chatLayout: LinearLayout
    // etc.

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set the content view to the root of the binding

        // Access views using the binding object
        // The problematic line was:
        // binding.splashLayout.visibility = View.GONE (and similar)
        // This will now work correctly.

        Handler(Looper.getMainLooper()).postDelayed({
            binding.splashLayout?.visibility = View.GONE
            binding.loginLayout?.visibility = View.VISIBLE
            binding.globalBeaconTitle?.visibility = View.VISIBLE
        }, 2500)

        // Initialize UI components using binding
        // loginLayout = binding.loginLayout // Example if you still want separate variables
        // chatLayout = binding.chatLayout
        // nameEditText = binding.nameEditText
        // enterButton = binding.enterButton
        // messagesRecyclerView = binding.messagesRecyclerView
        // messageEditText = binding.messageEditText
        // sendButton = binding.sendButton
        // sosButton = binding.sosButton

        // Setup RecyclerView
        chatAdapter = ChatAdapter(messages)
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.messagesRecyclerView.adapter = chatAdapter

        // Handle user login
        binding.enterButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            if (name.isNotEmpty()) {
                userName = name
                binding.loginLayout.visibility = View.GONE
                binding.chatLayout.visibility = View.VISIBLE

                NearbyManager.initialize(this)
                NearbyManager.setLocalUserName(userName)
                NearbyManager.onMessageReceivedListener = { chatMessage ->
                    runOnUiThread {
                        addMessage(chatMessage)
                    }
                }
                checkAndRequestBluetoothPermissions()
            }
        }

        // Handle sending normal messages
        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                val message = ChatMessage(
                    text = text,
                    sender = userName,
                    timestamp = TimeHelper.getCurrentTimestamp(),
                    isSos = false
                )
                NearbyManager.sendMessage(message)
                addMessage(message)
                binding.messageEditText.text.clear()
            }
        }

        // Handle sending SOS messages with real location
        binding.sosButton.setOnClickListener {
            checkLocationPermissionAndSendSos()
        }
    }

    private fun checkLocationPermissionAndSendSos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            sendSosMessage()
        }
    }

    private fun checkAndRequestBluetoothPermissions() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) // For older versions, location is needed for BT scan
        }

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), BLUETOOTH_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Bluetooth permissions already granted! Starting nearby chat.", Toast.LENGTH_SHORT).show()
            NearbyManager.start()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSosMessage()
                } else {
                    Toast.makeText(this, "Location permission is required to send an SOS message.", Toast.LENGTH_LONG).show()
                }
            }
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Permissions granted! Starting nearby chat.", Toast.LENGTH_SHORT).show()
                    NearbyManager.start()
                } else {
                    Toast.makeText(this, "Bluetooth permissions are required for mesh chat.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendSosMessage() {
        LocationHelper.getCurrentLocation(this) { location ->
            val sosMessageText: String
            if (location != null) {
                val lat = String.format("%.4f", location.first)
                val lon = String.format("%.4f", location.second)
                sosMessageText = "🚨 SOS from $userName at $lat, $lon"
            } else {
                sosMessageText = "🚨 SOS from $userName (Location Unavailable)"
            }

            val message = ChatMessage(
                text = sosMessageText,
                sender = userName,
                timestamp = TimeHelper.getCurrentTimestamp(),
                isSos = true,
                location = location?.let { Pair(it.first, it.second) }
            )
            runOnUiThread {
                NearbyManager.sendMessage(message)
                addMessage(message)
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        messages.add(message)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.messagesRecyclerView.scrollToPosition(messages.size - 1) // Use binding here
    }

    override fun onDestroy() {
        super.onDestroy()
        NearbyManager.disconnectAll()
    }
}
    