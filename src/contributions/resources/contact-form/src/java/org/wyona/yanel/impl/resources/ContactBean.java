package org.wyona.yanel.impl.resources;

import javax.servlet.http.HttpServletRequest;
import org.wyona.yanel.core.util.HttpServletRequestHelper;

public class ContactBean {
    String company, lastName, firstName, address, city, email, message;
    
    public String getAddress() {
        return address;
    }
    public String getEmail() {
        return email;
    }
    public String getCompany() {
        return company;
    }
    public String getMessage() {
        return message;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getCity() {
        return city;
    }
    public String getLastName() {
        return lastName;
    }
    public ContactBean(HttpServletRequest request) {
        company = HttpServletRequestHelper.getParameter(request, "company");
        firstName = HttpServletRequestHelper.getParameter(request, "firstName");
        lastName = HttpServletRequestHelper.getParameter(request, "lastName");
        address  = HttpServletRequestHelper.getParameter(request, "address");
        city  = HttpServletRequestHelper.getParameter(request, "zipCity");
        email  = HttpServletRequestHelper.getParameter(request, "email");
        message = HttpServletRequestHelper.getParameter(request, "message");
    }
}
