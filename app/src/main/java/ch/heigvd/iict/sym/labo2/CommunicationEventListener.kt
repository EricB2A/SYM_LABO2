// Auteurs: Ilias Goujgali, Eric Bousbaa, Guillaume Laubscher
package ch.heigvd.iict.sym.labo2


/**
 * Interface listener appelé en l'occurence une fois la réponse d'une requete reçu.
 */
interface CommunicationEventListener {
    fun handleServerResponse(response: String, contentType: SymComManager.ContentType)
}