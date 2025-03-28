public class Grille {
    
    public String[][] grille;

    public Grille() {
        this.grille = new String[3][3];
        // Initialisation avec des espaces vides
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.grille[i][j] = " ";
            }
        }
    }

    public void jouer(int numJoueur, int ligne, int colonne){
        if (numJoueur == 1) {
            grille[ligne][colonne] = "X";
        } else {
            grille[ligne][colonne] = "O";
        }
    }

    public void afficherGrille(){
        String returnString = "";
        for (String[] ligne : grille) {
            returnString += "-------------\n";
            returnString += "| ";
            for (String valeur : ligne) {
                returnString += valeur + " | ";
            }
            returnString += "\n";
        }
        returnString += "-------------";
        System.out.println(returnString);
    }


}
