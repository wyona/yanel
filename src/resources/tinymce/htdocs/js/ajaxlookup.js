function ajaxlookup(lookin) {
  new Ajax.Updater('lookup', '', { method: 'get', onLoading: createProgressBar('lookupfiles'), onComplete: sorttable.reinit, parameters: {lookup: 'true', 'yanel.resource.viewid': 'source', lookin: lookin} });
}