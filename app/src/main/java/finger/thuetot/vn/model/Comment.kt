package finger.thuetot.vn.model

/**
 * Created by sega4 on 15/08/2017.
 */

class Comment {
    var _id: String? = null
    var user: User? = null
    var content: String? = null
    var time: String? = null
    var listlike: ArrayList<String>? = ArrayList()
    var listreply: ArrayList<Comment>? = ArrayList()
    var stt: Boolean = false

    var comment: Comment? = null

}
