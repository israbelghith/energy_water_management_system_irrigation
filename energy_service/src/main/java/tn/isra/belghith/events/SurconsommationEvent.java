package tn.isra.belghith.events;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SurconsommationEvent {
	    private Long pompeId;
	    private String pompeReference;
	    private Double energieUtilisee;
	    private Double seuilDepasse;
	    private Date dateDetection;
	    private String message;
}
