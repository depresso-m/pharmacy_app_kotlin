package com.example.kotlin_project.other

import java.io.Serializable

class Collection : Serializable {
    var name: String? = null
    var drugs: ArrayList<Drug>? = null

    constructor()
    constructor(name: String?, drugs: ArrayList<Drug>?) {
        this.name = name
        this.drugs = drugs
    }
}