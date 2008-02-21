(function(){var $wnd = window;var $doc = $wnd.document;var $moduleName, $moduleBase;var _,ct='com.google.gwt.core.client.',dt='com.google.gwt.lang.',et='com.google.gwt.user.client.',ft='com.google.gwt.user.client.impl.',gt='com.google.gwt.user.client.ui.',ht='com.google.gwt.user.client.ui.impl.',it='java.lang.',jt='java.util.',kt='org.wyona.yanel.gwt.accesspolicyeditor.client.';function is(){}
function cm(a){return this===a;}
function dm(){return Em(this);}
function am(){}
_=am.prototype={};_.eQ=cm;_.hC=dm;_.tN=it+'Object';_.tI=1;function o(a){return a==null?null:a.tN;}
var p=null;function s(a){return a==null?0:a.$H?a.$H:(a.$H=u());}
function t(a){return a==null?0:a.$H?a.$H:(a.$H=u());}
function u(){return ++v;}
var v=0;function y(b,a){if(!mb(a,2)){return false;}return C(b,lb(a,2));}
function z(a){return s(a);}
function A(){return [];}
function B(){return {};}
function D(a){return y(this,a);}
function C(a,b){return a===b;}
function E(){return z(this);}
function w(){}
_=w.prototype=new am();_.eQ=D;_.hC=E;_.tN=ct+'JavaScriptObject';_.tI=7;function ab(c,a,d,b,e){c.a=a;c.b=b;c.tN=e;c.tI=d;return c;}
function cb(a,b,c){return a[b]=c;}
function db(b,a){return b[a];}
function eb(a){return a.length;}
function gb(e,d,c,b,a){return fb(e,d,c,b,0,eb(b),a);}
function fb(j,i,g,c,e,a,b){var d,f,h;if((f=db(c,e))<0){throw new Bl();}h=ab(new F(),f,db(i,e),db(g,e),j);++e;if(e<a){j=um(j,1);for(d=0;d<f;++d){cb(h,d,fb(j,i,g,c,e,a,b));}}else{for(d=0;d<f;++d){cb(h,d,b);}}return h;}
function hb(a,b,c){if(c!==null&&a.b!=0&& !mb(c,a.b)){throw new ll();}return cb(a,b,c);}
function F(){}
_=F.prototype=new am();_.tN=dt+'Array';_.tI=0;function kb(b,a){return !(!(b&&pb[b][a]));}
function lb(b,a){if(b!=null)kb(b.tI,a)||ob();return b;}
function mb(b,a){return b!=null&&kb(b.tI,a);}
function ob(){throw new ol();}
function nb(a){if(a!==null){throw new ol();}return a;}
function qb(b,d){_=d.prototype;if(b&& !(b.tI>=_.tI)){var c=b.toString;for(var a in _){b[a]=_[a];}b.toString=c;}return b;}
var pb;function ub(){ub=is;rc=lp(new jp());{mc=new Bd();Fd(mc);}}
function vb(b,a){ub();ie(mc,b,a);}
function wb(a,b){ub();return Dd(mc,a,b);}
function xb(){ub();return ke(mc,'button');}
function yb(){ub();return ke(mc,'div');}
function zb(){ub();return le(mc,'checkbox');}
function Ab(){ub();return le(mc,'text');}
function Bb(){ub();return ke(mc,'label');}
function Cb(a){ub();return me(mc,a);}
function Db(){ub();return ke(mc,'span');}
function Eb(){ub();return ke(mc,'tbody');}
function Fb(){ub();return ke(mc,'td');}
function ac(){ub();return ke(mc,'tr');}
function bc(){ub();return ke(mc,'table');}
function ec(b,a,d){ub();var c;c=p;{dc(b,a,d);}}
function dc(b,a,c){ub();var d;if(a===qc){if(gc(b)==8192){qc=null;}}d=cc;cc=b;try{c.D(b);}finally{cc=d;}}
function fc(b,a){ub();ne(mc,b,a);}
function gc(a){ub();return oe(mc,a);}
function hc(a){ub();ee(mc,a);}
function ic(a,b){ub();return pe(mc,a,b);}
function jc(a,b){ub();return qe(mc,a,b);}
function kc(a){ub();return re(mc,a);}
function lc(a){ub();return fe(mc,a);}
function nc(c,b,d,a){ub();se(mc,c,b,d,a);}
function oc(a){ub();var b,c;c=true;if(rc.b>0){b=nb(rp(rc,rc.b-1));if(!(c=null.lb())){fc(a,true);hc(a);}}return c;}
function pc(b,a){ub();te(mc,b,a);}
function uc(a,b,c){ub();we(mc,a,b,c);}
function sc(a,b,c){ub();ue(mc,a,b,c);}
function tc(a,b,c){ub();ve(mc,a,b,c);}
function vc(a,b){ub();xe(mc,a,b);}
function wc(a,b){ub();ye(mc,a,b);}
function xc(a,b){ub();ze(mc,a,b);}
function yc(b,c,a){ub();Ae(mc,b,c,a);}
function zc(b,a,c){ub();Be(mc,b,a,c);}
function Ac(a,b){ub();be(mc,a,b);}
var cc=null,mc=null,qc=null,rc;function Dc(a){if(mb(a,4)){return wb(this,lb(a,4));}return y(qb(this,Bc),a);}
function Ec(){return z(qb(this,Bc));}
function Bc(){}
_=Bc.prototype=new w();_.eQ=Dc;_.hC=Ec;_.tN=et+'Element';_.tI=8;function cd(a){return y(qb(this,Fc),a);}
function dd(){return z(qb(this,Fc));}
function Fc(){}
_=Fc.prototype=new w();_.eQ=cd;_.hC=dd;_.tN=et+'Event';_.tI=9;function jd(){jd=is;ld=lp(new jp());{kd();}}
function kd(){jd();pd(new fd());}
var ld;function hd(){while((jd(),ld).b>0){nb(rp((jd(),ld),0)).lb();}}
function id(){return null;}
function fd(){}
_=fd.prototype=new am();_.cb=hd;_.db=id;_.tN=et+'Timer$1';_.tI=10;function od(){od=is;rd=lp(new jp());zd=lp(new jp());{vd();}}
function pd(a){od();np(rd,a);}
function qd(a){od();$wnd.alert(a);}
function sd(){od();var a,b;for(a=rd.y();a.w();){b=lb(a.A(),5);b.cb();}}
function td(){od();var a,b,c,d;d=null;for(a=rd.y();a.w();){b=lb(a.A(),5);c=b.db();{d=c;}}return d;}
function ud(){od();var a,b;for(a=zd.y();a.w();){b=nb(a.A());null.lb();}}
function vd(){od();__gwt_initHandlers(function(){yd();},function(){return xd();},function(){wd();$wnd.onresize=null;$wnd.onbeforeclose=null;$wnd.onclose=null;});}
function wd(){od();var a;a=p;{sd();}}
function xd(){od();var a;a=p;{return td();}}
function yd(){od();var a;a=p;{ud();}}
var rd,zd;function ie(c,b,a){b.appendChild(a);}
function ke(b,a){return $doc.createElement(a);}
function le(b,c){var a=$doc.createElement('INPUT');a.type=c;return a;}
function me(c,a){var b;b=ke(c,'select');if(a){ue(c,b,'multiple',true);}return b;}
function ne(c,b,a){b.cancelBubble=a;}
function oe(b,a){switch(a.type){case 'blur':return 4096;case 'change':return 1024;case 'click':return 1;case 'dblclick':return 2;case 'focus':return 2048;case 'keydown':return 128;case 'keypress':return 256;case 'keyup':return 512;case 'load':return 32768;case 'losecapture':return 8192;case 'mousedown':return 4;case 'mousemove':return 64;case 'mouseout':return 32;case 'mouseover':return 16;case 'mouseup':return 8;case 'scroll':return 16384;case 'error':return 65536;case 'mousewheel':return 131072;case 'DOMMouseScroll':return 131072;}}
function pe(c,a,b){return !(!a[b]);}
function qe(d,a,c){var b=parseInt(a[c]);if(!b){return 0;}return b;}
function re(b,a){return a.__eventBits||0;}
function se(e,d,b,f,a){var c=new Option(b,f);if(a== -1||a>d.options.length-1){d.add(c,null);}else{d.add(c,d.options[a]);}}
function te(c,b,a){b.removeChild(a);}
function we(c,a,b,d){a[b]=d;}
function ue(c,a,b,d){a[b]=d;}
function ve(c,a,b,d){a[b]=d;}
function xe(c,a,b){a.__listener=b;}
function ye(c,a,b){if(!b){b='';}a.innerHTML=b;}
function ze(c,a,b){while(a.firstChild){a.removeChild(a.firstChild);}if(b!=null){a.appendChild($doc.createTextNode(b));}}
function Ae(e,c,d,a){var b=c.options[a];b.text=d;}
function Be(c,b,a,d){b.style[a]=d;}
function Ad(){}
_=Ad.prototype=new am();_.tN=ft+'DOMImpl';_.tI=0;function ee(b,a){a.preventDefault();}
function fe(c,a){var b=a.parentNode;if(b==null){return null;}if(b.nodeType!=1)b=null;return b||null;}
function ge(d){$wnd.__dispatchCapturedMouseEvent=function(b){if($wnd.__dispatchCapturedEvent(b)){var a=$wnd.__captureElem;if(a&&a.__listener){ec(b,a,a.__listener);b.stopPropagation();}}};$wnd.__dispatchCapturedEvent=function(a){if(!oc(a)){a.stopPropagation();a.preventDefault();return false;}return true;};$wnd.addEventListener('click',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('dblclick',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousedown',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mouseup',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousemove',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('mousewheel',$wnd.__dispatchCapturedMouseEvent,true);$wnd.addEventListener('keydown',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keyup',$wnd.__dispatchCapturedEvent,true);$wnd.addEventListener('keypress',$wnd.__dispatchCapturedEvent,true);$wnd.__dispatchEvent=function(b){var c,a=this;while(a&& !(c=a.__listener))a=a.parentNode;if(a&&a.nodeType!=1)a=null;if(c)ec(b,a,c);};$wnd.__captureElem=null;}
function he(c,b,a){b.__eventBits=a;b.onclick=a&1?$wnd.__dispatchEvent:null;b.ondblclick=a&2?$wnd.__dispatchEvent:null;b.onmousedown=a&4?$wnd.__dispatchEvent:null;b.onmouseup=a&8?$wnd.__dispatchEvent:null;b.onmouseover=a&16?$wnd.__dispatchEvent:null;b.onmouseout=a&32?$wnd.__dispatchEvent:null;b.onmousemove=a&64?$wnd.__dispatchEvent:null;b.onkeydown=a&128?$wnd.__dispatchEvent:null;b.onkeypress=a&256?$wnd.__dispatchEvent:null;b.onkeyup=a&512?$wnd.__dispatchEvent:null;b.onchange=a&1024?$wnd.__dispatchEvent:null;b.onfocus=a&2048?$wnd.__dispatchEvent:null;b.onblur=a&4096?$wnd.__dispatchEvent:null;b.onlosecapture=a&8192?$wnd.__dispatchEvent:null;b.onscroll=a&16384?$wnd.__dispatchEvent:null;b.onload=a&32768?$wnd.__dispatchEvent:null;b.onerror=a&65536?$wnd.__dispatchEvent:null;b.onmousewheel=a&131072?$wnd.__dispatchEvent:null;}
function ce(){}
_=ce.prototype=new Ad();_.tN=ft+'DOMImplStandard';_.tI=0;function Dd(c,a,b){if(!a&& !b){return true;}else if(!a|| !b){return false;}return a.isSameNode(b);}
function Fd(a){ge(a);Ed(a);}
function Ed(d){$wnd.addEventListener('mouseout',function(b){var a=$wnd.__captureElem;if(a&& !b.relatedTarget){if('html'==b.target.tagName.toLowerCase()){var c=$doc.createEvent('MouseEvents');c.initMouseEvent('mouseup',true,true,$wnd,0,b.screenX,b.screenY,b.clientX,b.clientY,b.ctrlKey,b.altKey,b.shiftKey,b.metaKey,b.button,null);a.dispatchEvent(c);}}},true);$wnd.addEventListener('DOMMouseScroll',$wnd.__dispatchCapturedMouseEvent,true);}
function be(c,b,a){he(c,b,a);ae(c,b,a);}
function ae(c,b,a){if(a&131072){b.addEventListener('DOMMouseScroll',$wnd.__dispatchEvent,false);}}
function Bd(){}
_=Bd.prototype=new ce();_.tN=ft+'DOMImplMozilla';_.tI=0;function zj(d,b,a){var c=b.parentNode;if(!c){return;}c.insertBefore(a,b);c.removeChild(b);}
function Aj(b,a){if(b.i!==null){zj(b,b.i,a);}b.i=a;}
function Bj(b,a){Ej(b.i,a);}
function Cj(b,a){Ac(b.r(),a|kc(b.r()));}
function Dj(){return this.i;}
function Ej(a,b){uc(a,'className',b);}
function xj(){}
_=xj.prototype=new am();_.r=Dj;_.tN=gt+'UIObject';_.tI=0;_.i=null;function zk(a){if(mb(a.h,8)){lb(a.h,8).gb(a);}else if(a.h!==null){throw wl(new vl(),"This widget's parent does not implement HasWidgets");}}
function Ak(b,a){if(b.x()){vc(b.r(),null);}Aj(b,a);if(b.x()){vc(a,b);}}
function Bk(c,b){var a;a=c.h;if(b===null){if(a!==null&&a.x()){c.F();}c.h=null;}else{if(a!==null){throw wl(new vl(),'Cannot set a new parent without first clearing the old parent');}c.h=b;if(b.x()){c.C();}}}
function Ck(){}
function Dk(){}
function Ek(){return this.g;}
function Fk(){if(this.x()){throw wl(new vl(),"Should only call onAttach when the widget is detached from the browser's document");}this.g=true;vc(this.r(),this);this.o();this.ab();}
function al(a){}
function bl(){if(!this.x()){throw wl(new vl(),"Should only call onDetach when the widget is attached to the browser's document");}try{this.bb();}finally{this.p();vc(this.r(),null);this.g=false;}}
function cl(){}
function dl(){}
function el(a){Ak(this,a);}
function gk(){}
_=gk.prototype=new xj();_.o=Ck;_.p=Dk;_.x=Ek;_.C=Fk;_.D=al;_.F=bl;_.ab=cl;_.bb=dl;_.hb=el;_.tN=gt+'Widget';_.tI=11;_.g=false;_.h=null;function vi(b,a){Bk(a,b);}
function xi(b,a){Bk(a,null);}
function yi(){var a,b;for(b=this.y();lk(b);){a=mk(b);a.C();}}
function zi(){var a,b;for(b=this.y();lk(b);){a=mk(b);a.F();}}
function Ai(){}
function Bi(){}
function ui(){}
_=ui.prototype=new gk();_.o=yi;_.p=zi;_.ab=Ai;_.bb=Bi;_.tN=gt+'Panel';_.tI=12;function eg(a){a.f=qk(new hk(),a);}
function fg(a){eg(a);return a;}
function gg(c,a,b){zk(a);rk(c.f,a);vb(b,a.r());vi(c,a);}
function ig(b,c){var a;if(c.h!==b){return false;}xi(b,c);a=c.r();pc(lc(a),a);xk(b.f,c);return true;}
function jg(){return vk(this.f);}
function kg(a){return ig(this,a);}
function dg(){}
_=dg.prototype=new ui();_.y=jg;_.gb=kg;_.tN=gt+'ComplexPanel';_.tI=13;function De(a){fg(a);a.hb(yb());zc(a.r(),'position','relative');zc(a.r(),'overflow','hidden');return a;}
function Ee(a,b){gg(a,b,a.r());}
function af(a){zc(a,'left','');zc(a,'top','');zc(a,'position','');}
function bf(b){var a;a=ig(this,b);if(a){af(b.r());}return a;}
function Ce(){}
_=Ce.prototype=new dg();_.gb=bf;_.tN=gt+'AbsolutePanel';_.tI=14;function yg(){yg=is;hl(),jl;}
function xg(b,a){hl(),jl;Ag(b,a);return b;}
function zg(b,a){switch(gc(a)){case 1:if(b.c!==null){bg(b.c,b);}break;case 4096:case 2048:break;case 128:case 512:case 256:break;}}
function Ag(b,a){Ak(b,a);Cj(b,7041);}
function Bg(a){if(this.c===null){this.c=Ff(new Ef());}np(this.c,a);}
function Cg(a){zg(this,a);}
function Dg(a){Ag(this,a);}
function wg(){}
_=wg.prototype=new gk();_.j=Bg;_.D=Cg;_.hb=Dg;_.tN=gt+'FocusWidget';_.tI=15;_.c=null;function ff(){ff=is;hl(),jl;}
function ef(b,a){hl(),jl;xg(b,a);return b;}
function gf(a){wc(this.r(),a);}
function df(){}
_=df.prototype=new wg();_.ib=gf;_.tN=gt+'ButtonBase';_.tI=16;function lf(){lf=is;hl(),jl;}
function hf(a){hl(),jl;ef(a,xb());mf(a.r());Bj(a,'gwt-Button');return a;}
function jf(b,a){hl(),jl;hf(b);b.ib(a);return b;}
function kf(c,a,b){hl(),jl;jf(c,a);c.j(b);return c;}
function mf(b){lf();if(b.type=='submit'){try{b.setAttribute('type','button');}catch(a){}}}
function cf(){}
_=cf.prototype=new df();_.tN=gt+'Button';_.tI=17;function of(a){fg(a);a.e=bc();a.d=Eb();vb(a.e,a.d);a.hb(a.e);return a;}
function qf(c,b,a){uc(b,'align',a.a);}
function rf(c,b,a){zc(b,'verticalAlign',a.a);}
function nf(){}
_=nf.prototype=new dg();_.tN=gt+'CellPanel';_.tI=18;_.d=null;_.e=null;function wf(){wf=is;hl(),jl;}
function tf(a){hl(),jl;uf(a,zb());Bj(a,'gwt-CheckBox');return a;}
function vf(b,a){hl(),jl;tf(b);zf(b,a);return b;}
function uf(b,a){var c;hl(),jl;ef(b,Db());b.a=a;b.b=Bb();Ac(b.a,kc(b.r()));Ac(b.r(),0);vb(b.r(),b.a);vb(b.r(),b.b);c='check'+ ++Df;uc(b.a,'id',c);uc(b.b,'htmlFor',c);return b;}
function xf(b){var a;a=b.x()?'checked':'defaultChecked';return ic(b.a,a);}
function yf(b,a){sc(b.a,'checked',a);sc(b.a,'defaultChecked',a);}
function zf(b,a){xc(b.b,a);}
function Af(){vc(this.a,this);}
function Bf(){vc(this.a,null);yf(this,xf(this));}
function Cf(a){wc(this.b,a);}
function sf(){}
_=sf.prototype=new df();_.ab=Af;_.bb=Bf;_.ib=Cf;_.tN=gt+'CheckBox';_.tI=19;_.a=null;_.b=null;var Df=0;function gn(d,a,b){var c;while(a.w()){c=a.A();if(b===null?c===null:b.eQ(c)){return a;}}return null;}
function jn(a){throw dn(new cn(),'add');}
function kn(b){var a;a=gn(this,this.y(),b);return a!==null;}
function fn(){}
_=fn.prototype=new am();_.l=jn;_.n=kn;_.tN=jt+'AbstractCollection';_.tI=0;function un(b,a){throw zl(new yl(),'Index: '+a+', Size: '+b.b);}
function vn(b,a){throw dn(new cn(),'add');}
function wn(a){this.k(this.jb(),a);return true;}
function xn(e){var a,b,c,d,f;if(e===this){return true;}if(!mb(e,13)){return false;}f=lb(e,13);if(this.jb()!=f.jb()){return false;}c=this.y();d=f.y();while(c.w()){a=c.A();b=d.A();if(!(a===null?b===null:a.eQ(b))){return false;}}return true;}
function yn(){var a,b,c,d;c=1;a=31;b=this.y();while(b.w()){d=b.A();c=31*c+(d===null?0:d.hC());}return c;}
function zn(){return nn(new mn(),this);}
function An(a){throw dn(new cn(),'remove');}
function ln(){}
_=ln.prototype=new fn();_.k=vn;_.l=wn;_.eQ=xn;_.hC=yn;_.y=zn;_.fb=An;_.tN=jt+'AbstractList';_.tI=20;function kp(a){{op(a);}}
function lp(a){kp(a);return a;}
function mp(c,a,b){if(a<0||a>c.b){un(c,a);}vp(c.a,a,b);++c.b;}
function np(b,a){Ep(b.a,b.b++,a);return true;}
function op(a){a.a=A();a.b=0;}
function qp(b,a){return sp(b,a)!=(-1);}
function rp(b,a){if(a<0||a>=b.b){un(b,a);}return Ap(b.a,a);}
function sp(b,a){return tp(b,a,0);}
function tp(c,b,a){if(a<0){un(c,a);}for(;a<c.b;++a){if(zp(b,Ap(c.a,a))){return a;}}return (-1);}
function up(c,a){var b;b=rp(c,a);Cp(c.a,a,1);--c.b;return b;}
function wp(a,b){mp(this,a,b);}
function xp(a){return np(this,a);}
function vp(a,b,c){a.splice(b,0,c);}
function yp(a){return qp(this,a);}
function zp(a,b){return a===b||a!==null&&a.eQ(b);}
function Bp(a){return rp(this,a);}
function Ap(a,b){return a[b];}
function Dp(a){return up(this,a);}
function Cp(a,c,b){a.splice(c,b);}
function Ep(a,b,c){a[b]=c;}
function Fp(){return this.b;}
function jp(){}
_=jp.prototype=new ln();_.k=wp;_.l=xp;_.n=yp;_.u=Bp;_.fb=Dp;_.jb=Fp;_.tN=jt+'ArrayList';_.tI=21;_.a=null;_.b=0;function Ff(a){lp(a);return a;}
function bg(d,c){var a,b;for(a=d.y();a.w();){b=lb(a.A(),6);b.E(c);}}
function Ef(){}
_=Ef.prototype=new jp();_.tN=gt+'ClickListenerCollection';_.tI=22;function ng(a,b){if(a.f!==null){throw wl(new vl(),'Composite.initWidget() may only be called once.');}zk(b);a.hb(b.r());a.f=b;Bk(b,a);}
function og(){if(this.f===null){throw wl(new vl(),'initWidget() was never called in '+o(this));}return this.i;}
function pg(){if(this.f!==null){return this.f.x();}return false;}
function qg(){this.f.C();this.ab();}
function rg(){try{this.bb();}finally{this.f.F();}}
function lg(){}
_=lg.prototype=new gk();_.r=og;_.x=pg;_.C=qg;_.F=rg;_.tN=gt+'Composite';_.tI=23;_.f=null;function tg(a){fg(a);a.hb(yb());return a;}
function ug(a,b){gg(a,b,a.r());}
function sg(){}
_=sg.prototype=new dg();_.tN=gt+'FlowPanel';_.tI=24;function eh(){eh=is;ch(new bh(),'center');fh=ch(new bh(),'left');ch(new bh(),'right');}
var fh;function ch(b,a){b.a=a;return b;}
function bh(){}
_=bh.prototype=new am();_.tN=gt+'HasHorizontalAlignment$HorizontalAlignmentConstant';_.tI=0;_.a=null;function lh(){lh=is;jh(new ih(),'bottom');jh(new ih(),'middle');mh=jh(new ih(),'top');}
var mh;function jh(a,b){a.a=b;return a;}
function ih(){}
_=ih.prototype=new am();_.tN=gt+'HasVerticalAlignment$VerticalAlignmentConstant';_.tI=0;_.a=null;function qh(a){a.a=(eh(),fh);a.c=(lh(),mh);}
function rh(a){of(a);qh(a);a.b=ac();vb(a.d,a.b);uc(a.e,'cellSpacing','0');uc(a.e,'cellPadding','0');return a;}
function sh(b,c){var a;a=uh(b);vb(b.b,a);gg(b,c,a);}
function uh(b){var a;a=Fb();qf(b,a,b.a);rf(b,a,b.c);return a;}
function vh(c){var a,b;b=lc(c.r());a=ig(this,c);if(a){pc(this.b,b);}return a;}
function ph(){}
_=ph.prototype=new nf();_.gb=vh;_.tN=gt+'HorizontalPanel';_.tI=25;_.b=null;function yh(a){a.hb(yb());Cj(a,131197);Bj(a,'gwt-Label');return a;}
function zh(b,a){yh(b);Bh(b,a);return b;}
function Bh(b,a){xc(b.r(),a);}
function Ch(a){switch(gc(a)){case 1:break;case 4:case 8:case 64:case 16:case 32:break;case 131072:break;}}
function xh(){}
_=xh.prototype=new gk();_.D=Ch;_.tN=gt+'Label';_.tI=26;function ii(){ii=is;hl(),jl;si=new Eh();}
function ei(b,a){ii();xg(b,Cb(a));Cj(b,1024);Bj(b,'gwt-ListBox');return b;}
function fi(b,a){ni(b,a,(-1));}
function gi(b,a,c){oi(b,a,c,(-1));}
function hi(b,a){if(a<0||a>=ji(b)){throw new yl();}}
function ji(a){return ai(si,a.r());}
function ki(b,a){hi(b,a);return bi(si,b.r(),a);}
function li(a){return jc(a.r(),'selectedIndex');}
function mi(b,a){hi(b,a);return ci(si,b.r(),a);}
function ni(c,b,a){oi(c,b,b,a);}
function oi(c,b,d,a){nc(c.r(),b,d,a);}
function pi(b,a){hi(b,a);di(si,b.r(),a);}
function qi(c,a,b){hi(c,a);if(b===null){throw El(new Dl(),'Cannot set an option to have null text');}yc(c.r(),b,a);}
function ri(a,b){tc(a.r(),'size',b);}
function ti(a){if(gc(a)==1024){}else{zg(this,a);}}
function Dh(){}
_=Dh.prototype=new wg();_.D=ti;_.tN=gt+'ListBox';_.tI=27;var si;function ai(b,a){return a.options.length;}
function bi(c,b,a){return b.options[a].text;}
function ci(c,b,a){return b.options[a].value;}
function di(c,b,a){b.options[a]=null;}
function Eh(){}
_=Eh.prototype=new am();_.tN=gt+'ListBox$Impl';_.tI=0;function cj(){cj=is;hj=Cq(new cq());}
function bj(b,a){cj();De(b);if(a===null){a=dj();}b.hb(a);b.C();return b;}
function ej(){cj();return fj(null);}
function fj(c){cj();var a,b;b=lb(cr(hj,c),7);if(b!==null){return b;}a=null;if(hj.c==0){gj();}dr(hj,c,b=bj(new Ci(),a));return b;}
function dj(){cj();return $doc.body;}
function gj(){cj();pd(new Di());}
function Ci(){}
_=Ci.prototype=new Ce();_.tN=gt+'RootPanel';_.tI=28;var hj;function Fi(){var a,b;for(b=po(Eo((cj(),hj)));wo(b);){a=lb(xo(b),7);if(a.x()){a.F();}}}
function aj(){return null;}
function Di(){}
_=Di.prototype=new am();_.cb=Fi;_.db=aj;_.tN=gt+'RootPanel$1';_.tI=29;function rj(){rj=is;hl(),jl;}
function qj(b,a){hl(),jl;xg(b,a);Cj(b,1024);return b;}
function sj(a){if(this.a===null){this.a=Ff(new Ef());}np(this.a,a);}
function tj(a){var b;zg(this,a);b=gc(a);if(b==1){if(this.a!==null){bg(this.a,this);}}else{}}
function pj(){}
_=pj.prototype=new wg();_.j=sj;_.D=tj;_.tN=gt+'TextBoxBase';_.tI=30;_.a=null;function vj(){vj=is;hl(),jl;}
function uj(a){hl(),jl;qj(a,Ab());Bj(a,'gwt-TextBox');return a;}
function wj(b,a){tc(b.r(),'size',a);}
function oj(){}
_=oj.prototype=new pj();_.tN=gt+'TextBox';_.tI=31;function ak(a){a.a=(eh(),fh);a.b=(lh(),mh);}
function bk(a){of(a);ak(a);uc(a.e,'cellSpacing','0');uc(a.e,'cellPadding','0');return a;}
function ck(b,d){var a,c;c=ac();a=ek(b);vb(c,a);vb(b.d,c);gg(b,d,a);}
function ek(b){var a;a=Fb();qf(b,a,b.a);rf(b,a,b.b);return a;}
function fk(c){var a,b;b=lc(c.r());a=ig(this,c);if(a){pc(this.d,lc(b));}return a;}
function Fj(){}
_=Fj.prototype=new nf();_.gb=fk;_.tN=gt+'VerticalPanel';_.tI=32;function qk(b,a){b.b=a;b.a=gb('[Lcom.google.gwt.user.client.ui.Widget;',[0],[10],[4],null);return b;}
function rk(a,b){uk(a,b,a.c);}
function tk(b,c){var a;for(a=0;a<b.c;++a){if(b.a[a]===c){return a;}}return (-1);}
function uk(d,e,a){var b,c;if(a<0||a>d.c){throw new yl();}if(d.c==d.a.a){c=gb('[Lcom.google.gwt.user.client.ui.Widget;',[0],[10],[d.a.a*2],null);for(b=0;b<d.a.a;++b){hb(c,b,d.a[b]);}d.a=c;}++d.c;for(b=d.c-1;b>a;--b){hb(d.a,b,d.a[b-1]);}hb(d.a,a,e);}
function vk(a){return jk(new ik(),a);}
function wk(c,b){var a;if(b<0||b>=c.c){throw new yl();}--c.c;for(a=b;a<c.c;++a){hb(c.a,a,c.a[a+1]);}hb(c.a,c.c,null);}
function xk(b,c){var a;a=tk(b,c);if(a==(-1)){throw new xr();}wk(b,a);}
function hk(){}
_=hk.prototype=new am();_.tN=gt+'WidgetCollection';_.tI=0;_.a=null;_.b=null;_.c=0;function jk(b,a){b.b=a;return b;}
function lk(a){return a.a<a.b.c-1;}
function mk(a){if(a.a>=a.b.c){throw new xr();}return a.b.a[++a.a];}
function nk(){return lk(this);}
function ok(){return mk(this);}
function pk(){if(this.a<0||this.a>=this.b.c){throw new vl();}this.b.b.gb(this.b.a[this.a--]);}
function ik(){}
_=ik.prototype=new am();_.w=nk;_.A=ok;_.eb=pk;_.tN=gt+'WidgetCollection$WidgetIterator';_.tI=0;_.a=(-1);function hl(){hl=is;il=gl(new fl());jl=il;}
function gl(a){hl();return a;}
function fl(){}
_=fl.prototype=new am();_.tN=ht+'FocusImpl';_.tI=0;var il,jl;function an(b,a){a;return b;}
function Fm(){}
_=Fm.prototype=new am();_.tN=it+'Throwable';_.tI=3;function tl(b,a){an(b,a);return b;}
function sl(){}
_=sl.prototype=new Fm();_.tN=it+'Exception';_.tI=4;function fm(b,a){tl(b,a);return b;}
function em(){}
_=em.prototype=new sl();_.tN=it+'RuntimeException';_.tI=5;function ll(){}
_=ll.prototype=new em();_.tN=it+'ArrayStoreException';_.tI=33;function ol(){}
_=ol.prototype=new em();_.tN=it+'ClassCastException';_.tI=34;function wl(b,a){fm(b,a);return b;}
function vl(){}
_=vl.prototype=new em();_.tN=it+'IllegalStateException';_.tI=35;function zl(b,a){fm(b,a);return b;}
function yl(){}
_=yl.prototype=new em();_.tN=it+'IndexOutOfBoundsException';_.tI=36;function Bl(){}
_=Bl.prototype=new em();_.tN=it+'NegativeArraySizeException';_.tI=37;function El(b,a){fm(b,a);return b;}
function Dl(){}
_=Dl.prototype=new em();_.tN=it+'NullPointerException';_.tI=38;function qm(b,a){if(!mb(a,1))return false;return ym(b,a);}
function rm(b,a){return b.indexOf(a);}
function sm(b,a){return tm(b,a,0);}
function tm(j,i,g){var a=new RegExp(i,'g');var h=[];var b=0;var k=j;var e=null;while(true){var f=a.exec(k);if(f==null||(k==''||b==g-1&&g>0)){h[b]=k;break;}else{h[b]=k.substring(0,f.index);k=k.substring(f.index+f[0].length,k.length);a.lastIndex=0;if(e==k){h[b]=k.substring(0,1);k=k.substring(1);}e=k;b++;}}if(g==0){for(var c=h.length-1;c>=0;c--){if(h[c]!=''){h.splice(c+1,h.length-(c+1));break;}}}var d=xm(h.length);var c=0;for(c=0;c<h.length;++c){d[c]=h[c];}return d;}
function um(b,a){return b.substr(a,b.length-a);}
function vm(c,a,b){return c.substr(a,b-a);}
function wm(c){var a=c.replace(/^(\s*)/,'');var b=a.replace(/\s*$/,'');return b;}
function xm(a){return gb('[Ljava.lang.String;',[0],[1],[a],null);}
function ym(a,b){return String(a)==b;}
function zm(a){return qm(this,a);}
function Bm(){var a=Am;if(!a){a=Am={};}var e=':'+this;var b=a[e];if(b==null){b=0;var f=this.length;var d=f<64?1:f/32|0;for(var c=0;c<f;c+=d){b<<=1;b+=this.charCodeAt(c);}b|=0;a[e]=b;}return b;}
_=String.prototype;_.eQ=zm;_.hC=Bm;_.tN=it+'String';_.tI=2;var Am=null;function jm(b,a){lm(b,a);return b;}
function km(c,d){if(d===null){d='null';}var a=c.js.length-1;var b=c.js[a].length;if(c.length>b*b){c.js[a]=c.js[a]+d;}else{c.js.push(d);}c.length+=d.length;return c;}
function lm(b,a){b.js=[a];b.length=a.length;}
function nm(a){a.B();return a.js[0];}
function om(){if(this.js.length>1){this.js=[this.js.join('')];this.length=this.js[0].length;}}
function im(){}
_=im.prototype=new am();_.B=om;_.tN=it+'StringBuffer';_.tI=0;function Em(a){return t(a);}
function dn(b,a){fm(b,a);return b;}
function cn(){}
_=cn.prototype=new em();_.tN=it+'UnsupportedOperationException';_.tI=39;function nn(b,a){b.c=a;return b;}
function pn(a){return a.a<a.c.jb();}
function qn(){return pn(this);}
function rn(){if(!pn(this)){throw new xr();}return this.c.u(this.b=this.a++);}
function sn(){if(this.b<0){throw new vl();}this.c.fb(this.b);this.a=this.b;this.b=(-1);}
function mn(){}
_=mn.prototype=new am();_.w=qn;_.A=rn;_.eb=sn;_.tN=jt+'AbstractList$IteratorImpl';_.tI=0;_.a=0;_.b=(-1);function Co(f,d,e){var a,b,c;for(b=xq(f.q());pq(b);){a=qq(b);c=a.s();if(d===null?c===null:d.eQ(c)){if(e){rq(b);}return a;}}return null;}
function Do(b){var a;a=b.q();return Dn(new Cn(),b,a);}
function Eo(b){var a;a=br(b);return no(new mo(),b,a);}
function Fo(a){return Co(this,a,false)!==null;}
function ap(d){var a,b,c,e,f,g,h;if(d===this){return true;}if(!mb(d,14)){return false;}f=lb(d,14);c=Do(this);e=f.z();if(!gp(c,e)){return false;}for(a=Fn(c);ho(a);){b=io(a);h=this.v(b);g=f.v(b);if(h===null?g!==null:!h.eQ(g)){return false;}}return true;}
function bp(b){var a;a=Co(this,b,false);return a===null?null:a.t();}
function cp(){var a,b,c;b=0;for(c=xq(this.q());pq(c);){a=qq(c);b+=a.hC();}return b;}
function dp(){return Do(this);}
function Bn(){}
_=Bn.prototype=new am();_.m=Fo;_.eQ=ap;_.v=bp;_.hC=cp;_.z=dp;_.tN=jt+'AbstractMap';_.tI=40;function gp(e,b){var a,c,d;if(b===e){return true;}if(!mb(b,15)){return false;}c=lb(b,15);if(c.jb()!=e.jb()){return false;}for(a=c.y();a.w();){d=a.A();if(!e.n(d)){return false;}}return true;}
function hp(a){return gp(this,a);}
function ip(){var a,b,c;a=0;for(b=this.y();b.w();){c=b.A();if(c!==null){a+=c.hC();}}return a;}
function ep(){}
_=ep.prototype=new fn();_.eQ=hp;_.hC=ip;_.tN=jt+'AbstractSet';_.tI=41;function Dn(b,a,c){b.a=a;b.b=c;return b;}
function Fn(b){var a;a=xq(b.b);return fo(new eo(),b,a);}
function ao(a){return this.a.m(a);}
function bo(){return Fn(this);}
function co(){return this.b.a.c;}
function Cn(){}
_=Cn.prototype=new ep();_.n=ao;_.y=bo;_.jb=co;_.tN=jt+'AbstractMap$1';_.tI=42;function fo(b,a,c){b.a=c;return b;}
function ho(a){return a.a.w();}
function io(b){var a;a=b.a.A();return a.s();}
function jo(){return ho(this);}
function ko(){return io(this);}
function lo(){this.a.eb();}
function eo(){}
_=eo.prototype=new am();_.w=jo;_.A=ko;_.eb=lo;_.tN=jt+'AbstractMap$2';_.tI=0;function no(b,a,c){b.a=a;b.b=c;return b;}
function po(b){var a;a=xq(b.b);return uo(new to(),b,a);}
function qo(a){return ar(this.a,a);}
function ro(){return po(this);}
function so(){return this.b.a.c;}
function mo(){}
_=mo.prototype=new fn();_.n=qo;_.y=ro;_.jb=so;_.tN=jt+'AbstractMap$3';_.tI=0;function uo(b,a,c){b.a=c;return b;}
function wo(a){return a.a.w();}
function xo(a){var b;b=a.a.A().t();return b;}
function yo(){return wo(this);}
function zo(){return xo(this);}
function Ao(){this.a.eb();}
function to(){}
_=to.prototype=new am();_.w=yo;_.A=zo;_.eb=Ao;_.tN=jt+'AbstractMap$4';_.tI=0;function Eq(){Eq=is;fr=lr();}
function Bq(a){{Dq(a);}}
function Cq(a){Eq();Bq(a);return a;}
function Dq(a){a.a=A();a.d=B();a.b=qb(fr,w);a.c=0;}
function Fq(b,a){if(mb(a,1)){return pr(b.d,lb(a,1))!==fr;}else if(a===null){return b.b!==fr;}else{return or(b.a,a,a.hC())!==fr;}}
function ar(a,b){if(a.b!==fr&&nr(a.b,b)){return true;}else if(kr(a.d,b)){return true;}else if(ir(a.a,b)){return true;}return false;}
function br(a){return vq(new lq(),a);}
function cr(c,a){var b;if(mb(a,1)){b=pr(c.d,lb(a,1));}else if(a===null){b=c.b;}else{b=or(c.a,a,a.hC());}return b===fr?null:b;}
function dr(c,a,d){var b;{b=c.b;c.b=d;}if(b===fr){++c.c;return null;}else{return b;}}
function er(c,a){var b;if(mb(a,1)){b=sr(c.d,lb(a,1));}else if(a===null){b=c.b;c.b=qb(fr,w);}else{b=rr(c.a,a,a.hC());}if(b===fr){return null;}else{--c.c;return b;}}
function gr(e,c){Eq();for(var d in e){if(d==parseInt(d)){var a=e[d];for(var f=0,b=a.length;f<b;++f){c.l(a[f]);}}}}
function hr(d,a){Eq();for(var c in d){if(c.charCodeAt(0)==58){var e=d[c];var b=gq(c.substring(1),e);a.l(b);}}}
function ir(f,h){Eq();for(var e in f){if(e==parseInt(e)){var a=f[e];for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.t();if(nr(h,d)){return true;}}}}return false;}
function jr(a){return Fq(this,a);}
function kr(c,d){Eq();for(var b in c){if(b.charCodeAt(0)==58){var a=c[b];if(nr(d,a)){return true;}}}return false;}
function lr(){Eq();}
function mr(){return br(this);}
function nr(a,b){Eq();if(a===b){return true;}else if(a===null){return false;}else{return a.eQ(b);}}
function qr(a){return cr(this,a);}
function or(f,h,e){Eq();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.s();if(nr(h,d)){return c.t();}}}}
function pr(b,a){Eq();return b[':'+a];}
function rr(f,h,e){Eq();var a=f[e];if(a){for(var g=0,b=a.length;g<b;++g){var c=a[g];var d=c.s();if(nr(h,d)){if(a.length==1){delete f[e];}else{a.splice(g,1);}return c.t();}}}}
function sr(c,a){Eq();a=':'+a;var b=c[a];delete c[a];return b;}
function cq(){}
_=cq.prototype=new Bn();_.m=jr;_.q=mr;_.v=qr;_.tN=jt+'HashMap';_.tI=43;_.a=null;_.b=null;_.c=0;_.d=null;var fr;function eq(b,a,c){b.a=a;b.b=c;return b;}
function gq(a,b){return eq(new dq(),a,b);}
function hq(b){var a;if(mb(b,16)){a=lb(b,16);if(nr(this.a,a.s())&&nr(this.b,a.t())){return true;}}return false;}
function iq(){return this.a;}
function jq(){return this.b;}
function kq(){var a,b;a=0;b=0;if(this.a!==null){a=this.a.hC();}if(this.b!==null){b=this.b.hC();}return a^b;}
function dq(){}
_=dq.prototype=new am();_.eQ=hq;_.s=iq;_.t=jq;_.hC=kq;_.tN=jt+'HashMap$EntryImpl';_.tI=44;_.a=null;_.b=null;function vq(b,a){b.a=a;return b;}
function xq(a){return nq(new mq(),a.a);}
function yq(c){var a,b,d;if(mb(c,16)){a=lb(c,16);b=a.s();if(Fq(this.a,b)){d=cr(this.a,b);return nr(a.t(),d);}}return false;}
function zq(){return xq(this);}
function Aq(){return this.a.c;}
function lq(){}
_=lq.prototype=new ep();_.n=yq;_.y=zq;_.jb=Aq;_.tN=jt+'HashMap$EntrySet';_.tI=45;function nq(c,b){var a;c.c=b;a=lp(new jp());if(c.c.b!==(Eq(),fr)){np(a,eq(new dq(),null,c.c.b));}hr(c.c.d,a);gr(c.c.a,a);c.a=a.y();return c;}
function pq(a){return a.a.w();}
function qq(a){return a.b=lb(a.a.A(),16);}
function rq(a){if(a.b===null){throw wl(new vl(),'Must call next() before remove().');}else{a.a.eb();er(a.c,a.b.s());a.b=null;}}
function sq(){return pq(this);}
function tq(){return qq(this);}
function uq(){rq(this);}
function mq(){}
_=mq.prototype=new am();_.w=sq;_.A=tq;_.eb=uq;_.tN=jt+'HashMap$EntrySetIterator';_.tI=0;_.a=null;_.b=null;function xr(){}
_=xr.prototype=new em();_.tN=jt+'NoSuchElementException';_.tI=46;function Cr(a){a.a=lp(new jp());return a;}
function Dr(b,a){return np(b.a,a);}
function Fr(b,a){return as(b,a);}
function as(b,a){return rp(b.a,a);}
function bs(a,b){mp(this.a,a,b);}
function cs(a){return Dr(this,a);}
function ds(a){return qp(this.a,a);}
function es(a){return as(this,a);}
function fs(){return this.a.y();}
function gs(a){return up(this.a,a);}
function hs(){return this.a.b;}
function Br(){}
_=Br.prototype=new ln();_.k=bs;_.l=cs;_.n=ds;_.u=es;_.y=fs;_.fb=gs;_.jb=hs;_.tN=jt+'Vector';_.tI=47;_.a=null;function ls(g){var a,b,c,d,e,f,h,i;i=bk(new Fj());Ee(ej(),i);e=bk(new Fj());ck(i,e);f=uj(new oj());wj(f,30);ck(e,f);ck(e,jf(new cf(),'Search within Identities'));b=rh(new ph());ck(i,b);ck(i,jf(new cf(),'Apply Policy'));ck(i,jf(new cf(),'Save Policy and Exit'));ck(i,jf(new cf(),'Cancel'));h=10;c=us(new ss(),h);d=zs(new xs(),h);a=os(new ms(),c.a,d.a);sh(b,c);sh(b,a);sh(b,d);}
function js(){}
_=js.prototype=new am();_.tN=kt+'AccessPolicyEditor';_.tI=0;function ns(a){a.b=tg(new sg());}
function os(c,a,b){ns(c);ng(c,c.b);c.e=kf(new cf(),'<',c);ug(c.b,c.e);c.a=kf(new cf(),'>',c);ug(c.b,c.a);c.c=a;c.d=b;return c;}
function qs(b,a){if(rm(a,'(')>0){return vm(a,0,rm(a,'('));}else{return a;}}
function rs(c){var a,b;if(c===this.a){a=li(this.c);if(a>=0){b=mi(this.c,a);qd('Add selected identity '+b+' to policy');pi(this.c,a);fi(this.d,b);}else{qd('No identity selected yet! Please select an identity.');}}else if(c===this.e){a=li(this.d);if(a>=0){b=mi(this.d,a);qd('Remove selected identity '+b+' from policy');pi(this.d,a);fi(this.c,qs(this,b));}else{qd('No identity selected yet! Please select an identity.');}}}
function ms(){}
_=ms.prototype=new lg();_.E=rs;_.tN=kt+'AddRemoveIdentitiesWidget';_.tI=48;_.a=null;_.c=null;_.d=null;_.e=null;function ts(a){a.b=bk(new Fj());}
function us(a,b){ts(a);ng(a,a.b);ck(a.b,zh(new xh(),'Identities'));a.a=ei(new Dh(),true);a.a.j(a);ri(a.a,b);fi(a.a,'U: michi');fi(a.a,'U: levi');fi(a.a,'U: vanya');fi(a.a,'U: ezra');ck(a.b,a.a);return a;}
function ws(a){}
function ss(){}
_=ss.prototype=new lg();_.E=ws;_.tN=kt+'IdentitiesListBoxWidget';_.tI=49;_.a=null;function ys(a){a.c=bk(new Fj());}
function zs(b,c){var a;ys(b);ng(b,b.c);ck(b.c,zh(new xh(),'Policy'));a=vf(new sf(),'Inherit rights from parent policies');yf(a,true);ck(b.c,a);b.a=ei(new Dh(),true);b.a.j(b);ri(b.a,c);gi(b.a,'U: alice (Read,Write)','U: alice (Read,Write)');gi(b.a,'U: karin (Read)','U: karin (Read)');gi(b.a,'U: susi','U: susi');gi(b.a,'WORLD','WORLD');ck(b.c,b.a);b.b=vf(new sf(),'Read');b.b.j(b);ck(b.c,b.b);b.d=vf(new sf(),'Write');b.d.j(b);ck(b.c,b.d);return b;}
function As(g,a,f){var b,c,d,e;b=false;e=Cr(new Br());for(c=0;c<a.a;c++){if(qm(a[c],f)){b=true;}else{Dr(e,a[c]);}}if(!b)Dr(e,f);d=gb('[Ljava.lang.String;',[0],[1],[e.a.b],null);for(c=0;c<d.a;c++){d[c]=lb(Fr(e,c),1);}return d;}
function Cs(b,a){if(rm(a,'(')>0){return wm(vm(a,0,rm(a,'(')));}else{return wm(a);}}
function Ds(c,a){var b;if(rm(a,'(')>0){b=vm(a,rm(a,'(')+1,rm(a,')'));return sm(b,',');}else{return gb('[Ljava.lang.String;',[0],[1],[0],null);}}
function Es(b){var a;a=li(b.a);if(a>=0){return ki(b.a,a);}return null;}
function Fs(f,a,e){var b,c,d;d=Cr(new Br());for(b=0;b<a.a;b++){if(!qm(a[b],e)){Dr(d,a[b]);}}c=gb('[Ljava.lang.String;',[0],[1],[d.a.b],null);for(b=0;b<c.a;b++){c[b]=lb(Fr(d,b),1);}return c;}
function at(e,c){var a,b,d;a=li(e.a);if(a>=0){d=jm(new im(),Cs(e,Es(e)));if(c.a>0){km(d,' ('+c[0]);for(b=1;b<c.a;b++){km(d,','+c[b]);}km(d,')');}qi(e.a,a,nm(d));}else{qd('Exception: No list item selected!');}}
function bt(h){var a,b,c,d,e,f,g;if(h===this.b||h===this.d){g=Es(this);if(g!==null){if(h===this.b){qd('Add/Remove Read right from selected identity '+g+' from policy');a=Ds(this,g);if(xf(this.b)){e=As(this,a,'Read');}else{e=Fs(this,a,'Read');}at(this,e);}else if(h===this.d){qd('Add/Remove Write right from selected identity '+g+' from policy');a=Ds(this,g);if(xf(this.b)){e=As(this,a,'Write');}else{e=Fs(this,a,'Write');}at(this,e);}}else{qd('No identity has been selected! Please select an identity in order to assign rights.');yf(this.b,false);yf(this.d,false);}}else if(h===this.a){g=Es(this);f=Ds(this,g);b=false;c=false;for(d=0;d<f.a;d++){if(qm(f[d],'Read')){yf(this.b,true);b=true;}else if(qm(f[d],'Write')){yf(this.d,true);c=true;}}if(!b)yf(this.b,false);if(!c)yf(this.d,false);}}
function xs(){}
_=xs.prototype=new lg();_.E=bt;_.tN=kt+'PolicyListBoxWidget';_.tI=50;_.a=null;_.b=null;_.d=null;function kl(){ls(new js());}
function gwtOnLoad(b,d,c){$moduleName=d;$moduleBase=c;if(b)try{kl();}catch(a){b(d);}else{kl();}}
var pb=[{},{},{1:1},{3:1},{3:1},{3:1},{3:1},{2:1},{2:1,4:1},{2:1},{5:1},{9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{13:1},{13:1},{13:1},{9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{7:1,8:1,9:1,10:1,11:1,12:1},{5:1},{9:1,10:1,11:1,12:1},{9:1,10:1,11:1,12:1},{8:1,9:1,10:1,11:1,12:1},{3:1},{3:1},{3:1},{3:1},{3:1},{3:1},{3:1},{14:1},{15:1},{15:1},{14:1},{16:1},{15:1},{3:1},{13:1},{6:1,9:1,10:1,11:1,12:1},{6:1,9:1,10:1,11:1,12:1},{6:1,9:1,10:1,11:1,12:1}];if (org_wyona_yanel_gwt_accesspolicyeditor_AccessPolicyEditor) {  var __gwt_initHandlers = org_wyona_yanel_gwt_accesspolicyeditor_AccessPolicyEditor.__gwt_initHandlers;  org_wyona_yanel_gwt_accesspolicyeditor_AccessPolicyEditor.onScriptLoad(gwtOnLoad);}})();