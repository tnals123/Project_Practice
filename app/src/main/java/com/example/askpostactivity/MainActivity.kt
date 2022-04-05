package com.example.askpostactivity

import android.app.ActionBar
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.Editable
import androidx.core.graphics.createBitmap
import androidx.core.view.drawToBitmap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable
import android.text.Spannable

import android.R.drawable
import org.w3c.dom.Text
import android.text.style.ClickableSpan

import android.text.SpannableString
import android.text.method.LinkMovementMethod


//코드 블록 사진 생길때, 없애고 webview 할때, edittext 할때, 처음 코드 블록이 생성됐을 때 index를 받아서 거따가 insert, delete 하기!



class MainActivity : AppCompatActivity() {

    var isCodeBlockClicked = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.askpostfragmentlayout)

        var asdf = findViewById<EditText>(R.id.asdfasdf)
        var codeblock = findViewById<ImageView>(R.id.codeblock)
        var layoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var containView = layoutInflater.inflate(R.layout.codeblock,null)

        codeblock.setOnClickListener(){

            // inflater 을 bitmap 으로 바꾸는 코드
            var imageview = findViewById<ImageView>(R.id.imageview)
            var mysize = imageview.measuredWidth

            containView.measure(
                View.MeasureSpec.makeMeasureSpec(mysize, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY))
            Log.d("advasde",containView.measuredWidth.toString())
            containView.layout(0, 0, containView.measuredWidth, containView.measuredHeight)

            val bitmap = containView.drawToBitmap()
            val canvas = Canvas(bitmap)
            containView.draw(canvas)

            if (isCodeBlockClicked == 0) {

                var selectionCursor: Int = asdf.selectionStart
                asdf.getText().insert(selectionCursor, ".")
                selectionCursor = asdf.selectionStart

                val builder = SpannableStringBuilder(asdf.getText())

                builder.setSpan(
                    //나중에 프레그먼트 처리할때 this 를 getactivity 로 바꾸기!
                    ImageSpan(this,bitmap), selectionCursor - ".".length, selectionCursor,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                builder.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        Log.d("asdfasesvd","advsefsfsf")
                    }
                },selectionCursor - ".".length, selectionCursor, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                asdf.setText(builder)
                asdf.setMovementMethod(LinkMovementMethod.getInstance());
                Log.d("ASdfasdfasdfasdf",asdf.text[selectionCursor-1].toString())

                asdf.setSelection(selectionCursor)

                isCodeBlockClicked = 1
            }
            else if (isCodeBlockClicked == 1){
                codeblock.setImageResource(R.drawable.codeblock)
                isCodeBlockClicked = 0
            }
        }
    }
    var ss = SpannableString("Hello World")
    var span1: ClickableSpan = object : ClickableSpan() {

        override fun onClick(textView: View) {
            // do some thing
        }
    }

    var span2: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            // do another thing
        }
    }
}