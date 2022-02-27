import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Program which reads a Protein Data Bank and generates a file containing alpha-carbon residue pairs
 * depending on whether or not they are within 8Å threshold from each other.
 * By Lenia Malki
 *
 * How to run:
 *  * 1. Run the command line: java Single_segment_scan file.pdb
 *  * where "file" refers to the PDB-file. A output file will be created by the name "residue.pairs"
 *  2. Give the command line argument dotplot.tcl file.pairs
 *  where "file" refers to the newly generated file of pairs.
 */
public class Distance_map_generator {

    /**
     * Data of all alpha carbons
     */
    static ArrayList<String[]> alphaCarbons = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("File argument missing. First, run command line: javac Distance_map_generator.java and then java Distance_map_generator file.pdb");
            System.exit(0);
        }
        try {

            FileInputStream inputFile = new FileInputStream(args[0]);
            Scanner fileScanner = new Scanner(inputFile);

            while (fileScanner.hasNextLine()) {
                getCA_data(fileScanner.nextLine());
            }
            generateResidueFile();
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not find file.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Hello

    /**
     * Methods which checks for CA atoms
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
     * Calculates the distance between one pair of residues
     *
     * @param atom_1 CA-atom 1
     * @param atom_2 CA-atom 2
     * @return the calculateddistance
     */
    static double calculateDistance(String[] atom_1, String[] atom_2) {

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
     * Calculates the distance between two alpha-carbon atoms and saves their residue numbers to a file
     *
     * @throws IOException throws exception if writer cannot write to file
     */
    private static void generateResidueFile() throws IOException {
        int fileNumber = 0;

        File outputFile = new File("residue.pairs");

        while (outputFile.exists()) {
            fileNumber++;
            outputFile = new File("residue" + fileNumber + ".pairs");
        }


        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        for (int i = 0; i <= alphaCarbons.size() - 1; i++) {
            for (int j = 0; j <= alphaCarbons.size() - 1; j++) {

                double distance = calculateDistance(alphaCarbons.get(i), alphaCarbons.get(j));

                //If distance is less than 8, save the pair to the list of pairs and append it to the outputFile
                if (distance < 8) {

                    int residueX = Integer.parseInt(alphaCarbons.get(i)[0]); //Gets the first number of the pair {residueX, -}
                    int residueY = Integer.parseInt(alphaCarbons.get(j)[0]); //Gets the second number of the pair : {-, residueY}

                    writer.append(String.valueOf(residueX)).append(" ").append(String.valueOf(residueY));
                    writer.newLine();

                }
            }
        }
        writer.close();

    }
}




