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
    private String streetName;
    private String zip;
    private String city;
    private String phone;

    private boolean confirmedByAdministrator = false;

    /**
     *
     */
    public UserRegistrationBean(String gender, String firstname, String lastname, String email, String password, String city, String phone) {
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
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    /**
     *
     */
    public boolean hasAdministratorConfirmedRegistration() {
        return confirmedByAdministrator;
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
     * Set street name
     * @param name Street name
     */
    public void setStreetName(String name) {
        this.streetName = name;
    }

    /**
     * Get street name
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Set ZIP code
     * @param number ZIP code
     */
    public void setZipCode(String number) {
        this.zip = number;
    }

    /**
     * Get ZIP code
     */
    public String getZipCode() {
        return zip;
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
