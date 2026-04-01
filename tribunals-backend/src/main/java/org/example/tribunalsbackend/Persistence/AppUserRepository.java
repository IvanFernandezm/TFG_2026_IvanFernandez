package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Abstracts.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByMail(String mail);
}
