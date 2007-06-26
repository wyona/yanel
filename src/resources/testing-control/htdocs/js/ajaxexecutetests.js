function ajaxexecutetests() {
  var params = Form.serialize(document.getElementsByTagName("form")[0]);
  new Ajax.Updater('ajaxreplace', ' ', { method: 'post', onLoading: createProgressBar('yanelprogressbarph'), onSuccess: ajaxeshowprogress(), parameters: params });
}

function ajaxeshowprogress () {
  new PeriodicalExecuter(function(pe1) {
    if (document.getElementById("testDone") != null) {
      stopProgressBar('yanelprogressbarph')
      pe1.stop();
    } else {
      new Ajax.Updater('ajaxreplace', ' ', { method: 'get', parameters: { 'ajaxshowprogress': 'true', 'yanel.resource.viewid': 'source'} });
    }
  }, 1);
}