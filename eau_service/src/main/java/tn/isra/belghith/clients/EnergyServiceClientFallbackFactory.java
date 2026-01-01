package tn.isra.belghith.clients;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EnergyServiceClientFallbackFactory implements FallbackFactory<EnergyServiceClient> {

    @Override
    public EnergyServiceClient create(Throwable cause) {
        return new EnergyServiceClient() {
            @Override
            public Boolean verifierDisponibiliteElectrique(Long pompeId) {
                log.error("Erreur lors de l'appel au service énergie pour pompe {}: {}", pompeId, cause.getMessage());
                // Par défaut, on retourne true pour ne pas bloquer l'enregistrement
                // Dans un système de production, vous pourriez retourner false pour plus de
                // sécurité
                return true;
            }
        };
    }
}
