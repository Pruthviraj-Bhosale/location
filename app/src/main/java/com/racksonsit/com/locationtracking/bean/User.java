package com.racksonsit.com.locationtracking.bean;

/**
 * Created by RACK-BOSS on 20/09/2018.
 */

public class User {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String name;

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public String androidid;
    public String address;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name,String androidid ,String address) {
        this.name = name;
        this.androidid = androidid;
        this.address = address;
    }
}
