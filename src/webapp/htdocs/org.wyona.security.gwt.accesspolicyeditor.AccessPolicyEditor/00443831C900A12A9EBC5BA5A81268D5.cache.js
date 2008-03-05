(function(){var $wnd = window;var $doc = $wnd.document;var $moduleName, $moduleBase;var _,yE='com.google.gwt.core.client.',zE='com.google.gwt.http.client.',AE='com.google.gwt.i18n.client.',BE='com.google.gwt.lang.',CE='com.google.gwt.user.client.',DE='com.google.gwt.user.client.impl.',EE='com.google.gwt.user.client.ui.',FE='com.google.gwt.user.client.ui.impl.',aF='com.google.gwt.xml.client.',bF='com.google.gwt.xml.client.impl.',cF='java.io.',dF='java.lang.',eF='java.util.',fF='org.wyona.security.gwt.accesspolicyeditor.client.',gF='org.wyona.yanel.gwt.client.';function BB(){}
function uu(a){return this===a;}
function vu(){return Bv(this);}
function wu(){return this.tN+'@'+this.hC();}
function su(){}
_=su.prototype={};_.eQ=uu;_.hC=vu;_.tS=wu;_.toString=function(){return this.tS();};_.tN=dF+'Object';_.tI=1;function w(a){return a==null?null:a.tN;}
var y=null;function B(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function C(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function D(){return ++E;}
var E=0;function Dv(b,a){b.b=a;return b;}
function Fv(b,a){if(b.a!==null){throw Et(new Dt(),"Can't overwrite cause");}if(a===b){throw Bt(new At(),'Self-causation not permitted');}b.a=a;return b;}
function aw(a){bw(a,(zv(),Av));}
function bw(e,d){var a,b,c;c=Cu(new Bu());b=e;while(b!==null){a=b.b;if(b!==e){Fu(c,'Caused by: ');}Fu(c,b.tN);Fu(c,': ');Fu(c,a===null?'(No exception detail)':a);Fu(c,'\n');b=b.a;}}
function cw(){var a,b;a=w(this);b=this.b;if(b!==null){return a+': '+b;}else{return a;}}
function Cv(){}
_=Cv.prototype=new su();_.tS=cw;_.tN=dF+'Throwable';_.tI=3;_.a=null;_.b=null;function yt(b,a){Dv(b,a);return b;}
function xt(){}
_=xt.prototype=new Cv();_.tN=dF+'Exception';_.tI=4;function yu(b,a){yt(b,a);return b;}
function xu(){}
_=xu.prototype=new xt();_.tN=dF+'RuntimeException';_.tI=5;function ab(c,b,a){yu(c,'JavaScript '+b+' exception: '+a);return c;}
function F(){}
_=F.prototype=new xu();_.tN=yE+'JavaScriptException';_.tI=6;function eb(b,a){if(!ge(a,2)){return false;}return jb(b,fe(a,2));}
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
_=cb.prototype=new su();_.eQ=kb;_.hC=lb;_.tS=nb;_.tN=yE+'JavaScriptObject';_.tI=7;function rc(b,d,c,a){if(d===null){throw new lu();}if(a===null){throw new lu();}if(c<0){throw new At();}b.a=c;b.c=d;if(c>0){b.b=vb(new ub(),b,a);rg(b.b,c);}else{b.b=null;}return b;}
function tc(a){var b;if(a.c!==null){b=a.c;a.c=null;dd(b);sc(a);}}
function sc(a){if(a.b!==null){ng(a.b);}}
function vc(e,a){var b,c,d,f;if(e.c===null){return;}sc(e);f=e.c;e.c=null;b=ed(f);if(b!==null){c=yu(new xu(),b);a.kb(e,c);}else{d=yc(f);a.mb(e,d);}}
function wc(b,a){if(b.c===null){return;}tc(b);a.kb(b,oc(new nc(),b,b.a));}
function xc(b){var a;if(b.c===null){return false;}a=fd(b.c);switch(a){case 1:case 2:case 3:return true;}return false;}
function yc(b){var a;a=qb(new pb(),b);return a;}
function zc(a){var b;b=y;{vc(this,a);}}
function ob(){}
_=ob.prototype=new su();_.t=zc;_.tN=zE+'Request';_.tI=0;_.a=0;_.b=null;_.c=null;function Ac(){}
_=Ac.prototype=new su();_.tN=zE+'Response';_.tI=0;function qb(a,b){a.a=b;return a;}
function sb(a){return hd(a.a);}
function tb(a){return gd(a.a);}
function pb(){}
_=pb.prototype=new Ac();_.tN=zE+'Request$1';_.tI=0;function og(){og=BB;yg=my(new ky());{xg();}}
function mg(a){og();return a;}
function ng(a){if(a.d){sg(a.e);}else{tg(a.e);}wy(yg,a);}
function pg(a){if(!a.d){wy(yg,a);}a.ub();}
function rg(b,a){if(a<=0){throw Bt(new At(),'must be positive');}ng(b);b.d=false;b.e=vg(b,a);oy(yg,b);}
function qg(b,a){if(a<=0){throw Bt(new At(),'must be positive');}ng(b);b.d=true;b.e=ug(b,a);oy(yg,b);}
function sg(a){og();$wnd.clearInterval(a);}
function tg(a){og();$wnd.clearTimeout(a);}
function ug(b,a){og();return $wnd.setInterval(function(){b.u();},a);}
function vg(b,a){og();return $wnd.setTimeout(function(){b.u();},a);}
function wg(){var a;a=y;{pg(this);}}
function xg(){og();Cg(new ig());}
function hg(){}
_=hg.prototype=new su();_.u=wg;_.tN=CE+'Timer';_.tI=8;_.d=false;_.e=0;var yg;function wb(){wb=BB;og();}
function vb(b,a,c){wb();b.a=a;b.b=c;mg(b);return b;}
function xb(){wc(this.a,this.b);}
function ub(){}
_=ub.prototype=new hg();_.ub=xb;_.tN=zE+'Request$2';_.tI=9;function Fb(){Fb=BB;dc=Ab(new zb(),'GET');ec=Ab(new zb(),'POST');fc=ni(new mi());}
function Db(b,a,c){Fb();Eb(b,a===null?null:a.a,c);return b;}
function Eb(b,a,c){Fb();Ec('httpMethod',a);Ec('url',c);b.b=a;b.d=c;return b;}
function ac(g,d,a){var b,c,e,f,h;h=pi(fc);{b=id(h,g.b,g.d,true);}if(b!==null){e=lc(new kc(),g.d);Fv(e,ic(new hc(),b));throw e;}cc(g,h);c=rc(new ob(),h,g.c,a);f=jd(h,c,d,a);if(f!==null){throw ic(new hc(),f);}return c;}
function bc(b,a,c){Ec('header',a);Ec('value',c);if(b.a===null){b.a=aA(new ez());}jA(b.a,a,c);}
function cc(e,f){var a,b,c,d;if(e.a!==null&&e.a.c>0){a=gA(e.a);d=Bz(a);while(tz(d)){c=uz(d);b=kd(f,fe(c.A(),1),fe(c.C(),1));if(b!==null){throw ic(new hc(),b);}}}else{kd(f,'Content-Type','text/plain; charset=utf-8');}}
function yb(){}
_=yb.prototype=new su();_.tN=zE+'RequestBuilder';_.tI=0;_.a=null;_.b=null;_.c=0;_.d=null;var dc,ec,fc;function Ab(b,a){b.a=a;return b;}
function Cb(){return this.a;}
function zb(){}
_=zb.prototype=new su();_.tS=Cb;_.tN=zE+'RequestBuilder$Method';_.tI=0;_.a=null;function ic(b,a){yt(b,a);return b;}
function hc(){}
_=hc.prototype=new xt();_.tN=zE+'RequestException';_.tI=10;function lc(a,b){ic(a,'The URL '+b+' is invalid or violates the same-origin security restriction');return a;}
function kc(){}
_=kc.prototype=new hc();_.tN=zE+'RequestPermissionException';_.tI=11;function oc(b,a,c){ic(b,qc(c));return b;}
function qc(a){return 'A request timeout has expired after '+fu(a)+' ms';}
function nc(){}
_=nc.prototype=new hc();_.tN=zE+'RequestTimeoutException';_.tI=12;function Ec(a,b){Fc(a,b);if(0==jv(pv(b))){throw Bt(new At(),a+' can not be empty');}}
function Fc(a,b){if(null===b){throw mu(new lu(),a+' can not be null');}}
function dd(a){a.onreadystatechange=ri;a.abort();}
function ed(b){try{if(b.status===undefined){return 'XmlHttpRequest.status == undefined, please see Safari bug '+'http://bugs.webkit.org/show_bug.cgi?id=3810 for more details';}return null;}catch(a){return 'Unable to read XmlHttpRequest.status; likely causes are a '+'networking error or bad cross-domain request. Please see '+'https://bugzilla.mozilla.org/show_bug.cgi?id=238559 for more '+'details';}}
function fd(a){return a.readyState;}
function gd(a){return a.responseText;}
function hd(a){return a.status;}
function id(e,c,d,b){try{e.open(c,d,b);return null;}catch(a){return a.message||a.toString();}}
function jd(e,c,d,b){e.onreadystatechange=function(){if(e.readyState==cd){e.onreadystatechange=ri;c.t(b);}};try{e.send(d);return null;}catch(a){e.onreadystatechange=ri;return a.message||a.toString();}}
function kd(d,b,c){try{d.setRequestHeader(b,c);return null;}catch(a){return a.message||a.toString();}}
var cd=4;function pd(){pd=BB;sd=aA(new ez());}
function md(b,a){pd();if(a===null||hv('',a)){throw Bt(new At(),'Cannot create a Dictionary with a null or empty name');}b.b='Dictionary '+a;od(b,a);if(b.a===null){throw iB(new hB(),"Cannot find JavaScript object with the name '"+a+"'",a,null);}return b;}
function nd(b,a){for(x in b.a){a.n(x);}}
function od(c,b){try{if(typeof $wnd[b]!='object'){ud(b);}c.a=$wnd[b];}catch(a){ud(b);}}
function qd(b,a){var c=b.a[a];if(c==null|| !Object.prototype.hasOwnProperty.call(b.a,a)){b.tb(a);}return String(c);}
function rd(b){var a;a=CA(new BA());nd(b,a);return a;}
function td(a){pd();var b;b=fe(hA(sd,a),3);if(b===null){b=md(new ld(),a);jA(sd,a,b);}return b;}
function vd(b){var a,c;c=rd(this);a="Cannot find '"+b+"' in "+this;if(c.a.c<20){a+='\n keys found: '+c;}throw iB(new hB(),a,this.b,b);}
function ud(a){pd();throw iB(new hB(),"'"+a+"' is not a JavaScript object and cannot be used as a Dictionary",null,a);}
function wd(){return this.b;}
function ld(){}
_=ld.prototype=new su();_.tb=vd;_.tS=wd;_.tN=AE+'Dictionary';_.tI=13;_.a=null;_.b=null;var sd;function yd(c,a,d,b,e){c.a=a;c.b=b;c.tN=e;c.tI=d;return c;}
function Ad(a,b,c){return a[b]=c;}
function Bd(b,a){return b[a];}
function Dd(b,a){return b[a];}
function Cd(a){return a.length;}
function Fd(e,d,c,b,a){return Ed(e,d,c,b,0,Cd(b),a);}
function Ed(j,i,g,c,e,a,b){var d,f,h;if((f=Bd(c,e))<0){throw new ju();}h=yd(new xd(),f,Bd(i,e),Bd(g,e),j);++e;if(e<a){j=nv(j,1);for(d=0;d<f;++d){Ad(h,d,Ed(j,i,g,c,e,a,b));}}else{for(d=0;d<f;++d){Ad(h,d,b);}}return h;}
function ae(f,e,c,g){var a,b,d;b=Cd(g);d=yd(new xd(),b,e,c,f);for(a=0;a<b;++a){Ad(d,a,Dd(g,a));}return d;}
function be(a,b,c){if(c!==null&&a.b!=0&& !ge(c,a.b)){throw new ht();}return Ad(a,b,c);}
function xd(){}
_=xd.prototype=new su();_.tN=BE+'Array';_.tI=0;function ee(b,a){return !(!(b&&je[b][a]));}
function fe(b,a){if(b!=null)ee(b.tI,a)||ie();return b;}
function ge(b,a){return b!=null&&ee(b.tI,a);}
function ie(){throw new tt();}
function he(a){if(a!==null){throw new tt();}return a;}
function ke(b,d){_=d.prototype;if(b&& !(b.tI>=_.tI)){var c=b.toString;for(var a in _){b[a]=_[a];}b.toString=c;}return b;}
var je;function ne(a){if(ge(a,4)){return a;}return ab(new F(),pe(a),oe(a));}
function oe(a){return a.message;}
function pe(a){return a.name;}
function re(){re=BB;rf=my(new ky());{mf=new ih();mh(mf);}}
function se(b,a){re();xh(mf,b,a);}
function te(a,b){re();return kh(mf,a,b);}
function ue(){re();return zh(mf,'button');}
function ve(){re();return zh(mf,'div');}
function we(){re();return Ah(mf,'checkbox');}
function xe(){re();return Ah(mf,'text');}
function ye(){re();return zh(mf,'label');}
function ze(a){re();return Bh(mf,a);}
function Ae(){re();return zh(mf,'span');}
function Be(){re();return zh(mf,'tbody');}
function Ce(){re();return zh(mf,'td');}
function De(){re();return zh(mf,'tr');}
function Ee(){re();return zh(mf,'table');}
function bf(b,a,d){re();var c;c=y;{af(b,a,d);}}
function af(b,a,c){re();var d;if(a===qf){if(df(b)==8192){qf=null;}}d=Fe;Fe=b;try{c.hb(b);}finally{Fe=d;}}
function cf(b,a){re();Ch(mf,b,a);}
function df(a){re();return Dh(mf,a);}
function ef(a){re();sh(mf,a);}
function ff(a){re();return th(mf,a);}
function jf(a,b){re();return ai(mf,a,b);}
function gf(a,b){re();return Eh(mf,a,b);}
function hf(a,b){re();return Fh(mf,a,b);}
function kf(a){re();return bi(mf,a);}
function lf(a){re();return uh(mf,a);}
function nf(c,b,d,a){re();ci(mf,c,b,d,a);}
function of(a){re();var b,c;c=true;if(rf.b>0){b=he(sy(rf,rf.b-1));if(!(c=null.Ab())){cf(a,true);ef(a);}}return c;}
function pf(b,a){re();di(mf,b,a);}
function uf(a,b,c){re();gi(mf,a,b,c);}
function sf(a,b,c){re();ei(mf,a,b,c);}
function tf(a,b,c){re();fi(mf,a,b,c);}
function vf(a,b){re();hi(mf,a,b);}
function wf(a,b){re();ii(mf,a,b);}
function xf(a,b){re();ji(mf,a,b);}
function yf(b,c,a){re();ki(mf,b,c,a);}
function zf(b,a,c){re();li(mf,b,a,c);}
function Af(a,b){re();oh(mf,a,b);}
function Bf(a){re();return ph(mf,a);}
var Fe=null,mf=null,qf=null,rf;function Ef(a){if(ge(a,5)){return te(this,fe(a,5));}return eb(ke(this,Cf),a);}
function Ff(){return fb(ke(this,Cf));}
function ag(){return Bf(this);}
function Cf(){}
_=Cf.prototype=new cb();_.eQ=Ef;_.hC=Ff;_.tS=ag;_.tN=CE+'Element';_.tI=14;function eg(a){return eb(ke(this,bg),a);}
function fg(){return fb(ke(this,bg));}
function gg(){return ff(this);}
function bg(){}
_=bg.prototype=new cb();_.eQ=eg;_.hC=fg;_.tS=gg;_.tN=CE+'Event';_.tI=15;function kg(){while((og(),yg).b>0){ng(fe(sy((og(),yg),0),6));}}
function lg(){return null;}
function ig(){}
_=ig.prototype=new su();_.ob=kg;_.pb=lg;_.tN=CE+'Timer$1';_.tI=16;function Bg(){Bg=BB;Eg=my(new ky());gh=my(new ky());{ch();}}
function Cg(a){Bg();oy(Eg,a);}
function Dg(a){Bg();$wnd.alert(a);}
function Fg(){Bg();var a,b;for(a=Eg.cb();a.F();){b=fe(a.eb(),7);b.ob();}}
function ah(){Bg();var a,b,c,d;d=null;for(a=Eg.cb();a.F();){b=fe(a.eb(),7);c=b.pb();{d=c;}}return d;}
function bh(){Bg();var a,b;for(a=gh.cb();a.F();){b=he(a.eb());null.Ab();}}
function ch(){Bg();__gwt_initHandlers(function(){fh();},function(){return eh();},function(){dh();$wnd.onresize=null;$wnd.onbeforeclose=null;$wnd.onclose=null;});}
function dh(){Bg();var a;a=y;{Fg();}}
function eh(){Bg();var a;a=y;{return ah();}}
function fh(){Bg();var a;a=y;{bh();}}
var Eg,gh;function xh(c,b,a){b.appendChild(a);}
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
function hh(){}
_=hh.prototype=new su();_.tN=DE+'DOMImpl';_.tI=0;function sh(b,a){a.preventDefault();}
function th(b,a){return a.toString();}
function uh(c,a){var b=a.parentNode;if(b==null){return null;}if(b.nodeType!=1)b=null;return b||null;}
function vh(d){$wnd.__dispatchCapturedMouseEvent=function(b){if($wnd.__dispatchCapturedEvent(b)){var a=$wnd.__captureElem;if(a&&a.__listener){bf(b,a,a.__listener);b.stopPropagation();}}};$wnd.__dispatchCapturedEvent=function(a){if(!of(a)){a.stopPropagation();a.preventDefault();return false;}return true;};$wnd.addEventListener('click',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('dblclick',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousedown',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mouseup',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousemove',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousewheel',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('keydown',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keyup',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keypress',$wnd.__dispatchCapturedEvent,true);$wnd.__dispatchEvent=function(b){var c,a=this;while(a&& !(c=a.__listener))a=a.parentNode;if(a&&a.nodeType!=1)a=null;if(c)bf(b,a,c);};$wnd.__captureElem=null;}
function wh(c,b,a){b.__eventBits=a;b.onclick=a&1?$wnd.__dispatchEvent:null;b.ondblclick=a&2?$wnd.__dispatchEvent:null;b.onmousedown=a&4?$wnd.__dispatchEvent:null;b.onmouseup=a&8?$wnd.__dispatchEvent:null;b.onmouseover=a&16?$wnd.__dispatchEvent:null;b.onmouseout=a&32?$wnd.__dispatchEvent:null;b.onmousemove=a&64?$wnd.__dispatchEvent:null;b.onkeydown=a&128?$wnd.__dispatchEvent:null;b.onkeypress=a&256?$wnd.__dispatchEvent:null;b.onkeyup=a&512?$wnd.__dispatchEvent:null;b.onchange=a&1024?$wnd.__dispatchEvent:null;b.onfocus=a&2048?$wnd.__dispatchEvent:null;b.onblur=a&4096?$wnd.__dispatchEvent:null;b.onlosecapture=a&8192?$wnd.__dispatchEvent:null;b.onscroll=a&16384?$wnd.__dispatchEvent:null;b.onload=a&32768?$wnd.__dispatchEvent:null;b.onerror=a&65536?$wnd.__dispatchEvent:null;b.onmousewheel=a&131072?$wnd.__dispatchEvent:null;}
function qh(){}
_=qh.prototype=new hh();_.tN=DE+'DOMImplStandard';_.tI=0;function kh(c,a,b){if(!a&& !b){return true;}else if(!a|| !b){return false;}return a.isSameNode(b);}
function mh(a){vh(a);lh(a);}
function lh(d){$wnd.addEventListener('mouseout',function(b){var a=$wnd.__captureElem;if(a&& !b.relatedTarget){if('html'==b.target.tagName.toLowerCase()){var c=$doc.createEvent('MouseEvents');c.initMouseEvent('mouseup',true,true,$wnd,0,b.screenX,b.screenY,b.clientX,b.clientY,b.ctrlKey,b.altKey,b.shiftKey,b.metaKey,b.button,null);a.dispatchEvent(c);}}},true);$wnd.addEventListener('DOMMouseScroll',$wnd.__dispatchCapturedMouseEvent,true);}
function oh(c,b,a){wh(c,b,a);nh(c,b,a);}
function nh(c,b,a){if(a&131072){b.addEventListener('DOMMouseScroll',$wnd.__dispatchEvent,false);}}
function ph(d,a){var b=a.cloneNode(true);var c=$doc.createElement('DIV');c.appendChild(b);outer=c.innerHTML;b.innerHTML='';return outer;}
function ih(){}
_=ih.prototype=new qh();_.tN=DE+'DOMImplMozilla';_.tI=0;function ni(a){ri=hb();return a;}
function pi(a){return qi(a);}
function qi(a){return new XMLHttpRequest();}
function mi(){}
_=mi.prototype=new su();_.tN=DE+'HTTPRequestImpl';_.tI=0;var ri=null;function un(d,b,a){var c=b.parentNode;if(!c){return;}c.insertBefore(a,b);c.removeChild(b);}
function vn(b,a){if(b.k!==null){un(b,b.k,a);}b.k=a;}
function wn(b,a){zn(b.k,a);}
function xn(b,a){Af(b.y(),a|kf(b.y()));}
function yn(){return this.k;}
function zn(a,b){uf(a,'className',b);}
function An(){if(this.k===null){return '(null handle)';}return Bf(this.k);}
function sn(){}
_=sn.prototype=new su();_.y=yn;_.tS=An;_.tN=EE+'UIObject';_.tI=0;_.k=null;function wo(a){if(ge(a.j,10)){fe(a.j,10).sb(a);}else if(a.j!==null){throw Et(new Dt(),"This widget's parent does not implement HasWidgets");}}
function xo(b,a){if(b.ab()){vf(b.y(),null);}vn(b,a);if(b.ab()){vf(a,b);}}
function yo(c,b){var a;a=c.j;if(b===null){if(a!==null&&a.ab()){c.jb();}c.j=null;}else{if(a!==null){throw Et(new Dt(),'Cannot set a new parent without first clearing the old parent');}c.j=b;if(b.ab()){c.gb();}}}
function zo(){}
function Ao(){}
function Bo(){return this.i;}
function Co(){if(this.ab()){throw Et(new Dt(),"Should only call onAttach when the widget is detached from the browser's document");}this.i=true;vf(this.y(),this);this.q();this.lb();}
function Do(a){}
function Eo(){if(!this.ab()){throw Et(new Dt(),"Should only call onDetach when the widget is attached to the browser's document");}try{this.nb();}finally{this.r();vf(this.y(),null);this.i=false;}}
function Fo(){}
function ap(){}
function bp(a){xo(this,a);}
function co(){}
_=co.prototype=new sn();_.q=zo;_.r=Ao;_.ab=Bo;_.gb=Co;_.hb=Do;_.jb=Eo;_.lb=Fo;_.nb=ap;_.vb=bp;_.tN=EE+'Widget';_.tI=17;_.i=false;_.j=null;function om(b,a){yo(a,b);}
function qm(b,a){yo(a,null);}
function rm(){var a,b;for(b=this.cb();io(b);){a=jo(b);a.gb();}}
function sm(){var a,b;for(b=this.cb();io(b);){a=jo(b);a.jb();}}
function tm(){}
function um(){}
function nm(){}
_=nm.prototype=new co();_.q=rm;_.r=sm;_.lb=tm;_.nb=um;_.tN=EE+'Panel';_.tI=18;function zj(a){a.f=no(new eo(),a);}
function Aj(a){zj(a);return a;}
function Bj(c,a,b){wo(a);oo(c.f,a);se(b,a.y());om(c,a);}
function Dj(b,c){var a;if(c.j!==b){return false;}qm(b,c);a=c.y();pf(lf(a),a);uo(b.f,c);return true;}
function Ej(){return so(this.f);}
function Fj(a){return Dj(this,a);}
function yj(){}
_=yj.prototype=new nm();_.cb=Ej;_.sb=Fj;_.tN=EE+'ComplexPanel';_.tI=19;function ti(a){Aj(a);a.vb(ve());zf(a.y(),'position','relative');zf(a.y(),'overflow','hidden');return a;}
function ui(a,b){Bj(a,b,a.y());}
function wi(a){zf(a,'left','');zf(a,'top','');zf(a,'position','');}
function xi(b){var a;a=Dj(this,b);if(a){wi(b.y());}return a;}
function si(){}
_=si.prototype=new yj();_.sb=xi;_.tN=EE+'AbsolutePanel';_.tI=20;function nk(){nk=BB;ep(),gp;}
function mk(b,a){ep(),gp;pk(b,a);return b;}
function ok(b,a){switch(df(a)){case 1:if(b.c!==null){wj(b.c,b);}break;case 4096:case 2048:break;case 128:case 512:case 256:break;}}
function pk(b,a){xo(b,a);xn(b,7041);}
function qk(a){if(this.c===null){this.c=uj(new tj());}oy(this.c,a);}
function rk(a){ok(this,a);}
function sk(a){pk(this,a);}
function lk(){}
_=lk.prototype=new co();_.l=qk;_.hb=rk;_.vb=sk;_.tN=EE+'FocusWidget';_.tI=21;_.c=null;function Bi(){Bi=BB;ep(),gp;}
function Ai(b,a){ep(),gp;mk(b,a);return b;}
function Ci(a){wf(this.y(),a);}
function zi(){}
_=zi.prototype=new lk();_.wb=Ci;_.tN=EE+'ButtonBase';_.tI=22;function aj(){aj=BB;ep(),gp;}
function Di(a){ep(),gp;Ai(a,ue());bj(a.y());wn(a,'gwt-Button');return a;}
function Ei(b,a){ep(),gp;Di(b);b.wb(a);return b;}
function Fi(c,a,b){ep(),gp;Ei(c,a);c.l(b);return c;}
function bj(b){aj();if(b.type=='submit'){try{b.setAttribute('type','button');}catch(a){}}}
function yi(){}
_=yi.prototype=new zi();_.tN=EE+'Button';_.tI=23;function dj(a){Aj(a);a.e=Ee();a.d=Be();se(a.e,a.d);a.vb(a.e);return a;}
function fj(c,b,a){uf(b,'align',a.a);}
function gj(c,b,a){zf(b,'verticalAlign',a.a);}
function cj(){}
_=cj.prototype=new yj();_.tN=EE+'CellPanel';_.tI=24;_.d=null;_.e=null;function lj(){lj=BB;ep(),gp;}
function ij(a){ep(),gp;jj(a,we());wn(a,'gwt-CheckBox');return a;}
function kj(b,a){ep(),gp;ij(b);oj(b,a);return b;}
function jj(b,a){var c;ep(),gp;Ai(b,Ae());b.a=a;b.b=ye();Af(b.a,kf(b.y()));Af(b.y(),0);se(b.y(),b.a);se(b.y(),b.b);c='check'+ ++sj;uf(b.a,'id',c);uf(b.b,'htmlFor',c);return b;}
function mj(b){var a;a=b.ab()?'checked':'defaultChecked';return gf(b.a,a);}
function nj(b,a){sf(b.a,'checked',a);sf(b.a,'defaultChecked',a);}
function oj(b,a){xf(b.b,a);}
function pj(){vf(this.a,this);}
function qj(){vf(this.a,null);nj(this,mj(this));}
function rj(a){wf(this.b,a);}
function hj(){}
_=hj.prototype=new zi();_.lb=pj;_.nb=qj;_.wb=rj;_.tN=EE+'CheckBox';_.tI=25;_.a=null;_.b=null;var sj=0;function hw(d,a,b){var c;while(a.F()){c=a.eb();if(b===null?c===null:b.eQ(c)){return a;}}return null;}
function jw(a){throw ew(new dw(),'add');}
function kw(b){var a;a=hw(this,this.cb(),b);return a!==null;}
function lw(){var a,b,c;c=Cu(new Bu());a=null;Fu(c,'[');b=this.cb();while(b.F()){if(a!==null){Fu(c,a);}else{a=', ';}Fu(c,xv(b.eb()));}Fu(c,']');return dv(c);}
function gw(){}
_=gw.prototype=new su();_.n=jw;_.p=kw;_.tS=lw;_.tN=eF+'AbstractCollection';_.tI=0;function vw(b,a){throw bu(new au(),'Index: '+a+', Size: '+b.b);}
function ww(b,a){throw ew(new dw(),'add');}
function xw(a){this.m(this.yb(),a);return true;}
function yw(e){var a,b,c,d,f;if(e===this){return true;}if(!ge(e,20)){return false;}f=fe(e,20);if(this.yb()!=f.yb()){return false;}c=this.cb();d=f.cb();while(c.F()){a=c.eb();b=d.eb();if(!(a===null?b===null:a.eQ(b))){return false;}}return true;}
function zw(){var a,b,c,d;c=1;a=31;b=this.cb();while(b.F()){d=b.eb();c=31*c+(d===null?0:d.hC());}return c;}
function Aw(){return ow(new nw(),this);}
function Bw(a){throw ew(new dw(),'remove');}
function mw(){}
_=mw.prototype=new gw();_.m=ww;_.n=xw;_.eQ=yw;_.hC=zw;_.cb=Aw;_.rb=Bw;_.tN=eF+'AbstractList';_.tI=26;function ly(a){{py(a);}}
function my(a){ly(a);return a;}
function ny(c,a,b){if(a<0||a>c.b){vw(c,a);}xy(c.a,a,b);++c.b;}
function oy(b,a){az(b.a,b.b++,a);return true;}
function py(a){a.a=gb();a.b=0;}
function ry(b,a){return ty(b,a)!=(-1);}
function sy(b,a){if(a<0||a>=b.b){vw(b,a);}return Cy(b.a,a);}
function ty(b,a){return uy(b,a,0);}
function uy(c,b,a){if(a<0){vw(c,a);}for(;a<c.b;++a){if(By(b,Cy(c.a,a))){return a;}}return (-1);}
function vy(c,a){var b;b=sy(c,a);Ey(c.a,a,1);--c.b;return b;}
function wy(c,b){var a;a=ty(c,b);if(a==(-1)){return false;}vy(c,a);return true;}
function yy(a,b){ny(this,a,b);}
function zy(a){return oy(this,a);}
function xy(a,b,c){a.splice(b,0,c);}
function Ay(a){return ry(this,a);}
function By(a,b){return a===b||a!==null&&a.eQ(b);}
function Dy(a){return sy(this,a);}
function Cy(a,b){return a[b];}
function Fy(a){return vy(this,a);}
function Ey(a,c,b){a.splice(c,b);}
function az(a,b,c){a[b]=c;}
function bz(){return this.b;}
function ky(){}
_=ky.prototype=new mw();_.m=yy;_.n=zy;_.p=Ay;_.D=Dy;_.rb=Fy;_.yb=bz;_.tN=eF+'ArrayList';_.tI=27;_.a=null;_.b=0;function uj(a){my(a);return a;}
function wj(d,c){var a,b;for(a=d.cb();a.F();){b=fe(a.eb(),8);b.ib(c);}}
function tj(){}
_=tj.prototype=new ky();_.tN=EE+'ClickListenerCollection';_.tI=28;function ck(a,b){if(a.h!==null){throw Et(new Dt(),'Composite.initWidget() may only be called once.');}wo(b);a.vb(b.y());a.h=b;yo(b,a);}
function dk(){if(this.h===null){throw Et(new Dt(),'initWidget() was never called in '+w(this));}return this.k;}
function ek(){if(this.h!==null){return this.h.ab();}return false;}
function fk(){this.h.gb();this.lb();}
function gk(){try{this.nb();}finally{this.h.jb();}}
function ak(){}
_=ak.prototype=new co();_.y=dk;_.ab=ek;_.gb=fk;_.jb=gk;_.tN=EE+'Composite';_.tI=29;_.h=null;function ik(a){Aj(a);a.vb(ve());return a;}
function jk(a,b){Bj(a,b,a.y());}
function hk(){}
_=hk.prototype=new yj();_.tN=EE+'FlowPanel';_.tI=30;function zk(){zk=BB;xk(new wk(),'center');Ak=xk(new wk(),'left');xk(new wk(),'right');}
var Ak;function xk(b,a){b.a=a;return b;}
function wk(){}
_=wk.prototype=new su();_.tN=EE+'HasHorizontalAlignment$HorizontalAlignmentConstant';_.tI=0;_.a=null;function al(){al=BB;Ek(new Dk(),'bottom');bl=Ek(new Dk(),'middle');cl=Ek(new Dk(),'top');}
var bl,cl;function Ek(a,b){a.a=b;return a;}
function Dk(){}
_=Dk.prototype=new su();_.tN=EE+'HasVerticalAlignment$VerticalAlignmentConstant';_.tI=0;_.a=null;function gl(a){a.a=(zk(),Ak);a.c=(al(),cl);}
function hl(a){dj(a);gl(a);a.b=De();se(a.d,a.b);uf(a.e,'cellSpacing','0');uf(a.e,'cellPadding','0');return a;}
function il(b,c){var a;a=kl(b);se(b.b,a);Bj(b,c,a);}
function kl(b){var a;a=Ce();fj(b,a,b.a);gj(b,a,b.c);return a;}
function ll(b,a){b.c=a;}
function ml(c){var a,b;b=lf(c.y());a=Dj(this,c);if(a){pf(this.b,b);}return a;}
function fl(){}
_=fl.prototype=new cj();_.sb=ml;_.tN=EE+'HorizontalPanel';_.tI=31;_.b=null;function pl(a){a.vb(ve());xn(a,131197);wn(a,'gwt-Label');return a;}
function ql(b,a){pl(b);sl(b,a);return b;}
function sl(b,a){xf(b.y(),a);}
function tl(a){switch(df(a)){case 1:break;case 4:case 8:case 64:case 16:case 32:break;case 131072:break;}}
function ol(){}
_=ol.prototype=new co();_.hb=tl;_.tN=EE+'Label';_.tI=32;function bm(){bm=BB;ep(),gp;lm=new vl();}
function Cl(b,a){bm();mk(b,ze(a));xn(b,1024);wn(b,'gwt-ListBox');return b;}
function Dl(b,a){gm(b,a,(-1));}
function El(b,a,c){hm(b,a,c,(-1));}
function Fl(b,a){if(a<0||a>=cm(b)){throw new au();}}
function am(a){wl(lm,a.y());}
function cm(a){return yl(lm,a.y());}
function dm(b,a){Fl(b,a);return zl(lm,b.y(),a);}
function em(a){return hf(a.y(),'selectedIndex');}
function fm(b,a){Fl(b,a);return Al(lm,b.y(),a);}
function gm(c,b,a){hm(c,b,b,a);}
function hm(c,b,d,a){nf(c.y(),b,d,a);}
function im(b,a){Fl(b,a);Bl(lm,b.y(),a);}
function jm(c,a,b){Fl(c,a);if(b===null){throw mu(new lu(),'Cannot set an option to have null text');}yf(c.y(),b,a);}
function km(a,b){tf(a.y(),'size',b);}
function mm(a){if(df(a)==1024){}else{ok(this,a);}}
function ul(){}
_=ul.prototype=new lk();_.hb=mm;_.tN=EE+'ListBox';_.tI=33;var lm;function wl(b,a){a.options.length=0;}
function yl(b,a){return a.options.length;}
function zl(c,b,a){return b.options[a].text;}
function Al(c,b,a){return b.options[a].value;}
function Bl(c,b,a){b.options[a]=null;}
function vl(){}
_=vl.prototype=new su();_.tN=EE+'ListBox$Impl';_.tI=0;function Bm(){Bm=BB;an=aA(new ez());}
function Am(b,a){Bm();ti(b);if(a===null){a=Cm();}b.vb(a);b.gb();return b;}
function Dm(){Bm();return Em(null);}
function Em(c){Bm();var a,b;b=fe(hA(an,c),9);if(b!==null){return b;}a=null;if(an.c==0){Fm();}jA(an,c,b=Am(new vm(),a));return b;}
function Cm(){Bm();return $doc.body;}
function Fm(){Bm();Cg(new wm());}
function vm(){}
_=vm.prototype=new si();_.tN=EE+'RootPanel';_.tI=34;var an;function ym(){var a,b;for(b=px(Ex((Bm(),an)));wx(b);){a=fe(xx(b),9);if(a.ab()){a.jb();}}}
function zm(){return null;}
function wm(){}
_=wm.prototype=new su();_.ob=ym;_.pb=zm;_.tN=EE+'RootPanel$1';_.tI=35;function ln(){ln=BB;ep(),gp;}
function kn(b,a){ep(),gp;mk(b,a);xn(b,1024);return b;}
function mn(a){return jf(a.y(),'value');}
function nn(a){if(this.a===null){this.a=uj(new tj());}oy(this.a,a);}
function on(a){var b;ok(this,a);b=df(a);if(b==1){if(this.a!==null){wj(this.a,this);}}else{}}
function jn(){}
_=jn.prototype=new lk();_.l=nn;_.hb=on;_.tN=EE+'TextBoxBase';_.tI=36;_.a=null;function qn(){qn=BB;ep(),gp;}
function pn(a){ep(),gp;kn(a,xe());wn(a,'gwt-TextBox');return a;}
function rn(b,a){tf(b.y(),'size',a);}
function hn(){}
_=hn.prototype=new jn();_.tN=EE+'TextBox';_.tI=37;function Cn(a){a.a=(zk(),Ak);a.b=(al(),cl);}
function Dn(a){dj(a);Cn(a);uf(a.e,'cellSpacing','0');uf(a.e,'cellPadding','0');return a;}
function En(b,d){var a,c;c=De();a=ao(b);se(c,a);se(b.d,c);Bj(b,d,a);}
function ao(b){var a;a=Ce();fj(b,a,b.a);gj(b,a,b.b);return a;}
function bo(c){var a,b;b=lf(c.y());a=Dj(this,c);if(a){pf(this.d,lf(b));}return a;}
function Bn(){}
_=Bn.prototype=new cj();_.sb=bo;_.tN=EE+'VerticalPanel';_.tI=38;function no(b,a){b.b=a;b.a=Fd('[Lcom.google.gwt.user.client.ui.Widget;',[0],[12],[4],null);return b;}
function oo(a,b){ro(a,b,a.c);}
function qo(b,c){var a;for(a=0;a<b.c;++a){if(b.a[a]===c){return a;}}return (-1);}
function ro(d,e,a){var b,c;if(a<0||a>d.c){throw new au();}if(d.c==d.a.a){c=Fd('[Lcom.google.gwt.user.client.ui.Widget;',[0],[12],[d.a.a*2],null);for(b=0;b<d.a.a;++b){be(c,b,d.a[b]);}d.a=c;}++d.c;for(b=d.c-1;b>a;--b){be(d.a,b,d.a[b-1]);}be(d.a,a,e);}
function so(a){return go(new fo(),a);}
function to(c,b){var a;if(b<0||b>=c.c){throw new au();}--c.c;for(a=b;a<c.c;++a){be(c.a,a,c.a[a+1]);}be(c.a,c.c,null);}
function uo(b,c){var a;a=qo(b,c);if(a==(-1)){throw new kB();}to(b,a);}
function eo(){}
_=eo.prototype=new su();_.tN=EE+'WidgetCollection';_.tI=0;_.a=null;_.b=null;_.c=0;function go(b,a){b.b=a;return b;}
function io(a){return a.a<a.b.c-1;}
function jo(a){if(a.a>=a.b.c){throw new kB();}return a.b.a[++a.a];}
function ko(){return io(this);}
function lo(){return jo(this);}
function mo(){if(this.a<0||this.a>=this.b.c){throw new Dt();}this.b.b.sb(this.b.a[this.a--]);}
function fo(){}
_=fo.prototype=new su();_.F=ko;_.eb=lo;_.qb=mo;_.tN=EE+'WidgetCollection$WidgetIterator';_.tI=0;_.a=(-1);function ep(){ep=BB;fp=dp(new cp());gp=fp;}
function dp(a){ep();return a;}
function cp(){}
_=cp.prototype=new su();_.tN=FE+'FocusImpl';_.tI=0;var fp,gp;function mp(c,a,b){yu(c,b);return c;}
function lp(){}
_=lp.prototype=new xu();_.tN=aF+'DOMException';_.tI=39;function xp(){xp=BB;yp=(os(),Es);}
function zp(a){xp();return ps(yp,a);}
var yp;function nq(b,a){b.a=a;return b;}
function oq(a,b){return b;}
function qq(a){if(ge(a,15)){return te(oq(this,this.a),oq(this,fe(a,15).a));}return false;}
function mq(){}
_=mq.prototype=new su();_.eQ=qq;_.tN=bF+'DOMItem';_.tI=40;_.a=null;function lr(b,a){nq(b,a);return b;}
function nr(a){return gr(new fr(),rs(a.a));}
function or(a){return ur(new tr(),ss(a.a));}
function pr(a){return ys(a.a);}
function qr(a){return Cs(a.a);}
function rr(a){return Ds(a.a);}
function sr(a){var b;if(a===null){return null;}b=zs(a);switch(b){case 2:return Bp(new Ap(),a);case 4:return bq(new aq(),a);case 8:return jq(new iq(),a);case 11:return wq(new vq(),a);case 9:return Aq(new zq(),a);case 1:return Fq(new Eq(),a);case 7:return Dr(new Cr(),a);case 3:return cs(new bs(),a);default:return lr(new kr(),a);}}
function kr(){}
_=kr.prototype=new mq();_.tN=bF+'NodeImpl';_.tI=41;function Bp(b,a){lr(b,a);return b;}
function Dp(a){return xs(a.a);}
function Ep(a){return Bs(a.a);}
function Fp(){var a;a=Cu(new Bu());Fu(a,' '+Dp(this));Fu(a,'="');Fu(a,Ep(this));Fu(a,'"');return dv(a);}
function Ap(){}
_=Ap.prototype=new kr();_.tS=Fp;_.tN=bF+'AttrImpl';_.tI=42;function fq(b,a){lr(b,a);return b;}
function hq(a){return ts(a.a);}
function eq(){}
_=eq.prototype=new kr();_.tN=bF+'CharacterDataImpl';_.tI=43;function cs(b,a){fq(b,a);return b;}
function es(){var a,b,c;a=Cu(new Bu());c=lv(hq(this),'(?=[;&<>\'"])',(-1));for(b=0;b<c.a;b++){if(mv(c[b],';')){Fu(a,'&semi;');Fu(a,nv(c[b],1));}else if(mv(c[b],'&')){Fu(a,'&amp;');Fu(a,nv(c[b],1));}else if(mv(c[b],'"')){Fu(a,'&quot;');Fu(a,nv(c[b],1));}else if(mv(c[b],"'")){Fu(a,'&apos;');Fu(a,nv(c[b],1));}else if(mv(c[b],'<')){Fu(a,'&lt;');Fu(a,nv(c[b],1));}else if(mv(c[b],'>')){Fu(a,'&gt;');Fu(a,nv(c[b],1));}else{Fu(a,c[b]);}}return dv(a);}
function bs(){}
_=bs.prototype=new eq();_.tS=es;_.tN=bF+'TextImpl';_.tI=44;function bq(b,a){cs(b,a);return b;}
function dq(){var a;a=Du(new Bu(),'<![CDATA[');Fu(a,hq(this));Fu(a,']]>');return dv(a);}
function aq(){}
_=aq.prototype=new bs();_.tS=dq;_.tN=bF+'CDATASectionImpl';_.tI=45;function jq(b,a){fq(b,a);return b;}
function lq(){var a;a=Du(new Bu(),'<!--');Fu(a,hq(this));Fu(a,'-->');return dv(a);}
function iq(){}
_=iq.prototype=new eq();_.tS=lq;_.tN=bF+'CommentImpl';_.tI=46;function sq(c,a,b){mp(c,12,'Failed to parse: '+uq(a));Fv(c,b);return c;}
function uq(a){return ov(a,0,iu(jv(a),128));}
function rq(){}
_=rq.prototype=new lp();_.tN=bF+'DOMParseException';_.tI=47;function wq(b,a){lr(b,a);return b;}
function yq(){var a,b;a=Cu(new Bu());for(b=0;b<or(this).B();b++){Eu(a,or(this).bb(b));}return dv(a);}
function vq(){}
_=vq.prototype=new kr();_.tS=yq;_.tN=bF+'DocumentFragmentImpl';_.tI=48;function Aq(b,a){lr(b,a);return b;}
function Cq(){return fe(sr(us(this.a)),16);}
function Dq(){var a,b,c;a=Cu(new Bu());b=or(this);for(c=0;c<b.B();c++){Fu(a,b.bb(c).tS());}return dv(a);}
function zq(){}
_=zq.prototype=new kr();_.w=Cq;_.tS=Dq;_.tN=bF+'DocumentImpl';_.tI=49;function Fq(b,a){lr(b,a);return b;}
function br(a){return As(a.a);}
function cr(a){return qs(this.a,a);}
function dr(a){return ur(new tr(),vs(this.a,a));}
function er(){var a;a=Du(new Bu(),'<');Fu(a,br(this));if(qr(this)){Fu(a,yr(nr(this)));}if(rr(this)){Fu(a,'>');Fu(a,yr(or(this)));Fu(a,'<\/');Fu(a,br(this));Fu(a,'>');}else{Fu(a,'/>');}return dv(a);}
function Eq(){}
_=Eq.prototype=new kr();_.v=cr;_.z=dr;_.tS=er;_.tN=bF+'ElementImpl';_.tI=50;function ur(b,a){nq(b,a);return b;}
function wr(a){return ws(a.a);}
function xr(b,a){return sr(Fs(b.a,a));}
function yr(c){var a,b;a=Cu(new Bu());for(b=0;b<c.B();b++){Fu(a,c.bb(b).tS());}return dv(a);}
function zr(){return wr(this);}
function Ar(a){return xr(this,a);}
function Br(){return yr(this);}
function tr(){}
_=tr.prototype=new mq();_.B=zr;_.bb=Ar;_.tS=Br;_.tN=bF+'NodeListImpl';_.tI=51;function gr(b,a){ur(b,a);return b;}
function ir(){return wr(this);}
function jr(a){return xr(this,a);}
function fr(){}
_=fr.prototype=new tr();_.B=ir;_.bb=jr;_.tN=bF+'NamedNodeMapImpl';_.tI=52;function Dr(b,a){lr(b,a);return b;}
function Fr(a){return ts(a.a);}
function as(){var a;a=Du(new Bu(),'<?');Fu(a,pr(this));Fu(a,' ');Fu(a,Fr(this));Fu(a,'?>');return dv(a);}
function Cr(){}
_=Cr.prototype=new kr();_.tS=as;_.tN=bF+'ProcessingInstructionImpl';_.tI=53;function os(){os=BB;Es=is(new gs());}
function ns(a){os();return a;}
function ps(e,c){var a,d;try{return fe(sr(ls(e,c)),17);}catch(a){a=ne(a);if(ge(a,18)){d=a;throw sq(new rq(),c,d);}else throw a;}}
function qs(b,a){os();return b.getAttribute(a);}
function rs(a){os();return a.attributes;}
function ss(b){os();var a=b.childNodes;return a==null?null:a;}
function ts(a){os();return a.data;}
function us(a){os();return a.documentElement;}
function vs(a,b){os();return ks(Es,a,b);}
function ws(a){os();return a.length;}
function xs(a){os();return a.name;}
function ys(a){os();var b=a.nodeName;return b==null?null:b;}
function zs(a){os();var b=a.nodeType;return b==null?-1:b;}
function As(a){os();return a.tagName;}
function Bs(a){os();return a.value;}
function Cs(a){os();return a.attributes.length!=0;}
function Ds(a){os();return a.hasChildNodes();}
function Fs(c,a){os();if(a>=c.length){return null;}var b=c.item(a);return b==null?null:b;}
function fs(){}
_=fs.prototype=new su();_.tN=bF+'XMLParserImpl';_.tI=0;var Es;function js(){js=BB;os();}
function hs(a){a.a=ms();}
function is(a){js();ns(a);hs(a);return a;}
function ks(c,a,b){return a.getElementsByTagNameNS('*',b);}
function ls(e,a){var b=e.a;var c=b.parseFromString(a,'text/xml');var d=c.documentElement;if(d.tagName=='parsererror'&&d.namespaceURI=='http://www.mozilla.org/newlayout/xml/parsererror.xml'){throw new Error(d.firstChild.data);}return c;}
function ms(){js();return new DOMParser();}
function gs(){}
_=gs.prototype=new fs();_.tN=bF+'XMLParserImplStandard';_.tI=0;function dt(){}
_=dt.prototype=new su();_.tN=cF+'OutputStream';_.tI=0;function bt(){}
_=bt.prototype=new dt();_.tN=cF+'FilterOutputStream';_.tI=0;function ft(){}
_=ft.prototype=new bt();_.tN=cF+'PrintStream';_.tI=0;function ht(){}
_=ht.prototype=new xu();_.tN=dF+'ArrayStoreException';_.tI=54;function lt(){lt=BB;mt=kt(new jt(),false);nt=kt(new jt(),true);}
function kt(a,b){lt();a.a=b;return a;}
function ot(a){return ge(a,19)&&fe(a,19).a==this.a;}
function pt(){var a,b;b=1231;a=1237;return this.a?1231:1237;}
function qt(){return this.a?'true':'false';}
function rt(a){lt();return a?nt:mt;}
function jt(){}
_=jt.prototype=new su();_.eQ=ot;_.hC=pt;_.tS=qt;_.tN=dF+'Boolean';_.tI=55;_.a=false;var mt,nt;function tt(){}
_=tt.prototype=new xu();_.tN=dF+'ClassCastException';_.tI=56;function Bt(b,a){yu(b,a);return b;}
function At(){}
_=At.prototype=new xu();_.tN=dF+'IllegalArgumentException';_.tI=57;function Et(b,a){yu(b,a);return b;}
function Dt(){}
_=Dt.prototype=new xu();_.tN=dF+'IllegalStateException';_.tI=58;function bu(b,a){yu(b,a);return b;}
function au(){}
_=au.prototype=new xu();_.tN=dF+'IndexOutOfBoundsException';_.tI=59;function pu(){pu=BB;{ru();}}
function ru(){pu();qu=/^[+-]?\d*\.?\d*(e[+-]?\d+)?$/i;}
var qu=null;function eu(){eu=BB;pu();}
function fu(a){eu();return wv(a);}
function iu(a,b){return a<b?a:b;}
function ju(){}
_=ju.prototype=new xu();_.tN=dF+'NegativeArraySizeException';_.tI=60;function mu(b,a){yu(b,a);return b;}
function lu(){}
_=lu.prototype=new xu();_.tN=dF+'NullPointerException';_.tI=61;function hv(b,a){if(!ge(a,1))return false;return rv(b,a);}
function iv(b,a){return b.indexOf(a);}
function jv(a){return a.length;}
function kv(b,a){return lv(b,a,0);}
function lv(j,i,g){var a=new RegExp(i,'g');var h=[];var b=0;var k=j;var e=null;while(true){var f=a.exec(k);if(f==null||(k==''||b==g-1&&g>0)){h[b]=k;break;}else{h[b]=k.substring(0,f.index);k=k.substring(f.index+f[0].length,k.length);a.lastIndex=0;if(e==k){h[b]=k.substring(0,1);k=k.substring(1);}e=k;b++;}}if(g==0){for(var c=h.length-1;c>=0;c--){if(h[c]!=''){h.splice(c+1,h.length-(c+1));break;}}}var d=qv(h.length);var c=0;for(c=0;c<h.length;++c){d[c]=h[c];}return d;}
function mv(b,a){return iv(b,a)==0;}
function nv(b,a){return b.substr(a,b.length-a);}
function ov(c,a,b){return c.substr(a,b-a);}
function pv(c){var a=c.replace(/^(\s*)/,'');var b=a.replace(/\s*$/,'');return b;}
function qv(a){return Fd('[Ljava.lang.String;',[0],[1],[a],null);}
function rv(a,b){return String(a)==b;}
function sv(a){return hv(this,a);}
function uv(){var a=tv;if(!a){a=tv={};}var e=':'+this;var b=a[e];if(b==null){b=0;var f=this.length;var d=f<64?1:f/32|0;for(var c=0;c<f;c+=d){b<<=1;b+=this.charCodeAt(c);}b|=0;a[e]=b;}return b;}
function vv(){return this;}
function wv(a){return ''+a;}
function xv(a){return a!==null?a.tS():'null';}
_=String.prototype;_.eQ=sv;_.hC=uv;_.tS=vv;_.tN=dF+'String';_.tI=2;var tv=null;function Cu(a){av(a);return a;}
function Du(b,a){bv(b,a);return b;}
function Eu(a,b){return Fu(a,xv(b));}
function Fu(c,d){if(d===null){d='null';}var a=c.js.length-1;var b=c.js[a].length;if(c.length>b*b){c.js[a]=c.js[a]+d;}else{c.js.push(d);}c.length+=d.length;return c;}
function av(a){bv(a,'');}
function bv(b,a){b.js=[a];b.length=a.length;}
function dv(a){a.fb();return a.js[0];}
function ev(){if(this.js.length>1){this.js=[this.js.join('')];this.length=this.js[0].length;}}
function fv(){return dv(this);}
function Bu(){}
_=Bu.prototype=new su();_.fb=ev;_.tS=fv;_.tN=dF+'StringBuffer';_.tI=0;function zv(){zv=BB;Av=new ft();}
function Bv(a){zv();return C(a);}
var Av;function ew(b,a){yu(b,a);return b;}
function dw(){}
_=dw.prototype=new xu();_.tN=dF+'UnsupportedOperationException';_.tI=62;function ow(b,a){b.c=a;return b;}
function qw(a){return a.a<a.c.yb();}
function rw(){return qw(this);}
function sw(){if(!qw(this)){throw new kB();}return this.c.D(this.b=this.a++);}
function tw(){if(this.b<0){throw new Dt();}this.c.rb(this.b);this.a=this.b;this.b=(-1);}
function nw(){}
_=nw.prototype=new su();_.F=rw;_.eb=sw;_.qb=tw;_.tN=eF+'AbstractList$IteratorImpl';_.tI=0;_.a=0;_.b=(-1);function Cx(f,d,e){var a,b,c;for(b=Bz(f.s());tz(b);){a=uz(b);c=a.A();if(d===null?c===null:d.eQ(c)){if(e){vz(b);}return a;}}return null;}
function Dx(b){var a;a=b.s();return Ew(new Dw(),b,a);}
function Ex(b){var a;a=gA(b);return nx(new mx(),b,a);}
function Fx(a){return Cx(this,a,false)!==null;}
function ay(d){var a,b,c,e,f,g,h;if(d===this){return true;}if(!ge(d,21)){return false;}f=fe(d,21);c=Dx(this);e=f.db();if(!hy(c,e)){return false;}for(a=ax(c);hx(a);){b=ix(a);h=this.E(b);g=f.E(b);if(h===null?g!==null:!h.eQ(g)){return false;}}return true;}
function by(b){var a;a=Cx(this,b,false);return a===null?null:a.C();}
function cy(){var a,b,c;b=0;for(c=Bz(this.s());tz(c);){a=uz(c);b+=a.hC();}return b;}
function dy(){return Dx(this);}
function ey(){var a,b,c,d;d='{';a=false;for(c=Bz(this.s());tz(c);){b=uz(c);if(a){d+=', ';}else{a=true;}d+=xv(b.A());d+='=';d+=xv(b.C());}return d+'}';}
function Cw(){}
_=Cw.prototype=new su();_.o=Fx;_.eQ=ay;_.E=by;_.hC=cy;_.db=dy;_.tS=ey;_.tN=eF+'AbstractMap';_.tI=63;function hy(e,b){var a,c,d;if(b===e){return true;}if(!ge(b,22)){return false;}c=fe(b,22);if(c.yb()!=e.yb()){return false;}for(a=c.cb();a.F();){d=a.eb();if(!e.p(d)){return false;}}return true;}
function iy(a){return hy(this,a);}
function jy(){var a,b,c;a=0;for(b=this.cb();b.F();){c=b.eb();if(c!==null){a+=c.hC();}}return a;}
function fy(){}
_=fy.prototype=new gw();_.eQ=iy;_.hC=jy;_.tN=eF+'AbstractSet';_.tI=64;function Ew(b,a,c){b.a=a;b.b=c;return b;}
function ax(b){var a;a=Bz(b.b);return fx(new ex(),b,a);}
function bx(a){return this.a.o(a);}
function cx(){return ax(this);}
function dx(){return this.b.a.c;}
function Dw(){}
_=Dw.prototype=new fy();_.p=bx;_.cb=cx;_.yb=dx;_.tN=eF+'AbstractMap$1';_.tI=65;function fx(b,a,c){b.a=c;return b;}
function hx(a){return a.a.F();}
function ix(b){var a;a=b.a.eb();return a.A();}
function jx(){return hx(this);}
function kx(){return ix(this);}
function lx(){this.a.qb();}
function ex(){}
_=ex.prototype=new su();_.F=jx;_.eb=kx;_.qb=lx;_.tN=eF+'AbstractMap$2';_.tI=0;function nx(b,a,c){b.a=a;b.b=c;return b;}
function px(b){var a;a=Bz(b.b);return ux(new tx(),b,a);}
function qx(a){return fA(this.a,a);}
function rx(){return px(this);}
function sx(){return this.b.a.c;}
function mx(){}
_=mx.prototype=new gw();_.p=qx;_.cb=rx;_.yb=sx;_.tN=eF+'AbstractMap$3';_.tI=0;function ux(b,a,c){b.a=c;return b;}
function wx(a){return a.a.F();}
function xx(a){var b;b=a.a.eb().C();return b;}
function yx(){return wx(this);}
function zx(){return xx(this);}
function Ax(){this.a.qb();}
function tx(){}
_=tx.prototype=new su();_.F=yx;_.eb=zx;_.qb=Ax;_.tN=eF+'AbstractMap$4';_.tI=0;function dA(){dA=BB;lA=rA();}
function Fz(a){{cA(a);}}
function aA(a){dA();Fz(a);return a;}
function bA(a,b){dA();Fz(a);iA(a,b);return a;}
function cA(a){a.a=gb();a.d=ib();a.b=ke(lA,cb);a.c=0;}
function eA(b,a){if(ge(a,1)){return vA(b.d,fe(a,1))!==lA;}else if(a===null){return b.b!==lA;}else{return uA(b.a,a,a.hC())!==lA;}}
function fA(a,b){if(a.b!==lA&&tA(a.b,b)){return true;}else if(qA(a.d,b)){return true;}else if(oA(a.a,b)){return true;}return false;}
function gA(a){return zz(new pz(),a);}
function hA(c,a){var b;if(ge(a,1)){b=vA(c.d,fe(a,1));}else if(a===null){b=c.b;}else{b=uA(c.a,a,a.hC());}return b===lA?null:b;}
function jA(c,a,d){var b;if(ge(a,1)){b=yA(c.d,fe(a,1),d);}else if(a===null){b=c.b;c.b=d;}else{b=xA(c.a,a,d,a.hC());}if(b===lA){++c.c;return null;}else{return b;}}
function iA(d,c){var a,b;b=Bz(gA(c));while(tz(b)){a=uz(b);jA(d,a.A(),a.C());}}
function kA(c,a){var b;if(ge(a,1)){b=AA(c.d,fe(a,1));}else if(a===null){b=c.b;c.b=ke(lA,cb);}else{b=zA(c.a,a,a.hC());}if(b===lA){return null;}else{--c.c;return b;}}
function mA(e,c){dA();for(var d in e){if(d==parseInt(d)){var a=e[d];for(var f=0,b=a.length;f<b;++f){c.n(a[f]);}}}}
function nA(d,a){dA();for(var c in d){if(c.charCodeAt(0)==58){var e=d[c];var b=iz(c.substring(1),e);a.n(b);}}}
function oA(f,h){dA();for(var e in f){if(e==parseInt(e)){var a=f[e];for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.C();if(tA(h,d)){return true;}}}}return false;}
function pA(a){return eA(this,a);}
function qA(c,d){dA();for(var b in c){if(b.charCodeAt(0)==58){var a=c[b];if(tA(d,a)){return true;}}}return false;}
function rA(){dA();}
function sA(){return gA(this);}
function tA(a,b){dA();if(a===b){return true;}else if(a===null){return false;}else{return a.eQ(b);}}
function wA(a){return hA(this,a);}
function uA(f,h,e){dA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(tA(h,d)){return c.C();}}}}
function vA(b,a){dA();return b[':'+a];}
function xA(f,h,j,e){dA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(tA(h,d)){var i=c.C();c.xb(j);return i;}}}else{a=f[e]=[];}var c=iz(h,j);a.push(c);}
function yA(c,a,d){dA();a=':'+a;var b=c[a];c[a]=d;return b;}
function zA(f,h,e){dA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(tA(h,d)){if(a.length==1){delete f[e];}else{a.splice(g,1);}return c.C();}}}}
function AA(c,a){dA();a=':'+a;var b=c[a];delete c[a];return b;}
function ez(){}
_=ez.prototype=new Cw();_.o=pA;_.s=sA;_.E=wA;_.tN=eF+'HashMap';_.tI=66;_.a=null;_.b=null;_.c=0;_.d=null;var lA;function gz(b,a,c){b.a=a;b.b=c;return b;}
function iz(a,b){return gz(new fz(),a,b);}
function jz(b){var a;if(ge(b,23)){a=fe(b,23);if(tA(this.a,a.A())&&tA(this.b,a.C())){return true;}}return false;}
function kz(){return this.a;}
function lz(){return this.b;}
function mz(){var a,b;a=0;b=0;if(this.a!==null){a=this.a.hC();}if(this.b!==null){b=this.b.hC();}return a^b;}
function nz(a){var b;b=this.b;this.b=a;return b;}
function oz(){return this.a+'='+this.b;}
function fz(){}
_=fz.prototype=new su();_.eQ=jz;_.A=kz;_.C=lz;_.hC=mz;_.xb=nz;_.tS=oz;_.tN=eF+'HashMap$EntryImpl';_.tI=67;_.a=null;_.b=null;function zz(b,a){b.a=a;return b;}
function Bz(a){return rz(new qz(),a.a);}
function Cz(c){var a,b,d;if(ge(c,23)){a=fe(c,23);b=a.A();if(eA(this.a,b)){d=hA(this.a,b);return tA(a.C(),d);}}return false;}
function Dz(){return Bz(this);}
function Ez(){return this.a.c;}
function pz(){}
_=pz.prototype=new fy();_.p=Cz;_.cb=Dz;_.yb=Ez;_.tN=eF+'HashMap$EntrySet';_.tI=68;function rz(c,b){var a;c.c=b;a=my(new ky());if(c.c.b!==(dA(),lA)){oy(a,gz(new fz(),null,c.c.b));}nA(c.c.d,a);mA(c.c.a,a);c.a=a.cb();return c;}
function tz(a){return a.a.F();}
function uz(a){return a.b=fe(a.a.eb(),23);}
function vz(a){if(a.b===null){throw Et(new Dt(),'Must call next() before remove().');}else{a.a.qb();kA(a.c,a.b.A());a.b=null;}}
function wz(){return tz(this);}
function xz(){return uz(this);}
function yz(){vz(this);}
function qz(){}
_=qz.prototype=new su();_.F=wz;_.eb=xz;_.qb=yz;_.tN=eF+'HashMap$EntrySetIterator';_.tI=0;_.a=null;_.b=null;function CA(a){a.a=aA(new ez());return a;}
function EA(a){var b;b=jA(this.a,a,rt(true));return b===null;}
function FA(a){return eA(this.a,a);}
function aB(){return ax(Dx(this.a));}
function bB(){return this.a.c;}
function cB(){return Dx(this.a).tS();}
function BA(){}
_=BA.prototype=new fy();_.n=EA;_.p=FA;_.cb=aB;_.yb=bB;_.tS=cB;_.tN=eF+'HashSet';_.tI=69;_.a=null;function iB(d,c,a,b){yu(d,c);return d;}
function hB(){}
_=hB.prototype=new xu();_.tN=eF+'MissingResourceException';_.tI=70;function kB(){}
_=kB.prototype=new xu();_.tN=eF+'NoSuchElementException';_.tI=71;function pB(a){a.a=my(new ky());return a;}
function qB(b,a){return oy(b.a,a);}
function sB(b,a){return tB(b,a);}
function tB(b,a){return sy(b.a,a);}
function uB(a,b){ny(this.a,a,b);}
function vB(a){return qB(this,a);}
function wB(a){return ry(this.a,a);}
function xB(a){return tB(this,a);}
function yB(){return this.a.cb();}
function zB(a){return vy(this.a,a);}
function AB(){return this.a.b;}
function oB(){}
_=oB.prototype=new mw();_.m=uB;_.n=vB;_.p=wB;_.D=xB;_.cb=yB;_.rb=zB;_.yb=AB;_.tN=eF+'Vector';_.tI=72;_.a=null;function tC(g,h){var a,c,d,e,f;c=EC(new CC(),h);try{e=vE(c);f=lC(new kC(),g,e,c);rg(f,1);}catch(a){a=ne(a);if(ge(a,25)){d=a;aw(d);}else throw a;}}
function uC(g,h){var a,c,d,e,f;c=hD(new fD(),h);try{e=vE(c);f=pC(new oC(),g,e,c);rg(f,1);}catch(a){a=ne(a);if(ge(a,25)){d=a;Dg('Exception: '+d.b);aw(d);}else throw a;}}
function vC(r){var a,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,s;k='DEFAULT-identities-and-usecases.xml';l='DEFAULT-policy.xml';f='DEFAULT-cancel.html';m='DEFAULT-save-policy.xml';try{h=td('getURLs');k=qd(h,'identities-url');l=qd(h,'policy-url');f=qd(h,'cancel-url');m=qd(h,'save-url');}catch(a){a=ne(a);if(ge(a,24)){i=a;Dg('Exception: '+i.b);}else throw a;}uC(r,l);tC(r,k);s=Dn(new Bn());ui(Dm(),s);p=Dn(new Bn());En(s,p);q=pn(new hn());rn(q,30);En(p,q);o=Fi(new yi(),'Save User or Group',EB(new DB(),r,q));En(p,o);j=hl(new fl());ll(j,(al(),bl));En(s,j);d=hl(new fl());En(s,d);n=m;r.g=Fi(new yi(),'Save Policy',cC(new bC(),r,n));wn(r.g,'gwt-wyona-SaveButton');il(d,r.g);g=f;e=Fi(new yi(),'Cancel',gC(new fC(),r,g));wn(r.g,'gwt-wyona-CancelButton');il(d,e);r.b=yD(new wD(),r.j,r.i,r.a);r.d=ED(new CD(),r.j,r.e,r.c,r.h);c=yC(new wC(),r.b.a,r.d.c,r.d);wn(c,'gwt-wyona-AddRemoveWidget');il(j,r.b);il(j,c);il(j,r.d);}
function CB(){}
_=CB.prototype=new su();_.tN=fF+'AccessPolicyEditor';_.tI=0;_.a=null;_.b=null;_.c=null;_.d=null;_.e=null;_.f=null;_.g=null;_.h=true;_.i=null;_.j=10;function EB(b,a,c){b.a=a;b.b=c;return b;}
function aC(d){var a,b,c;b=cm(this.a.b.a);for(a=0;a<b;a++){c=dm(this.a.b.a,a);if(iv(c,mn(this.b))>=0)Dg('Result: '+c);}}
function DB(){}
_=DB.prototype=new su();_.ib=aC;_.tN=fF+'AccessPolicyEditor$1';_.tI=73;function cC(b,a,c){b.a=a;b.b=c;return b;}
function eC(f){var a,c,d,e;c=oD(new nD(),this.b);try{e=qD(c,gE(this.a.d),bE(this.a.d),fE(this.a.d));}catch(a){a=ne(a);if(ge(a,25)){d=a;Dg('Exception: '+d.b);}else throw a;}}
function bC(){}
_=bC.prototype=new su();_.ib=eC;_.tN=fF+'AccessPolicyEditor$2';_.tI=74;function gC(b,a,c){b.a=c;return b;}
function iC(a,b){$wnd.location.href=b;}
function jC(a){Dg('Redirect to '+this.a);iC(this,this.a);}
function fC(){}
_=fC.prototype=new su();_.ib=jC;_.tN=fF+'AccessPolicyEditor$3';_.tI=75;function mC(){mC=BB;og();}
function lC(b,a,d,c){mC();b.a=a;b.c=d;b.b=c;mg(b);return b;}
function nC(){if(xc(this.c)){qg(this,10);}else{this.a.i=dD(this.b);this.a.a=bD(this.b);this.a.f=cD(this.b);AD(this.a.b,this.a.j,this.a.i,this.a.a);ng(this);Dg('Identities have been loaded!');}}
function kC(){}
_=kC.prototype=new hg();_.ub=nC;_.tN=fF+'AccessPolicyEditor$4';_.tI=76;function qC(){qC=BB;og();}
function pC(b,a,d,c){qC();b.a=a;b.c=d;b.b=c;mg(b);return b;}
function rC(){if(xc(this.c)){qg(this,10);}else{this.a.e=lD(this.b);this.a.c=kD(this.b);iE(this.a.d,this.a.j,this.a.e,this.a.c);this.a.h=this.b.b;jE(this.a.d,this.a.h);ng(this);Dg('Policy has been loaded!');}}
function oC(){}
_=oC.prototype=new hg();_.ub=rC;_.tN=fF+'AccessPolicyEditor$5';_.tI=77;function xC(a){a.b=ik(new hk());}
function yC(d,a,c,b){xC(d);ck(d,d.b);d.e=Fi(new yi(),'<',d);jk(d.b,d.e);d.a=Fi(new yi(),'>',d);jk(d.b,d.a);d.c=a;d.d=c;return d;}
function AC(b,a){if(iv(a,'(')>0){return ov(a,0,iv(a,'('));}else{return a;}}
function BC(c){var a,b;if(c===this.a){a=em(this.c);if(a>=0){b=fm(this.c,a);Dg('Add selected identity '+b+' to policy');im(this.c,a);El(this.d,ov(b,0,1)+': (-,-) '+pv(nv(b,2)),b);}else{Dg('No identity selected yet! Please select an identity.');}}else if(c===this.e){a=em(this.d);if(a>=0){b=fm(this.d,a);Dg('Remove selected identity '+b+' from policy');im(this.d,a);Dl(this.c,AC(this,b));}else{Dg('No identity selected yet! Please select an identity.');}}}
function wC(){}
_=wC.prototype=new ak();_.ib=BC;_.tN=fF+'AddRemoveIdentitiesWidget';_.tI=78;_.a=null;_.c=null;_.d=null;_.e=null;function rE(a){a.d=aA(new ez());}
function sE(a,b){rE(a);a.e=Db(new yb(),(Fb(),dc),b);wE(a);return a;}
function tE(e){var a,b,c,d;b='';a=bA(new ez(),e.d);for(d=Bz(gA(a));tz(d);){c=uz(d);b+=c.A()+''+c.C();if(tz(d)){b+='&';}}return b;}
function vE(a){return ac(a.e,tE(a),a);}
function wE(a){bc(a.e,'Content-Type','application/x-www-form-urlencoded');}
function xE(b,a){Dg('Exception: '+a.b);}
function qE(){}
_=qE.prototype=new su();_.kb=xE;_.tN=gF+'AsynchronousAgent';_.tI=0;_.e=null;function DC(a){a.c=pB(new oB());a.a=pB(new oB());a.b=pB(new oB());}
function EC(a,b){sE(a,b);DC(a);return a;}
function aD(d,c,a){var b;b=c.z(a);return fe(b.bb(0),16);}
function bD(c){var a,b;a=Fd('[Ljava.lang.String;',[0],[1],[c.a.a.b],null);for(b=0;b<c.a.a.b;b++){a[b]=fe(sB(c.a,b),1);}return a;}
function cD(c){var a,b;b=Fd('[Ljava.lang.String;',[0],[1],[c.b.a.b],null);for(a=0;a<c.b.a.b;a++){b[a]=fe(sB(c.b,a),1);}return b;}
function dD(b){var a,c;c=Fd('[Ljava.lang.String;',[0],[1],[b.c.a.b],null);for(a=0;a<b.c.a.b;a++){c[a]=fe(sB(b.c,a),1);}return c;}
function eD(d,e){var a,b,c,f,g,h,i,j;h=zp(tb(e)).w();j=aD(this,h,'users');i=j.z('user');for(c=0;c<i.B();c++){qB(this.c,fe(i.bb(c),16).v('id'));}b=aD(this,h,'groups');a=b.z('group');for(c=0;c<a.B();c++){qB(this.a,fe(a.bb(c),16).v('id'));}g=aD(this,h,'rights');f=g.z('right');for(c=0;c<f.B();c++){qB(this.b,fe(f.bb(c),16).v('id'));}}
function CC(){}
_=CC.prototype=new qE();_.mb=eD;_.tN=fF+'AsynchronousIdentitiesAndRightsGetter';_.tI=0;function gD(a){a.c=pB(new oB());a.a=pB(new oB());}
function hD(a,b){sE(a,b);gD(a);return a;}
function jD(d,c,a){var b;b=c.z(a);if(b.B()>0){return fe(b.bb(0),16);}else{return null;}}
function kD(c){var a,b;b=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[27],[c.a.a.b],null);for(a=0;a<b.a;a++){b[a]=fe(sB(c.a,a),27);}return b;}
function lD(c){var a,b;b=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[26],[c.c.a.b],null);for(a=0;a<b.a;a++){b[a]=fe(sB(c.c,a),26);}return b;}
function mD(c,d){var a,b,e,f,g,h,i;f=zp(tb(d)).w();g=f.v('use-inherited-policies');if(g===null){this.b=true;}else{if(hv(g,'false')){this.b=false;}else{this.b=true;}}i=jD(this,f,'world');h=f.z('user');for(b=0;b<h.B();b++){e=ae('[Ljava.lang.String;',0,1,['Write','Read']);qB(this.c,oE(new nE(),fe(h.bb(b),16).v('id'),e));}a=f.z('group');for(b=0;b<a.B();b++){e=ae('[Ljava.lang.String;',0,1,['Write','Read']);qB(this.a,uD(new tD(),fe(a.bb(b),16).v('id'),e));}}
function fD(){}
_=fD.prototype=new qE();_.mb=mD;_.tN=fF+'AsynchronousPolicyGetter';_.tI=0;_.b=true;function oD(a,b){Dg('Save policy to: '+b);a.a=Db(new yb(),(Fb(),ec),b);return a;}
function qD(f,h,b,g){var a,c,d,e;a=Du(new Bu(),'<?xml version="1.0"?>');Fu(a,'<policy xmlns="http://www.wyona.org/security/1.0" use-inherited-policies="'+g+'">');if(h!==null){for(c=0;c<h.a;c++){Fu(a,'<user id="'+h[c].a+'">');e=h[c].b;if(e!==null){for(d=0;d<e.a;d++){Fu(a,'<right id="'+e[d]+'">'+e[d]+'<\/right>');}}Fu(a,'<\/user>');}}if(b!==null){for(c=0;c<b.a;c++){Fu(a,'<group id="'+b[c].a+'">');e=b[c].b;if(e!==null){for(d=0;d<e.a;d++){Fu(a,'<right id="'+e[d]+'">'+e[d]+'<\/right>');}}Fu(a,'<\/group>');}}Fu(a,'<\/policy>');return ac(f.a,dv(a),f);}
function rD(b,a){Dg('Exception: '+a.b);}
function sD(a,b){if(sb(b)==200){Dg('Policy has been saved successfully!');}else{Dg('Policy has NOT been saved! Please check log files on server.');}}
function nD(){}
_=nD.prototype=new su();_.kb=rD;_.mb=sD;_.tN=fF+'AsynchronousPolicySetter';_.tI=0;_.a=null;function uD(c,a,b){c.a=a;c.b=b;return c;}
function tD(){}
_=tD.prototype=new su();_.tN=fF+'Group';_.tI=79;_.a=null;_.b=null;function xD(a){a.b=Dn(new Bn());}
function yD(b,d,c,a){xD(b);ck(b,b.b);En(b.b,ql(new ol(),'Identities'));b.a=Cl(new ul(),true);b.a.l(b);AD(b,d,c,a);En(b.b,b.a);return b;}
function AD(c,e,d,a){var b;am(c.a);km(c.a,e);if(d!==null){for(b=0;b<d.a;b++){Dl(c.a,'u: '+d[b]);}}else{Dl(c.a,'No users yet!');}if(a!==null){for(b=0;b<a.a;b++){Dl(c.a,'g: '+a[b]);}}else{Dl(c.a,'No groups yet!');}}
function BD(a){}
function wD(){}
_=wD.prototype=new ak();_.ib=BD;_.tN=fF+'IdentitiesListBoxWidget';_.tI=80;_.a=null;function DD(a){a.f=Dn(new Bn());}
function ED(b,e,d,a,c){DD(b);ck(b,b.f);En(b.f,ql(new ol(),'Policy'));b.d=kj(new hj(),'Inherit rights from parent policies');jE(b,c);En(b.f,b.d);b.c=Cl(new ul(),true);b.c.l(b);iE(b,e,d,a);En(b.f,b.c);b.e=kj(new hj(),'Read');b.e.l(b);En(b.f,b.e);b.g=kj(new hj(),'Write');b.g.l(b);En(b.f,b.g);return b;}
function FD(g,a,f){var b,c,d,e;e=pB(new oB());for(c=0;c<a.a;c++){qB(e,a[c]);}b=false;for(c=0;c<a.a;c++){if(hv(a[c],f)){b=true;break;}}if(!b)qB(e,f);d=Fd('[Ljava.lang.String;',[0],[1],[e.a.b],null);for(c=0;c<d.a;c++){d[c]=fe(sB(e,c),1);}return d;}
function bE(g){var a,b,c,d,e,f;b=pB(new oB());for(c=0;c<cm(g.c);c++){e=dm(g.c,c);f=dE(g,e);d=cE(g,c);if(mv(d,'g:')){qB(b,uD(new tD(),pv(nv(d,2)),f));}}a=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[27],[b.a.b],null);for(c=0;c<a.a;c++){a[c]=fe(sB(b,c),27);}return a;}
function cE(b,a){return fm(b.c,a);}
function dE(f,b){var a,c,d,e;if(iv(b,'(')>0){e=kv(ov(b,iv(b,'(')+1,iv(b,')')),',');c=pB(new oB());for(a=0;a<e.a;a++){if(!hv(e[a],'-'))qB(c,e[a]);}d=Fd('[Ljava.lang.String;',[0],[1],[c.a.b],null);for(a=0;a<d.a;a++){d[a]=fe(sB(c,a),1);}return d;}else{return Fd('[Ljava.lang.String;',[0],[1],[0],null);}}
function eE(b){var a;a=em(b.c);if(a>=0){return dm(b.c,a);}return null;}
function fE(a){return mj(a.d);}
function gE(e){var a,b,c,d,f,g;g=pB(new oB());for(a=0;a<cm(e.c);a++){c=dm(e.c,a);d=dE(e,c);b=cE(e,a);if(mv(b,'u:')){qB(g,oE(new nE(),pv(nv(b,2)),d));}}f=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[26],[g.a.b],null);for(a=0;a<f.a;a++){f[a]=fe(sB(g,a),26);}return f;}
function hE(f,a,e){var b,c,d;d=pB(new oB());for(b=0;b<a.a;b++){if(!hv(a[b],e)){qB(d,a[b]);}}c=Fd('[Ljava.lang.String;',[0],[1],[d.a.b],null);for(b=0;b<c.a;b++){c[b]=fe(sB(d,b),1);}return c;}
function iE(d,g,e,a){var b,c,f;am(d.c);km(d.c,g);if(e!==null||a!==null){if(e!==null){for(b=0;b<e.a;b++){c='u: ('+d.a+','+d.b+') '+e[b].a;f='u: '+e[b].a;El(d.c,c,f);}}if(a!==null){for(b=0;b<a.a;b++){c='g: ('+d.a+','+d.b+') '+a[b].a;f='g: '+a[b].a;El(d.c,c,f);}}else{Dg('No groups!');}}else{Dl(d.c,'No identities yet!');}}
function jE(a,b){if(a.d!==null){nj(a.d,b);}}
function kE(g,h,a,e,b){var c,d,f,i;f=Du(new Bu(),h+':');Fu(f,' (');d=false;i=false;for(c=0;c<e.a;c++){if(hv(e[c],g.a)){d=true;}if(hv(e[c],g.b)){i=true;}}if(d){Fu(f,g.a);}else{Fu(f,'-');}Fu(f,',');if(i){Fu(f,g.b);}else{Fu(f,'-');}Fu(f,')');Fu(f,' '+a);jm(g.c,b,dv(f));}
function lE(d,c){var a,b;b=em(d.c);if(b>=0){a=cE(d,b);kE(d,ov(a,0,1),pv(nv(a,2)),c,b);}else{Dg('Exception: No list item selected!');}}
function mE(h){var a,b,c,d,e,f,g;if(h===this.e||h===this.g){g=eE(this);if(g!==null){if(h===this.e){a=dE(this,g);if(mj(this.e)){Dg('Add Read right from selected identity '+g+' from policy');e=FD(this,a,this.a);}else{Dg('Remove Read right from selected identity '+g+' from policy');e=hE(this,a,this.a);}lE(this,e);}else if(h===this.g){a=dE(this,g);if(mj(this.g)){Dg('Add Write right from selected identity '+g+' from policy');e=FD(this,a,this.b);}else{Dg('Remove Write right from selected identity '+g+' from policy');e=hE(this,a,this.b);}lE(this,e);}}else{Dg('No identity has been selected! Please select an identity in order to assign rights.');nj(this.e,false);nj(this.g,false);}}else if(h===this.c){g=eE(this);f=dE(this,g);b=false;c=false;for(d=0;d<f.a;d++){if(hv(f[d],this.a)){nj(this.e,true);b=true;}else if(hv(f[d],this.b)){nj(this.g,true);c=true;}}if(!b)nj(this.e,false);if(!c)nj(this.g,false);}}
function CD(){}
_=CD.prototype=new ak();_.ib=mE;_.tN=fF+'PolicyListBoxWidget';_.tI=81;_.a='r';_.b='w';_.c=null;_.d=null;_.e=null;_.g=null;function oE(c,a,b){c.a=a;c.b=b;return c;}
function nE(){}
_=nE.prototype=new su();_.tN=fF+'User';_.tI=82;_.a=null;_.b=null;function at(){vC(new CB());}
function gwtOnLoad(b,d,c){$moduleName=d;$moduleBase=c;if(b)try{at();}catch(a){b(d);}else{at();}}
var je=[{},{},{1:1},{4:1},{4:1,25:1},{4:1,25:1},{4:1,18:1,25:1},{2:1},{6:1},{6:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{3:1},{2:1,5:1},{2:1},{7:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{20:1},{20:1},{20:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{9:1,10:1,11:1,12:1,13:1,14:1},{7:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{4:1,25:1},{15:1},{15:1},{15:1},{15:1},{15:1},{15:1},{15:1},{4:1,25:1},{15:1},{15:1,17:1},{15:1,16:1},{15:1},{15:1},{15:1},{4:1,25:1},{19:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{21:1},{22:1},{22:1},{21:1},{23:1},{22:1},{22:1},{4:1,24:1,25:1},{4:1,25:1},{20:1},{8:1},{8:1},{8:1},{6:1},{6:1},{8:1,11:1,12:1,13:1,14:1},{27:1},{8:1,11:1,12:1,13:1,14:1},{8:1,11:1,12:1,13:1,14:1},{26:1}];if (org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor) {  var __gwt_initHandlers = org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.__gwt_initHandlers;  org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.onScriptLoad(gwtOnLoad);}})();