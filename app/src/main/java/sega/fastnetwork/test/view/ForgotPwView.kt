package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.User

interface ForgotPwView {

    fun isFotgotPwSuccessful(isForgotPwSuccessful: Boolean, type: Int)
    fun setErrorMessage(errorMessage: String, type : Int)
}