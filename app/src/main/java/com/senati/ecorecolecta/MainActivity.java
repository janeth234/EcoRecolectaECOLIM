package com.senati.ecorecolecta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Pantalla de acceso. La autenticación de los trabajadores se maneja
 * con Firebase Authentication (correo/contraseña), igual que en el
 * proyecto de referencia del curso. Los datos operativos (residuos
 * recolectados) se guardan aparte, en SQLite local (ver DatabaseHelper),
 * tal como exige el caso práctico.
 */
public class MainActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword;
    private TextView lblRecuperar;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        etCorreo = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        Button btnIngresar = findViewById(R.id.btnIngresar);
        lblRecuperar = findViewById(R.id.lblRecuperar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere un momento...");
        progressDialog.setCanceledOnTouchOutside(false);

        // Si el trabajador ya tiene una sesión activa, pasa directo al registro
        if (firebaseAuth.getCurrentUser() != null) {
            irARegistroResiduo();
            return;
        }

        btnIngresar.setOnClickListener(v -> validarIngreso());

        lblRecuperar.setOnClickListener(v ->
                Toast.makeText(this, "Funcionalidad de recuperación pendiente de integrar",
                        Toast.LENGTH_SHORT).show());
    }

    private void validarIngreso() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Verificando credenciales...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        irARegistroResiduo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irARegistroResiduo() {
        startActivity(new Intent(MainActivity.this, RegistroResiduoActivity.class));
        finish();
    }
}
