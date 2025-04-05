
/**
 * Classe permettant de reproduire une grille de morpion et de pouvoir y jouer
 */
public class Grille {

    /** Grille de morpion */
    public char[][] grille;

    /**
     * Constructeur, initialise la grille
     */
    public Grille() {
        initialiserGrille();
    }

    /**
     * initialise chaque élément de la grille par le caractère espace
     */
    public void initialiserGrille() {
        this.grille = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grille[i][j] = ' ';
            }
        }
    }

    /**
     * Permet à un des joueurs d'effectuer un coup
     * 
     * @param numJoueur Le numéro du joueur (0 ou 1)
     * @param ligne     La ligne du coup
     * @param colonne   La colonne du coup
     * @return Si le coup a été joué ou non
     */
    public boolean jouer(int numJoueur, int ligne, int colonne) {
        if (grille[ligne][colonne] == ' ') {
            if (numJoueur == 0) {
                grille[ligne][colonne] = 'X';
            } else {
                grille[ligne][colonne] = 'O';
            }
            return true;
        }
        return false;
    }

    /**
     * Affiche la grille dans la console
     */
    public void afficherGrille() {
        String returnString = "";
        for (char[] ligne : grille) {
            returnString += "-------------\n";
            returnString += "| ";
            for (char valeur : ligne) {
                returnString += valeur + " | ";
            }
            returnString += "\n";
        }
        returnString += "-------------";
        System.out.println(returnString);
    }

    /**
     * Retourne la chaîne de caractères permettant d'afficher la grille
     */
    public String toString() {
        String returnString = "";
        for (char[] ligne : grille) {
            returnString += "-------------\n";
            returnString += "| ";
            for (char valeur : ligne) {
                returnString += valeur + " | ";
            }
            returnString += "\n";
        }
        returnString += "-------------";
        return returnString;
    }

    /**
     * Vérifie si le symbole passé en entrée a gagné la partie
     * 
     * @param symbole Le symbole d'un des joueurs ('X' ou 'O')
     * @return Booléen induquant si le joueur a gagné la partie
     */
    public boolean verifierGagnant(char symbole) {

        for (int i = 0; i < 3; i++) {
            if (grille[i][0] == symbole && grille[i][1] == symbole && grille[i][2] == symbole) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (grille[0][i] == symbole && grille[1][i] == symbole && grille[2][i] == symbole) {
                return true;
            }
        }

        if (grille[0][0] == symbole && grille[1][1] == symbole && grille[2][2] == symbole) {
            return true;
        }

        if (grille[0][2] == symbole && grille[1][1] == symbole && grille[2][0] == symbole) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si la grille est remplie
     * 
     * @return Booléen indiquand si la grille est remplie
     */
    public boolean grilleRemplie() {
        for (int ligne = 0; ligne < 3; ligne++) {
            for (int colonne = 0; colonne < 3; colonne++) {
                if (grille[ligne][colonne] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

}
