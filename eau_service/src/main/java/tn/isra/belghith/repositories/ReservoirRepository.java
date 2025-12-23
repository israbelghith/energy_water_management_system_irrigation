package tn.isra.belghith.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.isra.belghith.entities.Reservoir;

@Repository
public interface ReservoirRepository extends JpaRepository<Reservoir, Long> {

    Optional<Reservoir> findByNom(String nom);

    List<Reservoir> findByLocalisation(String localisation);

    @Query("SELECT r FROM Reservoir r WHERE r.volumeActuel < (r.capaciteTotale * 0.2)")
    List<Reservoir> findReservoirsEnAlerte();

    @Query("SELECT r FROM Reservoir r WHERE r.volumeActuel >= :volumeMin AND r.volumeActuel <= :volumeMax")
    List<Reservoir> findByVolumeActuelBetween(@Param("volumeMin") Double volumeMin,
            @Param("volumeMax") Double volumeMax);
}
