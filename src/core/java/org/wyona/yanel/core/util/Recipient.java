package org.wyona.yanel.core.util;

/**
 *
 */
public class Recipient {

    private String email;
    private String pgpPublicKey;

    /**
     * @param email Email address of recipient
     */
    public Recipient(String email) {
        this.email = email;
    }

    /**
     * Get email address of recipient
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set PGP public key
     * @param pgpPublicKey PGP public key
     */
    public void setPGPPublicKey(String pgpPublicKey) {
        this.pgpPublicKey = pgpPublicKey;
    }

    /**
     *
     */
    public String getPGPPublicKey() {
        return pgpPublicKey;
    }

    /**
     *
     */
    public void setSMIME() {
    }

    /**
     *
     */
    public String getSMIME() {
        return null;
    }
}
