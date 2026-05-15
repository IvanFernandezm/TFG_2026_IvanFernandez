package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Admin;
import org.example.tribunalsbackend.Domain.Docent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    public Admin findAdminByMail(String mail);
}

