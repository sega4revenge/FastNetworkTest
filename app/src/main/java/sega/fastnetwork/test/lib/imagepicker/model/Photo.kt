package sega.fastnetwork.test.lib.imagepicker.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Describe :全部照片
 * Created by Rain on 17-4-28.
 */
class Photo : Parcelable {

    private var id: Int = 0
    var path: String? = null
        private set
    private var size: Long = 0//byte 字节

    constructor(id: Int, path: String) {
        this.id = id
        this.path = path
    }

    constructor(id: Int, path: String, size: Long) {
        this.id = id
        this.path = path
        this.size = size
    }

    private constructor(`in`: Parcel) {
        id = `in`.readInt()
        path = `in`.readString()
        size = `in`.readLong()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Photo) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(path)
        dest.writeLong(size)
    }

    companion object {

        val CREATOR: Parcelable.Creator<Photo> = object : Parcelable.Creator<Photo> {
            override fun createFromParcel(`in`: Parcel): Photo {
                return Photo(`in`)
            }

            override fun newArray(size: Int): Array<Photo?> {
                return arrayOfNulls(size)
            }
        }
    }
}
