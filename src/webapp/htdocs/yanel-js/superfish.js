/*
 * Superfish v1.4.8 - jQuery menu widget
 * Copyright (c) 2008 Joel Birch
 *
 * Dual licensed under the MIT and GPL licenses:
 * 	http://www.opensource.org/licenses/mit-license.php
 * 	http://www.gnu.org/licenses/gpl.html
 *
 * CHANGELOG: http://users.tpg.com.au/j_birch/plugins/superfish/changelog.txt
 */

;(function($yanelJquery144){
	$yanelJquery144.fn.superfish = function(op){

		var sf = $yanelJquery144.fn.superfish,
			c = sf.c,
			$yanelJquery144arrow = $yanelJquery144(['<span class="',c.arrowClass,'"> &#187;</span>'].join('')),
			over = function(){
				var $yanelJquery144$yanelJquery144 = $yanelJquery144(this), menu = getMenu($yanelJquery144$yanelJquery144);
				clearTimeout(menu.sfTimer);
				$yanelJquery144$yanelJquery144.showSuperfishUl().siblings().hideSuperfishUl();
			},
			out = function(){
				var $yanelJquery144$yanelJquery144 = $yanelJquery144(this), menu = getMenu($yanelJquery144$yanelJquery144), o = sf.op;
				clearTimeout(menu.sfTimer);
				menu.sfTimer=setTimeout(function(){
					o.retainPath=($yanelJquery144.inArray($yanelJquery144$yanelJquery144[0],o.$yanelJquery144path)>-1);
					$yanelJquery144$yanelJquery144.hideSuperfishUl();
					if (o.$yanelJquery144path.length && $yanelJquery144$yanelJquery144.parents(['li.',o.hoverClass].join('')).length<1){over.call(o.$yanelJquery144path);}
				},o.delay);	
			},
			getMenu = function($yanelJquery144menu){
				var menu = $yanelJquery144menu.parents(['ul.',c.menuClass,':first'].join(''))[0];
				sf.op = sf.o[menu.serial];
				return menu;
			},
			addArrow = function($yanelJquery144a){ $yanelJquery144a.addClass(c.anchorClass).append($yanelJquery144arrow.clone()); };
			
		return this.each(function() {
			var s = this.serial = sf.o.length;
			var o = $yanelJquery144.extend({},sf.defaults,op);
			o.$yanelJquery144path = $yanelJquery144('li.'+o.pathClass,this).slice(0,o.pathLevels).each(function(){
				$yanelJquery144(this).addClass([o.hoverClass,c.bcClass].join(' '))
					.filter('li:has(ul)').removeClass(o.pathClass);
			});
			sf.o[s] = sf.op = o;
			
			$yanelJquery144('li:has(ul)',this)[($yanelJquery144.fn.hoverIntent && !o.disableHI) ? 'hoverIntent' : 'hover'](over,out).each(function() {
				if (o.autoArrows) addArrow( $yanelJquery144('>a:first-child',this) );
			})
			.not('.'+c.bcClass)
				.hideSuperfishUl();
			
			var $yanelJquery144a = $yanelJquery144('a',this);
			$yanelJquery144a.each(function(i){
				var $yanelJquery144li = $yanelJquery144a.eq(i).parents('li');
				$yanelJquery144a.eq(i).focus(function(){over.call($yanelJquery144li);}).blur(function(){out.call($yanelJquery144li);});
			});
			o.onInit.call(this);
			
		}).each(function() {
			var menuClasses = [c.menuClass];
			if (sf.op.dropShadows  && !($yanelJquery144.browser.msie && $yanelJquery144.browser.version < 7)) menuClasses.push(c.shadowClass);
			$yanelJquery144(this).addClass(menuClasses.join(' '));
		});
	};

	var sf = $yanelJquery144.fn.superfish;
	sf.o = [];
	sf.op = {};
	sf.IE7fix = function(){
		var o = sf.op;
		if ($yanelJquery144.browser.msie && $yanelJquery144.browser.version > 6 && o.dropShadows && o.animation.opacity!=undefined)
			this.toggleClass(sf.c.shadowClass+'-off');
		};
	sf.c = {
		bcClass     : 'sf-breadcrumb',
		menuClass   : 'sf-js-enabled',
		anchorClass : 'sf-with-ul',
		arrowClass  : 'sf-sub-indicator',
		shadowClass : 'sf-shadow'
	};
	sf.defaults = {
		hoverClass	: 'sfHover',
		pathClass	: 'overideThisToUse',
		pathLevels	: 1,
		delay		: 800,
		animation	: {opacity:'show'},
		speed		: 'normal',
		autoArrows	: true,
		dropShadows : true,
		disableHI	: false,		// true disables hoverIntent detection
		onInit		: function(){}, // callback functions
		onBeforeShow: function(){},
		onShow		: function(){},
		onHide		: function(){}
	};
	$yanelJquery144.fn.extend({
		hideSuperfishUl : function(){
			var o = sf.op,
				not = (o.retainPath===true) ? o.$yanelJquery144path : '';
			o.retainPath = false;
			var $yanelJquery144ul = $yanelJquery144(['li.',o.hoverClass].join(''),this).add(this).not(not).removeClass(o.hoverClass)
					.find('>ul').hide().css('visibility','hidden');
			o.onHide.call($yanelJquery144ul);
			return this;
		},
		showSuperfishUl : function(){
			var o = sf.op,
				sh = sf.c.shadowClass+'-off',
				$yanelJquery144ul = this.addClass(o.hoverClass)
					.find('>ul:hidden').css('visibility','visible');
			sf.IE7fix.call($yanelJquery144ul);
			o.onBeforeShow.call($yanelJquery144ul);
			$yanelJquery144ul.animate(o.animation,o.speed,function(){ sf.IE7fix.call($yanelJquery144ul); o.onShow.call($yanelJquery144ul); });
			return this;
		}
	});

})(jQuery);
