package ch.heigvd.iict.sym.labo2

import android.util.Xml

data class Person(var name: String) : XMLSerializable {
    override fun serialize(): String {
        // TODO serializer en XML
        return ""
    }
}