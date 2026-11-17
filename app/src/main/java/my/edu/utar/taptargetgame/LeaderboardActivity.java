package my.edu.utar.taptargetgame;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    LinearLayout leaderboardList;
    LeaderboardManager leaderboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Connect XML components
        leaderboardList = findViewById(R.id.leaderboardList);
        Button btnBack = findViewById(R.id.btnBack);

        // Initialize LeaderboardManager
        leaderboardManager = new LeaderboardManager(this);

        // Display saved scores
        displayLeaderboard();

        // Back to Home button
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LeaderboardActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    // Display leaderboard scores

    private void displayLeaderboard() {

        leaderboardList.removeAllViews();

        ArrayList<LeaderboardManager.ScoreEntry> scores =
                leaderboardManager.getScores();

        // Check whether leaderboard is empty
        if (scores.isEmpty()) {

            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("NO SCORES YET\n\nPLAY A GAME TO GET STARTED!");
            emptyMessage.setTextColor(Color.rgb(169, 176, 204));
            emptyMessage.setTextSize(14);
            emptyMessage.setGravity(Gravity.CENTER);
            emptyMessage.setPadding(20, 60, 20, 60);

            leaderboardList.addView(emptyMessage);
            return;
        }

        // TOP 3 PODIUM
        if (scores.size() >= 3) {

            LinearLayout podium = new LinearLayout(this);
            podium.setOrientation(LinearLayout.HORIZONTAL);
            podium.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            LinearLayout.LayoutParams podiumParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            podiumParams.setMargins(0, 15, 0, 25);

            // 2nd place - Silver
            podium.addView(createPodiumCard(
                    scores.get(1), 2,
                    Color.rgb(48, 55, 74),
                    Color.rgb(203, 213, 225),
                    135
            ), new LinearLayout.LayoutParams(
                    0, dp(135), 1
            ));

            // 1st place - Gold
            podium.addView(createPodiumCard(
                    scores.get(0), 1,
                    Color.rgb(73, 61, 33),
                    Color.rgb(251, 191, 36),
                    175
            ), new LinearLayout.LayoutParams(
                    0, dp(175), 1
            ));

            // 3rd place - Bronze
            podium.addView(createPodiumCard(
                    scores.get(2), 3,
                    Color.rgb(73, 53, 42),
                    Color.rgb(220, 167, 123),
                    120
            ), new LinearLayout.LayoutParams(
                    0, dp(120), 1
            ));

            leaderboardList.addView(podium);
        }

        // OTHER PLAYERS (4TH TO 25TH)
        int startIndex = Math.min(3, scores.size());

        for (int i = startIndex; i < scores.size(); i++) {

            LeaderboardManager.ScoreEntry entry = scores.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(12, 13, 12, 13);

            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.rgb(25, 25, 54));
            background.setCornerRadius(dp(12));
            background.setStroke(dp(1), Color.rgb(81, 68, 119));

            row.setBackground(background);

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            rowParams.setMargins(0, dp(4), 0, dp(4));

            // Rank
            TextView txtRank = new TextView(this);
            txtRank.setLayoutParams(new LinearLayout.LayoutParams(
                    dp(35),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            txtRank.setText(String.valueOf(i + 1));
            txtRank.setTextColor(Color.WHITE);
            txtRank.setTextSize(14);
            txtRank.setTypeface(null, Typeface.BOLD);

            // Player name
            TextView txtName = new TextView(this);
            txtName.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            ));
            txtName.setText(entry.name);
            txtName.setTextColor(Color.WHITE);
            txtName.setTextSize(14);

            // Score
            TextView txtScore = new TextView(this);
            txtScore.setLayoutParams(new LinearLayout.LayoutParams(
                    dp(50),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            txtScore.setText(String.valueOf(entry.score));
            txtScore.setTextColor(Color.rgb(192, 132, 252));
            txtScore.setTextSize(15);
            txtScore.setTypeface(null, Typeface.BOLD);
            txtScore.setGravity(Gravity.END);

            row.addView(txtRank);
            row.addView(txtName);
            row.addView(txtScore);

            leaderboardList.addView(row, rowParams);
        }
    }


    private LinearLayout createPodiumCard(
            LeaderboardManager.ScoreEntry entry,
            int rank,
            int backgroundColor,
            int borderColor,
            int heightDp) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(4), dp(8), dp(4), dp(8));

        GradientDrawable background = new GradientDrawable();
        background.setColor(backgroundColor);
        background.setCornerRadius(dp(14));
        background.setStroke(dp(2), borderColor);

        card.setBackground(background);

        // Medal
        TextView medal = new TextView(this);
        medal.setGravity(Gravity.CENTER);
        medal.setTextSize(rank == 1 ? 30 : 25);

        if (rank == 1) {
            medal.setText("🥇");
        } else if (rank == 2) {
            medal.setText("🥈");
        } else {
            medal.setText("🥉");
        }

        // Rank
        TextView txtRank = new TextView(this);
        txtRank.setText(rank == 1 ? "1ST" :
                rank == 2 ? "2ND" : "3RD");
        txtRank.setTextColor(borderColor);
        txtRank.setTextSize(13);
        txtRank.setTypeface(null, Typeface.BOLD);
        txtRank.setGravity(Gravity.CENTER);

        // Player name
        TextView txtName = new TextView(this);
        txtName.setText(entry.name);
        txtName.setTextColor(Color.WHITE);
        txtName.setTextSize(13);
        txtName.setGravity(Gravity.CENTER);
        txtName.setMaxLines(1);

        // Score
        TextView txtScore = new TextView(this);
        txtScore.setText(entry.score + " PTS");
        txtScore.setTextColor(borderColor);
        txtScore.setTextSize(14);
        txtScore.setTypeface(null, Typeface.BOLD);
        txtScore.setGravity(Gravity.CENTER);

        card.addView(medal);
        card.addView(txtRank);
        card.addView(txtName);
        card.addView(txtScore);

        return card;
    }

    // Convert dp to pixels
    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

}
