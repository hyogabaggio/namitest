package com.nami

class Users {

    Long id
    Long version

    String nom
    String prenom
    String username
    String sexe
    String email
    Integer age


    Date dateCreated


    static constraints = {
        age(range: 1..65)
    }
}
