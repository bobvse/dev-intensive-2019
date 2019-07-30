package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {
    private var borderColor = Color.WHITE
    private var borderWidth = (2 * resources.displayMetrics.density + 0.5f).toInt()

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, borderColor)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            a.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setImageDrawable(BitmapDrawable(resources, transformToCircleWithColorBorder(getBitmapFromDrawable(), borderColor, borderWidth)))
    }

    private fun getBitmapFromDrawable(): Bitmap {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun transformToCircleWithColorBorder(bitmap: Bitmap, borderColor: Int, borderWidth: Int): Bitmap {
        val minSize = min(bitmap.width, bitmap.height)
        val result = Bitmap.createBitmap(minSize, minSize, Bitmap.Config.ARGB_8888)
        var paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }

        val canvas = Canvas(result)
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(minSize / 2F, minSize / 2F, minSize / 2F, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val rect = Rect(0, 0, minSize, minSize)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        paint = Paint().apply {
            style = Paint.Style.STROKE
            color = borderColor
            strokeWidth = borderWidth.toFloat()
        }
        canvas.drawCircle(minSize / 2F, minSize / 2F, minSize / 2F, paint)

        return result
    }

    @Dimension
    fun getBorderWidth(): Int = (borderWidth / resources.displayMetrics.density + 0.5f).toInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = (borderWidth * resources.displayMetrics.density + 0.5f).toInt()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
    }
}