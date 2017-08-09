package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.User

interface DrawerView {

    fun setErrorMessage(errorMessage: String)
    fun     getUserDetail(user : User)

}