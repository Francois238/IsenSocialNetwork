package fr.isen.lesnullos.isensocialnetwork.model

data class User (var id: String, var name: String, var birthOfDate: String, var sexe: String, var photo : String){
    constructor(): this("", "","","", "")
}