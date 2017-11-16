package finger.thuetot.vn.model


import android.graphics.drawable.Drawable
import android.net.Uri

import finger.thuetot.vn.lib.MaterialChips.model.ChipInterface


class LocationChip(private val name: String, private val info: String) : ChipInterface {
    override fun getId(): Any? {
        return null
    }

    override fun getAvatarUri(): Uri? {
        return null
    }


    override fun getAvatarDrawable(): Drawable? {
        return null
    }

    override fun getLabel(): String {
        return name
    }

    override fun getInfo(): String {
        return info
    }
}
