function ajaxlookup(resourcetype, lookin) {
  new Ajax.Updater('lookup', '', { method: 'get', parameters: {lookup: 'true', 'yanel.resource.viewid': 'source', lookin: lookin, 'resource-type': resourcetype} });
}