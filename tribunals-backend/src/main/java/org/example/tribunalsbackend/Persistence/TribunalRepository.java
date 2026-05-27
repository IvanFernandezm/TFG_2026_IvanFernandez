package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Treball;
import org.example.tribunalsbackend.Domain.Tribunal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TribunalRepository extends JpaRepository<Tribunal, Long> {
    Tribunal findTribunalByTreball_Title(String treballTitle);
}
