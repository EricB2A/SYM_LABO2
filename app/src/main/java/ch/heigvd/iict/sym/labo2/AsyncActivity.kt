package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.w3c.dom.Text

class AsyncActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)
        val sendBtn: Button = findViewById(R.id.sendBtn)
        val requestInput  : EditText = findViewById(R.id.requestTextInput)
        val responseOutput  : TextView = findViewById(R.id.responseTextOutput)

        val sym = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String, contentType: SymComManager.ContentType) {
                responseOutput.text = response
            }
        })

        sendBtn.setOnClickListener {
            sym.sendRequest("http://mobile.iict.ch/api/txt", SymComManager.ContentType.TEXT_PLAIN, requestInput.text.toString())
        }
    }
}