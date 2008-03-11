(function(){var $wnd = window;var $doc = $wnd.document;var $moduleName, $moduleBase;var _,cF='com.google.gwt.core.client.',dF='com.google.gwt.http.client.',eF='com.google.gwt.i18n.client.',fF='com.google.gwt.lang.',gF='com.google.gwt.user.client.',hF='com.google.gwt.user.client.impl.',iF='com.google.gwt.user.client.ui.',jF='com.google.gwt.user.client.ui.impl.',kF='com.google.gwt.xml.client.',lF='com.google.gwt.xml.client.impl.',mF='java.io.',nF='java.lang.',oF='java.util.',pF='org.wyona.security.gwt.accesspolicyeditor.client.',qF='org.wyona.yanel.gwt.client.';function cC(){}
function Bu(a){return this===a;}
function Cu(){return cw(this);}
function Du(){return this.tN+'@'+this.hC();}
function zu(){}
_=zu.prototype={};_.eQ=Bu;_.hC=Cu;_.tS=Du;_.toString=function(){return this.tS();};_.tN=nF+'Object';_.tI=1;function w(a){return a==null?null:a.tN;}
var y=null;function B(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function C(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function D(){return ++E;}
var E=0;function ew(b,a){b.b=a;return b;}
function gw(b,a){if(b.a!==null){throw fu(new eu(),"Can't overwrite cause");}if(a===b){throw cu(new bu(),'Self-causation not permitted');}b.a=a;return b;}
function hw(a){iw(a,(aw(),bw));}
function iw(e,d){var a,b,c;c=dv(new cv());b=e;while(b!==null){a=b.b;if(b!==e){gv(c,'Caused by: ');}gv(c,b.tN);gv(c,': ');gv(c,a===null?'(No exception detail)':a);gv(c,'\n');b=b.a;}}
function jw(){var a,b;a=w(this);b=this.b;if(b!==null){return a+': '+b;}else{return a;}}
function dw(){}
_=dw.prototype=new zu();_.tS=jw;_.tN=nF+'Throwable';_.tI=3;_.a=null;_.b=null;function Ft(b,a){ew(b,a);return b;}
function Et(){}
_=Et.prototype=new dw();_.tN=nF+'Exception';_.tI=4;function Fu(b,a){Ft(b,a);return b;}
function Eu(){}
_=Eu.prototype=new Et();_.tN=nF+'RuntimeException';_.tI=5;function ab(c,b,a){Fu(c,'JavaScript '+b+' exception: '+a);return c;}
function F(){}
_=F.prototype=new Eu();_.tN=cF+'JavaScriptException';_.tI=6;function eb(b,a){if(!ee(a,2)){return false;}return jb(b,de(a,2));}
function fb(a){return B(a);}
function gb(){return [];}
function hb(){return function(){};}
function ib(){return {};}
function kb(a){return eb(this,a);}
function jb(a,b){return a===b;}
function lb(){return fb(this);}
function nb(){return mb(this);}
function mb(a){if(a.toString)return a.toString();return '[object]';}
function cb(){}
_=cb.prototype=new zu();_.eQ=kb;_.hC=lb;_.tS=nb;_.tN=cF+'JavaScriptObject';_.tI=7;function rc(b,d,c,a){if(d===null){throw new su();}if(a===null){throw new su();}if(c<0){throw new bu();}b.a=c;b.c=d;if(c>0){b.b=vb(new ub(),b,a);pg(b.b,c);}else{b.b=null;}return b;}
function tc(a){var b;if(a.c!==null){b=a.c;a.c=null;dd(b);sc(a);}}
function sc(a){if(a.b!==null){lg(a.b);}}
function vc(e,a){var b,c,d,f;if(e.c===null){return;}sc(e);f=e.c;e.c=null;b=ed(f);if(b!==null){c=Fu(new Eu(),b);a.kb(e,c);}else{d=yc(f);a.mb(e,d);}}
function wc(b,a){if(b.c===null){return;}tc(b);a.kb(b,oc(new nc(),b,b.a));}
function xc(b){var a;if(b.c===null){return false;}a=fd(b.c);switch(a){case 1:case 2:case 3:return true;}return false;}
function yc(b){var a;a=qb(new pb(),b);return a;}
function zc(a){var b;b=y;{vc(this,a);}}
function ob(){}
_=ob.prototype=new zu();_.t=zc;_.tN=dF+'Request';_.tI=0;_.a=0;_.b=null;_.c=null;function Ac(){}
_=Ac.prototype=new zu();_.tN=dF+'Response';_.tI=0;function qb(a,b){a.a=b;return a;}
function sb(a){return hd(a.a);}
function tb(a){return gd(a.a);}
function pb(){}
_=pb.prototype=new Ac();_.tN=dF+'Request$1';_.tI=0;function mg(){mg=cC;wg=ty(new ry());{vg();}}
function kg(a){mg();return a;}
function lg(a){if(a.d){qg(a.e);}else{rg(a.e);}Dy(wg,a);}
function ng(a){if(!a.d){Dy(wg,a);}a.ub();}
function pg(b,a){if(a<=0){throw cu(new bu(),'must be positive');}lg(b);b.d=false;b.e=tg(b,a);vy(wg,b);}
function og(b,a){if(a<=0){throw cu(new bu(),'must be positive');}lg(b);b.d=true;b.e=sg(b,a);vy(wg,b);}
function qg(a){mg();$wnd.clearInterval(a);}
function rg(a){mg();$wnd.clearTimeout(a);}
function sg(b,a){mg();return $wnd.setInterval(function(){b.u();},a);}
function tg(b,a){mg();return $wnd.setTimeout(function(){b.u();},a);}
function ug(){var a;a=y;{ng(this);}}
function vg(){mg();Ag(new gg());}
function fg(){}
_=fg.prototype=new zu();_.u=ug;_.tN=gF+'Timer';_.tI=8;_.d=false;_.e=0;var wg;function wb(){wb=cC;mg();}
function vb(b,a,c){wb();b.a=a;b.b=c;kg(b);return b;}
function xb(){wc(this.a,this.b);}
function ub(){}
_=ub.prototype=new fg();_.ub=xb;_.tN=dF+'Request$2';_.tI=9;function Fb(){Fb=cC;dc=Ab(new zb(),'GET');ec=Ab(new zb(),'POST');fc=ni(new mi());}
function Db(b,a,c){Fb();Eb(b,a===null?null:a.a,c);return b;}
function Eb(b,a,c){Fb();Ec('httpMethod',a);Ec('url',c);b.b=a;b.d=c;return b;}
function ac(g,d,a){var b,c,e,f,h;h=pi(fc);{b=id(h,g.b,g.d,true);}if(b!==null){e=lc(new kc(),g.d);gw(e,ic(new hc(),b));throw e;}cc(g,h);c=rc(new ob(),h,g.c,a);f=jd(h,c,d,a);if(f!==null){throw ic(new hc(),f);}return c;}
function bc(b,a,c){Ec('header',a);Ec('value',c);if(b.a===null){b.a=hA(new lz());}qA(b.a,a,c);}
function cc(e,f){var a,b,c,d;if(e.a!==null&&e.a.c>0){a=nA(e.a);d=cA(a);while(Az(d)){c=Bz(d);b=kd(f,de(c.A(),1),de(c.C(),1));if(b!==null){throw ic(new hc(),b);}}}else{kd(f,'Content-Type','text/plain; charset=utf-8');}}
function yb(){}
_=yb.prototype=new zu();_.tN=dF+'RequestBuilder';_.tI=0;_.a=null;_.b=null;_.c=0;_.d=null;var dc,ec,fc;function Ab(b,a){b.a=a;return b;}
function Cb(){return this.a;}
function zb(){}
_=zb.prototype=new zu();_.tS=Cb;_.tN=dF+'RequestBuilder$Method';_.tI=0;_.a=null;function ic(b,a){Ft(b,a);return b;}
function hc(){}
_=hc.prototype=new Et();_.tN=dF+'RequestException';_.tI=10;function lc(a,b){ic(a,'The URL '+b+' is invalid or violates the same-origin security restriction');return a;}
function kc(){}
_=kc.prototype=new hc();_.tN=dF+'RequestPermissionException';_.tI=11;function oc(b,a,c){ic(b,qc(c));return b;}
function qc(a){return 'A request timeout has expired after '+mu(a)+' ms';}
function nc(){}
_=nc.prototype=new hc();_.tN=dF+'RequestTimeoutException';_.tI=12;function Ec(a,b){Fc(a,b);if(0==qv(wv(b))){throw cu(new bu(),a+' can not be empty');}}
function Fc(a,b){if(null===b){throw tu(new su(),a+' can not be null');}}
function dd(a){a.onreadystatechange=ri;a.abort();}
function ed(b){try{if(b.status===undefined){return 'XmlHttpRequest.status == undefined, please see Safari bug '+'http://bugs.webkit.org/show_bug.cgi?id=3810 for more details';}return null;}catch(a){return 'Unable to read XmlHttpRequest.status; likely causes are a '+'networking error or bad cross-domain request. Please see '+'https://bugzilla.mozilla.org/show_bug.cgi?id=238559 for more '+'details';}}
function fd(a){return a.readyState;}
function gd(a){return a.responseText;}
function hd(a){return a.status;}
function id(e,c,d,b){try{e.open(c,d,b);return null;}catch(a){return a.message||a.toString();}}
function jd(e,c,d,b){e.onreadystatechange=function(){if(e.readyState==cd){e.onreadystatechange=ri;c.t(b);}};try{e.send(d);return null;}catch(a){e.onreadystatechange=ri;return a.message||a.toString();}}
function kd(d,b,c){try{d.setRequestHeader(b,c);return null;}catch(a){return a.message||a.toString();}}
var cd=4;function pd(){pd=cC;sd=hA(new lz());}
function md(b,a){pd();if(a===null||ov('',a)){throw cu(new bu(),'Cannot create a Dictionary with a null or empty name');}b.b='Dictionary '+a;od(b,a);if(b.a===null){throw pB(new oB(),"Cannot find JavaScript object with the name '"+a+"'",a,null);}return b;}
function nd(b,a){for(x in b.a){a.n(x);}}
function od(c,b){try{if(typeof $wnd[b]!='object'){ud(b);}c.a=$wnd[b];}catch(a){ud(b);}}
function qd(b,a){var c=b.a[a];if(c==null|| !Object.prototype.hasOwnProperty.call(b.a,a)){b.tb(a);}return String(c);}
function rd(b){var a;a=dB(new cB());nd(b,a);return a;}
function td(a){pd();var b;b=de(oA(sd,a),3);if(b===null){b=md(new ld(),a);qA(sd,a,b);}return b;}
function vd(b){var a,c;c=rd(this);a="Cannot find '"+b+"' in "+this;if(c.a.c<20){a+='\n keys found: '+c;}throw pB(new oB(),a,this.b,b);}
function ud(a){pd();throw pB(new oB(),"'"+a+"' is not a JavaScript object and cannot be used as a Dictionary",null,a);}
function wd(){return this.b;}
function ld(){}
_=ld.prototype=new zu();_.tb=vd;_.tS=wd;_.tN=eF+'Dictionary';_.tI=13;_.a=null;_.b=null;var sd;function yd(c,a,d,b,e){c.a=a;c.b=b;c.tN=e;c.tI=d;return c;}
function Ad(a,b,c){return a[b]=c;}
function Bd(b,a){return b[a];}
function Cd(a){return a.length;}
function Ed(e,d,c,b,a){return Dd(e,d,c,b,0,Cd(b),a);}
function Dd(j,i,g,c,e,a,b){var d,f,h;if((f=Bd(c,e))<0){throw new qu();}h=yd(new xd(),f,Bd(i,e),Bd(g,e),j);++e;if(e<a){j=uv(j,1);for(d=0;d<f;++d){Ad(h,d,Dd(j,i,g,c,e,a,b));}}else{for(d=0;d<f;++d){Ad(h,d,b);}}return h;}
function Fd(a,b,c){if(c!==null&&a.b!=0&& !ee(c,a.b)){throw new ot();}return Ad(a,b,c);}
function xd(){}
_=xd.prototype=new zu();_.tN=fF+'Array';_.tI=0;function ce(b,a){return !(!(b&&he[b][a]));}
function de(b,a){if(b!=null)ce(b.tI,a)||ge();return b;}
function ee(b,a){return b!=null&&ce(b.tI,a);}
function ge(){throw new At();}
function fe(a){if(a!==null){throw new At();}return a;}
function ie(b,d){_=d.prototype;if(b&& !(b.tI>=_.tI)){var c=b.toString;for(var a in _){b[a]=_[a];}b.toString=c;}return b;}
var he;function le(a){if(ee(a,4)){return a;}return ab(new F(),ne(a),me(a));}
function me(a){return a.message;}
function ne(a){return a.name;}
function pe(){pe=cC;pf=ty(new ry());{kf=new hh();mh(kf);}}
function qe(b,a){pe();xh(kf,b,a);}
function re(a,b){pe();return kh(kf,a,b);}
function se(){pe();return zh(kf,'button');}
function te(){pe();return zh(kf,'div');}
function ue(){pe();return Ah(kf,'checkbox');}
function ve(){pe();return Ah(kf,'text');}
function we(){pe();return zh(kf,'label');}
function xe(a){pe();return Bh(kf,a);}
function ye(){pe();return zh(kf,'span');}
function ze(){pe();return zh(kf,'tbody');}
function Ae(){pe();return zh(kf,'td');}
function Be(){pe();return zh(kf,'tr');}
function Ce(){pe();return zh(kf,'table');}
function Fe(b,a,d){pe();var c;c=y;{Ee(b,a,d);}}
function Ee(b,a,c){pe();var d;if(a===of){if(bf(b)==8192){of=null;}}d=De;De=b;try{c.hb(b);}finally{De=d;}}
function af(b,a){pe();Ch(kf,b,a);}
function bf(a){pe();return Dh(kf,a);}
function cf(a){pe();sh(kf,a);}
function df(a){pe();return th(kf,a);}
function gf(a,b){pe();return ai(kf,a,b);}
function ef(a,b){pe();return Eh(kf,a,b);}
function ff(a,b){pe();return Fh(kf,a,b);}
function hf(a){pe();return bi(kf,a);}
function jf(a){pe();return uh(kf,a);}
function lf(c,b,d,a){pe();ci(kf,c,b,d,a);}
function mf(a){pe();var b,c;c=true;if(pf.b>0){b=fe(zy(pf,pf.b-1));if(!(c=null.Ab())){af(a,true);cf(a);}}return c;}
function nf(b,a){pe();di(kf,b,a);}
function sf(a,b,c){pe();gi(kf,a,b,c);}
function qf(a,b,c){pe();ei(kf,a,b,c);}
function rf(a,b,c){pe();fi(kf,a,b,c);}
function tf(a,b){pe();hi(kf,a,b);}
function uf(a,b){pe();ii(kf,a,b);}
function vf(a,b){pe();ji(kf,a,b);}
function wf(b,c,a){pe();ki(kf,b,c,a);}
function xf(b,a,c){pe();li(kf,b,a,c);}
function yf(a,b){pe();oh(kf,a,b);}
function zf(a){pe();return ph(kf,a);}
var De=null,kf=null,of=null,pf;function Cf(a){if(ee(a,5)){return re(this,de(a,5));}return eb(ie(this,Af),a);}
function Df(){return fb(ie(this,Af));}
function Ef(){return zf(this);}
function Af(){}
_=Af.prototype=new cb();_.eQ=Cf;_.hC=Df;_.tS=Ef;_.tN=gF+'Element';_.tI=14;function cg(a){return eb(ie(this,Ff),a);}
function dg(){return fb(ie(this,Ff));}
function eg(){return df(this);}
function Ff(){}
_=Ff.prototype=new cb();_.eQ=cg;_.hC=dg;_.tS=eg;_.tN=gF+'Event';_.tI=15;function ig(){while((mg(),wg).b>0){lg(de(zy((mg(),wg),0),6));}}
function jg(){return null;}
function gg(){}
_=gg.prototype=new zu();_.ob=ig;_.pb=jg;_.tN=gF+'Timer$1';_.tI=16;function zg(){zg=cC;Cg=ty(new ry());eh=ty(new ry());{ah();}}
function Ag(a){zg();vy(Cg,a);}
function Bg(a){zg();$wnd.alert(a);}
function Dg(){zg();var a,b;for(a=Cg.cb();a.F();){b=de(a.eb(),7);b.ob();}}
function Eg(){zg();var a,b,c,d;d=null;for(a=Cg.cb();a.F();){b=de(a.eb(),7);c=b.pb();{d=c;}}return d;}
function Fg(){zg();var a,b;for(a=eh.cb();a.F();){b=fe(a.eb());null.Ab();}}
function ah(){zg();__gwt_initHandlers(function(){dh();},function(){return ch();},function(){bh();$wnd.onresize=null;$wnd.onbeforeclose=null;$wnd.onclose=null;});}
function bh(){zg();var a;a=y;{Dg();}}
function ch(){zg();var a;a=y;{return Eg();}}
function dh(){zg();var a;a=y;{Fg();}}
var Cg,eh;function xh(c,b,a){b.appendChild(a);}
function zh(b,a){return $doc.createElement(a);}
function Ah(b,c){var a=$doc.createElement('INPUT');a.type=c;return a;}
function Bh(c,a){var b;b=zh(c,'select');if(a){ei(c,b,'multiple',true);}return b;}
function Ch(c,b,a){b.cancelBubble=a;}
function Dh(b,a){switch(a.type){case 'blur':return 4096;case 'change':return 1024;case 'click':return 1;case 'dblclick':return 2;case 'focus':return 2048;case 'keydown':return 128;case 'keypress':return 256;case 'keyup':return 512;case 'load':return 32768;case 'losecapture':return 8192;case 'mousedown':return 4;case 'mousemove':return 64;case 'mouseout':return 32;case 'mouseover':return 16;case 'mouseup':return 8;case 'scroll':return 16384;case 'error':return 65536;case 'mousewheel':return 131072;case 'DOMMouseScroll':return 131072;}}
function ai(d,a,b){var c=a[b];return c==null?null:String(c);}
function Eh(c,a,b){return !(!a[b]);}
function Fh(d,a,c){var b=parseInt(a[c]);if(!b){return 0;}return b;}
function bi(b,a){return a.__eventBits||0;}
function ci(e,d,b,f,a){var c=new Option(b,f);if(a== -1||a>d.options.length-1){d.add(c,null);}else{d.add(c,d.options[a]);}}
function di(c,b,a){b.removeChild(a);}
function gi(c,a,b,d){a[b]=d;}
function ei(c,a,b,d){a[b]=d;}
function fi(c,a,b,d){a[b]=d;}
function hi(c,a,b){a.__listener=b;}
function ii(c,a,b){if(!b){b='';}a.innerHTML=b;}
function ji(c,a,b){while(a.firstChild){a.removeChild(a.firstChild);}if(b!=null){a.appendChild($doc.createTextNode(b));}}
function ki(e,c,d,a){var b=c.options[a];b.text=d;}
function li(c,b,a,d){b.style[a]=d;}
function fh(){}
_=fh.prototype=new zu();_.tN=hF+'DOMImpl';_.tI=0;function sh(b,a){a.preventDefault();}
function th(b,a){return a.toString();}
function uh(c,a){var b=a.parentNode;if(b==null){return null;}if(b.nodeType!=1)b=null;return b||null;}
function vh(d){$wnd.__dispatchCapturedMouseEvent=function(b){if($wnd.__dispatchCapturedEvent(b)){var a=$wnd.__captureElem;if(a&&a.__listener){Fe(b,a,a.__listener);b.stopPropagation();}}};$wnd.__dispatchCapturedEvent=function(a){if(!mf(a)){a.stopPropagation();a.preventDefault();return false;}return true;};$wnd.addEventListener('click',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('dblclick',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousedown',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mouseup',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousemove',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousewheel',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('keydown',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keyup',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keypress',$wnd.__dispatchCapturedEvent,true);$wnd.__dispatchEvent=function(b){var c,a=this;while(a&& !(c=a.__listener))a=a.parentNode;if(a&&a.nodeType!=1)a=null;if(c)Fe(b,a,c);};$wnd.__captureElem=null;}
function wh(c,b,a){b.__eventBits=a;b.onclick=a&1?$wnd.__dispatchEvent:null;b.ondblclick=a&2?$wnd.__dispatchEvent:null;b.onmousedown=a&4?$wnd.__dispatchEvent:null;b.onmouseup=a&8?$wnd.__dispatchEvent:null;b.onmouseover=a&16?$wnd.__dispatchEvent:null;b.onmouseout=a&32?$wnd.__dispatchEvent:null;b.onmousemove=a&64?$wnd.__dispatchEvent:null;b.onkeydown=a&128?$wnd.__dispatchEvent:null;b.onkeypress=a&256?$wnd.__dispatchEvent:null;b.onkeyup=a&512?$wnd.__dispatchEvent:null;b.onchange=a&1024?$wnd.__dispatchEvent:null;b.onfocus=a&2048?$wnd.__dispatchEvent:null;b.onblur=a&4096?$wnd.__dispatchEvent:null;b.onlosecapture=a&8192?$wnd.__dispatchEvent:null;b.onscroll=a&16384?$wnd.__dispatchEvent:null;b.onload=a&32768?$wnd.__dispatchEvent:null;b.onerror=a&65536?$wnd.__dispatchEvent:null;b.onmousewheel=a&131072?$wnd.__dispatchEvent:null;}
function qh(){}
_=qh.prototype=new fh();_.tN=hF+'DOMImplStandard';_.tI=0;function kh(c,a,b){if(!a&& !b){return true;}else if(!a|| !b){return false;}return a.isSameNode(b);}
function mh(a){vh(a);lh(a);}
function lh(d){$wnd.addEventListener('mouseout',function(b){var a=$wnd.__captureElem;if(a&& !b.relatedTarget){if('html'==b.target.tagName.toLowerCase()){var c=$doc.createEvent('MouseEvents');c.initMouseEvent('mouseup',true,true,$wnd,0,b.screenX,b.screenY,b.clientX,b.clientY,b.ctrlKey,b.altKey,b.shiftKey,b.metaKey,b.button,null);a.dispatchEvent(c);}}},true);$wnd.addEventListener('DOMMouseScroll',$wnd.__dispatchCapturedMouseEvent,true);}
function oh(c,b,a){wh(c,b,a);nh(c,b,a);}
function nh(c,b,a){if(a&131072){b.addEventListener('DOMMouseScroll',$wnd.__dispatchEvent,false);}}
function ph(d,a){var b=a.cloneNode(true);var c=$doc.createElement('DIV');c.appendChild(b);outer=c.innerHTML;b.innerHTML='';return outer;}
function gh(){}
_=gh.prototype=new qh();_.tN=hF+'DOMImplMozilla';_.tI=0;function hh(){}
_=hh.prototype=new gh();_.tN=hF+'DOMImplMozillaOld';_.tI=0;function ni(a){ri=hb();return a;}
function pi(a){return qi(a);}
function qi(a){return new XMLHttpRequest();}
function mi(){}
_=mi.prototype=new zu();_.tN=hF+'HTTPRequestImpl';_.tI=0;var ri=null;function un(d,b,a){var c=b.parentNode;if(!c){return;}c.insertBefore(a,b);c.removeChild(b);}
function vn(b,a){if(b.k!==null){un(b,b.k,a);}b.k=a;}
function wn(b,a){zn(b.k,a);}
function xn(b,a){yf(b.y(),a|hf(b.y()));}
function yn(){return this.k;}
function zn(a,b){sf(a,'className',b);}
function An(){if(this.k===null){return '(null handle)';}return zf(this.k);}
function sn(){}
_=sn.prototype=new zu();_.y=yn;_.tS=An;_.tN=iF+'UIObject';_.tI=0;_.k=null;function wo(a){if(ee(a.j,10)){de(a.j,10).sb(a);}else if(a.j!==null){throw fu(new eu(),"This widget's parent does not implement HasWidgets");}}
function xo(b,a){if(b.ab()){tf(b.y(),null);}vn(b,a);if(b.ab()){tf(a,b);}}
function yo(c,b){var a;a=c.j;if(b===null){if(a!==null&&a.ab()){c.jb();}c.j=null;}else{if(a!==null){throw fu(new eu(),'Cannot set a new parent without first clearing the old parent');}c.j=b;if(b.ab()){c.gb();}}}
function zo(){}
function Ao(){}
function Bo(){return this.i;}
function Co(){if(this.ab()){throw fu(new eu(),"Should only call onAttach when the widget is detached from the browser's document");}this.i=true;tf(this.y(),this);this.q();this.lb();}
function Do(a){}
function Eo(){if(!this.ab()){throw fu(new eu(),"Should only call onDetach when the widget is attached to the browser's document");}try{this.nb();}finally{this.r();tf(this.y(),null);this.i=false;}}
function Fo(){}
function ap(){}
function bp(a){xo(this,a);}
function co(){}
_=co.prototype=new sn();_.q=zo;_.r=Ao;_.ab=Bo;_.gb=Co;_.hb=Do;_.jb=Eo;_.lb=Fo;_.nb=ap;_.vb=bp;_.tN=iF+'Widget';_.tI=17;_.i=false;_.j=null;function om(b,a){yo(a,b);}
function qm(b,a){yo(a,null);}
function rm(){var a,b;for(b=this.cb();io(b);){a=jo(b);a.gb();}}
function sm(){var a,b;for(b=this.cb();io(b);){a=jo(b);a.jb();}}
function tm(){}
function um(){}
function nm(){}
_=nm.prototype=new co();_.q=rm;_.r=sm;_.lb=tm;_.nb=um;_.tN=iF+'Panel';_.tI=18;function zj(a){a.f=no(new eo(),a);}
function Aj(a){zj(a);return a;}
function Bj(c,a,b){wo(a);oo(c.f,a);qe(b,a.y());om(c,a);}
function Dj(b,c){var a;if(c.j!==b){return false;}qm(b,c);a=c.y();nf(jf(a),a);uo(b.f,c);return true;}
function Ej(){return so(this.f);}
function Fj(a){return Dj(this,a);}
function yj(){}
_=yj.prototype=new nm();_.cb=Ej;_.sb=Fj;_.tN=iF+'ComplexPanel';_.tI=19;function ti(a){Aj(a);a.vb(te());xf(a.y(),'position','relative');xf(a.y(),'overflow','hidden');return a;}
function ui(a,b){Bj(a,b,a.y());}
function wi(a){xf(a,'left','');xf(a,'top','');xf(a,'position','');}
function xi(b){var a;a=Dj(this,b);if(a){wi(b.y());}return a;}
function si(){}
_=si.prototype=new yj();_.sb=xi;_.tN=iF+'AbsolutePanel';_.tI=20;function nk(){nk=cC;lp(),np;}
function mk(b,a){lp(),np;pk(b,a);return b;}
function ok(b,a){switch(bf(a)){case 1:if(b.c!==null){wj(b.c,b);}break;case 4096:case 2048:break;case 128:case 512:case 256:break;}}
function pk(b,a){xo(b,a);xn(b,7041);}
function qk(a){if(this.c===null){this.c=uj(new tj());}vy(this.c,a);}
function rk(a){ok(this,a);}
function sk(a){pk(this,a);}
function lk(){}
_=lk.prototype=new co();_.l=qk;_.hb=rk;_.vb=sk;_.tN=iF+'FocusWidget';_.tI=21;_.c=null;function Bi(){Bi=cC;lp(),np;}
function Ai(b,a){lp(),np;mk(b,a);return b;}
function Ci(a){uf(this.y(),a);}
function zi(){}
_=zi.prototype=new lk();_.wb=Ci;_.tN=iF+'ButtonBase';_.tI=22;function aj(){aj=cC;lp(),np;}
function Di(a){lp(),np;Ai(a,se());bj(a.y());wn(a,'gwt-Button');return a;}
function Ei(b,a){lp(),np;Di(b);b.wb(a);return b;}
function Fi(c,a,b){lp(),np;Ei(c,a);c.l(b);return c;}
function bj(b){aj();if(b.type=='submit'){try{b.setAttribute('type','button');}catch(a){}}}
function yi(){}
_=yi.prototype=new zi();_.tN=iF+'Button';_.tI=23;function dj(a){Aj(a);a.e=Ce();a.d=ze();qe(a.e,a.d);a.vb(a.e);return a;}
function fj(c,b,a){sf(b,'align',a.a);}
function gj(c,b,a){xf(b,'verticalAlign',a.a);}
function cj(){}
_=cj.prototype=new yj();_.tN=iF+'CellPanel';_.tI=24;_.d=null;_.e=null;function lj(){lj=cC;lp(),np;}
function ij(a){lp(),np;jj(a,ue());wn(a,'gwt-CheckBox');return a;}
function kj(b,a){lp(),np;ij(b);oj(b,a);return b;}
function jj(b,a){var c;lp(),np;Ai(b,ye());b.a=a;b.b=we();yf(b.a,hf(b.y()));yf(b.y(),0);qe(b.y(),b.a);qe(b.y(),b.b);c='check'+ ++sj;sf(b.a,'id',c);sf(b.b,'htmlFor',c);return b;}
function mj(b){var a;a=b.ab()?'checked':'defaultChecked';return ef(b.a,a);}
function nj(b,a){qf(b.a,'checked',a);qf(b.a,'defaultChecked',a);}
function oj(b,a){vf(b.b,a);}
function pj(){tf(this.a,this);}
function qj(){tf(this.a,null);nj(this,mj(this));}
function rj(a){uf(this.b,a);}
function hj(){}
_=hj.prototype=new zi();_.lb=pj;_.nb=qj;_.wb=rj;_.tN=iF+'CheckBox';_.tI=25;_.a=null;_.b=null;var sj=0;function ow(d,a,b){var c;while(a.F()){c=a.eb();if(b===null?c===null:b.eQ(c)){return a;}}return null;}
function qw(a){throw lw(new kw(),'add');}
function rw(b){var a;a=ow(this,this.cb(),b);return a!==null;}
function sw(){var a,b,c;c=dv(new cv());a=null;gv(c,'[');b=this.cb();while(b.F()){if(a!==null){gv(c,a);}else{a=', ';}gv(c,Ev(b.eb()));}gv(c,']');return kv(c);}
function nw(){}
_=nw.prototype=new zu();_.n=qw;_.p=rw;_.tS=sw;_.tN=oF+'AbstractCollection';_.tI=0;function Cw(b,a){throw iu(new hu(),'Index: '+a+', Size: '+b.b);}
function Dw(b,a){throw lw(new kw(),'add');}
function Ew(a){this.m(this.yb(),a);return true;}
function Fw(e){var a,b,c,d,f;if(e===this){return true;}if(!ee(e,20)){return false;}f=de(e,20);if(this.yb()!=f.yb()){return false;}c=this.cb();d=f.cb();while(c.F()){a=c.eb();b=d.eb();if(!(a===null?b===null:a.eQ(b))){return false;}}return true;}
function ax(){var a,b,c,d;c=1;a=31;b=this.cb();while(b.F()){d=b.eb();c=31*c+(d===null?0:d.hC());}return c;}
function bx(){return vw(new uw(),this);}
function cx(a){throw lw(new kw(),'remove');}
function tw(){}
_=tw.prototype=new nw();_.m=Dw;_.n=Ew;_.eQ=Fw;_.hC=ax;_.cb=bx;_.rb=cx;_.tN=oF+'AbstractList';_.tI=26;function sy(a){{wy(a);}}
function ty(a){sy(a);return a;}
function uy(c,a,b){if(a<0||a>c.b){Cw(c,a);}Ey(c.a,a,b);++c.b;}
function vy(b,a){hz(b.a,b.b++,a);return true;}
function wy(a){a.a=gb();a.b=0;}
function yy(b,a){return Ay(b,a)!=(-1);}
function zy(b,a){if(a<0||a>=b.b){Cw(b,a);}return dz(b.a,a);}
function Ay(b,a){return By(b,a,0);}
function By(c,b,a){if(a<0){Cw(c,a);}for(;a<c.b;++a){if(cz(b,dz(c.a,a))){return a;}}return (-1);}
function Cy(c,a){var b;b=zy(c,a);fz(c.a,a,1);--c.b;return b;}
function Dy(c,b){var a;a=Ay(c,b);if(a==(-1)){return false;}Cy(c,a);return true;}
function Fy(a,b){uy(this,a,b);}
function az(a){return vy(this,a);}
function Ey(a,b,c){a.splice(b,0,c);}
function bz(a){return yy(this,a);}
function cz(a,b){return a===b||a!==null&&a.eQ(b);}
function ez(a){return zy(this,a);}
function dz(a,b){return a[b];}
function gz(a){return Cy(this,a);}
function fz(a,c,b){a.splice(c,b);}
function hz(a,b,c){a[b]=c;}
function iz(){return this.b;}
function ry(){}
_=ry.prototype=new tw();_.m=Fy;_.n=az;_.p=bz;_.D=ez;_.rb=gz;_.yb=iz;_.tN=oF+'ArrayList';_.tI=27;_.a=null;_.b=0;function uj(a){ty(a);return a;}
function wj(d,c){var a,b;for(a=d.cb();a.F();){b=de(a.eb(),8);b.ib(c);}}
function tj(){}
_=tj.prototype=new ry();_.tN=iF+'ClickListenerCollection';_.tI=28;function ck(a,b){if(a.h!==null){throw fu(new eu(),'Composite.initWidget() may only be called once.');}wo(b);a.vb(b.y());a.h=b;yo(b,a);}
function dk(){if(this.h===null){throw fu(new eu(),'initWidget() was never called in '+w(this));}return this.k;}
function ek(){if(this.h!==null){return this.h.ab();}return false;}
function fk(){this.h.gb();this.lb();}
function gk(){try{this.nb();}finally{this.h.jb();}}
function ak(){}
_=ak.prototype=new co();_.y=dk;_.ab=ek;_.gb=fk;_.jb=gk;_.tN=iF+'Composite';_.tI=29;_.h=null;function ik(a){Aj(a);a.vb(te());return a;}
function jk(a,b){Bj(a,b,a.y());}
function hk(){}
_=hk.prototype=new yj();_.tN=iF+'FlowPanel';_.tI=30;function zk(){zk=cC;xk(new wk(),'center');Ak=xk(new wk(),'left');xk(new wk(),'right');}
var Ak;function xk(b,a){b.a=a;return b;}
function wk(){}
_=wk.prototype=new zu();_.tN=iF+'HasHorizontalAlignment$HorizontalAlignmentConstant';_.tI=0;_.a=null;function al(){al=cC;Ek(new Dk(),'bottom');bl=Ek(new Dk(),'middle');cl=Ek(new Dk(),'top');}
var bl,cl;function Ek(a,b){a.a=b;return a;}
function Dk(){}
_=Dk.prototype=new zu();_.tN=iF+'HasVerticalAlignment$VerticalAlignmentConstant';_.tI=0;_.a=null;function gl(a){a.a=(zk(),Ak);a.c=(al(),cl);}
function hl(a){dj(a);gl(a);a.b=Be();qe(a.d,a.b);sf(a.e,'cellSpacing','0');sf(a.e,'cellPadding','0');return a;}
function il(b,c){var a;a=kl(b);qe(b.b,a);Bj(b,c,a);}
function kl(b){var a;a=Ae();fj(b,a,b.a);gj(b,a,b.c);return a;}
function ll(b,a){b.c=a;}
function ml(c){var a,b;b=jf(c.y());a=Dj(this,c);if(a){nf(this.b,b);}return a;}
function fl(){}
_=fl.prototype=new cj();_.sb=ml;_.tN=iF+'HorizontalPanel';_.tI=31;_.b=null;function pl(a){a.vb(te());xn(a,131197);wn(a,'gwt-Label');return a;}
function ql(b,a){pl(b);sl(b,a);return b;}
function sl(b,a){vf(b.y(),a);}
function tl(a){switch(bf(a)){case 1:break;case 4:case 8:case 64:case 16:case 32:break;case 131072:break;}}
function ol(){}
_=ol.prototype=new co();_.hb=tl;_.tN=iF+'Label';_.tI=32;function bm(){bm=cC;lp(),np;lm=new vl();}
function Cl(b,a){bm();mk(b,xe(a));xn(b,1024);wn(b,'gwt-ListBox');return b;}
function Dl(b,a){gm(b,a,(-1));}
function El(b,a,c){hm(b,a,c,(-1));}
function Fl(b,a){if(a<0||a>=cm(b)){throw new hu();}}
function am(a){wl(lm,a.y());}
function cm(a){return yl(lm,a.y());}
function dm(b,a){Fl(b,a);return zl(lm,b.y(),a);}
function em(a){return ff(a.y(),'selectedIndex');}
function fm(b,a){Fl(b,a);return Al(lm,b.y(),a);}
function gm(c,b,a){hm(c,b,b,a);}
function hm(c,b,d,a){lf(c.y(),b,d,a);}
function im(b,a){Fl(b,a);Bl(lm,b.y(),a);}
function jm(c,a,b){Fl(c,a);if(b===null){throw tu(new su(),'Cannot set an option to have null text');}wf(c.y(),b,a);}
function km(a,b){rf(a.y(),'size',b);}
function mm(a){if(bf(a)==1024){}else{ok(this,a);}}
function ul(){}
_=ul.prototype=new lk();_.hb=mm;_.tN=iF+'ListBox';_.tI=33;var lm;function wl(b,a){a.options.length=0;}
function yl(b,a){return a.options.length;}
function zl(c,b,a){return b.options[a].text;}
function Al(c,b,a){return b.options[a].value;}
function Bl(c,b,a){b.options[a]=null;}
function vl(){}
_=vl.prototype=new zu();_.tN=iF+'ListBox$Impl';_.tI=0;function Bm(){Bm=cC;an=hA(new lz());}
function Am(b,a){Bm();ti(b);if(a===null){a=Cm();}b.vb(a);b.gb();return b;}
function Dm(){Bm();return Em(null);}
function Em(c){Bm();var a,b;b=de(oA(an,c),9);if(b!==null){return b;}a=null;if(an.c==0){Fm();}qA(an,c,b=Am(new vm(),a));return b;}
function Cm(){Bm();return $doc.body;}
function Fm(){Bm();Ag(new wm());}
function vm(){}
_=vm.prototype=new si();_.tN=iF+'RootPanel';_.tI=34;var an;function ym(){var a,b;for(b=wx(fy((Bm(),an)));Dx(b);){a=de(Ex(b),9);if(a.ab()){a.jb();}}}
function zm(){return null;}
function wm(){}
_=wm.prototype=new zu();_.ob=ym;_.pb=zm;_.tN=iF+'RootPanel$1';_.tI=35;function ln(){ln=cC;lp(),np;}
function kn(b,a){lp(),np;mk(b,a);xn(b,1024);return b;}
function mn(a){return gf(a.y(),'value');}
function nn(a){if(this.a===null){this.a=uj(new tj());}vy(this.a,a);}
function on(a){var b;ok(this,a);b=bf(a);if(b==1){if(this.a!==null){wj(this.a,this);}}else{}}
function jn(){}
_=jn.prototype=new lk();_.l=nn;_.hb=on;_.tN=iF+'TextBoxBase';_.tI=36;_.a=null;function qn(){qn=cC;lp(),np;}
function pn(a){lp(),np;kn(a,ve());wn(a,'gwt-TextBox');return a;}
function rn(b,a){rf(b.y(),'size',a);}
function hn(){}
_=hn.prototype=new jn();_.tN=iF+'TextBox';_.tI=37;function Cn(a){a.a=(zk(),Ak);a.b=(al(),cl);}
function Dn(a){dj(a);Cn(a);sf(a.e,'cellSpacing','0');sf(a.e,'cellPadding','0');return a;}
function En(b,d){var a,c;c=Be();a=ao(b);qe(c,a);qe(b.d,c);Bj(b,d,a);}
function ao(b){var a;a=Ae();fj(b,a,b.a);gj(b,a,b.b);return a;}
function bo(c){var a,b;b=jf(c.y());a=Dj(this,c);if(a){nf(this.d,jf(b));}return a;}
function Bn(){}
_=Bn.prototype=new cj();_.sb=bo;_.tN=iF+'VerticalPanel';_.tI=38;function no(b,a){b.b=a;b.a=Ed('[Lcom.google.gwt.user.client.ui.Widget;',[0],[12],[4],null);return b;}
function oo(a,b){ro(a,b,a.c);}
function qo(b,c){var a;for(a=0;a<b.c;++a){if(b.a[a]===c){return a;}}return (-1);}
function ro(d,e,a){var b,c;if(a<0||a>d.c){throw new hu();}if(d.c==d.a.a){c=Ed('[Lcom.google.gwt.user.client.ui.Widget;',[0],[12],[d.a.a*2],null);for(b=0;b<d.a.a;++b){Fd(c,b,d.a[b]);}d.a=c;}++d.c;for(b=d.c-1;b>a;--b){Fd(d.a,b,d.a[b-1]);}Fd(d.a,a,e);}
function so(a){return go(new fo(),a);}
function to(c,b){var a;if(b<0||b>=c.c){throw new hu();}--c.c;for(a=b;a<c.c;++a){Fd(c.a,a,c.a[a+1]);}Fd(c.a,c.c,null);}
function uo(b,c){var a;a=qo(b,c);if(a==(-1)){throw new rB();}to(b,a);}
function eo(){}
_=eo.prototype=new zu();_.tN=iF+'WidgetCollection';_.tI=0;_.a=null;_.b=null;_.c=0;function go(b,a){b.b=a;return b;}
function io(a){return a.a<a.b.c-1;}
function jo(a){if(a.a>=a.b.c){throw new rB();}return a.b.a[++a.a];}
function ko(){return io(this);}
function lo(){return jo(this);}
function mo(){if(this.a<0||this.a>=this.b.c){throw new eu();}this.b.b.sb(this.b.a[this.a--]);}
function fo(){}
_=fo.prototype=new zu();_.F=ko;_.eb=lo;_.qb=mo;_.tN=iF+'WidgetCollection$WidgetIterator';_.tI=0;_.a=(-1);function lp(){lp=cC;mp=fp(new dp());np=mp!==null?kp(new cp()):mp;}
function kp(a){lp();return a;}
function cp(){}
_=cp.prototype=new zu();_.tN=jF+'FocusImpl';_.tI=0;var mp,np;function gp(){gp=cC;lp();}
function ep(a){hp(a);ip(a);jp(a);}
function fp(a){gp();kp(a);ep(a);return a;}
function hp(b){return function(a){if(this.parentNode.onblur){this.parentNode.onblur(a);}};}
function ip(b){return function(a){if(this.parentNode.onfocus){this.parentNode.onfocus(a);}};}
function jp(a){return function(){this.firstChild.focus();};}
function dp(){}
_=dp.prototype=new cp();_.tN=jF+'FocusImplOld';_.tI=0;function tp(c,a,b){Fu(c,b);return c;}
function sp(){}
_=sp.prototype=new Eu();_.tN=kF+'DOMException';_.tI=39;function Ep(){Ep=cC;Fp=(vs(),ft);}
function aq(a){Ep();return ws(Fp,a);}
var Fp;function uq(b,a){b.a=a;return b;}
function vq(a,b){return b;}
function xq(a){if(ee(a,15)){return re(vq(this,this.a),vq(this,de(a,15).a));}return false;}
function tq(){}
_=tq.prototype=new zu();_.eQ=xq;_.tN=lF+'DOMItem';_.tI=40;_.a=null;function sr(b,a){uq(b,a);return b;}
function ur(a){return nr(new mr(),ys(a.a));}
function vr(a){return Br(new Ar(),zs(a.a));}
function wr(a){return Fs(a.a);}
function xr(a){return dt(a.a);}
function yr(a){return et(a.a);}
function zr(a){var b;if(a===null){return null;}b=at(a);switch(b){case 2:return cq(new bq(),a);case 4:return iq(new hq(),a);case 8:return qq(new pq(),a);case 11:return Dq(new Cq(),a);case 9:return br(new ar(),a);case 1:return gr(new fr(),a);case 7:return es(new ds(),a);case 3:return js(new is(),a);default:return sr(new rr(),a);}}
function rr(){}
_=rr.prototype=new tq();_.tN=lF+'NodeImpl';_.tI=41;function cq(b,a){sr(b,a);return b;}
function eq(a){return Es(a.a);}
function fq(a){return ct(a.a);}
function gq(){var a;a=dv(new cv());gv(a,' '+eq(this));gv(a,'="');gv(a,fq(this));gv(a,'"');return kv(a);}
function bq(){}
_=bq.prototype=new rr();_.tS=gq;_.tN=lF+'AttrImpl';_.tI=42;function mq(b,a){sr(b,a);return b;}
function oq(a){return As(a.a);}
function lq(){}
_=lq.prototype=new rr();_.tN=lF+'CharacterDataImpl';_.tI=43;function js(b,a){mq(b,a);return b;}
function ls(){var a,b,c;a=dv(new cv());c=sv(oq(this),'(?=[;&<>\'"])',(-1));for(b=0;b<c.a;b++){if(tv(c[b],';')){gv(a,'&semi;');gv(a,uv(c[b],1));}else if(tv(c[b],'&')){gv(a,'&amp;');gv(a,uv(c[b],1));}else if(tv(c[b],'"')){gv(a,'&quot;');gv(a,uv(c[b],1));}else if(tv(c[b],"'")){gv(a,'&apos;');gv(a,uv(c[b],1));}else if(tv(c[b],'<')){gv(a,'&lt;');gv(a,uv(c[b],1));}else if(tv(c[b],'>')){gv(a,'&gt;');gv(a,uv(c[b],1));}else{gv(a,c[b]);}}return kv(a);}
function is(){}
_=is.prototype=new lq();_.tS=ls;_.tN=lF+'TextImpl';_.tI=44;function iq(b,a){js(b,a);return b;}
function kq(){var a;a=ev(new cv(),'<![CDATA[');gv(a,oq(this));gv(a,']]>');return kv(a);}
function hq(){}
_=hq.prototype=new is();_.tS=kq;_.tN=lF+'CDATASectionImpl';_.tI=45;function qq(b,a){mq(b,a);return b;}
function sq(){var a;a=ev(new cv(),'<!--');gv(a,oq(this));gv(a,'-->');return kv(a);}
function pq(){}
_=pq.prototype=new lq();_.tS=sq;_.tN=lF+'CommentImpl';_.tI=46;function zq(c,a,b){tp(c,12,'Failed to parse: '+Bq(a));gw(c,b);return c;}
function Bq(a){return vv(a,0,pu(qv(a),128));}
function yq(){}
_=yq.prototype=new sp();_.tN=lF+'DOMParseException';_.tI=47;function Dq(b,a){sr(b,a);return b;}
function Fq(){var a,b;a=dv(new cv());for(b=0;b<vr(this).B();b++){fv(a,vr(this).bb(b));}return kv(a);}
function Cq(){}
_=Cq.prototype=new rr();_.tS=Fq;_.tN=lF+'DocumentFragmentImpl';_.tI=48;function br(b,a){sr(b,a);return b;}
function dr(){return de(zr(Bs(this.a)),16);}
function er(){var a,b,c;a=dv(new cv());b=vr(this);for(c=0;c<b.B();c++){gv(a,b.bb(c).tS());}return kv(a);}
function ar(){}
_=ar.prototype=new rr();_.w=dr;_.tS=er;_.tN=lF+'DocumentImpl';_.tI=49;function gr(b,a){sr(b,a);return b;}
function ir(a){return bt(a.a);}
function jr(a){return xs(this.a,a);}
function kr(a){return Br(new Ar(),Cs(this.a,a));}
function lr(){var a;a=ev(new cv(),'<');gv(a,ir(this));if(xr(this)){gv(a,Fr(ur(this)));}if(yr(this)){gv(a,'>');gv(a,Fr(vr(this)));gv(a,'<\/');gv(a,ir(this));gv(a,'>');}else{gv(a,'/>');}return kv(a);}
function fr(){}
_=fr.prototype=new rr();_.v=jr;_.z=kr;_.tS=lr;_.tN=lF+'ElementImpl';_.tI=50;function Br(b,a){uq(b,a);return b;}
function Dr(a){return Ds(a.a);}
function Er(b,a){return zr(gt(b.a,a));}
function Fr(c){var a,b;a=dv(new cv());for(b=0;b<c.B();b++){gv(a,c.bb(b).tS());}return kv(a);}
function as(){return Dr(this);}
function bs(a){return Er(this,a);}
function cs(){return Fr(this);}
function Ar(){}
_=Ar.prototype=new tq();_.B=as;_.bb=bs;_.tS=cs;_.tN=lF+'NodeListImpl';_.tI=51;function nr(b,a){Br(b,a);return b;}
function pr(){return Dr(this);}
function qr(a){return Er(this,a);}
function mr(){}
_=mr.prototype=new Ar();_.B=pr;_.bb=qr;_.tN=lF+'NamedNodeMapImpl';_.tI=52;function es(b,a){sr(b,a);return b;}
function gs(a){return As(a.a);}
function hs(){var a;a=ev(new cv(),'<?');gv(a,wr(this));gv(a,' ');gv(a,gs(this));gv(a,'?>');return kv(a);}
function ds(){}
_=ds.prototype=new rr();_.tS=hs;_.tN=lF+'ProcessingInstructionImpl';_.tI=53;function vs(){vs=cC;ft=ps(new ns());}
function us(a){vs();return a;}
function ws(e,c){var a,d;try{return de(zr(ss(e,c)),17);}catch(a){a=le(a);if(ee(a,18)){d=a;throw zq(new yq(),c,d);}else throw a;}}
function xs(b,a){vs();return b.getAttribute(a);}
function ys(a){vs();return a.attributes;}
function zs(b){vs();var a=b.childNodes;return a==null?null:a;}
function As(a){vs();return a.data;}
function Bs(a){vs();return a.documentElement;}
function Cs(a,b){vs();return rs(ft,a,b);}
function Ds(a){vs();return a.length;}
function Es(a){vs();return a.name;}
function Fs(a){vs();var b=a.nodeName;return b==null?null:b;}
function at(a){vs();var b=a.nodeType;return b==null?-1:b;}
function bt(a){vs();return a.tagName;}
function ct(a){vs();return a.value;}
function dt(a){vs();return a.attributes.length!=0;}
function et(a){vs();return a.hasChildNodes();}
function gt(c,a){vs();if(a>=c.length){return null;}var b=c.item(a);return b==null?null:b;}
function ms(){}
_=ms.prototype=new zu();_.tN=lF+'XMLParserImpl';_.tI=0;var ft;function qs(){qs=cC;vs();}
function os(a){a.a=ts();}
function ps(a){qs();us(a);os(a);return a;}
function rs(c,a,b){return a.getElementsByTagNameNS('*',b);}
function ss(e,a){var b=e.a;var c=b.parseFromString(a,'text/xml');var d=c.documentElement;if(d.tagName=='parsererror'&&d.namespaceURI=='http://www.mozilla.org/newlayout/xml/parsererror.xml'){throw new Error(d.firstChild.data);}return c;}
function ts(){qs();return new DOMParser();}
function ns(){}
_=ns.prototype=new ms();_.tN=lF+'XMLParserImplStandard';_.tI=0;function kt(){}
_=kt.prototype=new zu();_.tN=mF+'OutputStream';_.tI=0;function it(){}
_=it.prototype=new kt();_.tN=mF+'FilterOutputStream';_.tI=0;function mt(){}
_=mt.prototype=new it();_.tN=mF+'PrintStream';_.tI=0;function ot(){}
_=ot.prototype=new Eu();_.tN=nF+'ArrayStoreException';_.tI=54;function st(){st=cC;tt=rt(new qt(),false);ut=rt(new qt(),true);}
function rt(a,b){st();a.a=b;return a;}
function vt(a){return ee(a,19)&&de(a,19).a==this.a;}
function wt(){var a,b;b=1231;a=1237;return this.a?1231:1237;}
function xt(){return this.a?'true':'false';}
function yt(a){st();return a?ut:tt;}
function qt(){}
_=qt.prototype=new zu();_.eQ=vt;_.hC=wt;_.tS=xt;_.tN=nF+'Boolean';_.tI=55;_.a=false;var tt,ut;function At(){}
_=At.prototype=new Eu();_.tN=nF+'ClassCastException';_.tI=56;function cu(b,a){Fu(b,a);return b;}
function bu(){}
_=bu.prototype=new Eu();_.tN=nF+'IllegalArgumentException';_.tI=57;function fu(b,a){Fu(b,a);return b;}
function eu(){}
_=eu.prototype=new Eu();_.tN=nF+'IllegalStateException';_.tI=58;function iu(b,a){Fu(b,a);return b;}
function hu(){}
_=hu.prototype=new Eu();_.tN=nF+'IndexOutOfBoundsException';_.tI=59;function wu(){wu=cC;{yu();}}
function yu(){wu();xu=/^[+-]?\d*\.?\d*(e[+-]?\d+)?$/i;}
var xu=null;function lu(){lu=cC;wu();}
function mu(a){lu();return Dv(a);}
function pu(a,b){return a<b?a:b;}
function qu(){}
_=qu.prototype=new Eu();_.tN=nF+'NegativeArraySizeException';_.tI=60;function tu(b,a){Fu(b,a);return b;}
function su(){}
_=su.prototype=new Eu();_.tN=nF+'NullPointerException';_.tI=61;function ov(b,a){if(!ee(a,1))return false;return yv(b,a);}
function pv(b,a){return b.indexOf(a);}
function qv(a){return a.length;}
function rv(b,a){return sv(b,a,0);}
function sv(j,i,g){var a=new RegExp(i,'g');var h=[];var b=0;var k=j;var e=null;while(true){var f=a.exec(k);if(f==null||(k==''||b==g-1&&g>0)){h[b]=k;break;}else{h[b]=k.substring(0,f.index);k=k.substring(f.index+f[0].length,k.length);a.lastIndex=0;if(e==k){h[b]=k.substring(0,1);k=k.substring(1);}e=k;b++;}}if(g==0){for(var c=h.length-1;c>=0;c--){if(h[c]!=''){h.splice(c+1,h.length-(c+1));break;}}}var d=xv(h.length);var c=0;for(c=0;c<h.length;++c){d[c]=h[c];}return d;}
function tv(b,a){return pv(b,a)==0;}
function uv(b,a){return b.substr(a,b.length-a);}
function vv(c,a,b){return c.substr(a,b-a);}
function wv(c){var a=c.replace(/^(\s*)/,'');var b=a.replace(/\s*$/,'');return b;}
function xv(a){return Ed('[Ljava.lang.String;',[0],[1],[a],null);}
function yv(a,b){return String(a)==b;}
function zv(a){return ov(this,a);}
function Bv(){var a=Av;if(!a){a=Av={};}var e=':'+this;var b=a[e];if(b==null){b=0;var f=this.length;var d=f<64?1:f/32|0;for(var c=0;c<f;c+=d){b<<=1;b+=this.charCodeAt(c);}b|=0;a[e]=b;}return b;}
function Cv(){return this;}
function Dv(a){return ''+a;}
function Ev(a){return a!==null?a.tS():'null';}
_=String.prototype;_.eQ=zv;_.hC=Bv;_.tS=Cv;_.tN=nF+'String';_.tI=2;var Av=null;function dv(a){hv(a);return a;}
function ev(b,a){iv(b,a);return b;}
function fv(a,b){return gv(a,Ev(b));}
function gv(c,d){if(d===null){d='null';}var a=c.js.length-1;var b=c.js[a].length;if(c.length>b*b){c.js[a]=c.js[a]+d;}else{c.js.push(d);}c.length+=d.length;return c;}
function hv(a){iv(a,'');}
function iv(b,a){b.js=[a];b.length=a.length;}
function kv(a){a.fb();return a.js[0];}
function lv(){if(this.js.length>1){this.js=[this.js.join('')];this.length=this.js[0].length;}}
function mv(){return kv(this);}
function cv(){}
_=cv.prototype=new zu();_.fb=lv;_.tS=mv;_.tN=nF+'StringBuffer';_.tI=0;function aw(){aw=cC;bw=new mt();}
function cw(a){aw();return C(a);}
var bw;function lw(b,a){Fu(b,a);return b;}
function kw(){}
_=kw.prototype=new Eu();_.tN=nF+'UnsupportedOperationException';_.tI=62;function vw(b,a){b.c=a;return b;}
function xw(a){return a.a<a.c.yb();}
function yw(){return xw(this);}
function zw(){if(!xw(this)){throw new rB();}return this.c.D(this.b=this.a++);}
function Aw(){if(this.b<0){throw new eu();}this.c.rb(this.b);this.a=this.b;this.b=(-1);}
function uw(){}
_=uw.prototype=new zu();_.F=yw;_.eb=zw;_.qb=Aw;_.tN=oF+'AbstractList$IteratorImpl';_.tI=0;_.a=0;_.b=(-1);function dy(f,d,e){var a,b,c;for(b=cA(f.s());Az(b);){a=Bz(b);c=a.A();if(d===null?c===null:d.eQ(c)){if(e){Cz(b);}return a;}}return null;}
function ey(b){var a;a=b.s();return fx(new ex(),b,a);}
function fy(b){var a;a=nA(b);return ux(new tx(),b,a);}
function gy(a){return dy(this,a,false)!==null;}
function hy(d){var a,b,c,e,f,g,h;if(d===this){return true;}if(!ee(d,21)){return false;}f=de(d,21);c=ey(this);e=f.db();if(!oy(c,e)){return false;}for(a=hx(c);ox(a);){b=px(a);h=this.E(b);g=f.E(b);if(h===null?g!==null:!h.eQ(g)){return false;}}return true;}
function iy(b){var a;a=dy(this,b,false);return a===null?null:a.C();}
function jy(){var a,b,c;b=0;for(c=cA(this.s());Az(c);){a=Bz(c);b+=a.hC();}return b;}
function ky(){return ey(this);}
function ly(){var a,b,c,d;d='{';a=false;for(c=cA(this.s());Az(c);){b=Bz(c);if(a){d+=', ';}else{a=true;}d+=Ev(b.A());d+='=';d+=Ev(b.C());}return d+'}';}
function dx(){}
_=dx.prototype=new zu();_.o=gy;_.eQ=hy;_.E=iy;_.hC=jy;_.db=ky;_.tS=ly;_.tN=oF+'AbstractMap';_.tI=63;function oy(e,b){var a,c,d;if(b===e){return true;}if(!ee(b,22)){return false;}c=de(b,22);if(c.yb()!=e.yb()){return false;}for(a=c.cb();a.F();){d=a.eb();if(!e.p(d)){return false;}}return true;}
function py(a){return oy(this,a);}
function qy(){var a,b,c;a=0;for(b=this.cb();b.F();){c=b.eb();if(c!==null){a+=c.hC();}}return a;}
function my(){}
_=my.prototype=new nw();_.eQ=py;_.hC=qy;_.tN=oF+'AbstractSet';_.tI=64;function fx(b,a,c){b.a=a;b.b=c;return b;}
function hx(b){var a;a=cA(b.b);return mx(new lx(),b,a);}
function ix(a){return this.a.o(a);}
function jx(){return hx(this);}
function kx(){return this.b.a.c;}
function ex(){}
_=ex.prototype=new my();_.p=ix;_.cb=jx;_.yb=kx;_.tN=oF+'AbstractMap$1';_.tI=65;function mx(b,a,c){b.a=c;return b;}
function ox(a){return a.a.F();}
function px(b){var a;a=b.a.eb();return a.A();}
function qx(){return ox(this);}
function rx(){return px(this);}
function sx(){this.a.qb();}
function lx(){}
_=lx.prototype=new zu();_.F=qx;_.eb=rx;_.qb=sx;_.tN=oF+'AbstractMap$2';_.tI=0;function ux(b,a,c){b.a=a;b.b=c;return b;}
function wx(b){var a;a=cA(b.b);return Bx(new Ax(),b,a);}
function xx(a){return mA(this.a,a);}
function yx(){return wx(this);}
function zx(){return this.b.a.c;}
function tx(){}
_=tx.prototype=new nw();_.p=xx;_.cb=yx;_.yb=zx;_.tN=oF+'AbstractMap$3';_.tI=0;function Bx(b,a,c){b.a=c;return b;}
function Dx(a){return a.a.F();}
function Ex(a){var b;b=a.a.eb().C();return b;}
function Fx(){return Dx(this);}
function ay(){return Ex(this);}
function by(){this.a.qb();}
function Ax(){}
_=Ax.prototype=new zu();_.F=Fx;_.eb=ay;_.qb=by;_.tN=oF+'AbstractMap$4';_.tI=0;function kA(){kA=cC;sA=yA();}
function gA(a){{jA(a);}}
function hA(a){kA();gA(a);return a;}
function iA(a,b){kA();gA(a);pA(a,b);return a;}
function jA(a){a.a=gb();a.d=ib();a.b=ie(sA,cb);a.c=0;}
function lA(b,a){if(ee(a,1)){return CA(b.d,de(a,1))!==sA;}else if(a===null){return b.b!==sA;}else{return BA(b.a,a,a.hC())!==sA;}}
function mA(a,b){if(a.b!==sA&&AA(a.b,b)){return true;}else if(xA(a.d,b)){return true;}else if(vA(a.a,b)){return true;}return false;}
function nA(a){return aA(new wz(),a);}
function oA(c,a){var b;if(ee(a,1)){b=CA(c.d,de(a,1));}else if(a===null){b=c.b;}else{b=BA(c.a,a,a.hC());}return b===sA?null:b;}
function qA(c,a,d){var b;if(ee(a,1)){b=FA(c.d,de(a,1),d);}else if(a===null){b=c.b;c.b=d;}else{b=EA(c.a,a,d,a.hC());}if(b===sA){++c.c;return null;}else{return b;}}
function pA(d,c){var a,b;b=cA(nA(c));while(Az(b)){a=Bz(b);qA(d,a.A(),a.C());}}
function rA(c,a){var b;if(ee(a,1)){b=bB(c.d,de(a,1));}else if(a===null){b=c.b;c.b=ie(sA,cb);}else{b=aB(c.a,a,a.hC());}if(b===sA){return null;}else{--c.c;return b;}}
function tA(e,c){kA();for(var d in e){if(d==parseInt(d)){var a=e[d];for(var f=0,b=a.length;f<b;++f){c.n(a[f]);}}}}
function uA(d,a){kA();for(var c in d){if(c.charCodeAt(0)==58){var e=d[c];var b=pz(c.substring(1),e);a.n(b);}}}
function vA(f,h){kA();for(var e in f){if(e==parseInt(e)){var a=f[e];for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.C();if(AA(h,d)){return true;}}}}return false;}
function wA(a){return lA(this,a);}
function xA(c,d){kA();for(var b in c){if(b.charCodeAt(0)==58){var a=c[b];if(AA(d,a)){return true;}}}return false;}
function yA(){kA();}
function zA(){return nA(this);}
function AA(a,b){kA();if(a===b){return true;}else if(a===null){return false;}else{return a.eQ(b);}}
function DA(a){return oA(this,a);}
function BA(f,h,e){kA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(AA(h,d)){return c.C();}}}}
function CA(b,a){kA();return b[':'+a];}
function EA(f,h,j,e){kA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(AA(h,d)){var i=c.C();c.xb(j);return i;}}}else{a=f[e]=[];}var c=pz(h,j);a.push(c);}
function FA(c,a,d){kA();a=':'+a;var b=c[a];c[a]=d;return b;}
function aB(f,h,e){kA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(AA(h,d)){if(a.length==1){delete f[e];}else{a.splice(g,1);}return c.C();}}}}
function bB(c,a){kA();a=':'+a;var b=c[a];delete c[a];return b;}
function lz(){}
_=lz.prototype=new dx();_.o=wA;_.s=zA;_.E=DA;_.tN=oF+'HashMap';_.tI=66;_.a=null;_.b=null;_.c=0;_.d=null;var sA;function nz(b,a,c){b.a=a;b.b=c;return b;}
function pz(a,b){return nz(new mz(),a,b);}
function qz(b){var a;if(ee(b,23)){a=de(b,23);if(AA(this.a,a.A())&&AA(this.b,a.C())){return true;}}return false;}
function rz(){return this.a;}
function sz(){return this.b;}
function tz(){var a,b;a=0;b=0;if(this.a!==null){a=this.a.hC();}if(this.b!==null){b=this.b.hC();}return a^b;}
function uz(a){var b;b=this.b;this.b=a;return b;}
function vz(){return this.a+'='+this.b;}
function mz(){}
_=mz.prototype=new zu();_.eQ=qz;_.A=rz;_.C=sz;_.hC=tz;_.xb=uz;_.tS=vz;_.tN=oF+'HashMap$EntryImpl';_.tI=67;_.a=null;_.b=null;function aA(b,a){b.a=a;return b;}
function cA(a){return yz(new xz(),a.a);}
function dA(c){var a,b,d;if(ee(c,23)){a=de(c,23);b=a.A();if(lA(this.a,b)){d=oA(this.a,b);return AA(a.C(),d);}}return false;}
function eA(){return cA(this);}
function fA(){return this.a.c;}
function wz(){}
_=wz.prototype=new my();_.p=dA;_.cb=eA;_.yb=fA;_.tN=oF+'HashMap$EntrySet';_.tI=68;function yz(c,b){var a;c.c=b;a=ty(new ry());if(c.c.b!==(kA(),sA)){vy(a,nz(new mz(),null,c.c.b));}uA(c.c.d,a);tA(c.c.a,a);c.a=a.cb();return c;}
function Az(a){return a.a.F();}
function Bz(a){return a.b=de(a.a.eb(),23);}
function Cz(a){if(a.b===null){throw fu(new eu(),'Must call next() before remove().');}else{a.a.qb();rA(a.c,a.b.A());a.b=null;}}
function Dz(){return Az(this);}
function Ez(){return Bz(this);}
function Fz(){Cz(this);}
function xz(){}
_=xz.prototype=new zu();_.F=Dz;_.eb=Ez;_.qb=Fz;_.tN=oF+'HashMap$EntrySetIterator';_.tI=0;_.a=null;_.b=null;function dB(a){a.a=hA(new lz());return a;}
function fB(a){var b;b=qA(this.a,a,yt(true));return b===null;}
function gB(a){return lA(this.a,a);}
function hB(){return hx(ey(this.a));}
function iB(){return this.a.c;}
function jB(){return ey(this.a).tS();}
function cB(){}
_=cB.prototype=new my();_.n=fB;_.p=gB;_.cb=hB;_.yb=iB;_.tS=jB;_.tN=oF+'HashSet';_.tI=69;_.a=null;function pB(d,c,a,b){Fu(d,c);return d;}
function oB(){}
_=oB.prototype=new Eu();_.tN=oF+'MissingResourceException';_.tI=70;function rB(){}
_=rB.prototype=new Eu();_.tN=oF+'NoSuchElementException';_.tI=71;function wB(a){a.a=ty(new ry());return a;}
function xB(b,a){return vy(b.a,a);}
function zB(b,a){return AB(b,a);}
function AB(b,a){return zy(b.a,a);}
function BB(a,b){uy(this.a,a,b);}
function CB(a){return xB(this,a);}
function DB(a){return yy(this.a,a);}
function EB(a){return AB(this,a);}
function FB(){return this.a.cb();}
function aC(a){return Cy(this.a,a);}
function bC(){return this.a.b;}
function vB(){}
_=vB.prototype=new tw();_.m=BB;_.n=CB;_.p=DB;_.D=EB;_.cb=FB;_.rb=aC;_.yb=bC;_.tN=oF+'Vector';_.tI=72;_.a=null;function AC(g,h){var a,c,d,e,f;c=fD(new dD(),h);try{e=FE(c);f=sC(new rC(),g,e,c);pg(f,1);}catch(a){a=le(a);if(ee(a,25)){d=a;hw(d);}else throw a;}}
function BC(g,h){var a,c,d,e,f;c=oD(new mD(),h);try{e=FE(c);f=wC(new vC(),g,e,c);pg(f,1);}catch(a){a=le(a);if(ee(a,25)){d=a;Bg('Exception: '+d.b);hw(d);}else throw a;}}
function CC(r){var a,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,s;k='DEFAULT-identities-and-usecases.xml';l='DEFAULT-policy.xml';f='DEFAULT-cancel.html';m='DEFAULT-save-policy.xml';try{h=td('getURLs');k=qd(h,'identities-url');l=qd(h,'policy-url');f=qd(h,'cancel-url');m=qd(h,'save-url');}catch(a){a=le(a);if(ee(a,24)){i=a;Bg('Exception: '+i.b);}else throw a;}BC(r,l);AC(r,k);s=Dn(new Bn());ui(Dm(),s);p=Dn(new Bn());En(s,p);q=pn(new hn());rn(q,30);En(p,q);o=Fi(new yi(),'Save User or Group',fC(new eC(),r,q));En(p,o);j=hl(new fl());ll(j,(al(),bl));En(s,j);d=hl(new fl());En(s,d);n=m;r.g=Fi(new yi(),'Save Policy',jC(new iC(),r,n));wn(r.g,'gwt-wyona-SaveButton');il(d,r.g);g=f;e=Fi(new yi(),'Cancel',nC(new mC(),r,g));wn(r.g,'gwt-wyona-CancelButton');il(d,e);r.b=FD(new DD(),r.j,r.i,r.a);r.d=fE(new dE(),r.j,r.e,r.c,r.h);c=FC(new DC(),r.b.a,r.d.c,r.d);wn(c,'gwt-wyona-AddRemoveWidget');il(j,r.b);il(j,c);il(j,r.d);}
function dC(){}
_=dC.prototype=new zu();_.tN=pF+'AccessPolicyEditor';_.tI=0;_.a=null;_.b=null;_.c=null;_.d=null;_.e=null;_.f=null;_.g=null;_.h=true;_.i=null;_.j=10;function fC(b,a,c){b.a=a;b.b=c;return b;}
function hC(d){var a,b,c;b=cm(this.a.b.a);for(a=0;a<b;a++){c=dm(this.a.b.a,a);if(pv(c,mn(this.b))>=0)Bg('Result: '+c);}}
function eC(){}
_=eC.prototype=new zu();_.ib=hC;_.tN=pF+'AccessPolicyEditor$1';_.tI=73;function jC(b,a,c){b.a=a;b.b=c;return b;}
function lC(f){var a,c,d,e;c=vD(new uD(),this.b);try{e=xD(c,nE(this.a.d),iE(this.a.d),mE(this.a.d));}catch(a){a=le(a);if(ee(a,25)){d=a;Bg('Exception: '+d.b);}else throw a;}}
function iC(){}
_=iC.prototype=new zu();_.ib=lC;_.tN=pF+'AccessPolicyEditor$2';_.tI=74;function nC(b,a,c){b.a=c;return b;}
function pC(a,b){$wnd.location.href=b;}
function qC(a){Bg('Redirect to '+this.a);pC(this,this.a);}
function mC(){}
_=mC.prototype=new zu();_.ib=qC;_.tN=pF+'AccessPolicyEditor$3';_.tI=75;function tC(){tC=cC;mg();}
function sC(b,a,d,c){tC();b.a=a;b.c=d;b.b=c;kg(b);return b;}
function uC(){if(xc(this.c)){og(this,10);}else{this.a.i=kD(this.b);this.a.a=iD(this.b);this.a.f=jD(this.b);bE(this.a.b,this.a.j,this.a.i,this.a.a);lg(this);Bg('Identities have been loaded!');}}
function rC(){}
_=rC.prototype=new fg();_.ub=uC;_.tN=pF+'AccessPolicyEditor$4';_.tI=76;function xC(){xC=cC;mg();}
function wC(b,a,d,c){xC();b.a=a;b.c=d;b.b=c;kg(b);return b;}
function yC(){if(xc(this.c)){og(this,10);}else{this.a.e=sD(this.b);this.a.c=rD(this.b);pE(this.a.d,this.a.j,this.a.e,this.a.c);this.a.h=this.b.b;qE(this.a.d,this.a.h);lg(this);Bg('Policy has been loaded!');}}
function vC(){}
_=vC.prototype=new fg();_.ub=yC;_.tN=pF+'AccessPolicyEditor$5';_.tI=77;function EC(a){a.b=ik(new hk());}
function FC(d,a,c,b){EC(d);ck(d,d.b);d.e=Fi(new yi(),'<',d);jk(d.b,d.e);d.a=Fi(new yi(),'>',d);jk(d.b,d.a);d.c=a;d.d=c;return d;}
function bD(b,a){if(pv(a,'(')>0){return vv(a,0,pv(a,'('));}else{return a;}}
function cD(c){var a,b;if(c===this.a){a=em(this.c);if(a>=0){b=fm(this.c,a);Bg('Add selected identity '+b+' to policy');im(this.c,a);El(this.d,vv(b,0,1)+': (-,-) '+wv(uv(b,2)),b);}else{Bg('No identity selected yet! Please select an identity.');}}else if(c===this.e){a=em(this.d);if(a>=0){b=fm(this.d,a);Bg('Remove selected identity '+b+' from policy');im(this.d,a);Dl(this.c,bD(this,b));}else{Bg('No identity selected yet! Please select an identity.');}}}
function DC(){}
_=DC.prototype=new ak();_.ib=cD;_.tN=pF+'AddRemoveIdentitiesWidget';_.tI=78;_.a=null;_.c=null;_.d=null;_.e=null;function BE(a){a.d=hA(new lz());}
function CE(a,b){BE(a);a.e=Db(new yb(),(Fb(),dc),b);aF(a);return a;}
function DE(e){var a,b,c,d;b='';a=iA(new lz(),e.d);for(d=cA(nA(a));Az(d);){c=Bz(d);b+=c.A()+''+c.C();if(Az(d)){b+='&';}}return b;}
function FE(a){return ac(a.e,DE(a),a);}
function aF(a){bc(a.e,'Content-Type','application/x-www-form-urlencoded');}
function bF(b,a){Bg('Exception: '+a.b);}
function AE(){}
_=AE.prototype=new zu();_.kb=bF;_.tN=qF+'AsynchronousAgent';_.tI=0;_.e=null;function eD(a){a.c=wB(new vB());a.a=wB(new vB());a.b=wB(new vB());}
function fD(a,b){CE(a,b);eD(a);return a;}
function hD(d,c,a){var b;b=c.z(a);return de(b.bb(0),16);}
function iD(c){var a,b;a=Ed('[Ljava.lang.String;',[0],[1],[c.a.a.b],null);for(b=0;b<c.a.a.b;b++){a[b]=de(zB(c.a,b),1);}return a;}
function jD(c){var a,b;b=Ed('[Ljava.lang.String;',[0],[1],[c.b.a.b],null);for(a=0;a<c.b.a.b;a++){b[a]=de(zB(c.b,a),1);}return b;}
function kD(b){var a,c;c=Ed('[Ljava.lang.String;',[0],[1],[b.c.a.b],null);for(a=0;a<b.c.a.b;a++){c[a]=de(zB(b.c,a),1);}return c;}
function lD(d,e){var a,b,c,f,g,h,i,j;h=aq(tb(e)).w();j=hD(this,h,'users');i=j.z('user');for(c=0;c<i.B();c++){xB(this.c,de(i.bb(c),16).v('id'));}b=hD(this,h,'groups');a=b.z('group');for(c=0;c<a.B();c++){xB(this.a,de(a.bb(c),16).v('id'));}g=hD(this,h,'rights');f=g.z('right');for(c=0;c<f.B();c++){xB(this.b,de(f.bb(c),16).v('id'));}}
function dD(){}
_=dD.prototype=new AE();_.mb=lD;_.tN=pF+'AsynchronousIdentitiesAndRightsGetter';_.tI=0;function nD(a){a.c=wB(new vB());a.a=wB(new vB());}
function oD(a,b){CE(a,b);nD(a);return a;}
function qD(d,c,a){var b;b=c.z(a);if(b.B()>0){return de(b.bb(0),16);}else{return null;}}
function rD(c){var a,b;b=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[27],[c.a.a.b],null);for(a=0;a<b.a;a++){b[a]=de(zB(c.a,a),27);}return b;}
function sD(c){var a,b;b=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[26],[c.c.a.b],null);for(a=0;a<b.a;a++){b[a]=de(zB(c.c,a),26);}return b;}
function tD(e,f){var a,b,c,d,g,h,i,j,k,l,m,n;j=aq(tb(f)).w();k=j.v('use-inherited-policies');if(k===null){this.b=true;}else{if(ov(k,'false')){this.b=false;}else{this.b=true;}}n=qD(this,j,'world');m=j.z('user');for(c=0;c<m.B();c++){l=de(m.bb(c),16);h=l.z('right');i=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[28],[h.B()],null);for(d=0;d<i.a;d++){g=de(h.bb(d),16);i[d]=vE(new uE(),g.v('id'),true);}xB(this.c,yE(new xE(),l.v('id'),i));}b=j.z('group');for(c=0;c<b.B();c++){a=de(b.bb(c),16);h=a.z('right');i=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[28],[h.B()],null);for(d=0;d<i.a;d++){g=de(h.bb(d),16);i[d]=vE(new uE(),g.v('id'),true);}xB(this.a,BD(new AD(),a.v('id'),i));}}
function mD(){}
_=mD.prototype=new AE();_.mb=tD;_.tN=pF+'AsynchronousPolicyGetter';_.tI=0;_.b=true;function vD(a,b){Bg('Save policy to: '+b);a.a=Db(new yb(),(Fb(),ec),b);return a;}
function xD(f,h,b,g){var a,c,d,e;a=ev(new cv(),'<?xml version="1.0"?>');gv(a,'<policy xmlns="http://www.wyona.org/security/1.0" use-inherited-policies="'+g+'">');if(h!==null){for(c=0;c<h.a;c++){gv(a,'<user id="'+h[c].a+'">');e=h[c].b;if(e!==null){for(d=0;d<e.a;d++){gv(a,'<right id="'+e[d].a+'" permission="'+e[d].b+'">'+e[d].a+'<\/right>');}}else{gv(a,'<right id="r" permission="false">r<\/right>');gv(a,'<right id="w" permission="false">w<\/right>');}gv(a,'<\/user>');}}if(b!==null){for(c=0;c<b.a;c++){gv(a,'<group id="'+b[c].a+'">');e=b[c].b;if(e!==null){for(d=0;d<e.a;d++){gv(a,'<right id="'+e[d].a+'" permission="'+e[d].b+'">'+e[d].a+'<\/right>');}}else{gv(a,'<right id="r" permission="false">r<\/right>');gv(a,'<right id="w" permission="false">w<\/right>');}gv(a,'<\/group>');}}gv(a,'<\/policy>');return ac(f.a,kv(a),f);}
function yD(b,a){Bg('Exception: '+a.b);}
function zD(a,b){if(sb(b)==200){Bg('Policy has been saved successfully!');}else{Bg('Policy has NOT been saved! Please check log files on server.');}}
function uD(){}
_=uD.prototype=new zu();_.kb=yD;_.mb=zD;_.tN=pF+'AsynchronousPolicySetter';_.tI=0;_.a=null;function BD(c,a,b){c.a=a;c.b=b;return c;}
function AD(){}
_=AD.prototype=new zu();_.tN=pF+'Group';_.tI=79;_.a=null;_.b=null;function ED(a){a.b=Dn(new Bn());}
function FD(b,d,c,a){ED(b);ck(b,b.b);En(b.b,ql(new ol(),'Identities'));b.a=Cl(new ul(),true);b.a.l(b);bE(b,d,c,a);En(b.b,b.a);return b;}
function bE(c,e,d,a){var b;am(c.a);km(c.a,e);if(d!==null){for(b=0;b<d.a;b++){Dl(c.a,'u: '+d[b]);}}else{Dl(c.a,'No users yet!');}if(a!==null){for(b=0;b<a.a;b++){Dl(c.a,'g: '+a[b]);}}else{Dl(c.a,'No groups yet!');}}
function cE(a){}
function DD(){}
_=DD.prototype=new ak();_.ib=cE;_.tN=pF+'IdentitiesListBoxWidget';_.tI=80;_.a=null;function eE(a){a.f=Dn(new Bn());}
function fE(b,e,d,a,c){eE(b);ck(b,b.f);En(b.f,ql(new ol(),'Policy'));b.d=kj(new hj(),'Inherit rights from parent policies');qE(b,c);En(b.f,b.d);b.c=Cl(new ul(),true);b.c.l(b);pE(b,e,d,a);En(b.f,b.c);b.e=kj(new hj(),'Read');b.e.l(b);En(b.f,b.e);b.g=kj(new hj(),'Write');b.g.l(b);En(b.f,b.g);return b;}
function gE(g,a,f){var b,c,d,e;e=wB(new vB());for(c=0;c<a.a;c++){if(a[c].b){xB(e,a[c].a);}}b=false;for(c=0;c<a.a;c++){if(ov(a[c].a,f)&&a[c].b){b=true;break;}}if(!b)xB(e,f);d=Ed('[Ljava.lang.String;',[0],[1],[e.a.b],null);for(c=0;c<d.a;c++){d[c]=de(zB(e,c),1);}return d;}
function iE(g){var a,b,c,d,e,f;b=wB(new vB());for(c=0;c<cm(g.c);c++){e=dm(g.c,c);f=kE(g,e);d=jE(g,c);if(tv(d,'g:')){xB(b,BD(new AD(),wv(uv(d,2)),f));}}a=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[27],[b.a.b],null);for(c=0;c<a.a;c++){a[c]=de(zB(b,c),27);}return a;}
function jE(b,a){return fm(b.c,a);}
function kE(f,b){var a,c,d,e;if(pv(b,'(')>0){d=rv(vv(b,pv(b,'(')+1,pv(b,')')),',');e=wB(new vB());for(a=0;a<d.a;a++){if(!ov(d[a],'-')){xB(e,vE(new uE(),d[a],true));}else{if(a==0){xB(e,vE(new uE(),'r',false));}else if(a==1){xB(e,vE(new uE(),'w',false));}else{xB(e,vE(new uE(),'TODO',false));}}}c=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[28],[e.a.b],null);for(a=0;a<c.a;a++){c[a]=de(zB(e,a),28);}return c;}else{return Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[28],[0],null);}}
function lE(b){var a;a=em(b.c);if(a>=0){return dm(b.c,a);}return null;}
function mE(a){return mj(a.d);}
function nE(e){var a,b,c,d,f,g;g=wB(new vB());for(a=0;a<cm(e.c);a++){c=dm(e.c,a);d=kE(e,c);b=jE(e,a);if(tv(b,'u:')){xB(g,yE(new xE(),wv(uv(b,2)),d));}}f=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[26],[g.a.b],null);for(a=0;a<f.a;a++){f[a]=de(zB(g,a),26);}return f;}
function oE(f,a,e){var b,c,d;d=wB(new vB());for(b=0;b<a.a;b++){if(!ov(a[b].a,e)&&a[b].b){xB(d,a[b].a);}}c=Ed('[Ljava.lang.String;',[0],[1],[d.a.b],null);for(b=0;b<c.a;b++){c[b]=de(zB(d,b),1);}return c;}
function pE(g,j,h,a){var b,c,d,e,f,i,k;am(g.c);km(g.c,j);if(h!==null||a!==null){if(h!==null){for(b=0;b<h.a;b++){d='u: (';f=h[b].b;e=false;for(c=0;c<f.a;c++){if(ov(f[c].a,g.a)){e=true;break;}}if(e){d=d+g.a;}else{d=d+'-';}d=d+',';k=false;for(c=0;c<f.a;c++){if(ov(f[c].a,g.b)){k=true;break;}}if(k){d=d+g.b;}else{d=d+'-';}d=d+') '+h[b].a;i='u: '+h[b].a;El(g.c,d,i);}}if(a!==null){for(b=0;b<a.a;b++){d='g: (';f=a[b].b;e=false;for(c=0;c<f.a;c++){if(ov(f[c].a,g.a)){e=true;break;}}if(e){d=d+g.a;}else{d=d+'-';}d=d+',';k=false;for(c=0;c<f.a;c++){if(ov(f[c].a,g.b)){k=true;break;}}if(k){d=d+g.b;}else{d=d+'-';}d=d+') '+a[b].a;i='g: '+a[b].a;El(g.c,d,i);}}else{Bg('No groups!');}}else{Dl(g.c,'No identities yet!');}}
function qE(a,b){if(a.d!==null){nj(a.d,b);}}
function rE(g,h,a,e,b){var c,d,f,i;f=ev(new cv(),h+':');gv(f,' (');d=false;i=false;for(c=0;c<e.a;c++){if(ov(e[c],g.a)){d=true;}if(ov(e[c],g.b)){i=true;}}if(d){gv(f,g.a);}else{gv(f,'-');}gv(f,',');if(i){gv(f,g.b);}else{gv(f,'-');}gv(f,')');gv(f,' '+a);jm(g.c,b,kv(f));}
function sE(d,c){var a,b;b=em(d.c);if(b>=0){a=jE(d,b);rE(d,vv(a,0,1),wv(uv(a,2)),c,b);}else{Bg('Exception: No list item selected!');}}
function tE(h){var a,b,c,d,e,f,g;if(h===this.e||h===this.g){g=lE(this);if(g!==null){if(h===this.e){a=kE(this,g);if(mj(this.e)){Bg('Add Read right of selected identity '+g+' to policy');e=gE(this,a,this.a);}else{Bg('Remove Read right of selected identity '+g+' from policy');e=oE(this,a,this.a);}sE(this,e);}else if(h===this.g){a=kE(this,g);if(mj(this.g)){Bg('Add Write right of selected identity '+g+' to policy');e=gE(this,a,this.b);}else{Bg('Remove Write right of selected identity '+g+' from policy');e=oE(this,a,this.b);}sE(this,e);}}else{Bg('No identity has been selected! Please select an identity in order to assign rights.');nj(this.e,false);nj(this.g,false);}}else if(h===this.c){g=lE(this);f=kE(this,g);b=false;c=false;for(d=0;d<f.a;d++){if(ov(f[d].a,this.a)&&f[d].b){nj(this.e,true);b=true;}else if(ov(f[d].a,this.b)&&f[d].b){nj(this.g,true);c=true;}}if(!b)nj(this.e,false);if(!c)nj(this.g,false);}}
function dE(){}
_=dE.prototype=new ak();_.ib=tE;_.tN=pF+'PolicyListBoxWidget';_.tI=81;_.a='r';_.b='w';_.c=null;_.d=null;_.e=null;_.g=null;function vE(c,a,b){c.a=a;c.b=b;return c;}
function uE(){}
_=uE.prototype=new zu();_.tN=pF+'Right';_.tI=82;_.a=null;_.b=false;function yE(c,a,b){c.a=a;c.b=b;return c;}
function xE(){}
_=xE.prototype=new zu();_.tN=pF+'User';_.tI=83;_.a=null;_.b=null;function ht(){CC(new dC());}
function gwtOnLoad(b,d,c){$moduleName=d;$moduleBase=c;if(b)try{ht();}catch(a){b(d);}else{ht();}}
var he=[{},{},{1:1},{4:1},{4:1,25:1},{4:1,25:1},{4:1,18:1,25:1},{2:1},{6:1},{6:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{3:1},{2:1,5:1},{2:1},{7:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{20:1},{20:1},{20:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{9:1,10:1,11:1,12:1,13:1,14:1},{7:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{4:1,25:1},{15:1},{15:1},{15:1},{15:1},{15:1},{15:1},{15:1},{4:1,25:1},{15:1},{15:1,17:1},{15:1,16:1},{15:1},{15:1},{15:1},{4:1,25:1},{19:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{21:1},{22:1},{22:1},{21:1},{23:1},{22:1},{22:1},{4:1,24:1,25:1},{4:1,25:1},{20:1},{8:1},{8:1},{8:1},{6:1},{6:1},{8:1,11:1,12:1,13:1,14:1},{27:1},{8:1,11:1,12:1,13:1,14:1},{8:1,11:1,12:1,13:1,14:1},{28:1},{26:1}];if (org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor) {  var __gwt_initHandlers = org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.__gwt_initHandlers;  org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.onScriptLoad(gwtOnLoad);}})();