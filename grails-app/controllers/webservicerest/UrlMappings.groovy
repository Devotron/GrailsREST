package webservicerest

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')

        "/book"(controller: "book", action: "getBooks", method: "GET")
        "/books"(controller: "book", action: "getBooksRedirect", method: "GET")
        "/book"(controller: "book", action: "createBook", method: "POST")
        "/book"(controller: "book", action: "optionsBooks", method: "OPTIONS")
        "/book/$id"(controller: "book", action: "getBook", method: "GET")
        "/book/$id?"(controller: "book", action: "updateBook", method: "PUT")
        "/book/$id?"(controller: "book", action: "deleteBook", method: "DELETE")
        "/library"(controller: "library", action: "getLibraries", method: "GET")
        "/libraries"(controller: "library", action: "getLibrariesRedirect", method: "GET")
        "/library"(controller: "library", action: "createLibrary", method: "POST")
        "/library"(controller: "library", action: "optionsLibrary", method: "OPTIONS")
        "/library/$id"(controller: "library", action: "getLibrary", method: "GET")
        "/library/$id?"(controller: "library", action: "updateLibrary", method:"PUT")
        "/library/$id?"(controller: "library", action: "deleteLibrary", method: "DELETE")
        "/library/$id/books"(controller: "library", action: "getLibraryBooks", method: "GET")
        "/library/$id/book/$idbook"(controller: "library", action: "getLibraryBook", method: "GET")
        "/library/$id/book"(controller: "library", action: "addBook", method: "POST")
    }
}
