package com.example.players;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private String[] users = {"User1", "User2", "User3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listViewUsers = findViewById(R.id.listViewUsers);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        listViewUsers.setAdapter(adapter);

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                String selectedUser = users[position];
                openChatActivity(selectedUser);
            }
        });
    }

    private void openChatActivity(String selectedUser) {
        Intent intent = new Intent(AdminActivity.this, ChatActivity.class);
        intent.putExtra("currentUser", selectedUser);
        intent.putExtra("userRole", "admin");
        startActivity(intent);
    }
}

