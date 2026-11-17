package my.edu.utar.taptargetgame;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardManager {

    private static final String PREF_NAME = "TapTargetLeaderboard";
    private static final String KEY_SCORES = "scores";
    private static final int MAX_SCORES = 25;

    private SharedPreferences preferences;

    public LeaderboardManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        // Initialize default leaderboard on first installation
        if (!preferences.contains(KEY_SCORES)) {
            initializeDefaultScores();
        }
    }

    // Initialize default Top 25 leaderboard
    private void initializeDefaultScores() {

        String[] names = {
                "King",
                "Peien",
                "AAA",
                "Phantom",
                "Blaze",
                "Ty",
                "NCT",
                "Ace",
                "Karina",
                "Johnny",
                "Test",
                "Queen",
                "Elsa",
                "Anna",
                "WooSan",
                "Zen",
                "Known",
                "Unknown",
                "Xyco",
                "Shawn",
                "Victor",
                "Danial",
                "Gojo",
                "Anton",
                "Ninja"
        };

        JSONArray jsonArray = new JSONArray();

        try {

            for (int i = 0; i < names.length; i++) {

                JSONObject object = new JSONObject();

                object.put("name", names[i]);
                object.put("score", 25 - i);

                jsonArray.put(object);
            }

            // Save default leaderboard permanently
            preferences.edit()
                    .putString(KEY_SCORES, jsonArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get all leaderboard scores
    public ArrayList<ScoreEntry> getScores() {

        ArrayList<ScoreEntry> scores = new ArrayList<>();

        String data = preferences.getString(KEY_SCORES, "[]");

        try {

            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                String name = object.getString("name");
                int score = object.getInt("score");

                scores.add(new ScoreEntry(name, score));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return scores;
    }

    // Check whether a score qualifies for Top 25
    public boolean isQualified(int newScore) {

        ArrayList<ScoreEntry> scores = getScores();

        if (scores.size() < MAX_SCORES) {
            return true;
        }

        return newScore > scores.get(scores.size() - 1).score;
    }

    // Add a new score
    public void addScore(String name, int newScore) {

        ArrayList<ScoreEntry> scores = getScores();

        scores.add(new ScoreEntry(name, newScore));

        // Sort scores from highest to lowest
        Collections.sort(scores, new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry a, ScoreEntry b) {
                return Integer.compare(b.score, a.score);
            }
        });

        // Keep only the Top 25
        if (scores.size() > MAX_SCORES) {
            scores = new ArrayList<>(
                    scores.subList(0, MAX_SCORES)
            );
        }

        // Convert scores into JSON
        JSONArray jsonArray = new JSONArray();

        try {

            for (ScoreEntry entry : scores) {

                JSONObject object = new JSONObject();

                object.put("name", entry.name);
                object.put("score", entry.score);

                jsonArray.put(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save permanently on the phone
        preferences.edit()
                .putString(KEY_SCORES, jsonArray.toString())
                .apply();
    }

    // Individual leaderboard entry
    public static class ScoreEntry {

        public String name;
        public int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
