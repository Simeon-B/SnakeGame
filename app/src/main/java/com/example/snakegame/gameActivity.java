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

import java.util.ArrayList;


/**
 * la partie jeux de mon aplication
 */
public class gameActivity extends AppCompatActivity implements SensorEventListener {

    // déclarer les varaibles d'objet
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private TextView orientationTextView;
    private ImageView mousView;
    ArrayList<ImageView> snakePartsView = new ArrayList<>();
    private TableLayout tableLayout;
    private TextView gameOver;
    private TextView scor;
    ArrayList<int[]> mousePositionsAndType = new ArrayList<>();
    ArrayList<int[]> snakePositionsAndType = new ArrayList<>();

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
        scor = findViewById(R.id.gameOver);
        scor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        // définir les images du repant
        addSnakePartsView();

        // définir les images de souris
        mousView = new ImageView(this);
        mousView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // définir la tail de l'écran
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        // démarage
        newStart();
        generateGameStructure(screenHeight, screenWidth);
        playGame.setMousWithPos(mousePositionsAndType.get(0), tableLayout, mousView);
        playGame.setSnakePartWithPos(snakePositionsAndType.get(0), tableLayout, snakePartsView.get(0));
    }

    // les variables globales ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    // les variables de créations du jeux
    int numRows = 10;
    int numColumns = 20;

    // les variables des éléments délacable
    ArrayList<int[]> limitMousPosAndType = new ArrayList<>();

    // variable pour jouer
    boolean isGameOver = false;
    int cooldown = 10;

    // Mes fonctions |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    /**
     * remet les valeurs initial
     */
    private void newStart(){
        limitMousPosAndType.clear();
        limitMousPosAndType.add(new int[]{0, numRows-1}); // X min max
        limitMousPosAndType.add(new int[]{0, numColumns-1}); // Y min max
        limitMousPosAndType.add(new int[]{0, 1}); // moustype min max
        mousePositionsAndType.clear();
        ArrayList<int[]> limitInitMousPosAnsType = new ArrayList<>();
        limitInitMousPosAnsType.add(new int[]{3, 6}); // X min max
        limitInitMousPosAnsType.add(new int[]{4, 15}); // Y min max
        limitInitMousPosAnsType.add(new int[]{0, 1}); // moustype min max
        int[] initPosition = playGame.RandomMousPosition(limitInitMousPosAnsType, snakePositionsAndType);
        mousePositionsAndType.add(initPosition);
        snakePositionsAndType.clear();
        snakePositionsAndType.add(new int[]{4, 0, 0});
        gameOver.setText("");
        scor.setText("");
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
                frameLayout.setClipChildren(false);

                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)) {
                    frameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.lightgreen));
                } else {
                    frameLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.darkgreen));
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
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Mise à jour des valeurs de x, y et z
        float fxValue = (event.values[0] * 10);
        float fyValue = (event.values[1] * 10);

        // Mise à jour de l'affichage dans le TextView
        orientationTextView.setText("x : " + fxValue + "%\ny : " + fyValue +
                "%\ncooldown : " + cooldown +
                "\nx : " + snakePositionsAndType.get(0)[0] +
                "\ny : " + snakePositionsAndType.get(0)[1]);

        int xValue = (int) fxValue;
        int yValue = (int) fyValue;

        if (cooldown < 1 && !isGameOver) {
            play(xValue, yValue);
        }
        cooldown = (cooldown == -1) ? 5 : cooldown - 1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne fait rien pour le moment
    }

    private void setRamdomMous() {

        playGame.removeAllWithListPos(mousePositionsAndType, tableLayout);

        int[] NewmousePositionsAndType;
        NewmousePositionsAndType = playGame.RandomMousPosition(limitMousPosAndType, snakePositionsAndType);
        mousePositionsAndType.clear();
        mousePositionsAndType.add(NewmousePositionsAndType);
        playGame.setMousWithPos(NewmousePositionsAndType,tableLayout,mousView);
    }

    private void play(int xValue, int yValue){
        int[] newPosition = playGame.SetNewSnakeHeadPosition(snakePositionsAndType.get(0), xValue, yValue);
        playGame.removeAllWithListPos(snakePositionsAndType, tableLayout);
        if (playGame.ifNotGameOver(newPosition, numRows, numColumns)){
            ArrayList<int[]> newSnakePositionsAndType = new ArrayList<>();
            newSnakePositionsAndType.addAll(snakePositionsAndType);
            snakePositionsAndType.clear();

            boolean mousEated = false;
            int matchPositions = playGame.thereMouseAtNewPosition(newSnakePositionsAndType.get(0), mousePositionsAndType);
            if(matchPositions >= 0){
                setRamdomMous();
                addSnakePartsView();
                mousEated = true;
            }
            snakePositionsAndType.addAll(playGame.snakeGo(newSnakePositionsAndType, newPosition, mousEated));
            playGame.setSnakeWithPos(snakePositionsAndType, tableLayout, snakePartsView);
            cooldown = 5;
        } else {
            isGameOver = true;
            playGame.setSnakePartWithPos(new int[]{snakePositionsAndType.get(0)[0], snakePositionsAndType.get(0)[1], -1}, tableLayout, snakePartsView.get(0));
            gameOver.setText("Game Over");
            String score = String.valueOf(snakePositionsAndType.size());
            scor.setText("Score : " + score);
        }
    }

    private void addSnakePartsView(){
        ImageView snakePartView;
        // définir l'image de bout de serpent
        snakePartView = new ImageView(this);
        snakePartView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        // Définir l'échelle de l'image
        float scale = 1.1f; // L'échelle souhaitée (1.1f représente une augmentation de 10%)
        snakePartView.setScaleX(scale);
        snakePartView.setScaleY(scale);
        snakePartsView.add(snakePartView);
    }
}