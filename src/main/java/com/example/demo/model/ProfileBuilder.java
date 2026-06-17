package com.example.demo.model;

public class ProfileBuilder {

    private final Profile profile;

    public ProfileBuilder() {
        profile = new Profile();
    }

    public ProfileBuilder firstName(String firstName) {
        profile.setFullName(firstName);
        return this;
    }

    public ProfileBuilder lastName(String lastName) {
        profile.setFullName(profile.getFullName() != null
                ? profile.getFullName() + " " + lastName
                : lastName);
        return this;
    }

    public ProfileBuilder email(String email) {
        profile.setEmail(email);
        return this;
    }

    public ProfileBuilder phone(String phone) {
        profile.setPhone(phone);
        return this;
    }

    public ProfileBuilder profileType(ProfileType type) {
        profile.setType(type);
        return this;
    }

    public Profile build() {
        return profile;
    }
}
