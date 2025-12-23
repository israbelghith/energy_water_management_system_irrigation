package tn.isra.belghith.repositories;


import tn.isra.belghith.entities.Pompe;
import tn.isra.belghith.entities.StatutPompe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PompeRepository extends JpaRepository<Pompe, Long> {
    List<Pompe> findByStatut(StatutPompe statut);
    Pompe  findByReference(String reference);
    
}
