package ch.heigvd.iict.sym.labo2.directory


class DirectoryUtils {
    companion object {
        /**
         * Genère une liste de Phone
         */
        fun generatePhone(): MutableList<Phone> {
            val phone1 = Phone("012798321", Phone.PhoneType.HOME.toString());
            val phone2 = Phone("012421412", Phone.PhoneType.WORK.toString())
            return mutableListOf(phone1, phone2)
        }

        /**
         * Genère une liste de Person
         */
        fun generatePeople(name: String): MutableList<Person> {
            val person1 = Person(name, "Frank", "tamer", generatePhone())
            return mutableListOf(person1)
        }

        /**
         * Genère un annuaire Directory
         */
        fun generateDirectory(name: String): Directory {
            return Directory(generatePeople(name));
        }
    }
}

