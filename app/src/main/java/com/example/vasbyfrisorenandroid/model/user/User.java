package com.example.vasbyfrisorenandroid.model.user;

public class User {

    private final String firstname, lastName, email;

    private User(UserBuilder builder){
        this.firstname = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
    }

    public String getFirstname(){
        return firstname;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public static class UserBuilder{
        private final String firstName, lastName;
        private String email;

        public UserBuilder(String firstName, String lastName){
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public UserBuilder email(String email){
            this.email = email;
            return this;
        }

        public User build(){
            User user = new User(this);
            //validatieUserObject(user); //optional
            return user;
        }
    }
}
