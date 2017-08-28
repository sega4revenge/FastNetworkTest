package sega.fastnetwork.test.lib.imagepicker.model

import java.util.*

/**
 * Describe :相册
 * Created by Rain on 17-4-28.
 */
class PhotoDirectory {

    private var id: String? = null
    var coverPath: String? = null
    var name: String? = null
    private val photos = ArrayList<Photo>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhotoDirectory) return false

        if (id != other.id) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = id!!.hashCode()
        result = 31 * result + name!!.hashCode()
        return result
    }

    fun setId(id: String) {
        this.id = id
    }

    fun setDateAdded(dateAdded: Long) {}

    fun getPhotos(): ArrayList<Photo> {
        return photos
    }


    val photoPaths: List<String>
        get() {
            val paths = ArrayList<String>(photos.size)
            photos.mapTo(paths) { it.path!! }
            return paths
        }

    fun addPhoto(id: Int, path: String) {
        photos.add(Photo(id, path))
    }

    fun addPhoto(id: Int, path: String, size: Long) {
        photos.add(Photo(id, path, size))
    }

}
