package com.saokanneh.auth.shared.dto;

import java.io.Serial;
import java.io.Serializable;

public class AddressDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -5483857252201749164L;

    private long id;
    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postCode;
    private String type;
    private UserDto userDetails;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDto getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDto userDetails) {
        this.userDetails = userDetails;
    }
}
