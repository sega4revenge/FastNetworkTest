package sega.fastnetwork.test.view

import sega.fastnetwork.test.model.Product

/**
 * Created by sega4 on 05/08/2017.
 */

interface ProductListView {

    fun setErrorMessage(errorMessage: String)
    fun getListProduct(productlist : ArrayList<Product>)




}