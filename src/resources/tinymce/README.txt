
Upgrading TinyMCE version:
--------------------------

- Update htdocs/tinymce.jelly and src/build/dependencies.xml
- Copy/create init file: htdocs/js/tinymceinit_VERSION.js
- Add custom/additional configuration (e.g. extended_valid_elements : "iframe[src|width|height|name|align]",) to init file htdocs/js/tinymceinit_VERSION.js
- Re-Build tinymce resource (or to be sure "clean build" Yanel)
- Test new version: http://127.0.0.1:8080/yanel/from-scratch-realm/en/index.html?yanel.toolbar=on
