package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.ResponseListProduct
import sega.fastnetwork.test.model.User

interface HomeView {

    fun setErrorMessage(errorMessage: String)
    fun getUserDetail(user : User)

    fun isgetUserDetailSuccess(success : Boolean)
    fun isgetProductDetailSuccess(success : Boolean,product: ResponseListProduct)
}