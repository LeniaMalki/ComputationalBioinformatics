import java.io.*;
import java.util.*;

/**
 * Program that detects steric overlap between two sets of spheres.
 * By Lenia Malki
 * <p>
 * How to run:
 * 1. Run cmd-line: javac main.java
 * 2. Run cmd-line java Steric_Overlap file1.pdb file2.pdb
 * where file1.pdb and file2.pdb refers to respective filename or path.
 */
public class Steric_Overlap {

    static private final ArrayList<Atom> atom_file_1 = new ArrayList<>();
    static private final ArrayList<Atom> atom_file_2 = new ArrayList<>();
    static private final ArrayList<Atom> overlaps = new ArrayList<>();
    static final int atom_radius = 2;
    private static int comparisons;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("File arguments missing");
            System.exit(0);
        }
        try {
            FileInputStream file1 = new FileInputStream(args[0]);
            FileInputStream file2 = new FileInputStream(args[1]);

            Scanner scanner1 = new Scanner(file1);
            Scanner scanner2 = new Scanner(file2);

            while (scanner1.hasNextLine()) {
                parseData(scanner1.nextLine(), atom_file_1);
            }

            while (scanner2.hasNextLine()) {
                parseData(scanner2.nextLine(), atom_file_2);
            }

            comparisons = findOverlaps();
            sort();
            generateOverlapsFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for reformatting input text
     *
     * @param nextLine next line of input data
     */
    private static void parseData(String nextLine, ArrayList<Atom> list) {
        String[] temp = nextLine.split("\\s+");
        if (temp[0].equals("ATOM") || temp[0].equals("HETATM")) {
            double[] residue_line = {Double.parseDouble(temp[6]), Double.parseDouble(temp[7]), Double.parseDouble(temp[8])};
            Atom atom = new Atom();
            atom.atom_number = Integer.parseInt(temp[1]);
            atom.acid = temp[3];
            atom.b = Integer.parseInt(temp[5]);
            atom.c = temp[2];
            atom.coordinates = residue_line;
            list.add(atom);
        }
    }

    /**
     * Class for an atom to keep track of its name and coordinates
     */
    private static class Atom {
        int atom_number;
        String acid;
        int b;
        String c;
        double[] coordinates;
    }

    /**
     * Calculates the distance between two residues
     *
     * @param a1 CA-atom 1
     * @param a2 CA-atom 2
     * @return the calculation
     */
    private static double calculateDistance(Atom a1, Atom a2) {
        double atom1_x = a1.coordinates[0];
        double atom1_y = a1.coordinates[1];
        double atom1_z = a1.coordinates[2];

        double atom2_x = a2.coordinates[0];
        double atom2_y = a2.coordinates[1];
        double atom2_z = a2.coordinates[2];

        return (Math.sqrt(Math.pow(atom1_x - atom2_x, 2) + Math.pow(atom1_y - atom2_y, 2) + Math.pow(atom1_z - atom2_z, 2)));

    }

    /**
     * Finds steric overlaps between two sets of spheres
     *
     * @return the number of comparisons made
     */
    private static int findOverlaps() {
        int comparisons = 0;

        for (Atom atom_1 : atom_file_1) {
            for (Atom atom_2 : atom_file_2) {
                if (!overlaps.contains(atom_2)) {
                    if ((calculateDistance(atom_1, atom_2) < (2 * atom_radius))) {
                        overlaps.add(atom_2);
                    }
                    comparisons++;
                }
            }
        }
        return comparisons;
    }

    /**
     * Sorts an arraylist of atoms in ascending order of atom_number
     */
    private static void sort() {
        overlaps.sort(Comparator.comparing(o -> o.atom_number));
    }

    private static void generateOverlapsFile() throws IOException {
        int fileNumber = 0;

        File outputFile = new File("overlaps.txt");

        while (outputFile.exists()) {
            fileNumber++;
            outputFile = new File("overlaps" + fileNumber + ".txt");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.append("Number of clashing atoms: ").append(String.valueOf(overlaps.size()));
        writer.newLine();
        writer.append("Number of comparisons made: ").append(String.valueOf(Steric_Overlap.comparisons));
        writer.newLine();
        for (Atom overlap : overlaps) {

            writer.append(String.valueOf(overlap.atom_number)).append(" ").append(overlap.acid).
                    append(" ").append(String.valueOf(overlap.b)).append(" ").append(String.valueOf(overlap.c));
            writer.newLine();

        }
        writer.close();
    }

    //Hellooo
}
