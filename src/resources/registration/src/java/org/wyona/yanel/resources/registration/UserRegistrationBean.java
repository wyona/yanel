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

    private boolean preAuthenticated = false;
    private String preAuthUsername = null;

    private boolean confirmedByAdministrator = false;
    private String adminConfirmationKey = null;

    /**
     * @param firstname First name
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
     * Set flag whether user is pre authenticated
     */
    public void setPreAuthenticated(boolean preAuthenticated) {
        this.preAuthenticated = preAuthenticated;
    }

    /**
     * Check whether user is pre authenticated
     * @return true when user is pre authenticated
     */
    public boolean isPreAuthenticated() {
        return preAuthenticated;
    }

    /**
     * Set pre authenticated username
     * @param name Username
     */
    public void setPreAuthenticatedUsername(String name) {
        this.preAuthUsername = name;
    }

    /**
     * Get pre authenticated username
     * @return pre authenticated username when pre authenticated and otherwise null
     */
    public String getPreAuthenticatedUsername() {
        return preAuthUsername;
    }

    /**
     * Set flag whether administrator has confirmed registration request
     */
    public void setAdministratorConfirmed(boolean hasConfirmed) {
        this.confirmedByAdministrator = hasConfirmed;
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
    public void setAdministratorConfirmationKey(String key) {
        this.adminConfirmationKey = key;
    }

    /**
     *
     */
    public String getAdministratorConfirmationKey() {
        return adminConfirmationKey;
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
     * Get first name
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Set first name
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Get last name
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Set last name
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
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
