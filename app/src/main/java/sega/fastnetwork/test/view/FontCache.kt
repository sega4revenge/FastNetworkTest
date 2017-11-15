package sega.fastnetwork.test.view

import android.content.Context
import android.graphics.Typeface
import java.util.*

object FontCache {
    val ROBOTO_REGULAR = "fonts/myriadpro-regular.otf"
    val ROBOTO_LIGHT = "fonts/myriadpro-light.otf"
    val ROBOTO_BOLD = "fonts/myriadpro-bold.otf"
    val ROBOTO_MEDIUM = "fonts/Roboto-Thin.ttf"
    private val fontCache = Hashtable<String, Typeface>()

    fun getTypeface(name: String, context: Context): Typeface? {
        var tf: Typeface? = fontCache[name]
        if (tf == null) {
            try {
                tf = Typeface.createFromAsset(context.assets, name)
            } catch (e: Exception) {
                return null
            }

            fontCache.put(name, tf)
        }
        return tf
    }
}
