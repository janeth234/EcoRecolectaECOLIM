package com.senati.ecorecolecta;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Construye el cliente Retrofit apuntando al servicio web de ECOLIM.
 * URL_BASE debe reemplazarse por el endpoint real del backend de la empresa.
 */
public class RetrofitClient {

    private static final String URL_BASE = "https://api.ecolim-simulado.com/";
    private static Retrofit retrofit;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
