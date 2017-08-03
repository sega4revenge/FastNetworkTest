package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.User

interface LoginView {

    fun isLoginSuccessful(isLoginSuccessful: Boolean)
    fun isRegisterSuccessful(isRegisterSuccessful: Boolean,type : Int)
    fun setErrorMessage(errorMessage: String)
    fun getUserDetail(user : User)
}