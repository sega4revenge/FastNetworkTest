package sega.fastnetwork.test.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by cc on 9/13/2017.
 */
class Location() : Parcelable {
    var point: String? = null
    var address: String? = null
    /* var coordinates : IntArray? = null*/
    var coordinates : ArrayList<Double>? = ArrayList()
    constructor(parcel: Parcel) : this() {
        point = parcel.readString()
        address = parcel.readString()
        /*  coordinates = parcel.createIntArray()*/
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(point)
        parcel.writeString(address)
        /*  parcel.writeIntArray(coordinates)*/
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }


}