package ru.teplicate.queensandkings.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat

object Util {

    fun getDrawable(resources: Resources, id: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, id, null)
    }

    fun showToast(stringId: Int, context: Context) {
        Toast.makeText(context, context.resources.getText(stringId), Toast.LENGTH_SHORT).show()
    }
}