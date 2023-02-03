package fr.isen.lesnullos.isensocialnetwork.model

class Post(
    var namePerson : String ?= null,
    var title : String ?= null,
    var body : String ?= null,
    var image : String ?= null,
    var commentaire : ArrayList<String> ?= null,
    var like : ArrayList<String> ?= null,

)
