package com.danielj.springadsandroid.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DTO class for Ad
 *
 * @author Daniel Johansson
 */
public class AdDto implements Parcelable {
    /**
     * Id of Ad
     */
    private long id;

    /**
     * Headline of Ad
     */
    private String headline;

    /**
     * Description of Ad
     */
    private String description;

    /**
     * Price of Ad
     */
    private long price;

    /**
     * Date when Ad was created
     */
    private String dateCreated;

    /**
     * Date when Ad is ending
     */
    private String dateEnding;

    /**
     * Name of category of the Ad
     */
    private String categoryName;

    /**
     * Type of Ad (SELL/BUY)
     */
    private String type;

    /**
     * Image for the Ad
     */
    private byte[] image;

    /**
     * First name of the user who created the Ad
     */
    private String firstName;

    /**
     * Last name of the user who created the Ad
     */
    private String lastName;

    /**
     * Email of the user who created the Ad
     */
    private String email;

    /**
     * Phone number of the user who created the Ad
     */
    private String phoneNumber;

    /**
     * Street address of the user who created the Ad
     */
    private String streetAddress;

    /**
     * City of the user who created the Ad
     */
    private String city;

    /**
     * Zip code of the user who created the Ad
     */
    private String zipcode;

    /**
     * State of the user who created the Ad
     */
    private String state;

    /**
     * Status of the Ad
     */
    private String active;

    /**
     * User id of the user who created the Ad
     */
    private long userId;

    /* Constructors */

    public AdDto() {
    }

    public AdDto(long id, String headline, String description, long price, String dateCreated,
                 String dateEnding, String categoryName, String firstName,
                 String lastName, String email, String phoneNumber, byte[] image, String streetAddress,
                 String city, String zipcode, String state, String active) {
        this.id = id;
        this.headline = headline;
        this.description = description;
        this.price = price;
        this.dateCreated = dateCreated;
        this.dateEnding = dateEnding;
        this.categoryName = categoryName;
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.zipcode = zipcode;
        this.state = state;
        this.active = active;
    }

    /* Getters and setters */

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

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

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateEnding() {
        return dateEnding;
    }

    public void setDateEnding(String dateEnding) {
        this.dateEnding = dateEnding;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Writes to Parcel
     *
     * @param out
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(headline);
        out.writeString(description);
        out.writeLong(price);
        out.writeString(dateCreated);
        out.writeString(dateEnding);
        out.writeString(categoryName);
        out.writeByteArray(image);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeString(email);
        out.writeString(phoneNumber);
        out.writeString(streetAddress);
        out.writeString(city);
        out.writeString(zipcode);
        out.writeString(state);
        out.writeString(active.toString());
        out.writeString(type);
        out.writeLong(userId);
    }

    public static final Parcelable.Creator<AdDto> CREATOR = new Parcelable.Creator<AdDto>() {
        public AdDto createFromParcel(Parcel in) {
            return new AdDto(in);
        }

        public AdDto[] newArray(int size) {
            return new AdDto[size];
        }
    };

    /**
     * Constructor used by Parcelable.Creator
     */
    private AdDto(Parcel in) {
        id = in.readLong();
        headline = in.readString();
        description = in.readString();
        price = in.readLong();
        dateCreated = in.readString();
        dateEnding = in.readString();
        categoryName = in.readString();
        image = in.createByteArray();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        streetAddress = in.readString();
        city = in.readString();
        zipcode = in.readString();
        state = in.readString();
        active = in.readString();
        type = in.readString();
        userId = in.readLong();
    }

    public int describeContents() {
        return 0;
    }
}
