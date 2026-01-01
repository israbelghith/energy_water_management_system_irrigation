package tn.isra.belghith.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.dto.DebitMesureDto;
import tn.isra.belghith.services.DebitMesureService;

@RestController
@RequestMapping("/api/debits")
@Slf4j
public class DebitMesureController {

    @Autowired
    private DebitMesureService debitMesureService;

    @GetMapping
    public ResponseEntity<List<DebitMesureDto>> getAllDebits() {
        log.info("Récupération de toutes les mesures de débit");
        List<DebitMesureDto> debits = debitMesureService.getAllDebitMesures();
        return ResponseEntity.ok(debits);
    }

    @PostMapping
    public ResponseEntity<DebitMesureDto> enregistrerDebitMesure(@RequestBody DebitMesureDto dto) {
        log.info("Enregistrement d'une mesure de débit via API");
        try {
            DebitMesureDto created = debitMesureService.enregistrerDebitMesure(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            log.error("Erreur lors de l'enregistrement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebitMesureDto> getDebitMesure(@PathVariable Long id) {
        log.info("Récupération de la mesure de débit ID: {}", id);
        DebitMesureDto debitMesure = debitMesureService.getDebitMesure(id);
        return ResponseEntity.ok(debitMesure);
    }

    @GetMapping("/pompe/{pompeId}")
    public ResponseEntity<List<DebitMesureDto>> getDebitsByPompe(@PathVariable Long pompeId) {
        log.info("Récupération des mesures de débit pour la pompe ID: {}", pompeId);
        List<DebitMesureDto> debits = debitMesureService.getDebitMesuresByPompe(pompeId);
        return ResponseEntity.ok(debits);
    }

    @GetMapping("/periode")
    public ResponseEntity<List<DebitMesureDto>> getDebitsByPeriod(
            @RequestParam(required = false) Long pompeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        log.info("Récupération des mesures de débit pour la période: {} à {}", dateDebut, dateFin);
        List<DebitMesureDto> debits = debitMesureService.getDebitMesuresByPeriod(pompeId, dateDebut, dateFin);
        return ResponseEntity.ok(debits);
    }

    @GetMapping("/pompe/{pompeId}/moyen")
    public ResponseEntity<Double> getDebitMoyen(@PathVariable Long pompeId) {
        log.info("Calcul du débit moyen pour la pompe ID: {}", pompeId);
        Double debitMoyen = debitMesureService.getDebitMoyen(pompeId);
        return ResponseEntity.ok(debitMoyen);
    }

    @GetMapping("/pompe/{pompeId}/total")
    public ResponseEntity<Double> getDebitTotal(
            @PathVariable Long pompeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        log.info("Calcul du débit total pour la pompe ID: {} entre {} et {}", pompeId, dateDebut, dateFin);
        Double debitTotal = debitMesureService.getDebitTotal(pompeId, dateDebut, dateFin);
        return ResponseEntity.ok(debitTotal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebitMesure(@PathVariable Long id) {
        log.info("Suppression de la mesure de débit ID: {}", id);
        debitMesureService.deleteDebitMesure(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verifier-energie/{pompeId}")
    public ResponseEntity<Boolean> verifierDisponibiliteElectrique(@PathVariable Long pompeId) {
        log.info("Vérification de la disponibilité électrique pour la pompe ID: {}", pompeId);
        try {
            boolean disponible = debitMesureService.verifierDisponibiliteElectrique(pompeId);
            return ResponseEntity.ok(disponible);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(false);
        }
    }
}
