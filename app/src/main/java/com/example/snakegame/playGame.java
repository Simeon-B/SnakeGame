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
     * place une image de bout de serpent dans une cellule du tableau
     * @param position    la position, le type de bout de serpant
     * @param tableLayout le tableau de jeux
     * @param SnakePartView la view pour l'image
     */
    static void setSnakePartWithPos(@NonNull int[] position, TableLayout tableLayout, ImageView SnakePartView) {
        // Obtenir le conteneur à la nouvelle position et i placer l'image
        FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(position[0] + "-" + position[1]);
        // Ajouter l'ImageView au conteneur
        switch (position[2]){
            case 0: SnakePartView.setImageResource(R.drawable.snake_head_rl); break;
            case 1: SnakePartView.setImageResource(R.drawable.snake_head_lr); break;
            case 2: SnakePartView.setImageResource(R.drawable.snake_head_tb);break;
            case 3: SnakePartView.setImageResource(R.drawable.snake_head_bt);break;
            case 4: SnakePartView.setImageResource(R.drawable.snake_body_rl);break;
            case 5: SnakePartView.setImageResource(R.drawable.snake_body_lr);break;
            case 6: SnakePartView.setImageResource(R.drawable.snake_body_tb);break;
            case 7: SnakePartView.setImageResource(R.drawable.snake_body_bt);break;
            case 8: SnakePartView.setImageResource(R.drawable.snake_body_rb);break;
            case 9: SnakePartView.setImageResource(R.drawable.snake_body_lt);break;
            case 10: SnakePartView.setImageResource(R.drawable.snake_body_tr);break;
            case 11: SnakePartView.setImageResource(R.drawable.snake_body_bl);break;
            case 12: SnakePartView.setImageResource(R.drawable.snake_body_rt);break;
            case 13: SnakePartView.setImageResource(R.drawable.snake_body_lb);break;
            case 14: SnakePartView.setImageResource(R.drawable.snake_body_tl);break;
            case 15: SnakePartView.setImageResource(R.drawable.snake_body_br);break;
            case 16: SnakePartView.setImageResource(R.drawable.snake_end_rl);break;
            case 17: SnakePartView.setImageResource(R.drawable.snake_end_lr);break;
            case 18: SnakePartView.setImageResource(R.drawable.snake_end_tb);break;
            case 19: SnakePartView.setImageResource(R.drawable.snake_end_bt);break;
            default: SnakePartView.setImageResource(R.drawable.img_error);
        }
        frameLayout.addView(SnakePartView);
    }

    static void setSnakeWithPos(ArrayList<int[]> positions, TableLayout tableLayout, ArrayList<ImageView> snakePartsView ) {
        int index = 0;
        for (int[] position : positions) {
            setSnakePartWithPos(position, tableLayout, snakePartsView.get(index));
            index++;
        }
    }

    /**
     * place une image de sourie dans une cellule du tableau
     * @param position    la position et le type de souris
     * @param tableLayout le tableau de jeux
     * @param mousView la view pour l'image
     */
    static void setMousWithPos(@NonNull int[] position, TableLayout tableLayout, ImageView mousView) {

        // Obtenir le conteneur à la nouvelle position et i placer l'image
        FrameLayout frameLayout = (FrameLayout) tableLayout.findViewWithTag(position[0] + "-" + position[1]);

        // Ajouter l'ImageView au conteneur
        switch (position[2]){
            case 0: mousView.setImageResource(R.drawable.white_mous); break;
            case 1: mousView.setImageResource(R.drawable.brown_mous); break;
            default: mousView.setImageResource(R.drawable.img_error);
        }
        frameLayout.addView(mousView);
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

    static void removeAllWithListPos(ArrayList<int[]> positions, TableLayout tableLayout) {
        for (int[] position : positions) {
            removeAllWithPos(position, tableLayout);
        }
    }

    static int[] SetNewSnakeHeadPosition(int[] oldPosition, int xGravity, int yGravity){
        // sortir les variables
        int posX = oldPosition[0];
        int posY = oldPosition[1];
        int direction = oldPosition[2];

        // Récupèrer les valeurs absolut
        int absXGravity = Math.abs(xGravity);
        int absYGravity = Math.abs(yGravity);

        boolean continuSame = false;

        // Définir la nouvelle position posX et posY
        if (absXGravity > 10 || absYGravity > 10){
            if (absXGravity > absYGravity) {
                if (xGravity > 10 && direction != 2) {
                    posX++;
                    direction = 3;
                } else if (xGravity < -10 && direction != 3) {
                    posX--;
                    direction = 2;
                } else {
                    continuSame =  true;
                }
            } else {
                if (yGravity > 10 && direction != 1) {
                    posY++;
                    direction = 0;
                } else if (yGravity < -10 && direction != 0) {
                    posY--;
                    direction = 1;
                } else {
                    continuSame =  true;
                }
            }
        } else {
            continuSame =  true;
        }
        if (continuSame){
            if (direction == 0) {
                posY++;
            } else if (direction == 1) {
                posY--;
            } else if (direction == 2) {
                posX--;
            } else {
                posX++;
            }
        }

        return new int[]{posX, posY, direction};
    }

    static int thereMouseAtNewPosition(@NonNull int[] newPosition, ArrayList<int[]> mousPositions) {
        int index = 0;
        for (int[] position : mousPositions) {
            if (position[0] == newPosition[0] && position[1] == newPosition[1]) {
                return index;  // Un élément égal à newPosition a été trouvé
            }
            index++;
        }
        return -1;  // Aucun élément égal à newPosition n'a été trouvé
    }


    static ArrayList<int[]> snakeGo(ArrayList<int[]> snakePositions, int[] newPosition, boolean AddAPart){
        if (snakePositions.size() == 1 && !AddAPart){
            snakePositions.set(0, newPosition);
        } else if ((snakePositions.size() == 2 && !AddAPart) || snakePositions.size() == 1) {
            if (snakePositions.size() == 2) {
                snakePositions.remove(1);
            }
            switch (newPosition[2]) {
                case 0 : snakePositions.add(new int[] {newPosition[0],newPosition[1] - 1,16});break;
                case 1 : snakePositions.add(new int[] {newPosition[0],newPosition[1] + 1,17});break;
                case 2 : snakePositions.add(new int[] {newPosition[0] + 1,newPosition[1],18});break;
                case 3 : snakePositions.add(new int[] {newPosition[0] - 1,newPosition[1],19});break;
                default: snakePositions.add(new int[] {newPosition[0],newPosition[1],-1});
            }
            snakePositions.set(0, newPosition);
        } else {
            snakePositions.add(0, newPosition);
            if (snakePositions.get(1)[2] == 0 && newPosition[2] == 0){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 4});
            } else if (snakePositions.get(1)[2] == 0 && newPosition[2] == 2){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 14});
            } else if (snakePositions.get(1)[2] == 0 && newPosition[2] == 3){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 11});
            } else if (snakePositions.get(1)[2] == 1 && newPosition[2] == 1){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 5});
            } else if (snakePositions.get(1)[2] == 1 && newPosition[2] == 2){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 10});
            } else if (snakePositions.get(1)[2] == 1 && newPosition[2] == 3){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 15});
            } else if (snakePositions.get(1)[2] == 2 && newPosition[2] == 2){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 6});
            } else if (snakePositions.get(1)[2] == 2 && newPosition[2] == 0){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 8});
            } else if (snakePositions.get(1)[2] == 2 && newPosition[2] == 1){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 13});
            } else if (snakePositions.get(1)[2] == 3 && newPosition[2] == 3){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 7});
            } else if (snakePositions.get(1)[2] == 3 && newPosition[2] == 0){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 12});
            } else if (snakePositions.get(1)[2] == 3 && newPosition[2] == 1){
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], 9});
            } else {
                snakePositions.set(1, new int[] {snakePositions.get(1)[0], snakePositions.get(1)[1], -1});
            }

            if (!AddAPart) {
                int lastItemIndex = snakePositions.size() -1;
                snakePositions.remove(lastItemIndex);
                lastItemIndex--;
                int[] lastPart = snakePositions.get(lastItemIndex);
                int lastPartType;
                if (lastPart[2] == 4 || lastPart[2] == 8  || lastPart[2] == 12) {
                    lastPartType = 16;
                } else if (lastPart[2] == 5 || lastPart[2] == 9  || lastPart[2] == 13) {
                    lastPartType = 17;
                } else if (lastPart[2] == 6 || lastPart[2] == 10  || lastPart[2] == 14) {
                    lastPartType = 18;
                } else if (lastPart[2] == 7 || lastPart[2] == 11  || lastPart[2] == 15) {
                    lastPartType = 19;
                } else {
                    lastPartType = -1;
                }

                snakePositions.set(lastItemIndex, new int[] {lastPart[0], lastPart[1], lastPartType});
            }
        }
        return snakePositions;
    }

    static boolean ifNotGameOver(int[] SnakeHeadPos, int xMax, int yMax){
        return SnakeHeadPos[0] >= 0 && SnakeHeadPos[0] < xMax && SnakeHeadPos[1] >= 0 && SnakeHeadPos[1] < yMax;
    }

    /**
     * Génère une position avec un type de souris pseudo aléatoire
     * @param limits Les limits pour les bornes [{Xmin, Xmax}, {Ymin, Ymax}, {Typmin, Typmax}]
     * @return une posistion avec un type de souris {X Y Type}
     */
    static int[] RandomMousPosition(ArrayList<int[]> limits, ArrayList<int[]> snake) {
        boolean ok;
        int posX;
        int posY;
        do {
            ok = true;  // Initialiser ok à true au début de chaque itération
            posX = RandomInt(limits.get(0)[0], limits.get(0)[1]);
            posY = RandomInt(limits.get(1)[0], limits.get(1)[1]);
            for (int[] part : snake) {
                if (part[0] == posX && part[1] == posY){
                    ok = false;  // Si une correspondance est trouvée, ok est mis à false
                    break;  // Sortir de la boucle for
                }
            }
        } while (!ok);  // Continuer tant que ok est false

        int moustype = RandomInt(limits.get(2)[0], limits.get(2)[1]);

        return new int[] {posX, posY, moustype};
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
