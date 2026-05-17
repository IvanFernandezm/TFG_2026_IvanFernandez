package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Estudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudiantRepository extends JpaRepository<Estudiant, String> {
    @Override
    Optional<Estudiant> findById(String mail);
}
