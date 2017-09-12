package com.danielj.springadsandroid.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DTO class for User
 *
 * @author Daniel Johansson
 */
public class UserDto implements Parcelable {
    /**
     * Id of the User
     */
    private long id;

    /**
     * First name of the User
     */
    private String firstName;

    /**
     * Last name of the User
     */
    private String lastName;

    /**
     * Email of the User
     */
    private String email;

    /**
     * Phone number of the User
     */
    private String phoneNumber;

    /**
     * Street address of the User
     */
    private String streetAddress;

    /**
     * City of the User
     */
    private String city;

    /**
     * Zip code of the User
     */
    private String zipcode;

    /**
     * State of the User
     */
    private String state;

    /**
     * Google id for the User
     */
    private String googleId;

    /* Constructors */

    public UserDto() {
    }

    public UserDto(long id, String firstName, String lastName, String email, String phoneNumber,
                   String streetAddress, String city, String zipcode, String state, String googleId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.zipcode = zipcode;
        this.state = state;
        this.googleId = googleId;
    }

    /* Getters and setters */

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Writes to parcel
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeString(phoneNumber);
        out.writeString(streetAddress);
        out.writeString(city);
        out.writeString(zipcode);
        out.writeString(state);
        out.writeString(googleId);
    }

    public static final Parcelable.Creator<UserDto> CREATOR = new Parcelable.Creator<UserDto>() {
        public UserDto createFromParcel(Parcel in) {
            return new UserDto(in);
        }

        public UserDto[] newArray(int size) {
            return new UserDto[size];
        }
    };

    /**
     * Constructor used by Parcelable.Creator
     */
    private UserDto(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        streetAddress = in.readString();
        city = in.readString();
        zipcode = in.readString();
        state = in.readString();
        googleId = in.readString();
    }

    public int describeContents() {
        return 0;
    }
}
