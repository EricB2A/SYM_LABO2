package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Xml
import android.widget.Button
import android.widget.EditText
import ch.heigvd.iict.sym.protobuf.DirectoryOuterClass
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream

class SerializedActivity : AppCompatActivity(), CommunicationEventListener {

    private lateinit var sendXML: Button
    private lateinit var sendJSON: Button
    private lateinit var sendProtoBuf: Button
    private lateinit var received: EditText
    private lateinit var sent: EditText
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialized)

        sendXML = findViewById(R.id.serialized_btn_send_xml)
        sendJSON = findViewById(R.id.serialized_btn_send_json)
        sendProtoBuf = findViewById(R.id.serialized_btn_send_protobuf)
        received = findViewById(R.id.serialized_receive_text)
        sent = findViewById(R.id.serialized_send_text)

        handler = Looper.getMainLooper()?.let { Handler(it) }!!

        received.isEnabled = false

        sendXML.setOnClickListener {
            var s = SymComManager(this)

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val serializer = factory.newSerializer()
            val stream = ByteArrayOutputStream()

            serializer.setOutput(stream, null)
            serializer.startDocument("UTF-8", null)
            serializer.docdecl(" directory SYSTEM \"http://mobile.iict.ch/directory.dtd\"")
            serializer.startTag(null, "directory")
            serializer.endTag(null, "directory")
            serializer.endDocument()
            serializer.flush()

            Log.d("xml", String(stream.toByteArray()))
            //s.sendRequest("http://mobile.iict.ch/api/xml", sent.text.toString(), "application/xml")
            s.sendRequest("http://mobile.iict.ch/api/xml", String(stream.toByteArray()), "application/xml")
        }

        sendJSON.setOnClickListener {
            var s = SymComManager(this)
            s.sendRequest("http://mobile.iict.ch/api/json", sent.text.toString(), "application/json")
        }

        sendProtoBuf.setOnClickListener {
            var s = SymComManager(this)
            var p = DirectoryOuterClass.Directory.newBuilder().addResults(
                DirectoryOuterClass.Person.newBuilder()
                    .setName("Bon")
                    .setFirstname("Jean")
                    .setMiddlename("Le")
                    .addPhone(DirectoryOuterClass.Phone.newBuilder()
                        //.setType(DirectoryOuterClass.Phone.Type.MOBILE)
                        .setNumber("077 444 88 88")
                    ).build()
            ).build()

            Log.d("main", sent.text.toString())
            s.sendRequest("http://mobile.iict.ch/api/protobuf", p.toString(), "application/protobuf")
        }
    }

    override fun handleServerResponse(response: String) {
        received.setText(response)
    }
}