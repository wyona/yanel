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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.MapFactory;

import org.wyona.yanel.util.ResourceAttributeHelper;

import org.apache.log4j.Category;

/**
 *
 */
public class YanelServlet extends HttpServlet {

    private static Category log = Category.getInstance(YanelServlet.class);

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
        View view = null;

        StringBuffer sb = new StringBuffer("");

        sb.append("<?xml version=\"1.0\"?>");

        String servletContextRealPath = config.getServletContext().getRealPath("/");
        sb.append("<yanel servlet-context-real-path=\""+servletContextRealPath+"\" xmlns=\"http://www.wyona.org/yanel/1.0\">");

        sb.append("<request uri=\""+request.getRequestURI()+"\" servlet-path=\""+request.getServletPath()+"\"/>");

	HttpSession session = request.getSession(true);
        sb.append("<session id=\""+session.getId()+"\">");
	Enumeration enum = session.getAttributeNames();
        if (!enum.hasMoreElements()) {
            sb.append("<no-attributes/>");
        }
        while (enum.hasMoreElements()) {
            String name = (String)enum.nextElement();
            String value = session.getAttribute(name).toString();
            sb.append("<attribute name=\"" + name + "\">" + value + "</attribute>");
        }
        sb.append("</session>");

        MapFactory mf = MapFactory.newInstance();
        Map map = mf.newMap();
        String rti = map.getResourceTypeIdentifier(new Path(request.getServletPath()));
        if (rti != null) {
            ResourceTypeDefinition rtd = ResourceTypeRegistry.getResourceTypeDefinition(rti);
            sb.append("<resource-type-identifier namespace=\"" + rtd.getResourceTypeNamespace() + "\" local-name=\"" + rtd.getResourceTypeLocalName() + "\"/>");

            try {
                Resource res = ResourceTypeRegistry.newResource(rti);
                res.setRTD(rtd);
                if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
                    sb.append("<resource>View Descriptors: " + ((ViewableV1) res).getViewDescriptors() + "</resource>");
                    String viewId = null;
                    view = ((ViewableV1) res).getView(request, viewId);
                } else {
                    sb.append("<resource>" + res.getClass().getName() + " is not viewable!</resource>");
                }
            } catch(Exception e) {
                sb.append("<exception>" + e + "</exception>");
                log.error(e.getMessage(), e);
            }
        } else {
            sb.append("<no-resource-type-identifier-found servlet-path=\""+request.getServletPath()+"\"/>");
        }

        sb.append("</yanel>");


        if (view != null) {
            response.setContentType(view.getMimeType());
            java.io.InputStream is = view.getInputStream();

            byte buffer[] = new byte[8192];
            int bytesRead;
            bytesRead = is.read(buffer);
	    if (bytesRead == -1) {
                response.setContentType("text/plain");
                PrintWriter writer = response.getWriter();
                writer.print("No content!");
                return;
            }
            java.io.OutputStream os = response.getOutputStream();
            os.write(buffer, 0, bytesRead);
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return;
        } else {
            response.setContentType("application/xml");
            PrintWriter writer = response.getWriter();
            writer.print(sb);
            return;
        }
    }

    /**
     *
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        response.setContentType("application/xhtml+xml");

        writer.println("<html>");
        writer.println("<body>");

        String value = request.getParameter("yanel.resource.usecase");
        if (value != null && value.equals("save")) {
           writer.println("Save data ...");
           log.error("Save data ...");
        } else {
           writer.println("No parameter yanel.resource.usecase!");
           log.error("No parameter yanel.resource.usecase!");
        }

        writer.println("</body>");
        writer.println("</html>");
    }

    /**
     * TODO: Reuse code doPost resp. share code with doPut
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuffer sb = new StringBuffer("");

        String value = request.getParameter("yanel.resource.usecase");
        if (value != null && value.equals("save")) {
            log.error("Save data ...");
            java.io.InputStream in = request.getInputStream();

            byte buffer[] = new byte[8192];
            int bytesRead;
            bytesRead = in.read(buffer);

/*
	     if (bytesRead == -1) {
                 response.setContentType("text/plain");
                 PrintWriter writer = response.getWriter();
                 writer.print("No content!");
                 return;
             }
*/

/*
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            out.write(buffer, 0, bytesRead);
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            log.error("Received Data: " + out.toString());
*/

            Resource res = getResource(request);
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "1")) {
                String contentType = request.getContentType();
                log.error("Content-Type: " + contentType);

                java.io.OutputStream out = ((ModifiableV1) res).getOutputStream(new Path(request.getServletPath()));
                out.write(buffer, 0, bytesRead);
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<html>");
                sb.append("<body>");
                sb.append("<p>Data has been saved ...</p>");
                sb.append("</body>");
                sb.append("</html>");
                response.setContentType("application/xhtml+xml");
            } else {
                log.error("<resource>" + res.getClass().getName() + " is not modifiable!</resource>");
                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<html>");
                sb.append("<body>");
                sb.append("<resource>" + res.getClass().getName() + " is not modifiable!</resource>");
                sb.append("</body>");
                sb.append("</html>");
                response.setContentType("application/xhtml+xml");
            }
	} else if (value != null && value.equals("checkin")) {
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<p>Checkin data ...</p>");
            sb.append("</body>");
            sb.append("</html>");
            response.setContentType("application/xhtml+xml");

            log.error("Checkin data ...");
            // TODO: Implement checkin ...
        } else {
            log.error("No parameter yanel.resource.usecase!");

            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<p>No parameter yanel.resource.usecase!</p>");
            sb.append("</body>");
            sb.append("</html>");
            response.setContentType("application/xhtml+xml");
        }


        PrintWriter writer = response.getWriter();
        writer.print(sb);
    }

    /**
     *
     */
    private Resource getResource(HttpServletRequest request) {
        MapFactory mf = MapFactory.newInstance();
        Map map = mf.newMap();
        String rti = map.getResourceTypeIdentifier(new Path(request.getServletPath()));
        if (rti != null) {
            ResourceTypeDefinition rtd = ResourceTypeRegistry.getResourceTypeDefinition(rti);

            try {
                Resource res = ResourceTypeRegistry.newResource(rti);
                res.setRTD(rtd);
                return res;
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            log.error("<no-resource-type-identifier-found servlet-path=\""+request.getServletPath()+"\"/>");
            return null;
        }
    }
}
