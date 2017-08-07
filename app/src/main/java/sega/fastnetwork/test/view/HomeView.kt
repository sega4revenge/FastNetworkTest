package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.ResponseListProduct
import sega.fastnetwork.test.model.User

/**
 * Created by sega4 on 05/08/2017.
 */

interface HomeView {

    fun setErrorMessage(errorMessage: String)
    fun getUserDetail(user : User)




}