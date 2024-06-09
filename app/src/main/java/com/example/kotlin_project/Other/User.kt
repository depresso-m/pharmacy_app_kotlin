package com.example.kotlin_project.Other

class User {
    var uid: String? = null
    var type: String? = null
    var name: String? = null
    var email: String? = null

    constructor()

    constructor(type: String?, name: String?, email: String?, uid: String?) {
        this.uid = uid
        this.type = type
        this.name = name
        this.email = email
    }
}
