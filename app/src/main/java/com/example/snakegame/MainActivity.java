package com.example.snakegame;

// importation des outils
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.ViewGroup;

/**
 * Mon application
 *
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int numRows = 10;
    int numColumns = 20;
    int SHposX = 4;
    int SHposY = 0;
    int SHorientation = 0;
    int WMposX = 6;
    int WMposY = 9;

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private TextView orientationTextView;
    private ImageView snakeHaedView;
    private ImageView whiteMousView;
    private TableLayout tableLayout;
    private TextView gameOver;

    // variable pour jouer
    boolean isGameOver = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orientationTextView = findViewById(R.id.orientationTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        /* ************ ************ ************ ************ ************
          Créer le plateau de jeux et le serpent
         */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int cellHeight = (int) ((screenHeight - 50) * 0.1);
        int cellWidth = (int) ((screenWidth + 10) * 0.05);

        tableLayout = findViewById(R.id.tableView);
        gameOver = findViewById(R.id.gameOver);
        gameOver.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        // définir l'image de la tête du serpent
        snakeHaedView = new ImageView(this);
        snakeHaedView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        snakeHaedView.setImageResource(R.drawable.snake_head);

        // définir l'image de la souris
        whiteMousView = new ImageView(this);
        whiteMousView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        whiteMousView.setImageResource(R.drawable.white_mous);

        // Création du tableau de jeux ave la tête du serpent
        for (int i = 0; i < numRows; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < numColumns; j++) {
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setLayoutParams(new TableRow.LayoutParams(cellWidth, cellHeight));

                // Créer et affecter une ID unique en combinant i et j
                frameLayout.setTag(i + "-" + j);

                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) {
                    frameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.lightgreen));
                } else {
                    frameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkgreen));
                }

                if (i == SHposX && j == SHposY) {
                    // Ajouter l'ImageView au conteneur
                    frameLayout.addView(snakeHaedView);
                }

                if (i == WMposX && j == WMposY) {
                    // Ajouter l'ImageView au conteneur
                    frameLayout.addView(whiteMousView);
                }

                // Ajouter les cellules (frameLayout) aux lignes
                tableRow.addView(frameLayout);
            }

            // Ajouter les lignes au tableau
            tableLayout.addView(tableRow);
        }
    }

    /**
     * regarde les changements du capteur
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Regarde si les capteurs change et change en même temps les affichages
     *
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if(!isGameOver) {
            // Mise à jour des valeurs de x, y et z
            int xValue = (int) (event.values[0] * 10);
            int yValue = (int) (event.values[1] * 10);
            int zValue = (int) (event.values[2] * 10);

            // Mise à jour de l'affichage dans le TextView
            orientationTextView.setText("x : " + xValue + "%\ny : " + yValue + "%\nz : " + zValue + "%");

        /* ************ ************ ************ ************ ************
          Controle du serpant
         */

            // jouer
            if (ifGo(yValue, xValue)) {

                // Definir la nouvelle position posX et posY
                go(yValue, xValue);

                // Supprimer l'ImageView du conteneur parent
                ViewGroup parent = (ViewGroup) snakeHaedView.getParent();
                if (parent != null) {
                    parent.removeView(snakeHaedView);
                }

                if (ifNoCrash()) {
                    // Obtenir le conteneur à la nouvelle position et i placer l'image
                    FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(SHposX + "-" + SHposY);
                    frameLayout.addView(snakeHaedView);
                    snakeHaedView.setRotation(SHorientation);
                } else {
                    isGameOver = true;
                    gameOver.setText("Game Over");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne fait rien pour le moment
    }

    private boolean ifGo(int yValue, int xValue) {
        return (yValue > 10 || yValue < -10 || xValue > 10 || xValue < -10 );
    }

    private void go(int yValue, int xValue){
        // Récupèrer les valeurs absolut
        int absYValue = Math.abs(yValue);
        int absXValue = Math.abs(xValue);

        // Définir la nouvelle position posX et posY
        if (absYValue > absXValue) {
            if (yValue > 10) {
                SHposY++;
                SHorientation = 0;
            } else if (yValue < -10) {
                SHposY--;
                SHorientation = 180;
            }
        } else {
            if (xValue > 10) {
                SHposX++;
                SHorientation = 90;
            } else if (xValue < -10) {
                SHposX--;
                SHorientation = 270;
            }
        }
    }

    private boolean ifNoCrash(){
        return (SHposX >= 0 && SHposX < numRows && SHposY >= 0 && SHposY < numColumns);
    }
}