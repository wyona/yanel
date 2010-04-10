$(document).ready(function(){
	$('.gallery').galleryScroll({
		btPrev: 'a.link-prev',
		btNext: 'a.link-next',
		holderList: '.g1',
		scrollElParent: '> ul',
		scrollEl: '> li',
		slideNum: 'div.swicher',
		duration : 1000,
		circleSlide: true,
		autoSlide: 5000
	});
});