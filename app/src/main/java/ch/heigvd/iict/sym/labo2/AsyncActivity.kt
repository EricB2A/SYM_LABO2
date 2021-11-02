package ch.heigvd.iict.sym.labo2

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.os.Looper
import java.lang.ref.WeakReference


class AsyncActivity : AppCompatActivity(), CommunicationEventListener {
    private lateinit var send: Button
    private lateinit var received: EditText
    private lateinit var sent: EditText
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)

        send = findViewById(R.id.async_btn_send)
        received = findViewById(R.id.async_receive_text)
        sent = findViewById(R.id.async_send_text)

        handler = Looper.getMainLooper()?.let { Handler(it) }!!

        received.isEnabled = false

        send.setOnClickListener {
            var s = SymComManager(this)
            s.sendRequest("http://mobile.iict.ch/api/txt", sent.text.toString(), "text/plain")
        }
    }

    override fun handleServerResponse(response: String) {
        received.setText(response)
    }
}