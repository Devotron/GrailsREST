package webservicerest

import webservicerest.api.Library
import webservicerest.api.Book

class BootStrap {

    def init = { servletContext ->

        // Initialisation de données par défaut

        if ( Library.count() == 0 ) {

            Library lib = new Library(name: "Librairie Saint-Joseph", address: "15 Av. Gonzales", yearCreated: 1985).save(flush:true, failOnError:true)
            Library lib2 = new Library(name: "Librairie Hermes", address: "23 Av. Apotheon", yearCreated: 1900).save(flush:true, failOnError:true)

            if ( Book.count() == 0 ) {

                Book b1 = new Book(name:  "Livre des révélations", releaseDate: new Date(), isbn: "REV2017",author: "Flamarion")
                Book b2 = new Book(name: "Grails pour les nuls", releaseDate: new Date(), isbn: "GRNUL17", author: "Hachette")
                Book b3 = new Book(name: "La dépression après Grails", releaseDate: new Date(), isbn: "DEPGL17", author: "Quebec-livres")
                Book b4 = new Book(name: "1984", releaseDate: new Date(), isbn: "L198417", author: "Georges Orwell")
                Book b5 = new Book(name: "Gang of four", releaseDate: new Date(), isbn: "LGO417", author: "J'ai lu")

                lib.addToBooks(b1).save(flush:true, failOnError:true)
                lib.addToBooks(b2).save(flush:true, failOnError:true)
                lib.addToBooks(b3).save(flush:true, failOnError:true)

                lib2.addToBooks(b4).save(flush:true, failOnError:true)
                lib2.addToBooks(b5).save(flush:true, failOnError:true)
            }


        }


    }
    def destroy = {
    }
}
