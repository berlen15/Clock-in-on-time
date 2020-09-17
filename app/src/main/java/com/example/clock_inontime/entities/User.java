package com.example.clock_inontime.entities;

import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String userid;
    private String name;
    private String surname;
    private String phone;
    private String city;
    private String photoURL;

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    private String dni;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    private String company;
    public User(String email, String password, String userid, String name, String surname, String dni, String company, String phone, String city, ArrayList<TimeCard> timeCard) {
        this.email = email;
        this.dni=dni;
        this.password = password;
        this.userid = userid;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.city = city;
        this.company=company;
        this.timeCard = timeCard;
    }


    private ArrayList<TimeCard> timeCard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public ArrayList<TimeCard> getTimeCard() {
        return timeCard;
    }

    public void setTimeCard(ArrayList<TimeCard> timeCard) {
        this.timeCard = timeCard;
    }


    public User(){}
    public User(String userid,String email, String password, ArrayList<TimeCard> timeCard) {
        this.email = email;
        this.password = password;
        this.userid = userid;
        this.timeCard = timeCard;
    }






}
