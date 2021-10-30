package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class DeferredActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deferred)
        val sendBtn: Button = findViewById(R.id.sendBtn)
        val requestInput  : EditText = findViewById(R.id.requestTextInput)
        val responseOutput  : TextView = findViewById(R.id.responseTextOutput)
        val sym = SymComManager(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                val text : String = responseOutput.text.toString()
                // TODO enelever la suggestion
                responseOutput.text = text + response + "\n"
            }
        })
        sendBtn.setOnClickListener {
            sym.sendRequest("http://mobile.iict.ch/api/txt", SymComManager.ContentType.TEXT_PLAIN, requestInput.text.toString())
        }
    }
}