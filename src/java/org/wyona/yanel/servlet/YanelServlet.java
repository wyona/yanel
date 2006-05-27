package org.wyona.yanel.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 */
public class YanelServlet extends HttpServlet {

    private ServletConfig config;

    /**
     *
     */
    public void init(ServletConfig config) {
        this.config = config;
    }

    /**
     *
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/xml");


        writer.println("<?xml version=\"1.0\"?>");


        String servletContextRealPath = config.getServletContext().getRealPath("/");
        writer.println("<yanel servlet-context-real-path=\""+servletContextRealPath+"\" xmlns=\"http://www.wyona.org/yanel/1.0\">");

	HttpSession session = request.getSession(true);
        writer.println("<session id=\""+session.getId()+"\">");
	Enumeration e = session.getAttributeNames();
        if (!e.hasMoreElements()) {
            writer.println("<no-attributes/>");
        }
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            String value = session.getAttribute(name).toString();
            writer.println("<attribute name=\"" + name + "\">" + value + "</attribute>");
        }
        writer.println("</session>");

        writer.println("</yanel>");
    }

    /**
     *
     */
/*
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("text/html");

        writer.println("<html>");
        writer.println("<body>");

        String name = request.getParameter("name");
        if (name != null && name.length() > 0) {
           String value = request.getParameter("value");
	   HttpSession session = request.getSession(true);
           session.setAttribute(name, value);

           writer.println("Session attribute has been set: " + name + " = " + value);
        } else {
           writer.println("No session attribute has been set!");
        }

        writer.println("</body>");
        writer.println("</html>");
    }
*/
}
