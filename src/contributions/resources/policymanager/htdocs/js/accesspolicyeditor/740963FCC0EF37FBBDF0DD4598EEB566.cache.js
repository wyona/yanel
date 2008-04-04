(function(){var $wnd = window;var $doc = $wnd.document;var $moduleName, $moduleBase;var _,nF='com.google.gwt.core.client.',oF='com.google.gwt.http.client.',pF='com.google.gwt.i18n.client.',qF='com.google.gwt.lang.',rF='com.google.gwt.user.client.',sF='com.google.gwt.user.client.impl.',tF='com.google.gwt.user.client.ui.',uF='com.google.gwt.user.client.ui.impl.',vF='com.google.gwt.xml.client.',wF='com.google.gwt.xml.client.impl.',xF='java.io.',yF='java.lang.',zF='java.util.',AF='org.wyona.security.gwt.accesspolicyeditor.client.',BF='org.wyona.yanel.gwt.client.';function iC(){}
function Du(a){return this===a;}
function Eu(){return iw(this);}
function Fu(){return this.tN+'@'+this.hC();}
function Bu(){}
_=Bu.prototype={};_.eQ=Du;_.hC=Eu;_.tS=Fu;_.toString=function(){return this.tS();};_.tN=yF+'Object';_.tI=1;function w(a){return a==null?null:a.tN;}
var y=null;function B(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function C(a){return a==null?0:a.$H?a.$H:(a.$H=D());}
function D(){return ++E;}
var E=0;function kw(b,a){b.b=a;return b;}
function mw(b,a){if(b.a!==null){throw hu(new gu(),"Can't overwrite cause");}if(a===b){throw eu(new du(),'Self-causation not permitted');}b.a=a;return b;}
function nw(a){ow(a,(gw(),hw));}
function ow(e,d){var a,b,c;c=fv(new ev());b=e;while(b!==null){a=b.b;if(b!==e){iv(c,'Caused by: ');}iv(c,b.tN);iv(c,': ');iv(c,a===null?'(No exception detail)':a);iv(c,'\n');b=b.a;}}
function pw(){var a,b;a=w(this);b=this.b;if(b!==null){return a+': '+b;}else{return a;}}
function jw(){}
_=jw.prototype=new Bu();_.tS=pw;_.tN=yF+'Throwable';_.tI=3;_.a=null;_.b=null;function bu(b,a){kw(b,a);return b;}
function au(){}
_=au.prototype=new jw();_.tN=yF+'Exception';_.tI=4;function bv(b,a){bu(b,a);return b;}
function av(){}
_=av.prototype=new au();_.tN=yF+'RuntimeException';_.tI=5;function ab(c,b,a){bv(c,'JavaScript '+b+' exception: '+a);return c;}
function F(){}
_=F.prototype=new av();_.tN=nF+'JavaScriptException';_.tI=6;function eb(b,a){if(!ee(a,2)){return false;}return jb(b,de(a,2));}
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
_=cb.prototype=new Bu();_.eQ=kb;_.hC=lb;_.tS=nb;_.tN=nF+'JavaScriptObject';_.tI=7;function rc(b,d,c,a){if(d===null){throw new uu();}if(a===null){throw new uu();}if(c<0){throw new du();}b.a=c;b.c=d;if(c>0){b.b=vb(new ub(),b,a);qg(b.b,c);}else{b.b=null;}return b;}
function tc(a){var b;if(a.c!==null){b=a.c;a.c=null;dd(b);sc(a);}}
function sc(a){if(a.b!==null){mg(a.b);}}
function vc(e,a){var b,c,d,f;if(e.c===null){return;}sc(e);f=e.c;e.c=null;b=ed(f);if(b!==null){c=bv(new av(),b);a.kb(e,c);}else{d=yc(f);a.mb(e,d);}}
function wc(b,a){if(b.c===null){return;}tc(b);a.kb(b,oc(new nc(),b,b.a));}
function xc(b){var a;if(b.c===null){return false;}a=fd(b.c);switch(a){case 1:case 2:case 3:return true;}return false;}
function yc(b){var a;a=qb(new pb(),b);return a;}
function zc(a){var b;b=y;{vc(this,a);}}
function ob(){}
_=ob.prototype=new Bu();_.s=zc;_.tN=oF+'Request';_.tI=0;_.a=0;_.b=null;_.c=null;function Ac(){}
_=Ac.prototype=new Bu();_.tN=oF+'Response';_.tI=0;function qb(a,b){a.a=b;return a;}
function sb(a){return hd(a.a);}
function tb(a){return gd(a.a);}
function pb(){}
_=pb.prototype=new Ac();_.tN=oF+'Request$1';_.tI=0;function ng(){ng=iC;xg=zy(new xy());{wg();}}
function lg(a){ng();return a;}
function mg(a){if(a.d){rg(a.e);}else{sg(a.e);}dz(xg,a);}
function og(a){if(!a.d){dz(xg,a);}a.ub();}
function qg(b,a){if(a<=0){throw eu(new du(),'must be positive');}mg(b);b.d=false;b.e=ug(b,a);By(xg,b);}
function pg(b,a){if(a<=0){throw eu(new du(),'must be positive');}mg(b);b.d=true;b.e=tg(b,a);By(xg,b);}
function rg(a){ng();$wnd.clearInterval(a);}
function sg(a){ng();$wnd.clearTimeout(a);}
function tg(b,a){ng();return $wnd.setInterval(function(){b.t();},a);}
function ug(b,a){ng();return $wnd.setTimeout(function(){b.t();},a);}
function vg(){var a;a=y;{og(this);}}
function wg(){ng();Bg(new hg());}
function gg(){}
_=gg.prototype=new Bu();_.t=vg;_.tN=rF+'Timer';_.tI=8;_.d=false;_.e=0;var xg;function wb(){wb=iC;ng();}
function vb(b,a,c){wb();b.a=a;b.b=c;lg(b);return b;}
function xb(){wc(this.a,this.b);}
function ub(){}
_=ub.prototype=new gg();_.ub=xb;_.tN=oF+'Request$2';_.tI=9;function Fb(){Fb=iC;dc=Ab(new zb(),'GET');ec=Ab(new zb(),'POST');fc=ji(new ii());}
function Db(b,a,c){Fb();Eb(b,a===null?null:a.a,c);return b;}
function Eb(b,a,c){Fb();Ec('httpMethod',a);Ec('url',c);b.b=a;b.d=c;return b;}
function ac(g,d,a){var b,c,e,f,h;h=li(fc);{b=id(h,g.b,g.d,true);}if(b!==null){e=lc(new kc(),g.d);mw(e,ic(new hc(),b));throw e;}cc(g,h);c=rc(new ob(),h,g.c,a);f=jd(h,c,d,a);if(f!==null){throw ic(new hc(),f);}return c;}
function bc(b,a,c){Ec('header',a);Ec('value',c);if(b.a===null){b.a=nA(new rz());}wA(b.a,a,c);}
function cc(e,f){var a,b,c,d;if(e.a!==null&&e.a.c>0){a=tA(e.a);d=iA(a);while(aA(d)){c=bA(d);b=kd(f,de(c.A(),1),de(c.C(),1));if(b!==null){throw ic(new hc(),b);}}}else{kd(f,'Content-Type','text/plain; charset=utf-8');}}
function yb(){}
_=yb.prototype=new Bu();_.tN=oF+'RequestBuilder';_.tI=0;_.a=null;_.b=null;_.c=0;_.d=null;var dc,ec,fc;function Ab(b,a){b.a=a;return b;}
function Cb(){return this.a;}
function zb(){}
_=zb.prototype=new Bu();_.tS=Cb;_.tN=oF+'RequestBuilder$Method';_.tI=0;_.a=null;function ic(b,a){bu(b,a);return b;}
function hc(){}
_=hc.prototype=new au();_.tN=oF+'RequestException';_.tI=10;function lc(a,b){ic(a,'The URL '+b+' is invalid or violates the same-origin security restriction');return a;}
function kc(){}
_=kc.prototype=new hc();_.tN=oF+'RequestPermissionException';_.tI=11;function oc(b,a,c){ic(b,qc(c));return b;}
function qc(a){return 'A request timeout has expired after '+ou(a)+' ms';}
function nc(){}
_=nc.prototype=new hc();_.tN=oF+'RequestTimeoutException';_.tI=12;function Ec(a,b){Fc(a,b);if(0==uv(Bv(b))){throw eu(new du(),a+' can not be empty');}}
function Fc(a,b){if(null===b){throw vu(new uu(),a+' can not be null');}}
function dd(a){a.onreadystatechange=ni;a.abort();}
function ed(b){try{if(b.status===undefined){return 'XmlHttpRequest.status == undefined, please see Safari bug '+'http://bugs.webkit.org/show_bug.cgi?id=3810 for more details';}return null;}catch(a){return 'Unable to read XmlHttpRequest.status; likely causes are a '+'networking error or bad cross-domain request. Please see '+'https://bugzilla.mozilla.org/show_bug.cgi?id=238559 for more '+'details';}}
function fd(a){return a.readyState;}
function gd(a){return a.responseText;}
function hd(a){return a.status;}
function id(e,c,d,b){try{e.open(c,d,b);return null;}catch(a){return a.message||a.toString();}}
function jd(e,c,d,b){e.onreadystatechange=function(){if(e.readyState==cd){e.onreadystatechange=ni;c.s(b);}};try{e.send(d);return null;}catch(a){e.onreadystatechange=ni;return a.message||a.toString();}}
function kd(d,b,c){try{d.setRequestHeader(b,c);return null;}catch(a){return a.message||a.toString();}}
var cd=4;function pd(){pd=iC;sd=nA(new rz());}
function md(b,a){pd();if(a===null||rv('',a)){throw eu(new du(),'Cannot create a Dictionary with a null or empty name');}b.b='Dictionary '+a;od(b,a);if(b.a===null){throw vB(new uB(),"Cannot find JavaScript object with the name '"+a+"'",a,null);}return b;}
function nd(b,a){for(x in b.a){a.m(x);}}
function od(c,b){try{if(typeof $wnd[b]!='object'){ud(b);}c.a=$wnd[b];}catch(a){ud(b);}}
function qd(b,a){var c=b.a[a];if(c==null|| !Object.prototype.hasOwnProperty.call(b.a,a)){b.tb(a);}return String(c);}
function rd(b){var a;a=jB(new iB());nd(b,a);return a;}
function td(a){pd();var b;b=de(uA(sd,a),3);if(b===null){b=md(new ld(),a);wA(sd,a,b);}return b;}
function vd(b){var a,c;c=rd(this);a="Cannot find '"+b+"' in "+this;if(c.a.c<20){a+='\n keys found: '+c;}throw vB(new uB(),a,this.b,b);}
function ud(a){pd();throw vB(new uB(),"'"+a+"' is not a JavaScript object and cannot be used as a Dictionary",null,a);}
function wd(){return this.b;}
function ld(){}
_=ld.prototype=new Bu();_.tb=vd;_.tS=wd;_.tN=pF+'Dictionary';_.tI=13;_.a=null;_.b=null;var sd;function yd(c,a,d,b,e){c.a=a;c.b=b;c.tN=e;c.tI=d;return c;}
function Ad(a,b,c){return a[b]=c;}
function Bd(b,a){return b[a];}
function Cd(a){return a.length;}
function Ed(e,d,c,b,a){return Dd(e,d,c,b,0,Cd(b),a);}
function Dd(j,i,g,c,e,a,b){var d,f,h;if((f=Bd(c,e))<0){throw new su();}h=yd(new xd(),f,Bd(i,e),Bd(g,e),j);++e;if(e<a){j=zv(j,1);for(d=0;d<f;++d){Ad(h,d,Dd(j,i,g,c,e,a,b));}}else{for(d=0;d<f;++d){Ad(h,d,b);}}return h;}
function Fd(a,b,c){if(c!==null&&a.b!=0&& !ee(c,a.b)){throw new qt();}return Ad(a,b,c);}
function xd(){}
_=xd.prototype=new Bu();_.tN=qF+'Array';_.tI=0;function ce(b,a){return !(!(b&&he[b][a]));}
function de(b,a){if(b!=null)ce(b.tI,a)||ge();return b;}
function ee(b,a){return b!=null&&ce(b.tI,a);}
function ge(){throw new Ct();}
function fe(a){if(a!==null){throw new Ct();}return a;}
function ie(b,d){_=d.prototype;if(b&& !(b.tI>=_.tI)){var c=b.toString;for(var a in _){b[a]=_[a];}b.toString=c;}return b;}
var he;function le(a){if(ee(a,4)){return a;}return ab(new F(),ne(a),me(a));}
function me(a){return a.message;}
function ne(a){return a.name;}
function pe(){pe=iC;qf=zy(new xy());{lf=new hh();ph(lf);}}
function qe(b,a){pe();rh(lf,b,a);}
function re(a,b){pe();return lh(lf,a,b);}
function se(){pe();return th(lf,'button');}
function te(){pe();return th(lf,'div');}
function ue(){pe();return uh(lf,'checkbox');}
function ve(){pe();return uh(lf,'text');}
function we(){pe();return th(lf,'label');}
function xe(a){pe();return vh(lf,a);}
function ye(){pe();return th(lf,'span');}
function ze(){pe();return th(lf,'tbody');}
function Ae(){pe();return th(lf,'td');}
function Be(){pe();return th(lf,'tr');}
function Ce(){pe();return th(lf,'table');}
function Fe(b,a,d){pe();var c;c=y;{Ee(b,a,d);}}
function Ee(b,a,c){pe();var d;if(a===pf){if(bf(b)==8192){pf=null;}}d=De;De=b;try{c.hb(b);}finally{De=d;}}
function af(b,a){pe();wh(lf,b,a);}
function bf(a){pe();return xh(lf,a);}
function cf(a){pe();mh(lf,a);}
function df(a){pe();return nh(lf,a);}
function ef(a){pe();return yh(lf,a);}
function hf(a,b){pe();return Bh(lf,a,b);}
function ff(a,b){pe();return zh(lf,a,b);}
function gf(a,b){pe();return Ah(lf,a,b);}
function jf(a){pe();return Ch(lf,a);}
function kf(a){pe();return oh(lf,a);}
function mf(c,b,d,a){pe();Dh(lf,c,b,d,a);}
function nf(a){pe();var b,c;c=true;if(qf.b>0){b=fe(Fy(qf,qf.b-1));if(!(c=null.Ab())){af(a,true);cf(a);}}return c;}
function of(b,a){pe();Eh(lf,b,a);}
function tf(a,b,c){pe();bi(lf,a,b,c);}
function rf(a,b,c){pe();Fh(lf,a,b,c);}
function sf(a,b,c){pe();ai(lf,a,b,c);}
function uf(a,b){pe();ci(lf,a,b);}
function vf(a,b){pe();di(lf,a,b);}
function wf(a,b){pe();ei(lf,a,b);}
function xf(b,c,a){pe();fi(lf,b,c,a);}
function yf(b,a,c){pe();gi(lf,b,a,c);}
function zf(a,b){pe();qh(lf,a,b);}
function Af(a){pe();return hi(lf,a);}
var De=null,lf=null,pf=null,qf;function Df(a){if(ee(a,5)){return re(this,de(a,5));}return eb(ie(this,Bf),a);}
function Ef(){return fb(ie(this,Bf));}
function Ff(){return Af(this);}
function Bf(){}
_=Bf.prototype=new cb();_.eQ=Df;_.hC=Ef;_.tS=Ff;_.tN=rF+'Element';_.tI=14;function dg(a){return eb(ie(this,ag),a);}
function eg(){return fb(ie(this,ag));}
function fg(){return df(this);}
function ag(){}
_=ag.prototype=new cb();_.eQ=dg;_.hC=eg;_.tS=fg;_.tN=rF+'Event';_.tI=15;function jg(){while((ng(),xg).b>0){mg(de(Fy((ng(),xg),0),6));}}
function kg(){return null;}
function hg(){}
_=hg.prototype=new Bu();_.ob=jg;_.pb=kg;_.tN=rF+'Timer$1';_.tI=16;function Ag(){Ag=iC;Dg=zy(new xy());fh=zy(new xy());{bh();}}
function Bg(a){Ag();By(Dg,a);}
function Cg(a){Ag();$wnd.alert(a);}
function Eg(){Ag();var a,b;for(a=Dg.cb();a.F();){b=de(a.eb(),7);b.ob();}}
function Fg(){Ag();var a,b,c,d;d=null;for(a=Dg.cb();a.F();){b=de(a.eb(),7);c=b.pb();{d=c;}}return d;}
function ah(){Ag();var a,b;for(a=fh.cb();a.F();){b=fe(a.eb());null.Ab();}}
function bh(){Ag();__gwt_initHandlers(function(){eh();},function(){return dh();},function(){ch();$wnd.onresize=null;$wnd.onbeforeclose=null;$wnd.onclose=null;});}
function ch(){Ag();var a;a=y;{Eg();}}
function dh(){Ag();var a;a=y;{return Fg();}}
function eh(){Ag();var a;a=y;{ah();}}
var Dg,fh;function rh(c,b,a){b.appendChild(a);}
function th(b,a){return $doc.createElement(a);}
function uh(b,c){var a=$doc.createElement('INPUT');a.type=c;return a;}
function vh(c,a){var b;b=th(c,'select');if(a){Fh(c,b,'multiple',true);}return b;}
function wh(c,b,a){b.cancelBubble=a;}
function xh(b,a){switch(a.type){case 'blur':return 4096;case 'change':return 1024;case 'click':return 1;case 'dblclick':return 2;case 'focus':return 2048;case 'keydown':return 128;case 'keypress':return 256;case 'keyup':return 512;case 'load':return 32768;case 'losecapture':return 8192;case 'mousedown':return 4;case 'mousemove':return 64;case 'mouseout':return 32;case 'mouseover':return 16;case 'mouseup':return 8;case 'scroll':return 16384;case 'error':return 65536;case 'mousewheel':return 131072;case 'DOMMouseScroll':return 131072;}}
function yh(c,b){var a=$doc.getElementById(b);return a||null;}
function Bh(d,a,b){var c=a[b];return c==null?null:String(c);}
function zh(c,a,b){return !(!a[b]);}
function Ah(d,a,c){var b=parseInt(a[c]);if(!b){return 0;}return b;}
function Ch(b,a){return a.__eventBits||0;}
function Dh(e,d,b,f,a){var c=new Option(b,f);if(a== -1||a>d.options.length-1){d.add(c,null);}else{d.add(c,d.options[a]);}}
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
function gh(){}
_=gh.prototype=new Bu();_.tN=sF+'DOMImpl';_.tI=0;function lh(c,a,b){return a==b;}
function mh(b,a){a.preventDefault();}
function nh(b,a){return a.toString();}
function oh(c,a){var b=a.parentNode;if(b==null){return null;}if(b.nodeType!=1)b=null;return b||null;}
function ph(d){$wnd.__dispatchCapturedMouseEvent=function(b){if($wnd.__dispatchCapturedEvent(b)){var a=$wnd.__captureElem;if(a&&a.__listener){Fe(b,a,a.__listener);b.stopPropagation();}}};$wnd.__dispatchCapturedEvent=function(a){if(!nf(a)){a.stopPropagation();a.preventDefault();return false;}return true;};$wnd.addEventListener('click',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('dblclick',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousedown',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mouseup',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousemove',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousewheel',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('keydown',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keyup',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keypress',$wnd.__dispatchCapturedEvent,true);$wnd.__dispatchEvent=function(b){var c,a=this;while(a&& !(c=a.__listener))a=a.parentNode;if(a&&a.nodeType!=1)a=null;if(c)Fe(b,a,c);};$wnd.__captureElem=null;}
function qh(c,b,a){b.__eventBits=a;b.onclick=a&1?$wnd.__dispatchEvent:null;b.ondblclick=a&2?$wnd.__dispatchEvent:null;b.onmousedown=a&4?$wnd.__dispatchEvent:null;b.onmouseup=a&8?$wnd.__dispatchEvent:null;b.onmouseover=a&16?$wnd.__dispatchEvent:null;b.onmouseout=a&32?$wnd.__dispatchEvent:null;b.onmousemove=a&64?$wnd.__dispatchEvent:null;b.onkeydown=a&128?$wnd.__dispatchEvent:null;b.onkeypress=a&256?$wnd.__dispatchEvent:null;b.onkeyup=a&512?$wnd.__dispatchEvent:null;b.onchange=a&1024?$wnd.__dispatchEvent:null;b.onfocus=a&2048?$wnd.__dispatchEvent:null;b.onblur=a&4096?$wnd.__dispatchEvent:null;b.onlosecapture=a&8192?$wnd.__dispatchEvent:null;b.onscroll=a&16384?$wnd.__dispatchEvent:null;b.onload=a&32768?$wnd.__dispatchEvent:null;b.onerror=a&65536?$wnd.__dispatchEvent:null;b.onmousewheel=a&131072?$wnd.__dispatchEvent:null;}
function jh(){}
_=jh.prototype=new gh();_.tN=sF+'DOMImplStandard';_.tI=0;function hh(){}
_=hh.prototype=new jh();_.tN=sF+'DOMImplOpera';_.tI=0;function ji(a){ni=hb();return a;}
function li(a){return mi(a);}
function mi(a){return new XMLHttpRequest();}
function ii(){}
_=ii.prototype=new Bu();_.tN=sF+'HTTPRequestImpl';_.tI=0;var ni=null;function qn(d,b,a){var c=b.parentNode;if(!c){return;}c.insertBefore(a,b);c.removeChild(b);}
function rn(b,a){if(b.j!==null){qn(b,b.j,a);}b.j=a;}
function sn(b,a){vn(b.j,a);}
function tn(b,a){zf(b.w(),a|jf(b.w()));}
function un(){return this.j;}
function vn(a,b){tf(a,'className',b);}
function wn(){if(this.j===null){return '(null handle)';}return Af(this.j);}
function on(){}
_=on.prototype=new Bu();_.w=un;_.tS=wn;_.tN=tF+'UIObject';_.tI=0;_.j=null;function so(a){if(ee(a.i,10)){de(a.i,10).sb(a);}else if(a.i!==null){throw hu(new gu(),"This widget's parent does not implement HasWidgets");}}
function to(b,a){if(b.ab()){uf(b.w(),null);}rn(b,a);if(b.ab()){uf(a,b);}}
function uo(c,b){var a;a=c.i;if(b===null){if(a!==null&&a.ab()){c.jb();}c.i=null;}else{if(a!==null){throw hu(new gu(),'Cannot set a new parent without first clearing the old parent');}c.i=b;if(b.ab()){c.gb();}}}
function vo(){}
function wo(){}
function xo(){return this.h;}
function yo(){if(this.ab()){throw hu(new gu(),"Should only call onAttach when the widget is detached from the browser's document");}this.h=true;uf(this.w(),this);this.p();this.lb();}
function zo(a){}
function Ao(){if(!this.ab()){throw hu(new gu(),"Should only call onDetach when the widget is attached to the browser's document");}try{this.nb();}finally{this.q();uf(this.w(),null);this.h=false;}}
function Bo(){}
function Co(){}
function Do(a){to(this,a);}
function En(){}
_=En.prototype=new on();_.p=vo;_.q=wo;_.ab=xo;_.gb=yo;_.hb=zo;_.jb=Ao;_.lb=Bo;_.nb=Co;_.vb=Do;_.tN=tF+'Widget';_.tI=17;_.h=false;_.i=null;function lm(b,a){uo(a,b);}
function nm(b,a){uo(a,null);}
function om(){var a,b;for(b=this.cb();eo(b);){a=fo(b);a.gb();}}
function pm(){var a,b;for(b=this.cb();eo(b);){a=fo(b);a.jb();}}
function qm(){}
function rm(){}
function km(){}
_=km.prototype=new En();_.p=om;_.q=pm;_.lb=qm;_.nb=rm;_.tN=tF+'Panel';_.tI=18;function wj(a){a.f=jo(new Fn(),a);}
function xj(a){wj(a);return a;}
function yj(c,a,b){so(a);ko(c.f,a);qe(b,a.w());lm(c,a);}
function Aj(b,c){var a;if(c.i!==b){return false;}nm(b,c);a=c.w();of(kf(a),a);qo(b.f,c);return true;}
function Bj(){return oo(this.f);}
function Cj(a){return Aj(this,a);}
function vj(){}
_=vj.prototype=new km();_.cb=Bj;_.sb=Cj;_.tN=tF+'ComplexPanel';_.tI=19;function pi(a){xj(a);a.vb(te());yf(a.w(),'position','relative');yf(a.w(),'overflow','hidden');return a;}
function qi(a,b){yj(a,b,a.w());}
function si(a){yf(a,'left','');yf(a,'top','');yf(a,'position','');}
function ti(b){var a;a=Aj(this,b);if(a){si(b.w());}return a;}
function oi(){}
_=oi.prototype=new vj();_.sb=ti;_.tN=tF+'AbsolutePanel';_.tI=20;function kk(){kk=iC;hp(),jp;}
function jk(b,a){hp(),jp;mk(b,a);return b;}
function lk(b,a){switch(bf(a)){case 1:if(b.c!==null){tj(b.c,b);}break;case 4096:case 2048:break;case 128:case 512:case 256:break;}}
function mk(b,a){to(b,a);tn(b,7041);}
function nk(a){if(this.c===null){this.c=rj(new qj());}By(this.c,a);}
function ok(a){lk(this,a);}
function pk(a){mk(this,a);}
function ik(){}
_=ik.prototype=new En();_.k=nk;_.hb=ok;_.vb=pk;_.tN=tF+'FocusWidget';_.tI=21;_.c=null;function xi(){xi=iC;hp(),jp;}
function wi(b,a){hp(),jp;jk(b,a);return b;}
function yi(a){vf(this.w(),a);}
function vi(){}
_=vi.prototype=new ik();_.wb=yi;_.tN=tF+'ButtonBase';_.tI=22;function Ci(){Ci=iC;hp(),jp;}
function zi(a){hp(),jp;wi(a,se());Di(a.w());sn(a,'gwt-Button');return a;}
function Ai(b,a){hp(),jp;zi(b);b.wb(a);return b;}
function Bi(c,a,b){hp(),jp;Ai(c,a);c.k(b);return c;}
function Di(b){Ci();if(b.type=='submit'){try{b.setAttribute('type','button');}catch(a){}}}
function ui(){}
_=ui.prototype=new vi();_.tN=tF+'Button';_.tI=23;function Fi(a){xj(a);a.e=Ce();a.d=ze();qe(a.e,a.d);a.vb(a.e);return a;}
function bj(c,b,a){tf(b,'align',a.a);}
function cj(c,b,a){yf(b,'verticalAlign',a.a);}
function Ei(){}
_=Ei.prototype=new vj();_.tN=tF+'CellPanel';_.tI=24;_.d=null;_.e=null;function hj(){hj=iC;hp(),jp;}
function ej(a){hp(),jp;fj(a,ue());sn(a,'gwt-CheckBox');return a;}
function gj(b,a){hp(),jp;ej(b);lj(b,a);return b;}
function fj(b,a){var c;hp(),jp;wi(b,ye());b.a=a;b.b=we();zf(b.a,jf(b.w()));zf(b.w(),0);qe(b.w(),b.a);qe(b.w(),b.b);c='check'+ ++pj;tf(b.a,'id',c);tf(b.b,'htmlFor',c);return b;}
function ij(b){var a;a=b.ab()?'checked':'defaultChecked';return ff(b.a,a);}
function jj(b,a){rf(b.a,'checked',a);rf(b.a,'defaultChecked',a);}
function kj(b,a){tf(b.a,'name',a);}
function lj(b,a){wf(b.b,a);}
function mj(){uf(this.a,this);}
function nj(){uf(this.a,null);jj(this,ij(this));}
function oj(a){vf(this.b,a);}
function dj(){}
_=dj.prototype=new vi();_.lb=mj;_.nb=nj;_.wb=oj;_.tN=tF+'CheckBox';_.tI=25;_.a=null;_.b=null;var pj=0;function uw(d,a,b){var c;while(a.F()){c=a.eb();if(b===null?c===null:b.eQ(c)){return a;}}return null;}
function ww(a){throw rw(new qw(),'add');}
function xw(b){var a;a=uw(this,this.cb(),b);return a!==null;}
function yw(){var a,b,c;c=fv(new ev());a=null;iv(c,'[');b=this.cb();while(b.F()){if(a!==null){iv(c,a);}else{a=', ';}iv(c,ew(b.eb()));}iv(c,']');return mv(c);}
function tw(){}
_=tw.prototype=new Bu();_.m=ww;_.o=xw;_.tS=yw;_.tN=zF+'AbstractCollection';_.tI=0;function cx(b,a){throw ku(new ju(),'Index: '+a+', Size: '+b.b);}
function dx(b,a){throw rw(new qw(),'add');}
function ex(a){this.l(this.yb(),a);return true;}
function fx(e){var a,b,c,d,f;if(e===this){return true;}if(!ee(e,30)){return false;}f=de(e,30);if(this.yb()!=f.yb()){return false;}c=this.cb();d=f.cb();while(c.F()){a=c.eb();b=d.eb();if(!(a===null?b===null:a.eQ(b))){return false;}}return true;}
function gx(){var a,b,c,d;c=1;a=31;b=this.cb();while(b.F()){d=b.eb();c=31*c+(d===null?0:d.hC());}return c;}
function hx(){return Bw(new Aw(),this);}
function ix(a){throw rw(new qw(),'remove');}
function zw(){}
_=zw.prototype=new tw();_.l=dx;_.m=ex;_.eQ=fx;_.hC=gx;_.cb=hx;_.rb=ix;_.tN=zF+'AbstractList';_.tI=26;function yy(a){{Cy(a);}}
function zy(a){yy(a);return a;}
function Ay(c,a,b){if(a<0||a>c.b){cx(c,a);}ez(c.a,a,b);++c.b;}
function By(b,a){nz(b.a,b.b++,a);return true;}
function Cy(a){a.a=gb();a.b=0;}
function Ey(b,a){return az(b,a)!=(-1);}
function Fy(b,a){if(a<0||a>=b.b){cx(b,a);}return jz(b.a,a);}
function az(b,a){return bz(b,a,0);}
function bz(c,b,a){if(a<0){cx(c,a);}for(;a<c.b;++a){if(iz(b,jz(c.a,a))){return a;}}return (-1);}
function cz(c,a){var b;b=Fy(c,a);lz(c.a,a,1);--c.b;return b;}
function dz(c,b){var a;a=az(c,b);if(a==(-1)){return false;}cz(c,a);return true;}
function fz(a,b){Ay(this,a,b);}
function gz(a){return By(this,a);}
function ez(a,b,c){a.splice(b,0,c);}
function hz(a){return Ey(this,a);}
function iz(a,b){return a===b||a!==null&&a.eQ(b);}
function kz(a){return Fy(this,a);}
function jz(a,b){return a[b];}
function mz(a){return cz(this,a);}
function lz(a,c,b){a.splice(c,b);}
function nz(a,b,c){a[b]=c;}
function oz(){return this.b;}
function xy(){}
_=xy.prototype=new zw();_.l=fz;_.m=gz;_.o=hz;_.D=kz;_.rb=mz;_.yb=oz;_.tN=zF+'ArrayList';_.tI=27;_.a=null;_.b=0;function rj(a){zy(a);return a;}
function tj(d,c){var a,b;for(a=d.cb();a.F();){b=de(a.eb(),8);b.ib(c);}}
function qj(){}
_=qj.prototype=new xy();_.tN=tF+'ClickListenerCollection';_.tI=28;function Fj(a,b){if(a.g!==null){throw hu(new gu(),'Composite.initWidget() may only be called once.');}so(b);a.vb(b.w());a.g=b;uo(b,a);}
function ak(){if(this.g===null){throw hu(new gu(),'initWidget() was never called in '+w(this));}return this.j;}
function bk(){if(this.g!==null){return this.g.ab();}return false;}
function ck(){this.g.gb();this.lb();}
function dk(){try{this.nb();}finally{this.g.jb();}}
function Dj(){}
_=Dj.prototype=new En();_.w=ak;_.ab=bk;_.gb=ck;_.jb=dk;_.tN=tF+'Composite';_.tI=29;_.g=null;function fk(a){xj(a);a.vb(te());return a;}
function gk(a,b){yj(a,b,a.w());}
function ek(){}
_=ek.prototype=new vj();_.tN=tF+'FlowPanel';_.tI=30;function wk(){wk=iC;uk(new tk(),'center');xk=uk(new tk(),'left');uk(new tk(),'right');}
var xk;function uk(b,a){b.a=a;return b;}
function tk(){}
_=tk.prototype=new Bu();_.tN=tF+'HasHorizontalAlignment$HorizontalAlignmentConstant';_.tI=0;_.a=null;function Dk(){Dk=iC;Bk(new Ak(),'bottom');Ek=Bk(new Ak(),'middle');Fk=Bk(new Ak(),'top');}
var Ek,Fk;function Bk(a,b){a.a=b;return a;}
function Ak(){}
_=Ak.prototype=new Bu();_.tN=tF+'HasVerticalAlignment$VerticalAlignmentConstant';_.tI=0;_.a=null;function dl(a){a.a=(wk(),xk);a.c=(Dk(),Fk);}
function el(a){Fi(a);dl(a);a.b=Be();qe(a.d,a.b);tf(a.e,'cellSpacing','0');tf(a.e,'cellPadding','0');return a;}
function fl(b,c){var a;a=hl(b);qe(b.b,a);yj(b,c,a);}
function hl(b){var a;a=Ae();bj(b,a,b.a);cj(b,a,b.c);return a;}
function il(b,a){b.c=a;}
function jl(c){var a,b;b=kf(c.w());a=Aj(this,c);if(a){of(this.b,b);}return a;}
function cl(){}
_=cl.prototype=new Ei();_.sb=jl;_.tN=tF+'HorizontalPanel';_.tI=31;_.b=null;function ml(a){a.vb(te());tn(a,131197);sn(a,'gwt-Label');return a;}
function nl(b,a){ml(b);pl(b,a);return b;}
function pl(b,a){wf(b.w(),a);}
function ql(a){switch(bf(a)){case 1:break;case 4:case 8:case 64:case 16:case 32:break;case 131072:break;}}
function ll(){}
_=ll.prototype=new En();_.hb=ql;_.tN=tF+'Label';_.tI=32;function El(){El=iC;hp(),jp;im=new sl();}
function zl(b,a){El();jk(b,xe(a));tn(b,1024);sn(b,'gwt-ListBox');return b;}
function Al(b,a){dm(b,a,(-1));}
function Bl(b,a,c){em(b,a,c,(-1));}
function Cl(b,a){if(a<0||a>=Fl(b)){throw new ju();}}
function Dl(a){tl(im,a.w());}
function Fl(a){return vl(im,a.w());}
function am(b,a){Cl(b,a);return wl(im,b.w(),a);}
function bm(a){return gf(a.w(),'selectedIndex');}
function cm(b,a){Cl(b,a);return xl(im,b.w(),a);}
function dm(c,b,a){em(c,b,b,a);}
function em(c,b,d,a){mf(c.w(),b,d,a);}
function fm(b,a){Cl(b,a);yl(im,b.w(),a);}
function gm(c,a,b){Cl(c,a);if(b===null){throw vu(new uu(),'Cannot set an option to have null text');}xf(c.w(),b,a);}
function hm(a,b){sf(a.w(),'size',b);}
function jm(a){if(bf(a)==1024){}else{lk(this,a);}}
function rl(){}
_=rl.prototype=new ik();_.hb=jm;_.tN=tF+'ListBox';_.tI=33;var im;function tl(b,a){a.options.length=0;}
function vl(b,a){return a.options.length;}
function wl(c,b,a){return b.options[a].text;}
function xl(c,b,a){return b.options[a].value;}
function yl(c,b,a){b.options[a]=null;}
function sl(){}
_=sl.prototype=new Bu();_.tN=tF+'ListBox$Impl';_.tI=0;function ym(){ym=iC;Cm=nA(new rz());}
function xm(b,a){ym();pi(b);if(a===null){a=zm();}b.vb(a);b.gb();return b;}
function Am(c){ym();var a,b;b=de(uA(Cm,c),9);if(b!==null){return b;}a=null;if(c!==null){if(null===(a=ef(c))){return null;}}if(Cm.c==0){Bm();}wA(Cm,c,b=xm(new sm(),a));return b;}
function zm(){ym();return $doc.body;}
function Bm(){ym();Bg(new tm());}
function sm(){}
_=sm.prototype=new oi();_.tN=tF+'RootPanel';_.tI=34;var Cm;function vm(){var a,b;for(b=Cx(ly((ym(),Cm)));dy(b);){a=de(ey(b),9);if(a.ab()){a.jb();}}}
function wm(){return null;}
function tm(){}
_=tm.prototype=new Bu();_.ob=vm;_.pb=wm;_.tN=tF+'RootPanel$1';_.tI=35;function gn(){gn=iC;hp(),jp;}
function fn(b,a){hp(),jp;jk(b,a);tn(b,1024);return b;}
function hn(a){return hf(a.w(),'value');}
function jn(a){if(this.a===null){this.a=rj(new qj());}By(this.a,a);}
function kn(a){var b;lk(this,a);b=bf(a);if(b==1){if(this.a!==null){tj(this.a,this);}}else{}}
function en(){}
_=en.prototype=new ik();_.k=jn;_.hb=kn;_.tN=tF+'TextBoxBase';_.tI=36;_.a=null;function mn(){mn=iC;hp(),jp;}
function ln(a){hp(),jp;fn(a,ve());sn(a,'gwt-TextBox');return a;}
function nn(b,a){sf(b.w(),'size',a);}
function dn(){}
_=dn.prototype=new en();_.tN=tF+'TextBox';_.tI=37;function yn(a){a.a=(wk(),xk);a.b=(Dk(),Fk);}
function zn(a){Fi(a);yn(a);tf(a.e,'cellSpacing','0');tf(a.e,'cellPadding','0');return a;}
function An(b,d){var a,c;c=Be();a=Cn(b);qe(c,a);qe(b.d,c);yj(b,d,a);}
function Cn(b){var a;a=Ae();bj(b,a,b.a);cj(b,a,b.b);return a;}
function Dn(c){var a,b;b=kf(c.w());a=Aj(this,c);if(a){of(this.d,kf(b));}return a;}
function xn(){}
_=xn.prototype=new Ei();_.sb=Dn;_.tN=tF+'VerticalPanel';_.tI=38;function jo(b,a){b.b=a;b.a=Ed('[Lcom.google.gwt.user.client.ui.Widget;',[0],[13],[4],null);return b;}
function ko(a,b){no(a,b,a.c);}
function mo(b,c){var a;for(a=0;a<b.c;++a){if(b.a[a]===c){return a;}}return (-1);}
function no(d,e,a){var b,c;if(a<0||a>d.c){throw new ju();}if(d.c==d.a.a){c=Ed('[Lcom.google.gwt.user.client.ui.Widget;',[0],[13],[d.a.a*2],null);for(b=0;b<d.a.a;++b){Fd(c,b,d.a[b]);}d.a=c;}++d.c;for(b=d.c-1;b>a;--b){Fd(d.a,b,d.a[b-1]);}Fd(d.a,a,e);}
function oo(a){return bo(new ao(),a);}
function po(c,b){var a;if(b<0||b>=c.c){throw new ju();}--c.c;for(a=b;a<c.c;++a){Fd(c.a,a,c.a[a+1]);}Fd(c.a,c.c,null);}
function qo(b,c){var a;a=mo(b,c);if(a==(-1)){throw new xB();}po(b,a);}
function Fn(){}
_=Fn.prototype=new Bu();_.tN=tF+'WidgetCollection';_.tI=0;_.a=null;_.b=null;_.c=0;function bo(b,a){b.b=a;return b;}
function eo(a){return a.a<a.b.c-1;}
function fo(a){if(a.a>=a.b.c){throw new xB();}return a.b.a[++a.a];}
function go(){return eo(this);}
function ho(){return fo(this);}
function io(){if(this.a<0||this.a>=this.b.c){throw new gu();}this.b.b.sb(this.b.a[this.a--]);}
function ao(){}
_=ao.prototype=new Bu();_.F=go;_.eb=ho;_.qb=io;_.tN=tF+'WidgetCollection$WidgetIterator';_.tI=0;_.a=(-1);function hp(){hp=iC;ip=bp(new Fo());jp=ip!==null?gp(new Eo()):ip;}
function gp(a){hp();return a;}
function Eo(){}
_=Eo.prototype=new Bu();_.tN=uF+'FocusImpl';_.tI=0;var ip,jp;function cp(){cp=iC;hp();}
function ap(a){dp(a);ep(a);fp(a);}
function bp(a){cp();gp(a);ap(a);return a;}
function dp(b){return function(a){if(this.parentNode.onblur){this.parentNode.onblur(a);}};}
function ep(b){return function(a){if(this.parentNode.onfocus){this.parentNode.onfocus(a);}};}
function fp(a){return function(){this.firstChild.focus();};}
function Fo(){}
_=Fo.prototype=new Eo();_.tN=uF+'FocusImplOld';_.tI=0;function pp(c,a,b){bv(c,b);return c;}
function op(){}
_=op.prototype=new av();_.tN=vF+'DOMException';_.tI=39;function Ap(){Ap=iC;Bp=(ws(),ht);}
function Cp(a){Ap();return xs(Bp,a);}
var Bp;function qq(b,a){b.a=a;return b;}
function rq(a,b){return b;}
function tq(a){if(ee(a,25)){return re(rq(this,this.a),rq(this,de(a,25).a));}return false;}
function pq(){}
_=pq.prototype=new Bu();_.eQ=tq;_.tN=wF+'DOMItem';_.tI=40;_.a=null;function or(b,a){qq(b,a);return b;}
function qr(a){return jr(new ir(),zs(a.a));}
function rr(a){return zr(new yr(),As(a.a));}
function sr(a){return at(a.a);}
function tr(a){return ct(a.a);}
function ur(a){return ft(a.a);}
function vr(a){return gt(a.a);}
function wr(a){var b;if(a===null){return null;}b=bt(a);switch(b){case 2:return Ep(new Dp(),a);case 4:return eq(new dq(),a);case 8:return mq(new lq(),a);case 11:return zq(new yq(),a);case 9:return Dq(new Cq(),a);case 1:return cr(new br(),a);case 7:return cs(new bs(),a);case 3:return hs(new gs(),a);default:return or(new nr(),a);}}
function xr(){return rr(this).bb(0);}
function nr(){}
_=nr.prototype=new pq();_.z=xr;_.tN=wF+'NodeImpl';_.tI=41;function Ep(b,a){or(b,a);return b;}
function aq(a){return Fs(a.a);}
function bq(a){return et(a.a);}
function cq(){var a;a=fv(new ev());iv(a,' '+aq(this));iv(a,'="');iv(a,bq(this));iv(a,'"');return mv(a);}
function Dp(){}
_=Dp.prototype=new nr();_.tS=cq;_.tN=wF+'AttrImpl';_.tI=42;function iq(b,a){or(b,a);return b;}
function kq(a){return Bs(a.a);}
function hq(){}
_=hq.prototype=new nr();_.tN=wF+'CharacterDataImpl';_.tI=43;function hs(b,a){iq(b,a);return b;}
function js(){var a,b,c;a=fv(new ev());c=xv(kq(this),'(?=[;&<>\'"])',(-1));for(b=0;b<c.a;b++){if(yv(c[b],';')){iv(a,'&semi;');iv(a,zv(c[b],1));}else if(yv(c[b],'&')){iv(a,'&amp;');iv(a,zv(c[b],1));}else if(yv(c[b],'"')){iv(a,'&quot;');iv(a,zv(c[b],1));}else if(yv(c[b],"'")){iv(a,'&apos;');iv(a,zv(c[b],1));}else if(yv(c[b],'<')){iv(a,'&lt;');iv(a,zv(c[b],1));}else if(yv(c[b],'>')){iv(a,'&gt;');iv(a,zv(c[b],1));}else{iv(a,c[b]);}}return mv(a);}
function gs(){}
_=gs.prototype=new hq();_.tS=js;_.tN=wF+'TextImpl';_.tI=44;function eq(b,a){hs(b,a);return b;}
function gq(){var a;a=gv(new ev(),'<![CDATA[');iv(a,kq(this));iv(a,']]>');return mv(a);}
function dq(){}
_=dq.prototype=new gs();_.tS=gq;_.tN=wF+'CDATASectionImpl';_.tI=45;function mq(b,a){iq(b,a);return b;}
function oq(){var a;a=gv(new ev(),'<!--');iv(a,kq(this));iv(a,'-->');return mv(a);}
function lq(){}
_=lq.prototype=new hq();_.tS=oq;_.tN=wF+'CommentImpl';_.tI=46;function vq(c,a,b){pp(c,12,'Failed to parse: '+xq(a));mw(c,b);return c;}
function xq(a){return Av(a,0,ru(uv(a),128));}
function uq(){}
_=uq.prototype=new op();_.tN=wF+'DOMParseException';_.tI=47;function zq(b,a){or(b,a);return b;}
function Bq(){var a,b;a=fv(new ev());for(b=0;b<rr(this).B();b++){hv(a,rr(this).bb(b));}return mv(a);}
function yq(){}
_=yq.prototype=new nr();_.tS=Bq;_.tN=wF+'DocumentFragmentImpl';_.tI=48;function Dq(b,a){or(b,a);return b;}
function Fq(){return de(wr(Cs(this.a)),26);}
function ar(){var a,b,c;a=fv(new ev());b=rr(this);for(c=0;c<b.B();c++){iv(a,b.bb(c).tS());}return mv(a);}
function Cq(){}
_=Cq.prototype=new nr();_.v=Fq;_.tS=ar;_.tN=wF+'DocumentImpl';_.tI=49;function cr(b,a){or(b,a);return b;}
function er(a){return dt(a.a);}
function fr(a){return ys(this.a,a);}
function gr(a){return zr(new yr(),Ds(this.a,a));}
function hr(){var a;a=gv(new ev(),'<');iv(a,er(this));if(ur(this)){iv(a,Dr(qr(this)));}if(vr(this)){iv(a,'>');iv(a,Dr(rr(this)));iv(a,'<\/');iv(a,er(this));iv(a,'>');}else{iv(a,'/>');}return mv(a);}
function br(){}
_=br.prototype=new nr();_.u=fr;_.y=gr;_.tS=hr;_.tN=wF+'ElementImpl';_.tI=50;function zr(b,a){qq(b,a);return b;}
function Br(a){return Es(a.a);}
function Cr(b,a){return wr(it(b.a,a));}
function Dr(c){var a,b;a=fv(new ev());for(b=0;b<c.B();b++){iv(a,c.bb(b).tS());}return mv(a);}
function Er(){return Br(this);}
function Fr(a){return Cr(this,a);}
function as(){return Dr(this);}
function yr(){}
_=yr.prototype=new pq();_.B=Er;_.bb=Fr;_.tS=as;_.tN=wF+'NodeListImpl';_.tI=51;function jr(b,a){zr(b,a);return b;}
function lr(){return Br(this);}
function mr(a){return Cr(this,a);}
function ir(){}
_=ir.prototype=new yr();_.B=lr;_.bb=mr;_.tN=wF+'NamedNodeMapImpl';_.tI=52;function cs(b,a){or(b,a);return b;}
function es(a){return Bs(a.a);}
function fs(){var a;a=gv(new ev(),'<?');iv(a,sr(this));iv(a,' ');iv(a,es(this));iv(a,'?>');return mv(a);}
function bs(){}
_=bs.prototype=new nr();_.tS=fs;_.tN=wF+'ProcessingInstructionImpl';_.tI=53;function ws(){ws=iC;ht=ms(new ls());}
function vs(a){ws();return a;}
function xs(e,c){var a,d;try{return de(wr(ts(e,c)),27);}catch(a){a=le(a);if(ee(a,28)){d=a;throw vq(new uq(),c,d);}else throw a;}}
function ys(b,a){ws();return b.getAttribute(a);}
function zs(a){ws();return a.attributes;}
function As(b){ws();var a=b.childNodes;return a==null?null:a;}
function Bs(a){ws();return a.data;}
function Cs(a){ws();return a.documentElement;}
function Ds(a,b){ws();return ss(ht,a,b);}
function Es(a){ws();return a.length;}
function Fs(a){ws();return a.name;}
function at(a){ws();var b=a.nodeName;return b==null?null:b;}
function bt(a){ws();var b=a.nodeType;return b==null?-1:b;}
function ct(a){ws();return a.nodeValue;}
function dt(a){ws();return a.tagName;}
function et(a){ws();return a.value;}
function ft(a){ws();return a.attributes.length!=0;}
function gt(a){ws();return a.hasChildNodes();}
function it(c,a){ws();if(a>=c.length){return null;}var b=c.item(a);return b==null?null:b;}
function ks(){}
_=ks.prototype=new Bu();_.tN=wF+'XMLParserImpl';_.tI=0;var ht;function rs(){rs=iC;ws();}
function ps(a){a.a=us();}
function qs(a){rs();vs(a);ps(a);return a;}
function ss(c,a,b){return a.getElementsByTagNameNS('*',b);}
function ts(e,a){var b=e.a;var c=b.parseFromString(a,'text/xml');var d=c.documentElement;if(d.tagName=='parsererror'&&d.namespaceURI=='http://www.mozilla.org/newlayout/xml/parsererror.xml'){throw new Error(d.firstChild.data);}return c;}
function us(){rs();return new DOMParser();}
function os(){}
_=os.prototype=new ks();_.tN=wF+'XMLParserImplStandard';_.tI=0;function ns(){ns=iC;rs();}
function ms(a){ns();qs(a);return a;}
function ls(){}
_=ls.prototype=new os();_.tN=wF+'XMLParserImplOpera';_.tI=0;function mt(){}
_=mt.prototype=new Bu();_.tN=xF+'OutputStream';_.tI=0;function kt(){}
_=kt.prototype=new mt();_.tN=xF+'FilterOutputStream';_.tI=0;function ot(){}
_=ot.prototype=new kt();_.tN=xF+'PrintStream';_.tI=0;function qt(){}
_=qt.prototype=new av();_.tN=yF+'ArrayStoreException';_.tI=54;function ut(){ut=iC;vt=tt(new st(),false);wt=tt(new st(),true);}
function tt(a,b){ut();a.a=b;return a;}
function xt(a){return ee(a,29)&&de(a,29).a==this.a;}
function yt(){var a,b;b=1231;a=1237;return this.a?1231:1237;}
function zt(){return this.a?'true':'false';}
function At(a){ut();return a?wt:vt;}
function st(){}
_=st.prototype=new Bu();_.eQ=xt;_.hC=yt;_.tS=zt;_.tN=yF+'Boolean';_.tI=55;_.a=false;var vt,wt;function Ct(){}
_=Ct.prototype=new av();_.tN=yF+'ClassCastException';_.tI=56;function eu(b,a){bv(b,a);return b;}
function du(){}
_=du.prototype=new av();_.tN=yF+'IllegalArgumentException';_.tI=57;function hu(b,a){bv(b,a);return b;}
function gu(){}
_=gu.prototype=new av();_.tN=yF+'IllegalStateException';_.tI=58;function ku(b,a){bv(b,a);return b;}
function ju(){}
_=ju.prototype=new av();_.tN=yF+'IndexOutOfBoundsException';_.tI=59;function yu(){yu=iC;{Au();}}
function Au(){yu();zu=/^[+-]?\d*\.?\d*(e[+-]?\d+)?$/i;}
var zu=null;function nu(){nu=iC;yu();}
function ou(a){nu();return dw(a);}
function ru(a,b){return a<b?a:b;}
function su(){}
_=su.prototype=new av();_.tN=yF+'NegativeArraySizeException';_.tI=60;function vu(b,a){bv(b,a);return b;}
function uu(){}
_=uu.prototype=new av();_.tN=yF+'NullPointerException';_.tI=61;function pv(b,a){return b.charCodeAt(a);}
function rv(b,a){if(!ee(a,1))return false;return Dv(b,a);}
function sv(b,a){return b.indexOf(a);}
function tv(c,b,a){return c.indexOf(b,a);}
function uv(a){return a.length;}
function vv(c,a,b){b=Ev(b);return c.replace(RegExp(a,'g'),b);}
function wv(b,a){return xv(b,a,0);}
function xv(j,i,g){var a=new RegExp(i,'g');var h=[];var b=0;var k=j;var e=null;while(true){var f=a.exec(k);if(f==null||(k==''||b==g-1&&g>0)){h[b]=k;break;}else{h[b]=k.substring(0,f.index);k=k.substring(f.index+f[0].length,k.length);a.lastIndex=0;if(e==k){h[b]=k.substring(0,1);k=k.substring(1);}e=k;b++;}}if(g==0){for(var c=h.length-1;c>=0;c--){if(h[c]!=''){h.splice(c+1,h.length-(c+1));break;}}}var d=Cv(h.length);var c=0;for(c=0;c<h.length;++c){d[c]=h[c];}return d;}
function yv(b,a){return sv(b,a)==0;}
function zv(b,a){return b.substr(a,b.length-a);}
function Av(c,a,b){return c.substr(a,b-a);}
function Bv(c){var a=c.replace(/^(\s*)/,'');var b=a.replace(/\s*$/,'');return b;}
function Cv(a){return Ed('[Ljava.lang.String;',[0],[1],[a],null);}
function Dv(a,b){return String(a)==b;}
function Ev(b){var a;a=0;while(0<=(a=tv(b,'\\',a))){if(pv(b,a+1)==36){b=Av(b,0,a)+'$'+zv(b,++a);}else{b=Av(b,0,a)+zv(b,++a);}}return b;}
function Fv(a){return rv(this,a);}
function bw(){var a=aw;if(!a){a=aw={};}var e=':'+this;var b=a[e];if(b==null){b=0;var f=this.length;var d=f<64?1:f/32|0;for(var c=0;c<f;c+=d){b<<=1;b+=this.charCodeAt(c);}b|=0;a[e]=b;}return b;}
function cw(){return this;}
function dw(a){return ''+a;}
function ew(a){return a!==null?a.tS():'null';}
_=String.prototype;_.eQ=Fv;_.hC=bw;_.tS=cw;_.tN=yF+'String';_.tI=2;var aw=null;function fv(a){jv(a);return a;}
function gv(b,a){kv(b,a);return b;}
function hv(a,b){return iv(a,ew(b));}
function iv(c,d){if(d===null){d='null';}var a=c.js.length-1;var b=c.js[a].length;if(c.length>b*b){c.js[a]=c.js[a]+d;}else{c.js.push(d);}c.length+=d.length;return c;}
function jv(a){kv(a,'');}
function kv(b,a){b.js=[a];b.length=a.length;}
function mv(a){a.fb();return a.js[0];}
function nv(){if(this.js.length>1){this.js=[this.js.join('')];this.length=this.js[0].length;}}
function ov(){return mv(this);}
function ev(){}
_=ev.prototype=new Bu();_.fb=nv;_.tS=ov;_.tN=yF+'StringBuffer';_.tI=0;function gw(){gw=iC;hw=new ot();}
function iw(a){gw();return C(a);}
var hw;function rw(b,a){bv(b,a);return b;}
function qw(){}
_=qw.prototype=new av();_.tN=yF+'UnsupportedOperationException';_.tI=62;function Bw(b,a){b.c=a;return b;}
function Dw(a){return a.a<a.c.yb();}
function Ew(){return Dw(this);}
function Fw(){if(!Dw(this)){throw new xB();}return this.c.D(this.b=this.a++);}
function ax(){if(this.b<0){throw new gu();}this.c.rb(this.b);this.a=this.b;this.b=(-1);}
function Aw(){}
_=Aw.prototype=new Bu();_.F=Ew;_.eb=Fw;_.qb=ax;_.tN=zF+'AbstractList$IteratorImpl';_.tI=0;_.a=0;_.b=(-1);function jy(f,d,e){var a,b,c;for(b=iA(f.r());aA(b);){a=bA(b);c=a.A();if(d===null?c===null:d.eQ(c)){if(e){cA(b);}return a;}}return null;}
function ky(b){var a;a=b.r();return lx(new kx(),b,a);}
function ly(b){var a;a=tA(b);return Ax(new zx(),b,a);}
function my(a){return jy(this,a,false)!==null;}
function ny(d){var a,b,c,e,f,g,h;if(d===this){return true;}if(!ee(d,31)){return false;}f=de(d,31);c=ky(this);e=f.db();if(!uy(c,e)){return false;}for(a=nx(c);ux(a);){b=vx(a);h=this.E(b);g=f.E(b);if(h===null?g!==null:!h.eQ(g)){return false;}}return true;}
function oy(b){var a;a=jy(this,b,false);return a===null?null:a.C();}
function py(){var a,b,c;b=0;for(c=iA(this.r());aA(c);){a=bA(c);b+=a.hC();}return b;}
function qy(){return ky(this);}
function ry(){var a,b,c,d;d='{';a=false;for(c=iA(this.r());aA(c);){b=bA(c);if(a){d+=', ';}else{a=true;}d+=ew(b.A());d+='=';d+=ew(b.C());}return d+'}';}
function jx(){}
_=jx.prototype=new Bu();_.n=my;_.eQ=ny;_.E=oy;_.hC=py;_.db=qy;_.tS=ry;_.tN=zF+'AbstractMap';_.tI=63;function uy(e,b){var a,c,d;if(b===e){return true;}if(!ee(b,32)){return false;}c=de(b,32);if(c.yb()!=e.yb()){return false;}for(a=c.cb();a.F();){d=a.eb();if(!e.o(d)){return false;}}return true;}
function vy(a){return uy(this,a);}
function wy(){var a,b,c;a=0;for(b=this.cb();b.F();){c=b.eb();if(c!==null){a+=c.hC();}}return a;}
function sy(){}
_=sy.prototype=new tw();_.eQ=vy;_.hC=wy;_.tN=zF+'AbstractSet';_.tI=64;function lx(b,a,c){b.a=a;b.b=c;return b;}
function nx(b){var a;a=iA(b.b);return sx(new rx(),b,a);}
function ox(a){return this.a.n(a);}
function px(){return nx(this);}
function qx(){return this.b.a.c;}
function kx(){}
_=kx.prototype=new sy();_.o=ox;_.cb=px;_.yb=qx;_.tN=zF+'AbstractMap$1';_.tI=65;function sx(b,a,c){b.a=c;return b;}
function ux(a){return a.a.F();}
function vx(b){var a;a=b.a.eb();return a.A();}
function wx(){return ux(this);}
function xx(){return vx(this);}
function yx(){this.a.qb();}
function rx(){}
_=rx.prototype=new Bu();_.F=wx;_.eb=xx;_.qb=yx;_.tN=zF+'AbstractMap$2';_.tI=0;function Ax(b,a,c){b.a=a;b.b=c;return b;}
function Cx(b){var a;a=iA(b.b);return by(new ay(),b,a);}
function Dx(a){return sA(this.a,a);}
function Ex(){return Cx(this);}
function Fx(){return this.b.a.c;}
function zx(){}
_=zx.prototype=new tw();_.o=Dx;_.cb=Ex;_.yb=Fx;_.tN=zF+'AbstractMap$3';_.tI=0;function by(b,a,c){b.a=c;return b;}
function dy(a){return a.a.F();}
function ey(a){var b;b=a.a.eb().C();return b;}
function fy(){return dy(this);}
function gy(){return ey(this);}
function hy(){this.a.qb();}
function ay(){}
_=ay.prototype=new Bu();_.F=fy;_.eb=gy;_.qb=hy;_.tN=zF+'AbstractMap$4';_.tI=0;function qA(){qA=iC;yA=EA();}
function mA(a){{pA(a);}}
function nA(a){qA();mA(a);return a;}
function oA(a,b){qA();mA(a);vA(a,b);return a;}
function pA(a){a.a=gb();a.d=ib();a.b=ie(yA,cb);a.c=0;}
function rA(b,a){if(ee(a,1)){return cB(b.d,de(a,1))!==yA;}else if(a===null){return b.b!==yA;}else{return bB(b.a,a,a.hC())!==yA;}}
function sA(a,b){if(a.b!==yA&&aB(a.b,b)){return true;}else if(DA(a.d,b)){return true;}else if(BA(a.a,b)){return true;}return false;}
function tA(a){return gA(new Cz(),a);}
function uA(c,a){var b;if(ee(a,1)){b=cB(c.d,de(a,1));}else if(a===null){b=c.b;}else{b=bB(c.a,a,a.hC());}return b===yA?null:b;}
function wA(c,a,d){var b;if(ee(a,1)){b=fB(c.d,de(a,1),d);}else if(a===null){b=c.b;c.b=d;}else{b=eB(c.a,a,d,a.hC());}if(b===yA){++c.c;return null;}else{return b;}}
function vA(d,c){var a,b;b=iA(tA(c));while(aA(b)){a=bA(b);wA(d,a.A(),a.C());}}
function xA(c,a){var b;if(ee(a,1)){b=hB(c.d,de(a,1));}else if(a===null){b=c.b;c.b=ie(yA,cb);}else{b=gB(c.a,a,a.hC());}if(b===yA){return null;}else{--c.c;return b;}}
function zA(e,c){qA();for(var d in e){if(d==parseInt(d)){var a=e[d];for(var f=0,b=a.length;f<b;++f){c.m(a[f]);}}}}
function AA(d,a){qA();for(var c in d){if(c.charCodeAt(0)==58){var e=d[c];var b=vz(c.substring(1),e);a.m(b);}}}
function BA(f,h){qA();for(var e in f){if(e==parseInt(e)){var a=f[e];for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.C();if(aB(h,d)){return true;}}}}return false;}
function CA(a){return rA(this,a);}
function DA(c,d){qA();for(var b in c){if(b.charCodeAt(0)==58){var a=c[b];if(aB(d,a)){return true;}}}return false;}
function EA(){qA();}
function FA(){return tA(this);}
function aB(a,b){qA();if(a===b){return true;}else if(a===null){return false;}else{return a.eQ(b);}}
function dB(a){return uA(this,a);}
function bB(f,h,e){qA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(aB(h,d)){return c.C();}}}}
function cB(b,a){qA();return b[':'+a];}
function eB(f,h,j,e){qA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(aB(h,d)){var i=c.C();c.xb(j);return i;}}}else{a=f[e]=[];}var c=vz(h,j);a.push(c);}
function fB(c,a,d){qA();a=':'+a;var b=c[a];c[a]=d;return b;}
function gB(f,h,e){qA();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(aB(h,d)){if(a.length==1){delete f[e];}else{a.splice(g,1);}return c.C();}}}}
function hB(c,a){qA();a=':'+a;var b=c[a];delete c[a];return b;}
function rz(){}
_=rz.prototype=new jx();_.n=CA;_.r=FA;_.E=dB;_.tN=zF+'HashMap';_.tI=66;_.a=null;_.b=null;_.c=0;_.d=null;var yA;function tz(b,a,c){b.a=a;b.b=c;return b;}
function vz(a,b){return tz(new sz(),a,b);}
function wz(b){var a;if(ee(b,33)){a=de(b,33);if(aB(this.a,a.A())&&aB(this.b,a.C())){return true;}}return false;}
function xz(){return this.a;}
function yz(){return this.b;}
function zz(){var a,b;a=0;b=0;if(this.a!==null){a=this.a.hC();}if(this.b!==null){b=this.b.hC();}return a^b;}
function Az(a){var b;b=this.b;this.b=a;return b;}
function Bz(){return this.a+'='+this.b;}
function sz(){}
_=sz.prototype=new Bu();_.eQ=wz;_.A=xz;_.C=yz;_.hC=zz;_.xb=Az;_.tS=Bz;_.tN=zF+'HashMap$EntryImpl';_.tI=67;_.a=null;_.b=null;function gA(b,a){b.a=a;return b;}
function iA(a){return Ez(new Dz(),a.a);}
function jA(c){var a,b,d;if(ee(c,33)){a=de(c,33);b=a.A();if(rA(this.a,b)){d=uA(this.a,b);return aB(a.C(),d);}}return false;}
function kA(){return iA(this);}
function lA(){return this.a.c;}
function Cz(){}
_=Cz.prototype=new sy();_.o=jA;_.cb=kA;_.yb=lA;_.tN=zF+'HashMap$EntrySet';_.tI=68;function Ez(c,b){var a;c.c=b;a=zy(new xy());if(c.c.b!==(qA(),yA)){By(a,tz(new sz(),null,c.c.b));}AA(c.c.d,a);zA(c.c.a,a);c.a=a.cb();return c;}
function aA(a){return a.a.F();}
function bA(a){return a.b=de(a.a.eb(),33);}
function cA(a){if(a.b===null){throw hu(new gu(),'Must call next() before remove().');}else{a.a.qb();xA(a.c,a.b.A());a.b=null;}}
function dA(){return aA(this);}
function eA(){return bA(this);}
function fA(){cA(this);}
function Dz(){}
_=Dz.prototype=new Bu();_.F=dA;_.eb=eA;_.qb=fA;_.tN=zF+'HashMap$EntrySetIterator';_.tI=0;_.a=null;_.b=null;function jB(a){a.a=nA(new rz());return a;}
function lB(a){var b;b=wA(this.a,a,At(true));return b===null;}
function mB(a){return rA(this.a,a);}
function nB(){return nx(ky(this.a));}
function oB(){return this.a.c;}
function pB(){return ky(this.a).tS();}
function iB(){}
_=iB.prototype=new sy();_.m=lB;_.o=mB;_.cb=nB;_.yb=oB;_.tS=pB;_.tN=zF+'HashSet';_.tI=69;_.a=null;function vB(d,c,a,b){bv(d,c);return d;}
function uB(){}
_=uB.prototype=new av();_.tN=zF+'MissingResourceException';_.tI=70;function xB(){}
_=xB.prototype=new av();_.tN=zF+'NoSuchElementException';_.tI=71;function CB(a){a.a=zy(new xy());return a;}
function DB(b,a){return By(b.a,a);}
function FB(b,a){return aC(b,a);}
function aC(b,a){return Fy(b.a,a);}
function bC(a,b){Ay(this.a,a,b);}
function cC(a){return DB(this,a);}
function dC(a){return Ey(this.a,a);}
function eC(a){return aC(this,a);}
function fC(){return this.a.cb();}
function gC(a){return cz(this.a,a);}
function hC(){return this.a.b;}
function BB(){}
_=BB.prototype=new zw();_.l=bC;_.m=cC;_.o=dC;_.D=eC;_.cb=fC;_.rb=gC;_.yb=hC;_.tN=zF+'Vector';_.tI=72;_.a=null;function aD(g,h){var a,c,d,e,f;h=vv(h,'&amp;','&');c=lD(new jD(),h);try{e=kF(c);f=yC(new xC(),g,e,c);qg(f,1);}catch(a){a=le(a);if(ee(a,35)){d=a;nw(d);}else throw a;}}
function bD(g,h){var a,c,d,e,f;h=vv(h,'&amp;','&');c=uD(new sD(),h);try{e=kF(c);f=CC(new BC(),g,e,c);qg(f,1);}catch(a){a=le(a);if(ee(a,35)){d=a;Cg('Exception: '+d.b);nw(d);}else throw a;}}
function cD(r){var a,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,s;k='DEFAULT-identities-and-usecases.xml';l='DEFAULT-policy.xml';f='DEFAULT-cancel.html';m='DEFAULT-save-policy.xml';try{h=td('getURLs');k=qd(h,'identities-url');l=qd(h,'policy-url');f=qd(h,'cancel-url');m=qd(h,'save-url');}catch(a){a=le(a);if(ee(a,34)){i=a;Cg('Exception: '+i.b);}else throw a;}aD(r,k);bD(r,l);s=zn(new xn());qi(Am('access-policy-editor-hook'),s);p=zn(new xn());An(s,p);q=ln(new dn());nn(q,30);An(p,q);o=Bi(new ui(),'Save User or Group',lC(new kC(),r,q));An(p,o);j=el(new cl());il(j,(Dk(),Ek));An(s,j);d=el(new cl());An(s,d);n=vv(m,'&amp;','&');r.e=Bi(new ui(),'Save Policy',pC(new oC(),r,n));sn(r.e,'gwt-wyona-SaveButton');fl(d,r.e);g=f;e=Bi(new ui(),'Cancel',tC(new sC(),r,g));sn(r.e,'gwt-wyona-CancelButton');fl(d,e);r.a=fE(new dE(),r.g);r.c=lE(new jE(),r.g,r.d,r.b,r.f);c=fD(new dD(),r.a.a,r.c.c,r.c);sn(c,'gwt-wyona-AddRemoveWidget');fl(j,r.a);fl(j,c);fl(j,r.c);}
function jC(){}
_=jC.prototype=new Bu();_.tN=AF+'AccessPolicyEditor';_.tI=0;_.a=null;_.b=null;_.c=null;_.d=null;_.e=null;_.f=true;_.g=10;function lC(b,a,c){b.a=a;b.b=c;return b;}
function nC(d){var a,b,c;b=Fl(this.a.a.a);for(a=0;a<b;a++){c=am(this.a.a.a,a);if(sv(c,hn(this.b))>=0)Cg('Result: '+c);}}
function kC(){}
_=kC.prototype=new Bu();_.ib=nC;_.tN=AF+'AccessPolicyEditor$1';_.tI=73;function pC(b,a,c){b.a=a;b.b=c;return b;}
function rC(f){var a,c,d,e;c=BD(new AD(),this.b);try{e=DD(c,wE(this.a.c),pE(this.a.c),vE(this.a.c));}catch(a){a=le(a);if(ee(a,35)){d=a;Cg('Exception: '+d.b);}else throw a;}}
function oC(){}
_=oC.prototype=new Bu();_.ib=rC;_.tN=AF+'AccessPolicyEditor$2';_.tI=74;function tC(b,a,c){b.a=c;return b;}
function vC(a,b){$wnd.location.href=b;}
function wC(a){vC(this,this.a);}
function sC(){}
_=sC.prototype=new Bu();_.ib=wC;_.tN=AF+'AccessPolicyEditor$3';_.tI=75;function zC(){zC=iC;ng();}
function yC(b,a,d,c){zC();b.a=a;b.c=d;b.b=c;lg(b);return b;}
function AC(){if(xc(this.c)){pg(this,10);}else{hE(this.a.a,this.a.g,qD(this.b),oD(this.b));CE(this.a.c,pD(this.b));mg(this);}}
function xC(){}
_=xC.prototype=new gg();_.ub=AC;_.tN=AF+'AccessPolicyEditor$4';_.tI=76;function DC(){DC=iC;ng();}
function CC(b,a,d,c){DC();b.a=a;b.c=d;b.b=c;lg(b);return b;}
function EC(){if(xc(this.c)){pg(this,10);}else{this.a.d=yD(this.b);this.a.b=xD(this.b);yE(this.a.c,this.a.g,this.a.d,this.a.b);this.a.f=this.b.b;zE(this.a.c,this.a.f);mg(this);}}
function BC(){}
_=BC.prototype=new gg();_.ub=EC;_.tN=AF+'AccessPolicyEditor$5';_.tI=77;function eD(a){a.b=fk(new ek());}
function fD(d,a,c,b){eD(d);Fj(d,d.b);d.f=Bi(new ui(),'<',d);gk(d.b,d.f);d.a=Bi(new ui(),'>',d);gk(d.b,d.a);d.c=a;d.d=c;d.e=b;return d;}
function hD(b,a){if(sv(a,'(')>0){return Av(a,0,sv(a,'('));}else{return a;}}
function iD(c){var a,b;if(c===this.a){a=bm(this.c);if(a>=0){b=cm(this.c,a);fm(this.c,a);mE(this.e,Av(b,0,1),Bv(zv(b,2)));}else{Cg('No identity selected yet! Please select an identity.');}}else if(c===this.f){a=bm(this.d);if(a>=0){b=cm(this.d,a);fm(this.d,a);Al(this.c,hD(this,b));}else{Cg('No identity selected yet! Please select an identity.');}}}
function dD(){}
_=dD.prototype=new Dj();_.ib=iD;_.tN=AF+'AddRemoveIdentitiesWidget';_.tI=78;_.a=null;_.c=null;_.d=null;_.e=null;_.f=null;function gF(a){a.d=nA(new rz());}
function hF(a,b){gF(a);a.e=Db(new yb(),(Fb(),dc),b);lF(a);return a;}
function iF(e){var a,b,c,d;b='';a=oA(new rz(),e.d);for(d=iA(tA(a));aA(d);){c=bA(d);b+=c.A()+''+c.C();if(aA(d)){b+='&';}}return b;}
function kF(a){return ac(a.e,iF(a),a);}
function lF(a){bc(a.e,'Content-Type','application/x-www-form-urlencoded');}
function mF(b,a){Cg('Exception: '+a.b);}
function fF(){}
_=fF.prototype=new Bu();_.kb=mF;_.tN=BF+'AsynchronousAgent';_.tI=0;_.e=null;function kD(a){a.c=CB(new BB());a.a=CB(new BB());a.b=CB(new BB());}
function lD(a,b){hF(a,b);kD(a);return a;}
function nD(d,c,a){var b;b=c.y(a);return de(b.bb(0),26);}
function oD(c){var a,b;a=Ed('[Ljava.lang.String;',[0],[1],[c.a.a.b],null);for(b=0;b<c.a.a.b;b++){a[b]=de(FB(c.a,b),1);}return a;}
function pD(c){var a,b;b=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[36],[c.b.a.b],null);for(a=0;a<c.b.a.b;a++){b[a]=de(FB(c.b,a),36);}return b;}
function qD(b){var a,c;c=Ed('[Ljava.lang.String;',[0],[1],[b.c.a.b],null);for(a=0;a<b.c.a.b;a++){c[a]=de(FB(b.c,a),1);}return c;}
function rD(e,f){var a,b,c,d,g,h,i,j,k;i=Cp(tb(f)).v();k=nD(this,i,'users');j=k.y('user');for(c=0;c<j.B();c++){DB(this.c,de(j.bb(c),26).u('id'));}b=nD(this,i,'groups');a=b.y('group');for(c=0;c<a.B();c++){DB(this.a,de(a.bb(c),26).u('id'));}h=nD(this,i,'rights');g=h.y('right');for(c=0;c<g.B();c++){d=tr(de(g.bb(c),26).z());DB(this.b,FE(new EE(),de(g.bb(c),26).u('id'),d));}}
function jD(){}
_=jD.prototype=new fF();_.mb=rD;_.tN=AF+'AsynchronousIdentitiesAndRightsGetter';_.tI=0;function tD(a){a.c=CB(new BB());a.a=CB(new BB());}
function uD(a,b){hF(a,b);tD(a);return a;}
function wD(d,c,a){var b;b=c.y(a);if(b.B()>0){return de(b.bb(0),26);}else{return null;}}
function xD(c){var a,b;b=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[38],[c.a.a.b],null);for(a=0;a<b.a;a++){b[a]=de(FB(c.a,a),38);}return b;}
function yD(c){var a,b;b=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[37],[c.c.a.b],null);for(a=0;a<b.a;a++){b[a]=de(FB(c.c,a),37);}return b;}
function zD(e,f){var a,b,c,d,g,h,i,j,k,l,m,n;j=Cp(tb(f)).v();k=j.u('use-inherited-policies');if(k===null){this.b=true;}else{if(rv(k,'false')){this.b=false;}else{this.b=true;}}n=wD(this,j,'world');m=j.y('user');for(c=0;c<m.B();c++){l=de(m.bb(c),26);h=l.y('right');i=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[36],[h.B()],null);for(d=0;d<i.a;d++){g=de(h.bb(d),26);i[d]=aF(new EE(),g.u('id'),true);}DB(this.c,dF(new cF(),l.u('id'),i));}b=j.y('group');for(c=0;c<b.B();c++){a=de(b.bb(c),26);h=a.y('right');i=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[36],[h.B()],null);for(d=0;d<i.a;d++){g=de(h.bb(d),26);i[d]=aF(new EE(),g.u('id'),true);}DB(this.a,bE(new aE(),a.u('id'),i));}}
function sD(){}
_=sD.prototype=new fF();_.mb=zD;_.tN=AF+'AsynchronousPolicyGetter';_.tI=0;_.b=true;function BD(a,b){a.a=Db(new yb(),(Fb(),ec),b);return a;}
function DD(f,h,b,g){var a,c,d,e;a=gv(new ev(),'<?xml version="1.0"?>');iv(a,'<policy xmlns="http://www.wyona.org/security/1.0" use-inherited-policies="'+g+'">');if(h!==null){for(c=0;c<h.a;c++){iv(a,'<user id="'+h[c].a+'">');e=h[c].b;if(e!==null){for(d=0;d<e.a;d++){iv(a,'<right id="'+e[d].a+'" permission="'+e[d].c+'">'+e[d].a+'<\/right>');}}else{iv(a,'<right id="r" permission="false">r<\/right>');iv(a,'<right id="w" permission="false">w<\/right>');}iv(a,'<\/user>');}}if(b!==null){for(c=0;c<b.a;c++){iv(a,'<group id="'+b[c].a+'">');e=b[c].b;if(e!==null){for(d=0;d<e.a;d++){iv(a,'<right id="'+e[d].a+'" permission="'+e[d].c+'">'+e[d].a+'<\/right>');}}else{iv(a,'<right id="r" permission="false">r<\/right>');iv(a,'<right id="w" permission="false">w<\/right>');}iv(a,'<\/group>');}}iv(a,'<\/policy>');return ac(f.a,mv(a),f);}
function ED(b,a){Cg('Exception: '+a.b);}
function FD(a,b){if(sb(b)==200){Cg('Policy has been saved successfully!');}else{Cg('Policy has NOT been saved! Please check log files on server.');}}
function AD(){}
_=AD.prototype=new Bu();_.kb=ED;_.mb=FD;_.tN=AF+'AsynchronousPolicySetter';_.tI=0;_.a=null;function bE(c,a,b){c.a=a;c.b=b;return c;}
function aE(){}
_=aE.prototype=new Bu();_.tN=AF+'Group';_.tI=79;_.a=null;_.b=null;function eE(a){a.b=zn(new xn());}
function fE(a,b){eE(a);Fj(a,a.b);An(a.b,nl(new ll(),'Identities'));a.a=zl(new rl(),true);a.a.k(a);hE(a,b,null,null);An(a.b,a.a);return a;}
function hE(c,e,d,a){var b;Dl(c.a);hm(c.a,e);if(d!==null){for(b=0;b<d.a;b++){Al(c.a,'u: '+d[b]);}}else{Al(c.a,'No users yet!');}if(a!==null){for(b=0;b<a.a;b++){Al(c.a,'g: '+a[b]);}}else{Al(c.a,'No groups yet!');}}
function iE(a){}
function dE(){}
_=dE.prototype=new Dj();_.ib=iE;_.tN=AF+'IdentitiesListBoxWidget';_.tI=80;_.a=null;function kE(a){a.e=zn(new xn());}
function lE(b,e,d,a,c){kE(b);Fj(b,b.e);An(b.e,nl(new ll(),'Policy'));b.d=gj(new dj(),'Inherit rights from parent policies');zE(b,c);An(b.e,b.d);b.c=zl(new rl(),true);b.c.k(b);yE(b,e,d,a);An(b.e,b.c);CE(b,null);return b;}
function mE(d,e,c){var a,b;a=gv(new ev(),'(-');for(b=1;b<d.b.a;b++){iv(a,',-');}iv(a,')');Bl(d.c,e+': '+a+' '+c,e+': '+c);}
function nE(e,a,d){var b,c;c=Ed('[Ljava.lang.String;',[0],[1],[e.a.a],null);for(b=0;b<c.a;b++){if(rv(e.a[b].a,d.a)){c[b]=d.a;}else{if(a[b].c){c[b]=a[b].a;}else{c[b]='-';}}}return c;}
function pE(g){var a,b,c,d,e,f;b=CB(new BB());for(c=0;c<Fl(g.c);c++){e=am(g.c,c);f=tE(g,e);d=qE(g,c);if(yv(d,'g:')){DB(b,bE(new aE(),Bv(zv(d,2)),f));}}a=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[0],[38],[b.a.b],null);for(c=0;c<a.a;c++){a[c]=de(FB(b,c),38);}return a;}
function qE(b,a){return cm(b.c,a);}
function rE(e,f,b,c){var a,d;d=gv(new ev(),f+':');iv(d,'('+c[0]);for(a=1;a<c.a;a++){iv(d,','+c[a]);}iv(d,')');iv(d,' '+b);return mv(d);}
function sE(g,h,b,e){var a,c,d,f;f=gv(new ev(),h+':');if(g.a!==null){iv(f,'(');for(a=0;a<g.a.a;a++){d=false;for(c=0;c<e.a;c++){if(rv(g.a[a].a,e[c].a)&&e[c].c){d=true;break;}}if(a>0){iv(f,',');}if(d){iv(f,g.a[a].a);}else{iv(f,'-');}}iv(f,')');}else{Cg('Available rights not loaded yet!');}iv(f,' '+b);return mv(f);}
function tE(e,b){var a,c,d;if(sv(b,'(')>0){d=wv(Av(b,sv(b,'(')+1,sv(b,')')),',');if(d.a!=e.a.a){Cg('Exception: Validation of rights length failed!');return null;}c=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[36],[e.a.a],null);for(a=0;a<d.a;a++){if(rv(d[a],'-')){c[a]=aF(new EE(),e.a[a].a,false);}else{c[a]=aF(new EE(),d[a],true);}}return c;}else{return Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[0],[36],[0],null);}}
function uE(b){var a;a=bm(b.c);if(a>=0){return am(b.c,a);}return null;}
function vE(a){return ij(a.d);}
function wE(e){var a,b,c,d,f,g;g=CB(new BB());for(a=0;a<Fl(e.c);a++){c=am(e.c,a);d=tE(e,c);b=qE(e,a);if(yv(b,'u:')){DB(g,dF(new cF(),Bv(zv(b,2)),d));}}f=Ed('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[0],[37],[g.a.b],null);for(a=0;a<f.a;a++){f[a]=de(FB(g,a),37);}return f;}
function xE(e,a,d){var b,c;c=Ed('[Ljava.lang.String;',[0],[1],[e.a.a],null);for(b=0;b<c.a;b++){if(rv(e.a[b].a,d.a)){c[b]='-';}else{if(a[b].c){c[b]=a[b].a;}else{c[b]='-';}}}return c;}
function CE(c,a){var b;c.a=a;if(a!==null){c.b=Ed('[Lcom.google.gwt.user.client.ui.CheckBox;',[0],[12],[a.a],null);for(b=0;b<c.b.a;b++){c.b[b]=gj(new dj(),a[b].b);kj(c.b[b],a[b].a);c.b[b].k(c);An(c.e,c.b[b]);}}else{}}
function yE(e,i,g,a){var b,c,d,f,h;Dl(e.c);hm(e.c,i);if(g!==null||a!==null){if(g!==null){for(b=0;b<g.a;b++){f='u';c=g[b].a;d=g[b].b;h=f+': '+c;Bl(e.c,sE(e,f,c,d),h);}}if(a!==null){for(b=0;b<a.a;b++){f='g';c=a[b].a;d=a[b].b;h=f+': '+c;Bl(e.c,sE(e,f,c,d),h);}}else{Cg('No groups!');}}else{Al(e.c,'No identities yet!');}}
function zE(a,b){if(a.d!==null){jj(a.d,b);}}
function AE(d,e,a,c,b){gm(d.c,b,rE(d,e,a,c));}
function BE(d,c){var a,b;b=bm(d.c);if(b>=0){a=qE(d,b);AE(d,Av(a,0,1),Bv(zv(a,2)),c,b);}else{Cg('Exception: No list item selected!');}}
function DE(i){var a,b,c,d,e,f,g,h;h=null;g=null;for(b=0;b<this.b.a;b++){if(i===this.b[b]){h=this.b[b];g=this.a[b];break;}}if(h!==null){f=uE(this);if(f!==null){a=tE(this,f);if(ij(h)){d=nE(this,a,g);}else{d=xE(this,a,g);}BE(this,d);}else{Cg('No identity has been selected! Please select an identity in order to assign rights.');jj(h,false);}}else if(i===this.c){f=uE(this);e=tE(this,f);for(c=0;c<this.b.a;c++){if(e[c].c){jj(this.b[c],true);}else{jj(this.b[c],false);}}}}
function jE(){}
_=jE.prototype=new Dj();_.ib=DE;_.tN=AF+'PolicyListBoxWidget';_.tI=81;_.a=null;_.b=null;_.c=null;_.d=null;function aF(c,a,b){c.a=a;c.c=b;return c;}
function FE(c,a,b){c.a=a;c.b=b;c.c=false;return c;}
function EE(){}
_=EE.prototype=new Bu();_.tN=AF+'Right';_.tI=82;_.a=null;_.b=null;_.c=false;function dF(c,a,b){c.a=a;c.b=b;return c;}
function cF(){}
_=cF.prototype=new Bu();_.tN=AF+'User';_.tI=83;_.a=null;_.b=null;function jt(){cD(new jC());}
function gwtOnLoad(b,d,c){$moduleName=d;$moduleBase=c;if(b)try{jt();}catch(a){b(d);}else{jt();}}
var he=[{},{},{1:1},{4:1},{4:1,35:1},{4:1,35:1},{4:1,28:1,35:1},{2:1},{6:1},{6:1},{4:1,35:1},{4:1,35:1},{4:1,35:1},{3:1},{2:1,5:1},{2:1},{7:1},{11:1,13:1,14:1,15:1},{10:1,11:1,13:1,14:1,15:1},{10:1,11:1,13:1,14:1,15:1},{10:1,11:1,13:1,14:1,15:1},{11:1,13:1,14:1,15:1,17:1,18:1,19:1,20:1,21:1},{11:1,13:1,14:1,15:1,16:1,17:1,18:1,19:1,20:1,21:1,22:1,23:1},{11:1,13:1,14:1,15:1,16:1,17:1,18:1,19:1,20:1,21:1,22:1,23:1},{10:1,11:1,13:1,14:1,15:1},{11:1,12:1,13:1,14:1,15:1,16:1,17:1,18:1,19:1,20:1,21:1,22:1,23:1,24:1},{30:1},{30:1},{30:1},{11:1,13:1,14:1,15:1},{10:1,11:1,13:1,14:1,15:1},{10:1,11:1,13:1,14:1,15:1},{11:1,13:1,14:1,15:1,18:1,23:1},{11:1,13:1,14:1,15:1,17:1,18:1,19:1,20:1,21:1,24:1},{9:1,10:1,11:1,13:1,14:1,15:1},{7:1},{11:1,13:1,14:1,15:1,17:1,18:1,19:1,20:1,21:1,23:1,24:1},{11:1,13:1,14:1,15:1,17:1,18:1,19:1,20:1,21:1,23:1,24:1},{10:1,11:1,13:1,14:1,15:1},{4:1,35:1},{25:1},{25:1},{25:1},{25:1},{25:1},{25:1},{25:1},{4:1,35:1},{25:1},{25:1,27:1},{25:1,26:1},{25:1},{25:1},{25:1},{4:1,35:1},{29:1},{4:1,35:1},{4:1,35:1},{4:1,35:1},{4:1,35:1},{4:1,35:1},{4:1,35:1},{4:1,35:1},{31:1},{32:1},{32:1},{31:1},{33:1},{32:1},{32:1},{4:1,34:1,35:1},{4:1,35:1},{30:1},{8:1},{8:1},{8:1},{6:1},{6:1},{8:1,11:1,13:1,14:1,15:1},{38:1},{8:1,11:1,13:1,14:1,15:1},{8:1,11:1,13:1,14:1,15:1},{36:1},{37:1}];if (org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor) {  var __gwt_initHandlers = org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.__gwt_initHandlers;  org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.onScriptLoad(gwtOnLoad);}})();