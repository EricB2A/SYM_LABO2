package ch.heigvd.iict.sym.labo2.directory

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Order
import org.simpleframework.xml.Path
import java.io.Serializable

@Order(elements=["name"])
class Person @JvmOverloads constructor(
    @Path("name")
    @field:Element
    var name: String ="",
    @field:Element
    var firstname: String ="",
    @field:Element(required = false)
    var middlename: String ="",
    @field:ElementList(inline = true, empty = false)
    var phones: MutableList<Phone> = mutableListOf()
) : Serializable {


}