package ar.edu.utn.frc.backend.tpi.locations.service;

import ar.edu.utn.frc.backend.tpi.locations.model.Coordenada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceService {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private final GoogleDistanceMatrixClient googleDistanceMatrixClient;

    public double calcularDistanciaEnKilometros(Coordenada origen, Coordenada destino) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las coordenadas de origen y destino son obligatorias");
        }
        if (origen.getLatitud() == null || origen.getLongitud() == null
                || destino.getLatitud() == null || destino.getLongitud() == null) {
            throw new IllegalArgumentException("Las coordenadas deben incluir latitud y longitud");
        }

        try {
            // 1) Intentar con Google Distance Matrix
            return googleDistanceMatrixClient.obtenerDistanciaEnKm(origen, destino);
        } catch (IllegalStateException ex) {
            // 2) Fallback Haversine si falla Google (según guía podés dejarlo o quitarlo)
            double lat1 = Math.toRadians(origen.getLatitud());
            double lon1 = Math.toRadians(origen.getLongitud());
            double lat2 = Math.toRadians(destino.getLatitud());
            double lon2 = Math.toRadians(destino.getLongitud());

            double dLat = lat2 - lat1;
            double dLon = lon2 - lon1;

            double a = Math.pow(Math.sin(dLat / 2), 2)
                    + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return RADIO_TIERRA_KM * c;
        }
    }
}
