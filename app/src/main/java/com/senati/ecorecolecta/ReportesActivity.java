package com.senati.ecorecolecta;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Sistema de generación de reportes (requisito del caso práctico):
 * filtra los registros locales por rango de fechas y/o tipo de residuo,
 * calcula totales y permite:
 *   a) Compartir el resumen (simulado, vía Intent.ACTION_SEND)
 *   b) Sincronizar los pendientes contra la API RESTful remota (Retrofit)
 */
public class ReportesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private Spinner spFiltroTipo;
    private TextView tvResumen;
    private String fechaDesde = null, fechaHasta = null;

    private final String[] tipos = {"Todos", "Papel y Cartón", "Plástico", "Vidrio",
            "Orgánico", "Metal", "Residuo Peligroso", "Residuo General"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        dbHelper = new DatabaseHelper(this);

        spFiltroTipo = findViewById(R.id.spFiltroTipo);
        spFiltroTipo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tipos));

        tvResumen = findViewById(R.id.tvResumen);

        Button btnFechaDesde = findViewById(R.id.btnFechaDesde);
        Button btnFechaHasta = findViewById(R.id.btnFechaHasta);
        Button btnGenerar = findViewById(R.id.btnGenerar);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnSincronizar = findViewById(R.id.btnSincronizar);

        btnFechaDesde.setOnClickListener(v -> elegirFecha(btnFechaDesde, true));
        btnFechaHasta.setOnClickListener(v -> elegirFecha(btnFechaHasta, false));
        btnGenerar.setOnClickListener(v -> generarReporte());
        btnCompartir.setOnClickListener(v -> compartirReporte());
        btnSincronizar.setOnClickListener(v -> sincronizarConServidor());
    }

    private void elegirFecha(Button boton, boolean esDesde) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            boton.setText(fecha);
            if (esDesde) fechaDesde = fecha; else fechaHasta = fecha;
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void generarReporte() {
        String tipoSeleccionado = spFiltroTipo.getSelectedItem().toString();
        List<Residuo> resultado = dbHelper.obtenerResiduos(tipoSeleccionado, fechaDesde, fechaHasta);

        double totalKg = 0;
        for (Residuo r : resultado) totalKg += r.getCantidadKg();

        String resumen = String.format(Locale.getDefault(),
                "Tipo: %s\nRegistros encontrados: %d\nTotal recolectado: %.2f kg",
                tipoSeleccionado, resultado.size(), totalKg);
        tvResumen.setText(resumen);
    }

    private void compartirReporte() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de residuos - ECOLIM S.A.C.");
        intent.putExtra(Intent.EXTRA_TEXT, tvResumen.getText().toString());
        startActivity(Intent.createChooser(intent, "Compartir reporte con..."));
    }

    /**
     * Envía cada registro pendiente (sincronizado = 0) al backend mediante
     * Retrofit. El endpoint es simulado para efectos académicos: en un
     * entorno real respondería con el ID remoto asignado por el servidor.
     */
    private void sincronizarConServidor() {
        List<Residuo> pendientes = dbHelper.obtenerPendientesSincronizar();
        if (pendientes.isEmpty()) {
            Toast.makeText(this, "No hay registros pendientes de sincronizar", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getApiService();
        for (Residuo r : pendientes) {
            api.enviarResiduo(r).enqueue(new Callback<ApiService.RespuestaApi>() {
                @Override
                public void onResponse(Call<ApiService.RespuestaApi> call,
                                        Response<ApiService.RespuestaApi> response) {
                    if (response.isSuccessful()) {
                        dbHelper.marcarComoSincronizado(r.getId());
                    }
                }

                @Override
                public void onFailure(Call<ApiService.RespuestaApi> call, Throwable t) {
                    // Sin conexión: el registro permanece marcado como pendiente
                    // y se reintentará en la próxima sincronización.
                }
            });
        }
        Toast.makeText(this, "Sincronización iniciada (" + pendientes.size() + " registros)",
                Toast.LENGTH_LONG).show();
    }
}
