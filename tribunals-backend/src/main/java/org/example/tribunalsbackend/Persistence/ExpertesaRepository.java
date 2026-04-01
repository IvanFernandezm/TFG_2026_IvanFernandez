package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Expertesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertesaRepository extends JpaRepository<Expertesa, String> {
}
