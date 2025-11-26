package com.example.players;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private String currentUserRole;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias UI
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        // Toolbar
        setSupportActionBar(toolbar);

        // Toggle del Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Obtener sesión REAL (sin valores basura)
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUser = preferences.getString("currentUser", null);
        currentUserRole = preferences.getString("userRole", null);

        // ⚠️ VALIDACIÓN OBLIGATORIA – Si la sesión no existe → volver al Login
        if (currentUser == null || currentUserRole == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Ocultar/mostrar menús según rol
        updateMenuVisibilityForUserRole();

        // Listener del menú
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_view_players) {
                startActivity(new Intent(MainActivity.this, PlayersListActivity.class));

            } else if (id == R.id.nav_add_player) {
                startActivity(new Intent(MainActivity.this, AddPlayerActivity.class));

            } else if (id == R.id.nav_update_position) {
                startActivity(new Intent(MainActivity.this, UpdatePlayerPositionActivity.class));

            } else if (id == R.id.nav_edit_player_name) {
                Intent intentEdit = new Intent(MainActivity.this, EditPlayerNameActivity.class);
                startActivity(intentEdit);

            } else if (id == R.id.nav_delete_player) {
                startActivity(new Intent(MainActivity.this, DeletePlayerActivity.class));

            } else if (id == R.id.nav_chat) {
                Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                chatIntent.putExtra("currentUser", currentUser);
                chatIntent.putExtra("userRole", currentUserRole);
                startActivity(chatIntent);

            } else if (id == R.id.nav_manage_users) {
                startActivity(new Intent(MainActivity.this, UserManagementActivity.class));

            } else if (id == R.id.nav_logout) {
                logout();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateMenuVisibilityForUserRole() {

        if (navigationView.getMenu() == null) return;

        if ("user".equals(currentUserRole)) {
            navigationView.getMenu().findItem(R.id.nav_add_player).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_update_position).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_edit_player_name).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_delete_player).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_manage_users).setVisible(false);

        } else if ("admin".equals(currentUserRole)) {
            navigationView.getMenu().findItem(R.id.nav_add_player).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_update_position).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_edit_player_name).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_delete_player).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_manage_users).setVisible(true);
        }
    }
}

