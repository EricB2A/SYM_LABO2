package ch.heigvd.iict.sym.labo2

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.*

class SymComManager(var communicationEventListener: CommunicationEventListener? = null) {

    private var pendingRequests: MutableList<Request> = arrayListOf();
    init {
        Timer().schedule(object : TimerTask() {
            override fun run() {

                // TODO A tester
                if (hasPendingRequest()) {
                    // copy pending request
                    val reqToSend = pendingRequests.toMutableList();

                    // clear la liste, si la requete echoue à nouveau alors elle sera à nouveau ajouter
                    // par sendRequest
                    pendingRequests.clear()
                    for(req in reqToSend){
                        sendRequest(req.url, req.contentType, req.request)
                    }
                }
            }
        }, 5000, 5000);
    }

    /**
     * TODO: On pourrait utiliser un companions object :
     * companion object {
     * const val TXT_URL  = "http://mobile.iict.ch/api/txt"
     * const val JSON_URL = "http://mobile.iict.ch/api/json"
     * const val BASE_URL = "http://mobile.iict.ch/"

     * const val CONTENT_TYPE_JSON = "application/json"
     * const val CONTENT_TYPE_TEXT = "text/plain"
     * }
     *
     * Accessible via SymComManager.TXT_URL
     */
    enum class ContentType(val text: String) {
        TEXT_PLAIN("text/plain"),
        JSON("application/json"),
        XML("application/xml"),
        BUFFER_PROTO("application/protobuf");


        override fun toString(): String {
            return text
        }
    }

    fun hasPendingRequest(): Boolean {
        return pendingRequests.isNotEmpty()
    }

    fun sendRequest(url: String, contentType: ContentType, request: String, compressed : Boolean = false) {
        Log.v(this.javaClass.simpleName, "Sending request : " + request)
        val handler = Handler(Looper.getMainLooper()!!)
        Thread() {
            run {
                val urlObject = URL(url)
                val httpConnection = urlObject.openConnection() as HttpURLConnection;
                try {
                    httpConnection.requestMethod = "POST"
                    httpConnection.setRequestProperty(
                        "Content-Type",
                        contentType.toString()
                    );

                    httpConnection.doOutput = true

                    val outputStream : OutputStream = if(compressed) {
                        httpConnection.setRequestProperty("X-Network", "CSD")
                        httpConnection.setRequestProperty("X-Content-Encoding", "deflate")

                        DeflaterOutputStream(httpConnection.outputStream, Deflater(Deflater.DEFAULT_COMPRESSION, true))
                    }else {
                        DataOutputStream(httpConnection.outputStream)
                    }


                    try {
                        outputStream.write(request.toByteArray(StandardCharsets.UTF_8))
                        outputStream.flush()

                    }catch (e : Exception){
                        println(e.printStackTrace())
                        //TODO log
                    }finally {
                        outputStream.close()
                    }

                    val inputStream : InputStream = if(compressed) {
                        InflaterInputStream(httpConnection.inputStream, Inflater(true))
                    }else {
                        httpConnection.inputStream
                    }

                    handler.post {
                        val byteArray = inputStream.readBytes()
                        run {
                            communicationEventListener?.handleServerResponse(String(byteArray), contentType, byteArray.size)
                        }
                    }




                } catch (unknownHostEx: UnknownHostException) {
                    pendingRequests.add(Request(url, contentType, request))
                } finally {
                    httpConnection.disconnect()
                }
            }
        }.start()
    }
}