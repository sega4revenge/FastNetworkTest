package sega.fastnetwork.test.model

/**
 * Created by Sega on 23/03/2017.
 */


class User {
    var _id: String? = null
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var tokenfirebase: String? = null
    val created_at: String? = null
    var facebook : Facebook? = Facebook()
    var google : Google? = Google()
}