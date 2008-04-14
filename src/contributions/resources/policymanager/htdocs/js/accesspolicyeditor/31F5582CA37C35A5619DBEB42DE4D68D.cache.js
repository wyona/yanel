(function(){var $wnd = window;var $doc = $wnd.document;var $moduleName, $moduleBase;var _,rG='com.google.gwt.core.client.',sG='com.google.gwt.http.client.',tG='com.google.gwt.i18n.client.',uG='com.google.gwt.lang.',vG='com.google.gwt.user.client.',wG='com.google.gwt.user.client.impl.',xG='com.google.gwt.user.client.ui.',yG='com.google.gwt.user.client.ui.impl.',zG='com.google.gwt.xml.client.',AG='com.google.gwt.xml.client.impl.',BG='java.io.',CG='java.lang.',DG='java.util.',EG='org.wyona.security.gwt.accesspolicyeditor.client.',FG='org.wyona.yanel.gwt.client.';function lD(){}
function Av(a){return this===a;}
function Bv(){return fx(this);}
function Cv(){return this.tN+'@'+this.hC();}
function yv(){}
_=yv.prototype={};_.eQ=Av;_.hC=Bv;_.tS=Cv;_.toString=function(){return this.tS();};_.tN=CG+'Object';_.tI=1;function v(){return D();}
function w(a){return a==null?null:a.tN;}
var y=null;function B(a){return a==null?0:a.$H?a.$H:(a.$H=E());}
function C(a){return a==null?0:a.$H?a.$H:(a.$H=E());}
function D(){var b=$doc.location.href;var a=b.indexOf('#');if(a!= -1)b=b.substring(0,a);a=b.indexOf('?');if(a!= -1)b=b.substring(0,a);a=b.lastIndexOf('/');if(a!= -1)b=b.substring(0,a);return b.length>0?b+'/':'';}
function E(){return ++F;}
var F=0;function hx(b,a){b.b=a;return b;}
function jx(b,a){if(b.a!==null){throw ev(new dv(),"Can't overwrite cause");}if(a===b){throw bv(new av(),'Self-causation not permitted');}b.a=a;return b;}
function kx(a){lx(a,(dx(),ex));}
function lx(e,d){var a,b,c;c=cw(new bw());b=e;while(b!==null){a=b.b;if(b!==e){fw(c,'Caused by: ');}fw(c,b.tN);fw(c,': ');fw(c,a===null?'(No exception detail)':a);fw(c,'\n');b=b.a;}}
function mx(){var a,b;a=w(this);b=this.b;if(b!==null){return a+': '+b;}else{return a;}}
function gx(){}
_=gx.prototype=new yv();_.tS=mx;_.tN=CG+'Throwable';_.tI=3;_.a=null;_.b=null;function Eu(b,a){hx(b,a);return b;}
function Du(){}
_=Du.prototype=new gx();_.tN=CG+'Exception';_.tI=4;function Ev(b,a){Eu(b,a);return b;}
function Dv(){}
_=Dv.prototype=new Du();_.tN=CG+'RuntimeException';_.tI=5;function bb(c,b,a){Ev(c,'JavaScript '+b+' exception: '+a);return c;}
function ab(){}
_=ab.prototype=new Dv();_.tN=rG+'JavaScriptException';_.tI=6;function fb(b,a){if(!he(a,2)){return false;}return kb(b,ge(a,2));}
function gb(a){return B(a);}
function hb(){return [];}
function ib(){return function(){};}
function jb(){return {};}
function lb(a){return fb(this,a);}
function kb(a,b){return a===b;}
function mb(){return gb(this);}
function ob(){return nb(this);}
function nb(a){if(a.toString)return a.toString();return '[object]';}
function db(){}
_=db.prototype=new yv();_.eQ=lb;_.hC=mb;_.tS=ob;_.tN=rG+'JavaScriptObject';_.tI=7;function sc(b,d,c,a){if(d===null){throw new rv();}if(a===null){throw new rv();}if(c<0){throw new av();}b.a=c;b.c=d;if(c>0){b.b=wb(new vb(),b,a);zg(b.b,c);}else{b.b=null;}return b;}
function uc(a){var b;if(a.c!==null){b=a.c;a.c=null;ed(b);tc(a);}}
function tc(a){if(a.b!==null){vg(a.b);}}
function wc(e,a){var b,c,d,f;if(e.c===null){return;}tc(e);f=e.c;e.c=null;b=fd(f);if(b!==null){c=Ev(new Dv(),b);a.kb(e,c);}else{d=zc(f);a.pb(e,d);}}
function xc(b,a){if(b.c===null){return;}uc(b);a.kb(b,pc(new oc(),b,b.a));}
function yc(b){var a;if(b.c===null){return false;}a=gd(b.c);switch(a){case 1:case 2:case 3:return true;}return false;}
function zc(b){var a;a=rb(new qb(),b);return a;}
function Ac(a){var b;b=y;{wc(this,a);}}
function pb(){}
_=pb.prototype=new yv();_.s=Ac;_.tN=sG+'Request';_.tI=8;_.a=0;_.b=null;_.c=null;function Bc(){}
_=Bc.prototype=new yv();_.tN=sG+'Response';_.tI=9;function rb(a,b){a.a=b;return a;}
function tb(a){return id(a.a);}
function ub(a){return hd(a.a);}
function qb(){}
_=qb.prototype=new Bc();_.tN=sG+'Request$1';_.tI=10;function wg(){wg=lD;ah=wz(new uz());{Fg();}}
function ug(a){wg();return a;}
function vg(a){if(a.d){Ag(a.e);}else{Bg(a.e);}cA(ah,a);}
function xg(a){if(!a.d){cA(ah,a);}a.xb();}
function zg(b,a){if(a<=0){throw bv(new av(),'must be positive');}vg(b);b.d=false;b.e=Dg(b,a);Az(ah,b);}
function yg(b,a){if(a<=0){throw bv(new av(),'must be positive');}vg(b);b.d=true;b.e=Cg(b,a);Az(ah,b);}
function Ag(a){wg();$wnd.clearInterval(a);}
function Bg(a){wg();$wnd.clearTimeout(a);}
function Cg(b,a){wg();return $wnd.setInterval(function(){b.t();},a);}
function Dg(b,a){wg();return $wnd.setTimeout(function(){b.t();},a);}
function Eg(){var a;a=y;{xg(this);}}
function Fg(){wg();eh(new qg());}
function pg(){}
_=pg.prototype=new yv();_.t=Eg;_.tN=vG+'Timer';_.tI=11;_.d=false;_.e=0;var ah;function xb(){xb=lD;wg();}
function wb(b,a,c){xb();b.a=a;b.b=c;ug(b);return b;}
function yb(){xc(this.a,this.b);}
function vb(){}
_=vb.prototype=new pg();_.xb=yb;_.tN=sG+'Request$2';_.tI=12;function ac(){ac=lD;ec=Bb(new Ab(),'GET');fc=Bb(new Ab(),'POST');gc=xi(new wi());}
function Eb(b,a,c){ac();Fb(b,a===null?null:a.a,c);return b;}
function Fb(b,a,c){ac();Fc('httpMethod',a);Fc('url',c);b.b=a;b.d=c;return b;}
function bc(g,d,a){var b,c,e,f,h;h=zi(gc);{b=jd(h,g.b,g.d,true);}if(b!==null){e=mc(new lc(),g.d);jx(e,jc(new ic(),b));throw e;}dc(g,h);c=sc(new pb(),h,g.c,a);f=kd(h,c,d,a);if(f!==null){throw jc(new ic(),f);}return c;}
function cc(b,a,c){Fc('header',a);Fc('value',c);if(b.a===null){b.a=qB(new uA());}zB(b.a,a,c);}
function dc(e,f){var a,b,c,d;if(e.a!==null&&e.a.c>0){a=wB(e.a);d=lB(a);while(dB(d)){c=eB(d);b=ld(f,ge(c.A(),1),ge(c.C(),1));if(b!==null){throw jc(new ic(),b);}}}else{ld(f,'Content-Type','text/plain; charset=utf-8');}}
function zb(){}
_=zb.prototype=new yv();_.tN=sG+'RequestBuilder';_.tI=13;_.a=null;_.b=null;_.c=0;_.d=null;var ec,fc,gc;function Bb(b,a){b.a=a;return b;}
function Db(){return this.a;}
function Ab(){}
_=Ab.prototype=new yv();_.tS=Db;_.tN=sG+'RequestBuilder$Method';_.tI=14;_.a=null;function jc(b,a){Eu(b,a);return b;}
function ic(){}
_=ic.prototype=new Du();_.tN=sG+'RequestException';_.tI=15;function mc(a,b){jc(a,'The URL '+b+' is invalid or violates the same-origin security restriction');return a;}
function lc(){}
_=lc.prototype=new ic();_.tN=sG+'RequestPermissionException';_.tI=16;function pc(b,a,c){jc(b,rc(c));return b;}
function rc(a){return 'A request timeout has expired after '+lv(a)+' ms';}
function oc(){}
_=oc.prototype=new ic();_.tN=sG+'RequestTimeoutException';_.tI=17;function Fc(a,b){ad(a,b);if(0==rw(yw(b))){throw bv(new av(),a+' can not be empty');}}
function ad(a,b){if(null===b){throw sv(new rv(),a+' can not be null');}}
function ed(a){a.onreadystatechange=Bi;a.abort();}
function fd(b){try{if(b.status===undefined){return 'XmlHttpRequest.status == undefined, please see Safari bug '+'http://bugs.webkit.org/show_bug.cgi?id=3810 for more details';}return null;}catch(a){return 'Unable to read XmlHttpRequest.status; likely causes are a '+'networking error or bad cross-domain request. Please see '+'https://bugzilla.mozilla.org/show_bug.cgi?id=238559 for more '+'details';}}
function gd(a){return a.readyState;}
function hd(a){return a.responseText;}
function id(a){return a.status;}
function jd(e,c,d,b){try{e.open(c,d,b);return null;}catch(a){return a.message||a.toString();}}
function kd(e,c,d,b){e.onreadystatechange=function(){if(e.readyState==dd){e.onreadystatechange=Bi;c.s(b);}};try{e.send(d);return null;}catch(a){e.onreadystatechange=Bi;return a.message||a.toString();}}
function ld(d,b,c){try{d.setRequestHeader(b,c);return null;}catch(a){return a.message||a.toString();}}
var dd=4;function qd(){qd=lD;td=qB(new uA());}
function nd(b,a){qd();if(a===null||ow('',a)){throw bv(new av(),'Cannot create a Dictionary with a null or empty name');}b.b='Dictionary '+a;pd(b,a);if(b.a===null){throw yC(new xC(),"Cannot find JavaScript object with the name '"+a+"'",a,null);}return b;}
function od(b,a){for(x in b.a){a.m(x);}}
function pd(c,b){try{if(typeof $wnd[b]!='object'){vd(b);}c.a=$wnd[b];}catch(a){vd(b);}}
function rd(b,a){var c=b.a[a];if(c==null|| !Object.prototype.hasOwnProperty.call(b.a,a)){b.wb(a);}return String(c);}
function sd(b){var a;a=mC(new lC());od(b,a);return a;}
function ud(a){qd();var b;b=ge(xB(td,a),3);if(b===null){b=nd(new md(),a);zB(td,a,b);}return b;}
function wd(b){var a,c;c=sd(this);a="Cannot find '"+b+"' in "+this;if(c.a.c<20){a+='\n keys found: '+c;}throw yC(new xC(),a,this.b,b);}
function vd(a){qd();throw yC(new xC(),"'"+a+"' is not a JavaScript object and cannot be used as a Dictionary",null,a);}
function xd(){return this.b;}
function md(){}
_=md.prototype=new yv();_.wb=wd;_.tS=xd;_.tN=tG+'Dictionary';_.tI=18;_.a=null;_.b=null;var td;function zd(c,a,d,b,e){c.a=a;c.b=b;c.tN=e;c.tI=d;return c;}
function Bd(a,b,c){return a[b]=c;}
function Dd(a,b){return Cd(a,b);}
function Cd(a,b){return zd(new yd(),b,a.tI,a.b,a.tN);}
function Ed(b,a){return b[a];}
function Fd(a){return a.length;}
function be(e,d,c,b,a){return ae(e,d,c,b,0,Fd(b),a);}
function ae(j,i,g,c,e,a,b){var d,f,h;if((f=Ed(c,e))<0){throw new pv();}h=zd(new yd(),f,Ed(i,e),Ed(g,e),j);++e;if(e<a){j=ww(j,1);for(d=0;d<f;++d){Bd(h,d,ae(j,i,g,c,e,a,b));}}else{for(d=0;d<f;++d){Bd(h,d,b);}}return h;}
function ce(a,b,c){if(c!==null&&a.b!=0&& !he(c,a.b)){throw new nu();}return Bd(a,b,c);}
function yd(){}
_=yd.prototype=new yv();_.tN=uG+'Array';_.tI=19;function fe(b,a){return !(!(b&&le[b][a]));}
function ge(b,a){if(b!=null)fe(b.tI,a)||ke();return b;}
function he(b,a){return b!=null&&fe(b.tI,a);}
function ie(a){return a&65535;}
function ke(){throw new zu();}
function je(a){if(a!==null){throw new zu();}return a;}
function me(b,d){_=d.prototype;if(b&& !(b.tI>=_.tI)){var c=b.toString;for(var a in _){b[a]=_[a];}b.toString=c;}return b;}
var le;function pe(a){if(he(a,4)){return a;}return bb(new ab(),re(a),qe(a));}
function qe(a){return a.message;}
function re(a){return a.name;}
function te(){te=lD;zf=wz(new uz());{uf=new qh();yh(uf);}}
function ue(b,a){te();Ah(uf,b,a);}
function ve(a,b){te();return uh(uf,a,b);}
function we(){te();return Ch(uf,'button');}
function xe(){te();return Ch(uf,'div');}
function ye(){te();return Dh(uf,'checkbox');}
function ze(){te();return Dh(uf,'text');}
function Ae(){te();return Ch(uf,'label');}
function Be(a){te();return Eh(uf,a);}
function Ce(){te();return Ch(uf,'span');}
function De(){te();return Ch(uf,'tbody');}
function Ee(){te();return Ch(uf,'td');}
function Fe(){te();return Ch(uf,'tr');}
function af(){te();return Ch(uf,'table');}
function df(b,a,d){te();var c;c=y;{cf(b,a,d);}}
function cf(b,a,c){te();var d;if(a===yf){if(lf(b)==8192){yf=null;}}d=bf;bf=b;try{c.hb(b);}finally{bf=d;}}
function ef(b,a){te();Fh(uf,b,a);}
function ff(a){te();return ai(uf,a);}
function gf(a){te();return bi(uf,a);}
function hf(a){te();return ci(uf,a);}
function jf(a){te();return di(uf,a);}
function kf(a){te();return ei(uf,a);}
function lf(a){te();return fi(uf,a);}
function mf(a){te();vh(uf,a);}
function nf(a){te();return wh(uf,a);}
function of(a){te();return gi(uf,a);}
function rf(a,b){te();return ji(uf,a,b);}
function pf(a,b){te();return hi(uf,a,b);}
function qf(a,b){te();return ii(uf,a,b);}
function sf(a){te();return ki(uf,a);}
function tf(a){te();return xh(uf,a);}
function vf(c,b,d,a){te();li(uf,c,b,d,a);}
function wf(a){te();var b,c;c=true;if(zf.b>0){b=je(Ez(zf,zf.b-1));if(!(c=null.Db())){ef(a,true);mf(a);}}return c;}
function xf(b,a){te();mi(uf,b,a);}
function Cf(a,b,c){te();pi(uf,a,b,c);}
function Af(a,b,c){te();ni(uf,a,b,c);}
function Bf(a,b,c){te();oi(uf,a,b,c);}
function Df(a,b){te();qi(uf,a,b);}
function Ef(a,b){te();ri(uf,a,b);}
function Ff(a,b){te();si(uf,a,b);}
function ag(b,c,a){te();ti(uf,b,c,a);}
function bg(b,a,c){te();ui(uf,b,a,c);}
function cg(a,b){te();zh(uf,a,b);}
function dg(a){te();return vi(uf,a);}
var bf=null,uf=null,yf=null,zf;function gg(a){if(he(a,5)){return ve(this,ge(a,5));}return fb(me(this,eg),a);}
function hg(){return gb(me(this,eg));}
function ig(){return dg(this);}
function eg(){}
_=eg.prototype=new db();_.eQ=gg;_.hC=hg;_.tS=ig;_.tN=vG+'Element';_.tI=22;function mg(a){return fb(me(this,jg),a);}
function ng(){return gb(me(this,jg));}
function og(){return nf(this);}
function jg(){}
_=jg.prototype=new db();_.eQ=mg;_.hC=ng;_.tS=og;_.tN=vG+'Event';_.tI=23;function sg(){while((wg(),ah).b>0){vg(ge(Ez((wg(),ah),0),6));}}
function tg(){return null;}
function qg(){}
_=qg.prototype=new yv();_.rb=sg;_.sb=tg;_.tN=vG+'Timer$1';_.tI=24;function dh(){dh=lD;gh=wz(new uz());oh=wz(new uz());{kh();}}
function eh(a){dh();Az(gh,a);}
function fh(a){dh();$wnd.alert(a);}
function hh(){dh();var a,b;for(a=gh.cb();a.F();){b=ge(a.eb(),7);b.rb();}}
function ih(){dh();var a,b,c,d;d=null;for(a=gh.cb();a.F();){b=ge(a.eb(),7);c=b.sb();{d=c;}}return d;}
function jh(){dh();var a,b;for(a=oh.cb();a.F();){b=je(a.eb());null.Db();}}
function kh(){dh();__gwt_initHandlers(function(){nh();},function(){return mh();},function(){lh();$wnd.onresize=null;$wnd.onbeforeclose=null;$wnd.onclose=null;});}
function lh(){dh();var a;a=y;{hh();}}
function mh(){dh();var a;a=y;{return ih();}}
function nh(){dh();var a;a=y;{jh();}}
var gh,oh;function Ah(c,b,a){b.appendChild(a);}
function Ch(b,a){return $doc.createElement(a);}
function Dh(b,c){var a=$doc.createElement('INPUT');a.type=c;return a;}
function Eh(c,a){var b;b=Ch(c,'select');if(a){ni(c,b,'multiple',true);}return b;}
function Fh(c,b,a){b.cancelBubble=a;}
function ai(b,a){return !(!a.altKey);}
function bi(b,a){return !(!a.ctrlKey);}
function ci(b,a){return a.which||(a.keyCode|| -1);}
function di(b,a){return !(!a.metaKey);}
function ei(b,a){return !(!a.shiftKey);}
function fi(b,a){switch(a.type){case 'blur':return 4096;case 'change':return 1024;case 'click':return 1;case 'dblclick':return 2;case 'focus':return 2048;case 'keydown':return 128;case 'keypress':return 256;case 'keyup':return 512;case 'load':return 32768;case 'losecapture':return 8192;case 'mousedown':return 4;case 'mousemove':return 64;case 'mouseout':return 32;case 'mouseover':return 16;case 'mouseup':return 8;case 'scroll':return 16384;case 'error':return 65536;case 'mousewheel':return 131072;case 'DOMMouseScroll':return 131072;}}
function gi(c,b){var a=$doc.getElementById(b);return a||null;}
function ji(d,a,b){var c=a[b];return c==null?null:String(c);}
function hi(c,a,b){return !(!a[b]);}
function ii(d,a,c){var b=parseInt(a[c]);if(!b){return 0;}return b;}
function ki(b,a){return a.__eventBits||0;}
function li(e,d,b,f,a){var c=new Option(b,f);if(a== -1||a>d.options.length-1){d.add(c,null);}else{d.add(c,d.options[a]);}}
function mi(c,b,a){b.removeChild(a);}
function pi(c,a,b,d){a[b]=d;}
function ni(c,a,b,d){a[b]=d;}
function oi(c,a,b,d){a[b]=d;}
function qi(c,a,b){a.__listener=b;}
function ri(c,a,b){if(!b){b='';}a.innerHTML=b;}
function si(c,a,b){while(a.firstChild){a.removeChild(a.firstChild);}if(b!=null){a.appendChild($doc.createTextNode(b));}}
function ti(e,c,d,a){var b=c.options[a];b.text=d;}
function ui(c,b,a,d){b.style[a]=d;}
function vi(b,a){return a.outerHTML;}
function ph(){}
_=ph.prototype=new yv();_.tN=wG+'DOMImpl';_.tI=25;function uh(c,a,b){return a==b;}
function vh(b,a){a.preventDefault();}
function wh(b,a){return a.toString();}
function xh(c,a){var b=a.parentNode;if(b==null){return null;}if(b.nodeType!=1)b=null;return b||null;}
function yh(d){$wnd.__dispatchCapturedMouseEvent=function(b){if($wnd.__dispatchCapturedEvent(b)){var a=$wnd.__captureElem;if(a&&a.__listener){df(b,a,a.__listener);b.stopPropagation();}}};$wnd.__dispatchCapturedEvent=function(a){if(!wf(a)){a.stopPropagation();a.preventDefault();return false;}return true;};$wnd.addEventListener('click',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('dblclick',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousedown',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mouseup',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousemove',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousewheel',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('keydown',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keyup',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keypress',$wnd.__dispatchCapturedEvent,true);$wnd.__dispatchEvent=function(b){var c,a=this;while(a&& !(c=a.__listener))a=a.parentNode;if(a&&a.nodeType!=1)a=null;if(c)df(b,a,c);};$wnd.__captureElem=null;}
function zh(c,b,a){b.__eventBits=a;b.onclick=a&1?$wnd.__dispatchEvent:null;b.ondblclick=a&2?$wnd.__dispatchEvent:null;b.onmousedown=a&4?$wnd.__dispatchEvent:null;b.onmouseup=a&8?$wnd.__dispatchEvent:null;b.onmouseover=a&16?$wnd.__dispatchEvent:null;b.onmouseout=a&32?$wnd.__dispatchEvent:null;b.onmousemove=a&64?$wnd.__dispatchEvent:null;b.onkeydown=a&128?$wnd.__dispatchEvent:null;b.onkeypress=a&256?$wnd.__dispatchEvent:null;b.onkeyup=a&512?$wnd.__dispatchEvent:null;b.onchange=a&1024?$wnd.__dispatchEvent:null;b.onfocus=a&2048?$wnd.__dispatchEvent:null;b.onblur=a&4096?$wnd.__dispatchEvent:null;b.onlosecapture=a&8192?$wnd.__dispatchEvent:null;b.onscroll=a&16384?$wnd.__dispatchEvent:null;b.onload=a&32768?$wnd.__dispatchEvent:null;b.onerror=a&65536?$wnd.__dispatchEvent:null;b.onmousewheel=a&131072?$wnd.__dispatchEvent:null;}
function sh(){}
_=sh.prototype=new ph();_.tN=wG+'DOMImplStandard';_.tI=26;function qh(){}
_=qh.prototype=new sh();_.tN=wG+'DOMImplOpera';_.tI=27;function xi(a){Bi=ib();return a;}
function zi(a){return Ai(a);}
function Ai(a){return new XMLHttpRequest();}
function wi(){}
_=wi.prototype=new yv();_.tN=wG+'HTTPRequestImpl';_.tI=28;var Bi=null;function oo(d,b,a){var c=b.parentNode;if(!c){return;}c.insertBefore(a,b);c.removeChild(b);}
function po(b,a){if(b.j!==null){oo(b,b.j,a);}b.j=a;}
function qo(b,a){to(b.j,a);}
function ro(b,a){cg(b.w(),a|sf(b.w()));}
function so(){return this.j;}
function to(a,b){Cf(a,'className',b);}
function uo(){if(this.j===null){return '(null handle)';}return dg(this.j);}
function mo(){}
_=mo.prototype=new yv();_.w=so;_.tS=uo;_.tN=xG+'UIObject';_.tI=29;_.j=null;function pp(a){if(he(a.i,11)){ge(a.i,11).vb(a);}else if(a.i!==null){throw ev(new dv(),"This widget's parent does not implement HasWidgets");}}
function qp(b,a){if(b.ab()){Df(b.w(),null);}po(b,a);if(b.ab()){Df(a,b);}}
function rp(c,b){var a;a=c.i;if(b===null){if(a!==null&&a.ab()){c.jb();}c.i=null;}else{if(a!==null){throw ev(new dv(),'Cannot set a new parent without first clearing the old parent');}c.i=b;if(b.ab()){c.gb();}}}
function sp(){}
function tp(){}
function up(){return this.h;}
function vp(){if(this.ab()){throw ev(new dv(),"Should only call onAttach when the widget is detached from the browser's document");}this.h=true;Df(this.w(),this);this.p();this.ob();}
function wp(a){}
function xp(){if(!this.ab()){throw ev(new dv(),"Should only call onDetach when the widget is attached to the browser's document");}try{this.qb();}finally{this.q();Df(this.w(),null);this.h=false;}}
function yp(){}
function zp(){}
function Ap(a){qp(this,a);}
function Co(){}
_=Co.prototype=new mo();_.p=sp;_.q=tp;_.ab=up;_.gb=vp;_.hb=wp;_.jb=xp;_.ob=yp;_.qb=zp;_.yb=Ap;_.tN=xG+'Widget';_.tI=30;_.h=false;_.i=null;function hn(b,a){rp(a,b);}
function kn(b,a){rp(a,null);}
function ln(){var a,b;for(b=this.cb();bp(b);){a=cp(b);a.gb();}}
function mn(){var a,b;for(b=this.cb();bp(b);){a=cp(b);a.jb();}}
function nn(){}
function on(){}
function gn(){}
_=gn.prototype=new Co();_.p=ln;_.q=mn;_.ob=nn;_.qb=on;_.tN=xG+'Panel';_.tI=31;function ek(a){a.f=gp(new Do(),a);}
function fk(a){ek(a);return a;}
function gk(c,a,b){pp(a);hp(c.f,a);ue(b,a.w());hn(c,a);}
function ik(b,c){var a;if(c.i!==b){return false;}kn(b,c);a=c.w();xf(tf(a),a);np(b.f,c);return true;}
function jk(){return lp(this.f);}
function kk(a){return ik(this,a);}
function dk(){}
_=dk.prototype=new gn();_.cb=jk;_.vb=kk;_.tN=xG+'ComplexPanel';_.tI=32;function Di(a){fk(a);a.yb(xe());bg(a.w(),'position','relative');bg(a.w(),'overflow','hidden');return a;}
function Ei(a,b){gk(a,b,a.w());}
function aj(a){bg(a,'left','');bg(a,'top','');bg(a,'position','');}
function bj(b){var a;a=ik(this,b);if(a){aj(b.w());}return a;}
function Ci(){}
_=Ci.prototype=new dk();_.vb=bj;_.tN=xG+'AbsolutePanel';_.tI=33;function yk(){yk=lD;eq(),gq;}
function xk(b,a){eq(),gq;Ak(b,a);return b;}
function zk(b,a){switch(lf(a)){case 1:if(b.c!==null){bk(b.c,b);}break;case 4096:case 2048:break;case 128:case 512:case 256:break;}}
function Ak(b,a){qp(b,a);ro(b,7041);}
function Bk(a){if(this.c===null){this.c=Fj(new Ej());}Az(this.c,a);}
function Ck(a){zk(this,a);}
function Dk(a){Ak(this,a);}
function wk(){}
_=wk.prototype=new Co();_.k=Bk;_.hb=Ck;_.yb=Dk;_.tN=xG+'FocusWidget';_.tI=34;_.c=null;function fj(){fj=lD;eq(),gq;}
function ej(b,a){eq(),gq;xk(b,a);return b;}
function gj(a){Ef(this.w(),a);}
function dj(){}
_=dj.prototype=new wk();_.zb=gj;_.tN=xG+'ButtonBase';_.tI=35;function kj(){kj=lD;eq(),gq;}
function hj(a){eq(),gq;ej(a,we());lj(a.w());qo(a,'gwt-Button');return a;}
function ij(b,a){eq(),gq;hj(b);b.zb(a);return b;}
function jj(c,a,b){eq(),gq;ij(c,a);c.k(b);return c;}
function lj(b){kj();if(b.type=='submit'){try{b.setAttribute('type','button');}catch(a){}}}
function cj(){}
_=cj.prototype=new dj();_.tN=xG+'Button';_.tI=36;function nj(a){fk(a);a.e=af();a.d=De();ue(a.e,a.d);a.yb(a.e);return a;}
function pj(c,b,a){Cf(b,'align',a.a);}
function qj(c,b,a){bg(b,'verticalAlign',a.a);}
function mj(){}
_=mj.prototype=new dk();_.tN=xG+'CellPanel';_.tI=37;_.d=null;_.e=null;function vj(){vj=lD;eq(),gq;}
function sj(a){eq(),gq;tj(a,ye());qo(a,'gwt-CheckBox');return a;}
function uj(b,a){eq(),gq;sj(b);zj(b,a);return b;}
function tj(b,a){var c;eq(),gq;ej(b,Ce());b.a=a;b.b=Ae();cg(b.a,sf(b.w()));cg(b.w(),0);ue(b.w(),b.a);ue(b.w(),b.b);c='check'+ ++Dj;Cf(b.a,'id',c);Cf(b.b,'htmlFor',c);return b;}
function wj(b){var a;a=b.ab()?'checked':'defaultChecked';return pf(b.a,a);}
function xj(b,a){Af(b.a,'checked',a);Af(b.a,'defaultChecked',a);}
function yj(b,a){Cf(b.a,'name',a);}
function zj(b,a){Ff(b.b,a);}
function Aj(){Df(this.a,this);}
function Bj(){Df(this.a,null);xj(this,wj(this));}
function Cj(a){Ef(this.b,a);}
function rj(){}
_=rj.prototype=new dj();_.ob=Aj;_.qb=Bj;_.zb=Cj;_.tN=xG+'CheckBox';_.tI=38;_.a=null;_.b=null;var Dj=0;function rx(d,a,b){var c;while(a.F()){c=a.eb();if(b===null?c===null:b.eQ(c)){return a;}}return null;}
function tx(a){throw ox(new nx(),'add');}
function ux(b){var a;a=rx(this,this.cb(),b);return a!==null;}
function vx(){var a,b,c;c=cw(new bw());a=null;fw(c,'[');b=this.cb();while(b.F()){if(a!==null){fw(c,a);}else{a=', ';}fw(c,bx(b.eb()));}fw(c,']');return jw(c);}
function qx(){}
_=qx.prototype=new yv();_.m=tx;_.o=ux;_.tS=vx;_.tN=DG+'AbstractCollection';_.tI=39;function Fx(b,a){throw hv(new gv(),'Index: '+a+', Size: '+b.b);}
function ay(b,a){throw ox(new nx(),'add');}
function by(a){this.l(this.Bb(),a);return true;}
function cy(e){var a,b,c,d,f;if(e===this){return true;}if(!he(e,31)){return false;}f=ge(e,31);if(this.Bb()!=f.Bb()){return false;}c=this.cb();d=f.cb();while(c.F()){a=c.eb();b=d.eb();if(!(a===null?b===null:a.eQ(b))){return false;}}return true;}
function dy(){var a,b,c,d;c=1;a=31;b=this.cb();while(b.F()){d=b.eb();c=31*c+(d===null?0:d.hC());}return c;}
function ey(){return yx(new xx(),this);}
function fy(a){throw ox(new nx(),'remove');}
function wx(){}
_=wx.prototype=new qx();_.l=ay;_.m=by;_.eQ=cy;_.hC=dy;_.cb=ey;_.ub=fy;_.tN=DG+'AbstractList';_.tI=40;function vz(a){{Bz(a);}}
function wz(a){vz(a);return a;}
function xz(b,a){vz(b);yz(b,a);return b;}
function zz(c,a,b){if(a<0||a>c.b){Fx(c,a);}eA(c.a,a,b);++c.b;}
function Az(b,a){nA(b.a,b.b++,a);return true;}
function yz(d,a){var b,c;c=a.cb();b=c.F();while(c.F()){nA(d.a,d.b++,c.eb());}return b;}
function Bz(a){a.a=hb();a.b=0;}
function Dz(b,a){return Fz(b,a)!=(-1);}
function Ez(b,a){if(a<0||a>=b.b){Fx(b,a);}return jA(b.a,a);}
function Fz(b,a){return aA(b,a,0);}
function aA(c,b,a){if(a<0){Fx(c,a);}for(;a<c.b;++a){if(iA(b,jA(c.a,a))){return a;}}return (-1);}
function bA(c,a){var b;b=Ez(c,a);lA(c.a,a,1);--c.b;return b;}
function cA(c,b){var a;a=Fz(c,b);if(a==(-1)){return false;}bA(c,a);return true;}
function dA(c,a){var b;if(a.a<c.b){a=Dd(a,c.b);}for(b=0;b<c.b;++b){ce(a,b,jA(c.a,b));}if(a.a>c.b){ce(a,c.b,null);}return a;}
function fA(a,b){zz(this,a,b);}
function gA(a){return Az(this,a);}
function eA(a,b,c){a.splice(b,0,c);}
function hA(a){return Dz(this,a);}
function iA(a,b){return a===b||a!==null&&a.eQ(b);}
function kA(a){return Ez(this,a);}
function jA(a,b){return a[b];}
function mA(a){return bA(this,a);}
function lA(a,c,b){a.splice(c,b);}
function nA(a,b,c){a[b]=c;}
function oA(){return this.b;}
function uz(){}
_=uz.prototype=new wx();_.l=fA;_.m=gA;_.o=hA;_.D=kA;_.ub=mA;_.Bb=oA;_.tN=DG+'ArrayList';_.tI=41;_.a=null;_.b=0;function Fj(a){wz(a);return a;}
function bk(d,c){var a,b;for(a=d.cb();a.F();){b=ge(a.eb(),8);b.ib(c);}}
function Ej(){}
_=Ej.prototype=new uz();_.tN=xG+'ClickListenerCollection';_.tI=42;function nk(a,b){if(a.g!==null){throw ev(new dv(),'Composite.initWidget() may only be called once.');}pp(b);a.yb(b.w());a.g=b;rp(b,a);}
function ok(){if(this.g===null){throw ev(new dv(),'initWidget() was never called in '+w(this));}return this.j;}
function pk(){if(this.g!==null){return this.g.ab();}return false;}
function qk(){this.g.gb();this.ob();}
function rk(){try{this.qb();}finally{this.g.jb();}}
function lk(){}
_=lk.prototype=new Co();_.w=ok;_.ab=pk;_.gb=qk;_.jb=rk;_.tN=xG+'Composite';_.tI=43;_.g=null;function tk(a){fk(a);a.yb(xe());return a;}
function uk(a,b){gk(a,b,a.w());}
function sk(){}
_=sk.prototype=new dk();_.tN=xG+'FlowPanel';_.tI=44;function el(){el=lD;cl(new bl(),'center');fl=cl(new bl(),'left');cl(new bl(),'right');}
var fl;function cl(b,a){b.a=a;return b;}
function bl(){}
_=bl.prototype=new yv();_.tN=xG+'HasHorizontalAlignment$HorizontalAlignmentConstant';_.tI=45;_.a=null;function ll(){ll=lD;jl(new il(),'bottom');ml=jl(new il(),'middle');nl=jl(new il(),'top');}
var ml,nl;function jl(a,b){a.a=b;return a;}
function il(){}
_=il.prototype=new yv();_.tN=xG+'HasVerticalAlignment$VerticalAlignmentConstant';_.tI=46;_.a=null;function rl(a){a.a=(el(),fl);a.c=(ll(),nl);}
function sl(a){nj(a);rl(a);a.b=Fe();ue(a.d,a.b);Cf(a.e,'cellSpacing','0');Cf(a.e,'cellPadding','0');return a;}
function tl(b,c){var a;a=vl(b);ue(b.b,a);gk(b,c,a);}
function vl(b){var a;a=Ee();pj(b,a,b.a);qj(b,a,b.c);return a;}
function wl(b,a){b.c=a;}
function xl(c){var a,b;b=tf(c.w());a=ik(this,c);if(a){xf(this.b,b);}return a;}
function ql(){}
_=ql.prototype=new mj();_.vb=xl;_.tN=xG+'HorizontalPanel';_.tI=47;_.b=null;function Bl(c,a,b){}
function Cl(c,a,b){}
function Dl(c,a,b){}
function zl(){}
_=zl.prototype=new yv();_.lb=Bl;_.mb=Cl;_.nb=Dl;_.tN=xG+'KeyboardListenerAdapter';_.tI=48;function Fl(a){wz(a);return a;}
function bm(f,e,b,d){var a,c;for(a=f.cb();a.F();){c=ge(a.eb(),9);c.lb(e,b,d);}}
function cm(f,e,b,d){var a,c;for(a=f.cb();a.F();){c=ge(a.eb(),9);c.mb(e,b,d);}}
function dm(f,e,b,d){var a,c;for(a=f.cb();a.F();){c=ge(a.eb(),9);c.nb(e,b,d);}}
function em(d,c,a){var b;b=fm(a);switch(lf(a)){case 128:bm(d,c,ie(hf(a)),b);break;case 512:dm(d,c,ie(hf(a)),b);break;case 256:cm(d,c,ie(hf(a)),b);break;}}
function fm(a){return (kf(a)?1:0)|(jf(a)?8:0)|(gf(a)?2:0)|(ff(a)?4:0);}
function El(){}
_=El.prototype=new uz();_.tN=xG+'KeyboardListenerCollection';_.tI=49;function im(a){a.yb(xe());ro(a,131197);qo(a,'gwt-Label');return a;}
function jm(b,a){im(b);lm(b,a);return b;}
function lm(b,a){Ff(b.w(),a);}
function mm(a){switch(lf(a)){case 1:break;case 4:case 8:case 64:case 16:case 32:break;case 131072:break;}}
function hm(){}
_=hm.prototype=new Co();_.hb=mm;_.tN=xG+'Label';_.tI=50;function Am(){Am=lD;eq(),gq;en=new om();}
function vm(b,a){Am();xk(b,Be(a));ro(b,1024);qo(b,'gwt-ListBox');return b;}
function wm(b,a){Fm(b,a,(-1));}
function xm(b,a,c){an(b,a,c,(-1));}
function ym(b,a){if(a<0||a>=Bm(b)){throw new gv();}}
function zm(a){pm(en,a.w());}
function Bm(a){return rm(en,a.w());}
function Cm(b,a){ym(b,a);return sm(en,b.w(),a);}
function Dm(a){return qf(a.w(),'selectedIndex');}
function Em(b,a){ym(b,a);return tm(en,b.w(),a);}
function Fm(c,b,a){an(c,b,b,a);}
function an(c,b,d,a){vf(c.w(),b,d,a);}
function bn(b,a){ym(b,a);um(en,b.w(),a);}
function cn(c,a,b){ym(c,a);if(b===null){throw sv(new rv(),'Cannot set an option to have null text');}ag(c.w(),b,a);}
function dn(a,b){Bf(a.w(),'size',b);}
function fn(a){if(lf(a)==1024){}else{zk(this,a);}}
function nm(){}
_=nm.prototype=new wk();_.hb=fn;_.tN=xG+'ListBox';_.tI=51;var en;function pm(b,a){a.options.length=0;}
function rm(b,a){return a.options.length;}
function sm(c,b,a){return b.options[a].text;}
function tm(c,b,a){return b.options[a].value;}
function um(c,b,a){b.options[a]=null;}
function om(){}
_=om.prototype=new yv();_.tN=xG+'ListBox$Impl';_.tI=52;function vn(){vn=lD;zn=qB(new uA());}
function un(b,a){vn();Di(b);if(a===null){a=wn();}b.yb(a);b.gb();return b;}
function xn(c){vn();var a,b;b=ge(xB(zn,c),10);if(b!==null){return b;}a=null;if(c!==null){if(null===(a=of(c))){return null;}}if(zn.c==0){yn();}zB(zn,c,b=un(new pn(),a));return b;}
function wn(){vn();return $doc.body;}
function yn(){vn();eh(new qn());}
function pn(){}
_=pn.prototype=new Ci();_.tN=xG+'RootPanel';_.tI=53;var zn;function sn(){var a,b;for(b=zy(iz((vn(),zn)));az(b);){a=ge(bz(b),10);if(a.ab()){a.jb();}}}
function tn(){return null;}
function qn(){}
_=qn.prototype=new yv();_.rb=sn;_.sb=tn;_.tN=xG+'RootPanel$1';_.tI=54;function fo(){fo=lD;eq(),gq;}
function co(b,a){eq(),gq;xk(b,a);ro(b,1024);return b;}
function eo(b,a){if(b.b===null){b.b=Fl(new El());}Az(b.b,a);}
function go(a){return rf(a.w(),'value');}
function ho(a){if(this.a===null){this.a=Fj(new Ej());}Az(this.a,a);}
function io(a){var b;zk(this,a);b=lf(a);if(this.b!==null&&(b&896)!=0){em(this.b,this,a);}else if(b==1){if(this.a!==null){bk(this.a,this);}}else{}}
function bo(){}
_=bo.prototype=new wk();_.k=ho;_.hb=io;_.tN=xG+'TextBoxBase';_.tI=55;_.a=null;_.b=null;function ko(){ko=lD;eq(),gq;}
function jo(a){eq(),gq;co(a,ze());qo(a,'gwt-TextBox');return a;}
function lo(b,a){Bf(b.w(),'size',a);}
function ao(){}
_=ao.prototype=new bo();_.tN=xG+'TextBox';_.tI=56;function wo(a){a.a=(el(),fl);a.b=(ll(),nl);}
function xo(a){nj(a);wo(a);Cf(a.e,'cellSpacing','0');Cf(a.e,'cellPadding','0');return a;}
function yo(b,d){var a,c;c=Fe();a=Ao(b);ue(c,a);ue(b.d,c);gk(b,d,a);}
function Ao(b){var a;a=Ee();pj(b,a,b.a);qj(b,a,b.b);return a;}
function Bo(c){var a,b;b=tf(c.w());a=ik(this,c);if(a){xf(this.d,tf(b));}return a;}
function vo(){}
_=vo.prototype=new mj();_.vb=Bo;_.tN=xG+'VerticalPanel';_.tI=57;function gp(b,a){b.b=a;b.a=be('[Lcom.google.gwt.user.client.ui.Widget;',[130],[14],[4],null);return b;}
function hp(a,b){kp(a,b,a.c);}
function jp(b,c){var a;for(a=0;a<b.c;++a){if(b.a[a]===c){return a;}}return (-1);}
function kp(d,e,a){var b,c;if(a<0||a>d.c){throw new gv();}if(d.c==d.a.a){c=be('[Lcom.google.gwt.user.client.ui.Widget;',[130],[14],[d.a.a*2],null);for(b=0;b<d.a.a;++b){ce(c,b,d.a[b]);}d.a=c;}++d.c;for(b=d.c-1;b>a;--b){ce(d.a,b,d.a[b-1]);}ce(d.a,a,e);}
function lp(a){return Fo(new Eo(),a);}
function mp(c,b){var a;if(b<0||b>=c.c){throw new gv();}--c.c;for(a=b;a<c.c;++a){ce(c.a,a,c.a[a+1]);}ce(c.a,c.c,null);}
function np(b,c){var a;a=jp(b,c);if(a==(-1)){throw new AC();}mp(b,a);}
function Do(){}
_=Do.prototype=new yv();_.tN=xG+'WidgetCollection';_.tI=58;_.a=null;_.b=null;_.c=0;function Fo(b,a){b.b=a;return b;}
function bp(a){return a.a<a.b.c-1;}
function cp(a){if(a.a>=a.b.c){throw new AC();}return a.b.a[++a.a];}
function dp(){return bp(this);}
function ep(){return cp(this);}
function fp(){if(this.a<0||this.a>=this.b.c){throw new dv();}this.b.b.vb(this.b.a[this.a--]);}
function Eo(){}
_=Eo.prototype=new yv();_.F=dp;_.eb=ep;_.tb=fp;_.tN=xG+'WidgetCollection$WidgetIterator';_.tI=59;_.a=(-1);function eq(){eq=lD;fq=Ep(new Cp());gq=fq!==null?dq(new Bp()):fq;}
function dq(a){eq();return a;}
function Bp(){}
_=Bp.prototype=new yv();_.tN=yG+'FocusImpl';_.tI=60;var fq,gq;function Fp(){Fp=lD;eq();}
function Dp(a){aq(a);bq(a);cq(a);}
function Ep(a){Fp();dq(a);Dp(a);return a;}
function aq(b){return function(a){if(this.parentNode.onblur){this.parentNode.onblur(a);}};}
function bq(b){return function(a){if(this.parentNode.onfocus){this.parentNode.onfocus(a);}};}
function cq(a){return function(){this.firstChild.focus();};}
function Cp(){}
_=Cp.prototype=new Bp();_.tN=yG+'FocusImplOld';_.tI=61;function mq(c,a,b){Ev(c,b);return c;}
function lq(){}
_=lq.prototype=new Dv();_.tN=zG+'DOMException';_.tI=62;function xq(){xq=lD;yq=(tt(),eu);}
function zq(a){xq();return ut(yq,a);}
var yq;function nr(b,a){b.a=a;return b;}
function or(a,b){return b;}
function qr(a){if(he(a,26)){return ve(or(this,this.a),or(this,ge(a,26).a));}return false;}
function mr(){}
_=mr.prototype=new yv();_.eQ=qr;_.tN=AG+'DOMItem';_.tI=63;_.a=null;function ls(b,a){nr(b,a);return b;}
function ns(a){return gs(new fs(),wt(a.a));}
function os(a){return ws(new vs(),xt(a.a));}
function ps(a){return Dt(a.a);}
function qs(a){return Ft(a.a);}
function rs(a){return cu(a.a);}
function ss(a){return du(a.a);}
function ts(a){var b;if(a===null){return null;}b=Et(a);switch(b){case 2:return Bq(new Aq(),a);case 4:return br(new ar(),a);case 8:return jr(new ir(),a);case 11:return wr(new vr(),a);case 9:return Ar(new zr(),a);case 1:return Fr(new Er(),a);case 7:return Fs(new Es(),a);case 3:return et(new dt(),a);default:return ls(new ks(),a);}}
function us(){return os(this).bb(0);}
function ks(){}
_=ks.prototype=new mr();_.z=us;_.tN=AG+'NodeImpl';_.tI=64;function Bq(b,a){ls(b,a);return b;}
function Dq(a){return Ct(a.a);}
function Eq(a){return bu(a.a);}
function Fq(){var a;a=cw(new bw());fw(a,' '+Dq(this));fw(a,'="');fw(a,Eq(this));fw(a,'"');return jw(a);}
function Aq(){}
_=Aq.prototype=new ks();_.tS=Fq;_.tN=AG+'AttrImpl';_.tI=65;function fr(b,a){ls(b,a);return b;}
function hr(a){return yt(a.a);}
function er(){}
_=er.prototype=new ks();_.tN=AG+'CharacterDataImpl';_.tI=66;function et(b,a){fr(b,a);return b;}
function gt(){var a,b,c;a=cw(new bw());c=uw(hr(this),'(?=[;&<>\'"])',(-1));for(b=0;b<c.a;b++){if(vw(c[b],';')){fw(a,'&semi;');fw(a,ww(c[b],1));}else if(vw(c[b],'&')){fw(a,'&amp;');fw(a,ww(c[b],1));}else if(vw(c[b],'"')){fw(a,'&quot;');fw(a,ww(c[b],1));}else if(vw(c[b],"'")){fw(a,'&apos;');fw(a,ww(c[b],1));}else if(vw(c[b],'<')){fw(a,'&lt;');fw(a,ww(c[b],1));}else if(vw(c[b],'>')){fw(a,'&gt;');fw(a,ww(c[b],1));}else{fw(a,c[b]);}}return jw(a);}
function dt(){}
_=dt.prototype=new er();_.tS=gt;_.tN=AG+'TextImpl';_.tI=67;function br(b,a){et(b,a);return b;}
function dr(){var a;a=dw(new bw(),'<![CDATA[');fw(a,hr(this));fw(a,']]>');return jw(a);}
function ar(){}
_=ar.prototype=new dt();_.tS=dr;_.tN=AG+'CDATASectionImpl';_.tI=68;function jr(b,a){fr(b,a);return b;}
function lr(){var a;a=dw(new bw(),'<!--');fw(a,hr(this));fw(a,'-->');return jw(a);}
function ir(){}
_=ir.prototype=new er();_.tS=lr;_.tN=AG+'CommentImpl';_.tI=69;function sr(c,a,b){mq(c,12,'Failed to parse: '+ur(a));jx(c,b);return c;}
function ur(a){return xw(a,0,ov(rw(a),128));}
function rr(){}
_=rr.prototype=new lq();_.tN=AG+'DOMParseException';_.tI=70;function wr(b,a){ls(b,a);return b;}
function yr(){var a,b;a=cw(new bw());for(b=0;b<os(this).B();b++){ew(a,os(this).bb(b));}return jw(a);}
function vr(){}
_=vr.prototype=new ks();_.tS=yr;_.tN=AG+'DocumentFragmentImpl';_.tI=71;function Ar(b,a){ls(b,a);return b;}
function Cr(){return ge(ts(zt(this.a)),27);}
function Dr(){var a,b,c;a=cw(new bw());b=os(this);for(c=0;c<b.B();c++){fw(a,b.bb(c).tS());}return jw(a);}
function zr(){}
_=zr.prototype=new ks();_.v=Cr;_.tS=Dr;_.tN=AG+'DocumentImpl';_.tI=72;function Fr(b,a){ls(b,a);return b;}
function bs(a){return au(a.a);}
function cs(a){return vt(this.a,a);}
function ds(a){return ws(new vs(),At(this.a,a));}
function es(){var a;a=dw(new bw(),'<');fw(a,bs(this));if(rs(this)){fw(a,As(ns(this)));}if(ss(this)){fw(a,'>');fw(a,As(os(this)));fw(a,'<\/');fw(a,bs(this));fw(a,'>');}else{fw(a,'/>');}return jw(a);}
function Er(){}
_=Er.prototype=new ks();_.u=cs;_.y=ds;_.tS=es;_.tN=AG+'ElementImpl';_.tI=73;function ws(b,a){nr(b,a);return b;}
function ys(a){return Bt(a.a);}
function zs(b,a){return ts(fu(b.a,a));}
function As(c){var a,b;a=cw(new bw());for(b=0;b<c.B();b++){fw(a,c.bb(b).tS());}return jw(a);}
function Bs(){return ys(this);}
function Cs(a){return zs(this,a);}
function Ds(){return As(this);}
function vs(){}
_=vs.prototype=new mr();_.B=Bs;_.bb=Cs;_.tS=Ds;_.tN=AG+'NodeListImpl';_.tI=74;function gs(b,a){ws(b,a);return b;}
function is(){return ys(this);}
function js(a){return zs(this,a);}
function fs(){}
_=fs.prototype=new vs();_.B=is;_.bb=js;_.tN=AG+'NamedNodeMapImpl';_.tI=75;function Fs(b,a){ls(b,a);return b;}
function bt(a){return yt(a.a);}
function ct(){var a;a=dw(new bw(),'<?');fw(a,ps(this));fw(a,' ');fw(a,bt(this));fw(a,'?>');return jw(a);}
function Es(){}
_=Es.prototype=new ks();_.tS=ct;_.tN=AG+'ProcessingInstructionImpl';_.tI=76;function tt(){tt=lD;eu=jt(new it());}
function st(a){tt();return a;}
function ut(e,c){var a,d;try{return ge(ts(qt(e,c)),28);}catch(a){a=pe(a);if(he(a,29)){d=a;throw sr(new rr(),c,d);}else throw a;}}
function vt(b,a){tt();return b.getAttribute(a);}
function wt(a){tt();return a.attributes;}
function xt(b){tt();var a=b.childNodes;return a==null?null:a;}
function yt(a){tt();return a.data;}
function zt(a){tt();return a.documentElement;}
function At(a,b){tt();return pt(eu,a,b);}
function Bt(a){tt();return a.length;}
function Ct(a){tt();return a.name;}
function Dt(a){tt();var b=a.nodeName;return b==null?null:b;}
function Et(a){tt();var b=a.nodeType;return b==null?-1:b;}
function Ft(a){tt();return a.nodeValue;}
function au(a){tt();return a.tagName;}
function bu(a){tt();return a.value;}
function cu(a){tt();return a.attributes.length!=0;}
function du(a){tt();return a.hasChildNodes();}
function fu(c,a){tt();if(a>=c.length){return null;}var b=c.item(a);return b==null?null:b;}
function ht(){}
_=ht.prototype=new yv();_.tN=AG+'XMLParserImpl';_.tI=77;var eu;function ot(){ot=lD;tt();}
function mt(a){a.a=rt();}
function nt(a){ot();st(a);mt(a);return a;}
function pt(c,a,b){return a.getElementsByTagNameNS('*',b);}
function qt(e,a){var b=e.a;var c=b.parseFromString(a,'text/xml');var d=c.documentElement;if(d.tagName=='parsererror'&&d.namespaceURI=='http://www.mozilla.org/newlayout/xml/parsererror.xml'){throw new Error(d.firstChild.data);}return c;}
function rt(){ot();return new DOMParser();}
function lt(){}
_=lt.prototype=new ht();_.tN=AG+'XMLParserImplStandard';_.tI=78;function kt(){kt=lD;ot();}
function jt(a){kt();nt(a);return a;}
function it(){}
_=it.prototype=new lt();_.tN=AG+'XMLParserImplOpera';_.tI=79;function ju(){}
_=ju.prototype=new yv();_.tN=BG+'OutputStream';_.tI=80;function hu(){}
_=hu.prototype=new ju();_.tN=BG+'FilterOutputStream';_.tI=81;function lu(){}
_=lu.prototype=new hu();_.tN=BG+'PrintStream';_.tI=82;function nu(){}
_=nu.prototype=new Dv();_.tN=CG+'ArrayStoreException';_.tI=83;function ru(){ru=lD;su=qu(new pu(),false);tu=qu(new pu(),true);}
function qu(a,b){ru();a.a=b;return a;}
function uu(a){return he(a,30)&&ge(a,30).a==this.a;}
function vu(){var a,b;b=1231;a=1237;return this.a?1231:1237;}
function wu(){return this.a?'true':'false';}
function xu(a){ru();return a?tu:su;}
function pu(){}
_=pu.prototype=new yv();_.eQ=uu;_.hC=vu;_.tS=wu;_.tN=CG+'Boolean';_.tI=84;_.a=false;var su,tu;function zu(){}
_=zu.prototype=new Dv();_.tN=CG+'ClassCastException';_.tI=85;function bv(b,a){Ev(b,a);return b;}
function av(){}
_=av.prototype=new Dv();_.tN=CG+'IllegalArgumentException';_.tI=86;function ev(b,a){Ev(b,a);return b;}
function dv(){}
_=dv.prototype=new Dv();_.tN=CG+'IllegalStateException';_.tI=87;function hv(b,a){Ev(b,a);return b;}
function gv(){}
_=gv.prototype=new Dv();_.tN=CG+'IndexOutOfBoundsException';_.tI=88;function vv(){vv=lD;{xv();}}
function xv(){vv();wv=/^[+-]?\d*\.?\d*(e[+-]?\d+)?$/i;}
var wv=null;function kv(){kv=lD;vv();}
function lv(a){kv();return ax(a);}
function ov(a,b){return a<b?a:b;}
function pv(){}
_=pv.prototype=new Dv();_.tN=CG+'NegativeArraySizeException';_.tI=89;function sv(b,a){Ev(b,a);return b;}
function rv(){}
_=rv.prototype=new Dv();_.tN=CG+'NullPointerException';_.tI=90;function mw(b,a){return b.charCodeAt(a);}
function ow(b,a){if(!he(a,1))return false;return Aw(b,a);}
function pw(b,a){return b.indexOf(a);}
function qw(c,b,a){return c.indexOf(b,a);}
function rw(a){return a.length;}
function sw(c,a,b){b=Bw(b);return c.replace(RegExp(a,'g'),b);}
function tw(b,a){return uw(b,a,0);}
function uw(j,i,g){var a=new RegExp(i,'g');var h=[];var b=0;var k=j;var e=null;while(true){var f=a.exec(k);if(f==null||(k==''||b==g-1&&g>0)){h[b]=k;break;}else{h[b]=k.substring(0,f.index);k=k.substring(f.index+f[0].length,k.length);a.lastIndex=0;if(e==k){h[b]=k.substring(0,1);k=k.substring(1);}e=k;b++;}}if(g==0){for(var c=h.length-1;c>=0;c--){if(h[c]!=''){h.splice(c+1,h.length-(c+1));break;}}}var d=zw(h.length);var c=0;for(c=0;c<h.length;++c){d[c]=h[c];}return d;}
function vw(b,a){return pw(b,a)==0;}
function ww(b,a){return b.substr(a,b.length-a);}
function xw(c,a,b){return c.substr(a,b-a);}
function yw(c){var a=c.replace(/^(\s*)/,'');var b=a.replace(/\s*$/,'');return b;}
function zw(a){return be('[Ljava.lang.String;',[124],[1],[a],null);}
function Aw(a,b){return String(a)==b;}
function Bw(b){var a;a=0;while(0<=(a=qw(b,'\\',a))){if(mw(b,a+1)==36){b=xw(b,0,a)+'$'+ww(b,++a);}else{b=xw(b,0,a)+ww(b,++a);}}return b;}
function Cw(a){return ow(this,a);}
function Ew(){var a=Dw;if(!a){a=Dw={};}var e=':'+this;var b=a[e];if(b==null){b=0;var f=this.length;var d=f<64?1:f/32|0;for(var c=0;c<f;c+=d){b<<=1;b+=this.charCodeAt(c);}b|=0;a[e]=b;}return b;}
function Fw(){return this;}
function ax(a){return ''+a;}
function bx(a){return a!==null?a.tS():'null';}
_=String.prototype;_.eQ=Cw;_.hC=Ew;_.tS=Fw;_.tN=CG+'String';_.tI=2;var Dw=null;function cw(a){gw(a);return a;}
function dw(b,a){hw(b,a);return b;}
function ew(a,b){return fw(a,bx(b));}
function fw(c,d){if(d===null){d='null';}var a=c.js.length-1;var b=c.js[a].length;if(c.length>b*b){c.js[a]=c.js[a]+d;}else{c.js.push(d);}c.length+=d.length;return c;}
function gw(a){hw(a,'');}
function hw(b,a){b.js=[a];b.length=a.length;}
function jw(a){a.fb();return a.js[0];}
function kw(){if(this.js.length>1){this.js=[this.js.join('')];this.length=this.js[0].length;}}
function lw(){return jw(this);}
function bw(){}
_=bw.prototype=new yv();_.fb=kw;_.tS=lw;_.tN=CG+'StringBuffer';_.tI=91;function dx(){dx=lD;ex=new lu();}
function fx(a){dx();return C(a);}
var ex;function ox(b,a){Ev(b,a);return b;}
function nx(){}
_=nx.prototype=new Dv();_.tN=CG+'UnsupportedOperationException';_.tI=92;function yx(b,a){b.c=a;return b;}
function Ax(a){return a.a<a.c.Bb();}
function Bx(){return Ax(this);}
function Cx(){if(!Ax(this)){throw new AC();}return this.c.D(this.b=this.a++);}
function Dx(){if(this.b<0){throw new dv();}this.c.ub(this.b);this.a=this.b;this.b=(-1);}
function xx(){}
_=xx.prototype=new yv();_.F=Bx;_.eb=Cx;_.tb=Dx;_.tN=DG+'AbstractList$IteratorImpl';_.tI=93;_.a=0;_.b=(-1);function gz(f,d,e){var a,b,c;for(b=lB(f.r());dB(b);){a=eB(b);c=a.A();if(d===null?c===null:d.eQ(c)){if(e){fB(b);}return a;}}return null;}
function hz(b){var a;a=b.r();return iy(new hy(),b,a);}
function iz(b){var a;a=wB(b);return xy(new wy(),b,a);}
function jz(a){return gz(this,a,false)!==null;}
function kz(d){var a,b,c,e,f,g,h;if(d===this){return true;}if(!he(d,32)){return false;}f=ge(d,32);c=hz(this);e=f.db();if(!rz(c,e)){return false;}for(a=ky(c);ry(a);){b=sy(a);h=this.E(b);g=f.E(b);if(h===null?g!==null:!h.eQ(g)){return false;}}return true;}
function lz(b){var a;a=gz(this,b,false);return a===null?null:a.C();}
function mz(){var a,b,c;b=0;for(c=lB(this.r());dB(c);){a=eB(c);b+=a.hC();}return b;}
function nz(){return hz(this);}
function oz(){var a,b,c,d;d='{';a=false;for(c=lB(this.r());dB(c);){b=eB(c);if(a){d+=', ';}else{a=true;}d+=bx(b.A());d+='=';d+=bx(b.C());}return d+'}';}
function gy(){}
_=gy.prototype=new yv();_.n=jz;_.eQ=kz;_.E=lz;_.hC=mz;_.db=nz;_.tS=oz;_.tN=DG+'AbstractMap';_.tI=94;function rz(e,b){var a,c,d;if(b===e){return true;}if(!he(b,33)){return false;}c=ge(b,33);if(c.Bb()!=e.Bb()){return false;}for(a=c.cb();a.F();){d=a.eb();if(!e.o(d)){return false;}}return true;}
function sz(a){return rz(this,a);}
function tz(){var a,b,c;a=0;for(b=this.cb();b.F();){c=b.eb();if(c!==null){a+=c.hC();}}return a;}
function pz(){}
_=pz.prototype=new qx();_.eQ=sz;_.hC=tz;_.tN=DG+'AbstractSet';_.tI=95;function iy(b,a,c){b.a=a;b.b=c;return b;}
function ky(b){var a;a=lB(b.b);return py(new oy(),b,a);}
function ly(a){return this.a.n(a);}
function my(){return ky(this);}
function ny(){return this.b.a.c;}
function hy(){}
_=hy.prototype=new pz();_.o=ly;_.cb=my;_.Bb=ny;_.tN=DG+'AbstractMap$1';_.tI=96;function py(b,a,c){b.a=c;return b;}
function ry(a){return a.a.F();}
function sy(b){var a;a=b.a.eb();return a.A();}
function ty(){return ry(this);}
function uy(){return sy(this);}
function vy(){this.a.tb();}
function oy(){}
_=oy.prototype=new yv();_.F=ty;_.eb=uy;_.tb=vy;_.tN=DG+'AbstractMap$2';_.tI=97;function xy(b,a,c){b.a=a;b.b=c;return b;}
function zy(b){var a;a=lB(b.b);return Ey(new Dy(),b,a);}
function Ay(a){return vB(this.a,a);}
function By(){return zy(this);}
function Cy(){return this.b.a.c;}
function wy(){}
_=wy.prototype=new qx();_.o=Ay;_.cb=By;_.Bb=Cy;_.tN=DG+'AbstractMap$3';_.tI=98;function Ey(b,a,c){b.a=c;return b;}
function az(a){return a.a.F();}
function bz(a){var b;b=a.a.eb().C();return b;}
function cz(){return az(this);}
function dz(){return bz(this);}
function ez(){this.a.tb();}
function Dy(){}
_=Dy.prototype=new yv();_.F=cz;_.eb=dz;_.tb=ez;_.tN=DG+'AbstractMap$4';_.tI=99;function rA(b){var a,c;a=wz(new uz());for(c=0;c<b.a;c++){Az(a,b[c]);}return a;}
function tB(){tB=lD;BB=bC();}
function pB(a){{sB(a);}}
function qB(a){tB();pB(a);return a;}
function rB(a,b){tB();pB(a);yB(a,b);return a;}
function sB(a){a.a=hb();a.d=jb();a.b=me(BB,db);a.c=0;}
function uB(b,a){if(he(a,1)){return fC(b.d,ge(a,1))!==BB;}else if(a===null){return b.b!==BB;}else{return eC(b.a,a,a.hC())!==BB;}}
function vB(a,b){if(a.b!==BB&&dC(a.b,b)){return true;}else if(aC(a.d,b)){return true;}else if(EB(a.a,b)){return true;}return false;}
function wB(a){return jB(new FA(),a);}
function xB(c,a){var b;if(he(a,1)){b=fC(c.d,ge(a,1));}else if(a===null){b=c.b;}else{b=eC(c.a,a,a.hC());}return b===BB?null:b;}
function zB(c,a,d){var b;if(he(a,1)){b=iC(c.d,ge(a,1),d);}else if(a===null){b=c.b;c.b=d;}else{b=hC(c.a,a,d,a.hC());}if(b===BB){++c.c;return null;}else{return b;}}
function yB(d,c){var a,b;b=lB(wB(c));while(dB(b)){a=eB(b);zB(d,a.A(),a.C());}}
function AB(c,a){var b;if(he(a,1)){b=kC(c.d,ge(a,1));}else if(a===null){b=c.b;c.b=me(BB,db);}else{b=jC(c.a,a,a.hC());}if(b===BB){return null;}else{--c.c;return b;}}
function CB(e,c){tB();for(var d in e){if(d==parseInt(d)){var a=e[d];for(var f=0,b=a.length;f<b;++f){c.m(a[f]);}}}}
function DB(d,a){tB();for(var c in d){if(c.charCodeAt(0)==58){var e=d[c];var b=yA(c.substring(1),e);a.m(b);}}}
function EB(f,h){tB();for(var e in f){if(e==parseInt(e)){var a=f[e];for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.C();if(dC(h,d)){return true;}}}}return false;}
function FB(a){return uB(this,a);}
function aC(c,d){tB();for(var b in c){if(b.charCodeAt(0)==58){var a=c[b];if(dC(d,a)){return true;}}}return false;}
function bC(){tB();}
function cC(){return wB(this);}
function dC(a,b){tB();if(a===b){return true;}else if(a===null){return false;}else{return a.eQ(b);}}
function gC(a){return xB(this,a);}
function eC(f,h,e){tB();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(dC(h,d)){return c.C();}}}}
function fC(b,a){tB();return b[':'+a];}
function hC(f,h,j,e){tB();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(dC(h,d)){var i=c.C();c.Ab(j);return i;}}}else{a=f[e]=[];}var c=yA(h,j);a.push(c);}
function iC(c,a,d){tB();a=':'+a;var b=c[a];c[a]=d;return b;}
function jC(f,h,e){tB();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.A();if(dC(h,d)){if(a.length==1){delete f[e];}else{a.splice(g,1);}return c.C();}}}}
function kC(c,a){tB();a=':'+a;var b=c[a];delete c[a];return b;}
function uA(){}
_=uA.prototype=new gy();_.n=FB;_.r=cC;_.E=gC;_.tN=DG+'HashMap';_.tI=100;_.a=null;_.b=null;_.c=0;_.d=null;var BB;function wA(b,a,c){b.a=a;b.b=c;return b;}
function yA(a,b){return wA(new vA(),a,b);}
function zA(b){var a;if(he(b,39)){a=ge(b,39);if(dC(this.a,a.A())&&dC(this.b,a.C())){return true;}}return false;}
function AA(){return this.a;}
function BA(){return this.b;}
function CA(){var a,b;a=0;b=0;if(this.a!==null){a=this.a.hC();}if(this.b!==null){b=this.b.hC();}return a^b;}
function DA(a){var b;b=this.b;this.b=a;return b;}
function EA(){return this.a+'='+this.b;}
function vA(){}
_=vA.prototype=new yv();_.eQ=zA;_.A=AA;_.C=BA;_.hC=CA;_.Ab=DA;_.tS=EA;_.tN=DG+'HashMap$EntryImpl';_.tI=101;_.a=null;_.b=null;function jB(b,a){b.a=a;return b;}
function lB(a){return bB(new aB(),a.a);}
function mB(c){var a,b,d;if(he(c,39)){a=ge(c,39);b=a.A();if(uB(this.a,b)){d=xB(this.a,b);return dC(a.C(),d);}}return false;}
function nB(){return lB(this);}
function oB(){return this.a.c;}
function FA(){}
_=FA.prototype=new pz();_.o=mB;_.cb=nB;_.Bb=oB;_.tN=DG+'HashMap$EntrySet';_.tI=102;function bB(c,b){var a;c.c=b;a=wz(new uz());if(c.c.b!==(tB(),BB)){Az(a,wA(new vA(),null,c.c.b));}DB(c.c.d,a);CB(c.c.a,a);c.a=a.cb();return c;}
function dB(a){return a.a.F();}
function eB(a){return a.b=ge(a.a.eb(),39);}
function fB(a){if(a.b===null){throw ev(new dv(),'Must call next() before remove().');}else{a.a.tb();AB(a.c,a.b.A());a.b=null;}}
function gB(){return dB(this);}
function hB(){return eB(this);}
function iB(){fB(this);}
function aB(){}
_=aB.prototype=new yv();_.F=gB;_.eb=hB;_.tb=iB;_.tN=DG+'HashMap$EntrySetIterator';_.tI=103;_.a=null;_.b=null;function mC(a){a.a=qB(new uA());return a;}
function oC(a){var b;b=zB(this.a,a,xu(true));return b===null;}
function pC(a){return uB(this.a,a);}
function qC(){return ky(hz(this.a));}
function rC(){return this.a.c;}
function sC(){return hz(this.a).tS();}
function lC(){}
_=lC.prototype=new pz();_.m=oC;_.o=pC;_.cb=qC;_.Bb=rC;_.tS=sC;_.tN=DG+'HashSet';_.tI=104;_.a=null;function yC(d,c,a,b){Ev(d,c);return d;}
function xC(){}
_=xC.prototype=new Dv();_.tN=DG+'MissingResourceException';_.tI=105;function AC(){}
_=AC.prototype=new Dv();_.tN=DG+'NoSuchElementException';_.tI=106;function FC(a){a.a=wz(new uz());return a;}
function aD(b,a){return Az(b.a,a);}
function cD(b,a){return dD(b,a);}
function dD(b,a){return Ez(b.a,a);}
function eD(a,b){zz(this.a,a,b);}
function fD(a){return aD(this,a);}
function gD(a){return Dz(this.a,a);}
function hD(a){return dD(this,a);}
function iD(){return this.a.cb();}
function jD(a){return bA(this.a,a);}
function kD(){return this.a.b;}
function EC(){}
_=EC.prototype=new wx();_.l=eD;_.m=fD;_.o=gD;_.D=hD;_.cb=iD;_.ub=jD;_.Bb=kD;_.tN=DG+'Vector';_.tI=107;_.a=null;function dE(g,h){var a,c,d,e,f;if(g.c===null||g.b===null||g.a===null){h=v()+sw(h,'&amp;','&');c=oE(new mE(),h);try{e=pG(c);f=BD(new AD(),g,e,c);zg(f,1);}catch(a){a=pe(a);if(he(a,41)){d=a;kx(d);}else throw a;}}}
function eE(g,h){var a,c,d,e,f;h=v()+sw(h,'&amp;','&');c=yE(new wE(),h);try{e=pG(c);f=FD(new ED(),g,e,c);zg(f,1);}catch(a){a=pe(a);if(he(a,41)){d=a;fh('Exception: '+d.b);kx(d);}else throw a;}}
function fE(q){var a,c,d,e,f,g,h,i,j,k,l,m,n,o,p,r;k='DEFAULT-identities-and-usecases.xml';l='DEFAULT-policy.xml';f='DEFAULT-cancel.html';m='DEFAULT-save-policy.xml';try{h=ud('getURLs');k=rd(h,'identities-url');l=rd(h,'policy-url');f=rd(h,'cancel-url');m=rd(h,'save-url');}catch(a){a=pe(a);if(he(a,40)){i=a;fh('Exception: '+i.b);}else throw a;}dE(q,k);q.d=kF(new iF(),q.j);mF(q.d,q.j,q.c,q.b);q.f=qF(new oF(),q.j,q.g,q.e,q.i);eE(q,l);r=xo(new vo());Ei(xn('access-policy-editor-hook'),r);o=xo(new vo());yo(r,o);p=jo(new ao());lo(p,30);yo(o,p);eo(p,oD(new nD(),q,p));j=sl(new ql());wl(j,(ll(),ml));yo(r,j);d=sl(new ql());yo(r,d);n=v()+sw(m,'&amp;','&');q.h=jj(new cj(),'Save Policy',sD(new rD(),q,n));qo(q.h,'gwt-wyona-SaveButton');tl(d,q.h);g=f;e=jj(new cj(),'Cancel',wD(new vD(),q,g));qo(q.h,'gwt-wyona-CancelButton');tl(d,e);c=iE(new gE(),q.d.a,q.f.c,q.f);qo(c,'gwt-wyona-AddRemoveWidget');tl(j,q.d);tl(j,c);tl(j,q.f);}
function mD(){}
_=mD.prototype=new yv();_.tN=EG+'AccessPolicyEditor';_.tI=108;_.a=null;_.b=null;_.c=null;_.d=null;_.e=null;_.f=null;_.g=null;_.h=null;_.i=true;_.j=10;function oD(b,a,c){b.a=a;b.b=c;return b;}
function qD(h,f,g){var a,b,c,d,e,i,j,k,l;b=this.a.d.a;k=wz(new uz());j=wz(new uz());zm(b);d=this.a.c.a;for(a=0;a<d;a++){e=this.a.c[a];if(pw(e,go(this.b))>=0){Az(k,e);}}c=this.a.b.a;for(a=0;a<c;a++){e=this.a.b[a];if(pw(e,go(this.b))>=0){Az(j,e);}}l=be('[Ljava.lang.String;',[124],[1],[k.b],null);dA(k,l);i=be('[Ljava.lang.String;',[124],[1],[j.b],null);dA(j,i);mF(this.a.d,this.a.j,l,i);}
function nD(){}
_=nD.prototype=new zl();_.nb=qD;_.tN=EG+'AccessPolicyEditor$1';_.tI=109;function sD(b,a,c){b.a=a;b.b=c;return b;}
function uD(f){var a,c,d,e;c=aF(new FE(),this.b);try{e=cF(c,BF(this.a.f),uF(this.a.f),AF(this.a.f));}catch(a){a=pe(a);if(he(a,41)){d=a;fh('Exception: '+d.b);}else throw a;}}
function rD(){}
_=rD.prototype=new yv();_.ib=uD;_.tN=EG+'AccessPolicyEditor$2';_.tI=110;function wD(b,a,c){b.a=c;return b;}
function yD(a,b){$wnd.location.href=b;}
function zD(a){yD(this,v()+this.a);}
function vD(){}
_=vD.prototype=new yv();_.ib=zD;_.tN=EG+'AccessPolicyEditor$3';_.tI=111;function CD(){CD=lD;wg();}
function BD(b,a,d,c){CD();b.a=a;b.c=d;b.b=c;ug(b);return b;}
function DD(){if(yc(this.c)){yg(this,10);}else{this.a.a=sE(this.b);this.a.c=tE(this.b);this.a.b=rE(this.b);vg(this);if(this.a.a.a>0||this.a.c.a>0||this.a.b.a>0){bG(this.a.f,this.a.a);mF(this.a.d,this.a.j,this.a.c,this.a.b);}else{fh('No Identities have been loaded!');}}}
function AD(){}
_=AD.prototype=new pg();_.xb=DD;_.tN=EG+'AccessPolicyEditor$4';_.tI=112;function aE(){aE=lD;wg();}
function FD(b,a,d,c){aE();b.a=a;b.c=d;b.b=c;ug(b);return b;}
function bE(){var a,b,c,d,e,f,g,h;if(yc(this.c)){yg(this,10);}else{this.a.g=CE(this.b);this.a.e=BE(this.b);DF(this.a.f,this.a.j,this.a.g,this.a.e);this.a.i=this.b.b;EF(this.a.f,this.a.i);vg(this);g=xz(new uz(),rA(this.a.c));f=xz(new uz(),rA(this.a.b));c=this.a.g.a;for(a=0;a<c;a++){d=this.a.g[a].a;cA(g,d);}b=this.a.e.a;for(a=0;a<b;a++){d=this.a.e[a].a;cA(f,d);}h=be('[Ljava.lang.String;',[124],[1],[g.b],null);dA(g,h);this.a.c=h;e=be('[Ljava.lang.String;',[124],[1],[f.b],null);dA(f,e);this.a.b=e;mF(this.a.d,this.a.j,be('[Ljava.lang.String;',[124],[1],[0],null),be('[Ljava.lang.String;',[124],[1],[0],null));mF(this.a.d,this.a.j,this.a.c,this.a.b);}}
function ED(){}
_=ED.prototype=new pg();_.xb=bE;_.tN=EG+'AccessPolicyEditor$5';_.tI=113;function hE(a){a.b=tk(new sk());}
function iE(d,a,c,b){hE(d);nk(d,d.b);d.f=jj(new cj(),'<',d);uk(d.b,d.f);d.a=jj(new cj(),'>',d);uk(d.b,d.a);d.c=a;d.d=c;d.e=b;return d;}
function kE(b,a){if(pw(a,'(')>0){return xw(a,0,pw(a,'('));}else{return a;}}
function lE(c){var a,b;if(c===this.a){a=Dm(this.c);if(a>=0){b=Em(this.c,a);bn(this.c,a);rF(this.e,xw(b,0,1),yw(ww(b,2)));}else{fh('No identity selected yet! Please select an identity.');}}else if(c===this.f){a=Dm(this.d);if(a>=0){b=Em(this.d,a);bn(this.d,a);wm(this.c,kE(this,b));}else{fh('No identity selected yet! Please select an identity.');}}}
function gE(){}
_=gE.prototype=new lk();_.ib=lE;_.tN=EG+'AddRemoveIdentitiesWidget';_.tI=114;_.a=null;_.c=null;_.d=null;_.e=null;_.f=null;function lG(a){a.d=qB(new uA());}
function mG(a,b){lG(a);a.e=Eb(new zb(),(ac(),ec),b);qG(a);return a;}
function nG(e){var a,b,c,d;b='';a=rB(new uA(),e.d);for(d=lB(wB(a));dB(d);){c=eB(d);b+=c.A()+''+c.C();if(dB(d)){b+='&';}}return b;}
function pG(a){return bc(a.e,nG(a),a);}
function qG(a){cc(a.e,'Content-Type','application/x-www-form-urlencoded');}
function kG(){}
_=kG.prototype=new yv();_.tN=FG+'AsynchronousAgent';_.tI=115;_.e=null;function nE(a){a.c=FC(new EC());a.a=FC(new EC());a.b=FC(new EC());}
function oE(a,b){mG(a,b);nE(a);return a;}
function qE(d,c,a){var b;b=c.y(a);return ge(b.bb(0),27);}
function rE(c){var a,b;a=be('[Ljava.lang.String;',[124],[1],[c.a.a.b],null);for(b=0;b<c.a.a.b;b++){a[b]=ge(cD(c.a,b),1);}return a;}
function sE(c){var a,b;b=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[127],[36],[c.b.a.b],null);for(a=0;a<c.b.a.b;a++){b[a]=ge(cD(c.b,a),36);}return b;}
function tE(b){var a,c;c=be('[Ljava.lang.String;',[124],[1],[b.c.a.b],null);for(a=0;a<b.c.a.b;a++){c[a]=ge(cD(b.c,a),1);}return c;}
function uE(a,b){}
function vE(e,f){var a,b,c,d,g,h,i,j,k;i=zq(ub(f)).v();k=qE(this,i,'users');j=k.y('user');for(c=0;c<j.B();c++){aD(this.c,ge(j.bb(c),27).u('id'));}b=qE(this,i,'groups');a=b.y('group');for(c=0;c<a.B();c++){aD(this.a,ge(a.bb(c),27).u('id'));}h=qE(this,i,'rights');g=h.y('right');for(c=0;c<g.B();c++){d=qs(ge(g.bb(c),27).z());aD(this.b,eG(new dG(),ge(g.bb(c),27).u('id'),d));}}
function mE(){}
_=mE.prototype=new kG();_.kb=uE;_.pb=vE;_.tN=EG+'AsynchronousIdentitiesAndRightsGetter';_.tI=116;function xE(a){a.c=FC(new EC());a.a=FC(new EC());}
function yE(a,b){mG(a,b);xE(a);return a;}
function AE(d,c,a){var b;b=c.y(a);if(b.B()>0){return ge(b.bb(0),27);}else{return null;}}
function BE(c){var a,b;b=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[126],[35],[c.a.a.b],null);for(a=0;a<b.a;a++){b[a]=ge(cD(c.a,a),35);}return b;}
function CE(c){var a,b;b=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[125],[34],[c.c.a.b],null);for(a=0;a<b.a;a++){b[a]=ge(cD(c.c,a),34);}return b;}
function DE(a,b){}
function EE(e,f){var a,b,c,d,g,h,i,j,k,l,m,n;j=zq(ub(f)).v();k=j.u('use-inherited-policies');if(k===null){this.b=true;}else{if(ow(k,'false')){this.b=false;}else{this.b=true;}}n=AE(this,j,'world');m=j.y('user');for(c=0;c<m.B();c++){l=ge(m.bb(c),27);h=l.y('right');i=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[127],[36],[h.B()],null);for(d=0;d<i.a;d++){g=ge(h.bb(d),27);i[d]=fG(new dG(),g.u('id'),true);}aD(this.c,iG(new hG(),l.u('id'),i));}b=j.y('group');for(c=0;c<b.B();c++){a=ge(b.bb(c),27);h=a.y('right');i=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[127],[36],[h.B()],null);for(d=0;d<i.a;d++){g=ge(h.bb(d),27);i[d]=fG(new dG(),g.u('id'),true);}aD(this.a,gF(new fF(),a.u('id'),i));}}
function wE(){}
_=wE.prototype=new kG();_.kb=DE;_.pb=EE;_.tN=EG+'AsynchronousPolicyGetter';_.tI=117;_.b=true;function aF(a,b){a.a=Eb(new zb(),(ac(),fc),b);return a;}
function cF(f,h,b,g){var a,c,d,e;a=dw(new bw(),'<?xml version="1.0"?>');fw(a,'<policy xmlns="http://www.wyona.org/security/1.0" use-inherited-policies="'+g+'">');if(h!==null){for(c=0;c<h.a;c++){fw(a,'<user id="'+h[c].a+'">');e=h[c].b;if(e!==null){for(d=0;d<e.a;d++){fw(a,'<right id="'+e[d].a+'" permission="'+e[d].c+'">'+e[d].a+'<\/right>');}}else{fw(a,'<right id="r" permission="false">r<\/right>');fw(a,'<right id="w" permission="false">w<\/right>');}fw(a,'<\/user>');}}if(b!==null){for(c=0;c<b.a;c++){fw(a,'<group id="'+b[c].a+'">');e=b[c].b;if(e!==null){for(d=0;d<e.a;d++){fw(a,'<right id="'+e[d].a+'" permission="'+e[d].c+'">'+e[d].a+'<\/right>');}}else{fw(a,'<right id="r" permission="false">r<\/right>');fw(a,'<right id="w" permission="false">w<\/right>');}fw(a,'<\/group>');}}fw(a,'<\/policy>');return bc(f.a,jw(a),f);}
function dF(b,a){fh('Exception: '+a.b);}
function eF(a,b){if(tb(b)==200){fh('Policy has been saved successfully!');}else{fh('Policy has NOT been saved! Please check log files on server.');}}
function FE(){}
_=FE.prototype=new yv();_.kb=dF;_.pb=eF;_.tN=EG+'AsynchronousPolicySetter';_.tI=118;_.a=null;function gF(c,a,b){c.a=a;c.b=b;return c;}
function fF(){}
_=fF.prototype=new yv();_.tN=EG+'Group';_.tI=119;_.a=null;_.b=null;function jF(a){a.b=xo(new vo());}
function kF(a,b){jF(a);nk(a,a.b);yo(a.b,jm(new hm(),'Identities'));a.a=vm(new nm(),true);a.a.k(a);mF(a,b,null,null);yo(a.b,a.a);return a;}
function mF(c,e,d,a){var b;zm(c.a);dn(c.a,e);if(d!==null){for(b=0;b<d.a;b++){wm(c.a,'u: '+d[b]);}}else{wm(c.a,'No users yet!');}if(a!==null){for(b=0;b<a.a;b++){wm(c.a,'g: '+a[b]);}}else{wm(c.a,'No groups yet!');}}
function nF(a){}
function iF(){}
_=iF.prototype=new lk();_.ib=nF;_.tN=EG+'IdentitiesListBoxWidget';_.tI=120;_.a=null;function pF(a){a.e=xo(new vo());}
function qF(b,e,d,a,c){pF(b);nk(b,b.e);yo(b.e,jm(new hm(),'Policy'));b.d=uj(new rj(),'Inherit rights from parent policies');EF(b,c);yo(b.e,b.d);b.c=vm(new nm(),true);b.c.k(b);DF(b,e,d,a);yo(b.e,b.c);bG(b,null);return b;}
function rF(d,e,c){var a,b;a=dw(new bw(),'(-');for(b=1;b<d.b.a;b++){fw(a,',-');}fw(a,')');xm(d.c,e+': '+a+' '+c,e+': '+c);}
function sF(e,a,d){var b,c;c=be('[Ljava.lang.String;',[124],[1],[e.a.a],null);for(b=0;b<c.a;b++){if(ow(e.a[b].a,d.a)){c[b]=d.a;}else{if(a[b].c){c[b]=a[b].a;}else{c[b]='-';}}}return c;}
function uF(g){var a,b,c,d,e,f;b=FC(new EC());for(c=0;c<Bm(g.c);c++){e=Cm(g.c,c);f=yF(g,e);d=vF(g,c);if(vw(d,'g:')){aD(b,gF(new fF(),yw(ww(d,2)),f));}}a=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Group;',[126],[35],[b.a.b],null);for(c=0;c<a.a;c++){a[c]=ge(cD(b,c),35);}return a;}
function vF(b,a){return Em(b.c,a);}
function wF(e,f,b,c){var a,d;d=dw(new bw(),f+':');fw(d,'('+c[0]);for(a=1;a<c.a;a++){fw(d,','+c[a]);}fw(d,')');fw(d,' '+b);return jw(d);}
function xF(g,h,b,e){var a,c,d,f;f=dw(new bw(),h+':');if(g.a!==null){fw(f,'(');for(a=0;a<g.a.a;a++){d=false;for(c=0;c<e.a;c++){if(ow(g.a[a].a,e[c].a)&&e[c].c){d=true;break;}}if(a>0){fw(f,',');}if(d){fw(f,g.a[a].a);}else{fw(f,'-');}}fw(f,')');}else{fh('Available rights not loaded yet!');}fw(f,' '+b);return jw(f);}
function yF(e,b){var a,c,d;if(pw(b,'(')>0){d=tw(xw(b,pw(b,'(')+1,pw(b,')')),',');if(d.a!=e.a.a){fh('Exception: Validation of rights length failed!');return null;}c=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[127],[36],[e.a.a],null);for(a=0;a<d.a;a++){if(ow(d[a],'-')){c[a]=fG(new dG(),e.a[a].a,false);}else{c[a]=fG(new dG(),d[a],true);}}return c;}else{return be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.Right;',[127],[36],[0],null);}}
function zF(b){var a;a=Dm(b.c);if(a>=0){return Cm(b.c,a);}return null;}
function AF(a){return wj(a.d);}
function BF(e){var a,b,c,d,f,g;g=FC(new EC());for(a=0;a<Bm(e.c);a++){c=Cm(e.c,a);d=yF(e,c);b=vF(e,a);if(vw(b,'u:')){aD(g,iG(new hG(),yw(ww(b,2)),d));}}f=be('[Lorg.wyona.security.gwt.accesspolicyeditor.client.User;',[125],[34],[g.a.b],null);for(a=0;a<f.a;a++){f[a]=ge(cD(g,a),34);}return f;}
function CF(e,a,d){var b,c;c=be('[Ljava.lang.String;',[124],[1],[e.a.a],null);for(b=0;b<c.a;b++){if(ow(e.a[b].a,d.a)){c[b]='-';}else{if(a[b].c){c[b]=a[b].a;}else{c[b]='-';}}}return c;}
function bG(c,a){var b;c.a=a;if(a!==null){c.b=be('[Lcom.google.gwt.user.client.ui.CheckBox;',[129],[13],[a.a],null);for(b=0;b<c.b.a;b++){c.b[b]=uj(new rj(),a[b].b);yj(c.b[b],a[b].a);c.b[b].k(c);yo(c.e,c.b[b]);}}else{}}
function DF(e,i,g,a){var b,c,d,f,h;zm(e.c);dn(e.c,i);if(g!==null||a!==null){if(g!==null){for(b=0;b<g.a;b++){f='u';c=g[b].a;d=g[b].b;h=f+': '+c;xm(e.c,xF(e,f,c,d),h);}}if(a!==null){for(b=0;b<a.a;b++){f='g';c=a[b].a;d=a[b].b;h=f+': '+c;xm(e.c,xF(e,f,c,d),h);}}else{fh('No groups!');}}else{wm(e.c,'No identities yet!');}}
function EF(a,b){if(a.d!==null){xj(a.d,b);}}
function FF(d,e,a,c,b){cn(d.c,b,wF(d,e,a,c));}
function aG(d,c){var a,b;b=Dm(d.c);if(b>=0){a=vF(d,b);FF(d,xw(a,0,1),yw(ww(a,2)),c,b);}else{fh('Exception: No list item selected!');}}
function cG(i){var a,b,c,d,e,f,g,h;h=null;g=null;for(b=0;b<this.b.a;b++){if(i===this.b[b]){h=this.b[b];g=this.a[b];break;}}if(h!==null){f=zF(this);if(f!==null){a=yF(this,f);if(wj(h)){d=sF(this,a,g);}else{d=CF(this,a,g);}aG(this,d);}else{fh('No identity has been selected! Please select an identity in order to assign rights.');xj(h,false);}}else if(i===this.c){f=zF(this);e=yF(this,f);for(c=0;c<this.b.a;c++){if(e[c].c){xj(this.b[c],true);}else{xj(this.b[c],false);}}}}
function oF(){}
_=oF.prototype=new lk();_.ib=cG;_.tN=EG+'PolicyListBoxWidget';_.tI=121;_.a=null;_.b=null;_.c=null;_.d=null;function fG(c,a,b){c.a=a;c.c=b;return c;}
function eG(c,a,b){c.a=a;c.b=b;c.c=false;return c;}
function dG(){}
_=dG.prototype=new yv();_.tN=EG+'Right';_.tI=122;_.a=null;_.b=null;_.c=false;function iG(c,a,b){c.a=a;c.b=b;return c;}
function hG(){}
_=hG.prototype=new yv();_.tN=EG+'User';_.tI=123;_.a=null;_.b=null;function gu(){fE(new mD());}
function gwtOnLoad(b,d,c){$moduleName=d;$moduleBase=c;if(b)try{gu();}catch(a){b(d);}else{gu();}}
var le=[{},{12:1},{1:1,12:1,37:1,38:1},{4:1,12:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{4:1,12:1,29:1,41:1},{2:1,12:1},{12:1},{12:1},{12:1},{6:1,12:1},{6:1,12:1},{12:1},{12:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{3:1,12:1},{12:1},{12:1},{12:1},{2:1,5:1,12:1},{2:1,12:1},{7:1,12:1},{12:1},{12:1},{12:1},{12:1},{12:1,15:1},{12:1,14:1,15:1,16:1},{11:1,12:1,14:1,15:1,16:1},{11:1,12:1,14:1,15:1,16:1},{11:1,12:1,14:1,15:1,16:1},{12:1,14:1,15:1,16:1,18:1,19:1,20:1,21:1,22:1},{12:1,14:1,15:1,16:1,17:1,18:1,19:1,20:1,21:1,22:1,23:1,24:1},{12:1,14:1,15:1,16:1,17:1,18:1,19:1,20:1,21:1,22:1,23:1,24:1},{11:1,12:1,14:1,15:1,16:1},{12:1,13:1,14:1,15:1,16:1,17:1,18:1,19:1,20:1,21:1,22:1,23:1,24:1,25:1},{12:1},{12:1,31:1},{12:1,31:1},{12:1,31:1},{12:1,14:1,15:1,16:1},{11:1,12:1,14:1,15:1,16:1},{12:1},{12:1},{11:1,12:1,14:1,15:1,16:1},{9:1,12:1},{12:1,31:1},{12:1,14:1,15:1,16:1,19:1,24:1},{12:1,14:1,15:1,16:1,18:1,19:1,20:1,21:1,22:1,25:1},{12:1},{10:1,11:1,12:1,14:1,15:1,16:1},{7:1,12:1},{12:1,14:1,15:1,16:1,18:1,19:1,20:1,21:1,22:1,24:1,25:1},{12:1,14:1,15:1,16:1,18:1,19:1,20:1,21:1,22:1,24:1,25:1},{11:1,12:1,14:1,15:1,16:1},{12:1},{12:1},{12:1},{12:1},{4:1,12:1,41:1},{12:1,26:1},{12:1,26:1},{12:1,26:1},{12:1,26:1},{12:1,26:1},{12:1,26:1},{12:1,26:1},{4:1,12:1,41:1},{12:1,26:1},{12:1,26:1,28:1},{12:1,26:1,27:1},{12:1,26:1},{12:1,26:1},{12:1,26:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{4:1,12:1,41:1},{12:1,30:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{4:1,12:1,41:1},{12:1,38:1},{4:1,12:1,41:1},{12:1},{12:1,32:1},{12:1,33:1},{12:1,33:1},{12:1},{12:1},{12:1},{12:1,32:1},{12:1,39:1},{12:1,33:1},{12:1},{12:1,33:1},{4:1,12:1,40:1,41:1},{4:1,12:1,41:1},{12:1,31:1},{12:1},{9:1,12:1},{8:1,12:1},{8:1,12:1},{6:1,12:1},{6:1,12:1},{8:1,12:1,14:1,15:1,16:1},{12:1},{12:1},{12:1},{12:1},{12:1,35:1},{8:1,12:1,14:1,15:1,16:1},{8:1,12:1,14:1,15:1,16:1},{12:1,36:1},{12:1,34:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1},{12:1}];if (org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor) {  var __gwt_initHandlers = org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.__gwt_initHandlers;  org_wyona_security_gwt_accesspolicyeditor_AccessPolicyEditor.onScriptLoad(gwtOnLoad);}})();