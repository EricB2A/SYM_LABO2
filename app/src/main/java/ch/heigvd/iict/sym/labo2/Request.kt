// Auteurs: Ilias Goujgali, Eric Bousbaa, Guillaume Laubscher
package ch.heigvd.iict.sym.labo2

/**
 * Classe représentant une requete. Elle contient les informations utile à son envoie.
 */
data class Request(var url: SymComManager.Url, var contentType : SymComManager.ContentType, var request:String ) {

}