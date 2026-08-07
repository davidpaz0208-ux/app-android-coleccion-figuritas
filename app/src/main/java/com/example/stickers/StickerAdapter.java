package com.example.stickers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StickerAdapter extends ArrayAdapter<Sticker> {

    private Context context;
    private List<Sticker> list;
    private DBHelper db;
    private String username;

    private boolean isMarket;
    private boolean isSearch;
    private boolean isRepeatedScreen;

    public StickerAdapter(
            Context context,
            List<Sticker> list,
            boolean isMarket,
            boolean isSearch,
            boolean isRepeatedScreen,
            String username
    ) {
        super(context, R.layout.item_sticker, list);

        this.context = context;
        this.list = list;
        this.isMarket = isMarket;
        this.isSearch = isSearch;
        this.isRepeatedScreen = isRepeatedScreen;
        this.username = username;
        this.db = new DBHelper(context);
    }

    static class ViewHolder {

        TextView tvNumber;
        TextView tvPlayer;
        TextView tvTeam;
        TextView tvRarity;
        TextView tvPrice;

        Button btnAction;
        Button btnTrade;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_sticker, parent, false);

            holder = new ViewHolder();

            holder.tvNumber = convertView.findViewById(R.id.stickerNumber);
            holder.tvPlayer = convertView.findViewById(R.id.stickerPlayer);
            holder.tvTeam = convertView.findViewById(R.id.stickerTeam);
            holder.tvRarity = convertView.findViewById(R.id.stickerRarity);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);

            holder.btnAction = convertView.findViewById(R.id.btnBuy);
            holder.btnTrade = convertView.findViewById(R.id.btnTrade);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Sticker sticker = getItem(position);
        if (sticker == null) return convertView;

        int price = sticker.getPrice();

        holder.tvNumber.setText("#" + sticker.getNumber());
        holder.tvPlayer.setText(sticker.getName());
        holder.tvTeam.setText(sticker.getTeam());
        holder.tvRarity.setText(sticker.getRarity());
        holder.tvPrice.setText("$" + price);

        // =========================
        // 🎯 RARITY COLOR
        // =========================
        switch (sticker.getRarity()) {
            case "Legend":
                holder.tvRarity.setTextColor(Color.YELLOW);
                break;
            case "Rare":
                holder.tvRarity.setTextColor(Color.CYAN);
                break;
            default:
                holder.tvRarity.setTextColor(Color.WHITE);
                break;
        }

        // =========================
        // 🔁 REPETIDAS (FUT MODE)
        // =========================
        if (isRepeatedScreen) {

            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnTrade.setVisibility(View.VISIBLE);

            holder.btnAction.setText("Vender");

            holder.btnAction.setOnClickListener(v -> {

                int sellPrice = sticker.getPrice() / 2;

                db.deleteSticker(sticker.getId(), username);
                db.updateBudget(db.getBudget() + sellPrice);

                Toast.makeText(context,
                        "Vendiste $" + sellPrice,
                        Toast.LENGTH_SHORT).show();

                list.remove(sticker);
                notifyDataSetChanged();
            });

            holder.btnTrade.setOnClickListener(v -> {

                if (context instanceof RepeatedStickersActivity) {

                    ((RepeatedStickersActivity) context)
                            .tradeSticker(sticker);
                }

                Toast.makeText(context,
                        "Solicitud de intercambio enviada",
                        Toast.LENGTH_SHORT).show();
            });

            return convertView;
        }

        // =========================
        // 🔍 SEARCH MODE
        // =========================
        if (isSearch) {
            holder.btnAction.setVisibility(View.GONE);
            holder.btnTrade.setVisibility(View.GONE);
            return convertView;
        }

        holder.btnTrade.setVisibility(View.GONE);

        // =========================
        // 🛒 MARKET MODE
        // =========================
        if (isMarket) {

            holder.btnAction.setText("Comprar");

            holder.btnAction.setOnClickListener(v -> {

                int budget = db.getBudget();

                if (budget < price) {
                    Toast.makeText(context,
                            "No tenés dinero 💸",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.existsSticker(
                        sticker.getNumber(),
                        sticker.getTeam(),
                        username
                )) {
                    Toast.makeText(context,
                            "Ya la tenés ⚠️",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                db.insertSticker(sticker, username);
                db.updateBudget(budget - price);

                list.remove(sticker);
                notifyDataSetChanged();

                Toast.makeText(context,
                        "Compraste a " + sticker.getName(),
                        Toast.LENGTH_SHORT).show();

                if (context instanceof StickerListActivity) {
                    ((StickerListActivity) context).updateBudgetUI();
                }
            });

        } else {

            // =========================
            // 📘 ALBUM MODE (SELL)
            // =========================
            holder.btnAction.setText("Vender");

            holder.btnAction.setOnClickListener(v -> {

                int sellPrice = sticker.getPrice() / 2;

                db.deleteSticker(sticker.getId(), username);
                db.updateBudget(db.getBudget() + sellPrice);

                list.remove(sticker);
                notifyDataSetChanged();

                Toast.makeText(context,
                        "Vendiste $" + sellPrice,
                        Toast.LENGTH_SHORT).show();

                if (context instanceof StickerListActivity) {
                    ((StickerListActivity) context).updateBudgetUI();
                }
            });
        }

        return convertView;
    }
}