package tn.isra.belghith.dto;

import java.util.Date;

import lombok.Data;

@Data
public class DebitMesureDto {
	
  private Long id;
  private Long pompeId;
  private Double debit;
  private String unite;
  private Date dateMesure;

}
