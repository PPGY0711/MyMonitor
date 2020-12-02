//获取xpath
function readXPath(element) {
    if (element.id !== "") {//判断id属性，如果这个元素有id，则显 示//*[@id="xPath"]  形式内容
        return '//*[@id=\"' + element.id + '\"]';
    }
    //这里需要需要主要字符串转译问题，可参考js 动态生成html时字符串和变量转译（注意引号的作用）
    if (element == document.body) {//递归到body处，结束递归
        return '/html/' + element.tagName.toLowerCase();
    }
    var ix = 1,//在nodelist中的位置，且每次点击初始化
         siblings = element.parentNode.childNodes;//同级的子元素

    for (var i = 0, l = siblings.length; i < l; i++) {
        var sibling = siblings[i];
        //如果这个元素是siblings数组中的元素，则执行递归操作
        if (sibling == element) {
            return arguments.callee(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + (ix) + ']';
            //如果不符合，判断是否是element元素，并且是否是相同元素，如果是相同的就开始累加
        } else if (sibling.nodeType == 1 && sibling.tagName == element.tagName) {
            ix++;
        }
    }
};

(function () {
    if ( typeof window.CustomEvent === "function" ) return false;
    function CustomEvent ( event, params ) {
     params = params || { bubbles: false, cancelable: false, detail: undefined };
     var evt = document.createEvent( 'CustomEvent' );
     evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
     return evt;
    }
    CustomEvent.prototype = window.Event.prototype;
    window.CustomEvent = CustomEvent;
})();

(function () {
    // var times = 0;
    function ajaxEventTrigger(event) {
     var ajaxEvent = new CustomEvent(event, { detail: this });
//     console.log("call event!" + event);
    //  times++;
     window.dispatchEvent(ajaxEvent);
    }

    var oldXHR = window.XMLHttpRequest;

    function newXHR() {
        var realXHR = new oldXHR();
        realXHR.detailResponse = "";
        realXHR.detailResponseType = "";
        realXHR.detailResponseText = "";
        realXHR.detailResponseURL = "";
        realXHR.detail_ty_rum = new Object();
        realXHR.abort_time = 0;
        realXHR.isAbort = !1;
        realXHR.isTimeout = !1;
        realXHR.isError = !1;
        realXHR.error_time = 0;
        realXHR.req_time = 0;
        realXHR.res_time = 0;
        realXHR.firstbyte_time = 0;
        realXHR.lastbyte_time = 0;
        realXHR.cb_start_time = 0;
        realXHR.cb_end_time = 0;
        realXHR.load_time = 0;
        realXHR.process_change_time = new Array();
        realXHR.state_change_time = new Array();

        realXHR.addEventListener('abort', function () { ajaxEventTrigger.call(this, 'ajaxAbort'); }, false);

        realXHR.addEventListener('error', function () { ajaxEventTrigger.call(this, 'ajaxError'); }, false);

        realXHR.addEventListener('load', function () { ajaxEventTrigger.call(this, 'ajaxLoad'); }, false);

        realXHR.addEventListener('loadstart', function () { ajaxEventTrigger.call(this, 'ajaxLoadStart'); }, false);

        realXHR.addEventListener('progress', function () { ajaxEventTrigger.call(this, 'ajaxProgress'); }, false);

        realXHR.addEventListener('timeout', function () { ajaxEventTrigger.call(this, 'ajaxTimeout'); }, false);

        realXHR.addEventListener('loadend', function () { ajaxEventTrigger.call(this, 'ajaxLoadEnd'); }, false);

        realXHR.addEventListener('readystatechange', function() { ajaxEventTrigger.call(this, 'ajaxReadyStateChange'); }, false);

        return realXHR;
    }

    window.XMLHttpRequest = newXHR;

    window.addEventListener("ajaxAbort",function(e){
        e.detail.isAbort = !0;
        e.detail.abort_time = (new Date()).valueOf();
    });

    window.addEventListener("ajaxError",function(e){
        e.detail.isError = !0;
        e.detail.error_time =  (new Date()).valueOf();
    });

    window.addEventListener("ajaxLoad",function(e){
//         console.log("ajaxLoad");
        e.detail.load_time = (new Date()).valueOf();
    });

    window.addEventListener("ajaxLoadStart",function(e){
        e.detail.req_time = (new Date()).valueOf();
    });

    window.addEventListener("ajaxProgress",function(e){
        var i = e.detail.process_change_time.length + 1;
        e.detail.process_change_time.push({state: e.detail.readyState, status: e.detail.status?e.detail.status:0, time: (new Date()).valueOf()});
    });

    window.addEventListener("ajaxTimeout",function(e){
        e.detail.isTimeout = !0;
    });

    window.addEventListener("ajaxLoadEnd",function(e){
        e.detail.cb_end_time = (new Date()).valueOf();
        ajaxEventTrigger.call(e.detail,'ajaxRequestEnd');
    });

    window.addEventListener("ajaxReadyStateChange",function(e){
        var i = e.detail.state_change_time.length + 1;
        e.detail.state_change_time.push({state: e.detail.readyState, status: e.detail.status, time: (new Date()).valueOf()});
        if(e.detail.readyState === 3)
            e.detail.firstbyte_time = (new Date()).valueOf();
        if(e.detail.readyState === 4){
            e.detail.cb_start_time = e.detail.res_time = e.detail.lastbyte_time = (new Date()).valueOf();
            e.detail.detailResponse = e.detail.response;
            e.detail.detailResponseType = e.detail.responseType;
            e.detail.detailResponseText = e.detail.responseText;
            e.detail.detailResponseURL = e.detail.responseURL;
            e.detail.detail_ty_rum = e.detail._ty_rum;
        }
    });

    //监听Ajax事件
    window.addEventListener("ajaxRequestEnd",function(e){
//        console.log(e.detail);
        var ajaxData = {
            type : "WebViewMonitor_ajax",
			url	 : window.location.href,
			uri  : window.location.pathname,
			domain: window.location.hostname,
            payload : e.detail
        }
         sendAjaxData(ajaxData);
    });

     function sendAjaxData(e) {
         console.log(JSON.stringify(e));
         ajaxObj.sendAjaxInfo(JSON.stringify(e))
     };
})();

(function(e) {

    var intervalTime = 3000; //ms
    var hrefUrl = e.location.href;
    var hostname = e.location.hostname;
    var pathname = e.location.pathname;
    var host = e.location.host;
    var pageTime = (new Date).getTime();

    e.startWebViewMonitor = function() {

        // 已经开始执行了就返回
        if (e.monitorStarted) return !1;
        e.monitorStarted = !0;

        setTimeout(function() {
            var navigationTiming = {
                type: "WebViewMonitor_resourceTiming",
                payload: {
                    url: hrefUrl,
                    domain: hostname,
                    uri: pathname,
                    navigationTiming: performanceTiming.getNavigationTiming(),
                    resourceTiming: performanceTiming.getResourceTiming()
                }
            };
            sendResourceTiming(navigationTiming);
        }, 0);

        var getResourceTiming = function() {
            var timing = performanceTiming.getResourceTiming();
            if (timing.length > 0) {
                var resourceTiming = {
                    type: "WebViewMonitor_resourceTiming",
                    payload: {
                        url: hrefUrl,
                        domain: hostname,
                        uri: pathname,
                        navigationTiming: {},
                        resourceTiming: timing,
                    }
                };
                sendResourceTiming(resourceTiming);
            }
        };

        e.setInterval(getResourceTiming, intervalTime); //每隔3秒执行一次

        var already = !0;
        e.addEventListener("beforeunload",
            function() {
                already && (already = !1, getResourceTiming())
            }
        );
        e.addEventListener("unload",
            function() {
                already && (already = !1, getResourceTiming())
            }
        );
    };

    function sendResourceTiming(e) {
        console.log(JSON.stringify(e));
        loadingObj.sendLoadingInfo(JSON.stringify(e))
    };

    function sendClickData(e) {
        console.log(JSON.stringify(e));
        clickObj.sendClickInfo(JSON.stringify(e))
    };

    // 监听点击事件
    e.addCollectEvent = function(){
        // 已经开始执行了就返回
        if (e.monitorStarted2) return !1;
        e.monitorStarted2 = !0;

        for(var n = this.document.getElementsByTagName("a"), i = n.length, j = 0; j < i; j++){
            n[j].addEventListener("click",function(){
                var txt = this.innerText;
                var href = this.href;
                txt = txt.replace(/<.*[^>]*/g,"");
                var click_time = (new Date()).valueOf();
                var element = readXPath(this);
                var clickRecord = {
                    type : "WebViewMonitor_click",
					url	 : hrefUrl,
					uri  : pathname,
					domain: hostname,
                    payload : {
                        elementTagName: "a",
                        elementXPATH: element,
                        elementInnerText: txt,
                        elementHref: href,
                        clickTime: click_time
                    }
                }
                sendClickData(clickRecord);
            })
        }
        e.addEventListener("click", function(ev){
            var target = ev.target;
            var element = readXPath(target);
            var tagName = target.nodeName.toLowerCase();
            var innerHTML = ev.target.innerHTML;
            if(tagName!="a"){
                if("img" == tagName)
                    innerHTML = target.alt||"";
                else
                    innerHTML = innerHTML.replace(/<.*[^>]*/g,"");
            }
            var click_time = (new Date()).valueOf();
            var clickRecord = {
                type : "WebViewMonitor_click",
				url	 : hrefUrl,
				uri  : pathname,
				domain: hostname,
                payload : {
                    elementTagName : tagName,
                    elementXPATH: element,
                    elementInnerText: innerHTML,
                    elementHref: "",
                    clickTime: click_time
                }
            }
            sendClickData(clickRecord);
        });
    };

    function sendErrors() {
        var err = errorMonitor.getError();
        if (err.length > 0) {
            var errorInfo = {
                type: "WebViewMonitor_error",
                payload: {
                    url: hrefUrl,
                    domain: hostname,
                    uri: pathname,
                    error_list: err
                }
            };

            console.log(JSON.stringify(errorInfo));
            errorObj.sendErrorInfo(JSON.stringify(errorInfo))
        }
    };


    /**
     * 在这里每隔三秒去发送ajax和error错误信息
     */
    e.setInterval(
        function() {
            sendErrors();
        }, intervalTime
    );

    var performanceTiming = function() {
        function navigationTiming() {
            if (!e.performance || !e.performance.timing) return {};
            var time = e.performance.timing;
            return {
                navigationStart: time.navigationStart,
                redirectStart: time.redirectStart,
                redirectEnd: time.redirectEnd,
                fetchStart: time.fetchStart,
                domainLookupStart: time.domainLookupStart,
                domainLookupEnd: time.domainLookupEnd,
                connectStart: time.connectStart,
                secureConnectionStart: time.secureConnectionStart ? time.secureConnectionStart: time.connectEnd - time.secureConnectionStart,
                connectEnd: time.connectEnd,
                requestStart: time.requestStart,
                responseStart: time.responseStart,
                responseEnd: time.responseEnd,
                unloadEventStart: time.unloadEventStart,
                unloadEventEnd: time.unloadEventEnd,
                domLoading: time.domLoading,
                domInteractive: time.domInteractive,
                domContentLoadedEventStart: time.domContentLoadedEventStart,
                domContentLoadedEventEnd: time.domContentLoadedEventEnd,
                domComplete: time.domComplete,
                loadEventStart: time.loadEventStart,
                loadEventEnd: time.loadEventEnd,
                pageTime: pageTime
            }
        }
        function resourceTiming() {
            if (!e.performance || !e.performance.getEntriesByType) return [];
            for (var time = e.performance.getEntriesByType("resource"), resArr = [], i = 0; i < time.length; i++) {
                var t = time[i].secureConnectionStart ? time[i].secureConnectionStart: time[i].connectEnd - time[i].secureConnectionStart,
                    res = {
                        connectEnd: time[i].connectEnd,
                        connectStart: time[i].connectStart,
                        domainLookupEnd: time[i].domainLookupEnd,
                        domainLookupStart: time[i].domainLookupStart,
                        duration: time[i].duration,
                        entryType: time[i].entryType,
                        fetchStart: time[i].fetchStart,
                        initiatorType: time[i].initiatorType,
                        name: time[i].name,
                        redirectEnd: time[i].redirectEnd,
                        redirectStart: time[i].redirectStart,
                        requestStart: time[i].requestStart,
                        responseEnd: time[i].responseEnd,
                        responseStart: time[i].responseStart,
                        secureConnectionStart: t,
                        startTime: time[i].startTime
                    };
                resArr.push(res);
            }
            return resArr;
        }
        return {
            cacheResourceTimingLength: 0,
            getNavigationTiming: function() {
                return navigationTiming();
            },
            getResourceTiming: function() {
                var timing = resourceTiming();
                var len = timing.length;
                return timing.length != this.cacheResourceTimingLength ?
                    (timing = timing.slice(this.cacheResourceTimingLength, len), this.cacheResourceTimingLength = len, timing) : []
            }
        }
    }();

    var errorMonitor = function() {
        var errors = [];
        return e.addEventListener && e.addEventListener("error",
            function(e) {
                var eInfo = {};
                eInfo.time = e.timeStamp || (new Date).getTime(),
                    eInfo.url = e.filename,
                    eInfo.msg = e.message,
                    eInfo.line = e.lineno,
                    eInfo.column = e.colno,
                    e.error ? (eInfo.type = e.error.name, eInfo.stack = e.error.stack) :
                    (eInfo.msg.indexOf("Uncaught ") > -1 ?
                    eInfo.stack = eInfo.msg.split("Uncaught ")[1] + " at " + eInfo.url + ":" + eInfo.line + ":"
                    + eInfo.column: eInfo.stack = eInfo.msg + " at " + eInfo.url + ":" + eInfo.line + ":"
                     + eInfo.column, eInfo.type = eInfo.stack.slice(0, eInfo.stack.indexOf(":"))),
                eInfo.type.toLowerCase().indexOf("script error") > -1 && (eInfo.type = "ScriptError"),
                    errors.push(eInfo);
            }, !1), {
            getError: function() {
                return errors.splice(0, errors.length);
            }
        }
    }();

    console.log("Start WebView Monitor Here");
    e.startWebViewMonitor();
    e.addCollectEvent();
    console.log("Execute the functions");
}) (this);