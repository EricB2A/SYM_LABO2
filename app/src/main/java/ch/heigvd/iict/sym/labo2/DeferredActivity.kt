package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText

class DeferredActivity : AppCompatActivity(), CommunicationEventListener {

    private lateinit var send: Button
    private lateinit var received: EditText
    private lateinit var sent: EditText
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deferred)

        send = findViewById(R.id.deferred_btn_send)
        received = findViewById(R.id.deferred_receive_text)
        sent = findViewById(R.id.deferred_send_text)

        handler = Looper.getMainLooper()?.let { Handler(it) }!!

        received.isEnabled = false

        send.setOnClickListener {
            var s = SymComManager(this)
            s.sendRequestDeferred("http://mobile.iict.ch/api/txt", sent.text.toString(), "text/plain")
        }
    }

    override fun handleServerResponse(response: String) {
        received.setText(response)
    }
}