import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Program which reads a Protein Data Bank and identifies the residue
 * at which the chain is most clearly partitioned into two parts/domains.
 * By Lenia Malki
 *
 * How to run:
 * 1. Run the command line: java Single_segment_scan file.pdb
 * where "file" refers to the PDB-file.
 */
public class Single_segment_scan {

    static ArrayList<String[]> alphaCarbons = new ArrayList<>();
    static ArrayList<int[]> splitScores = new ArrayList<>();
    static List<int[]> tempScoreArray = new ArrayList<>();

    static int INT_A = 0;
    static int INT_B = 0;
    static int EXT_AB = 0;


    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("File argument missing. First, run command line: javac Single_segment_scan.java and then java Single_segment_scan file.pdb");
            System.exit(0);
        }
        try {
            FileInputStream file = new FileInputStream(args[0]);
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                getCA_data(fileScanner.nextLine());
            }
            fileScanner.close();

            generatePartition();
            getMaxSplitValue();

        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not find file.");
            e.printStackTrace();
        }
    }

    /**
     * Methods which extracts CA-data
     *
     * @param pdb_line the data inputted
     */
    private static void getCA_data(String pdb_line) {
        if (pdb_line.startsWith("ATOM")) {
            String[] atom_line = pdb_line.split("\\s+");

            //If it is a CA_atom, save its data to the list of alphaCarbons
            if (atom_line[2].equals("CA")) {

                String[] residue_line = {atom_line[5], atom_line[6], atom_line[7], atom_line[8]};
                alphaCarbons.add(alphaCarbons.size(), residue_line);
            }
        }
    }

    /**
     * Calculates the distance between two residues
     *
     * @param atom_1 CA-atom 1
     * @param atom_2 CA-atom 2
     * @return the calculation
     */
    private static double calculateDistance(String[] atom_1, String[] atom_2) {

        double atom1_x, atom1_y, atom1_z;
        double atom2_x, atom2_y, atom2_z;

        atom1_x = Double.parseDouble(atom_1[1]);
        atom1_y = Double.parseDouble(atom_1[2]);
        atom1_z = Double.parseDouble(atom_1[3]);

        atom2_x = Double.parseDouble(atom_2[1]);
        atom2_y = Double.parseDouble(atom_2[2]);
        atom2_z = Double.parseDouble(atom_2[3]);

        return Math.sqrt(Math.pow(atom1_x - atom2_x, 2) + Math.pow(atom1_y - atom2_y, 2) + Math.pow(atom1_z - atom2_z, 2));
    }

    /**
     * Handles splitting of residue pairs in sequence
     */
    private static void generatePartition() {

        //added difference of segment lengths
        int dx = 0;

        //Continue along the sequence staring from a minimum split index of 2.
        while (dx + 2 < alphaCarbons.size()) {

            int split_index = dx + 2; ///The length of the index at which we have split up segment A


            //PARTITION A
            tempScoreArray = new ArrayList<>();
            for (int i = 0; i < split_index; i++) {
                for (int j = 0; j < split_index; j++) {
                    if (i != j) {
                        extractResiduePair(i, j);
                        INT_A = tempScoreArray.size();
                    }
                }
            }


            //PARTITION B
            tempScoreArray = new ArrayList<>();
            for (int i = split_index; i < alphaCarbons.size(); i++) {
                for (int j = split_index; j < alphaCarbons.size(); j++) {
                    if (i != j) {
                        extractResiduePair(i, j);
                        INT_B = tempScoreArray.size();
                    }
                }
            }

            //PARTITION EXT_AB
            tempScoreArray = new ArrayList<>();
            for (int i = 0; i < split_index; i++) {
                for (int j = split_index; j < alphaCarbons.size(); j++) {
                    extractResiduePair(i, j);
                    EXT_AB = tempScoreArray.size();
                }
            }

            int[] scores = new int[2];
            scores[0] = split_index;
            scores[1] = ((INT_A / EXT_AB) * (INT_B / EXT_AB));
            splitScores.add(scores);

            dx += 1;
        }
    }

    /**
     * Helper for generateDomak()
     *
     * @param i index i from generateDomak()
     * @param j index j from generateDomak()
     */
    private static void extractResiduePair(int i, int j) {

        if (calculateDistance(alphaCarbons.get(i), alphaCarbons.get(j)) < 8) {
            boolean exists = false;
            for (int[] ints : tempScoreArray) {
                if (ints[1] == Integer.parseInt(alphaCarbons.get(i)[0])) {
                    exists = true;
                }
            }
            if (!exists) {
                int[] row = new int[2];
                row[0] = Integer.parseInt(alphaCarbons.get(i)[0]);
                row[1] = Integer.parseInt(alphaCarbons.get(j)[0]);
                tempScoreArray.add(row);
            }

        }
    }

    /**
     * Extracts the highest score from scoreValues-array
     */
    public static void getMaxSplitValue() {
        int maxSplitValue = 0;
        int max_index = 0;
        for (int i = 0; i < splitScores.size(); i++) {
            int[] row = splitScores.get(i);
            if (row[1] > maxSplitValue) {
                maxSplitValue = row[1];
                max_index = i;
            }
        }
        System.out.println("Best index for partition: " + max_index + " with a max split value of : " + maxSplitValue);
    }

}







