package tn.isra.belghith.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import tn.isra.belghith.dto.ReservoirDto;
import tn.isra.belghith.services.ReservoirService;

@RestController
@RequestMapping("/api/reservoirs")
@Slf4j
public class ReservoirController {

    @Autowired
    private ReservoirService reservoirService;

    @PostMapping
    public ResponseEntity<ReservoirDto> creerReservoir(@RequestBody ReservoirDto dto) {
        log.info("Création d'un réservoir via API");
        ReservoirDto created = reservoirService.creerReservoir(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservoirDto> getReservoir(@PathVariable Long id) {
        log.info("Récupération du réservoir ID: {}", id);
        ReservoirDto reservoir = reservoirService.getReservoir(id);
        return ResponseEntity.ok(reservoir);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservoirDto> updateReservoir(
            @PathVariable Long id,
            @RequestBody ReservoirDto dto) {
        log.info("Mise à jour du réservoir ID: {}", id);
        ReservoirDto updated = reservoirService.updateReservoir(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ReservoirDto>> getAllReservoirs() {
        log.info("Récupération de tous les réservoirs");
        List<ReservoirDto> reservoirs = reservoirService.getAllReservoirs();
        return ResponseEntity.ok(reservoirs);
    }

    @PutMapping("/{id}/volume")
    public ResponseEntity<ReservoirDto> updateVolume(
            @PathVariable Long id,
            @RequestParam Double nouveauVolume) {
        log.info("Mise à jour du volume pour le réservoir ID: {}", id);
        ReservoirDto updated = reservoirService.updateVolumeActuel(id, nouveauVolume);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/alertes")
    public ResponseEntity<List<ReservoirDto>> getReservoirsEnAlerte() {
        log.info("Récupération des réservoirs en alerte");
        List<ReservoirDto> reservoirs = reservoirService.getReservoirsEnAlerte();
        return ResponseEntity.ok(reservoirs);
    }

    @GetMapping("/localisation/{localisation}")
    public ResponseEntity<List<ReservoirDto>> getByLocalisation(@PathVariable String localisation) {
        log.info("Récupération des réservoirs par localisation: {}", localisation);
        List<ReservoirDto> reservoirs = reservoirService.getReservoirsByLocalisation(localisation);
        return ResponseEntity.ok(reservoirs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservoir(@PathVariable Long id) {
        log.info("Suppression du réservoir ID: {}", id);
        reservoirService.deleteReservoir(id);
        return ResponseEntity.noContent().build();
    }
}
