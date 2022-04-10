package com.example.askpostactivity

import android.app.ActionBar
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.core.graphics.createBitmap
import androidx.core.view.drawToBitmap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable

import android.R.drawable
import android.graphics.Color.parseColor
import android.text.*
import org.w3c.dom.Text
import android.text.style.ClickableSpan

import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import android.widget.RelativeLayout
import android.webkit.WebSettings.PluginState


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

            containView.layout(0, 0, containView.measuredWidth, containView.measuredHeight)

            val bitmap = containView.drawToBitmap()
            val canvas = Canvas(bitmap)
            containView.draw(canvas)

            //그럼 이제 이 이미지 누르면 창 뜨고,
            //언어 고르면 edittext를 3개로 나누자.
            //그 그림 기준 앞 뒤로 잘라서
            //처음 edittext 그 앞으로 text 하고,
            //뒤에 edittext 는 그 뒤 내용으로 text 하기.
            //가운데 edittext 가 있고, 만약에 eidttext를 지운다고 하면 두 개의 edittext의 값을 받아와서 다 지운 후 다시 하나의 edittext로

            if (isCodeBlockClicked == 0) {

                var selectionCursor: Int = asdf.selectionStart
                asdf.getText().insert(selectionCursor, "╊")
                selectionCursor = asdf.selectionStart

                val builder = SpannableStringBuilder(asdf.getText())

                builder.setSpan(
                    //나중에 프레그먼트 처리할때 this 를 getactivity 로 바꾸기!
                    //특수문자 ╊ <- 절대 지우면 안됨 까먹음
                    ImageSpan(this,bitmap), selectionCursor - "╊".length, selectionCursor,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                builder.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        //매개 변수 : 코드 블록 이미지가 있는 현재 index 와, 코드 블록을 기준 앞 뒤 문자열
                        codeChioceDialog(selectionCursor,asdf.text.slice(0..selectionCursor-2).toString(),
                                         asdf.text.slice(selectionCursor..asdf.text.length-1).toString())

                        Log.d("index",selectionCursor.toString())
                    }
                },selectionCursor - "╊".length, selectionCursor, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                asdf.setText(builder)
                asdf.setMovementMethod(LinkMovementMethod.getInstance());
                Log.d("코드블록 전 문자",asdf.text.slice(0..selectionCursor-2).toString())
                Log.d("wesef",asdf.text.slice(selectionCursor..asdf.text.length-1).toString())
                Log.d("zxcv",selectionCursor.toString())

                asdf.setSelection(selectionCursor)

                isCodeBlockClicked = 1
            }
            else if (isCodeBlockClicked == 1){
                codeblock.setImageResource(R.drawable.codeblock)
                isCodeBlockClicked = 0
            }
        }
    }

    fun codeChioceDialog(codeBlockIindex : Int, textBehindCodeBlock : String , textFrontCodeBlock : String){

        var popupView = getLayoutInflater().inflate(R.layout.langchoice, null);
        var alertdialog = AlertDialog.Builder(this).create()

        var pythonLinear = popupView.findViewById<LinearLayout>(R.id.pythonlinear)
        var kotlinLinear = popupView.findViewById<LinearLayout>(R.id.kotlinlinear)
        var javaScriptLinear = popupView.findViewById<LinearLayout>(R.id.jslinear)
        var javaLinear = popupView.findViewById<LinearLayout>(R.id.Javalinear)
        var htmlLinear = popupView.findViewById<LinearLayout>(R.id.htmllinear)
        var cssLinear = popupView.findViewById<LinearLayout>(R.id.csslinear)
        var cLinear = popupView.findViewById<LinearLayout>(R.id.clinear)
        var cplusLinear = popupView.findViewById<LinearLayout>(R.id.cpluslinear)
        var swiftLinear = popupView.findViewById<LinearLayout>(R.id.swiftlinear)

        pythonLinear.setOnClickListener(){
            splitEditText(codeBlockIindex,textBehindCodeBlock,textFrontCodeBlock)
            alertdialog.hide()
        }

        alertdialog.setView(popupView)
        alertdialog.show()
        alertdialog.window!!.setLayout(800,1000)

    }


    fun splitEditText(codeBlockIindex : Int, textBehindCodeBlock : String , textFrontCodeBlock : String){

        var editTextScroll = findViewById<ScrollView>(R.id.postEditScrollView)
        editTextScroll.removeAllViews()

        var editTextLinearLayout = LinearLayout(this)

        var LinearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)

        var buttonLinearParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT )

        var editTextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        var codeBlockParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)

        var buttonparams = LinearLayout.LayoutParams(100,100,1.0f)

        buttonparams.setMargins(50,0,50,0)

        editTextLinearLayout.orientation = LinearLayout.VERTICAL
        editTextLinearLayout.layoutParams = LinearLayoutParams

        var buttonLinearlayout = LinearLayout(this)
        buttonLinearlayout.gravity = Gravity.CENTER
