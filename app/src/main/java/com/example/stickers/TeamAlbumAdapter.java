package com.example.stickers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TeamAlbumAdapter
        extends ArrayAdapter<TeamStickerItem> {

    public TeamAlbumAdapter(
            Context context,
            List<TeamStickerItem> items
    ) {
        super(context, 0, items);
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        if (convertView == null) {

            convertView =
                    LayoutInflater.from(
                            getContext()
                    ).inflate(
                            R.layout.item_team_sticker,
                            parent,
                            false
                    );
        }

        TeamStickerItem item =
                getItem(position);

        TextView txt =
                convertView.findViewById(
                        R.id.txtSticker
                );

        if (item.isObtained()) {

            txt.setText(
                    "✅ " +
                            item.getName()
            );

        } else {

            txt.setText(
                    "⬜ " +
                            item.getName()
            );
        }

        return convertView;
    }
}