package tn.isra.belghith.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "energy-service", url = "http://localhost:9092", fallbackFactory = EnergyServiceClientFallbackFactory.class)
public interface EnergyServiceClient {

    /**
     * Vérifie la disponibilité électrique pour une pompe
     * 
     * @param pompeId L'identifiant de la pompe
     * @return true si la disponibilité électrique est suffisante, false sinon
     */
    @GetMapping("/api/pompes/{pompeId}/disponibilite-electrique")
    Boolean verifierDisponibiliteElectrique(@PathVariable("pompeId") Long pompeId);
}
