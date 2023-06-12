package com.example.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

/**
 * la partie pour gèrer l'utilisation de l'application, des utilisateur et résultat
 */
public class MainActivity extends AppCompatActivity {

    // déclarer les varaibles d'objet
    private Button btPlay;
    private Button btAddPlayer;
    private RelativeLayout rlAddPlayer;
    private Button btOKAddPlayer;
    private Button btCancelAddPlayer;
    private TextInputEditText txtuserInput;
    private TextView txtVJoueur1;
    private TextView txtVJoueur2;
    private Button btSetGamemod;
    private RelativeLayout rlSetGamemod;
    private Button btOKSetGamemod;
    private Button btCancelSetGamemod;
    private Spinner spinGamemod;
    private TextView txtVGamemod;
    private Button btReset;

    /**
     * à la première création du programme
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Désactiver l'écran de verrouillage spécifiquement pour cette activité
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // affecter l'objet à la variable aproprier
        btPlay = findViewById(R.id.main_button_play);
        btAddPlayer = findViewById(R.id.main_button_add_player);
        rlAddPlayer = findViewById(R.id.main_saisie_utilisateur_layaout);
        btOKAddPlayer = findViewById(R.id.main_button_add_p_OK);
        btCancelAddPlayer = findViewById(R.id.main_button_add_p_Cancel);
        txtuserInput = findViewById(R.id.enter_user);
        txtVJoueur1 = findViewById(R.id.main_player1);
        txtVJoueur2 = findViewById(R.id.main_player2);
        btSetGamemod = findViewById(R.id.main_button_set_gamemod);
        rlSetGamemod = findViewById(R.id.main_saisie_gamemod);
        btOKSetGamemod = findViewById(R.id.main_button_gamemod_OK);
        btCancelSetGamemod = findViewById(R.id.main_button_gamemod_Cancel);
        spinGamemod = findViewById(R.id.set_gamemode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gamemode_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGamemod.setAdapter(adapter);

        txtVGamemod = findViewById(R.id.main_gamemod);
        btReset = findViewById(R.id.main_button_reset);

        newStart();
    }

    // Mes fonctions |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    // les variables globales
    private int numberUsersEntered = 0;
    private String player1 = "";
    private String player2 = "";
    private String gamemod = "";

    /**
     * remet les valeurs initial
     */
    private void newStart(){
        btAddPlayer.setEnabled(false);
        btSetGamemod.setEnabled(true);
        btPlay.setEnabled(false);
        btReset.setEnabled(false);
        rlAddPlayer.setVisibility(View.INVISIBLE);
        rlSetGamemod.setVisibility(View.INVISIBLE);
        txtVJoueur1.setText("");
        txtVJoueur2.setText("");
        txtVGamemod.setText("");
        numberUsersEntered = 0;
        player1 = "";
        player2 = "";
        gamemod = "";
    }

    /**
     * Quand on valide l'ajout d'un joueur
     * on teste la saisie et on mets du text alternativ si il n'y en as pas
     */
    private void okAddPlayerOpen(){

        //vérifier la saisie et mettre un text alternatif si c'est vide
        if (numberUsersEntered == 0) {
            player1 = txtuserInput.getText().toString();
            if (player1.matches("")) {
                player1 = "???";
            }
            txtVJoueur1.setText(player1);
            numberUsersEntered = 1;
        } else if (numberUsersEntered == 1) {
            player2 = txtuserInput.getText().toString();
            if (player2.matches("")) {
                player2 = "???";
            }
            txtVJoueur2.setText(player2);
            numberUsersEntered = 2;
            btAddPlayer.setEnabled(false);
            btPlay.setEnabled(true);
        } else {
            txtVJoueur1.setText("Error");
            txtVJoueur2.setText("Error");
        }
        // cacher le clavier après la saisie
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtuserInput.getWindowToken(), 0);

        txtuserInput.setText("");
        rlAddPlayer.setVisibility(View.INVISIBLE);
    }

    private void oksetGamemode(){

        gamemod = getSelectedSpinnerValue();
        txtVGamemod.setText(gamemod);
        rlSetGamemod.setVisibility(View.INVISIBLE);
        btPlay.setEnabled(true);
    }

    private String getSelectedSpinnerValue() {
        Object selectedItem = spinGamemod.getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.toString();
        }
        return "";
    }

    /**
     * Les préparation et ouverture d'une partie à jouer
     */
    private void startGame(){
        // ouvrir et transferer le résultat sur le résultat
        // préparer le transfère de données
        Intent ActivityIntent = new Intent(MainActivity.this, gameActivity.class);
        // afecter les variable et les valeurs au transfèr
        //ActivityIntent.putExtra("Player1", player1);
        //ActivityIntent.putExtra("Player2", player2);
        ActivityIntent.putExtra("Gamemod", gamemod);
        // Ouvrir l'activité de destination
        startActivity(ActivityIntent);
    }

    // |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    // S'éxecute l'or de louverture/l'affichage de l'application |||||||||||||||||||||||||||||||||||
    @Override
    protected void onStart(){
        super.onStart();
        newStart();
        // Ici on y place les écoute d'évènements (Listener)


        //   ...........  quand je clic sur le bt jouer
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        //   ...........  quand je clic sur le bt ajouter un joueur
        btAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ajouter un joueur
                rlAddPlayer.setVisibility(View.VISIBLE);
            }
        });

        //   ...........  quand je clic sur le bt OK ajouter
        btOKAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ok
                okAddPlayerOpen();
            }
        });

        //   ...........  quand je clic sur le bt cancel ajouter
        btCancelAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // annuler
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtuserInput.getWindowToken(), 0);
                txtuserInput.setText("");
                rlAddPlayer.setVisibility(View.INVISIBLE);
            }
        });

        //   ...........  quand je clic sur le bt définir le mode de jeux
        btSetGamemod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // définir le mod de jeux
                rlSetGamemod.setVisibility(View.VISIBLE);
            }
        });

        //   ...........  quand je clic sur le bt OK gamemod
        btOKSetGamemod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ok
                oksetGamemode();
            }
        });

        //   ...........  quand je clic sur le bt cancel gamemod
        btCancelSetGamemod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // annuler
                // ...
                rlSetGamemod.setVisibility(View.INVISIBLE);
            }
        });

        //   ...........  quand je clic sur le bt reset
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset
                newStart();
            }
        });
    }
}