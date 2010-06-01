tinyMCE.init({
	mode : "textareas",
	theme : "simple",
	//theme : "advanced",
	plugins : "fullpage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left",
	theme_advanced_statusbar_location : "bottom",
	theme_advanced_resizing : true,
	entity_encoding : "numeric",
	file_browser_callback : 'yanelLookupResource',
});

function yanelLookupResource(field_name, url, type, win) {

    // alert("Field_Name: " + field_name + "\nURL: " + url + "\nType: " + type + "\nWin: " + win); // debug/testing

    /* If you work with sessions in PHP and your client doesn't accept cookies you might need to carry
       the session name and session ID in the request string (can look like this: "?PHPSESSID=88p0n70s9dsknra96qhuk6etm5").
       These lines of code extract the necessary parameters and add them back to the filebrowser URL again. */

    if (window.location.pathname.charAt(window.location.pathname.length) != "/") {
        path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/") + 1) 
    }
    var cmsURL = path + BACK2REALM + "usecases/tinymce-lookup.html" // script URL
    if (cmsURL.indexOf("?") < 0) {
        //add the type as the only query parameter
        cmsURL = cmsURL + "?type=" + type;
    }
    else {
        //add the type as an additional query parameter
        // (PHP session ID is now included if there is one at all)
        cmsURL = cmsURL + "&type=" + type;
    }
    // INFO: Suppress the toolbar
    cmsURL = cmsURL + "&yanel.target-back2realm=" + BACK2REALM + "&yanel.toolbar=suppress";

    tinyMCE.activeEditor.windowManager.open({
        file : cmsURL,
        title : 'Lookup',
        width : 420,  // Your dimensions may differ - toy around with them!
        height : 400,
        resizable : "yes",
        inline : "yes",  // This parameter only has an effect if you use the inlinepopups plugin!
        close_previous : "no"
    }, {
        window : win,
        input : field_name
    });
    return false;
}
