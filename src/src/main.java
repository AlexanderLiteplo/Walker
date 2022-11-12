package src;
//todos
// find out sentence input
// make sentence generator
// modify walksat
// generate specified number of sentences
// make timer
// run everything
// save output
// graph saved output
public class main {
    public static void main(String[] args) {
        WalkSAT walk = new WalkSAT();

        String sentence = "";
        int numflips = 1000;
        double probabilityOfRandomWalk = 0.5;
        walk.findModelFor(sentence, numflips, probabilityOfRandomWalk);
    }
}
