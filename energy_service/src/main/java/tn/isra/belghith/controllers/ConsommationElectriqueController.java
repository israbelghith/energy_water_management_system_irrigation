package tn.isra.belghith.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.isra.belghith.DTO.ConsommationElectriqueDTO;
import tn.isra.belghith.entities.ConsommationElectrique;
import tn.isra.belghith.services.ConsommationElectriqueService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/consommations")
@Slf4j
public class ConsommationElectriqueController {
    
    @Autowired
    private ConsommationElectriqueService consommationService;
    
    @PostMapping
    public ResponseEntity<ConsommationElectriqueDTO> enregistrerConsommation(@RequestBody ConsommationElectrique consommation) {
        log.info("Requête d'enregistrement de consommation pour pompe: {}", consommation.getPompe().getId());
        ConsommationElectriqueDTO created = consommationService.enregistrerConsommation(consommation);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    
    @GetMapping("/pompe/{pompeId}")
    public ResponseEntity<List<ConsommationElectriqueDTO>> getConsommationsByPompe( @PathVariable Long pompeId) {
        log.info("Récupération des consommations pour pompe: {}", pompeId);
        List<ConsommationElectriqueDTO> consommations =consommationService.getConsommationsByPompe(pompeId);
        return ResponseEntity.ok(consommations);
    }
    

    @GetMapping("/pompe/{pompeId}/periode")
    public ResponseEntity<List<ConsommationElectriqueDTO>> getConsommationsByPeriod(
            @PathVariable Long pompeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fin) {
        log.info("Récupération consommations pompe {} du {} au {}", pompeId, debut, fin);
        List<ConsommationElectriqueDTO> consommations =consommationService.getConsommationsByPeriod(pompeId, debut, fin);
        return ResponseEntity.ok(consommations);
    }
    
    
    @GetMapping("/pompe/{pompeId}/total")
    public ResponseEntity<Double> getConsommationTotale(
            @PathVariable Long pompeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date debut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date fin) {
        log.info("Calcul consommation totale pompe {} du {} au {}", pompeId, debut, fin);
        Double total = consommationService.getConsommationTotale(pompeId, debut, fin);
        return ResponseEntity.ok(total);
    }
}
