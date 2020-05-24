function getAbsXPATH(element) {
    if (element.nodeName == 'BODY') {
        return '/html/' + element.tagName.toLowerCase();
    }
    let ix = 1;//
    let siblings = element.parentNode.childNodes;//ͬ
    for (let i = 0, l = siblings.length; i < l; i++) {
        let sibling = siblings[i];
        if (sibling == element) {
            return this.getAbsXPATH(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + (ix) + ']';
        } else if (sibling.nodeType == 1 && sibling.tagName == element.tagName) {
            ix++;
        }
    }
}
window.onload=function(){
	var eles = document.getElementsByTagName('*');
	var oBtn = document.getElementById("btn");
	oBtn.onclick = function(){
		//alert(getAbsXPATH(this));
		testCall();
	}
	var oBtn1 = document.getElementById('btn1');
            oBtn1.onclick=function(){
                console.log('send Ajax');
                sendAjax();
    }
}
window.onclick = function(e){
	console.log(e.target.nodeName.toLowerCase());
	alert(getAbsXPATH(e.target));
	alert(e.target);
}
!function(e){
	e.testCall=function(){
		alert('test immediate call');
	}
}(this)
function sendAjax(){
            var oAjax=null;
            //1.创建Ajax对象
            if(window.XMLHttpRequest){
                var oAjax=new XMLHttpRequest();
            }
            else{
                var oAjax=new ActionXObject("Microsoft.XMLHTTP");
            }
            //2.连接服务器
            oAjax.open('GET',"file:////android_asset/img.png",true);
            //3.发送
            oAjax.send();
            //4.接收
            oAjax.onreadystatechange=function(){
                if(oAjax.readyState==4){
                    if(oAjax.status==200){
                        fuSucc(oAjax.responseText);
                    }
                    else{
                        if(fnFailed)
                            fnFailed();
                    }
                }
            };

            console.log("oAjax: "+oAjax);
}