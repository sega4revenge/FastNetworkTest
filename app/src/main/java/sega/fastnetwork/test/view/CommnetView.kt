package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.Comment
import sega.fastnetwork.test.model.ResponseListComment

/**
 * Created by cc on 8/17/2017.
 */
interface CommnetView {

    fun isCommentSuccessful(isCommentSuccessful: Boolean)
    fun setErrorMessage(errorMessage: String)
    fun getCommentDetail(listcomment :ArrayList<Comment>)

}