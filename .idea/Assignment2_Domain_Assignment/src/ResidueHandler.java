import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Abstract class which sorts out a list of interacting residues do exclusively unique tuples.
 * NOT IN USE ATM
 */
public abstract class ResidueHandler {

    static ArrayList<int[]> residuePairs = new ArrayList<>();

    /**
     * Calculates the distance between two alpha-carbon atoms and saves their residue numbers to an array
     **/
    private static void matchResiduePairs() throws IOException {
        for (int i = 0; i <= Distance_map_generator.alphaCarbons.size() - 1; i++) {
            for (int j = 0; j <= Distance_map_generator.alphaCarbons.size() - 1; j++) {

                double distance = Distance_map_generator.calculateDistance(Distance_map_generator.alphaCarbons.get(i), Distance_map_generator.alphaCarbons.get(j));

                //If distance is less than 8, save the pair to the list of pairs and append it to the file
                if (distance < 8) {

                    int residueX = Integer.parseInt(Distance_map_generator.alphaCarbons.get(i)[0]); //Gets the first number of the pair {residueX, -}
                    int residueY = Integer.parseInt(Distance_map_generator.alphaCarbons.get(j)[0]); //Gets the second number of the pair : {-, residueY}

                    int[] newPair = {residueX, residueY};
                    if (residueX != residueY) {
                        residuePairs.add(newPair);
                    }
                }
            }
        }
    }

    /**
     * Method for extracting only unique tuples of residue pairs within a threshold
     */
    private static void extractUniquePairs() {

        for (int i = 0; i < residuePairs.size(); i++) {
            for (int j = i + 1; j < residuePairs.size(); j++) {
                {
                    int[] currentPair = {residuePairs.get(i)[0], residuePairs.get(i)[1]};
                    int[] mirroredPair = {currentPair[1], currentPair[0]};

                    if ((Arrays.equals(residuePairs.get(j), mirroredPair))) {
                        residuePairs.remove(j);
                        break;
                    }
                }
            }
        }
    }

}
