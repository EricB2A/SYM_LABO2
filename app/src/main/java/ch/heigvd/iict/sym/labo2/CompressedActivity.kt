package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import ch.heigvd.iict.sym.labo2.directory.DirectoryUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class CompressedActivity : AppCompatActivity() {

    lateinit var requestTextInput : EditText
    lateinit var sendCompressedBtn : Button
    lateinit var sendUncompressedBtn : Button
    lateinit var responseOutput : TextView
    lateinit var responseSizeOutput : TextView

    val gson: Gson = GsonBuilder().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compressed)

        requestTextInput = findViewById(R.id.requestTextInput)
        sendCompressedBtn = findViewById(R.id.sendCompressedBtn)
        sendUncompressedBtn = findViewById(R.id.sendUncompressedBtn)
        responseOutput = findViewById(R.id.responseTextOutput)
        responseSizeOutput = findViewById(R.id.responseSizeText)

        sendCompressedBtn.setOnClickListener {
            val sym = SymComManager(object : CommunicationEventListener {
                override fun handleServerResponse(
                    response: String,
                    contentType: SymComManager.ContentType,
                    size: Int
                ) {
                    responseOutput.text = gson.fromJson(response, String::class.java)
                    responseSizeOutput.text = size.toString()

                }
            })


            sym.sendRequest(
                SymComManager.Url.JSON,
                SymComManager.ContentType.JSON,
                gson.toJson(requestTextInput.text.toString()),
                true
            )
        }

        sendUncompressedBtn.setOnClickListener {
            val sym = SymComManager(object : CommunicationEventListener {
                override fun handleServerResponse(
                    response: String,
                    contentType: SymComManager.ContentType,
                    size: Int
                ) {
                    responseOutput.text = gson.fromJson(response, String::class.java)
                    responseSizeOutput.text = size.toString()
                }
            })

            sym.sendRequest(
                SymComManager.Url.JSON,
                SymComManager.ContentType.JSON,
                gson.toJson(requestTextInput.text.toString()),
            )
        }

    }


}