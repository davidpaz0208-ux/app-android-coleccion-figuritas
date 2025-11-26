package com.example.players;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

public class UserManagementActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Spinner spinnerRole;
    private Button btnAddUser, btnBack;
    private ListView listViewUsers;
    private DBHelper dbHelper;
    private ArrayAdapter<String> userAdapter;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnBack = findViewById(R.id.btnBack);
        listViewUsers = findViewById(R.id.listViewUsers);

        etUsername.setTextColor(Color.WHITE);
        etPassword.setTextColor(Color.WHITE);
        btnAddUser.setTextColor(Color.WHITE);
        btnBack.setTextColor(Color.WHITE);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.roles_array)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(22);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(22);
                textView.setBackgroundColor(Color.BLACK);
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);


        dbHelper = new DBHelper(this);
        updateUserList();

        btnAddUser.setOnClickListener(v -> addUser());
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = userList.get(position);
            String usernameToEdit = selectedUser.split("\n")[0];

            String[] options = {"Editar", "Eliminar"};

            ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = view.findViewById(android.R.id.text1);
                    textView.setTextSize(24); // Aumenta el tamaño de la letra
                    return view;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
            builder.setTitle("Opciones para " + usernameToEdit)
                    .setAdapter(optionsAdapter, (dialog, which) -> {
                        if (which == 0) {
                            editUser(usernameToEdit);
                        } else {
                            deleteUser(usernameToEdit);
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void addUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showCustomToast("Por favor, completa los campos.");
            return;
        }

        if (dbHelper.isUsernameTaken(username)) {
            showCustomToast("Este nombre ya está en uso.");
            return;
        }

        if (dbHelper.insertUser(username, password, role)) {
            showCustomToast("Usuario agregado correctamente.");
            updateUserList();
        } else {
            showCustomToast("Error al agregar el usuario.");
        }
    }

    public void editUser(String usernameToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);

        EditText etNewUsername = dialogView.findViewById(R.id.etNewUsername);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        Spinner spinnerNewRole = dialogView.findViewById(R.id.spinnerNewRole);

        etNewUsername.setTextSize(22);
        etNewPassword.setTextSize(22);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.roles_array)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(22);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextSize(22);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.BLACK);
                return textView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNewRole.setAdapter(adapter);


        builder.setTitle("Editar Usuario")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialogInterface, which) -> {
                    String newUsername = etNewUsername.getText().toString().trim();
                    String newPassword = etNewPassword.getText().toString().trim();
                    String newRole = spinnerNewRole.getSelectedItem().toString();

                    if (newUsername.isEmpty() || newPassword.isEmpty()) {
                        showCustomToast("Los campos no pueden estar vacíos.");
                        return;
                    }

                    boolean isUpdated = dbHelper.updateUser(usernameToEdit, newUsername, newPassword, newRole);
                    if (isUpdated) {
                        showCustomToast("Usuario actualizado correctamente.");
                        updateUserList();
                    } else {
                        showCustomToast("Error al actualizar el usuario.");
                    }
                })
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
    }

    private void updateUserList() {
        userList = dbHelper.getAllUsers();

        userAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(24);
                textView.setTextColor(Color.BLACK);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextSize(24);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };

        listViewUsers.setAdapter(userAdapter);
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = layout.findViewById(R.id.tvToastMessage);
        text.setText(message);
        text.setTextColor(Color.WHITE);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void deleteUser(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
        builder.setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar a " + username + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean isDeleted = dbHelper.deleteUser(username);
                    if (isDeleted) {
                        showCustomToast("Usuario eliminado correctamente.");
                        updateUserList();
                    } else {
                        showCustomToast("Error al eliminar el usuario.");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}

