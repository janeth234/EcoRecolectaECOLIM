package com.senati.ecorecolecta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Contrato del servicio web (API RESTful) que centraliza los registros
 * de todas las sedes de ECOLIM S.A.C. Este endpoint es simulado para
 * efectos del trabajo final; en producción apuntaría al backend real
 * de la empresa (por ejemplo: https://api.ecolim.com/v1/residuos).
 */
public interface ApiService {

    @POST("residuos")
    Call<RespuestaApi> enviarResiduo(@Body Residuo residuo);

    class RespuestaApi {
        public boolean exito;
        public String mensaje;
        public int idRemoto;
    }
}
