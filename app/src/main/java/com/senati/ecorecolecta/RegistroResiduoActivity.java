package com.senati.ecorecolecta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formulario principal de la App: permite al trabajador registrar,
 * en tiempo real, una recolección de residuos.
 *
 * Identificación del tipo de residuo (pregunta guía 1):
 * se resuelve con un Spinner (menú desplegable) validado obligatoriamente,
 * complementado con un botón de "escaneo de código" opcional para
 * contenedores ya etiquetados (simulado en esta versión educativa).
 */
public class RegistroResiduoActivity extends AppCompatActivity {

    private Spinner spTipoResiduo, spArea;
    private EditText etCantidad, etObservaciones;
    private TextView lblFechaHora;
    private DatabaseHelper dbHelper;
    private String usuarioActual;

    private final String[] tiposResiduo = {
            "Papel y Cartón", "Plástico", "Vidrio", "Orgánico",
            "Metal", "Residuo Peligroso", "Residuo General"
    };
    private final String[] areas = {
            "Planta Industrial - Zona A", "Planta Industrial - Zona B",
            "Oficinas Administrativas", "Almacén", "Patio de Maniobras"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_residuo);

        dbHelper = new DatabaseHelper(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        usuarioActual = (firebaseAuth.getCurrentUser() != null
                && firebaseAuth.getCurrentUser().getEmail() != null)
                ? firebaseAuth.getCurrentUser().getEmail()
                : "desconocido";

        spTipoResiduo = findViewById(R.id.spTipoResiduo);
        spArea = findViewById(R.id.spArea);
        etCantidad = findViewById(R.id.etCantidad);
        etObservaciones = findViewById(R.id.etObservaciones);
        lblFechaHora = findViewById(R.id.lblFechaHora);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnVerLista = findViewById(R.id.btnVerLista);
        Button btnEscanear = findViewById(R.id.btnEscanearCodigo);

        spTipoResiduo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, tiposResiduo));
        spArea.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, areas));

        actualizarFechaHora();

        // Simulación de escaneo de código: en un entregable real se integraría
        // ML Kit Barcode Scanning o ZXing; aquí se preselecciona el tipo de
        // residuo para mostrar el flujo de identificación semiautomática.
        btnEscanear.setOnClickListener(v -> {
            spTipoResiduo.setSelection(1); // Ej: contenedor identificado como "Plástico"
            Toast.makeText(this, "Código leído: contenedor tipo PLÁSTICO", Toast.LENGTH_SHORT).show();
        });

        btnGuardar.setOnClickListener(v -> guardarRegistro());
        btnVerLista.setOnClickListener(v ->
                startActivity(new Intent(RegistroResiduoActivity.this, ListaResiduosActivity.class)));
    }

    private void actualizarFechaHora() {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        lblFechaHora.setText("Fecha y hora: " + fecha + " (automático)");
    }

    private void guardarRegistro() {
        String tipo = spTipoResiduo.getSelectedItem().toString();
        String area = spArea.getSelectedItem().toString();
        String cantidadTxt = etCantidad.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (TextUtils.isEmpty(cantidadTxt)) {
            Toast.makeText(this, "Ingrese la cantidad recolectada", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadTxt);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una cantidad válida mayor a 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaHoraSql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Residuo residuo = new Residuo(tipo, cantidad, area, observaciones, fechaHoraSql, usuarioActual);
        long id = dbHelper.insertarResiduo(residuo);

        if (id > 0) {
            Toast.makeText(this, "Registro guardado correctamente", Toast.LENGTH_SHORT).show();
            etCantidad.setText("");
            etObservaciones.setText("");
        } else {
            Toast.makeText(this, "Ocurrió un error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}
