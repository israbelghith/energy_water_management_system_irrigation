package tn.isra.belghith.entities;

import jakarta.persistence.*;

import lombok.Data;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Pompe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private Double puissance; // en kW

    @Enumerated(EnumType.STRING)
    private StatutPompe statut;

    @Column(nullable = false)
    private Date dateMiseEnService;

    @OneToMany(mappedBy = "pompe", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ConsommationElectrique> listeConsommationsElectriques;
}
