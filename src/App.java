public class App {
    
    public static void main(String[] args){
        Grille g = new Grille();
        g.afficherGrille();
        g.jouer(1, 2,2);
        g.jouer(2, 0,2);
        g.jouer(1, 1,0);
        g.afficherGrille();
    }
}
