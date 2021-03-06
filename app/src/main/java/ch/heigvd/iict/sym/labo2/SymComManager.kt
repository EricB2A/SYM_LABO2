// Auteurs: Ilias Goujgali, Eric Bousbaa, Guillaume Laubscher

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
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * Méthodes permettant d'envoyer des requêtes de manière asynchrone
 */
class SymComManager(var communicationEventListener: CommunicationEventListener? = null) {

    // Collection des requetes ayant echoué et donc qui seront à renvoyer
    private var pendingRequests: MutableList<Request> = arrayListOf()

    init {
        // On renvoie les requetes ayant échoué toutes les 5 secondes
        Timer().schedule(object : TimerTask() {
            override fun run() {
                /*
                    Note : pas de gestion de concurrence ici.
                 */
                if (hasPendingRequest()) {
                    // copy pending request
                    val reqToSend = pendingRequests.toMutableList()

                    // clear la liste, si la requete echoue à nouveau alors elle sera à nouveau ajouter
                    // par sendRequest
                    pendingRequests.clear()
                    for(req in reqToSend){
                        sendRequest(req.url, req.contentType, req.request)
                    }
                }
            }
        }, 5000, 5000)
    }

    enum class ContentType(val text: String) {
        TEXT_PLAIN("text/plain"),
        JSON("application/json"),
        XML("application/xml"),
        BUFFER_PROTO("application/protobuf");

        override fun toString(): String {
            return text
        }
    }

    enum class Url(val text: String) {
        TXT("http://mobile.iict.ch/api/txt"),
        JSON("http://mobile.iict.ch/api/json"),
        XML("http://mobile.iict.ch/api/xml"),
        GRAPHQL("http://mobile.iict.ch/graphql"),
        PROTOBUF("http://mobile.iict.ch/api/protobuf");

        override fun toString(): String {
            return text
        }
    }

    /**
     * Retourne vrai s'il y a des requetes à renvoyer
     */
    fun hasPendingRequest(): Boolean {
        return pendingRequests.isNotEmpty()
    }

    /**
     * Méthodes d'envoie des requetes
     */
    fun sendRequest(url: Url, contentType: ContentType, request: String, compressed : Boolean = false) {
        val handler = Handler(Looper.getMainLooper()!!)
        Thread {
            run {
                val urlObject = URL(url.toString())
                val httpConnection = urlObject.openConnection() as HttpURLConnection
                try {

                    // Prépration de la requête
                    httpConnection.requestMethod = "POST"
                    httpConnection.setRequestProperty(
                        "Content-Type",
                        contentType.toString()
                    )
                    httpConnection.doOutput = true

                    // Option à la requête si compressée
                    val outputStream : OutputStream = if(compressed) {
                        httpConnection.setRequestProperty("X-Network", "CSD")
                        httpConnection.setRequestProperty("X-Content-Encoding", "deflate")

                        DeflaterOutputStream(httpConnection.outputStream, Deflater(Deflater.DEFAULT_COMPRESSION, true))
                    }else {
                        DataOutputStream(httpConnection.outputStream)
                    }

                    // Envoie de la requête
                    try {
                        outputStream.write(request.toByteArray(StandardCharsets.UTF_8))
                        outputStream.flush()
                    }catch (e : Exception){
                        Log.e(this.javaClass.simpleName, e.printStackTrace().toString())
                    }finally {
                        outputStream.close()
                    }

                    // Option de lecture si réponse compressée
                    val inputStream : InputStream = if(compressed) {
                        InflaterInputStream(httpConnection.inputStream, Inflater(true))
                    }else {
                        httpConnection.inputStream
                    }

                    // Envoie de la réponse au GUI
                    val s = String(inputStream.readBytes())
                    handler.post {
                        run {
                            communicationEventListener?.handleServerResponse(s, contentType)
                        }
                    }

                } catch (unknownHostEx: UnknownHostException) {
                    // En cas d'erreur, on rajoute la requete à pendingRequest pour qu'elle soit renvoyer plus tard
                    pendingRequests.add(Request(url, contentType, request))
                } finally {
                    httpConnection.disconnect()
                }
            }
        }.start()
    }
}