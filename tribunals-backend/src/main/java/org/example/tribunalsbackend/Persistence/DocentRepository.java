package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Docent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocentRepository extends JpaRepository<Docent, String> {
    @Override
    Optional<Docent> findById(String s);

    Optional<Docent> findDocentByName(String name);


}
