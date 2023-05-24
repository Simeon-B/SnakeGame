package com.example.snakegame;

// importation des outils
import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Objects;


/**
 * la partie jeux de mon aplication
 */
public class gameActivity extends AppCompatActivity implements SensorEventListener {

    // déclarer les varaibles d'objet
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private TextView orientationTextView;
    private ImageView snakeHaedView;
    private ImageView whiteMousView;
    private ImageView brownMousView;
    private TableLayout tableLayout;
    private TextView gameOver;

    ArrayList<int[]> mousePositionsType = new ArrayList<>();

    /**
     * à la première création du programme
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // affecter l'objet à la variable aproprier |||||||||||||||||||||

        // les capteurs
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        // les éléments du jeux
        orientationTextView = findViewById(R.id.orientationTextView);
        tableLayout = findViewById(R.id.tableView);
        gameOver = findViewById(R.id.gameOver);
        gameOver.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        // définir l'image de la tête du serpent
        snakeHaedView = new ImageView(this);
        snakeHaedView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        snakeHaedView.setImageResource(R.drawable.snake_head);

        // définir les images de souris
        whiteMousView = new ImageView(this);
        whiteMousView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        whiteMousView.setImageResource(R.drawable.white_mous);
        brownMousView = new ImageView(this);
        brownMousView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        brownMousView.setImageResource(R.drawable.brown_mous);

        // définir la tail de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        // démarage
        newStart();
        generateGameStructure(screenHeight, screenWidth);

        int[] tes2Sourie = mousePositionsType.get(0);
        int[] testSourie = {3, 5, 1};
        setMousWithPos(testSourie);
    }

    // les variables globales ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    // les variables de créations du jeux
    int numRows = 10;
    int numColumns = 20;

    // les variables des éléments délacable
    int SHposX = 4;
    int SHposY = 0;
    int SHorientation = 0;
    int WMposX = 6;
    int WMposY = 9;

    // variable pour jouer
    boolean isGameOver = false;
    int cooldown = 0;

    // Mes fonctions |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    /**
     * remet les valeurs initial
     */
    private void newStart(){
        SHposX = 4;
        SHposY = 0;
        SHorientation = 0;
        WMposX = 6;
        WMposY = 9;
        mousePositionsType.clear();
        int[] initPosition = {6,9,0};
        mousePositionsType.add(initPosition);
    }

    /**
     * Génération du plan de jeux avec les élément de départ
     */
    private void generateGameStructure(int screenHeight, int screenWidth){

        // définir la taile des cellules du tableau
        int cellHeight = (int) ((screenHeight - 50) * 0.1);
        int cellWidth = (int) ((screenWidth + 10) * 0.05);

        // Création du tableau de jeux avec la tête du serpent et une sourie
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

                if (i == mousePositionsType.get(0)[0] && j == mousePositionsType.get(0)[1]) {
                    // Ajouter l'ImageView au conteneur
                    if (mousePositionsType.get(0)[2] == 0){
                        frameLayout.addView(whiteMousView);
                    } else {
                        frameLayout.addView(whiteMousView);
                    }
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
        // Mise à jour des valeurs de x, y et z
        float fxValue = (event.values[0] * 10);
        float fyValue = (event.values[1] * 10);

        // Mise à jour de l'affichage dans le TextView
        orientationTextView.setText("x : " + fxValue + "%\ny : " + fyValue);

        int xValue = (int) fxValue;
        int yValue = (int) fyValue;

        if(!isGameOver) {  //  && cooldown <= 0

            // jouer
            if (ifGo(yValue, xValue)) {

                // Definir la nouvelle position posX et posY
                go(yValue, xValue);

                // Supprimer l'ImageView du conteneur parent
                ViewGroup SHparent = (ViewGroup) snakeHaedView.getParent();
                if (SHparent != null) {
                    SHparent.removeView(snakeHaedView);
                }

                if (ifNoCrash()) {
                    // Obtenir le conteneur à la nouvelle position et i placer l'image
                    FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(SHposX + "-" + SHposY);
                    snakeHaedView.setRotation(SHorientation);
                    frameLayout.addView(snakeHaedView);

                    // vérifier si le serpent bouffe la souri
                    ViewGroup WMparent = (ViewGroup) whiteMousView.getParent();
                    if (Objects.equals(SHparent.getTag(), WMparent.getTag()) && WMparent != null) {
                        WMparent.removeView(whiteMousView);
                    }
                } else {
                    isGameOver = true;
                    gameOver.setText("Game Over");
                }
            }

            cooldown = 0;
        }

        cooldown--;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne fait rien pour le moment
    }

    private boolean ifGo(int yValue, int xValue) {
        return (yValue > 10 || yValue < -10 || xValue > 10 || xValue < -10 );
    }

    // ici je dois éxecuter ça en boucle avec un cooldown et en straide !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void go(int yValue, int xValue){
        // Récupèrer les valeurs absolut
        int absYValue = Math.abs(yValue);
        int absXValue = Math.abs(xValue);

        // Définir la nouvelle position posX et posY
        if (absYValue > absXValue) {
            if (yValue > 10 && SHorientation != 180) {
                SHposY++;
                SHorientation = 0;
            } else if (yValue < -10 && SHorientation != 0) {
                SHposY--;
                SHorientation = 180;
            }
        } else {
            if (xValue > 10 && SHorientation != 270) {
                SHposX++;
                SHorientation = 90;
            } else if (xValue < -10 && SHorientation != 90) {
                SHposX--;
                SHorientation = 270;
            }
        }
    }

    private boolean ifNoCrash(){
        return (SHposX >= 0 && SHposX < numRows && SHposY >= 0 && SHposY < numColumns);
    }

    /**
     * place une image de sourie dans une cellule du tableau
     * @param positions     contien la position et le type de souris
     */
    private void setMousWithPos(@NonNull int[] positions) {
        // Obtenir le conteneur à la nouvelle position et i placer l'image
        FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(positions[0] + "-" + positions[1]);
        // Ajouter l'ImageView au conteneur
        if (positions[2] == 0){
            frameLayout.addView(whiteMousView);
        } else {
            frameLayout.addView(brownMousView);
        }
    }
}