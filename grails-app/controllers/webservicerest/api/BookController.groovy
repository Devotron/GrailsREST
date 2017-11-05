package webservicerest.api

import grails.converters.JSON
import grails.converters.XML
import org.grails.web.json.JSONObject

class BookController {

    static  allowedMethods = [getBook: "GET", getBooks: "GET", getBooksRedirect:"GET", createBook: "POST", updateBook: "PUT", deleteBook: "DELETE", optionsBooks: "OPTIONS"]

    def index() { }

    // HTTP GET
    def getBook() {
        println("GET BOOK")

        if (!Book.get(params.id)) {
            render(status: 400, text: "The ressource book ID : ${params.id} doesn't exist")
            return
        }

        response.status = 200

        withFormat {
            xml { render Book.get(params.id) as XML }
            json { render Book.get(params.id) as JSON }
        }
    }

    // HTTP GET
    def getBooks() {
        println("GET BOOKS")
        if ( params.status !=null ) {
            response.status = 301
        } else {
            response.status = 200
        }

        withFormat {
            xml { render Book.getAll() as XML }
            json { render Book.getAll() as JSON }
        }

    }

    // Cas WebServiceREST/books
    def getBooksRedirect() {
        println("GET BOOKS REDIRECT")
        redirect(action: "getBooks", permanent:true, params: [status: 301])
        return
    }

    // HTTP POST
    def createBook() {
        println("POST BOOK")
        println(params)

        if ( request.getJSON().toString().equals("{}") && invalidBookParams(params) ) {
            println("400")
            render(status: 400, text:"Bad request : method POST datas must be send either as form-data with parameters" +
                    " (name, releaseDate, isbn, author, library) or json")
            return
        }

        if ( !Library.get(params.library) ) {
            render(status: 400, text:"Bad request : The ressource book ID : ${params.id} doesn't exist")
            return
        }

        if ( !invalidBookParams(params) ) {

            Book book = new Book(name:  params.name, releaseDate: Date.parse("dd/MM/yyyy", params.releaseDate), isbn: params.isbn, author: params.author)

            Library.get(params.library).addToBooks(book).save(flush:true)
        }

        render(status: 200)

    }

    // HTTP PUT
    def updateBook() {
        println("PUT BOOK")
        println(params)

        if ( params.id == null) {
            println("400")
            render (status: "400", text: "Bad request : method PUT needs a book ressource ID")
            return
        }

        if ( request.getJSON().toString().equals("{}") && invalidBookParams(params) ) {
            println("400")
            render(status: 400, text:"Bad request : method PUT datas must be send either as form-data with parameters" +
                    " (name, releaseDate, isbn, author, library) or json")
            return
        }

        if ( request.getJSON().toString().equals("{}") && !Library.get(params.library) ) {
            render(status: 400, text:"Bad request : The ressource library ID : ${params.library} doesn't exist")
            return
        }

        if (!Book.get(params.id)) {
            render(status: 400, text: "The ressource book ID : ${params.id} doesn't exist")
            return
        }


        JSONObject b = new JSONObject(request.getJSON())

        def book = Book.get(params.id)
        book.name = b.name
        book.releaseDate = Date.parse("yyyy-MM-dd", b.releaseDate)
        book.isbn = b.isbn
        book.author = b.author

        if (!Library.get(b.library.id)) {
            render(status: 400, text: "The ressource library ID : ${b.library.id} doesn't exist")
            return
        }

        book.save(flush:true)

        Library.get(b.library.id).addToBooks(book).save(flush:true)

        render(status: 200)
    }

    // HTTP DELETE
    def deleteBook(){
        println("DELETE BOOK")

        // URL mapping /book/$id? pour fournir plus d'information que
        // le mapping /book/$$id qui causerait simplement un 500
        if ( params.id == null) {
            println("400")
            render (status: "400", text: "Bad request : method DELETE needs a book ressource ID")
            return
        }

        if ( !Book.get(params.id) ) {
            println("404")
            render (status: "404", text: "The ressource book ID : ${params.id} doesn't exist")
            return
        }

        Book.get(params.id).delete(flush: true)
        render (status: "200")

    }

    // HTTP OPTIONS
    def optionsBooks() {
        println("OPTIONS BOOK")
        response.setHeader('Allow', "GET, POST, PUT, DELETE, OPTIONS")
        response.status = 200
        return response
    }

    //Verifie si l'ensemble des parametres nécéssaires est présent dans la requete
    def invalidBookParams(def params) {

        if ( !params.name || !params.releaseDate || !params.isbn || !params.author || !params.library ) {
            return true
        } else return false
    }
}
