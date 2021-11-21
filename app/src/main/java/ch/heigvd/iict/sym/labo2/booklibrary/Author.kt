package ch.heigvd.iict.sym.labo2.booklibrary

import java.math.BigInteger

/**
 * Class Auteur utilisé pour la déserialisation des auteurs dans GraphQl
 */
class Author(var id: BigInteger, var name: String, var books: List<Book>) {

}