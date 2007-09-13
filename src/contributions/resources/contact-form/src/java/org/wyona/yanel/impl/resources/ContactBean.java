package org.wyona.yanel.impl.resources;

import javax.servlet.http.HttpServletRequest;

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
        company = request.getParameter("company");
        firstName = request.getParameter("firstName");
        lastName = request.getParameter("lastName");
        address  = request.getParameter("address");
        city  = request.getParameter("zipCity");
        email  = request.getParameter("email");
        message = request.getParameter("message");
    }
}
