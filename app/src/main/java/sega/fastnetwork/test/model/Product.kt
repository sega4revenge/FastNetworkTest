package sega.fastnetwork.test.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Sega on 23/03/2017.
 */


class Product() : Parcelable {
    var _id : String?=null
    var user: User? = null
    var productname: String? = null
    var price: String? = null
    var time: String? = null
    var number : String?=null
    var category: String?= null
    var address: String? = null
    var description: String? = null
    var type: String? = null
    var created_at: String? = null
    var images : ArrayList<String>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        productname = parcel.readString()
        number = parcel.readString()
        price = parcel.readString()
        time = parcel.readString()
        category = parcel.readString()
        address = parcel.readString()
        description = parcel.readString()
        type = parcel.readString()
        created_at = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(productname)
        parcel.writeString(number)
        parcel.writeString(price)
        parcel.writeString(time)
        parcel.writeString(category)
        parcel.writeString(address)
        parcel.writeString(description)
        parcel.writeString(type)
        parcel.writeString(created_at)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

}