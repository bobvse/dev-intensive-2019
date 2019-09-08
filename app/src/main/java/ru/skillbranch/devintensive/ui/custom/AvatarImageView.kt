package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView

class AvatarImageView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    fun setInitials(initials: String) {

    }

    //temp
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.GREEN
        it.style = Paint.Style.FILL
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val radius = width / 2F
        canvas?.drawCircle(radius, radius, radius, paint)
    }
}