function ajax(url,fnSucc,fnFailed){
    var oAjax = null;
    //1.创建Ajax对象
    if(window.XMLHttpRequest){
        oAjax=new XMLHttpRequest();
    }
    else{
        oAjax=new ActionXObject("Microsoft.XMLHTTP");
    }
    //2.连接服务器
    oAjax.open('GET',url,true);
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
}
