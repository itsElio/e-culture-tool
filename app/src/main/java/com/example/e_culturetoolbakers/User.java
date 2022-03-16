package com.example.e_culturetoolbakers;

public class User {

    public String username, email, type;

    public User() {

    }

    public User(String username, String email, String type) {
        this.username = username;
        this.email = email;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
