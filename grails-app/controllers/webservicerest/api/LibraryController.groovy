package webservicerest.api

import grails.converters.JSON
import grails.converters.XML
import org.grails.web.json.JSONObject

class LibraryController {

    static  allowedMethods = [getLibrary: "GET", getLibraries: "GET", getLibrariesRedirect:"GET", getLibraryBooks: "GET", getLibraryBook: "GET", createLibrary: "POST", addBook: "POST", updateLibrary: "PUT", deleteLibrary: "DELETE", optionsLibrary: "OPTIONS"]

    def index() {}

    //GET /biblio/1/books
    //PUT /biblio/2/book/1


    // HTTP GET
    def getLibrary() {
        println("GET LIBRARY")

        if ( !Library.get(params.id) ) {
            render (status: "404", text: "The ressource library ID : ${params.id} doesn't exist")
            return
        }

        withFormat {
            xml { render Library.get(params.id) as XML }
            json { render Library.get(params.id) as JSON }
        }

    }

    // HTTP GET
    def getLibraries() {
        println("GET LIBRARIES")
        if ( params.status !=null ) {
            response.status = 301
        } else {
            response.status = 200
        }

        withFormat {
            xml { render Library.getAll() as XML }
            json { render Library.getAll() as JSON }
        }
    }

    // Cas WebServiceREST/books
    def getLibrariesRedirect() {
        println("GET LIBRARIES REDIRECT")
        redirect(action: "getLibraries", permanent:true, params: [status: 301])
        return
    }

    // HTTP POST
    def createLibrary() {
        println("POST LIBRARY")


        if ( request.getJSON().toString().equals("{}") && invalidLibraryParams(params) ) {
            println("400")
            render(status: 400, text:"Bad request : method POST datas must be send either as form-data with parameters" +
                    " (name, address, yearCreated) or JSON")
            return
        }

        //CAS form-data
        if ( !invalidLibraryParams(params) ) {
            println("FORM-DATA")

            Library library = new Library(name: params.name, address: params.address, yearCreated: Integer.parseInt(params.yearCreated))
            library.save(flush:true)
            render(status: 200)
            return
        }

        //CAS JSON
        // - Recuperation du JSON
        JSONObject json = new JSONObject(request.getJSON())

        if ( invalidLibraryParams(json) ) {
            render(status: 400, text:"Bad request : method POST datas must be send as JSON (name:, address:, yearCreated:)")
            return
        }

        def library = new Library(name: json.name, yearCreated: json.optInt("yearCreated"), address:  json.address)
        library.save(flush:true)
        render(status: 200)
    }

    // HTTP PUT
    def updateLibrary() {
        println("PUT LIBRARY")

        if ( params.id == null) {
            println("400")
            render (status: "400", text: "Bad request : method PUT needs a library ressource ID")
            return
        }

        if (!Library.get(params.id)) {
            render(status: 404, text: "The ressource library ID : ${params.id} doesn't exist")
            return
        }

        // - Recuperation du JSON
        JSONObject b = new JSONObject(request.getJSON())

        if ( request.getJSON().toString().equals("{}") || invalidLibraryParams(b) ) {
            println("400")
            render(status: 400, text:"Bad request : method PUT datas must be send as JSON" +
                    " (name:, address:, yearCreated:)")
            return
        }

        def library = Library.get(params.id)
        library.name = b.name
        library.yearCreated = b.optInt("yearCreated")
        library.address = b.address

        library.save(flush:true)

        render(status: 200)
    }

    // HTTP DELETE
    def deleteLibrary() {
        println("DELETE LIBRARY")

        // URL mapping /library/$id? pour fournir plus d'information que
        // le mapping /library/$id qui causerait simplement un 500
        if ( params.id == null) {
            println("400")
            render (status: "400", text: "Bad request : method DELETE needs a library ressource ID")
            return
        }

        if ( !Library.get(params.id) ) {
            println("404")
            render (status: "404", text: "The ressource library ID : ${params.id} doesn't exist")
            return
        }

        Library.get(params.id).delete(flush: true)
        render (status: "200")

    }

    // HTTP OPTIONS
    def optionsLibrary() {
        println("OPTIONS LIBRARY")
        response.setHeader('Allow', "GET, POST, PUT, DELETE, OPTIONS")
        response.status = 200
        return response
    }

    //Verifie si l'ensemble des parametres nécéssaires est présent dans la requete
    def invalidLibraryParams(def params) {

        if ( !params.name || !params.address || !params.yearCreated ) {
            return true
        } else return false
    }

    // GET library/id/book/idbook
    def getLibraryBooks() {
        println("GET LIBRARY BOOKS")
        if (!Library.get(params.id)) {
            println("Pas de bibliothèque avec cet ID")
            render(status: 400, text: "The ressource library ID : ${params.id} doesn't exist")
            return
        }

        response.status = 200

        def books = Book.findAllByLibrary(Library.get(params.id))

        withFormat {
            xml { render books as XML }
            json { render books as JSON }
        }

    }

    // GET library/id/books/idbook
    def getLibraryBook() {
        println("GET LIBRARY BOOK")
        println(params)

        if (!Library.get(params.id)) {
            render(status: 404, text: "The ressource library ID : ${params.id} doesn't exist")
            return
        }

        if (!Book.get(params.idbook)) {
            render(status: 404, text: "The ressource book ID : ${params.idbook} doesn't exist")
            return
        }

        def book = Book.get(params.idbook)

        if ( book.library.id.equals(params.id) ) {
            render(status: 404, text: "The ressource library[ID : ${params.id}] doesn't have a ressource book with ID : ${params.idbook}")
            return
        }

        response.status = 200

        withFormat {
            xml { render book as XML }
            json { render book as JSON }
        }

    }

    def addBook() {

        if (!Library.get(params.id)) {
            render(status: 404, text: "The ressource library ID : ${params.id} doesn't exist")
            return
        }

        if ( request.getJSON().toString().equals("{}") ) {
            println("400")
            render(status: 400, text:"Bad request : method PUT datas must be send as JSON {name:, releaseDate:, isbn:, author:, library:{id:}})")
            return
        }

        JSONObject b = new JSONObject(request.getJSON())

        if ( !b.name || !b.releaseDate || !b.isbn || !b.author ) {
            println("400")
            render(status: 400, text:"Bad request : missing parameter(s) {name:, releaseDate:, isbn:, author:)")
            return
        }

        Book book = new Book(name:  b.name, releaseDate: Date.parse("yyyy-MM-dd", b.releaseDate), isbn: b.isbn, author: b.author)

        Library.get(params.id).addToBooks(book).save(flush:true)
        render(status: 200)
    }

}
