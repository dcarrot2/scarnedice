package dcarrot2.com.scarnedice;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder builder = null;
    private Button rollButton = null;
    private Button holdButton = null;
    private boolean didRollOne = false;
    public static String startMessage = "Your score: %d Computer score: %d";
    private static int userOverallScore = 0;
    private static int userTurnScore = 0;
    private static int compOverallScore = 0;
    private static int compTurnScore = 0;
    private static boolean winner = false; // 0 for comp, 1 for human
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userOverallScore = 0;
        compOverallScore = 0;
        userTurnScore = 0;
        compOverallScore = 0;
        startMessage = "Your score: %d Computer score: %d";
        TextView score = (TextView) findViewById(R.id.scoreView);
        score.setText(String.format(startMessage, 0, 0));

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("WINNER");
        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = MainActivity.this.getIntent();

                MainActivity.this.finish();
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickHandler(View view) {
        switch (view.getId()) {
            case R.id.rollView:
                int roll = random.nextInt(6) + 1;
                updateView(roll);
                if (roll == 1) {
                    endTurn(false);
                } else {
                    addToScore(roll);
                }
                break;
            case R.id.resetView:
                resetGame();
                break;
            case R.id.holdView:
                endTurn(true);
                break;
        }
    }

    private void computerTurn() {
        rollButton = (Button) findViewById(R.id.rollView);
        holdButton = (Button) findViewById(R.id.holdView);
        rollButton.setEnabled(false);
        holdButton.setEnabled(false);

        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
        asyncTaskRunner.execute("");

    }

    private void endCompTurn(boolean didComputerHold) {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        if (didComputerHold) {
            compOverallScore += compTurnScore;
        }
        if (compOverallScore >= 20) {
            declareWinner("computer");
            return;
        }
        didRollOne = false;
        compTurnScore = 0;
        startMessage = String.format("Your score: %d Computer score: %d", userOverallScore, compOverallScore);
        scoreView.setText(startMessage);
        rollButton.setEnabled(true);
        holdButton.setEnabled(true);
    }

    private void addToCompScore(int roll) {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        compTurnScore += roll;
        startMessage = String.format("Your score: %d Computer score: %d Computer turn score: %d", userOverallScore, compOverallScore, compTurnScore);
        scoreView.setText(startMessage);
    }

    private void resetGame() {
        TextView score = (TextView) findViewById(R.id.scoreView);
        startMessage = "Your score: %d Computer score: %d";
        userOverallScore = 0;
        userTurnScore = 0;
        compOverallScore = 0;
        compTurnScore = 0;
        score.setText(String.format(startMessage, 0, 0));
    }

    private void addToScore(int roll) {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        userTurnScore += roll;
        startMessage = String.format("Your score: %d Computer score: %d Your turn score: %d", userOverallScore, compOverallScore, userTurnScore);
        scoreView.setText(startMessage);
    }

    private void endTurn(boolean didUserHold) {
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        if (didUserHold) {
            userOverallScore += userTurnScore;
        }
        if (userOverallScore >= 20) {
            declareWinner("human");
            return;
        }
        userTurnScore = 0;
        startMessage = String.format("Your score: %d Computer score: %d", userOverallScore, compOverallScore);
        scoreView.setText(startMessage);
        computerTurn();

    }
    public void updateView(int roll) {
        ImageView dice = (ImageView) findViewById(R.id.diceView);
        String diceImage = String.format("dice%d", roll);
        int id = getResources().getIdentifier(diceImage, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        dice.setImageDrawable(drawable);
    }

    public void declareWinner(String winner) {
        AlertDialog dialog = builder.create();
        if (winner.equals("human")) {
            dialog.setMessage(String.format("You win with a score of %d!", userOverallScore));
            dialog.show();
        } else if (winner.equals("computer")) {
            dialog.setMessage(String.format("Computer wins with a score of %d!", compOverallScore));
            dialog.show();
        }
    }

    protected class AsyncTaskRunner extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            while(!didRollOne && compTurnScore < 20) {
                final int roll = random.nextInt(6) + 1;
                publishProgress(roll);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (didRollOne) {
                endCompTurn(false);
            } else {
                endCompTurn(true);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int roll = values[0];
            updateView(roll);
            addToCompScore(roll);
            if (roll == 1) {
                didRollOne = true;
            }
        }
    }
}
