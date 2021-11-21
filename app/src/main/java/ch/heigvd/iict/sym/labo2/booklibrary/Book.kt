package ch.heigvd.iict.sym.labo2.booklibrary

import java.math.BigInteger
import java.util.concurrent.Flow

/**
 * Class Book utilisé pour la déserialisation des livres dans GraphQl
 */
class Book(
    var id: BigInteger,
    var title: String,
    var avergaeRating: Double,
    var isbn13: String,
    var language_code: String,
    var num_pages: Int,
    var publication_date: String,
    var publisher: String,
    var text_reviews_count: Int
)