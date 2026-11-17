package my.edu.utar.taptargetgame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    // Game variables
    GridLayout gameGrid;
    TextView txtScore;
    TextView txtTimer;
    CountDownTimer gameTimer;
    boolean timeUp = false;
    LinearLayout gameOverPanel;
    boolean scoreSaved = false;

    ArrayList<TextView> views = new ArrayList<>();

    Random random = new Random();

    // Game variables
    int score = 0;
    int highlightedIndex = -1;

    // Level management
    int currentLevel = 1;
    int[] levelSizes = {2, 3, 4, 5};
    int totalLevels = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Connect Java with XML
        gameGrid = findViewById(R.id.gameGrid);
        txtScore = findViewById(R.id.txtScore);
        txtTimer = findViewById(R.id.txtTimer);

        // Start Level 1
        startLevel();
    }

    private void startLevel() {

        // Update level title
        TextView txtLevel = findViewById(R.id.txtLevel);

        txtLevel.setText("LEVEL " + currentLevel + " / " + totalLevels);

        // Get grid size for current level
        int gridSize = levelSizes[currentLevel - 1];

        // Generate grid
        createGrid(gridSize);

        // Reset timer
        startTimer();
    }

    // Generate the game grid

    private void createGrid(int gridSize) {

        gameGrid.removeAllViews();
        views.clear();

        gameGrid.setRowCount(gridSize);
        gameGrid.setColumnCount(gridSize);

        // Calculate available screen width
        float density = getResources().getDisplayMetrics().density;

        int screenWidthDp = (int) (
                getResources().getDisplayMetrics().widthPixels / density
        );

        // Root layout has 20dp padding on each side
        int gridAvailableDp = screenWidthDp - 40;

        // Space between tiles
        int tileMarginDp = 4;

        // Automatically calculate square tile size
        int tileSizeDp = (
                gridAvailableDp - (gridSize * tileMarginDp * 2)
        ) / gridSize;

        int tileSizePx = (int) (tileSizeDp * density + 0.5f);
        int marginPx = (int) (tileMarginDp * density + 0.5f);

        int totalViews = gridSize * gridSize;

        for (int i = 0; i < totalViews; i++) {

            int row = i / gridSize;
            int col = i % gridSize;

            TextView box = new TextView(this);

            box.setGravity(Gravity.CENTER);
            box.setText("");
            box.setTextSize(24);

            // Normal tile appearance
            box.setBackground(createBackground(Color.rgb(48, 57, 88)));

            // Make each tile square
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();

            params.width = tileSizePx;
            params.height = tileSizePx;

            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(col);

            params.setMargins(marginPx, marginPx, marginPx, marginPx);

            gameGrid.addView(box, params);

            views.add(box);

            // Detect user touch
            final int index = i;

            box.setOnClickListener(v -> {

                if (!timeUp && index == highlightedIndex) {

                    // Correct touch
                    score++;

                    txtScore.setText("SCORE: " + score);

                    // Highlight another tile
                    highlightRandomView();
                }
            });
        }

        // Start by highlighting one tile
        highlightRandomView();
    }


    // Create rounded background
    private GradientDrawable createBackground(int color) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(24);

        if (color == Color.rgb(139, 92, 246)) {
            // Highlighted neon purple target
            drawable.setStroke(4, Color.rgb(216, 180, 254));
        } else {
            // Normal dark target tile
            drawable.setStroke(2, Color.rgb(65, 75, 115));
        }

        return drawable;
    }

    // Randomly highlight a View
    private void highlightRandomView() {

        // Reset all Views to normal
        for (TextView box : views) {

            box.setBackground(
                    createBackground(Color.rgb(48, 57, 88))
            );
        }

        int newIndex;

        do {
            newIndex = random.nextInt(views.size());
        } while (views.size() > 1 &&
                newIndex == highlightedIndex);

        highlightedIndex = newIndex;

        // Highlight selected View
        views.get(highlightedIndex).setBackground(
                createBackground(Color.rgb(139, 92, 246))
        );
    }

    private void startTimer() {

        timeUp = false;

        gameTimer = new CountDownTimer(5000, 100) {

            @Override
            public void onTick(long millisUntilFinished) {

                long secondsLeft =
                        (millisUntilFinished + 999) / 1000;

                txtTimer.setText("TIME: " + secondsLeft + "s");
            }

            @Override
            public void onFinish() {

                timeUp = true;

                txtTimer.setText("TIME: 0s");

                if (currentLevel < totalLevels) {

                    // Show Continue or End Game options
                    showLevelCompleted();

                } else {

                    // All 4 levels completed
                    showFinalGameOver();

                }
            }

        }.start();
    }

    private void showLevelCompleted() {

        // Disable all target buttons
        for (TextView box : views) {
            box.setClickable(false);
            box.setAlpha(0.4f);
        }

        highlightedIndex = -1;

        // Hide target grid
        gameGrid.setVisibility(View.GONE);

        // Get parent layout
        ViewGroup parent = (ViewGroup) gameGrid.getParent();

        // Create Level Completed panel
        gameOverPanel = new LinearLayout(this);
        gameOverPanel.setOrientation(LinearLayout.VERTICAL);
        gameOverPanel.setGravity(Gravity.CENTER);
        gameOverPanel.setPadding(20, 30, 20, 30);

        gameOverPanel.setBackgroundResource(
                R.drawable.bg_level_completed
        );

        // Level Completed title
        TextView txtTitle = new TextView(this);
        txtTitle.setText("LEVEL " + currentLevel + " COMPLETED!");
        txtTitle.setText("✦ LEVEL " + currentLevel + " CLEARED! ✦");
        txtTitle.setTextColor(Color.rgb(216, 180, 254));
        txtTitle.setTextSize(23);
        txtTitle.setGravity(Gravity.CENTER);
        txtTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        txtTitle.setLetterSpacing(0.04f);

        // Current score
        TextView txtCurrentScore = new TextView(this);
        txtCurrentScore.setText(
                "YOUR CURRENT SCORE\n" + score + " POINTS"
        );
        txtCurrentScore.setTextColor(Color.rgb(34, 211, 238));
        txtCurrentScore.setTextSize(21);
        txtCurrentScore.setGravity(Gravity.CENTER);
        txtCurrentScore.setTypeface(null, android.graphics.Typeface.BOLD);
        txtCurrentScore.setPadding(0, 20, 0, 20);

        // Continue button
        Button btnContinue = new Button(this);
        btnContinue.setText("CONTINUE");
        btnContinue.setTextColor(Color.WHITE);
        btnContinue.setTextSize(15);
        btnContinue.setTypeface(null, android.graphics.Typeface.BOLD);
        btnContinue.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(139, 92, 246)
                )
        );

        // End Game button
        Button btnEndGame = new Button(this);
        btnEndGame.setText("END GAME");
        btnEndGame.setTextColor(Color.rgb(216, 180, 254));
        btnEndGame.setTextSize(14);
        btnEndGame.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(35, 32, 65)
                )
        );

        // Continue to next level
        btnContinue.setOnClickListener(v -> {

            // Remove completed panel
            parent.removeView(gameOverPanel);

            // Move to next level
            currentLevel++;

            // Show grid again
            gameGrid.setVisibility(View.VISIBLE);

            // Start next level
            startLevel();
        });

        // End game
        btnEndGame.setOnClickListener(v -> {

            parent.removeView(gameOverPanel);

            showFinalGameOver();
        });

        // Add components
        gameOverPanel.addView(txtTitle);
        gameOverPanel.addView(txtCurrentScore);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        buttonParams.topMargin = 20;

        // Only show Continue if there are more levels
        if (currentLevel < totalLevels) {
            gameOverPanel.addView(btnContinue, buttonParams);
        }

        gameOverPanel.addView(btnEndGame, buttonParams);

        // Add panel to screen
        ViewGroup.LayoutParams panelParams =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        parent.addView(gameOverPanel, panelParams);
    }

    private void showFinalGameOver() {

        // Hide target grid
        gameGrid.setVisibility(View.GONE);

        ViewGroup parent = (ViewGroup) gameGrid.getParent();

        gameOverPanel = new LinearLayout(this);
        gameOverPanel.setOrientation(LinearLayout.VERTICAL);
        gameOverPanel.setGravity(Gravity.CENTER);
        gameOverPanel.setPadding(20, 30, 20, 30);

        gameOverPanel.setBackgroundResource(
                R.drawable.bg_game_over
        );

        // Trophy illustration
        TextView txtTrophy = new TextView(this);
        txtTrophy.setText("🏆");
        txtTrophy.setTextSize(48);
        txtTrophy.setGravity(Gravity.CENTER);

        gameOverPanel.addView(txtTrophy);

        TextView txtTitle = new TextView(this);
        txtTitle.setText("GAME OVER!");
        txtTitle.setTextColor(Color.rgb(216, 180, 254));
        txtTitle.setTextSize(27);
        txtTitle.setGravity(Gravity.CENTER);
        txtTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        txtTitle.setLetterSpacing(0.08f);

        TextView txtFinalScore = new TextView(this);
        txtFinalScore.setText(
                "YOUR FINAL SCORE\n" + score + " POINTS"
        );
        txtFinalScore.setTextColor(Color.rgb(34, 211, 238));
        txtFinalScore.setTextSize(21);
        txtFinalScore.setGravity(Gravity.CENTER);
        txtFinalScore.setTypeface(null, android.graphics.Typeface.BOLD);
        txtFinalScore.setPadding(0, 18, 0, 25);

        Button btnPlayAgain = new Button(this);
        btnPlayAgain.setText("PLAY AGAIN");
        btnPlayAgain.setTextColor(Color.WHITE);
        btnPlayAgain.setTextSize(15);
        btnPlayAgain.setTypeface(null, android.graphics.Typeface.BOLD);
        btnPlayAgain.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(139, 92, 246)
                )
        );

        btnPlayAgain.setOnClickListener(v -> {

            parent.removeView(gameOverPanel);

            // Reset game
            score = 0;
            currentLevel = 1;
            timeUp = false;
            scoreSaved = false;

            txtScore.setText("SCORE: 0");
            txtTimer.setText("TIME: 5s");

            gameGrid.setVisibility(View.VISIBLE);

            // Restart from Level 1
            startLevel();
        });

        gameOverPanel.addView(txtTitle);
        gameOverPanel.addView(txtFinalScore);

        // CHECK WHETHER SCORE HAS BEEN SAVED
        if (!scoreSaved) {

            // Check whether the score qualifies for Top 25
            LeaderboardManager manager = new LeaderboardManager(this);

            if (manager.isQualified(score)) {

                // Ask player to enter name
                EditText inputName = new EditText(this);
                inputName.setHint("ENTER YOUR NAME");
                inputName.setSingleLine(true);
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);

                inputName.setTextColor(Color.WHITE);
                inputName.setHintTextColor(Color.rgb(167, 139, 250));
                inputName.setTextSize(16);

                inputName.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.rgb(139, 92, 246)
                        )
                );

                inputName.setPadding(16, 18, 16, 18);
                inputName.setMinHeight(55);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("NEW HIGH SCORE!")
                        .setMessage(
                                "Your score qualifies for the Top 25!\nEnter your name:"
                        )
                        .setView(inputName)
                        .setCancelable(false)
                        .setPositiveButton("SAVE", (dialogInterface, which) -> {

                            String playerName = inputName.getText().toString().trim();

                            if (playerName.isEmpty()) {
                                playerName = "Player";
                            }

                            manager.addScore(playerName, score);
                            scoreSaved = true;
                        })
                        .create();

                dialog.show();
                styleNeonDialog(dialog);

            } else {

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("NOT IN TOP 25")
                        .setMessage(
                                "Your score did not qualify for the leaderboard.\n\n" +
                                        "Keep practising and try again!"
                        )
                        .setPositiveButton("OK", (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                        })
                        .create();

                dialog.show();
                styleNeonDialog(dialog);

            }
        }

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        buttonParams.topMargin = 20;

        gameOverPanel.addView(btnPlayAgain, buttonParams);

        // HOME button
        Button btnHome = new Button(this);
        btnHome.setText("HOME");
        btnHome.setTextColor(Color.rgb(34, 211, 238));
        btnHome.setTextSize(15);
        btnHome.setTypeface(null, android.graphics.Typeface.BOLD);
        btnHome.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(35, 32, 65)
                )
        );

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        LinearLayout.LayoutParams homeParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        homeParams.topMargin = 10;

        gameOverPanel.addView(btnHome, homeParams);

        ViewGroup.LayoutParams panelParams =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        parent.addView(gameOverPanel, panelParams);
    }

    // Apply Neon Arcade styling to AlertDialogs
    private void styleNeonDialog(AlertDialog dialog) {

        // Dark background with purple border
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(17, 16, 39));
        background.setCornerRadius(24);
        background.setStroke(3, Color.rgb(139, 92, 246));

        dialog.getWindow().setBackgroundDrawable(background);

        // Title colour
        int titleId = getResources().getIdentifier(
                "alertTitle",
                "id",
                "android"
        );
        TextView title = dialog.findViewById(titleId);

        if (title != null) {
            title.setTextColor(Color.rgb(216, 180, 254));
            title.setTextSize(20);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        // Message colour
        TextView message = dialog.findViewById(android.R.id.message);

        if (message != null) {
            message.setTextColor(Color.WHITE);
            message.setTextSize(15);
        }

        // SAVE / OK button colour
        Button positiveButton = dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
        );

        if (positiveButton != null) {
            positiveButton.setTextColor(Color.rgb(192, 132, 252));
            positiveButton.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        Button negativeButton = dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
        );

        if (negativeButton != null) {
            negativeButton.setTextColor(Color.rgb(34, 211, 238));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }
}