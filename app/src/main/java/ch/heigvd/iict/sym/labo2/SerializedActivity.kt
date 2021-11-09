package ch.heigvd.iict.sym.labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Xml
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.sym.protobuf.DirectoryOuterClass
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.util.*

class SerializedActivity : AppCompatActivity(), CommunicationEventListener {

    class Person(var name: String, var firstname: String, var middlename: String?, var phone: Phone) {
        constructor() : this("", "", null, Phone())

        class Phone(var number: String, var type: PhoneType) {
            constructor(): this("", PhoneType.HOME)
            enum class PhoneType(val phoneType: String) {
                HOME("home"),
                WORK("work"),
                MOBILE("mobile")
            }

            override fun toString(): String {
                return "$number ($type)"
            }
        }

        override fun toString(): String {
            return "$name $firstname ${middlename.takeIf { it != null }} ${phone.toString()}"
        }
    }

    private lateinit var sendXML: Button
    private lateinit var sendJSON: Button
    private lateinit var sendProtoBuf: Button
    private lateinit var received: EditText

    private lateinit var listPersons: ListView
    private lateinit var personName: EditText
    private lateinit var personFirstname: EditText
    private lateinit var personMiddlename: EditText
    private lateinit var personPhoneNumber: EditText
    private lateinit var personPhoneType: Spinner
    private lateinit var btnAddPerson: Button
    private lateinit var btnClearList: Button

    private lateinit var adapter: ArrayAdapter<Person>

    private lateinit var handler: Handler

    private var persons: MutableList<Person> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialized)

        sendXML = findViewById(R.id.serialized_btn_send_xml)
        sendJSON = findViewById(R.id.serialized_btn_send_json)
        sendProtoBuf = findViewById(R.id.serialized_btn_send_protobuf)
        received = findViewById(R.id.serialized_receive_text)

        listPersons = findViewById(R.id.serialized_form_persons_list)
        personName = findViewById(R.id.serialized_form_name)
        personFirstname = findViewById(R.id.serialized_form_firstname)
        personMiddlename = findViewById(R.id.serialized_form_middlename)
        personPhoneNumber = findViewById(R.id.serialized_form_phone_number)
        personPhoneType = findViewById(R.id.serialized_form_phone_type)
        btnAddPerson = findViewById(R.id.serialized_form_btn_add_person)
        btnClearList = findViewById(R.id.serialized_form_btn_clear_list)

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            persons
        )

        listPersons.adapter = adapter

        btnAddPerson.setOnClickListener {
            if (personName.text.toString().isBlank()) {
                toast("Add a name")
            } else if(personFirstname.text.toString().isBlank()) {
                toast("Add a firstname")
            } else if(personPhoneNumber.text.toString().isBlank()) {
                toast("Add a phone number")
            } else {
                persons.add(
                    Person(
                        personName.text.toString(),
                        personFirstname.text.toString(),
                        personMiddlename.text.toString().ifEmpty { null },
                        Person.Phone(
                            personPhoneNumber.text.toString(),
                            Person.Phone.PhoneType.valueOf(
                                personPhoneType.selectedItem.toString().uppercase(Locale.getDefault())
                            )
                        )
                    )
                )
                adapter.notifyDataSetChanged()
            }
        }

        btnClearList.setOnClickListener {
            persons.clear()
            adapter.notifyDataSetChanged()
        }

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

            persons.forEach {
                serializer.startTag(null, "person")
                addElemToXML(serializer, "name", it.name)
                addElemToXML(serializer, "firstname", it.firstname)
                addElemToXML(serializer, "middlename", it.middlename)

                addElemToXML(serializer, "phone", it.phone.number, "type", it.phone.type.phoneType)

                serializer.endTag(null, "person")
            }

            serializer.endTag(null, "directory")
            serializer.endDocument()
            serializer.flush()

            Log.d("xml", String(stream.toByteArray()))
            s.sendRequest("http://mobile.iict.ch/api/xml", String(stream.toByteArray()), "application/xml")
        }

        sendJSON.setOnClickListener {
            var s = SymComManager(this)
            //s.sendRequest("http://mobile.iict.ch/api/json", sent.text.toString(), "application/json")
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

            //Log.d("main", sent.text.toString())
            s.sendRequest("http://mobile.iict.ch/api/protobuf", p.toString(), "application/protobuf")
        }
    }

    override fun handleServerResponse(response: String) {
        received.setText("")
        for (p in XMLToPersons(response)) {
            received.setText("${received.text}\n${p.toString()}")
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun addElemToXML(serializer: XmlSerializer, tag: String, value: String?, attributeName: String? = null, attributeValue: String? = null) {
        if (value != null) {
            if(value.isNotBlank()) {
                serializer.startTag(null, tag)
                if(attributeName != null && attributeValue != null) {
                    if(attributeName.isNotBlank() && attributeValue.isNotBlank()) {
                        serializer.attribute(null, attributeName, attributeValue)
                    }
                }
                serializer.text(value)
                serializer.endTag(null, tag)
            }
        }
    }

    private fun XMLToPersons(xml: String): MutableList<Person> {
        var persons = mutableListOf<Person>()

        var factory = XmlPullParserFactory.newInstance();
        factory.isNamespaceAware = false
        var xpp = factory.newPullParser()

        val stream = ByteArrayInputStream(xml.toByteArray())

        xpp.setInput(stream, null)
        var eventType = xpp.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG && xpp.name == "person") {
                val p = Person()
                eventType = xpp.next()
                while (eventType != XmlPullParser.END_TAG || xpp.name != "person") {
                    when(eventType) {
                        XmlPullParser.START_TAG -> {
                            when(xpp.name) {
                                "name" -> {
                                    xpp.next()
                                    p.name = xpp.text
                                }
                                "firstname" -> {
                                    xpp.next()
                                    p.firstname = xpp.text
                                }
                                "middlename" -> {
                                    xpp.next()
                                    p.middlename = xpp.text
                                }
                                "phone" -> {
                                    p.phone.type = Person.Phone.PhoneType.valueOf(
                                        xpp.getAttributeValue(null, "type")
                                            .toString()
                                            .uppercase(Locale.getDefault())
                                    )
                                    xpp.next()
                                    p.phone.number = xpp.text
                                }
                            }
                        }
                    }
                    eventType = xpp.next()
                }
                persons.add(p)
            }
            eventType = xpp.next()
        }

        return persons
    }
}