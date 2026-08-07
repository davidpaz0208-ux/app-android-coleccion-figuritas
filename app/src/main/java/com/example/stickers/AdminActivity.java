package com.example.stickers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    private ListView listViewOptions;

    private String[] options = {
            "Gestionar Figuritas",
            "Ver Usuarios",
            "Chat",
            "Cerrar sesión"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listViewOptions = findViewById(R.id.listViewUsers);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                options
        );

        listViewOptions.setAdapter(adapter);

        listViewOptions.setOnItemClickListener((parent, view, position, id) -> {

            switch (position) {

                case 0:
                    startActivity(new Intent(this, ManageStickersActivity.class));
                    break;

                case 1:
                    startActivity(new Intent(this, UserManagementActivity.class));
                    break;

                case 2:
                    startActivity(new Intent(this, ChatActivity.class));
                    break;

                case 3:
                    finish();
                    break;
            }
        });
    }
}

