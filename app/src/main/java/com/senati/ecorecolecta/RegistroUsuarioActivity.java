package com.senati.ecorecolecta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword, etConfirmar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        firebaseAuth = FirebaseAuth.getInstance();

        etCorreo = findViewById(R.id.etCorreoRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etConfirmar = findViewById(R.id.etConfirmarPassword);
        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        TextView lblVolverLogin = findViewById(R.id.lblVolverLogin);

        btnCrearCuenta.setOnClickListener(v -> crearCuenta());

        lblVolverLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistroUsuarioActivity.this, MainActivity.class));
            finish();
        });
    }

    private void crearCuenta() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmar = etConfirmar.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Ingrese un correo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistroUsuarioActivity.this, RegistroResiduoActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se pudo crear la cuenta: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}