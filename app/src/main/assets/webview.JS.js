<script src="http://code.jquery.com/jquery-latest.js"></script>


function changeBoldStyle() {
    var sel = window.getSelection();
    var str=document.getElementById("editing-code");
    Android.showToast(str.innerHTML.toString());
    var e = document.createElement('span');
    e.innerHTML = sel.toString();
    var mystr = str.innerHTML.replace(e.innerHTML,"『" + e.innerHTML + "』");
    str.innerHTML = "Asdfasdfasdfasdffdas";

    }

function changeItalicStyle() {
    var sel = window.getSelection();
    var str=document.getElementById("editing-code");
    if (sel.rangeCount) {
        var e = document.createElement('span');
        e.innerHTML = sel.toString();
        var mystr = str.innerHTML.replace(e.innerHTML,"「" + e.innerHTML + "」");
        str.innerHTML = mystr;
        }
    }


function update_mycode(){
    var asdf = 0;
    var textcound = 0 ;
    var checkTagRequired = true;
    var first = 0;
    var final = 0;

    var result_elem = document.getElementById("count1");
    var text = document.getElementById("editing-code");
    var lines = text.value.split("\n");
    var lines2 = text.value.split("‛");
    var resultString = "";

    for (var i = 0; i < lines.length; i++) {
         resultString += "<p>"+ lines[i] + "</p>";
    }

    for (var i = 0 ; i <resultString.length; i++){
        if (resultString[i] == "‛"){
            if (textcound == 0){
                    first = i;
                    textcound += 1;
                }
                else if (textcound == 1){
                    final = i;
                    textcound += 1;
                }
        }
        if (textcound == 2){

            var mytext = resultString.slice(first+1,final);
            let html = hljs.highlightAuto(mytext).value;

            html.replace(new RegExp("  ", "g"), "&nbsp; ");

            resultString = resultString.replace(resultString.slice(first,final+1),"<pre id = 'asdf' " + " onchange = " + '"'+ auto_grow2(document.getElementById("asdf")) + '"' + "style = width:300px;" + "><code style = border-radius:3px;"+"width:300px;"+"overflow:auto" + "background-color:#D3D3D3;>"  + html + "</code></pre>")
            // resultString += "<code style = border-radius:3px;"+ "background-color:#D3D3D3;>" + html + "</code>"
            textcound = 0;
        }

    }



