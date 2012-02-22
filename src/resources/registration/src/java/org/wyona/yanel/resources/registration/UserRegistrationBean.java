/*
 * Copyright 2012 Wyona
 */
package org.wyona.yanel.resources.registration;

/**
 * User registration bean
 */
public class UserRegistrationBean {

    private String uuid;
    private String gender;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String city;
    private String phone;

    /**
     *
     */
    public UserRegistrationBean(String uuid, String gender, String firstname, String lastname, String email, String password, String city, String phone) {
        this.uuid = uuid;
        this.gender = gender;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.city = city;
        this.phone = phone;
    }

    /**
     *
     */
    public String getUUID() {
        return uuid;
    }

    /**
     *
     */
    public String getGender() {
        return gender;
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

    /**
     *
     */
    public String getCity() {
        return city;
    }

    /**
     *
     */
    public String getPhone() {
        return phone;
    }
}
