package com.example.stickers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class AlbumProgressAdapter
        extends ArrayAdapter<AlbumProgress> {

    public AlbumProgressAdapter(
            Context context,
            List<AlbumProgress> list
    ) {
        super(context, 0, list);
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
                            R.layout.item_album_team,
                            parent,
                            false
                    );
        }

        AlbumProgress item =
                getItem(position);

        TextView txtTeam =
                convertView.findViewById(
                        R.id.txtTeam
                );

        TextView txtProgress =
                convertView.findViewById(
                        R.id.txtProgress
                );

        ProgressBar progress =
                convertView.findViewById(
                        R.id.progressTeam
                );

        txtTeam.setText(
                "🏆 " +
                        item.getTeam()
        );

        txtProgress.setText(
                item.getOwned()
                        + " / "
                        + item.getTotal()
                        + " figuritas"
        );

        progress.setMax(
                item.getTotal()
        );

        progress.setProgress(
                item.getOwned()
        );

        return convertView;
    }
}