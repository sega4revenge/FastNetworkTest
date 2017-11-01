package sega.fastnetwork.test.model

import org.json.JSONObject

/**
 * Created by Sega on 23/03/2017.
 */


class Response {
    val status : Int?=null
    val user: User? = null
    val message: String? = null
    val token: String? = null
    var product : Product? =null
    var statussave: Boolean? = null
    var listchat: ChatMessager? = null
    var chatlist: JSONObject? = null
}