package com.example.snakegame;

// importation des outils
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
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
    private TextView scor;
    ArrayList<int[]> mousePositionsAndType = new ArrayList<>();
    ArrayList<int[]> snakePositionsAndType = new ArrayList<>();
    private String gamemod;

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

        // Désactiver l'écran de verrouillage spécifiquement pour cette activité
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Récupérer l'intent qui a lancé cette activité
        Intent intent = getIntent();

        // affecter l'objet à la variable aproprier |||||||||||||||||||||

        // les capteurs
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        // les éléments du jeux
        orientationTextView = findViewById(R.id.orientationTextView);
        tableLayout = findViewById(R.id.tableView);
        scor = findViewById(R.id.scor);
        scor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        // définir les images du repant
        addSnakePartsView();

        // récupèrer le mode de jeux
        // Vérifier si l'intent contient une valeur pour la clé "Gamemod"
        if(intent.hasExtra("Gamemod")) {

            // Récupérer la valeur de la variable "Gamemod"
           gamemod = intent.getStringExtra("Gamemod");
        }

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
    boolean isGameOver;
    int cooldown;
    int cooldownLimit;

    // Mes fonctions |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    /**
     * remet les valeurs initial
     */
    private void newStart(){
        limitMousPosAndType.clear();
        limitMousPosAndType.add(new int[]{1, numRows-2}); // X min max
        limitMousPosAndType.add(new int[]{1, numColumns-2}); // Y min max
        limitMousPosAndType.add(new int[]{0, 1}); // moustype min max
        mousePositionsAndType.clear();
        ArrayList<int[]> limitInitMousPosAnsType = new ArrayList<>();
        limitInitMousPosAnsType.add(new int[]{3, numRows-4}); // X min max
        limitInitMousPosAnsType.add(new int[]{4, numColumns-4}); // Y min max
        limitInitMousPosAnsType.add(new int[]{0, 1}); // moustype min max
        int[] initPosition = playGame.RandomMousPosition(limitInitMousPosAnsType, snakePositionsAndType);
        mousePositionsAndType.add(initPosition);
        snakePositionsAndType.clear();
        snakePositionsAndType.add(new int[]{4, 0, 0});
        scor.setText("");
        cooldownLimit = 12;
        cooldown = cooldownLimit;
        isGameOver = false;
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
    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Mise à jour des valeurs de x, y et z
        float fxValue = (event.values[0] * 10);
        float fyValue = (event.values[1] * 10);

        StringBuilder LesXDuSerpent = new StringBuilder();
        LesXDuSerpent.append("\nx");
        StringBuilder LesYDuSerpent = new StringBuilder();
        LesYDuSerpent.append("\nx");
        for (int X = 0; X < snakePositionsAndType.size(); X++) {
            LesXDuSerpent.append(" : ").append(snakePositionsAndType.get(X)[0]);
            LesYDuSerpent.append(" : ").append(snakePositionsAndType.get(X)[1]);
        }

        // Mise à jour de l'affichage dans le TextView
        orientationTextView.setText("x : " + fxValue + "%\ny : " + fyValue +
                "%\ncooldown : " + cooldown +
                LesXDuSerpent.toString() + LesYDuSerpent.toString() +
                "\ngamemod : " + gamemod);

        int xValue = (int) fxValue;
        int yValue = (int) fyValue;

        // execute le mod de jeux
        modPlay(xValue, yValue);

        if (cooldown < 1 && !isGameOver) {
            play(xValue, yValue);
        }
        cooldown = (cooldown < 0) ? cooldownLimit : cooldown - 1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ne fait rien pour le moment
    }

    private void modPlay(int xValue, int yValue) {
        int snakeSize = snakePositionsAndType.size();
        if (gamemod.equals("Sandbox")) {
            cooldownLimit = 10;
            if (Math.abs(xValue) < 10 && Math.abs(yValue) < 10) {
                cooldown = cooldownLimit;
            }
        } else if (gamemod.equals("Hardcore")) {
            cooldownLimit = 2;
            if (snakeSize > 10) {
                cooldownLimit = 1;
            }
        } else {
            cooldownLimit = 5;
            if (snakeSize > 3 ) {
                cooldownLimit = 4;
            } else if (snakeSize > 6 ) {
                cooldownLimit = 3;
            } else if (snakeSize > 10 ) {
                cooldownLimit = 2;
            } else if (snakeSize > 15 ) {
                cooldownLimit = 1;
            }
        }
    }

    /**
     * enclanche un étape du jeux avec tous les règles
     * Puis on peut répèter ceci pour tous le jeux
     * @param xValue Valeur X du capteur gravity
     * @param yValue Valeur Y du capteur gravity
     */
    private void play(int xValue, int yValue){

        // trouve la nouvelle position de la tête puis supprime tous le serpent
        int[] newPosition = playGame.SetNewSnakeHeadPosition(snakePositionsAndType.get(0), xValue, yValue);
        playGame.removeAllWithListPos(snakePositionsAndType, tableLayout);

        // si je n'ai pa perdu
        if (playGame.ifNotGameOver(snakePositionsAndType, newPosition, numRows, numColumns)){

            // récupèrer les position actuel/encient
            ArrayList<int[]> oldSnakePositionsAndType = new ArrayList<>();
            oldSnakePositionsAndType.addAll(snakePositionsAndType);

            // gèrer la souris
            boolean mousEated = false;
            int matchPositions = playGame.thereMouseAtNewPosition(snakePositionsAndType.get(0), mousePositionsAndType);
            if(matchPositions >= 0){
                playGame.setRamdomMous(mousePositionsAndType, snakePositionsAndType, limitMousPosAndType, tableLayout, mousView);
                addSnakePartsView();
                mousEated = true;
            }

            // playGame.removeSnakeStartAndEndWithPos(snakePositionsAndType, tableLayout, mousEated); // tentative d'amélioré le chragement du serpent

            // effacer puis replacer le serpent
            snakePositionsAndType.clear();
            snakePositionsAndType.addAll(playGame.snakeGo(oldSnakePositionsAndType, newPosition, mousEated));
            playGame.setSnakeWithPos(snakePositionsAndType, tableLayout, snakePartsView);

            // playGame.setSnakeStartAndEndWithPos(snakePositionsAndType, tableLayout, snakePartsView); // tentative d'amélioré le chragement du serpent
            cooldown = cooldownLimit;
        } else {
            isGameOver = true;
            playGame.setSnakePartWithPos(new int[]{snakePositionsAndType.get(0)[0], snakePositionsAndType.get(0)[1], -1}, tableLayout, snakePartsView.get(0));
            String score = String.valueOf(snakePositionsAndType.size() -1);
            scor.setText("Score : " + score);
        }
    }

    /**
     * Ajouter un élément au serpent
     */
    private void addSnakePartsView(){
        // définir l'image de bout de serpent
        ImageView snakePartView;
        snakePartView = new ImageView(this);
        snakePartView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Définir l'échelle de l'image
        float scale = 1.1f; // L'échelle souhaitée (1.1f représente une augmentation de 10%)
        snakePartView.setScaleX(scale);
        snakePartView.setScaleY(scale);

        // ajouter une image à la liste d'image pour le serpent
        snakePartsView.add(snakePartView);
    }
}