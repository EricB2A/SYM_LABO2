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

    private var handler: Handler = Looper.getMainLooper()?.let { Handler(it) }!!

    fun sendRequest(url: String, request: String, contentType: String) {
        Thread {
            execRequest(url, request, contentType)
        }.start()
    }

    fun sendRequestDeferred(url: String, request: String, contentType: String){
        Thread {
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
                    Thread.sleep(5000)
                    sendRequestDeferred(url, request, contentType)
                } else {
                    execRequest(url, request, contentType)
                }
            } catch (exception: IOException) {
                Thread.sleep(5000)
                sendRequestDeferred(url, request, contentType)
            }
        }.start()
    }

    private fun execRequest(url: String, request: String, contentType: String) {
        var urlConn = URL(url)
        var httpConn:HttpURLConnection = urlConn.openConnection() as HttpURLConnection
        httpConn.setRequestProperty("Content-Type", contentType)
        httpConn.setRequestProperty("Accept", contentType)
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
            communicationEventListener?.handleServerResponse(response)
        } else {
            Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            val response = httpConn.inputStream.bufferedReader().readText()
            communicationEventListener?.handleServerResponse(response)
        }

        httpConn.disconnect()
    }

    companion object {
        const val defaultMethod = "POST"
    }

}