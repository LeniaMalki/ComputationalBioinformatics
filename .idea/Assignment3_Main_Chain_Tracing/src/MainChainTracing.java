import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Program which read the coordinates of a set of alpha-carbon atoms from a file,
 * and identifies the order of them in the chain.
 * By Lenia Malki
 * <p>
 * Run cmd-line javac MainChainTracing.java and then java MainChainTracing file.txt
 * <p>
 * In order to find the order of alpha-carbons in a given input, the first atom with only 1 neighbour
 * is used as a reference point. This atom has to be located at one endpoint. The atom is removed from the chain.
 * This process is repeated during the length of the chain until the input file consist of only 3 atoms.
 * In that case, the current atom is returned immediately in order to avoid swapping the order.
 */
public class MainChainTracing {
    static private final ArrayList<Atom> atoms = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("File argument missing");
            System.exit(0);
        }
        try {
            FileInputStream file = new FileInputStream(args[0]);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                parseData(scanner.nextLine());
            }
            final int num_atoms = atoms.size();
            printOrders(num_atoms);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for reformatting input text
     *
     * @param nextLine next line of input data
     */
    private static void parseData(String nextLine) {
        String[] temp = nextLine.split("\\s+");
        double[] residue_line = {Double.parseDouble(temp[2]), Double.parseDouble(temp[3]), Double.parseDouble(temp[4])};
        Atom atom = new Atom();
        atom.name = temp[1];
        atom.coordinates = residue_line;
        atoms.add(atom);
    }

    /**
     * Method which calculates the distance between two atoms
     *
     * @param atom1 atom 1
     * @param atom2 atom 2
     * @return the distance as a double
     */
    private static double calculateDistance(Atom atom1, Atom atom2) {
        double atom1_x = atom1.coordinates[0];
        double atom1_y = atom1.coordinates[1];
        double atom1_z = atom1.coordinates[2];

        double atom2_x = atom2.coordinates[0];
        double atom2_y = atom2.coordinates[1];
        double atom2_z = atom2.coordinates[2];

        return (Math.sqrt(Math.pow(atom1_x - atom2_x, 2) + Math.pow(atom1_y - atom2_y, 2) + Math.pow(atom1_z - atom2_z, 2)));
    }

    /**
     * Counts an atoms direct neighbours which varies between 1 and 2.
     *
     * @param a given atom to use as base
     * @return the number of direct neighbours
     */
    private static int countNeighbours(Atom a) {
        int numNeighbours = 0;

        for (Atom b : atoms) {
            if (a != b) {
                double distance = calculateDistance(a, b);
                if (distance > 3.780033465 && distance < 3.857441898) {
                    numNeighbours++;
                }
            }
        }
        return numNeighbours;
    }

    /**
     * Finds the first atom in the chain.
     *
     * @return the first atom
     */
    private static Atom findFirst() {
        Atom firstAtom = new Atom();
        for (Atom a : atoms) {
            if (countNeighbours(a) <= 1) {
                firstAtom = a;
            }
            if (MainChainTracing.atoms.size() == 3) { //If the size of the chain is 3, don't continue looping.
                firstAtom = a;
                break;
            }
        }
        return firstAtom;
    }

    /**
     * Prints the orders of the atoms. Not really intuitive but it works in reverse.
     * The "name"-field works as the order and does not correspond to the actual atom.
     *
     * @param num_atoms length of the original chain
     */
    private static void printOrders(int num_atoms) {
        int alphaCarbons = 0;
        System.out.println("Order of alpha-carbon atoms: ");
        for (int i = 0; i < num_atoms; i++) {
            Atom a = findFirst();
            atoms.remove(a);
            System.out.println(a.name);
            alphaCarbons++;
        }
        System.out.println("Total number of alpha-carbon atoms: " + alphaCarbons);
    }

    /**
     * Class for an atom to keep track of its name and coordinates
     */
    public static class Atom {
        String name;
        double[] coordinates;
    }
}

