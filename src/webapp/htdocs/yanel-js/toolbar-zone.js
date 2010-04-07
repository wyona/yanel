/*
 Toolbar zone (depends on jquery)
*/
$(document).ready( function()
            {
                $("#yanelToolbarZone").hover( function()
                {
                    $("#yanelToolbarZoneLink").stop().animate({opacity: 1}, 1000, function()
                    {
                        $("#yanelToolbarZoneLink").fadeIn();
                    });
                }
                , function ()
                {
                    $("#yanelToolbarZoneLink").stop().fadeOut();
                });
});
