package sega.fastnetwork.test.view

/**
 * Created by VinhNguyen on 8/30/2017.
 */
interface EditProductInterface {
    public fun ConnectHttp(userid: String, productname : String, price : String, time: String, number : String, category: String, address: String, description: String, productid: String,imgdel: String)
    public fun ConnectHttpDeleteProduct(productid: String,listimg: String)
}