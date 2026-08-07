package com.example.stickers;

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

public class UserManagementActivity extends AppCompatActivity {

    private Button btnBack;
    private ListView listViewUsers;
    private DBHelper dbHelper;
    private ArrayAdapter<String> userAdapter;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        btnBack = findViewById(R.id.btnBack);
        listViewUsers = findViewById(R.id.listViewUsers);

        dbHelper = new DBHelper(this);
        updateUserList();

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(UserManagementActivity.this, MainActivity.class));
            finish();
        });

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            String usernameToEdit = userList.get(position);

            String[] options = {"Editar", "Eliminar"};

            new AlertDialog.Builder(UserManagementActivity.this)
                    .setTitle("Opciones para " + usernameToEdit)
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            editUser(usernameToEdit);
                        } else {
                            deleteUser(usernameToEdit);
                        }
                    })
                    .show();
        });
    }

    private void editUser(String usernameToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);

        EditText etNewUsername = dialogView.findViewById(R.id.etNewUsername);
        EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        Spinner spinnerNewRole = dialogView.findViewById(R.id.spinnerNewRole);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.roles_array)
        ) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                view.setTextSize(20);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(Color.BLACK);
                view.setBackgroundColor(Color.WHITE);
                view.setTextSize(20);
                return view;
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
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateUserList() {
        userList = dbHelper.getAllUsers();

        userAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, userList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(24);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        };

        listViewUsers.setAdapter(userAdapter);
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.tvToastMessage);
        text.setText(message);
        text.setTextColor(Color.WHITE);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void deleteUser(String username) {
        new AlertDialog.Builder(UserManagementActivity.this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Eliminar a " + username + "?")
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