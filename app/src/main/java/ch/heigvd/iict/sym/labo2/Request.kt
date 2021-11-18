package ch.heigvd.iict.sym.labo2

data class Request(var url: SymComManager.Url, var contentType : SymComManager.ContentType, var request:String ) {

}