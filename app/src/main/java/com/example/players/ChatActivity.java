package com.example.players;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.content.Context;
import android.view.ViewGroup;
import android.graphics.Color;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listViewMessages;
    private EditText editTextMessage;
    private Button btnSendMessage, btnBack, btnClearChatHistory;
    private String currentUser;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences preferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUser = preferences.getString("currentUser", null);

        if (currentUser == null || currentUser.isEmpty()) {
            showCustomToast("Usuario desconocido, no se puede enviar el mensaje");
            return;
        }

        dbHelper = new DBHelper(this);

        userRole = dbHelper.getUserRole(currentUser);

        editTextMessage = findViewById(R.id.editTextMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        listViewMessages = findViewById(R.id.listViewMessages);
        btnBack = findViewById(R.id.btnBack);
        btnClearChatHistory = findViewById(R.id.btnClearChatHistory);

        if ("admin".equals(userRole)) {
            btnClearChatHistory.setEnabled(true);
        } else {
            btnClearChatHistory.setEnabled(false);
        }

        loadMessages();

        btnBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> sendMessage());

        btnClearChatHistory.setOnClickListener(v -> clearChatHistory());
        btnClearChatHistory.setVisibility("admin".equals(userRole) ? View.VISIBLE : View.GONE);
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();

        if (messageText.isEmpty()) {
            showCustomToast("Por favor ingrese un mensaje");
            return;
        }

        if (currentUser != null && !currentUser.isEmpty()) {
            if ("admin".equals(userRole)) {
                showReceiverSelectionDialog(messageText);
            } else {
                sendMessageToUser("admin", messageText);
            }
        } else {
            showCustomToast("Usuario desconocido, no se puede enviar el mensaje");
        }
    }

    private void showReceiverSelectionDialog(String messageText) {
        List<String> users = dbHelper.getAllUsers();
        final String[] usersArray = users.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setTitle("Selecciona el receptor del mensaje")
                .setItems(usersArray, (dialog, which) -> {
                    String selectedUser = usersArray[which];
                    sendMessageToUser(selectedUser, messageText);
                })
                .show();
    }

    private void sendMessageToUser(String receiver, String message) {
        String sender = currentUser;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = sdf.format(new Date());

        if (receiver == null || receiver.isEmpty()) {
            showCustomToast("El receptor no es válido.");
            return;
        }

        if (message == null || message.trim().isEmpty()) {
            showCustomToast("El mensaje no puede estar vacío.");
            return;
        }

        boolean result = dbHelper.insertMessage(sender, receiver, message, timestamp);

        if (result) {
            loadMessages();
            editTextMessage.setText("");
            showCustomToast("Mensaje enviado");
        } else {
            showCustomToast("Error al enviar el mensaje");
        }
    }

    public void clearChatHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("¿Estás seguro de borrar el historial?");

        TextView textView = new TextView(this);
        textView.setText("¿Estás seguro de borrar el historial?");
        textView.setTextSize(24);
        builder.setCustomTitle(textView);

        builder.setPositiveButton("Sí", (dialog, which) -> {
            int rowsAffected = dbHelper.deleteAllMessages();
            if (rowsAffected > 0) {
                showCustomToast("Historial de chat borrado");
            } else {
                showCustomToast("No hay mensajes para borrar.");
            }
            loadMessages();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            btnPositive.setTextSize(20);
            btnNegative.setTextSize(20);
        });

        dialog.show();
    }

    private void loadMessages() {
        List<Message> messages;

        if ("admin".equals(userRole)) {
            messages = dbHelper.getAllMessages();
        } else {
            messages = dbHelper.getMessagesForUser(currentUser);
        }

        List<String> messageStrings = new ArrayList<>();
        String lastTimestamp = "";

        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat baFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        baFormat.setTimeZone(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));

        for (Message message : messages) {
            String formattedMessage = message.getSender() + ": " + message.getMessage();
            String timestamp = message.getTimestamp();

            try {
                Date date = utcFormat.parse(timestamp);
                lastTimestamp = baFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                lastTimestamp = timestamp;
            }

            messageStrings.add(formattedMessage + " (" + lastTimestamp + ")");
        }

        TextView tvTimestamp = findViewById(R.id.tvTimestamp);
        if (!lastTimestamp.isEmpty()) {
            tvTimestamp.setText("Último mensaje a las " + lastTimestamp);
            tvTimestamp.setTextSize(24);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, messageStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
                    view = inflater.inflate(R.layout.list_item, parent, false);
                }

                TextView tvMessage = view.findViewById(R.id.tvMessage);
                TextView tvTimestamp = view.findViewById(R.id.tvTimestamp);

                String message = messageStrings.get(position);
                String[] parts = message.split("\\(");

                if (parts.length > 1) {
                    String messageText = parts[0];
                    String timestampText = "(" + parts[1];

                    tvMessage.setText(messageText);
                    tvTimestamp.setText(timestampText);

                    tvMessage.setTextSize(24);
                    tvTimestamp.setTextSize(20);

                    if (messageText.startsWith(currentUser)) {
                        tvMessage.setBackgroundResource(R.drawable.message_bubble);
                        tvMessage.setTextColor(Color.WHITE);
                    } else {
                        tvMessage.setBackgroundResource(R.drawable.message_bubble_other);
                        tvMessage.setTextColor(Color.BLACK);
                    }
                }

                return view;
            }
        };

        listViewMessages.setAdapter(adapter);
    }
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        if (layout != null) {
            TextView text = layout.findViewById(R.id.tvToastMessage);
            text.setText(message);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}




