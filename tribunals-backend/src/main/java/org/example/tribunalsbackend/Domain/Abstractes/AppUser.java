package org.example.tribunalsbackend.Domain.Abstractes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AppUser {
    private String mail;
    private String name;

    public AppUser() {}
    public AppUser(String mail, String name) {
        this.mail = mail;
        this.name = name;
    }

}
