package ch.heigvd.iict.sym.labo2

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.Bundle
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.InetAddress


class SymComManager(var communicationEventListener: CommunicationEventListener? = null) {

    fun sendRequest(url: String, request: String) {
        var handler = Looper.myLooper()?.let { Handler(it) }
        handler?.post(Runnable {
            execRequest(url, request)
        })
    }

    fun sendRequestDeferred(url: String, request: String){
        var handler = Looper.myLooper()?.let { Handler(it) }
        handler?.postDelayed(Runnable {
            val policy = ThreadPolicy.Builder().permitNetwork().build()
            StrictMode.setThreadPolicy(policy)

            try {
                val connection = URL("http://" + URL(url).host)
                    .openConnection() as HttpURLConnection
                connection.connectTimeout = 1000
                connection.readTimeout = 1000
                connection.requestMethod = "HEAD"
                val responseCode = connection.responseCode
                if (responseCode != 200) {
                    sendRequestDeferred(url, request)
                } else {
                    execRequest(url, request)
                }
            } catch (exception: IOException) {
                sendRequestDeferred(url, request)
            }
        }, 5000)
    }

    private fun execRequest(url: String, request: String) {
        var urlConn = URL(url)
        var httpConn:HttpURLConnection = urlConn.openConnection() as HttpURLConnection
        httpConn.setRequestProperty("Content-Type", "text/plain")
        httpConn.requestMethod = defaultMethod
        httpConn.doInput = true
        httpConn.doOutput = true

        val policy = ThreadPolicy.Builder().permitNetwork().build()
        StrictMode.setThreadPolicy(policy)

        val outputStreamWriter = OutputStreamWriter(httpConn.outputStream)
        outputStreamWriter.write(request)
        outputStreamWriter.flush()

        val responseCode = httpConn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = httpConn.inputStream.bufferedReader().readText()
            Log.d("request", response)
            // Convert raw JSON to pretty JSON using GSON library
            /*val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(JsonParser.parseString(response))*/
            communicationEventListener?.handleServerResponse(response)
        } else {
            Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
        }
    }

    companion object {
        const val defaultMethod = "POST"
    }

}