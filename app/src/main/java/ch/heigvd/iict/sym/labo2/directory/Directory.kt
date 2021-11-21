// Auteurs: Ilias Goujgali, Eric Bousbaa, Guillaume Laubscher
package ch.heigvd.iict.sym.labo2.directory

import ch.heigvd.iict.sym.protobuf.DirectoryOuterClass
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.ByteArrayOutputStream
import java.io.Serializable
import java.util.*

/**
 * Class Directory utiliser dans l'activité de serialisation
 */
@Root(name = "directory") // Root lors d'une séralisation XML
class Directory @JvmOverloads constructor( //@JvmOverloads permet de fournir (entre autre) un constructeur vide pour la déserialisation de Simple.
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
                .setName(p.name).firstname = p.firstname
            if(p.middlename.isNotBlank()) {
                person.middlename = p.middlename
            }
            for (ph in p.phones) {
                phone.clear()
                    .setType(DirectoryOuterClass.Phone.Type.valueOf(ph.type.uppercase(
                        Locale.getDefault()))).number = ph.number
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