public class Grille {

    public char[][] grille;

    public Grille() {
        initialierGrille();
    }

    public void initialierGrille() {
        this.grille = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grille[i][j] = ' ';
            }
        }
    }

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

}
