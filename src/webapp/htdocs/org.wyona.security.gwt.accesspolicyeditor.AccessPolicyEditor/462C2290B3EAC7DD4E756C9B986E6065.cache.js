(function(){var $wnd = window;var $doc = $wnd.document;var $moduleName, $moduleBase;var _,dF='com.google.gwt.core.client.',eF='com.google.gwt.http.client.',fF='com.google.gwt.i18n.client.',gF='com.google.gwt.lang.',hF='com.google.gwt.user.client.',iF='com.google.gwt.user.client.impl.',jF='com.google.gwt.user.client.ui.',kF='com.google.gwt.user.client.ui.impl.',lF='com.google.gwt.xml.client.',mF='com.google.gwt.xml.client.impl.',nF='java.io.',oF='java.lang.',pF='java.util.',qF='org.wyona.security.gwt.accesspolicyeditor.client.',rF='org.wyona.yanel.gwt.client.';function gC(){}
function Fu(a){return this===a;}
function av(){return gw(this);}
function bv(){return this.tN+'@'+this.hC();}
function Du(){}
_=Du.prototype={};_.eQ=Fu;_.hC=av;_.tS=bv;_.toString=function(){return this.tS();};_.tN=oF+'Object';_.tI=1;function w(a){return a==null?null:a.tN;}
var y=null;function B(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function C(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function D(){return ++E;}
var E=0;function iw(b,a){b.b=a;return b;}
function kw(b,a){if(b.a!==null){throw ju(new iu(),"Can't overwrite cause");}if(a===b){throw gu(new fu(),'Self-causation not permitted');}b.a=a;return b;}
function lw(a){mw(a,(ew(),fw));}
function mw(e,d){var a,b,c;c=hv(new gv());b=e;while(b!==null){a=b.b;if(b!==e){kv(c,'Caused by: ');}kv(c,b.tN);kv(c,': ');kv(c,a===null?'(No exception detail)':a);kv(c,'\n');b=b.a;}}
function nw(){var a,b;a=w(this);b=this.b;if(b!==null){return a+': '+b;}else{return a;}}
function hw(){}
_=hw.prototype=new Du();_.tS=nw;_.tN=oF+'Throwable';_.tI=3;_.a=null;_.b=null;function du(b,a){iw(b,a);return b;}
function cu(){}
_=cu.prototype=new hw();_.tN=oF+'Exception';_.tI=4;function dv(b,a){du(b,a);return b;}
function cv(){}
_=cv.prototype=new cu();_.tN=oF+'RuntimeException';_.tI=5;function ab(c,b,a){dv(c,'JavaScript '+b+' exception: '+a);return c;}
function F(){}
_=F.prototype=new cv();_.tN=dF+'JavaScriptException';_.tI=6;function eb(b,a){if(!ge(a,2)){return false;}return jb(b,fe(a,2));}
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
_=cb.prototype=new Du();_.eQ=kb;_.hC=lb;_.tS=nb;_.tN=dF+'JavaScriptObject';_.tI=7;function rc(b,d,c,a){if(d===null){throw new wu();}if(a===null){throw new wu();}if(c<0){throw new fu();}b.a=c;b.c=d;if(c>0){b.b=vb(new ub(),b,a);rg(b.b,c);}else{b.b=null;}return b;}
function tc(a){var b;if(a.c!==null){b=a.c;a.c=null;dd(b);sc(a);}}
function sc(a){if(a.b!==null){ng(a.b);}}
function vc(e,a){var b,c,d,f;if(e.c===null){return;}sc(e);f=e.c;e.c=null;b=ed(f);if(b!==null){c=dv(new cv(),b);a.kb(e,c);}else{d=yc(f);a.mb(e,d);}}
function wc(b,a){if(b.c===null){return;}tc(b);a.kb(b,oc(new nc(),b,b.a));}
function xc(b){var a;if(b.c===null){return false;}a=fd(b.c);switch(a){case 1:case 2:case 3:return true;}return false;}
function yc(b){var a;a=qb(new pb(),b);return a;}
function zc(a){var b;b=y;{vc(this,a);}}
function ob(){}
_=ob.prototype=new Du();_.t=zc;_.tN=eF+'Request';_.tI=0;_.a=0;_.b=null;_.c=null;function Ac(){}
_=Ac.prototype=new Du();_.tN=eF+'Response';_.tI=0;function qb(a,b){a.a=b;return a;}
function sb(a){return hd(a.a);}
function tb(a){return gd(a.a);}
function pb(){}
_=pb.prototype=new Ac();_.tN=eF+'Request$1';_.tI=0;function og(){og=gC;yg=xy(new vy());{xg();}}
function mg(a){og();return a;}
function ng(a){if(a.d){sg(a.e);}else{tg(a.e);}bz(yg,a);}
function pg(a){if(!a.d){bz(yg,a);}a.ub();}
function rg(b,a){if(a<=0){throw gu(new fu(),'must be positive');}ng(b);b.d=false;b.e=vg(b,a);zy(yg,b);}
function qg(b,a){if(a<=0){throw gu(new fu(),'must be positive');}ng(b);b.d=true;b.e=ug(b,a);zy(yg,b);}
function sg(a){og();$wnd.clearInterval(a);}
function tg(a){og();$wnd.clearTimeout(a);}
function ug(b,a){og();return $wnd.setInterval(function(){b.u();},a);}
function vg(b,a){og();return $wnd.setTimeout(function(){b.u();},a);}
function wg(){var a;a=y;{pg(this);}}
function xg(){og();Cg(new ig());}
function hg(){}
_=hg.prototype=new Du();_.u=wg;_.tN=hF+'Timer';_.tI=8;_.d=false;_.e=0;var yg;function wb(){wb=gC;og();}
function vb(b,a,c){wb();b.a=a;b.b=c;mg(b);return b;}
function xb(){wc(this.a,this.b);}
function ub(){}
_=ub.prototype=new hg();_.ub=xb;_.tN=eF+'Request$2';_.tI=9;function Fb(){Fb=gC;dc=Ab(new zb(),'GET');ec=Ab(new zb(),'POST');fc=ji(new ii());}
function Db(b,a,c){Fb();Eb(b,a===null?null:a.a,c);return b;}
function Eb(b,a,c){Fb();Ec('httpMethod',a);Ec('url',c);b.b=a;b.d=c;return b;}
function ac(g,d,a){var b,c,e,f,h;h=li(fc);{b=id(h,g.b,g.d,true);}if(b!==null){e=lc(new kc(),g.d);kw(e,ic(new hc(),b));throw e;}cc(g,h);c=rc(new ob(),h,g.c,a);f=jd(h,c,d,a);if(f!==null){throw ic(new hc(),f);}return c;}
function bc(b,a,c){Ec('header',a);Ec('value',c);if(b.a===null){b.a=lA(new pz());}uA(b.a,a,c);}
function cc(e,f){var a,b,c,d;if(e.a!==null&&e.a.c>0){a=rA(e.a);d=gA(a);while(Ez(d)){c=Fz(d);b=kd(f,fe(c.A(),1),fe(c.C(),1));if(b!==null){throw ic(new hc(),b);}}}else{kd(f,'Content-Type','text/plain; charset=utf-8');}}
function yb(){}
_=yb.prototype=new Du();_.tN=eF+'RequestBuilder';_.tI=0;_.a=null;_.b=null;_.c=0;_.d=null;var dc,ec,fc;function Ab(b,a){b.a=a;return b;}
function Cb(){return this.a;}
function zb(){}
_=zb.prototype=new Du();_.tS=Cb;_.tN=eF+'RequestBuilder$Method';_.tI=0;_.a=null;function ic(b,a){du(b,a);return b;}
function hc(){}
_=hc.prototype=new cu();_.tN=eF+'RequestException';_.tI=10;function lc(a,b){ic(a,'The URL '+b+' is invalid or violates the same-origin security restriction');return a;}
function kc(){}
_=kc.prototype=new hc();_.tN=eF+'RequestPermissionException';_.tI=11;function oc(b,a,c){ic(b,qc(c));return b;}
function qc(a){return 'A request timeout has expired after '+qu(a)+' ms';}
function nc(){}
_=nc.prototype=new hc();_.tN=eF+'RequestTimeoutException';_.tI=12;function Ec(a,b){Fc(a,b);if(0==uv(Av(b))){throw gu(new fu(),a+' can not be empty');}}
function Fc(a,b){if(null===b){throw xu(new wu(),a+' can not be null');}}
function dd(a){a.onreadystatechange=ni;a.abort();}
function ed(b){try{if(b.status===undefined){return 'XmlHttpRequest.status == undefined, please see Safari bug '+'http://bugs.webkit.org/show_bug.cgi?id=3810 for more details';}return null;}catch(a){return 'Unable to read XmlHttpRequest.status; likely causes are a '+'networking error or bad cross-domain request. Please see '+'https://bugzilla.mozilla.org/show_bug.cgi?id=238559 for more '+'details';}}
function fd(a){return a.readyState;}
function gd(a){return a.responseText;}
function hd(a){return a.status;}
function id(e,c,d,b){try{e.open(c,d,b);return null;}catch(a){return a.message||a.toString();}}
function jd(e,c,d,b){e.onreadystatechange=function(){if(e.readyState==cd){e.onreadystatechange=ni;c.t(b);}};try{e.send(d);return null;}catch(a){e.onreadystatechange=ni;return a.message||a.toString();}}
function kd(d,b,c){try{d.setRequestHeader(b,c);return null;}catch(a){return a.message||a.toString();}}
var cd=4;function pd(){pd=gC;sd=lA(new pz());}
function md(b,a){pd();if(a===null||sv('',a)){throw gu(new fu(),'Cannot create a Dictionary with a null or empty name');}b.b='Dictionary '+a;od(b,a);if(b.a===null){throw tB(new sB(),"Cannot find JavaScript object with the name '"+a+"'",a,null);}return b;}
function nd(b,a){for(x in b.a){a.n(x);}}
function od(c,b){try{if(typeof $wnd[b]!='object'){ud(b);}c.a=$wnd[b];}catch(a){ud(b);}}
function qd(b,a){var c=b.a[a];if(c==null|| !Object.prototype.hasOwnProperty.call(b.a,a)){b.tb(a);}return String(c);}
function rd(b){var a;a=hB(new gB());nd(b,a);return a;}
function td(a){pd();var b;b=fe(sA(sd,a),3);if(b===null){b=md(new ld(),a);uA(sd,a,b);}return b;}
function vd(b){var a,c;c=rd(this);a="Cannot find '"+b+"' in "+this;if(c.a.c<20){a+='\n keys found: '+c;}throw tB(new sB(),a,this.b,b);}
function ud(a){pd();throw tB(new sB(),"'"+a+"' is not a JavaScript object and cannot be used as a Dictionary",null,a);}
function wd(){return this.b;}
function ld(){}
_=ld.prototype=new Du();_.tb=vd;_.tS=wd;_.tN=fF+'Dictionary';_.tI=13;_.a=null;_.b=null;var sd;function yd(c,a,d,b,e){c.a=a;c.b=b;c.tN=e;c.tI=d;return c;}
function Ad(a,b,c){return a[b]=c;}
function Bd(b,a){return b[a];}
function Dd(b,a){return b[a];}
function Cd(a){return a.length;}
function Fd(e,d,c,b,a){return Ed(e,d,c,b,0,Cd(b),a);}
function Ed(j,i,g,c,e,a,b){var d,f,h;if((f=Bd(c,e))<0){throw new uu();}h=yd(new xd(),f,Bd(i,e),Bd(g,e),j);++e;if(e<a){j=yv(j,1);for(d=0;d<f;++d){Ad(h,d,Ed(j,i,g,c,e,a,b));}}else{for(d=0;d<f;++d){Ad(h,d,b);}}return h;}
function ae(f,e,c,g){var a,b,d;b=Cd(g);d=yd(new xd(),b,e,c,f);for(a=0;a<b;++a){Ad(d,a,Dd(g,a));}return d;}
function be(a,b,c){if(c!==null&&a.b!=0&& !ge(c,a.b)){throw new st();}return Ad(a,b,c);}
function xd(){}
_=xd.prototype=new Du();_.tN=gF+'Array';_.tI=0;function ee(b,a){return !(!(b&&je[b][a]));}
function fe(b,a){if(b!=null)ee(b.tI,a)||ie();return b;}
function ge(b,a){return b!=null&&ee(b.tI,a);}
function ie(){throw new Et();}
function he(a){if(a!==null){throw new Et();}return a;}
function ke(b,d){_=d.prototype;if(b&& !(b.tI>=_.tI)){var c=b.toString;for(var a in _){b[a]=_[a];}b.toString=c;}return b;}
var je;function ne(a){if(ge(a,4)){return a;}return ab(new F(),pe(a),oe(a));}
function oe(a){return a.message;}
function pe(a){return a.name;}
function re(){re=gC;rf=xy(new vy());{mf=new ih();rh(mf);}}
function se(b,a){re();th(mf,b,a);}
function te(a,b){re();return nh(mf,a,b);}
function ue(){re();return vh(mf,'button');}
function ve(){re();return vh(mf,'div');}
function we(){re();return wh(mf,'checkbox');}
function xe(){re();return wh(mf,'text');}
function ye(){re();return vh(mf,'label');}
function ze(a){re();return xh(mf,a);}
function Ae(){re();return vh(mf,'span');}
function Be(){re();return vh(mf,'tbody');}
function Ce(){re();return vh(mf,'td');}
function De(){re();return vh(mf,'tr');}
function Ee(){re();return vh(mf,'table');}
function bf(b,a,d){re();var c;c=y;{af(b,a,d);}}
function af(b,a,c){re();var d;if(a===qf){if(df(b)==8192){qf=null;}}d=Fe;Fe=b;try{c.hb(b);}finally{Fe=d;}}
function cf(b,a){re();yh(mf,b,a);}
function df(a){re();return zh(mf,a);}
function ef(a){re();oh(mf,a);}
function ff(a){re();return ph(mf,a);}
function jf(a,b){re();return Ch(mf,a,b);}
function gf(a,b){re();return Ah(mf,a,b);}
function hf(a,b){re();return Bh(mf,a,b);}
function kf(a){re();return Dh(mf,a);}
function lf(a){re();return qh(mf,a);}
function nf(c,b,d,a){re();kh(mf,c,b,d,a);}
function of(a){re();var b,c;c=true;if(rf.b>0){b=he(Dy(rf,rf.b-1));if(!(c=null.Ab())){cf(a,true);ef(a);}}return c;}
function pf(b,a){re();Eh(mf,b,a);}
function uf(a,b,c){re();bi(mf,a,b,c);}
function sf(a,b,c){re();Fh(mf,a,b,c);}
function tf(a,b,c){re();ai(mf,a,b,c);}
function vf(a,b){re();ci(mf,a,b);}
function wf(a,b){re();di(mf,a,b);}
function xf(a,b){re();ei(mf,a,b);}
function yf(b,c,a){re();fi(mf,b,c,a);}
function zf(b,a,c){re();gi(mf,b,a,c);}
function Af(a,b){re();sh(mf,a,b);}
function Bf(a){re();return hi(mf,a);}
var Fe=null,mf=null,qf=null,rf;function Ef(a){if(ge(a,5)){return te(this,fe(a,5));}return eb(ke(this,Cf),a);}
function Ff(){return fb(ke(this,Cf));}
function ag(){return Bf(this);}
function Cf(){}
_=Cf.prototype=new cb();_.eQ=Ef;_.hC=Ff;_.tS=ag;_.tN=hF+'Element';_.tI=14;function eg(a){return eb(ke(this,bg),a);}
function fg(){return fb(ke(this,bg));}
function gg(){return ff(this);}
function bg(){}
_=bg.prototype=new cb();_.eQ=eg;_.hC=fg;_.tS=gg;_.tN=hF+'Event';_.tI=15;function kg(){while((og(),yg).b>0){ng(fe(Dy((og(),yg),0),6));}}
function lg(){return null;}
function ig(){}
_=ig.prototype=new Du();_.ob=kg;_.pb=lg;_.tN=hF+'Timer$1';_.tI=16;function Bg(){Bg=gC;Eg=xy(new vy());gh=xy(new vy());{ch();}}
function Cg(a){Bg();zy(Eg,a);}
function Dg(a){Bg();$wnd.alert(a);}
function Fg(){Bg();var a,b;for(a=Eg.cb();a.F();){b=fe(a.eb(),7);b.ob();}}
function ah(){Bg();var a,b,c,d;d=null;for(a=Eg.cb();a.F();){b=fe(a.eb(),7);c=b.pb();{d=c;}}return d;}
function bh(){Bg();var a,b;for(a=gh.cb();a.F();){b=he(a.eb());null.Ab();}}
function ch(){Bg();__gwt_initHandlers(function(){fh();},function(){return eh();},function(){dh();$wnd.onresize=null;$wnd.onbeforeclose=null;$wnd.onclose=null;});}
function dh(){Bg();var a;a=y;{Fg();}}
function eh(){Bg();var a;a=y;{return ah();}}
function fh(){Bg();var a;a=y;{bh();}}
var Eg,gh;function th(c,b,a){b.appendChild(a);}
function vh(b,a){return $doc.createElement(a);}
function wh(b,c){var a=$doc.createElement('INPUT');a.type=c;return a;}
function xh(c,a){var b;b=vh(c,'select');if(a){Fh(c,b,'multiple',true);}return b;}
function yh(c,b,a){b.cancelBubble=a;}
function zh(b,a){switch(a.type){case 'blur':return 4096;case 'change':return 1024;case 'click':return 1;case 'dblclick':return 2;case 'focus':return 2048;case 'keydown':return 128;case 'keypress':return 256;case 'keyup':return 512;case 'load':return 32768;case 'losecapture':return 8192;case 'mousedown':return 4;case 'mousemove':return 64;case 'mouseout':return 32;case 'mouseover':return 16;case 'mouseup':return 8;case 'scroll':return 16384;case 'error':return 65536;case 'mousewheel':return 131072;case 'DOMMouseScroll':return 131072;}}
function Ch(d,a,b){var c=a[b];return c==null?null:String(c);}
function Ah(c,a,b){return !(!a[b]);}
function Bh(d,a,c){var b=parseInt(a[c]);if(!b){return 0;}return b;}
function Dh(b,a){return a.__eventBits||0;}
function Eh(c,b,a){b.removeChild(a);}
function bi(c,a,b,d){a[b]=d;}
function Fh(c,a,b,d){a[b]=d;}
function ai(c,a,b,d){a[b]=d;}
function ci(c,a,b){a.__listener=b;}
function di(c,a,b){if(!b){b='';}a.innerHTML=b;}
function ei(c,a,b){while(a.firstChild){a.removeChild(a.firstChild);}if(b!=null){a.appendChild($doc.createTextNode(b));}}
function fi(e,c,d,a){var b=c.options[a];b.text=d;}
function gi(c,b,a,d){b.style[a]=d;}
function hi(b,a){return a.outerHTML;}
function hh(){}
_=hh.prototype=new Du();_.tN=iF+'DOMImpl';_.tI=0;function nh(c,a,b){return a==b;}
function oh(b,a){a.preventDefault();}
function ph(b,a){return a.toString();}
function qh(c,a){var b=a.parentNode;if(b==null){return null;}if(b.nodeType!=1)b=null;return b||null;}
function rh(d){$wnd.__dispatchCapturedMouseEvent=function(b){if($wnd.__dispatchCapturedEvent(b)){var a=$wnd.__captureElem;if(a&&a.__listener){bf(b,a,a.__listener);b.stopPropagation();}}};$wnd.__dispatchCapturedEvent=function(a){if(!of(a)){a.stopPropagation();a.preventDefault();return false;}return true;};$wnd.addEventListener('click',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('dblclick',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousedown',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mouseup',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousemove',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousewheel',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('keydown',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keyup',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keypress',$wnd.__dispatchCapturedEvent,true);$wnd.__dispatchEvent=function(b){var c,a=this;while(a&& !(c=a.__listener))a=a.parentNode;if(a&&a.nodeType!=1)a=null;if(c)bf(b,a,c);};$wnd.__captureElem=null;}
function sh(c,b,a){b.__eventBits=a;b.onclick=a&1?$wnd.__dispatchEvent:null;b.ondblclick=a&2?$wnd.__dispatchEvent:null;b.onmousedown=a&4?$wnd.__dispatchEvent:null;b.onmouseup=a&8?$wnd.__dispatchEvent:null;b.onmouseover=a&16?$wnd.__dispatchEvent:null;b.onmouseout=a&32?$wnd.__dispatchEvent:null;b.onmousemove=a&64?$wnd.__dispatchEvent:null;b.onkeydown=a&128?$wnd.__dispatchEvent:null;b.onkeypress=a&256?$wnd.__dispatchEvent:null;b.onkeyup=a&512?$wnd.__dispatchEvent:null;b.onchange=a&1024?$wnd.__dispatchEvent:null;b.onfocus=a&2048?$wnd.__dispatchEvent:null;b.onblur=a&4096?$wnd.__dispatchEvent:null;b.onlosecapture=a&8192?$wnd.__dispatchEvent:null;b.onscroll=a&16384?$wnd.__dispatchEvent:null;b.onload=a&32768?$wnd.__dispatchEvent:null;b.onerror=a&65536?$wnd.__dispatchEvent:null;b.onmousewheel=a&131072?$wnd.__dispatchEvent:null;}
function lh(){}
_=lh.prototype=new hh();_.tN=iF+'DOMImplStandard';_.tI=0;function kh(e,c,d,f,a){var b=new Option(d,f);if(a== -1||a>c.children.length-1){c.appendChild(b);}else{c.insertBefore(b,c.children[a]);}}
function ih(){}
_=ih.prototype=new lh();_.tN=iF+'DOMImplSafari';_.tI=0;function ji(a){ni=hb();return a;}
function li(a){return mi(a);}
function mi(a){return new XMLHttpRequest();}
function ii(){}
_=ii.prototype=new Du();_.tN=iF+'HTTPRequestImpl';_.tI=0;var ni=null;function sn(d,b,a){var c=b.parentNode;if(!c){return;}c.insertBefore(a,b);c.removeChild(b);}
function tn(b,a){if(b.k!==null){sn(b,b.k,a);}b.k=a;}
function un(b,a){xn(b.k,a);}
function vn(b,a){Af(b.y(),a|kf(b.y()));}
function wn(){return this.k;}
function xn(a,b){uf(a,'className',b);}
function yn(){if(this.k===null){return '(null handle)';}return Bf(this.k);}
function qn(){}
_=qn.prototype=new Du();_.y=wn;_.tS=yn;_.tN=jF+'UIObject';_.tI=0;_.k=null;function uo(a){if(ge(a.j,10)){fe(a.j,10).sb(a);}else if(a.j!==null){throw ju(new iu(),"This widget's parent does not implement HasWidgets");}}
function vo(b,a){if(b.ab()){vf(b.y(),null);}tn(b,a);if(b.ab()){vf(a,b);}}
function wo(c,b){var a;a=c.j;if(b===null){if(a!==null&&a.ab()){c.jb();}c.j=null;}else{if(a!==null){throw ju(new iu(),'Cannot set a new parent without first clearing the old parent');}c.j=b;if(b.ab()){c.gb();}}}
function xo(){}
function yo(){}
function zo(){return this.i;}
function Ao(){if(this.ab()){throw ju(new iu(),"Should only call onAttach when the widget is detached from the browser's document");}this.i=true;vf(this.y(),this);this.q();this.lb();}
function Bo(a){}
function Co(){if(!this.ab()){throw ju(new iu(),"Should only call onDetach when the widget is attached to the browser's document");}try{this.nb();}finally{this.r();vf(this.y(),null);this.i=false;}}
function Do(){}
function Eo(){}
function Fo(a){vo(this,a);}
function ao(){}
_=ao.prototype=new qn();_.q=xo;_.r=yo;_.ab=zo;_.gb=Ao;_.hb=Bo;_.jb=Co;_.lb=Do;_.nb=Eo;_.vb=Fo;_.tN=jF+'Widget';_.tI=17;_.i=false;_.j=null;function mm(b,a){wo(a,b);}
function om(b,a){wo(a,null);}
function pm(){var a,b;for(b=this.cb();go(b);){a=ho(b);a.gb();}}
function qm(){var a,b;for(b=this.cb();go(b);){a=ho(b);a.jb();}}
function rm(){}
function sm(){}
function lm(){}
_=lm.prototype=new ao();_.q=pm;_.r=qm;_.lb=rm;_.nb=sm;_.tN=jF+'Panel';_.tI=18;function vj(a){a.f=lo(new bo(),a);}
function wj(a){vj(a);return a;}
function xj(c,a,b){uo(a);mo(c.f,a);se(b,a.y());mm(c,a);}
function zj(b,c){var a;if(c.j!==b){return false;}om(b,c);a=c.y();pf(lf(a),a);so(b.f,c);return true;}
function Aj(){return qo(this.f);}
function Bj(a){return zj(this,a);}
function uj(){}
_=uj.prototype=new lm();_.cb=Aj;_.sb=Bj;_.tN=jF+'ComplexPanel';_.tI=19;function pi(a){wj(a);a.vb(ve());zf(a.y(),'position','relative');zf(a.y(),'overflow','hidden');return a;}
function qi(a,b){xj(a,b,a.y());}
function si(a){zf(a,'left','');zf(a,'top','');zf(a,'position','');}
function ti(b){var a;a=zj(this,b);if(a){si(b.y());}return a;}
function oi(){}
_=oi.prototype=new uj();_.sb=ti;_.tN=jF+'AbsolutePanel';_.tI=20;function jk(){jk=gC;mp(),op;}
function ik(b,a){mp(),op;lk(b,a);return b;}
function kk(b,a){switch(df(a)){case 1:if(b.c!==null){sj(b.c,b);}break;case 4096:case 2048:break;case 128:case 512:case 256:break;}}
function lk(b,a){vo(b,a);vn(b,7041);}
function mk(a){if(this.c===null){this.c=qj(new pj());}zy(this.c,a);}
function nk(a){kk(this,a);}
function ok(a){lk(this,a);}
function hk(){}
_=hk.prototype=new ao();_.l=mk;_.hb=nk;_.vb=ok;_.tN=jF+'FocusWidget';_.tI=21;_.c=null;function xi(){xi=gC;mp(),op;}
function wi(b,a){mp(),op;ik(b,a);return b;}
function yi(a){wf(this.y(),a);}
function vi(){}
_=vi.prototype=new hk();_.wb=yi;_.tN=jF+'ButtonBase';_.tI=22;function Ci(){Ci=gC;mp(),op;}
function zi(a){mp(),op;wi(a,ue());Di(a.y());un(a,'gwt-Button');return a;}
function Ai(b,a){mp(),op;zi(b);b.wb(a);return b;}
function Bi(c,a,b){mp(),op;Ai(c,a);c.l(b);return c;}
function Di(b){Ci();if(b.type=='submit'){try{b.setAttribute('type','button');}catch(a){}}}
function ui(){}
_=ui.prototype=new vi();_.tN=jF+'Button';_.tI=23;function Fi(a){wj(a);a.e=Ee();a.d=Be();se(a.e,a.d);a.vb(a.e);return a;}
function bj(c,b,a){uf(b,'align',a.a);}
function cj(c,b,a){zf(b,'verticalAlign',a.a);}
function Ei(){}
_=Ei.prototype=new uj();_.tN=jF+'CellPanel';_.tI=24;_.d=null;_.e=null;function hj(){hj=gC;mp(),op;}
function ej(a){mp(),op;fj(a,we());un(a,'gwt-CheckBox');return a;}
function gj(b,a){mp(),op;ej(b);kj(b,a);return b;}
function fj(b,a){var c;mp(),op;wi(b,Ae());b.a=a;b.b=ye();Af(b.a,kf(b.y()));Af(b.y(),0);se(b.y(),b.a);se(b.y(),b.b);c='check'+ ++oj;uf(b.a,'id',c);uf(b.b,'htmlFor',c);return b;}
function ij(b){var a;a=b.ab()?'checked':'defaultChecked';return gf(b.a,a);}
function jj(b,a){sf(b.a,'checked',a);sf(b.a,'defaultChecked',a);}
function kj(b,a){xf(b.b,a);}
function lj(){vf(this.a,this);}
function mj(){vf(this.a,null);jj(this,ij(this));}
function nj(a){wf(this.b,a);}
function dj(){}
_=dj.prototype=new vi();_.lb=lj;_.nb=mj;_.wb=nj;_.tN=jF+'CheckBox';_.tI=25;_.a=null;_.b=null;var oj=0;function sw(d,a,b){var c;while(a.F()){c=a.eb();if(b===null?c===null:b.eQ(c)){return a;}}return null;}
function uw(a){throw pw(new ow(),'add');}
function vw(b){var a;a=sw(this,this.cb(),b);return a!==null;}
function ww(){var a,b,c;c=hv(new gv());a=null;kv(c,'[');b=this.cb();while(b.F()){if(a!==null){kv(c,a);}else{a=', ';}kv(c,cw(b.eb()));}kv(c,']');return ov(c);}
function rw(){}
_=rw.prototype=new Du();_.n=uw;_.p=vw;_.tS=ww;_.tN=pF+'AbstractCollection';_.tI=0;function ax(b,a){throw mu(new lu(),'Index: '+a+', Size: '+b.b);}
function bx(b,a){throw pw(new ow(),'add');}
function cx(a){this.m(this.yb(),a);return true;}
function dx(e){var a,b,c,d,f;if(e===this){return true;}if(!ge(e,20)){return false;}f=fe(e,20);if(this.yb()!=f.yb()){return false;}c=this.cb();d=f.cb();while(c.F()){a=c.eb();b=d.eb();if(!(a===null?b===null:a.eQ(b))){return false;}}return true;}
function ex(){var a,b,c,d;c=1;a=31;b=this.cb();while(b.F()){d=b.eb();c=31*c+(d===null?0:d.hC());}return c;}
function fx(){return zw(new yw(),this);}
function gx(a){throw pw(new ow(),'remove');}
function xw(){}
_=xw.prototype=new rw();_.m=bx;_.n=cx;_.eQ=dx;_.hC=ex;_.cb=fx;_.rb=gx;_.tN=pF+'AbstractList';_.tI=26;function wy(a){{Ay(a);}}
function xy(a){wy(a);return a;}
function yy(c,a,b){if(a<0||a>c.b){ax(c,a);}cz(c.a,a,b);++c.b;}
function zy(b,a){lz(b.a,b.b++,a);return true;}
function Ay(a){a.a=gb();a.b=0;}
function Cy(b,a){return Ey(b,a)!=(-1);}
function Dy(b,a){if(a<0||a>=b.b){ax(b,a);}return hz(b.a,a);}
function Ey(b,a){return Fy(b,a,0);}
function Fy(c,b,a){if(a<0){ax(c,a);}for(;a<c.b;++a){if(gz(b,hz(c.a,a))){return a;}}return (-1);}
function az(c,a){var b;b=Dy(c,a);jz(c.a,a,1);--c.b;return b;}
function bz(c,b){var a;a=Ey(c,b);if(a==(-1)){return false;}az(c,a);return true;}
function dz(a,b){yy(this,a,b);}
function ez(a){return zy(this,a);}
function cz(a,b,c){a.splice(b,0,c);}
function fz(a){return Cy(this,a);}
function gz(a,b){return a===b||a!==null&&a.eQ(b);}
function iz(a){return Dy(this,a);}
function hz(a,b){return a[b];}
function kz(a){return az(this,a);}
function jz(a,c,b){a.splice(c,b);}
function lz(a,b,c){a[b]=c;}
function mz(){return this.b;}
function vy(){}
_=vy.prototype=new xw();_.m=dz;_.n=ez;_.p=fz;_.D=iz;_.rb=kz;_.yb=mz;_.tN=pF+'ArrayList';_.tI=27;_.a=null;_.b=0;function qj(a){xy(a);return a;}
function sj(d,c){var a,b;for(a=d.cb();a.F();){b=fe(a.eb(),8);b.ib(c);}}
function pj(){}
_=pj.prototype=new vy();_.tN=jF+'ClickListenerCollection';_.tI=28;function Ej(a,b){if(a.h!==null){throw ju(new iu(),'Composite.initWidget() may only be called once.');}uo(b);a.vb(b.y());a.h=b;wo(b,a);}
function Fj(){if(this.h===null){throw ju(new iu(),'initWidget() was never called in '+w(this));}return this.k;}
function ak(){if(this.h!==null){return this.h.ab();}return false;}
function bk(){this.h.gb();this.lb();}
function ck(){try{this.nb();}finally{this.h.jb();}}
function Cj(){}
_=Cj.prototype=new ao();_.y=Fj;_.ab=ak;_.gb=bk;_.jb=ck;_.tN=jF+'Composite';_.tI=29;_.h=null;function ek(a){wj(a);a.vb(ve());return a;}
function fk(a,b){xj(a,b,a.y());}
function dk(){}
_=dk.prototype=new uj();_.tN=jF+'FlowPanel';_.tI=30;function vk(){vk=gC;tk(new sk(),'center');wk=tk(new sk(),'left');tk(new sk(),'right');}
var wk;function tk(b,a){b.a=a;return b;}
function sk(){}
_=sk.prototype=new Du();_.tN=jF+'HasHorizontalAlignment$HorizontalAlignmentConstant';_.tI=0;_.a=null;function Ck(){Ck=gC;Ak(new zk(),'bottom');Dk=Ak(new zk(),'middle');Ek=Ak(new zk(),'top');}
var Dk,Ek;function Ak(a,b){a.a=b;return a;}
function zk(){}
_=zk.prototype=new Du();_.tN=jF+'HasVerticalAlignment$VerticalAlignmentConstant';_.tI=0;_.a=null;function cl(a){a.a=(vk(),wk);a.c=(Ck(),Ek);}
function dl(a){Fi(a);cl(a);a.b=De();se(a.d,a.b);uf(a.e,'cellSpacing','0');uf(a.e,'cellPadding','0');return a;}
function el(b,c){var a;a=gl(b);se(b.b,a);xj(b,c,a);}
function gl(b){var a;a=Ce();bj(b,a,b.a);cj(b,a,b.c);return a;}
function hl(b,a){b.c=a;}
function il(c){var a,b;b=lf(c.y());a=zj(this,c);if(a){pf(this.b,b);}return a;}
function bl(){}
_=bl.prototype=new Ei();_.sb=il;_.tN=jF+'HorizontalPanel';_.tI=31;_.b=null;function ll(a){a.vb(ve());vn(a,131197);un(a,'gwt-Label');return a;}
function ml(b,a){ll(b);ol(b,a);return b;}
function ol(b,a){xf(b.y(),a);}
function pl(a){switch(df(a)){case 1:break;case 4:case 8:case 64:case 16:case 32:break;case 131072:break;}}
function kl(){}
_=kl.prototype=new ao();_.hb=pl;_.tN=jF+'Label';_.tI=32;function Fl(){Fl=gC;mp(),op;jm=new sl();}
function Al(b,a){Fl();ik(b,ze(a));vn(b,1024);un(b,'gwt-ListBox');return b;}
function Bl(b,a){em(b,a,(-1));}
function Cl(b,a,c){fm(b,a,c,(-1));}
function Dl(b,a){if(a<0||a>=am(b)){throw new lu();}}
function El(a){tl(jm,a.y());}
function am(a){return vl(jm,a.y());}
function bm(b,a){Dl(b,a);return wl(jm,b.y(),a);}
function cm(a){return hf(a.y(),'selectedIndex');}
function dm(b,a){Dl(b,a);return xl(jm,b.y(),a);}
function em(c,b,a){fm(c,b,b,a);}
function fm(c,b,d,a){nf(c.y(),b,d,a);}
function gm(b,a){Dl(b,a);yl(jm,b.y(),a);}
function hm(c,a,b){Dl(c,a);if(b===null){throw xu(new wu(),'Cannot set an option to have null text');}yf(c.y(),b,a);}
function im(a,b){tf(a.y(),'size',b);}
function km(a){if(df(a)==1024){}else{kk(this,a);}}
function ql(){}
_=ql.prototype=new hk();_.hb=km;_.tN=jF+'ListBox';_.tI=33;var jm;function rl(){}
_=rl.prototype=new Du();_.tN=jF+'ListBox$Impl';_.tI=0;function tl(b,a){a.innerText='';}
function vl(b,a){return a.children.length;}
function wl(c,b,a){return b.children[a].text;}
function xl(c,b,a){return b.children[a].value;}
function yl(c,b,a){b.removeChild(b.children[a]);}
function sl(){}
_=sl.prototype=new rl();_.tN=jF+'ListBox$ImplSafari';_.tI=0;function zm(){zm=gC;Em=lA(new pz());}
function ym(b,a){zm();pi(b);if(a===null){a=Am();}b.vb(a);b.gb();return b;}
function Bm(){zm();return Cm(null);}
function Cm(c){zm();var a,b;b=fe(sA(Em,c),9);if(b!==null){return b;}a=null;if(Em.c==0){Dm();}uA(Em,c,b=ym(new tm(),a));return b;}
function Am(){zm();return $doc.body;}
function Dm(){zm();Cg(new um());}
function tm(){}
_=tm.prototype=new oi();_.tN=jF+'RootPanel';_.tI=34;var Em;function wm(){var a,b;for(b=Ax(jy((zm(),Em)));by(b);){a=fe(cy(b),9);if(a.ab()){a.jb();}}}
function xm(){return null;}
function um(){}
_=um.prototype=new Du();_.ob=wm;_.pb=xm;_.tN=jF+'RootPanel$1';_.tI=35;function jn(){jn=gC;mp(),op;}
function hn(b,a){mp(),op;ik(b,a);vn(b,1024);return b;}
function kn(a){return jf(a.y(),'value');}
function ln(a){if(this.a===null){this.a=qj(new pj());}zy(this.a,a);}
function mn(a){var b;kk(this,a);b=df(a);if(b==1){if(this.a!==null){sj(this.a,this);}}else{}}
function gn(){}
_=gn.prototype=new hk();_.l=ln;_.hb=mn;_.tN=jF+'TextBoxBase';_.tI=36;_.a=null;function on(){on=gC;mp(),op;}
function nn(a){mp(),op;hn(a,xe());un(a,'gwt-TextBox');return a;}
function pn(b,a){tf(b.y(),'size',a);}
function fn(){}
_=fn.prototype=new gn();_.tN=jF+'TextBox';_.tI=37;function An(a){a.a=(vk(),wk);a.b=(Ck(),Ek);}
function Bn(a){Fi(a);An(a);uf(a.e,'cellSpacing','0');uf(a.e,'cellPadding','0');return a;}
function Cn(b,d){var a,c;c=De();a=En(b);se(c,a);se(b.d,c);xj(b,d,a);}
function En(b){var a;a=Ce();bj(b,a,b.a);cj(b,a,b.b);return a;}
function Fn(c){var a,b;b=lf(c.y());a=zj(this,c);if(a){pf(this.d,lf(b));}return a;}
function zn(){}
_=zn.prototype=new Ei();_.sb=Fn;_.tN=jF+'VerticalPanel';_.tI=38;function lo(b,a){b.b=a;b.a=Fd('[Lcom.google.gwt.user.client.ui.Widget;',[0],[12],[4],null);return b;}
function mo(a,b){po(a,b,a.c);}
function oo(b,c){var a;for(a=0;a<b.c;++a){if(b.a[a]===c){return a;}}return (-1);}
function po(d,e,a){var b,c;if(a<0||a>d.c){throw new lu();}if(d.c==d.a.a){c=Fd('[Lcom.google.gwt.user.client.ui.Widget;',[0],[12],[d.a.a*2],null);for(b=0;b<d.a.a;++b){be(c,b,d.a[b]);}d.a=c;}++d.c;for(b=d.c-1;b>a;--b){be(d.a,b,d.a[b-1]);}be(d.a,a,e);}
function qo(a){return eo(new co(),a);}
function ro(c,b){var a;if(b<0||b>=c.c){throw new lu();}--c.c;for(a=b;a<c.c;++a){be(c.a,a,c.a[a+1]);}be(c.a,c.c,null);}
function so(b,c){var a;a=oo(b,c);if(a==(-1)){throw new vB();}ro(b,a);}
function bo(){}
_=bo.prototype=new Du();_.tN=jF+'WidgetCollection';_.tI=0;_.a=null;_.b=null;_.c=0;function eo(b,a){b.b=a;return b;}
function go(a){return a.a<a.b.c-1;}
function ho(a){if(a.a>=a.b.c){throw new vB();}return a.b.a[++a.a];}
function io(){return go(this);}
function jo(){return ho(this);}
function ko(){if(this.a<0||this.a>=this.b.c){throw new iu();}this.b.b.sb(this.b.a[this.a--]);}
function co(){}
_=co.prototype=new Du();_.F=io;_.eb=jo;_.qb=ko;_.tN=jF+'WidgetCollection$WidgetIterator';_.tI=0;_.a=(-1);function mp(){mp=gC;np=ip(new hp());op=np!==null?lp(new ap()):np;}
function lp(a){mp();return a;}
function ap(){}
_=ap.prototype=new Du();_.tN=kF+'FocusImpl';_.tI=0;var np,op;function ep(){ep=gC;mp();}
function cp(a){fp(a);gp(a);kp(a);}
function dp(a){ep();lp(a);cp(a);return a;}
function fp(b){return function(a){if(this.parentNode.onblur){this.parentNode.onblur(a);}};}
function gp(b){return function(a){if(this.parentNode.onfocus){this.parentNode.onfocus(a);}};}
function bp(){}
_=bp.prototype=new ap();_.tN=kF+'FocusImplOld';_.tI=0;function jp(){jp=gC;ep();}
function ip(a){jp();dp(a);return a;}
function kp(b){return function(){var a=this.firstChild;$wnd.setTimeout(function(){a.focus();},0);};}
function hp(){}
_=hp.prototype=new bp();_.tN=kF+'FocusImplSafari';_.tI=0;function up(c,a,b){dv(c,b);return c;}
function tp(){}
_=tp.prototype=new cv();_.tN=lF+'DOMException';_.tI=39;function Fp(){Fp=gC;aq=(zs(),jt);}
function bq(a){Fp();return As(aq,a);}
var aq;function vq(b,a){b.a=a;return b;}
function wq(a,b){return b;}
function yq(a){if(ge(a,15)){return te(wq(this,this.a),wq(this,fe(a,15).a));}return false;}
function uq(){}
_=uq.prototype=new Du();_.eQ=yq;_.tN=mF+'DOMItem';_.tI=40;_.a=null;function tr(b,a){vq(b,a);return b;}
function vr(a){return or(new nr(),Cs(a.a));}
function wr(a){return Cr(new Br(),Ds(a.a));}
function xr(a){return dt(a.a);}
function yr(a){return ht(a.a);}
function zr(a){return it(a.a);}
function Ar(a){var b;if(a===null){return null;}b=et(a);switch(b){case 2:return dq(new cq(),a);case 4:return jq(new iq(),a);case 8:return rq(new qq(),a);case 11:return Eq(new Dq(),a);case 9:return cr(new br(),a);case 1:return hr(new gr(),a);case 7:return fs(new es(),a);case 3:return ks(new js(),a);default:return tr(new sr(),a);}}
function sr(){}
_=sr.prototype=new uq();_.tN=mF+'NodeImpl';_.tI=41;function dq(b,a){tr(b,a);return b;}
function fq(a){return ct(a.a);}
function gq(a){return gt(a.a);}
function hq(){var a;a=hv(new gv());kv(a,' '+fq(this));kv(a,'="');kv(a,gq(this));kv(a,'"');return ov(a);}
function cq(){}
_=cq.prototype=new sr();_.tS=hq;_.tN=mF+'AttrImpl';_.tI=42;function nq(b,a){tr(b,a);return b;}
function pq(a){return Es(a.a);}
function mq(){}
_=mq.prototype=new sr();_.tN=mF+'CharacterDataImpl';_.tI=43;function ks(b,a){nq(b,a);return b;}
function ms(){var a,b,c;a=hv(new gv());c=wv(pq(this),'(?=[;&<>\'"])',(-1));for(b=0;b<c.a;b++){if(xv(c[b],';')){kv(a,'&semi;');kv(a,yv(c[b],1));}else if(xv(c[b],'&')){kv(a,'&amp;');kv(a,yv(c[b],1));}else if(xv(c[b],'"')){kv(a,'&quot;');kv(a,yv(c[b],1));}else if(xv(c[b],"'")){kv(a,'&apos;');kv(a,yv(c[b],1));}else if(xv(c[b],'<')){kv(a,'&lt;');kv(a,yv(c[b],1));}else if(xv(c[b],'>')){kv(a,'&gt;');kv(a,yv(c[b],1));}else{kv(a,c[b]);}}return ov(a);}
function js(){}
_=js.prototype=new mq();_.tS=ms;_.tN=mF+'TextImpl';_.tI=44;function jq(b,a){ks(b,a);return b;}
function lq(){var a;a=iv(new gv(),'<![CDATA[');kv(a,pq(this));kv(a,']]>');return ov(a);}
function iq(){}
_=iq.prototype=new js();_.tS=lq;_.tN=mF+'CDATASectionImpl';_.tI=45;function rq(b,a){nq(b,a);return b;}
function tq(){var a;a=iv(new gv(),'<!--');kv(a,pq(this));kv(a,'-->');return ov(a);}
function qq(){}
_=qq.prototype=new mq();_.tS=tq;_.tN=mF+'CommentImpl';_.tI=46;function Aq(c,a,b){up(c,12,'Failed to parse: '+Cq(a));kw(c,b);return c;}
function Cq(a){return zv(a,0,tu(uv(a),128));}
function zq(){}
_=zq.prototype=new tp();_.tN=mF+'DOMParseException';_.tI=47;function Eq(b,a){tr(b,a);return b;}
function ar(){var a,b;a=hv(new gv());for(b=0;b<wr(this).B();b++){jv(a,wr(this).bb(b));}return ov(a);}
function Dq(){}
_=Dq.prototype=new sr();_.tS=ar;_.tN=mF+'DocumentFragmentImpl';_.tI=48;function cr(b,a){tr(b,a);return b;}
function er(){return fe(Ar(Fs(this.a)),16);}
function fr(){var a,b,c;a=hv(new gv());b=wr(this);for(c=0;c<b.B();c++){kv(a,b.bb(c).tS());}return ov(a);}
function br(){}
_=br.prototype=new sr();_.w=er;_.tS=fr;_.tN=mF+'DocumentImpl';_.tI=49;function hr(b,a){tr(b,a);return b;}
function jr(a){return ft(a.a);}
function kr(a){return Bs(this.a,a);}
function lr(a){return Cr(new Br(),at(this.a,a));}
function mr(){var a;a=iv(new gv(),'<');kv(a,jr(this));if(yr(this)){kv(a,as(vr(this)));}if(zr(this)){kv(a,'>');kv(a,as(wr(this)));kv(a,'<\/');kv(a,jr(this));kv(a,'>');}else{kv(a,'/>');}return ov(a);}
function gr(){}
_=gr.prototype=new sr();_.v=kr;_.z=lr;_.tS=mr;_.tN=mF+'ElementImpl';_.tI=50;function Cr(b,a){vq(b,a);return b;}
function Er(a){return bt(a.a);}
function Fr(b,a){return Ar(kt(b.a,a));}
function as(c){var a,b;a=hv(new gv());for(b=0;b<c.B();b++){kv(a,c.bb(b).tS());}return ov(a);}
function bs(){return Er(this);}
function cs(a){return Fr(this,a);}
function ds(){return as(this);}
function Br(){}
_=Br.prototype=new uq();_.B=bs;_.bb=cs;_.tS=ds;_.tN=mF+'NodeListImpl';_.tI=51;function or(b,a){Cr(b,a);return b;}
function qr(){return Er(this);}
function rr(a){return Fr(this,a);}
function nr(){}
_=nr.prototype=new Br();_.B=qr;_.bb=rr;_.tN=mF+'NamedNodeMapImpl';_.tI=52;function fs(b,a){tr(b,a);return b;}
function hs(a){return Es(a.a);}
function is(){var a;a=iv(new gv(),'<?');kv(a,xr(this));kv(a,' ');kv(a,hs(this));kv(a,'?>');return ov(a);}
function es(){}
_=es.prototype=new sr();_.tS=is;_.tN=mF+'ProcessingInstructionImpl';_.tI=53;function zs(){zs=gC;jt=ps(new os());}
function ys(a){zs();return a;}
function As(e,c){var a,d;try{return fe(Ar(ss(e,c)),17);}catch(a){a=ne(a);if(ge(a,18)){d=a;throw Aq(new zq(),c,d);}else throw a;}}
function Bs(b,a){zs();return b.getAttribute(a);}
function Cs(a){zs();return a.attributes;}
function Ds(b){zs();var a=b.childNodes;return a==null?null:a;}
function Es(a){zs();return a.data;}
function Fs(a){zs();return a.documentElement;}
function at(a,b){zs();return rs(jt,a,b);}
function bt(a){zs();return a.length;}
function ct(a){zs();return a.name;}
function dt(a){zs();var b=a.nodeName;return b==null?null:b;}
function et(a){zs();var b=a.nodeType;return b==null?-1:b;}
function ft(a){zs();return a.tagName;}
function gt(a){zs();return a.value;}
function ht(a){zs();return a.attributes.length!=0;}
function it(a){zs();return a.hasChildNodes();}
function kt(c,a){zs();if(a>=c.length){return null;}var b=c.item(a);return b==null?null:b;}
function ns(){}
_=ns.prototype=new Du();_.tN=mF+'XMLParserImpl';_.tI=0;var jt;function ws(){ws=gC;zs();}
function us(a){a.a=xs();}
function vs(a){ws();ys(a);us(a);return a;}
function xs(){ws();return new DOMParser();}
function ts(){}
_=ts.prototype=new ns();_.tN=mF+'XMLParserImplStandard';_.tI=0;function qs(){qs=gC;ws();}
function ps(a){qs();vs(a);return a;}
function rs(c,a,b){return a.getElementsByTagName(b);}
function ss(g,a){var b=g.a;var e=b.parseFromString(a,'text/xml');var d=e.getElementsByTagName('parsererror');if(d.length>0){var c=d.item(0);var f='white-space: pre; border: 2px solid #c77; padding: 0 1em 0 1em; margin: 1em; background-color: #fdd; color: black';if(c.getAttribute('style')==f){throw new Error(c.item(1).innerHTML);}}return e;}
function os(){}
_=os.prototype=new ts();_.tN=mF+'XMLParserImplSafari';_.tI=0;function ot(){}
_=ot.prototype=new Du();_.tN=nF+'OutputStream';_.tI=0;function mt(){}
_=mt.prototype=new ot();_.tN=nF+'FilterOutputStream';_.tI=0;function qt(){}
_=qt.prototype=new mt();_.tN=nF+'PrintStream';_.tI=0;function st(){}
_=st.prototype=new cv();_.tN=oF+'ArrayStoreException';_.tI=54;function wt(){wt=gC;xt=vt(new ut(),false);yt=vt(new ut(),true);}
function vt(a,b){wt();a.a=b;return a;}
function zt(a){return ge(a,19)&&fe(a,19).a==this.a;}
function At(){var a,b;b=1231;a=1237;return this.a?1231:1237;}
function Bt(){return this.a?'true':'false';}
function Ct(a){wt();return a?yt:xt;}
function ut(){}
_=ut.prototype=new Du();_.eQ=zt;_.hC=At;_.tS=Bt;_.tN=oF+'Boolean';_.tI=55;_.a=false;var xt,yt;function Et(){}
_=Et.prototype=new cv();_.tN=oF+'ClassCastException';_.tI=56;function gu(b,a){dv(b,a);return b;}
function fu(){}
_=fu.prototype=new cv();_.tN=oF+'IllegalArgumentException';_.tI=57;function ju(b,a){dv(b,a);return b;}
function iu(){}
_=iu.prototype=new cv();_.tN=oF+'IllegalStateException';_.tI=58;function mu(b,a){dv(b,a);return b;}
function lu(){}
_=lu.prototype=new cv();_.tN=oF+'IndexOutOfBoundsException';_.tI=59;function Au(){Au=gC;{Cu();}}
function Cu(){Au();Bu=/^[+-]?\d*\.?\d*(e[+-]?\d+)?$/i;}
var Bu=null;function pu(){pu=gC;Au();}
function qu(a){pu();return bw(a);}
function tu(a,b){return a<b?a:b;}
function uu(){}
_=uu.prototype=new cv();_.tN=oF+'NegativeArraySizeException';_.tI=60;function xu(b,a){dv(b,a);return b;}
function wu(){}
_=wu.prototype=new cv();_.tN=oF+'NullPointerException';_.tI=61;function sv(b,a){if(!ge(a,1))return false;return Cv(b,a);}
function tv(b,a){return b.indexOf(a);}
function uv(a){return a.length;}
function vv(b,a){return wv(b,a,0);}
function wv(j,i,g){var a=new RegExp(i,'g');var h=[];var b=0;var k=j;var e=null;while(true){var f=a.exec(k);if(f==null||(k==''||b==g-1&&g>0)){h[b]=k;break;}else{h[b]=k.substring(0,f.index);k=k.substring(f.index+f[0].length,k.length);a.lastIndex=0;if(e==k){h[b]=k.substring(0,1);k=k.substring(1);}e=k;b++;}}if(g==0){for(var c=h.length-1;c>=0;c--){if(h[c]!=''){h.splice(c+1,h.length-(c+1));break;}}}var d=Bv(h.length);var c=0;for(c=0;c<h.length;++c){d[c]=h[c];}return d;}
function xv(b,a){return tv(b,a)==0;}
function yv(b,a){return b.substr(a,b.length-a);}
function zv(c,a,b){return c.substr(a,b-a);}
function Av(c){var a=c.replace(/^(\s*)/,'');var b=a.replace(/\s*$/,'');return b;}
function Bv(a){return Fd('[Ljava.lang.String;',[0],[1],[a],null);}
function Cv(a,b){return String(a)==b;}
function Dv(a){return sv(this,a);}
function Fv(){var a=Ev;if(!a){a=Ev={};}var e=':'+this;var b=a[e];if(b==null){b=0;var f=this.length;var d=f<64?1:f/32|0;for(var c=0;c<f;c+=d){b<<=1;b+=this.charCodeAt(c);}b|=0;a[e]=b;}return b;}
function aw(){return this;}
function bw(a){return ''+a;}
function cw(a){return a!==null?a.tS():'null';}
_=String.prototype;_.eQ=Dv;_.hC=Fv;_.tS=aw;_.tN=oF+'String';_.tI=2;var Ev=null;function hv(a){lv(a);return a;}
function iv(b,a){mv(b,a);return b;}
function jv(a,b){return kv(a,cw(b));}
function kv(c,d){if(d===null){d='null';}var a=c.js.length-1;var b=c.js[a].length;if(c.length>b*b){c.js[a]=c.js[a]+d;}else{c.js.push(d);}c.length+=d.length;return c;}
function lv(a){mv(a,'');}
function mv(b,a){b.js=[a];b.length=a.length;}
function ov(a){a.fb();return a.js[0];}
function pv(){if(this.js.length>1){this.js=[this.js.join('')];this.length=this.js[0].length;}}
function qv(){return ov(this);}
function gv(){}
_=gv.prototype=new Du();_.fb=pv;_.tS=qv;_.tN=oF+'StringBuffer';_.tI=0;function ew(){ew=gC;fw=new qt();}
function gw(a){ew();return C(a);}
var fw;function pw(b,a){dv(b,a);return b;}
function ow(){}
_=ow.prototype=new cv();_.tN=oF+'UnsupportedOperationException';_.tI=62;function zw(b,a){b.c=a;return b;}
function Bw(a){return a.a<a.c.yb();}
function Cw(){return Bw(this);}
function Dw(){if(!Bw(this)){throw new vB();}return this.c.D(this.b=this.a++);}
function Ew(){if(this.b<0){throw new iu();}this.c.rb(this.b);this.a=this.b;this.b=(-1);}
function yw(){}
_=yw.prototype=new Du();_.F=Cw;_.eb=Dw;_.qb=Ew;_.tN=pF+'AbstractList$IteratorImpl';_.tI=0;_.a=0;_.b=(-1);function hy(f,d,e){var a,b,c;for(b=gA(f.s());Ez(b);){a=Fz(b);c=a.A();if(d===null?c===null:d.eQ(c)){if(e){aA(b);}return a;}}return null;}
function iy(b){var a;a=b.s();return jx(new ix(),b,a);}
function jy(b){var a;a=rA(b);return yx(new xx(),b,a);}
function ky(a){return hy(this,a,false)!==null;}
function ly(d){var a,b,c,e,f,g,h;if(d===this){return true;}if(!ge(d,21)){return false;}f=fe(d,21);c=iy(this);e=f.db();if(!sy(c,e)){return false;}for(a=lx(c);sx(a);){b=tx(a);h=this.E(b);g=f.E(b);if(h===null?g!==null:!h.eQ(g)){return false;}}return true;}
function my(b){var a;a=hy(this,b,false);return a===null?null:a.C();}
function ny(){var a,b,c;b=0;for(c=gA(this.s());Ez(c);){a=Fz(c);b+=a.hC();}return b;}
function oy(){return iy(this);}
function py(){var a,b,c,d;d='{';a=false;for(c=gA(this.s());Ez(c);){b=Fz(c);if(a){d+=', ';}else{a=true;}d+=cw(b.A());d+='=';d+=cw(b.C());}return d+'}';}
function hx(){}
_=hx.prototype=new Du();_.o=ky;_.eQ=ly;_.E=my;_.hC=ny;_.db=oy;_.tS=py;_.tN=pF+'AbstractMap';_.tI=63;function sy(e,b){var a,c,d;if(b===e){return true;}if(!ge(b,22)){return false;}c=fe(b,22);if(c.yb()!=e.yb()){return false;}for(a=c.cb();a.F();){d=a.eb();if(!e.p(d)){return false;}}return true;}
function ty(a){return sy(this,a);}
function uy(){var a,b,c;a=0;for(b=this.cb();b.F();){c=b.eb();if(c!==null){a+=c.hC();}}return a;}
function qy(){}
_=qy.prototype=new rw();_.eQ=ty;_.hC=uy;_.tN=pF+'AbstractSet';_.tI=64;function jx(b,a,c){b.a=a;b.b=c;return b;}
function lx(b){var a;a=gA(b.b);return qx(new px(),b,a);}
function mx(a){return this.a.o(a);}
function nx(){return lx(this);}
function ox(){return this.b.a.c;}
function ix(){}
_=ix.prototype=new qy();_.p=mx;_.cb=nx;_.yb=ox;_.tN=pF+'AbstractMap$1';_.tI=65;function qx(b,a,c){b.a=c;return b;}
function sx(a){return a.a.F();}
function tx(b){var a;a=b.a.eb();return a.A();}
function ux(){return sx(this);}
function vx(){return tx(this);}
function wx(){this.a.qb();}
function px(){}
_=px.prototype=new Du();_.F=ux;_.eb=vx;_.qb=wx;_.tN=pF+'AbstractMap$2';_.tI=0;function yx(b,a,c){b.a=a;b.b=c;return b;}
function Ax(b){var a;a=gA(b.b);return Fx(new Ex(),b,a);}
function Bx(a){return qA(this.a,a);}
function Cx(){return Ax(this);}
function Dx(){return this.b.a.c;}
function xx(){}
_=xx.prototype=new rw();_.p=Bx;_.cb=Cx;_.yb=Dx;_.tN=pF+'AbstractMap$3';_.tI=0;function Fx(b,a,c){b.a=c;return b;}
function by(a){return a.a.F();}
function cy(a){var b;b=a.a.eb().C();return b;}
function dy(){return by(this);}
function ey(){return cy(this);}
function fy(){this.a.qb();}
function Ex(){}
_=Ex.prototype=new Du();_.F=dy;_.eb=ey;_.qb=fy;_.tN=pF+'AbstractMap$4';_.tI=0;function oA(){oA=gC;wA=CA();}
function kA(a){{nA(a);}}
function lA(a){oA();kA(a);return a;}
function mA(a,b){oA();kA(a);tA(a,b);return a;}
function nA(a){a.a=gb();a.d=ib();a.b=ke(wA,cb);a.c=0;}
function pA(b,a){if(ge(a,1)){return aB(b.d,fe(a,1))!==wA;}else if(a===null){return b.b!==wA;}else{return FA(b.a,a,a.hC())!==wA;}}
function qA(a,b){if(a.b!==wA&&EA(a.b,b)){return true;}else if(BA(a.d,b)){return true;}else if(zA(a.a,b)){return true;}return false;}
function rA(a){return eA(new Az(),a);}
function sA(c,a){var b;if(ge(a,1)){b=aB(c.d,fe(a,1));}else if(a===null){b=c.b;}else{b=FA(c.a,a,a.hC());}return b===wA?null:b;}
function uA(c,a,d){var b;if(ge(a,1)){b=dB(c.d,fe(a,1),d);}else if(a===null){b=c.b;c.b=d;}else{b=cB(c.a,a,d,a.hC());}if(b===wA){++c.c;return null;}else{return b;}}
function tA(d,c){var a,b;b=gA(rA(c));while(Ez(b)){a=Fz(b);uA(d,a.A(),a.C());}}
function vA(c,a){var b;if(ge(a,1)){b=fB(c.d,fe(a,1));}else if(a===null){b=c.b;c.b=ke(wA,cb);}else{b=eB(c.a,a,a.hC());}if(b===wA){return null;}else{--c.c;return b;}}
function xA(e,c){oA();for(var d in e){if(d==parseInt(d)){var a=e[d];for(var f=0,b=a.length;f<b;++f){c.n(a[f]);}}}}
function yA(d,a){oA();for(var c in d){if(c.charCodeAt(0)==58){var e=d[c];var b=tz(c.substring(1),e);a.n(b);}}}
function zA(f,h){oA();for(var e in f){if(e==parseInt(e)){var a=f[e];for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.C();if(EA(h,d)){return true;}}}}return false;}
function AA(a){return pA(this,a);}
function BA(c,d){oA();for(var b in c){if(b.charCodeAt(0)==58){var a=c[b];if(EA(d,a)){return true;}}}return false;}
function CA(){oA();}
function DA(){return rA(this);}
function EA(a,b){oA();if(a===b){return true;}else if(a===null){return false;}else{return a.eQ(b);}}
function bB(a){return sA(this,a);}
function FA(f,h,e){oA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(EA(h,d)){return c.C();}}}}
function aB(b,a){oA();return b[':'+a];}
function cB(f,h,j,e){oA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(EA(h,d)){var i=c.C();c.xb(j);return i;}}}else{a=f[e]=[];}var c=tz(h,j);a.push(c);}
function dB(c,a,d){oA();a=':'+a;var b=c[a];c[a]=d;return b;}
function eB(f,h,e){oA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(EA(h,d)){if(a.length==1){delete f[e];}else{a.splice(g,1);}return c.C();}}}}
function fB(c,a){oA();a=':'+a;var b=c[a];delete c[a];return b;}
function pz(){}
_=pz.prototype=new hx();_.o=AA;_.s=DA;_.E=bB;_.tN=pF+'HashMap';_.tI=66;_.a=null;_.b=null;_.c=0;_.d=null;var wA;function rz(b,a,c){b.a=a;b.b=c;return b;}
function tz(a,b){return rz(new qz(),a,b);}
function uz(b){var a;if(ge(b,23)){a=fe(b,23);if(EA(this.a,a.A())&&EA(this.b,a.C())){return true;}}return false;}
function vz(){return this.a;}
function wz(){return this.b;}
function xz(){var a,b;a=0;b=0;if(this.a!==null){a=this.a.hC();}if(this.b!==null){b=this.b.hC();}return a^b;}
function yz(a){var b;b=this.b;this.b=a;return b;}
function zz(){return this.a+'='+this.b;}
function qz(){}
_=qz.prototype=new Du();_.eQ=uz;_.A=vz;_.C=wz;_.hC=xz;_.xb=yz;_.tS=zz;_.tN=pF+'HashMap$EntryImpl';_.tI=67;_.a=null;_.b=null;function eA(b,a){b.a=a;return b;}
function gA(a){return Cz(new Bz(),a.a);}
function hA(c){var a,b,d;if(ge(c,23)){a=fe(c,23);b=a.A();if(pA(this.a,b)){d=sA(this.a,b);return EA(a.C(),d);}}return false;}
function iA(){return gA(this);}
function jA(){return this.a.c;}
function Az(){}
_=Az.prototype=new qy();_.p=hA;_.cb=iA;_.yb=jA;_.tN=pF+'HashMap$EntrySet';_.tI=68;function Cz(c,b){var a;c.c=b;a=xy(new vy());if(c.c.b!==(oA(),wA)){zy(a,rz(new qz(),null,c.c.b));}yA(c.c.d,a);xA(c.c.a,a);c.a=a.cb();return c;}
function Ez(a){return a.a.F();}
function Fz(a){return a.b=fe(a.a.eb(),23);}
function aA(a){if(a.b===null){throw ju(new iu(),'Must call next() before remove().');}else{a.a.qb();vA(a.c,a.b.A());a.b=null;}}
function bA(){return Ez(this);}
function cA(){return Fz(this);}
function dA(){aA(this);}
function Bz(){}
_=Bz.prototype=new Du();_.F=bA;_.eb=cA;_.qb=dA;_.tN=pF+'HashMap$EntrySetIterator';_.tI=0;_.a=null;_.b=null;function hB(a){a.a=lA(new pz());return a;}
function jB(a){var b;b=uA(this.a,a,Ct(true));return b===null;}
function kB(a){return pA(this.a,a);}
function lB(){return lx(iy(this.a));}
function mB(){return this.a.c;}
function nB(){return iy(this.a).tS();}
function gB(){}
_=gB.prototype=new qy();_.n=jB;_.p=kB;_.cb=lB;_.yb=mB;_.tS=nB;_.tN=pF+'HashSet';_.tI=69;_.a=null;function tB(d,c,a,b){dv(d,c);return d;}
function sB(){}
_=sB.prototype=new cv();_.tN=pF+'MissingResourceException';_.tI=70;function vB(){}
_=vB.prototype=new cv();_.tN=pF+'NoSuchElementException';_.tI=71;function AB(a){a.a=xy(new vy());return a;}
function BB(b,a){return zy(b.a,a);}
function DB(b,a){return EB(b,a);}
function EB(b,a){return Dy(b.a,a);}
function FB(a,b){yy(this.a,a,b);}
function aC(a){return BB(this,a);}
function bC(a){return Cy(this.a,a);}
function cC(a){return EB(this,a);}
function dC(){return this.a.cb();}
function eC(a){return az(this.a,a);}
function fC(){return this.a.b;}
function zB(){}
_=zB.prototype=new xw();_.m=FB;_.n=aC;_.p=bC;_.D=cC;_.cb=dC;_.rb=eC;_.yb=fC;_.tN=pF+'Vector';_.tI=72;_.a=null;function EC(g,h){var a,c,d,e,f;c=jD(new hD(),h);try{e=aF(c);f=wC(new vC(),g,e,c);rg(f,1);}catch(a){a=ne(a);if(ge(a,25)){d=a;lw(d);}else throw a;}}
function FC(g,h){var a,c,d,e,f;c=sD(new qD(),h);try{e=aF(c);f=AC(new zC(),g,e,c);rg(f,1);}catch(a){a=ne(a);if(ge(a,25)){d=a;Dg('Exception: '+d.b);lw(d);}else throw a;}}
function aD(r){var a,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,s;k='DEFAULT-identities-and-usecases.xml';l='DEFAULT-policy.xml';f='DEFAULT-cancel.html';m='DEFAULT-save-policy.xml';try{h=td('getURLs');k=qd(h,'identities-url');l=qd(h,'policy-url');f=qd(h,'cancel-url');m=qd(h,'save-url');}catch(a){a=ne(a);if(ge(a,24)){i=a;Dg('Exception: '+i.b);}else throw a;}FC(r,l);EC(r,k);s=Bn(new zn());qi(Bm(),s);p=Bn(new zn());Cn(s,p);q=nn(new fn());pn(q,30);Cn(p,q);o=Bi(new ui(),'Save User or Group',jC(new iC(),r,q));Cn(p,o);j=dl(new bl());hl(j,(Ck(),Dk));Cn(s,j);d=dl(new bl());Cn(s,d);n=m;r.g=Bi(new ui(),'Save Policy',nC(new mC(),r,n));un(r.g,'gwt-wyona-SaveButton');el(d,r.g);g=f;e=Bi(new ui(),'Cancel',rC(new qC(),r,g));un(r.g,'gwt-wyona-CancelButton');el(d,e);r.b=dE(new bE(),r.j,r.i,r.a);r.d=jE(new hE(),r.j,r.e,r.c,r.h);c=dD(new bD(),r.b.a,r.d.c,r.d);un(c,'gwt-wyona-AddRemoveWidget');el(j,r.b);el(j,c);el(j,r.d);}
function hC(){}
_=hC.prototype=new Du();_.tN=qF+'AccessPolicyEditor';_.tI=0;_.a=null;_.b=null;_.c=null;_.d=null;_.e=null;_.f=null;_.g=null;_.h=true;_.i=null;_.j=10;function jC(b,a,c){b.a=a;b.b=c;return b;}
function lC(d){var a,b,c;b=am(this.a.b.a);for(a=0;a<b;a++){c=bm(this.a.b.a,a);if(tv(c,kn(this.b))>=0)Dg('Result: '+c);}}
function iC(){}
_=iC.prototype=new Du();_.ib=lC;_.tN=qF+'AccessPolicyEditor$1';_.tI=73;function nC(b,a,c){b.a=a;b.b=c;return b;}
function pC(f){var a,c,d,e;c=zD(new yD(),this.b);try{e=BD(c,rE(this.a.d),mE(this.a.d),qE(this.a.d));}catch(a){a=ne(a);if(ge(a,25)){d=a;Dg('Exception: '+d.b);}else throw a;}}
function mC(){}
_=mC.prototype=new Du();_.ib=pC;_.tN=qF+'AccessPolicyEditor$2';_.tI=74;function rC(b,a,c){b.a=c;return b;}
function tC(a,b){$wnd.location.href=b;}
function uC(a){Dg('Redirect to '+this.a);tC(this,this.a);}
function qC(){}
_=qC.prototype=new Du();_.ib=uC;_.tN=qF+'AccessPolicyEditor$3';_.tI=75;function xC(){xC=gC;og();}
function wC(b,a,d,c){xC();b.a=a;b.c=d;b.b=c;mg(b);return b;}
function yC(){if(xc(this.c)){qg(this,10);}else{this.a.i=oD(this.b);this.a.a=mD(this.b);this.a.f=nD(this.b);fE(this.a.b,this.a.j,this.a.i,this.a.a);ng(this);Dg('Identities have been loaded!');}}
function vC(){}
_=vC.prototype=new hg();_.ub=yC;_.tN=qF+'AccessPolicyEditor$4';_.tI=76;function BC(){BC=gC;og();}
function AC(b,a,d,c){BC();b.a=a;b.c=d;b.b=c;mg(b);return b;}
function CC(){if(xc(this.c)){qg(this,10);}else{this.a.e=wD(this.b);this.a.c=vD(this.b);tE(this.a.d,this.a.j,this.a.e,this.a.c);this.a.h=this.b.b;uE(this.a.d,this.a.h);ng(this);Dg('Policy has been loaded!');}}
function zC(){}
_=zC.prototype=new hg();_.ub=CC;_.tN=qF+'AccessPolicyEditor$5';_.tI=77;function cD(a){a.b=ek(new dk());}
function dD(d,a,c,b){cD(d);Ej(d,d.b);d.e=Bi(new ui(),'<',d);fk(d.b,d.e);d.a=Bi(new ui(),'>',d);fk(d.b,d.a);d.c=a;d.d=c;return d;}
function fD(b,a){if(tv(a,'(')>0){return zv(a,0,tv(a,'('));}else{return a;}}
function gD(c){var a,b;if(c===this.a){a=cm(this.c);if(a>=0){b=dm(this.c,a);Dg('Add selected identity '+b+' to policy');gm(this.c,a);Cl(this.d,zv(b,0,1)+': (-,-) '+Av(yv(b,2)),b);}else{Dg('No identity selected yet! Please select an identity.');}}else if(c===this.e){a=cm(this.d);if(a>=0){b=dm(this.d,a);Dg('Remove selected identity '+b+' from policy');gm(this.d,a);Bl(this.c,fD(this,b));}else{Dg('No identity selected yet! Please select an identity.');}}}
function bD(){}
_=bD.prototype=new Cj();_.ib=gD;_.tN=qF+'AddRemoveIdentitiesWidget';_.tI=78;_.a=null;_.c=null;_.d=null;_.e=null;function CE(a){a.d=lA(new pz());}
function DE(a,b){CE(a);a.e=Db(new yb(),(Fb(),dc),b);bF(a);return a;}
function EE(e){var a,b,c,d;b='';a=mA(new pz(),e.d);for(d=gA(rA(a));Ez(d);){c=Fz(d);b+=c.A()+''+c.C();if(Ez(d)){b+='&';}}return b;}
function aF(a){return ac(a.e,EE(a),a);}
function bF(a){bc(a.e,'Content-Type','application/x-www-form-urlencoded');}
function cF(b,a){Dg('Exception: '+a.b);}
function BE(){}
_=BE.prototype=new Du();_.kb=cF;_.tN=rF+'AsynchronousAgent';_.tI=0;_.e=null;function iD(a){a.c=AB(new zB());a.a=AB(new zB());a.b=AB(new zB());}
function jD(a,b){DE(a,b);iD(a);return a;}
function lD(d,c,a){var b;b=c.z(a);return fe(b.bb(0),16);}
function mD(c){var a,b;a=Fd('[Ljava.lang.String;',[0],[1],[c.a.a.b],null);for(b=0;b<c.a.a.b;b++){a[b]=fe(DB(c.a,b),1);}return a;}
function nD(c){var a,b;b=Fd('[Ljava.lang.String;',[0],[1],[c.b.a.b],null);for(a=0;a<c.b.a.b;a++){b[a]=fe(DB(c.b,a),1);}return b;}
function oD(b){var a,c;c=Fd('[Ljava.lang.String;',[0],[1],[b.c.a.b],null);for(a=0;a<b.c.a.b;a++){c[a]=fe(DB(b.c,a),1);}return c;}
function pD(d,e){var a,b,c,f,g,h,i,j;h=bq(tb(e)).w();j=lD(this,h,'users');i=j.z('user');for(c=0;c<i.B();c++){BB(this.c,fe(i.bb(c),16).v('id'));}b=lD(this,h,'groups');a=b.z('group');for(c=0;c<a.B();c++){BB(this.a,fe(a.bb(c),16).v('id'));}g=lD(this,h,'rights');f=g.z('right');for(c=0;c<f.B();c++){BB(this.b,fe(f.bb(c),16).v('id'));}}
function hD(){}
_=hD.prototype=new BE();_.mb=pD;_.tN=qF+'AsynchronousIdentitiesAndRightsGetter';_.tI=0;function rD(a){a.c=AB(new zB());a.a=AB(new zB());}
function sD(a,b){DE(a,b);rD(a);return a;}
function uD(d,c,a){var b;b=c.z(a);if(b.B()>0){return fe(b.bb(0),16);}else{return null;}}
function vD(c){var a,b;b=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[27],[c.a.a.b],null);for(a=0;a<b.a;a++){b[a]=fe(DB(c.a,a),27);}return b;}
function wD(c){var a,b;b=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[26],[c.c.a.b],null);for(a=0;a<b.a;a++){b[a]=fe(DB(c.c,a),26);}return b;}
function xD(c,d){var a,b,e,f,g,h,i;f=bq(tb(d)).w();g=f.v('use-inherited-policies');if(g===null){this.b=true;}else{if(sv(g,'false')){this.b=false;}else{this.b=true;}}i=uD(this,f,'world');h=f.z('user');for(b=0;b<h.B();b++){e=ae('[Ljava.lang.String;',0,1,['Write','Read']);BB(this.c,zE(new yE(),fe(h.bb(b),16).v('id'),e));}a=f.z('group');for(b=0;b<a.B();b++){e=ae('[Ljava.lang.String;',0,1,['Write','Read']);BB(this.a,FD(new ED(),fe(a.bb(b),16).v('id'),e));}}
function qD(){}
_=qD.prototype=new BE();_.mb=xD;_.tN=qF+'AsynchronousPolicyGetter';_.tI=0;_.b=true;function zD(a,b){Dg('Save policy to: '+b);a.a=Db(new yb(),(Fb(),ec),b);return a;}
function BD(f,h,b,g){var a,c,d,e;a=iv(new gv(),'<?xml version="1.0"?>');kv(a,'<policy xmlns="http://www.wyona.org/security/1.0" use-inherited-policies="'+g+'">');if(h!==null){for(c=0;c<h.a;c++){kv(a,'<user id="'+h[c].a+'">');e=h[c].b;if(e!==null){for(d=0;d<e.a;d++){kv(a,'<right id="'+e[d]+'">'+e[d]+'<\/right>');}}kv(a,'<\/user>');}}if(b!==null){for(c=0;c<b.a;c++){kv(a,'<group id="'+b[c].a+'">');e=b[c].b;if(e!==null){for(d=0;d<e.a;d++){kv(a,'<right id="'+e[d]+'">'+e[d]+'<\/right>');}}kv(a,'<\/group>');}}kv(a,'<\/policy>');return ac(f.a,ov(a),f);}
function CD(b,a){Dg('Exception: '+a.b);}
function DD(a,b){if(sb(b)==200){Dg('Policy has been saved successfully!');}else{Dg('Policy has NOT been saved! Please check log files on server.');}}
function yD(){}
_=yD.prototype=new Du();_.kb=CD;_.mb=DD;_.tN=qF+'AsynchronousPolicySetter';_.tI=0;_.a=null;function FD(c,a,b){c.a=a;c.b=b;return c;}
function ED(){}
_=ED.prototype=new Du();_.tN=qF+'Group';_.tI=79;_.a=null;_.b=null;function cE(a){a.b=Bn(new zn());}
function dE(b,d,c,a){cE(b);Ej(b,b.b);Cn(b.b,ml(new kl(),'Identities'));b.a=Al(new ql(),true);b.a.l(b);fE(b,d,c,a);Cn(b.b,b.a);return b;}
function fE(c,e,d,a){var b;El(c.a);im(c.a,e);if(d!==null){for(b=0;b<d.a;b++){Bl(c.a,'u: '+d[b]);}}else{Bl(c.a,'No users yet!');}if(a!==null){for(b=0;b<a.a;b++){Bl(c.a,'g: '+a[b]);}}else{Bl(c.a,'No groups yet!');}}
function gE(a){}
function bE(){}
_=bE.prototype=new Cj();_.ib=gE;_.tN=qF+'IdentitiesListBoxWidget';_.tI=80;_.a=null;function iE(a){a.f=Bn(new zn());}
function jE(b,e,d,a,c){iE(b);Ej(b,b.f);Cn(b.f,ml(new kl(),'Policy'));b.d=gj(new dj(),'Inherit rights from parent policies');uE(b,c);Cn(b.f,b.d);b.c=Al(new ql(),true);b.c.l(b);tE(b,e,d,a);Cn(b.f,b.c);b.e=gj(new dj(),'Read');b.e.l(b);Cn(b.f,b.e);b.g=gj(new dj(),'Write');b.g.l(b);Cn(b.f,b.g);return b;}
function kE(g,a,f){var b,c,d,e;e=AB(new zB());for(c=0;c<a.a;c++){BB(e,a[c]);}b=false;for(c=0;c<a.a;c++){if(sv(a[c],f)){b=true;break;}}if(!b)BB(e,f);d=Fd('[Ljava.lang.String;',[0],[1],[e.a.b],null);for(c=0;c<d.a;c++){d[c]=fe(DB(e,c),1);}return d;}
function mE(g){var a,b,c,d,e,f;b=AB(new zB());for(c=0;c<am(g.c);c++){e=bm(g.c,c);f=oE(g,e);d=nE(g,c);if(xv(d,'g:')){BB(b,FD(new ED(),Av(yv(d,2)),f));}}a=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[27],[b.a.b],null);for(c=0;c<a.a;c++){a[c]=fe(DB(b,c),27);}return a;}
function nE(b,a){return dm(b.c,a);}
function oE(f,b){var a,c,d,e;if(tv(b,'(')>0){e=vv(zv(b,tv(b,'(')+1,tv(b,')')),',');c=AB(new zB());for(a=0;a<e.a;a++){if(!sv(e[a],'-'))BB(c,e[a]);}d=Fd('[Ljava.lang.String;',[0],[1],[c.a.b],null);for(a=0;a<d.a;a++){d[a]=fe(DB(c,a),1);}return d;}else{return Fd('[Ljava.lang.String;',[0],[1],[0],null);}}
function pE(b){var a;a=cm(b.c);if(a>=0){return bm(b.c,a);}return null;}
function qE(a){return ij(a.d);}
function rE(e){var a,b,c,d,f,g;g=AB(new zB());for(a=0;a<am(e.c);a++){c=bm(e.c,a);d=oE(e,c);b=nE(e,a);if(xv(b,'u:')){BB(g,zE(new yE(),Av(yv(b,2)),d));}}f=Fd('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[26],[g.a.b],null);for(a=0;a<f.a;a++){f[a]=fe(DB(g,a),26);}return f;}
function sE(f,a,e){var b,c,d;d=AB(new zB());for(b=0;b<a.a;b++){if(!sv(a[b],e)){BB(d,a[b]);}}c=Fd('[Ljava.lang.String;',[0],[1],[d.a.b],null);for(b=0;b<c.a;b++){c[b]=fe(DB(d,b),1);}return c;}
function tE(d,g,e,a){var b,c,f;El(d.c);im(d.c,g);if(e!==null||a!==null){if(e!==null){for(b=0;b<e.a;b++){c='u: ('+d.a+','+d.b+') '+e[b].a;f='u: '+e[b].a;Cl(d.c,c,f);}}if(a!==null){for(b=0;b<a.a;b++){c='g: ('+d.a+','+d.b+') '+a[b].a;f='g: '+a[b].a;Cl(d.c,c,f);}}else{Dg('No groups!');}}else{Bl(d.c,'No identities yet!');}}
function uE(a,b){if(a.d!==null){jj(a.d,b);}}
function vE(g,h,a,e,b){var c,d,f,i;f=iv(new gv(),h+':');kv(f,' (');d=false;i=false;for(c=0;c<e.a;c++){if(sv(e[c],g.a)){d=true;}if(sv(e[c],g.b)){i=true;}}if(d){kv(f,g.a);}else{kv(f,'-');}kv(f,',');if(i){kv(f,g.b);}else{kv(f,'-');}kv(f,')');kv(f,' '+a);hm(g.c,b,ov(f));}
function wE(d,c){var a,b;b=cm(d.c);if(b>=0){a=nE(d,b);vE(d,zv(a,0,1),Av(yv(a,2)),c,b);}else{Dg('Exception: No list item selected!');}}
function xE(h){var a,b,c,d,e,f,g;if(h===this.e||h===this.g){g=pE(this);if(g!==null){if(h===this.e){a=oE(this,g);if(ij(this.e)){Dg('Add Read right from selected identity '+g+' from policy');e=kE(this,a,this.a);}else{Dg('Remove Read right from selected identity '+g+' from policy');e=sE(this,a,this.a);}wE(this,e);}else if(h===this.g){a=oE(this,g);if(ij(this.g)){Dg('Add Write right from selected identity '+g+' from policy');e=kE(this,a,this.b);}else{Dg('Remove Write right from selected identity '+g+' from policy');e=sE(this,a,this.b);}wE(this,e);}}else{Dg('No identity has been selected! Please select an identity in order to assign rights.');jj(this.e,false);jj(this.g,false);}}else if(h===this.c){g=pE(this);f=oE(this,g);b=false;c=false;for(d=0;d<f.a;d++){if(sv(f[d],this.a)){jj(this.e,true);b=true;}else if(sv(f[d],this.b)){jj(this.g,true);c=true;}}if(!b)jj(this.e,false);if(!c)jj(this.g,false);}}
function hE(){}
_=hE.prototype=new Cj();_.ib=xE;_.tN=qF+'PolicyListBoxWidget';_.tI=81;_.a='r';_.b='w';_.c=null;_.d=null;_.e=null;_.g=null;function zE(c,a,b){c.a=a;c.b=b;return c;}
function yE(){}
_=yE.prototype=new Du();_.tN=qF+'User';_.tI=82;_.a=null;_.b=null;function lt(){aD(new hC());}
function gwtOnLoad(b,d,c){$moduleName=d;$moduleBase=c;if(b)try{lt();}catch(a){b(d);}else{lt();}}
var je=[{},{},{1:1},{4:1},{4:1,25:1},{4:1,25:1},{4:1,18:1,25:1},{2:1},{6:1},{6:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{3:1},{2:1,5:1},{2:1},{7:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{20:1},{20:1},{20:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{9:1,10:1,11:1,12:1,13:1,14:1},{7:1},{11:1,12:1,13:1,14:1},{11:1,12:1,13:1,14:1},{10:1,11:1,12:1,13:1,14:1},{4:1,25:1},{15:1},{15:1},{15:1},{15:1},{15:1},{15:1},{15:1},{4:1,25:1},{15:1},{15:1,17:1},{15:1,16:1},{15:1},{15:1},{15:1},{4:1,25:1},{19:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{4:1,25:1},{21:1},{22:1},{22:1},{21:1},{23:1},{22:1},{22:1},{4:1,24:1,25:1},{4:1,25:1},{20:1},{8:1},{8:1},{8:1},{6:1},{6:1},{8:1,11:1,12:1,13:1,14:1},{27:1},{8:1,11:1,12:1,13:1,14:1},{8:1,11:1,12:1,13:1,14:1},{26:1}];if (org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor) {  var __gwt_initHandlers = org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.__gwt_initHandlers;  org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.onScriptLoad(gwtOnLoad);}})();