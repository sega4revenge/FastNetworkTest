package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.User

interface DetailUserView {

    fun setErrorMessage(errorMessage: String)
    fun getUserDetail(user : User)

    fun isgetUserDetailSuccess(success : Boolean)

}