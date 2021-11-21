package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class CompressedActivity : AppCompatActivity() {

    lateinit var requestTextInput : EditText
    lateinit var sendCompressedBtn : Button
    lateinit var sendUncompressedBtn : Button
    lateinit var responseOutput : TextView

    val gson: Gson = GsonBuilder().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compressed)

        requestTextInput = findViewById(R.id.requestTextInput)
        sendCompressedBtn = findViewById(R.id.sendCompressedBtn)
        sendUncompressedBtn = findViewById(R.id.sendUncompressedBtn)
        responseOutput = findViewById(R.id.responseTextOutput)

        sendCompressedBtn.setOnClickListener {
            val sym = SymComManager(object : CommunicationEventListener {
                override fun handleServerResponse(
                    response: String,
                    contentType: SymComManager.ContentType
                ) {
                    responseOutput.text = response;

                }
            })

            // Pour l'envoi compressé, on a qu'un argument à ajouter : compressed.
            sym.sendRequest(
                SymComManager.Url.TXT,
                SymComManager.ContentType.TEXT_PLAIN,
                // gson.toJson(requestTextInput.text.toString()),
                requestTextInput.text.toString(),
                true
            )
        }

        sendUncompressedBtn.setOnClickListener {
            val sym = SymComManager(object : CommunicationEventListener {
                override fun handleServerResponse(
                    response: String,
                    contentType: SymComManager.ContentType
                ) {
                    responseOutput.text = response;
                }
            })

            sym.sendRequest(
                SymComManager.Url.TXT,
                SymComManager.ContentType.TEXT_PLAIN,
                //gson.toJson(requestTextInput.text.toString()),
                requestTextInput.text.toString()
            )
        }

    }


}