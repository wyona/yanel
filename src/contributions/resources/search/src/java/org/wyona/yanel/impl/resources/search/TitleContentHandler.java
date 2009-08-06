/**
 * Licensed to Wyona
 */
package org.wyona.yanel.impl.resources.search;

import java.io.OutputStream;
import java.io.Writer;

import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.WriteOutContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;

/**
 * Content handler decorator that only passes everything inside
 * the XHTML &lt;title/&gt; tag to the underlying handler. Note that
 * the &lt;title/&gt; tag itself is <em>not</em> passed on.
 */
public class TitleContentHandler extends ContentHandlerDecorator {

    /**
     * XHTML XPath parser.
     */
    private static final XPathParser PARSER =
        new XPathParser("xhtml", XHTMLContentHandler.XHTML);

    /**
     * The XPath matcher used to select the XHTML title contents.
     */
    //private static final Matcher MATCHER = PARSER.parse("/xhtml:html/xhtml:head/xhtml:title/descendant:node()");
    private static final Matcher MATCHER = PARSER.parse("/html/head/title/descendant:node()");

    /**
     * Creates a content handler that passes all XHTML title events to the
     * given underlying content handler.
     *
     * @param handler content handler
     */
    public TitleContentHandler(ContentHandler handler) {
        super(new MatchingContentHandler(handler, MATCHER));
    }

    /**
     * Creates a content handler that writes XHTML title character events to
     * the given writer.
     *
     * @param writer writer
     */
    public TitleContentHandler(Writer writer) {
        this(new WriteOutContentHandler(writer));
    }

    /**
     * Creates a content handler that writes XHTML title character events to
     * the given output stream using the default encoding.
     *
     * @param stream output stream
     */
    public TitleContentHandler(OutputStream stream) {
        this(new WriteOutContentHandler(stream));
    }

    /**
     * Creates a content handler that writes XHTML title character events to
     * an internal string buffer. The contents of the buffer can be retrieved
     * using the {@link #toString()} method.
     */
    public TitleContentHandler() {
        this(new WriteOutContentHandler());
    }

}
