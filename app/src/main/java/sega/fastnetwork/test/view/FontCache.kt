package sega.fastnetwork.test.view

import android.content.Context
import android.graphics.Typeface
import java.util.*

object FontCache {
    val ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf"
    val ROBOTO_LIGHT = "fonts/Roboto-Light.ttf"
    val ROBOTO_BOLD = "fonts/Roboto-Bold.ttf"
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
