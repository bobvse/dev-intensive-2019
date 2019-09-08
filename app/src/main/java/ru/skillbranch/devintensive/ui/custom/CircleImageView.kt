package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr){

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = Utils.convertDpToPx(2)

    private var sourceDrawable: Drawable? = null

    private var defaultAvatar: Bitmap? = null
    private var text: String? = null
    private var lastTextSize: Int = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also { it.color = Color.LTGRAY }

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            a.recycle()
        }
    }

    fun getBorderWidth(): Int = Utils.convertPxToDp(borderWidth)

    fun setBorderWidth(dp: Int) {
        borderWidth = Utils.convertDpToPx(dp)
        updateBorder()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        updateBorder()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

    fun getSourceDrawable(): Drawable? {
        return sourceDrawable
    }

    override fun setImageDrawable(drawable: Drawable?) {
        sourceDrawable = drawable
        val img = getBitmapFromDrawable(drawable) ?: generateDefaultAvatar()
        super.setImageDrawable(getRoundedDrawable(img))
    }

    override fun setImageResource(resId: Int) {
        sourceDrawable = resources.getDrawable(resId, context.theme)
        val img = BitmapFactory.decodeResource(resources, resId)
        super.setImageDrawable(getRoundedDrawable(img))
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        sourceDrawable = BitmapDrawable(resources, bitmap)
        super.setImageDrawable(getRoundedDrawable(bitmap))
    }

    private fun getRoundedDrawable(bitmap: Bitmap?): Drawable =
        RoundedBitmapDrawableFactory.create(resources, drawBorder(bitmap)).also { it.isCircular = true }

    private fun drawBorder(bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        val radius = bitmap.width / 2f
        val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = borderWidth.toFloat()
        paintBorder.color = borderColor
        val bitmapWithBorder = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmapWithBorder)
        canvas.drawCircle(radius, radius, radius - borderWidth / 2f, paintBorder)
        return bitmapWithBorder
    }

    fun generateAvatar(text: String?, size: Int = Utils.convertSpToPx(48)) {
        if (sourceDrawable == null || text != this.text || size != this.lastTextSize) {
            defaultAvatar = if (text == null) generateDefaultAvatar()
                else generateTextAvatar(text, size)
            this.text = text
            this.lastTextSize = size
            if (sourceDrawable == null)
                super.setImageDrawable(BitmapDrawable(resources, drawBorder(defaultAvatar)))
        }
    }

    private fun generateDefaultAvatar(): Bitmap {
        val bitmap = Bitmap.createBitmap(layoutParams.width, layoutParams.height, Bitmap.Config.ARGB_8888)
        val color = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, color, true)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.style = Paint.Style.FILL
            it.color = color.data
        }
        val radius = layoutParams.width / 2f
        val canvas = Canvas(bitmap)
        canvas.drawCircle(radius, radius, radius, paint)
        return bitmap
    }

    private fun generateTextAvatar(text: String, size: Int): Bitmap {
        val image = generateDefaultAvatar()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
            it.textSize = size.toFloat()
            it.color = DEFAULT_BORDER_COLOR
            it.textAlign = Paint.Align.CENTER
        }
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, layoutParams.width.toFloat(), layoutParams.height.toFloat())

        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
        val canvas = Canvas(image)
        canvas.drawText(text, backgroundBounds.centerX(), textBottom, paint)
        return image
    }

    private fun updateBorder() {
        if (sourceDrawable != null)
            setImageDrawable(sourceDrawable)
        else
            super.setImageDrawable(BitmapDrawable(resources, drawBorder(defaultAvatar)))
    }

    private fun getBitmapFromDrawable(drawable: Drawable?) = when (drawable) {
        null -> null
        is BitmapDrawable -> drawable.bitmap
        else -> try {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}