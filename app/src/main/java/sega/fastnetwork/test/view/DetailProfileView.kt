package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.Product
import sega.fastnetwork.test.model.User

/**
 * Created by VinhNguyen on 8/23/2017.
 */
interface DetailProfileView {
    fun setErrorMessage(errorMessage: String)
    fun getListProduct(productlist : ArrayList<Product>,user: User)
    fun getUser(user: User)
    fun setMessagerNotFound()
}