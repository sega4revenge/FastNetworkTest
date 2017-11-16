package finger.thuetot.vn.model

import android.graphics.drawable.Drawable
import android.net.Uri
import finger.thuetot.vn.lib.MaterialChips.model.ChipInterface

/**
 * Created by sega4 on 25/07/2017.
 */



data class Category(val id : Int,val name : String, val avatar : Drawable, var selected : Boolean): ChipInterface {
    override fun getId(): Any? {
        return id
    }

    override fun getAvatarUri(): Uri? {
        return null
    }

    override fun getAvatarDrawable(): Drawable? {
        return avatar
    }

    override fun getLabel(): String {
        return name
    }

    override fun getInfo(): String? {
        return null
    }
}