/*
 * Copyright 2012 Wyona
 */
package org.wyona.yanel.resources.registration;

/**
 *
 */
public class UserRegistrationBean {

    private String firstname;
    private String lastname;
    private String email;
    private String password;

    /**
     *
     */
    public UserRegistrationBean(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    /**
     *
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     *
     */
    public String getLastname() {
        return lastname;
    }

    /**
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     */
    public String getPassword() {
        return password;
    }
}
