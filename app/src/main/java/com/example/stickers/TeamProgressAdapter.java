package com.example.stickers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class TeamProgressAdapter
        extends ArrayAdapter<TeamProgress> {

    public TeamProgressAdapter(
            Context context,
            List<TeamProgress> teams
    ) {
        super(context, 0, teams);
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    ) {

        TeamProgress team = getItem(position);

        LinearLayout layout =
                new LinearLayout(getContext());

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(30,30,30,30);

        layout.setBackgroundColor(
                Color.parseColor("#2E2E2E")
        );

        TextView tvTeam =
                new TextView(getContext());

        tvTeam.setText(team.getTeam());

        tvTeam.setTextSize(22);

        tvTeam.setTextColor(Color.WHITE);

        TextView tvCount =
                new TextView(getContext());

        tvCount.setText(
                team.getCount()
                        + " figuritas"
        );

        tvCount.setTextColor(Color.LTGRAY);

        layout.addView(tvTeam);
        layout.addView(tvCount);

        return layout;
    }
}