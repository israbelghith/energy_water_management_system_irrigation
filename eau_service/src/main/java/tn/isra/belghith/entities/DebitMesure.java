package tn.isra.belghith.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class DebitMesure {
 
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long pompeId; // référence côté microservice Énergie

  @Column(nullable = false)
  private Double debit; // m³/h

  @Column(nullable = false)
  private String unite; // ex: "m3/h"

  @Column(nullable = false)
  private Date dateMesure;

}
