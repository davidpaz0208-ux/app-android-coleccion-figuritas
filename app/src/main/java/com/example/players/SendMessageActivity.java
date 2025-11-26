package com.example.players;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SendMessageActivity extends AppCompatActivity {

    private Spinner spinnerUsers;
    private EditText etMessage;
    private Button btnSendMessage;
    private DBHelper dbHelper;
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        spinnerUsers = findViewById(R.id.spinnerUsers);
        etMessage = findViewById(R.id.etMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        dbHelper = new DBHelper(this);

        loadUsersInSpinner();

        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void loadUsersInSpinner() {
        userList = dbHelper.getAllUsers();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(adapter);
    }

    private void sendMessage() {
        String selectedUser = (String) spinnerUsers.getSelectedItem();
        String message = etMessage.getText().toString().trim();

        if (selectedUser == null || selectedUser.isEmpty()) {
            showToast("Por favor, selecciona un usuario.");
            return;
        }

        if (message.isEmpty()) {
            showToast("Por favor, escribe un mensaje.");
            return;
        }


        showToast("Mensaje enviado a " + selectedUser);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