//        buttonLinearlayout.setBackgroundColor(getColor(R.color.black))
        buttonLinearlayout.layoutParams = buttonLinearParams
        buttonLinearlayout.orientation = LinearLayout.HORIZONTAL



        var editTextBehindCodeBlock = EditText(this)
        editTextBehindCodeBlock.setSingleLine(false)
        editTextBehindCodeBlock.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        editTextBehindCodeBlock.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                                            InputType.TYPE_CLASS_TEXT)
        editTextBehindCodeBlock.layoutParams = editTextParams
        editTextBehindCodeBlock.setBackgroundResource(android.R.color.transparent);

        var codeBlock = EditText(this)
        codeBlock.setBackgroundColor(getColor(R.color.gray));
        codeBlock.layoutParams = codeBlockParams
        codeBlock.hint = "코드를 적어주시고, 밑에 확인 버튼을 눌러주세요"
        codeBlock.setSingleLine(false)
        codeBlock.minHeight = 350
        codeBlock.gravity = Gravity.TOP
        codeBlock.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        codeBlock.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_CLASS_TEXT)

        var editTextFrontCodeBlock = EditText(this)
        editTextFrontCodeBlock.setSingleLine(false)
        editTextFrontCodeBlock.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        editTextFrontCodeBlock.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_CLASS_TEXT)
        editTextFrontCodeBlock.layoutParams = editTextParams
        editTextFrontCodeBlock.setBackgroundResource(android.R.color.transparent)
//        editTextFrontCodeBlock.setBackgroundColor(getColor(R.color.black))


        var conFormButton = Button(this)
        conFormButton.text = "확인"
        conFormButton.layoutParams = buttonparams

        conFormButton.setOnClickListener(){
            conFormCodeInsert(codeBlockIindex, textBehindCodeBlock , textFrontCodeBlock , codeBlock.text.toString())
        }


        var cancelButton = Button(this)
        cancelButton.text = "취소"
        cancelButton.layoutParams = buttonparams

        buttonLinearlayout.addView(conFormButton)
        buttonLinearlayout.addView(cancelButton)



        editTextBehindCodeBlock.hint = "제목"
        editTextBehindCodeBlock.setText(textBehindCodeBlock)
        editTextFrontCodeBlock.setText(textFrontCodeBlock)


        editTextLinearLayout.addView(editTextBehindCodeBlock)
        editTextLinearLayout.addView(codeBlock)
        editTextLinearLayout.addView(buttonLinearlayout)
        editTextLinearLayout.addView(editTextFrontCodeBlock)

        editTextScroll.addView(editTextLinearLayout)

    }

    fun conFormCodeInsert(codeBlockIindex : Int, textBehindCodeBlock : String , textFrontCodeBlock : String , code : String){

        var editTextScroll = findViewById<ScrollView>(R.id.postEditScrollView)
        editTextScroll.removeAllViews()

        var editTextLinearLayout = LinearLayout(this)

        var LinearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)

        var editTextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        var webViewParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        editTextLinearLayout.orientation = LinearLayout.VERTICAL
        editTextLinearLayout.layoutParams = LinearLayoutParams

        var editTextBehindCodeBlock = EditText(this)
        editTextBehindCodeBlock.setSingleLine(false)
        editTextBehindCodeBlock.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        editTextBehindCodeBlock.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_CLASS_TEXT)
        editTextBehindCodeBlock.layoutParams = editTextParams
        editTextBehindCodeBlock.setBackgroundResource(android.R.color.transparent)

        var codeWithHighLight = WebView(this)
        codeWithHighLight.layoutParams = webViewParams
        var weburl = "file:///android_asset/highlight.html"
        codeWithHighLight.settings.javaScriptEnabled = true
//        codeWithHighLight.loadUrl("javascript:insertCode('language-javascript',var asdf = asdf)");
//

        class WebBrideg(private val mContext: Context) {
            @JavascriptInterface
            fun showToast(code : String) {
                Toast.makeText(mContext, code, Toast.LENGTH_SHORT).show()
            }
        }
        codeWithHighLight.addJavascriptInterface(WebBrideg(this),"Kchoi")
        codeWithHighLight.loadUrl(weburl)







        var editTextFrontCodeBlock = EditText(this)
        editTextFrontCodeBlock.setSingleLine(false)
        editTextFrontCodeBlock.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        editTextFrontCodeBlock.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_CLASS_TEXT)
        editTextFrontCodeBlock.layoutParams = editTextParams
        editTextFrontCodeBlock.setBackgroundResource(android.R.color.transparent)

        editTextBehindCodeBlock.setText(textBehindCodeBlock)
        editTextFrontCodeBlock.setText(textFrontCodeBlock)

        editTextLinearLayout.addView(editTextBehindCodeBlock)
        editTextLinearLayout.addView(codeWithHighLight)
        editTextLinearLayout.addView(editTextFrontCodeBlock)

        editTextScroll.addView(editTextLinearLayout)

    }

    fun asdf(){
        setContentView(R.layout.webview)
        var webview = findViewById<WebView>(R.id.webview)
        webview.settings.javaScriptEnabled = true
        var weburl : String = "file:///android_asset/highlight.html"



        webview.loadUrl(weburl)


    }

}