/**
 * 
 */
package com.aalmeida.citizencard.entities;

/**
 * The Class CitizenCardData.
 *
 * @author Alexandre
 */
public class CitizenCardData {

    private String token;
    private String firstname;
    private String surname;
    private String nif;

    /**
     * Gets the token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param pToken
     *            the token to set
     */
    public void setToken(String pToken) {
        token = pToken;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstname;
    }

    /**
     * Sets the first name.
     *
     * @param pFirstname
     *            the new first name
     */
    public void setFirstName(String pFirstname) {
        firstname = pFirstname;
    }

    /**
     * Gets the surname.
     *
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname.
     *
     * @param pSurname
     *            the new surname
     */
    public void setSurname(String pSurname) {
        surname = pSurname;
    }

    /**
     * @return the nif
     */
    public String getNif() {
        return nif;
    }

    /**
     * @param pNif the nif to set
     */
    public void setNif(String pNif) {
        nif = pNif;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CitizenCardData [token=" + token + ", firstname=" + firstname + ", surname=" + surname + ", nif=" + nif
                + "]";
    }
}
