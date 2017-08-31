package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.Product

/**
 * Created by VinhNguyen on 8/9/2017.
 */
interface SearchView {

    fun setErrorMessage(errorMessage: String)

    fun getListProduct(productlist : ArrayList<Product>)
    fun setMessagerNotFound()
}