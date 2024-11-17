package com.example.loginproject.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.loginproject.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    // UI Elements
    private TextView userNombre, userEmail, userID;
    private CircleImageView userImg;
    private Button btnCerrarSesion, btnEliminarCuenta, btnAcercaDe, btnMainActivity2, btnPoliticasDe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes UI
        initializeUI();

        // Inicializar Firebase Auth y Google Sign-In
        setupFirebaseAndGoogleSignIn();

        // Mostrar los datos del usuario actual
        displayUserData();

        // Configurar listeners de los botones
        setupButtonListeners();
    }

    private void initializeUI() {
        userNombre = findViewById(R.id.userNombre);
        userEmail = findViewById(R.id.userEmail);
        userID = findViewById(R.id.userId);
        userImg = findViewById(R.id.userImagen);
        btnCerrarSesion = findViewById(R.id.btnLogout);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCta);
        btnAcercaDe = findViewById(R.id.btnAcerca);
        btnMainActivity2 = findViewById(R.id.btnApps);
        btnPoliticasDe = findViewById(R.id.btnPoliticas);
    }

    private void setupFirebaseAndGoogleSignIn() {
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void displayUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userID.setText(currentUser.getUid());
            userNombre.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());

            Glide.with(this)
                    .load(currentUser.getPhotoUrl())
                    .into(userImg);
        }
    }

    private void setupButtonListeners() {
        btnAcercaDe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AcercaDe.class);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> signOut());

        btnEliminarCuenta.setOnClickListener(v -> deleteAccount());

        btnMainActivity2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        btnPoliticasDe.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PoliticasDeActivity.class);
            startActivity(intent);
        });
    }

    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signInAccount != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Cuenta eliminada correctamente.", Toast.LENGTH_SHORT).show();
                                        signOut();
                                    } else {
                                        showErrorMessage("No se pudo eliminar la cuenta. Inténtalo nuevamente.");
                                    }
                                });
                            } else {
                                showErrorMessage("Re-autenticación fallida. Por favor, cierra sesión e inicia nuevamente.");
                            }
                        });
            } else {
                showErrorMessage("No se encontró una cuenta vinculada.");
            }
        } else {
            showErrorMessage("No hay un usuario autenticado.");
        }
    }

    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginActivity);
            finish();
        });
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e("MainActivity", message);
    }
}
