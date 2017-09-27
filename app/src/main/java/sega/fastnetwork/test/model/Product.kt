package sega.fastnetwork.test.model

import android.os.Bundle
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
    var description: String? = null
    var type: String? = null
    var created_at: String? = null
    var status: String? = null
    var view: Int? = null
    var location : Location? = null
    var images : ArrayList<String>? = ArrayList()
    var comment : ArrayList<Comment>? = ArrayList()
    var bundle: Bundle = Bundle()

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        user = parcel.readParcelable(User::class.java.classLoader)
        productname = parcel.readString()
        price = parcel.readString()
        time = parcel.readString()
        number = parcel.readString()
        category = parcel.readString()
        description = parcel.readString()
        type = parcel.readString()
        created_at = parcel.readString()
        status = parcel.readString()
        location = parcel.readParcelable(Location::class.java.classLoader)
        view = parcel.readInt()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeParcelable(user, flags)
        parcel.writeString(productname)
        parcel.writeString(price)
        parcel.writeString(time)
        parcel.writeString(number)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(type)
        parcel.writeString(created_at)
        parcel.writeString(status)
        parcel.writeParcelable(location, flags)
        parcel.writeInt(view!!)


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