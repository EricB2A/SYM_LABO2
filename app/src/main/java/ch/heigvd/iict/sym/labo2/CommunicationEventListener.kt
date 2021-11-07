package ch.heigvd.iict.sym.labo2

import java.io.InputStreamReader

interface CommunicationEventListener {
    fun handleServerResponse(response: String, contentType: String)
}