package finger.thuetot.vn.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by VinhNguyen on 9/22/2017.
 */
class Chat : Parcelable {
    var userfrom: User? = null
    var userto: User? = null
    var messages: ArrayList<ChatMessager>? = ArrayList()



    constructor(parcel: Parcel) : this() {
        userfrom = parcel.readParcelable(User::class.java.classLoader)
        userto = parcel.readParcelable(User::class.java.classLoader)
    }

    constructor()


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(userfrom, flags)
        parcel.writeParcelable(userto, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}