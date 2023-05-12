package com.example.cleanplanet.model;

public class pointsHelper {
    int _id;
    String title;
    String shortDescription;
    String address;
    Double latitude;
    Double longitude;
    String contacts;

    public pointsHelper(int _id, String title, String shortDescription, String address, Double latitude, Double longitude, String contacts) {
        this._id = _id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contacts = contacts;
    }

    public pointsHelper() {
    }

    public pointsHelper(String title, String shortDescription, String address, Double latitude, Double longitude, String contacts) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contacts = contacts;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "_id=" + _id +
                "\ntitle='" + title + '\'' +
                "\nshortDescription='" + shortDescription + '\'' +
                "\naddress='" + address + '\'' +
                "\nlatitude=" + latitude +
                "\nlongitude=" + longitude +
                "\ncontacts='" + contacts + '\'' +
                '}';
    }
}

