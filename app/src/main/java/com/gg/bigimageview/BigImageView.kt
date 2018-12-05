package com.gg.bigimageview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.log

/**
 *  Create by GG on 2018/12/5
 *  mail is gg.jin.yu@gmail.com
 */
class BigImageView : View {

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var decoder: BitmapRegionDecoder

    private val options = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    private val mRect = Rect()

    private var mHeight = 0
    private var mWidth = 0

    private var mMoveX = 0
    private var mMoveY = 0

    fun setInputStream(inputStream: InputStream) {
        try {
            decoder = BitmapRegionDecoder.newInstance(inputStream, false)
            val meaureOptions = BitmapFactory.Options()
            meaureOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, meaureOptions)
            mHeight = meaureOptions.outHeight
            mWidth = meaureOptions.outWidth
            Log.w("mHeight", "-----" + mHeight)
            Log.w("mWidth", "-----" + mWidth)
            Log.w("height", "-----" + height)
            Log.w("Width", "-----" + width)


            requestLayout()
            invalidate()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setImage(bitmap: Bitmap) {
        setInputStream(bitmap2InputStream(bitmap))
    }

    // 将Bitmap转换成InputStream
    private fun bitmap2InputStream(bm: Bitmap): InputStream {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return ByteArrayInputStream(baos.toByteArray())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mMoveX = event.x.toInt()
                mMoveY = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val currentX = event.x.toInt()
                val currentY = event.y.toInt()
                val moveX = currentX - mMoveX
                val moveY = currentY - mMoveY
                onMove(moveX, moveY)
                mMoveX = currentX
                mMoveY = currentY
//                Log.w("x", "-------------$mMoveX")
//                Log.w("y", "-------------$mMoveY")
            }
        }
        return true
    }

    private fun onMove(x: Int, y: Int) {
        if (mWidth > width) {
            mRect.offset(-x, 0)
            checkWidth()
            invalidate()
        }
        if (mHeight > height) {
            mRect.offset(0, -y)
            checkHeight()
            invalidate()
        }
    }

    private fun checkHeight() {
        val rect = mRect
        if (rect.bottom > mHeight) {
            rect.bottom = mHeight
            rect.top = mHeight - height
        }
        if (rect.top < 0) {
            rect.top = 0
            rect.bottom = height
        }
    }

    private fun checkWidth() {
        val rect = mRect
        if (rect.right > mWidth) {
            rect.right = mWidth
            rect.left = mWidth - width
        }
        if (rect.left < 0) {
            rect.left = 0
            rect.right = width
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureWidth = measuredWidth
        val measureHeight = measuredHeight
        mRect.left = 0
        mRect.top = 0
        mRect.bottom = measureHeight
        mRect.right = measureWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val bitmap = decoder.decodeRegion(mRect, options)
//        Log.w("mRect top", "----${mRect.top}")
//        Log.w("mRect right", "----${mRect.right}")
//        Log.w("mRect bottom", "----${mRect.bottom}")
//        Log.w("mRect left", "----${mRect.left}")
        canvas?.drawBitmap(bitmap, 0f, 0f, null)
    }
}