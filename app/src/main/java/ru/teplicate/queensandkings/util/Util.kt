package ru.teplicate.queensandkings.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

object Util {

    fun getDrawable(resources: Resources, id: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, id, null)
    }
}