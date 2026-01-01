package tn.isra.belghith.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.DTO.PompeDTO;
import tn.isra.belghith.entities.Pompe;
import tn.isra.belghith.entities.StatutPompe;
import tn.isra.belghith.services.PompeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/pompes")
public class PompeController {

    @Autowired
    private PompeService pompeService;

    @PostMapping
    public ResponseEntity<PompeDTO> createPompe(@RequestBody PompeDTO pompeDTO) {
        log.info("Requête de création de pompe: {}", pompeDTO.getReference());
        return new ResponseEntity<>(pompeService.createPompeFromDTO(pompeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PompeDTO> updatePompe(@PathVariable Long id, @RequestBody PompeDTO pompeDTO) {
        log.info("Requête de mise à jour de pompe id: {}", id);
        return ResponseEntity.ok(pompeService.updatePompeFromDTO(id, pompeDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PompeDTO> getPompeById(@PathVariable Long id) {
        log.info("Requête de récupération de pompe id: {}", id);
        return ResponseEntity.ok(pompeService.getPompeById(id));
    }

    @GetMapping
    public ResponseEntity<List<PompeDTO>> getAllPompes() {
        log.info("Requête de récupération de toutes les pompes");
        return ResponseEntity.ok(pompeService.getAllPompes());
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<PompeDTO>> getPompesByStatut(@PathVariable StatutPompe statut) {
        log.info("Requête de récupération des pompes par statut: {}", statut);
        return ResponseEntity.ok(pompeService.getPompesByStatut(statut));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<PompeDTO> getPompeByReference(@PathVariable String reference) {
        log.info("Requête de récupération de pompe par référence: {}", reference);
        PompeDTO pompe = pompeService.getPompeByReference(reference);
        return ResponseEntity.ok(pompe);
    }

    @GetMapping("/{id}/disponibilite")
    public ResponseEntity<Boolean> verifierDisponibilite(@PathVariable Long id) {
        log.info("Vérification de disponibilité électrique pour pompe id: {}", id);
        boolean disponible = pompeService.verifierDisponibiliteElectrique(id);
        return ResponseEntity.ok(disponible);
    }

    // Endpoint compatible avec le client Feign du microservice eau
    @GetMapping("/{id}/disponibilite-electrique")
    public ResponseEntity<Boolean> verifierDisponibiliteElectrique(@PathVariable Long id) {
        log.info("Vérification de disponibilité électrique (Feign) pour pompe id: {}", id);
        boolean disponible = pompeService.verifierDisponibiliteElectrique(id);
        return ResponseEntity.ok(disponible);
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<PompeDTO> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutPompe nouveauStatut) {
        log.info("Changement de statut pour pompe id: {} vers {}", id, nouveauStatut);
        PompeDTO pompe = pompeService.changerStatut(id, nouveauStatut);
        return ResponseEntity.ok(pompe);
    }

    @PutMapping("/{id}/activer")
    public ResponseEntity<Void> activerPompe(@PathVariable Long id) {
        log.info("Activation de la pompe id: {}", id);
        pompeService.changerStatut(id, StatutPompe.ACTIVE);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiverPompe(@PathVariable Long id) {
        log.info("Désactivation de la pompe id: {}", id);
        pompeService.changerStatut(id, StatutPompe.INACTIVE);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePompe(@PathVariable Long id) {
        log.info("Requête de suppression de pompe id: {}", id);
        pompeService.deletePompe(id);
        return ResponseEntity.noContent().build();
    }
}