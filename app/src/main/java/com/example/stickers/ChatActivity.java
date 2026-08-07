package com.example.stickers;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;

public class ChatActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listViewChat;
    private EditText editMessage;
    private Button btnSend;
    private Spinner spinnerUsers;

    private String currentUser;
    private String selectedUser;
    private String userRole;

    private ArrayAdapter<String> chatAdapter;
    private List<String> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DBHelper(this);


        listViewChat = findViewById(R.id.listViewChat);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        spinnerUsers = findViewById(R.id.spinnerUsers);


        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUser = preferences.getString("currentUser", null);

        if (currentUser == null || currentUser.isEmpty()) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userRole = dbHelper.getUserRole(currentUser);

        if (userRole == null) {
            Toast.makeText(this, "Rol no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatMessages = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                chatMessages
        );

        listViewChat.setAdapter(chatAdapter);


        setupUserSelection();
        setupSendButton();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupUserSelection() {

        if ("admin".equals(userRole)) {

            spinnerUsers.setVisibility(View.VISIBLE);
            List<String> users = dbHelper.getAllUsers();

            if (users != null) {
                users.remove("admin"); // 🔥 ADMIN NO SE ENVÍA MENSAJES A SÍ MISMO
            }

            if (users == null || users.isEmpty()) {
                Toast.makeText(this, "No hay usuarios", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayAdapter<String> spinnerAdapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            users
                    );

            spinnerAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            spinnerUsers.setAdapter(spinnerAdapter);

            spinnerUsers.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view,
                                                   int position,
                                                   long id) {

                            selectedUser = users.get(position);
                            loadConversation();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
        } else {
            selectedUser = "admin";
            loadConversation();
        }
    }

    private void setupSendButton() {

        btnSend.setOnClickListener(v -> {

            String message = editMessage.getText().toString().trim();

            if (!message.isEmpty() && selectedUser != null) {

                String timestamp = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

                boolean inserted = dbHelper.insertMessage(
                        currentUser,
                        selectedUser,
                        message,
                        timestamp
                );

                if (inserted) {
                    editMessage.setText("");
                    loadConversation();
                }
            }
        });
    }

    private void loadConversation() {

        if (selectedUser == null) return;

        chatMessages.clear();

        List<String> conversation =
                dbHelper.getConversation(currentUser, selectedUser);

        if (conversation != null) {
            chatMessages.addAll(conversation);
        }

        chatAdapter.notifyDataSetChanged();

        if (!chatMessages.isEmpty()) {
            listViewChat.setSelection(chatMessages.size() - 1);
        }
    }
}



