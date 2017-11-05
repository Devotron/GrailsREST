package webservicerest.api

class Library {

    String name
    String address
    Integer yearCreated

    static hasMany = [books:Book]

    static constraints = {
        name blank: false
        address blank: false
        yearCreated nullable: false
    }
}
