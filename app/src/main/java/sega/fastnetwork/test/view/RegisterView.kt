package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.User

interface RegisterView {

    fun isRegisterSuccessful(isRegisterSuccessful: Boolean)
    fun setErrorMessage(errorMessage: String)
    fun getUserDetail(user : User)
}