var i = 0;
var flag;
var yanelProgressBarActive;

function startMoveProgressBar () {
  yanelProgressBarActive = window.setInterval('moveProgressBar()', 1);
}

function moveProgressBar () {
  if (i == 0 || flag == "a") {
    i = i + 1;
    flag = "a";
  }
  if (i >= 190 || flag == "d") {
    i = i - 1;
    flag = "d";
  }
  if ($('yanelprogressbarindicator') != null) {
    $('yanelprogressbarindicator').style.left = i-80 + '%';
  } else {
    window.clearInterval(yanelProgressBarActive); 
  }
}

function createProgressBar (target) {
  $(target).update('<div id="yanelprogressbar"><div id="yanelprogressbarindicator">|||||||||</div></div>');
  startMoveProgressBar();
}

function stopProgressBar (target) {
  $(target).remove();
}