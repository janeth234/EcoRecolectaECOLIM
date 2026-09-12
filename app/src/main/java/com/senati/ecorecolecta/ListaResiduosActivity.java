package com.senati.ecorecolecta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ListaResiduosActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_residuos);

        dbHelper = new DatabaseHelper(this);

        rv = findViewById(R.id.rvResiduos);
        rv.setLayoutManager(new LinearLayoutManager(this));

        cargarLista();

        Button btnReportes = findViewById(R.id.btnReportes);
        btnReportes.setOnClickListener(v ->
                startActivity(new Intent(ListaResiduosActivity.this, ReportesActivity.class)));

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarLista() {
        List<Residuo> lista = dbHelper.obtenerResiduos(null, null, null);
        rv.setAdapter(new ResiduoAdapter(lista));
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ListaResiduosActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarLista(); // refresca la lista al volver de Registro, SIN recrear la Activity
    }
}