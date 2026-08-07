package com.example.stickers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class AlbumAdapter extends ArrayAdapter<AlbumItem> {

    public AlbumAdapter(
            Context context,
            List<AlbumItem> list
    ) {
        super(context, 0, list);
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        AlbumItem item = getItem(position);

        LinearLayout layout =
                new LinearLayout(getContext());

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                20,
                20,
                20,
                20
        );

        layout.setGravity(Gravity.CENTER);

        layout.setMinimumHeight(220);

        TextView tvNumber =
                new TextView(getContext());

        tvNumber.setTextSize(18);
        tvNumber.setTypeface(
                null,
                Typeface.BOLD
        );

        tvNumber.setTextColor(Color.WHITE);

        TextView tvName =
                new TextView(getContext());

        tvName.setTextSize(15);
        tvName.setTextColor(Color.WHITE);
        tvName.setGravity(Gravity.CENTER);

        TextView tvTeam =
                new TextView(getContext());

        tvTeam.setTextSize(13);
        tvTeam.setTextColor(Color.LTGRAY);
        tvTeam.setGravity(Gravity.CENTER);

        TextView tvRarity =
                new TextView(getContext());

        tvRarity.setTextSize(12);
        tvRarity.setGravity(Gravity.CENTER);

        TextView tvRepeated =
                new TextView(getContext());

        tvRepeated.setTextSize(12);
        tvRepeated.setGravity(Gravity.CENTER);

        if (item.isOwned()) {

            Sticker s = item.getSticker();

            tvNumber.setText(
                    "#" + s.getNumber()
            );

            tvName.setText(
                    s.getName()
            );

            tvTeam.setText(
                    s.getTeam()
            );

            tvRarity.setText(
                    "⭐ " + s.getRarity()
            );

            tvRarity.setTextColor(
                    Color.WHITE
            );

            if (s.isRepeated()) {

                tvRepeated.setText(
                        "🔁 Repetida"
                );

                tvRepeated.setTextColor(
                        Color.YELLOW
                );
            }

            if ("Legendaria".equalsIgnoreCase(
                    s.getRarity()
            )) {

                layout.setBackgroundColor(
                        Color.parseColor(
                                "#B8860B"
                        )
                );

            } else if ("Épica".equalsIgnoreCase(
                    s.getRarity()
            )) {

                layout.setBackgroundColor(
                        Color.parseColor(
                                "#6A1B9A"
                        )
                );

            } else if ("Rara".equalsIgnoreCase(
                    s.getRarity()
            )) {

                layout.setBackgroundColor(
                        Color.parseColor(
                                "#1565C0"
                        )
                );

            } else {

                layout.setBackgroundColor(
                        Color.parseColor(
                                "#424242"
                        )
                );
            }

        } else {

            Sticker s = item.getSticker();

            tvNumber.setText(
                    "#" + s.getNumber()
            );

            tvName.setText(
                    "FALTANTE"
            );

            tvTeam.setText(
                    s.getTeam()
            );

            tvRarity.setText(
                    "❓ Desconocida"
            );

            tvRarity.setTextColor(
                    Color.GRAY
            );

            layout.setBackgroundColor(
                    Color.parseColor(
                            "#202020"
                    )
            );
        }

        layout.addView(tvNumber);
        layout.addView(tvName);
        layout.addView(tvTeam);
        layout.addView(tvRarity);
        layout.addView(tvRepeated);

        return layout;
    }
}