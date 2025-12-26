package tn.isra.belghith.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class ConsommationElectrique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pompe_id")
    private Pompe pompe;

    @Column(nullable = false)
    private Double energieUtilisee; // en kWh

    @Column(nullable = false)
    private Double duree; // en heures

    @Column(nullable = false)
    private Date dateMesure;

    /*
     * @Transient
     * private boolean surconsommation;
     */

}
