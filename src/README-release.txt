Checklist for releasing a new Yanel version:
- devise a new version string (e.g. "1.1")
- identify the revision: use `svnversion` on $YANEL_HOME for now (see http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=5341 for a more automated alternative)
- publish all artifacts: source snapshots for Windows and Unix, binary snapshots for Windows (installer) and Unix:
  - run %YANEL_HOME%\src\release <version string> <revision number>
  - copy all artifacts to the web server, using WinSCP for example
  - create new empty file resources locally using the Yanel toolbar
  - svnignore the empty files
  - change their size to the correct ones
  - move the files to the correct place on the server
- publish Yanel Maven artifacts?
- update release-specific Yanel website pages:
 (you can simply go there, log in and use Yulup for direct editing)
  - Unix download: http://yanel.wyona.org/en/download/unix.html
  - Windows download: http://yanel.wyona.org/en/download/windows.html
  - [TODO: not sure how to edit this: ] news: http://yanel.wyona.org/news/index.html
  - home: http://yanel.wyona.org/ (all available languages!)
  - features?
 ...and *don't forget to commit* the changes!
- update the website itself on the server:
  - (if needed: ) update the Yanel engine
  - (if needed: ) update the resources
  - update the "yanel-website" realm
- announce the release: TODO
  - Freshmeat?
  - theserverside.com?
