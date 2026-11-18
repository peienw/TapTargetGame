
package my.edu.utar.taptargetgame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnLeaderboard, btnHowToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Java with XML buttons
        btnStart = findViewById(R.id.btnStart);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnHowToPlay = findViewById(R.id.btnHowToPlay);

        // Start Game button
        btnStart.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    GameActivity.class
            );

            startActivity(intent);

        });

        // Leaderboard button
        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LeaderboardActivity.class
            );
            startActivity(intent);
        });

        // How to Play button
        btnHowToPlay.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    HowToPlayActivity.class
            );

            startActivity(intent);
        });
    }
}
