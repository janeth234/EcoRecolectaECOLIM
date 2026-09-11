package com.senati.ecorecolecta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Lista, mediante un RecyclerView, todos los registros almacenados
 * localmente en SQLite. Sirve de puente hacia la pantalla de Reportes
 * y permite cerrar la sesión de Firebase iniciada en MainActivity.
 */
public class ListaResiduosActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_residuos);

        dbHelper = new DatabaseHelper(this);

        RecyclerView rv = findViewById(R.id.rvResiduos);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<Residuo> lista = dbHelper.obtenerResiduos(null, null, null);
        rv.setAdapter(new ResiduoAdapter(lista));

        Button btnReportes = findViewById(R.id.btnReportes);
        btnReportes.setOnClickListener(v ->
                startActivity(new Intent(ListaResiduosActivity.this, ReportesActivity.class)));

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ListaResiduosActivity.this, MainActivity.class);
        // Limpia el historial de pantallas para que "atrás" no regrese a datos de la sesión anterior
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recreate(); // refresca la lista al volver de Registro
    }
}
