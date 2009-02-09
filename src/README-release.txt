Checklist for releasing a new Yanel version:
- devise a new version string (e.g. "1.1")
- identify the revision: use `svncommit` on $YANEL_HOME for now (see http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=5341 for a more automated alternative)
- build all artifacts: source snapshots for Windows and Unix, binary snapshots for Windows (installer) and Unix:
  - run %YANEL_HOME%\src\release <version string> <revision number>
- publish Yanel Maven artifacts?
- update release-specific Yanel website pages:
 (you can simply go there, log in and use Yulup for direct editing)
  - Unix download: http://yanel.wyona.org/en/download/unix.html
  - Windows download: http://yanel.wyona.org/en/download/windows.html
  - news: http://yanel.wyona.org/news/index.html
  - home: http://yanel.wyona.org/
  - features?
 ...and *don't forget to commit* the changes!
- announce the release: TODO
  - Freshmeat?
  - theserverside.com?
