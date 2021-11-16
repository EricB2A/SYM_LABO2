package ch.heigvd.iict.sym.labo2.directory

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import java.io.Serializable

@Root
class Phone @JvmOverloads  constructor(
    @field:Text
    var number: String= "",

    @field:Attribute(required = true )
    var type: String = PhoneType.HOME.toString()
) : Serializable{

    enum class PhoneType(val text: String) {
        HOME("home"),
        WORK("work"),
        MOBILE("mobile");

        override fun toString(): String {
            return text
        }
    }
    override fun toString(): String {
        return number;
    }
}