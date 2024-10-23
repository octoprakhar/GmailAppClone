package com.example.gmailappclone.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class ConnectivityMonitor (private val context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback : ConnectivityManager.NetworkCallback? = null

    //Register network callback to detect internet connections
    fun registerNetworkCallback(onConnected: () -> Unit, onDisconnected: () -> Unit){
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                onConnected()
            }
            override fun onLost(network: android.net.Network) {
                onDisconnected()
            }

        }

        //Register the network callback with connectivity manager
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)

    }

    //unregister network callback
    fun unregisterNetworkCallback(){
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
    }

}