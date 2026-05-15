package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Estudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudiantRepository extends JpaRepository<Estudiant, String> {
    Estudiant findEstudiantByMail(String mail);
}
