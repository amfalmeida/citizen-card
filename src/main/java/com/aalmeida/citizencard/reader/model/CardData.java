/*
 * Copyright (c) 2018 Alexandre Almeida.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 *
 */
package com.aalmeida.citizencard.reader.model;

public class CardData {

    private final long id;

    private final String givenName;
    private final String surname;
    private final String initials;

    private final String dateOfBirth;
    private final String gender;

    private final String country;
    private final String locale;

    private final String cardNumber;
    private final String civilianIdNumber;
    private final String healthNumber;
    private final String taxNumber;
    private final String socialSecurityNumber;

    private final String accidentalIndications;

    private final String validityBeginDate;
    private final String validityEndDate;

    private final byte[] picture;

    private CardData(Builder builder) {
        id = builder.id;
        givenName = builder.givenName;
        surname = builder.surname;
        initials = builder.initials;
        dateOfBirth = builder.dateOfBirth;
        gender = builder.gender;
        country = builder.country;
        locale = builder.locale;
        cardNumber = builder.cardNumber;
        civilianIdNumber = builder.civilianIdNumber;
        healthNumber = builder.healthNumbe;
        taxNumber = builder.taxNumber;
        socialSecurityNumber = builder.socialSecurityNumber;
        accidentalIndications = builder.accidentalIndications;
        validityBeginDate = builder.validityBeginDate;
        validityEndDate = builder.validityEndDate;
        picture = builder.picture;
    }

    public long getId() {
        return id;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public String getInitials() {
        return initials;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getLocale() {
        return locale;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCivilianIdNumber() {
        return civilianIdNumber;
    }

    public String getHealthNumber() {
        return healthNumber;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public String getAccidentalIndications() {
        return accidentalIndications;
    }

    public String getValidityBeginDate() {
        return validityBeginDate;
    }

    public String getValidityEndDate() {
        return validityEndDate;
    }

    public byte[] getPicture() {
        return picture;
    }

    @Override
    public String toString() {
        return String.format("CardData{id=%s, givenName='%s', surname='%s', initials='%s', dateOfBirth='%s', gender='%s', country='%s', locale='%s', cardNumber='%s', civilianIdNumber='%s', healthNumber='%s', taxNumber='%s', socialSecurityNumber='%s', accidentalIndications='%s', validityBeginDate='%s', validityEndDate='%s'}",
                id, givenName, surname, initials, dateOfBirth, gender, country, locale, cardNumber, civilianIdNumber,
                healthNumber, taxNumber, socialSecurityNumber, accidentalIndications, validityBeginDate, validityEndDate);
    }

    public static final class Builder {
        private final long id;
        private String givenName;
        private String surname;
        private String initials;
        private String dateOfBirth;
        private String gender;
        private String country;
        private String locale;
        private String cardNumber;
        private String civilianIdNumber;
        private String healthNumbe;
        private String taxNumber;
        private String socialSecurityNumber;
        private String accidentalIndications;
        private String validityBeginDate;
        private String validityEndDate;
        private byte[] picture;

        public Builder(long id) {
            this.id = id;
        }

        public Builder setGivenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setInitials(String initials) {
            this.initials = initials;
            return this;
        }

        public Builder setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setLocale(String locale) {
            this.locale = locale;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setCivilianIdNumber(String civilianIdNumber) {
            this.civilianIdNumber = civilianIdNumber;
            return this;
        }

        public Builder setHealthNumbe(String healthNumbe) {
            this.healthNumbe = healthNumbe;
            return this;
        }

        public Builder setTaxNumber(String taxNumber) {
            this.taxNumber = taxNumber;
            return this;
        }

        public Builder setSocialSecurityNumber(String socialSecurityNumber) {
            this.socialSecurityNumber = socialSecurityNumber;
            return this;
        }

        public Builder setAccidentalIndications(String accidentalIndications) {
            this.accidentalIndications = accidentalIndications;
            return this;
        }

        public Builder setValidityBeginDate(String validityBeginDate) {
            this.validityBeginDate = validityBeginDate;
            return this;
        }

        public Builder setValidityEndDate(String validityEndDate) {
            this.validityEndDate = validityEndDate;
            return this;
        }

        public Builder setPicture(byte[] picture) {
            this.picture = picture;
            return this;
        }

        public CardData build() {
            return new CardData(this);
        }
    }
}
