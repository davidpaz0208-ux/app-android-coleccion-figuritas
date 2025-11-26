package com.example.players;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ImageButton btnLogin;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                new LoginTask(LoginActivity.this, dbHelper, username).execute(password);
            }
        });
    }

    private static class LoginTask extends AsyncTask<String, Void, String> {
        private Context context;
        private DBHelper dbHelper;
        private String username;

        public LoginTask(Context context, DBHelper dbHelper, String username) {
            this.context = context;
            this.dbHelper = dbHelper;
            this.username = username;
        }

        @Override
        protected String doInBackground(String... params) {
            String password = params[0];
            String userRole = null;

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                String storedHash = cursor.getString(cursor.getColumnIndex("password")); // Obtener el hash almacenado
                if (BCrypt.checkpw(password, storedHash)) {
                    userRole = cursor.getString(cursor.getColumnIndex("role"));
                }
                cursor.close();
            }

            return userRole;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                SharedPreferences preferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("currentUser", username);
                editor.putString("userRole", result);
                editor.apply();

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


