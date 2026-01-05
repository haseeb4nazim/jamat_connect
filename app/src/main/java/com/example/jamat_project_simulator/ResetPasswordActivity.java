package com.example.jamat_project_simulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout emailForgotLayout;
    private TextInputEditText emailForgot;
    private MaterialButton btnSend;
    private TextView goBacklogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Firebase Auth
        auth = FirebaseAuth.getInstance();

        // UI
        emailForgotLayout = findViewById(R.id.emailForgotLayout);
        emailForgot = findViewById(R.id.emailForgot);
        btnSend = findViewById(R.id.btnSend);
        goBacklogin=findViewById(R.id.backToLogin);

        goBacklogin.setOnClickListener(view -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSend.setOnClickListener(v -> sendResetLink());
    }

    private void sendResetLink() {
        String email = emailForgot.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            emailForgotLayout.setError("Email is required");
            emailForgot.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailForgotLayout.setError("Enter a valid email");
            emailForgot.requestFocus();
            return;
        }

        // Clear previous error
        emailForgotLayout.setError(null);

        // Disable button while sending
        btnSend.setEnabled(false);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnSend.setEnabled(true);

                    if (task.isSuccessful()) {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                "Reset link sent! Check your email.",
                                Snackbar.LENGTH_LONG
                        ).show();
                    } else {
                        Snackbar.make(
                                findViewById(android.R.id.content),
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Failed! Try again",
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
