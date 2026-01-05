package com.example.jamat_project_simulator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private TextInputEditText l_email, l_pass;
    private TextView signUp, forgotPass;
    private MaterialButton login_btn;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null && email.equalsIgnoreCase("admin@gmail.com")) {
                startActivity(new Intent(LoginActivity.this, AdminDashBoard.class));
            } else {
                startActivity(new Intent(LoginActivity.this, UserDashBoard.class));
            }
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialize UI
        constraintLayout = findViewById(R.id.login1);
        l_email = findViewById(R.id.login_email);
        l_pass = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signup);
        forgotPass = findViewById(R.id.forgotPassword);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        // Signup redirect - Can click on entire signup container or just text
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Also handle click on signup container if needed
        LinearLayout signupContainer = findViewById(R.id.signupContainer);
        if (signupContainer != null) {
            signupContainer.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // Forgot password redirect
        forgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        // Login button
        login_btn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = l_email.getText().toString().trim();
        String pass = l_pass.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email)) {
            l_email.setError("Email is required");
            l_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            l_email.setError("Enter a valid email");
            l_email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            l_pass.setError("Password is required");
            l_pass.requestFocus();
            return;
        }

        if (pass.length() < 6) {
            l_pass.setError("Password must be at least 6 characters");
            l_pass.requestFocus();
            return;
        }

        // Disable button and show progress
        login_btn.setEnabled(false);
        progressDialog.show();

        // Firebase authentication
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    progressDialog.dismiss();
                    login_btn.setEnabled(true);

                    // Check if admin
                    if (email.equalsIgnoreCase("admin@gmail.com")) {
                        Toast.makeText(LoginActivity.this,
                                "Welcome Admin!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, AdminDashBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Login successful!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, UserDashBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    login_btn.setEnabled(true);

                    // Show user-friendly error messages
                    String errorMessage = "Login failed. Please try again.";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("no user record")) {
                            errorMessage = "No account found with this email.";
                        } else if (e.getMessage().contains("password is invalid")) {
                            errorMessage = "Incorrect password.";
                        } else if (e.getMessage().contains("network")) {
                            errorMessage = "Network error. Check your connection.";
                        }
                    }

                    showSnack(errorMessage);
                });
    }

    private void showSnack(String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}