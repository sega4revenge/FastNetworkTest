package finger.thuetot.vn.lib.MaterialChips.model


import android.graphics.drawable.Drawable
import android.net.Uri

class Chip : ChipInterface {

    private var id: Any? = null
    private var avatarUri: Uri? = null
    private var avatarDrawable: Drawable? = null
    private var label: String? = null
    private var info: String? = null

    constructor(id: Any, avatarUri: Uri?, label: String, info: String?) {
        this.id = id
        this.avatarUri = avatarUri
        this.label = label
        this.info = info
    }

    constructor(id: Any, avatarDrawable: Drawable?, label: String, info: String?) {
        this.id = id
        this.avatarDrawable = avatarDrawable
        this.label = label
        this.info = info
    }

    constructor(avatarUri: Uri?, label: String, info: String?) {
        this.avatarUri = avatarUri
        this.label = label
        this.info = info
    }

    constructor(avatarDrawable: Drawable?, label: String, info: String?) {
        this.avatarDrawable = avatarDrawable
        this.label = label
        this.info = info
    }

    constructor(id: Any, label: String, info: String?) {
        this.id = id
        this.label = label
        this.info = info
    }

    constructor(label: String, info: String?) {
        this.label = label
        this.info = info
    }

    override fun getId(): Any? {
        return id
    }

    override fun getAvatarUri(): Uri? {
        return avatarUri
    }

    override fun getAvatarDrawable(): Drawable? {
        return avatarDrawable
    }

    override fun getLabel(): String? {
        return label
    }

    override fun getInfo(): String? {
        return info
    }
}
