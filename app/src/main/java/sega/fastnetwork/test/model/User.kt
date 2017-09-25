package sega.fastnetwork.test.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Sega on 23/03/2017.
 */


class User() : Parcelable {
    var _id: String? = null
    var name: String? = null
    var email: String? = null
    var photoprofile: String? = null
    var phone: String? = null
    var listproduct: ArrayList<Product>? = ArrayList()
    var listsavedproduct: ArrayList<Product>? = ArrayList()
    var password: String? = null
    var tokenfirebase: String? = null
    val created_at: String? = null
    var facebook : Facebook? = Facebook()
    var google : Google? = Google()

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        name = parcel.readString()
        email = parcel.readString()
        photoprofile = parcel.readString()
        phone = parcel.readString()
        password = parcel.readString()
        tokenfirebase = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(photoprofile)
        parcel.writeString(phone)
        parcel.writeString(password)
        parcel.writeString(tokenfirebase)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}