package com.example.stickers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnCreateAccount;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        dbHelper = new DBHelper(this);

        btnCreateAccount.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty()
                    || password.isEmpty()
                    || confirmPassword.isEmpty()) {

                Toast.makeText(
                        this,
                        "Complete todos los campos",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (!password.equals(confirmPassword)) {

                Toast.makeText(
                        this,
                        "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (dbHelper.isUsernameTaken(username)) {

                Toast.makeText(
                        this,
                        "El usuario ya existe",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean success =
                    dbHelper.registerUser(
                            username,
                            password,
                            "user"
                    );

            if (success) {

                Toast.makeText(
                        this,
                        "Cuenta creada correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Error al crear la cuenta",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}