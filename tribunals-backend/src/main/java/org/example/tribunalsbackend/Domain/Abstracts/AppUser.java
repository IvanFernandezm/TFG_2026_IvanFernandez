package org.example.tribunalsbackend.Domain.Abstracts;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AppUser {

    @Id
    @Column(name = "mail")
    private String mail;
    private String name;

    public AppUser() {}
    public AppUser(String mail, String name) {
        this.mail = mail;
        this.name = name;
    }

}
