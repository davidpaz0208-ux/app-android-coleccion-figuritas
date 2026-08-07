package com.example.stickers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;

import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private CardView cardCollection;
    private CardView cardRepeated;
    private Toolbar toolbar;

    private TextView txtLastStickers;

    private String currentUserRole;
    private String currentUser;

    private TextView txtWelcome;
    private TextView txtProgress;
    private TextView txtCollectionCount;

    private CardView cardAlbum;
    private TextView txtRepeatedCount;
    private TextView txtMissingCount;

    private ProgressBar progressAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        cardCollection = findViewById(R.id.cardCollection);
        cardRepeated = findViewById(R.id.cardRepeated);

        cardAlbum = findViewById(R.id.cardAlbum);

        cardAlbum.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            AlbumActivity.class
                    );

            startActivity(intent);
        });

        cardCollection.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            StickerListActivity.class
                    );

            intent.putExtra("mode", "collection");

            startActivity(intent);
        });

        cardRepeated.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            RepeatedStickersActivity.class
                    );

            startActivity(intent);
        });

        txtWelcome = findViewById(R.id.txtWelcome);
        txtProgress = findViewById(R.id.txtProgress);
        txtCollectionCount = findViewById(R.id.txtCollectionCount);
        txtRepeatedCount = findViewById(R.id.txtRepeatedCount);
        txtMissingCount = findViewById(R.id.txtMissingCount);

        progressAlbum = findViewById(R.id.progressAlbum);

        setSupportActionBar(toolbar);

        txtLastStickers = findViewById(R.id.txtLastStickers);

        cardRepeated.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RepeatedStickersActivity.class
            );

            startActivity(intent);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        currentUser = preferences.getString("currentUser", null);
        currentUserRole = preferences.getString("userRole", null);

        if (currentUser == null || currentUserRole == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            finish();

            return;
        }

        updateMenuVisibilityForUserRole();

        loadDashboard();

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_market) {

                Intent intent = new Intent(MainActivity.this, StickerListActivity.class);

                intent.putExtra("mode", "market");

                startActivity(intent);


            } else if (id == R.id.nav_edit_sticker) {

                startActivity(new Intent(MainActivity.this, UpdateStickerActivity.class));

            } else if (id == R.id.nav_delete_sticker) {

                startActivity(new Intent(MainActivity.this, DeleteStickerActivity.class));

            } else if (id == R.id.nav_chat) {

                Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);

                chatIntent.putExtra("currentUser", currentUser);

                chatIntent.putExtra("userRole", currentUserRole);

                startActivity(chatIntent);


            } else if (id == R.id.nav_manage_users) {

                startActivity(new Intent(MainActivity.this, UserManagementActivity.class));

            } else if (id == R.id.nav_filter_rarity) {

                startActivity(
                        new Intent(
                                this,
                                FilterByRarityActivity.class
                        )
                );

            } else if (id == R.id.nav_logout) {

                logout();
            } else if (id == R.id.nav_trades) {

                startActivity(
                        new Intent(
                                MainActivity.this,
                                TradeRequestsActivity.class
                        )
                );
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

            navigationView.getMenu().findItem(R.id.nav_edit_sticker).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_delete_sticker).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_manage_users).setVisible(false);

        } else if ("admin".equals(currentUserRole)) {

            navigationView.getMenu().findItem(R.id.nav_edit_sticker).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_delete_sticker).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_manage_users).setVisible(true);
        }
    }
    private void loadDashboard() {

        DBHelper dbHelper = new DBHelper(this);

        List<Sticker> collection =
                dbHelper.getAllStickers(currentUser);

        int collectionCount = collection.size();

        int repeatedCount = 0;

        List<String> processed = new ArrayList<>();

        for (Sticker sticker : collection) {

            String key =
                    sticker.getNumber() + "_" + sticker.getTeam();

            if (processed.contains(key)) {
                continue;
            }

            int count = 0;

            for (Sticker other : collection) {

                if (other.getNumber() == sticker.getNumber()
                        && other.getTeam().equals(sticker.getTeam())) {

                    count++;
                }
            }

            if (count > 1) {

                repeatedCount += (count - 1);
            }

            processed.add(key);
        }

        int totalAlbum = loadTotalAlbum();

        int missingCount =
                Math.max(0,
                        totalAlbum - collectionCount);

        int progress =
                totalAlbum == 0
                        ? 0
                        : (collectionCount * 100) / totalAlbum;

        txtWelcome.setText(
                "⚽ Bienvenido, " + currentUser
        );

        txtCollectionCount.setText(
                collectionCount + " figuritas"
        );

        txtRepeatedCount.setText(
                repeatedCount + " repetidas"
        );

        txtMissingCount.setText(
                missingCount + " faltantes"
        );

        txtProgress.setText(
                collectionCount + " / " +
                        totalAlbum +
                        " figuritas (" +
                        progress + "%)"
        );

        progressAlbum.setProgress(progress);


        StringBuilder last = new StringBuilder();

        int start =
                Math.max(0,
                        collection.size() - 3);

        for (int i = start;
             i < collection.size();
             i++) {

            last.append("• ")
                    .append(collection.get(i).getName())
                    .append("\n");
        }

        if (collection.isEmpty()) {

            txtLastStickers.setText(
                    "Sin figuritas"
            );

        } else {

            txtLastStickers.setText(
                    last.toString()
            );
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (currentUser != null) {
            loadDashboard();
        }
    }
    private int loadTotalAlbum() {

        try {

            java.io.InputStream is =
                    getAssets().open("mock_stickers.json");

            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            org.json.JSONArray array =
                    new org.json.JSONArray(
                            new String(buffer)
                    );

            return array.length();

        } catch (Exception e) {

            e.printStackTrace();
            return 0;
        }
    }
}
