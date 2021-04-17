//user field file, used for profile/preferences
package com.example.Savor;

public class User {

    public String name, age, email;

    public User(String name, String age, String email, String vegan, String recipesDisplayed){

    }
    public User(String name, String age, String email){
        this.name = name;
        this.age = age;
        this.email = email;
    }
}