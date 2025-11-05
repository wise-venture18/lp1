import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// ---------------------------------------------
// Two-Pass Macro Processor
// ---------------------------------------------
// Pass I: Builds MNT (Macro Name Table), MDT (Macro Definition Table),
//         and Intermediate Code (program without macro definitions)
// Pass II: Expands macros using MNT and MDT and generates final code
// ---------------------------------------------
public class TwoPassMacroProcessor {

    // ------------------ MNT Entry ------------------
    // Stores each macro's name and the index where its definition starts in MDT
    static class MNTEntry {
        String name;
        int mdtIndex;

        MNTEntry(String name, int mdtIndex) {
            this.name = name;
            this.mdtIndex = mdtIndex;
        }
    }

    public static void main(String[] args) throws IOException {

        // ------------------ Sample Assembly Program ------------------
        String[] input = {
            "MACRO",
            "INCR &ARG1,&ARG2",
            "LDA &ARG1",
            "ADD &ARG2",
            "STA &ARG1",
            "MEND",
            "START",
            "INCR A,B",
            "END"
        };

        // Data structures for MNT, MDT, and Intermediate Code
        List<MNTEntry> MNT = new ArrayList<>();
        List<String> MDT = new ArrayList<>();
        List<String> IC = new ArrayList<>();

        // ------------------ PASS I ------------------
        // Build MNT, MDT and Intermediate Code
        pass1(input, MNT, MDT, IC);

        // ------------------ PASS II ------------------
        // Expand macro calls using tables generated in Pass I
        List<String> expanded = pass2(MNT, MDT, IC);

        // ------------------ Display Output ------------------
        System.out.println("MNT:");
        for (MNTEntry e : MNT)
            System.out.println(e.name + " -> MDT index: " + e.mdtIndex);

        System.out.println("\nMDT:");
        for (int i = 0; i < MDT.size(); i++)
            System.out.println(i + ": " + MDT.get(i));

        System.out.println("\nIntermediate Code:");
        for (String s : IC)
            System.out.println(s);

        System.out.println("\nExpanded Code:");
        for (String s : expanded)
            System.out.println(s);

        // ------------------ Write Output Files ------------------
        writeToFiles(MNT, MDT, IC, expanded);

        System.out.println("\nFiles Created:");
        System.out.println("MNT.txt, MDT.txt, IC.txt, ExpandedCode.txt");
    }

    // ------------------------------------------------------------
    // PASS I: Process the input and build MNT, MDT, and Intermediate Code
    // ------------------------------------------------------------
    static void pass1(String[] input, List<MNTEntry> MNT, List<String> MDT, List<String> IC) {
        boolean insideMacro = false; // Tracks if currently inside a macro definition
        String currentMacro = "";    // Stores current macro name

        for (String line : input) {
            line = line.trim();
            if (line.equals("")) continue; // Skip empty lines
            String[] tokens = line.split("\\s+");

            // If we encounter MACRO keyword, enter macro definition mode
            if (tokens[0].equalsIgnoreCase("MACRO")) {
                insideMacro = true;
                continue;
            }

            // If we are currently inside a macro definition
            if (insideMacro) {
                if (tokens[0].equalsIgnoreCase("MEND")) {
                    // End of macro definition
                    MDT.add("MEND");
                    insideMacro = false;
                    currentMacro = "";
                    continue;
                }

                // First line after MACRO keyword → macro prototype (name + args)
                if (currentMacro.equals("")) {
                    currentMacro = tokens[0];
                    MNT.add(new MNTEntry(currentMacro, MDT.size())); // add entry to MNT
                    MDT.add(line); // add macro prototype to MDT
                } else {
                    MDT.add(line); // add macro body lines to MDT
                }
            } else {
                // Outside macro definition → add to intermediate code
                IC.add(line);
            }
        }
    }

    // ------------------------------------------------------------
    // PASS II: Expand all macro calls using the tables from Pass I
    // ------------------------------------------------------------
    static List<String> pass2(List<MNTEntry> MNT, List<String> MDT, List<String> IC) {
        List<String> expanded = new ArrayList<>();

        for (String line : IC) {
            String[] tokens = line.split("\\s+");
            if (tokens.length == 0) continue;

            // Check if this instruction is a macro call
            MNTEntry macro = null;
            for (MNTEntry e : MNT)
                if (tokens[0].equalsIgnoreCase(e.name))
                    macro = e;

            if (macro != null) {
                // If macro call found, fetch its definition from MDT
                int mdtIndex = macro.mdtIndex;
                String proto = MDT.get(mdtIndex);
                String[] protoTokens = proto.split("\\s+");
                String formalStr = protoTokens.length > 1 ? protoTokens[1] : "";
                String[] formals = formalStr.split(",");

                // Extract actual arguments from macro call
                String actualStr = tokens.length > 1 ? tokens[1] : "";
                String[] actuals = actualStr.split(",");

                // Build ALA (Argument List Array) to map formal to actual args
                Map<String, String> ALA = new HashMap<>();
                for (int i = 0; i < formals.length; i++) {
                    String formal = formals[i].trim();
                    String actual = i < actuals.length ? actuals[i].trim() : "";
                    ALA.put(formal, actual);
                }

                // Expand macro body line by line, replacing arguments
                int i = mdtIndex + 1;
                while (!MDT.get(i).equalsIgnoreCase("MEND")) {
                    String expandedLine = MDT.get(i);
                    for (Map.Entry<String, String> entry : ALA.entrySet()) {
                        expandedLine = expandedLine.replace(entry.getKey(), entry.getValue());
                    }
                    expanded.add(expandedLine);
                    i++;
                }
            } else {
                // Not a macro call → copy directly
                expanded.add(line);
            }
        }
        return expanded;
    }

    // ------------------------------------------------------------
    // Write outputs to text files for verification
    // ------------------------------------------------------------
    static void writeToFiles(List<MNTEntry> MNT, List<String> MDT, List<String> IC, List<String> expanded) throws IOException {

        // Write MNT
        FileWriter f1 = new FileWriter("MNT.txt");
        for (MNTEntry e : MNT)
            f1.write(e.name + " -> MDT index: " + e.mdtIndex + "\n");
        f1.close();

        // Write MDT
        FileWriter f2 = new FileWriter("MDT.txt");
        for (int i = 0; i < MDT.size(); i++)
            f2.write(i + ": " + MDT.get(i) + "\n");
        f2.close();

        // Write Intermediate Code
        FileWriter f3 = new FileWriter("IC.txt");
        for (String s : IC)
            f3.write(s + "\n");
        f3.close();

        // Write Expanded Code
        FileWriter f4 = new FileWriter("ExpandedCode.txt");
        for (String s : expanded)
            f4.write(s + "\n");
        f4.close();
    }
}
