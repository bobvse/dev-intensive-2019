package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils
import kotlin.math.min

class CircleImageView(
    context: Context,
    attrs: AttributeSet? = null
) : ImageView(context, attrs){

    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = Utils.convertDpToPx(2)
    private var originalDrawable: Drawable? = null
    private var defaultAvatar: Bitmap? = null
    private var text: String? = null

    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            a.recycle()
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        Log.d("M_CircleImageView", "setImageDrawable")
        originalDrawable = drawable
        val img = getBitmapFromDrawable(drawable) ?: generateDefaultAvatar()
        super.setImageDrawable(getRoundedDrawable(img))
    }

    override fun setImageResource(resId: Int) {
        Log.d("M_CircleImageView", "setImageResource")
        originalDrawable = resources.getDrawable(resId, context.theme)
        val img = BitmapFactory.decodeResource(resources, resId)
        super.setImageDrawable(getRoundedDrawable(img))
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        Log.d("M_CircleImageView", "setImageBitmap")
        originalDrawable = BitmapDrawable(resources, bitmap)
        super.setImageDrawable(getRoundedDrawable(bitmap))
    }

    private fun getRoundedDrawable(bitmap: Bitmap?): Drawable =
        RoundedBitmapDrawableFactory.create(resources, bitmap).also { it.isCircular = true }

    override fun getDrawable(): Drawable {
        return originalDrawable ?: super.getDrawable()
    }

    fun generateAvatar(text: String?, size: Int) {
        if (originalDrawable == null || text != this.text) {
            defaultAvatar = if (text == null) generateDefaultAvatar()
            else generateTextAvatar(text, size)
        }
        this.text = text
        invalidate()
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

    fun getBorderWidth(): Int = Utils.convertPxToDp(borderWidth)

    fun setBorderWidth(dp: Int) {
        borderWidth = Utils.convertDpToPx(dp)
        this.invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = Resources.getSystem().getColor(colorId, context.theme)
        this.invalidate()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?) =
        when (drawable) {
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

    override fun onDraw(canvas: Canvas?) {
        if (originalDrawable != null)
            super.onDraw(canvas)
        else {
            if (defaultAvatar == null) {
                defaultAvatar = generateDefaultAvatar()
                canvas?.drawBitmap(defaultAvatar!!, 0f, 0f, null)
            }
            else
                canvas?.drawBitmap(defaultAvatar!!, 0f, 0f, null)
        }

        val radius = width / 2f
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeWidth = borderWidth.toFloat()
        paintBorder.color = borderColor

        canvas?.drawCircle(radius, radius, radius - borderWidth / 2f, paintBorder)
    }
}