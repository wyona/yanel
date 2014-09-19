/**
 * plugin.js
 *
 * Copyright, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://www.tinymce.com/license
 * Contributing: http://www.tinymce.com/contributing
 */

/*global tinymce:true */

tinymce.PluginManager.add('help', function(editor) {

	function buildListItems(inputList, itemCallback, startItems) {
		function appendItems(values, output) {
			output = output || [];

			tinymce.each(values, function(item) {
				var menuItem = {text: item.text || item.title};

				if (item.menu) {
					menuItem.menu = appendItems(item.menu);
				} else {
					menuItem.value = item.value;
					itemCallback(menuItem);
				}

				output.push(menuItem);
			});

			return output;
		}

		return appendItems(inputList, startItems || []);
	}

	function createImageList(callback) {
		return function() {
			var helpList = editor.settings.help_list;

			if (typeof(helpList) == "string") {
				tinymce.util.XHR.send({
					url: helpList,
					success: function(text) {
						callback(tinymce.util.JSON.parse(text));
					}
				});
			} else if (typeof(helpList) == "function") {
				helpList(callback);
			} else {
				callback(helpList);
			}
		};
	}

	function showDialog(helpList) {
		var win, data = {}, dom = editor.dom, imgElm = editor.selection.getNode();
		var width, height, helpListCtrl, classListCtrl;

		function recalcSize() {
			var widthCtrl, heightCtrl, newWidth, newHeight;

			widthCtrl = win.find('#width')[0];
			heightCtrl = win.find('#height')[0];

			if (!widthCtrl || !heightCtrl) {
				return;
			}

			newWidth = widthCtrl.value();
			newHeight = heightCtrl.value();

			if (win.find('#constrain')[0].checked() && width && height && newWidth && newHeight) {
				if (width != newWidth) {
					newHeight = Math.round((newWidth / width) * newHeight);
					heightCtrl.value(newHeight);
				} else {
					newWidth = Math.round((newHeight / height) * newWidth);
					widthCtrl.value(newWidth);
				}
			}

			width = newWidth;
			height = newHeight;
		}

		function onSubmitForm() {
			function waitLoad(imgElm) {
				function selectImage() {
					imgElm.onload = imgElm.onerror = null;

					if (editor.selection) {
						editor.selection.select(imgElm);
						editor.nodeChanged();
					}
				}

				imgElm.onerror = selectImage;
			}

			updateStyle();
			recalcSize();

			data = tinymce.extend(data, win.toJSON());

			if (!data.alt) {
				data.alt = '';
			}

			if (data.width === '') {
				data.width = null;
			}

			if (data.height === '') {
				data.height = null;
			}

			if (!data.style) {
				data.style = null;
			}

			// Setup new data excluding style properties
			data = {
				src: data.src,
				alt: data.alt,
				width: data.width,
				height: data.height,
				style: data.style,
				"class": data["class"]
			};

			editor.undoManager.transact(function() {
				if (!data.src) {
					if (imgElm) {
						dom.remove(imgElm);
						editor.focus();
						editor.nodeChanged();
					}

					return;
				}

				if (!imgElm) {
					data.id = '__mcenew';
					editor.focus();
					editor.selection.setContent(dom.createHTML('img', data));
					imgElm = dom.get('__mcenew');
					dom.setAttrib(imgElm, 'id', null);
				} else {
					dom.setAttribs(imgElm, data);
				}

				waitLoad(imgElm);
			});
		}

		function removePixelSuffix(value) {
			if (value) {
				value = value.replace(/px$/, '');
			}

			return value;
		}

		function srcChange(e) {
			var meta = e.meta || {};

			if (helpListCtrl) {
				helpListCtrl.value(editor.convertURL(this.value(), 'src'));
			}

			tinymce.each(meta, function(value, key) {
				win.find('#' + key).value(value);
			});
		}

		width = dom.getAttrib(imgElm, 'width');
		height = dom.getAttrib(imgElm, 'height');

		if (imgElm.nodeName == 'IMG' && !imgElm.getAttribute('data-mce-object') && !imgElm.getAttribute('data-mce-placeholder')) {
			data = {
				src: dom.getAttrib(imgElm, 'src'),
				alt: dom.getAttrib(imgElm, 'alt'),
				"class": dom.getAttrib(imgElm, 'class'),
				width: width,
				height: height
			};
		} else {
			imgElm = null;
		}

		if (helpList) {
			helpListCtrl = {
				type: 'listbox',
				label: 'Image list',
				values: buildListItems(
					helpList,
					function(item) {
						item.value = editor.convertURL(item.value || item.url, 'src');
					},
					[{text: 'None', value: ''}]
				),
				value: data.src && editor.convertURL(data.src, 'src'),
				onselect: function(e) {
					var altCtrl = win.find('#alt');

					if (!altCtrl.value() || (e.lastControl && altCtrl.value() == e.lastControl.text())) {
						altCtrl.value(e.control.text());
					}

					win.find('#src').value(e.control.value()).fire('change');
				},
				onPostRender: function() {
					helpListCtrl = this;
				}
			};
		}

		if (editor.settings.help_class_list) {
			classListCtrl = {
				name: 'class',
				type: 'listbox',
				label: 'Class',
				values: buildListItems(
					editor.settings.help_class_list,
					function(item) {
						if (item.value) {
							item.textStyle = function() {
								return editor.formatter.getCssText({inline: 'img', classes: [item.value]});
							};
						}
					}
				)
			};
		}

		// General settings shared between simple and advanced dialogs
/*
		var generalFormItems = [
			{
				name: 'src',
				type: 'filepicker',
				filetype: 'help',
				label: 'Source',
				autofocus: true,
				onchange: srcChange
			},
			helpListCtrl
		];

		generalFormItems.push(classListCtrl);
*/

		function updateStyle() {
			function addPixelSuffix(value) {
				if (value.length > 0 && /^[0-9]+$/.test(value)) {
					value += 'px';
				}

				return value;
			}

			if (!editor.settings.help_advtab) {
				return;
			}

			var data = win.toJSON();
			var css = dom.parseStyle(data.style);

			delete css.margin;
			css['margin-top'] = css['margin-bottom'] = addPixelSuffix(data.vspace);
			css['margin-left'] = css['margin-right'] = addPixelSuffix(data.hspace);
			css['border-width'] = addPixelSuffix(data.border);

			win.find('#style').value(dom.serializeStyle(dom.parseStyle(dom.serializeStyle(css))));
		}

			// Simple default dialog
			win = editor.windowManager.alert("TinyMCE Version 4.1.5");
/*
			win = editor.windowManager.open({
				title: 'Help',
                                url: 'help.html',
                                width: 50,
                                height: 50
				//data: data,
				//body: generalFormItems,
				//onSubmit: onSubmitForm
			});
*/
	}

	editor.addButton('help', {
		icon: 'help',
		tooltip: 'Help',
		onclick: createImageList(showDialog),
		stateSelector: 'img:not([data-mce-object],[data-mce-placeholder])'
	});

	editor.addMenuItem('help', {
		icon: 'help',
		text: 'POINTInsert help',
		onclick: createImageList(showDialog),
		context: 'insert',
		prependToContext: true
	});

	editor.addCommand('mceImage', createImageList(showDialog));
});
