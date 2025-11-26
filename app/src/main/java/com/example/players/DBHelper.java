package com.example.players;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import com.example.players.Message;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PlayerDB";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "role TEXT" +
                    ");";

    private static final String CREATE_TABLE_PLAYERS =
            "CREATE TABLE IF NOT EXISTS players (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "position TEXT" +
                    ");";

    private static final String CREATE_TABLE_PLAYER_STATS =
            "CREATE TABLE IF NOT EXISTS player_stats (" +
                    "player_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "player_name TEXT NOT NULL, " +
                    "goals INTEGER DEFAULT 0, " +
                    "assists INTEGER DEFAULT 0, " +
                    "matches_played INTEGER DEFAULT 0, " +
                    "yellow_cards INTEGER DEFAULT 0, " +
                    "red_cards INTEGER DEFAULT 0" +
                    ");";

    private static final String CREATE_TABLE_MESSAGES =
            "CREATE TABLE IF NOT EXISTS messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "sender TEXT NOT NULL," +
                    "receiver TEXT NOT NULL," +
                    "message TEXT NOT NULL," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PLAYERS);
        db.execSQL(CREATE_TABLE_PLAYER_STATS);
        db.execSQL(CREATE_TABLE_MESSAGES);

        ContentValues values = new ContentValues();
        values.put("username", "admin");
        values.put("password", hashPassword("123"));
        values.put("role", "admin");
        db.insert("users", null, values);

        values.clear();
        values.put("username", "user");
        values.put("password", hashPassword("456"));
        values.put("role", "user");
        db.insert("users", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE messages ADD COLUMN receiver TEXT NOT NULL DEFAULT ''");
        }
    }

    public SQLiteDatabase getReadableDB() {

        return this.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDB() {

        return this.getWritableDatabase();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }

    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDB();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();

            return BCrypt.checkpw(password, storedHash);
        }

        return false;
    }


    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDB();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE username = ?", new String[]{username});
        String role = null;

        if (cursor != null && cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndex("role"));
        }
        cursor.close();
        return role;
    }

    public boolean insertPlayer(String playerName, String playerPosition) {
        SQLiteDatabase db = this.getWritableDB();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", playerName);
            values.put("position", playerPosition);
            long result1 = db.insert("players", null, values);

            Random random = new Random();
            int goals = random.nextInt(50);
            int assists = random.nextInt(50);
            int matches = random.nextInt(37) + 1;
            int yellowCards = random.nextInt(10);
            int redCards = random.nextInt(3);

            ContentValues statsValues = new ContentValues();
            statsValues.put("player_name", playerName);
            statsValues.put("goals", goals);
            statsValues.put("assists", assists);
            statsValues.put("matches_played", matches);
            statsValues.put("yellow_cards", yellowCards);
            statsValues.put("red_cards", redCards);
            long result2 = db.insert("player_stats", null, statsValues);

            if (result1 != -1 && result2 != -1) {
                db.setTransactionSuccessful();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public boolean updatePlayerPosition(String playerName, String newPosition) {
        SQLiteDatabase db = this.getWritableDB();
        ContentValues values = new ContentValues();
        values.put("position", newPosition);

        int result = db.update("players", values, "name = ?", new String[]{playerName});
        return result > 0;
    }

    public boolean deletePlayer(String playerName) {
        SQLiteDatabase db = this.getWritableDB();
        int result = db.delete("players", "name = ?", new String[]{playerName});
        return result > 0;
    }

    public PlayerStats getPlayerStats(String playerName) {
        SQLiteDatabase db = this.getReadableDB();
        Cursor cursor = db.rawQuery("SELECT * FROM player_stats WHERE player_name = ?", new String[]{playerName});
        PlayerStats playerStats = null;

        if (cursor != null && cursor.moveToFirst()) {
            int goals = cursor.getInt(cursor.getColumnIndex("goals"));
            int assists = cursor.getInt(cursor.getColumnIndex("assists"));
            int matchesPlayed = cursor.getInt(cursor.getColumnIndex("matches_played"));
            int yellowCards = cursor.getInt(cursor.getColumnIndex("yellow_cards"));
            int redCards = cursor.getInt(cursor.getColumnIndex("red_cards"));

            playerStats = new PlayerStats(goals, assists, matchesPlayed, yellowCards, redCards);
            cursor.close();
        }

        return playerStats;
    }

    public boolean insertPlayerStats(String playerName, int goals, int assists, int matchesPlayed, int yellowCards, int redCards) {
        SQLiteDatabase db = this.getWritableDB();
        ContentValues values = new ContentValues();
        values.put("player_name", playerName);
        values.put("goals", goals);
        values.put("assists", assists);
        values.put("matches_played", matchesPlayed);
        values.put("yellow_cards", yellowCards);
        values.put("red_cards", redCards);

        long result = db.insert("player_stats", null, values);
        return result != -1;
    }

    public boolean insertMessage(String sender, String receiver, String message, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sender", sender);
        values.put("receiver", receiver);
        values.put("message", message);
        values.put("timestamp", timestamp);

        long result = db.insert("messages", null, values);
        return result != -1;
    }

    public List<Message> getAllMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM messages ORDER BY timestamp DESC", null);

        List<Message> messages = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndex("sender"));
                String receiver = cursor.getString(cursor.getColumnIndex("receiver"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

                messages.add(new Message(sender, receiver, message, timestamp));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return messages;
    }

    public boolean deleteMessage(int messageId) {
        SQLiteDatabase db = this.getWritableDB();
        int result = db.delete("messages", "id = ?", new String[]{String.valueOf(messageId)});
        return result > 0;
    }

    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDB();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean insertUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDB();
        ContentValues values = new ContentValues();

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        values.put("username", username);
        values.put("password", hashedPassword);
        values.put("role", role);

        long result = db.insert("users", null, values);
        return result != -1;
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT username FROM users WHERE username != 'admin'", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex("username"));
                users.add(username);
            }
            cursor.close();
        }

        return users;
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDB();
        int result = db.delete("users", "username = ?", new String[]{username});
        return result > 0;
    }

    public String[] getUserData(String username) {
        SQLiteDatabase db = this.getReadableDB();
        Cursor cursor = db.rawQuery("SELECT username, password, role FROM users WHERE username=?", new String[]{username});
        if (cursor.moveToFirst()) {
            String[] userData = {
                    cursor.getString(0), // username
                    cursor.getString(1), // password
                    cursor.getString(2)  // role
            };
            cursor.close();
            return userData;
        }
        cursor.close();
        return null;
    }

    public boolean updateUser(String oldUsername, String newUsername, String newPassword, String newRole) {
        SQLiteDatabase db = this.getWritableDB();
        ContentValues values = new ContentValues();

        if (newPassword != null && !newPassword.isEmpty()) {
            newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            values.put("password", newPassword);
        }

        values.put("username", newUsername);
        values.put("role", newRole);

        int result = db.update("users", values, "username = ?", new String[]{oldUsername});
        return result > 0;
    }

    public List<Message> getMessagesForUser(String currentUser) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM messages WHERE sender = ? OR receiver = ? ORDER BY timestamp DESC",
                new String[]{currentUser, currentUser});

        List<Message> messages = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndex("sender"));
                String receiver = cursor.getString(cursor.getColumnIndex("receiver"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

                messages.add(new Message(sender, receiver, message, timestamp));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return messages;
    }

    public int deleteAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("messages", null, null);
        db.close();
        return rowsAffected;
    }
}

