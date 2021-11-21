package ch.heigvd.iict.sym.labo2.directory

import ch.heigvd.iict.sym.protobuf.DirectoryOuterClass
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*

@Root(name = "directory")
class Directory @JvmOverloads constructor(
    @field:ElementList(inline = true)
    var directory: MutableList<Person> = mutableListOf()
) : Serializable {
    fun toProtoBuf(): String {
        val stream = ByteArrayOutputStream()
        val directory = DirectoryOuterClass.Directory.newBuilder()
        val person = DirectoryOuterClass.Person.newBuilder()
        val phone = DirectoryOuterClass.Phone.newBuilder()

        for (p in this.directory) {
            person.clear()
                .setName(p.name)
                .setFirstname(p.firstname)
            if(p.middlename.isNotBlank()) {
                person.setMiddlename(p.middlename)
            }
            for (ph in p.phones) {
                phone.clear()
                    .setType(DirectoryOuterClass.Phone.Type.valueOf(ph.type.uppercase(
                        Locale.getDefault())))
                    .setNumber(ph.number)
                person.addPhone(phone.build())
            }
            directory.addResults(person.build())
        }
        directory.build().writeTo(stream)
        return String(stream.toByteArray())
    }

    companion object {
        fun fromProtoBuf(directory: DirectoryOuterClass.Directory): Directory {
            val d = Directory()
            for (person in directory.resultsList) {
                val p = Person()
                p.firstname = person.firstname
                p.name = person.name
                if(person.middlename.isNotBlank()) {
                    p.middlename = person.middlename
                }
                for (phone in person.phoneList) {
                    val ph = Phone()
                    ph.type = phone.type.name.lowercase(Locale.getDefault())
                    ph.number = phone.number
                    p.phones += ph
                }
                d.directory += p
            }
            return d
        }
    }
}