package org.example.tribunalsbackend.Persistence;

import org.example.tribunalsbackend.Domain.Treball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreballRepository extends JpaRepository<Treball, String> {
}
