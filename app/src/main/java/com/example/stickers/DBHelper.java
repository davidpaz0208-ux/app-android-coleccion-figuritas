package com.example.stickers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stickers.db";

    private static final int DATABASE_VERSION = 11;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "role TEXT)");

        db.execSQL("CREATE TABLE stickers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number INTEGER, " +
                "name TEXT, " +
                "team TEXT, " +
                "rarity TEXT, " +
                "price INTEGER, " +
                        "repeated INTEGER DEFAULT 0, " +
                "username TEXT)");

        db.execSQL("CREATE TABLE messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender TEXT, " +
                "receiver TEXT, " +
                "message TEXT, " +
                "timestamp TEXT)");

        db.execSQL(
                "CREATE TABLE market (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "sticker_id INTEGER, " +
                        "seller TEXT, " +
                        "price INTEGER, " +
                        "status TEXT)");


                db.execSQL(
                                "CREATE TABLE trade_offers (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "from_user TEXT NOT NULL, " +
                                        "to_user TEXT NOT NULL, " +
                                        "offered_sticker_id INTEGER NOT NULL, " +
                                        "requested_sticker_id INTEGER, " +
                                        "status TEXT DEFAULT 'pending', " +
                                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                                        ")"
                        );


        db.execSQL("CREATE TABLE budget (" +
                "id INTEGER PRIMARY KEY, " +
                "amount INTEGER)");

        ContentValues budget = new ContentValues();
        budget.put("id", 1);
        budget.put("amount", 1000000);

        db.insert("budget", null, budget);

        insertDefaultUsers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS stickers");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS messages");
        db.execSQL("DROP TABLE IF EXISTS market");
        db.execSQL("DROP TABLE IF EXISTS trade_offers");
        db.execSQL("DROP TABLE IF EXISTS budget");

        onCreate(db);
    }

    private void insertDefaultUsers(SQLiteDatabase db) {

        String hash = hashPassword("123");

        ContentValues admin = new ContentValues();
        admin.put("username", "admin");
        admin.put("password", hash);
        admin.put("role", "admin");

        db.insert("users", null, admin);

        ContentValues user = new ContentValues();
        user.put("username", "user");
        user.put("password", hash);
        user.put("role", "user");

        db.insert("users", null, user);
    }

    public boolean registerUser(String username, String password, String role) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", hashPassword(password));
        values.put("role", role);

        long result = db.insert("users", null, values);

        db.close();

        return result != -1;
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean isUsernameTaken(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT username FROM users WHERE username=?",
                new String[]{username}
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }

    public String getUserRole(String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE username=?",
                new String[]{username}
        );

        String role = "";

        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return role;
    }

    public boolean existsSticker(int number, String team, String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM stickers WHERE number=? AND team=? AND username=?",
                new String[]{
                        String.valueOf(number),
                        team,
                        username
                }
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return exists;
    }

    public boolean insertSticker(Sticker s, String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("number", s.getNumber());
        values.put("name", s.getName());
        values.put("team", s.getTeam());
        values.put("rarity", s.getRarity());
        values.put("price", s.getPrice());

        values.put(
                "repeated",
                s.isRepeated() ? 1 : 0
        );

        values.put("username", username);

        long result =
                db.insert(
                        "stickers",
                        null,
                        values
                );

        db.close();

        return result != -1;
    }

    public List<Sticker> getAllStickers(String username) {

        List<Sticker> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM stickers WHERE username = ?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            do {
                Sticker s = new Sticker(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("number")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("team")),
                        cursor.getString(cursor.getColumnIndexOrThrow("rarity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("repeated")) == 1
                );
                list.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public boolean deleteSticker(int id, String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(
                "stickers",
                "id=? AND username=?",
                new String[]{
                        String.valueOf(id),
                        username
                }
        );

        db.close();

        return rows > 0;
    }

    public boolean deleteStickerByData(int number, String team, String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(
                "stickers",
                "number=? AND team=? AND username=?",
                new String[]{
                        String.valueOf(number),
                        team,
                        username
                }
        );

        db.close();

        return rows > 0;
    }

    public boolean updateStickerName(int id, String newPlayer) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", newPlayer);

        int rows = db.update(
                "stickers",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return rows > 0;
    }

    public boolean updateStickerTeam(int id, String newTeam) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("team", newTeam);

        int rows = db.update(
                "stickers",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return rows > 0;
    }

    public int getBudget() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT amount FROM budget WHERE id=1",
                null
        );

        int amount = 0;

        if (cursor.moveToFirst()) {
            amount = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return amount;
    }

    public void updateBudget(int newAmount) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("amount", newAmount);

        db.update("budget", values, "id=1", null);

        db.close();
    }

    public boolean insertMessage(
            String sender,
            String receiver,
            String message,
            String timestamp
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sender", sender);
        values.put("receiver", receiver);
        values.put("message", message);
        values.put("timestamp", timestamp);

        long result = db.insert("messages", null, values);

        db.close();

        return result != -1;
    }

    public List<String> getConversation(String user1, String user2) {

        List<String> messages = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT sender, message FROM messages " +
                        "WHERE (sender=? AND receiver=?) " +
                        "OR (sender=? AND receiver=?) " +
                        "ORDER BY id ASC",
                new String[]{user1, user2, user2, user1}
        );

        if (cursor.moveToFirst()) {

            do {

                messages.add(
                        cursor.getString(0)
                                + ": "
                                + cursor.getString(1)
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messages;
    }
    public List<String> getAllUsers() {

        List<String> users = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT username FROM users WHERE username <> 'admin'",
                null
        );

        if (cursor.moveToFirst()) {

            do {
                users.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return users;
    }

    public boolean updateUser(
            String oldUsername,
            String newUsername,
            String newPassword,
            String newRole
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("password", hashPassword(newPassword));
        values.put("role", newRole);

        int rows = db.update(
                "users",
                values,
                "username=?",
                new String[]{oldUsername}
        );

        db.close();

        return rows > 0;
    }
    public boolean deleteUser(String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(
                "users",
                "username=?",
                new String[]{username}
        );

        db.close();

        return rows > 0;
    }
    public int getTotalStickerCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM stickers",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }
    public List<Sticker> getAllStickers() {

        List<Sticker> stickers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM stickers",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                Sticker sticker = new Sticker();

                sticker.setId(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        )
                );

                sticker.setNumber(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("number")
                        )
                );

                sticker.setName(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")
                        )
                );

                sticker.setTeam(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("team")
                        )
                );

                sticker.setRarity(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("rarity")
                        )
                );

                sticker.setRepeated(
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("repeated")
                        ) == 1
                );

                stickers.add(sticker);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return stickers;
    }
    public List<TeamProgress> getTeamProgress(String username) {

        List<TeamProgress> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT team, COUNT(*) as total " +
                        "FROM stickers " +
                        "WHERE username=? " +
                        "GROUP BY team " +
                        "ORDER BY team",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {

            do {

                String team =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("team")
                        );

                int total =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("total")
                        );

                list.add(new TeamProgress(team, total));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }
    public boolean hasSticker(
            String username,
            String name
    ) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM stickers " +
                        "WHERE username=? AND name=?",
                new String[]{
                        username,
                        name
                }
        );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    public void createWalletIfNotExists(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT username FROM user_wallet WHERE username=?",
                new String[]{username}
        );

        if (!c.moveToFirst()) {
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("amount", 100000);

            db.insert("user_wallet", null, cv);
        }

        c.close();
    }

    public int getMoney(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT amount FROM user_wallet WHERE username=?",
                new String[]{username}
        );

        int money = 0;
        if (c.moveToFirst()) {
            money = c.getInt(0);
        }

        c.close();
        return money;
    }

    public void updateMoney(String username, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("amount", amount);

        db.update("user_wallet", cv, "username=?",
                new String[]{username});
    }
    public void sellToMarket(int stickerId, String seller, int price) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("sticker_id", stickerId);
        cv.put("seller", seller);
        cv.put("price", price);
        cv.put("status", "open");

        db.insert("market", null, cv);
    }
    public boolean buyFromMarket(int marketId, String buyer) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT sticker_id, seller, price FROM market WHERE id=?",
                new String[]{String.valueOf(marketId)}
        );

        if (!c.moveToFirst()) return false;

        int stickerId = c.getInt(0);
        String seller = c.getString(1);
        int price = c.getInt(2);

        c.close();

        transferSticker(stickerId, seller, buyer);

        updateMoney(buyer, getMoney(buyer) - price);
        updateMoney(seller, getMoney(seller) + price);

        return true;
    }

    public boolean transferSticker(int stickerId,
                                   String fromUser,
                                   String toUser) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("username", toUser);
        values.put("repeated", 0);

        int rows = db.update(
                "stickers",
                values,
                "id=? AND username=?",
                new String[]{
                        String.valueOf(stickerId),
                        fromUser
                }
        );

        return rows > 0;
    }

    public List<Sticker> getRepeatedStickers(String username) {

        List<Sticker> repeated = new ArrayList<>();

        List<Sticker> collection = getAllStickers(username);

        for (Sticker s : collection) {

            int count = 0;

            for (Sticker x : collection) {

                if (s.getNumber() == x.getNumber()
                        && s.getTeam().equals(x.getTeam())) {

                    count++;
                }
            }

            if (count > 1) {

                boolean alreadyAdded = false;

                for (Sticker r : repeated) {

                    if (r.getNumber() == s.getNumber()
                            && r.getTeam().equals(s.getTeam())) {

                        alreadyAdded = true;
                        break;
                    }
                }

                if (!alreadyAdded) {
                    repeated.add(s);
                }
            }
        }

        return repeated;
    }

    public void createTradeOffer(String fromUser, String toUser,
                                 int offeredId, int requestedId) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("from_user", fromUser);
        values.put("to_user", toUser);
        values.put("offered_sticker_id", offeredId);
        values.put("requested_sticker_id", requestedId);
        values.put("status", "pending");

        db.insert("trade_offers", null, values);
    }

    public List<TradeOffer> getIncomingTrades(String username) {

        List<TradeOffer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM trade_offers WHERE to_user=? AND status='pending'",
                new String[]{username}
        );

        if (c.moveToFirst()) {
            do {
                TradeOffer t = new TradeOffer();

                t.id = c.getInt(c.getColumnIndexOrThrow("id"));
                t.fromUser = c.getString(c.getColumnIndexOrThrow("from_user"));
                t.toUser = c.getString(c.getColumnIndexOrThrow("to_user"));
                t.offeredStickerId = c.getInt(c.getColumnIndexOrThrow("offered_sticker_id"));
                t.requestedStickerId = c.getInt(c.getColumnIndexOrThrow("requested_sticker_id"));
                t.status = c.getString(c.getColumnIndexOrThrow("status"));

                list.add(t);

            } while (c.moveToNext());
        }

        c.close();
        return list;
    }
    public void acceptTrade(TradeOffer trade) {

        Sticker offered =
                getStickerById(
                        trade.offeredStickerId,
                        trade.fromUser
                );

        Sticker requested =
                getStickerById(
                        trade.requestedStickerId,
                        trade.toUser
                );

        if (offered == null || requested == null) {
            return;
        }

        deleteSticker(
                trade.offeredStickerId,
                trade.fromUser
        );

        deleteSticker(
                trade.requestedStickerId,
                trade.toUser
        );

        insertSticker(
                offered,
                trade.toUser
        );

        insertSticker(
                requested,
                trade.fromUser
        );

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put("status", "accepted");

        db.update(
                "trade_offers",
                values,
                "id=?",
                new String[]{
                        String.valueOf(trade.id)
                }
        );

        db.close();
    }

    public void rejectTrade(int tradeId) {

        ContentValues values = new ContentValues();
        values.put("status", "rejected");

        SQLiteDatabase db = this.getWritableDatabase();

        db.update("trade_offers", values, "id=?",
                new String[]{String.valueOf(tradeId)});
    }
    public Sticker getStickerById(int id, String username) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM stickers WHERE id=? AND username=?",
                new String[]{String.valueOf(id), username}
        );

        if (cursor.moveToFirst()) {

            Sticker s = new Sticker(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("number")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("team")),
                    cursor.getString(cursor.getColumnIndexOrThrow("rarity")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("repeated")) == 1
            );

            cursor.close();
            return s;
        }

        cursor.close();
        return null;
    }

    public void clearTradeOffers() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("trade_offers", null, null);

        db.close();
    }

    public List<Sticker> getStickersByRarity(
            String username,
            String rarity
    ) {

        List<Sticker> list = new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM stickers " +
                        "WHERE username=? AND rarity=?",
                new String[]{
                        username,
                        rarity
                }
        );

        if (cursor.moveToFirst()) {

            do {

                list.add(
                        new Sticker(
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow("id")
                                ),
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow("number")
                                ),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow("name")
                                ),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow("team")
                                ),
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow("rarity")
                                ),
                                cursor.getInt(
                                        cursor.getColumnIndexOrThrow("repeated")
                                ) == 1
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }
}