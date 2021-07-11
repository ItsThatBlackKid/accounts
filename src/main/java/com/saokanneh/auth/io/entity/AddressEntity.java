package com.saokanneh.auth.io.entity;

import com.saokanneh.auth.shared.dto.AddressDto;
import com.saokanneh.auth.shared.dto.UserDto;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity(name = "addresses")
public class AddressEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -3220022242740881020L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true, length = 30)
    private String addressId;

    @Column(nullable = false, length = 15)
    private String city;
    @Column(nullable = false, length = 50)
    private String country;
    @Column(nullable = false, length = 120)
    private String streetName;
    @Column(nullable = false, length = 8)
    private String postCode;
    @Column(nullable = false, length = 20)
    private String type;

    @ManyToOne
    @JoinColumn(name="users_id")
    private UserEntity userDetails;

    public long getId() {
        return id;
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

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }
}
