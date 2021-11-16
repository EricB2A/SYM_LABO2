package ch.heigvd.iict.sym.labo2.directory

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name="directory")
class Directory @JvmOverloads constructor(
    @field:ElementList(inline=true)
    var directory:  MutableList<Person> = mutableListOf()
) {}