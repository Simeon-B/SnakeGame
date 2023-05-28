package com.example.snakegame;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class  playGame extends AppCompatActivity {

    /**
     * place une image de bout de serpant dans une cellule du tableau
     * @param position    la position, la direction le type de bout de serpant
     * @param tableLayout le tableau de jeux
     * @param SnakePartView la view pour l'image
     */
    static void setSnakePartWithPos(@NonNull int[] position, TableLayout tableLayout, ImageView SnakePartView) {
        // Obtenir le conteneur à la nouvelle position et i placer l'image
        FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(position[0] + "-" + position[1]);
        // Ajouter l'ImageView au conteneur
        if (position[3] == 0){
            SnakePartView.setImageResource(R.drawable.snake_head_lr);
        } else {
            SnakePartView.setImageResource(R.drawable.brown_mous);
        }
        frameLayout.addView(SnakePartView);
    }

    /**
     * place une image de sourie dans une cellule du tableau
     * @param position    la position et le type de souris
     * @param tableLayout le tableau de jeux
     * @param MousView la view pour l'image
     */
    static void setMousWithPos(@NonNull int[] position, TableLayout tableLayout, ImageView MousView) {
        // Obtenir le conteneur à la nouvelle position et i placer l'image
        FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(position[0] + "-" + position[1]);
        // Ajouter l'ImageView au conteneur
        if (position[2] == 0){
            MousView.setImageResource(R.drawable.white_mous);
        } else {
            MousView.setImageResource(R.drawable.brown_mous);
        }
        frameLayout.addView(MousView);
    }

    /**
     * supprimer tous d'une case à position donné
     * @param position    la position et le type de souris
     * @param tableLayout le tableau de jeux
     */
    static void removeAllWithPos(@NonNull int[] position, TableLayout tableLayout) {
        // Obtenir le conteneur à la position donnée
        FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(position[0] + "-" + position[1]);

        // Supprimer l'ImageView du conteneur s'il en contient un
        if (frameLayout.getChildCount() > 0) {
            frameLayout.removeAllViews();
        }
    }

    /**
     * Génère une position avec un type de souris pseudo aléatoire
     * @param limits Les limits pour les bornes [{Xmin, Xmax}, {Ymin, Ymax}, {Typmin, Typmax}]
     * @return une posistion avec un type de souris {X Y Type}
     */
    static int[] RandomMousPosition(ArrayList<int[]> limits) {
        int posX = RandomInt(limits.get(0)[0], limits.get(0)[1]);
        int posY = RandomInt(limits.get(1)[0], limits.get(1)[1]);
        int moustype = RandomInt(limits.get(2)[0], limits.get(2)[1]);

        int[] position = {posX, posY, moustype};

        return position;
    }

    /**
     * génère un nombre entier pseudo aléatoire
     * @param min valeur minimal
     * @param max valeur maximal
     * @return valeur généré
     */
    private static int RandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
