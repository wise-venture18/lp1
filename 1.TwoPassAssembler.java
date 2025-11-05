// Steps To Execute (Terminal Commands) :
// javac Pass1.java
// java Pass1 input.txt
// javac Pass2.java
// java Pass2 IC.txt

// Create Pass1.java
// -------------------- PASS 1 --------------------
// Performs first pass of assembler
// Generates: IC.txt, SYMTAB.txt, LITTAB.txt
// ------------------------------------------------

import java.io.*;
import java.util.*;

public class Pass1 {
    static Map<String, Integer> SYMTAB = new LinkedHashMap<>();  // Symbol Table
    static List<String> LITTAB = new ArrayList<>();               // Literal Table
    static List<String> IC = new ArrayList<>();                   // Intermediate Code
    static int LC = 0;                                            // Location Counter

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java Pass1 input.txt");
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;

        // -------- Read Assembly Line by Line --------
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.replace(",", "").split("\\s+");
            String op = parts[0].toUpperCase();

            // START directive
            if (op.equals("START")) {
                LC = Integer.parseInt(parts[1]);
                IC.add("(AD,01)(C," + LC + ")");
                continue;
            }

            // If first word is not mnemonic → it's a label
            if (!isMnemonic(op)) {
                SYMTAB.put(parts[0], LC);
                op = parts[1].toUpperCase();
            }

            // END directive
            if (op.equals("END")) {
                IC.add("(AD,02)");
                break;
            }

            // Imperative statements (MOVER, ADD, MOVEM)
            if (op.equals("MOVER") || op.equals("ADD") || op.equals("MOVEM")) {
                String reg = parts[1].equalsIgnoreCase("AREG") ? "1" : "0";
                String operand = parts[2];

                // If operand is a literal (=)
                if (operand.startsWith("=")) {
                    if (!LITTAB.contains(operand)) LITTAB.add(operand);
                    IC.add("(IS," + code(op) + ")(" + reg + ")(L," + (LITTAB.indexOf(operand) + 1) + ")");
                } 
                // If operand is a symbol
                else {
                    if (!SYMTAB.containsKey(operand)) SYMTAB.put(operand, 0);
                    IC.add("(IS," + code(op) + ")(" + reg + ")(S," + operand + ")");
                }
                LC++;
            }

            // Declarative statement (DS)
            if (op.equals("DS")) {
                int size = Integer.parseInt(parts[2]);
                IC.add("(DL,02)(C," + size + ")");
                LC += size;
            }
        }

        // -------- Write Output Files --------
        PrintWriter ic = new PrintWriter("IC.txt");
        for (String s : IC) ic.println(s);
        ic.close();

        PrintWriter sym = new PrintWriter("SYMTAB.txt");
        for (Map.Entry<String, Integer> e : SYMTAB.entrySet())
            sym.println(e.getKey() + " " + e.getValue());
        sym.close();

        PrintWriter lit = new PrintWriter("LITTAB.txt");
        int addr = LC;
        for (String l : LITTAB) lit.println(l + " " + (addr++));
        lit.close();

        System.out.println("Pass1 complete. IC, SYMTAB, LITTAB created.");
    }

    // Check if word is mnemonic
    static boolean isMnemonic(String s) {
        String[] m = {"STOP","ADD","SUB","MULT","MOVER","MOVEM","COMP","BC","DIV","READ","PRINT","START","END","DS","DC"};
        for (String x : m) if (x.equalsIgnoreCase(s)) return true;
        return false;
    }

    // Return operation code for mnemonic
    static String code(String s) {
        switch (s.toUpperCase()) {
            case "MOVER": return "04";
            case "MOVEM": return "05";
            case "ADD": return "01";
            default: return "00";
        }
    }
}




//______ Create Pass2.java File_____
// -------------------- PASS 2 --------------------
// Performs second pass of assembler
// Reads: IC.txt, SYMTAB.txt, LITTAB.txt
// Generates: MACHINECODE.txt
// ------------------------------------------------

import java.io.*;
import java.util.*;

public class Pass2 {
    public static void main(String[] args) throws Exception {

        // Read input files
        BufferedReader ic = new BufferedReader(new FileReader("IC.txt"));
        BufferedReader sym = new BufferedReader(new FileReader("SYMTAB.txt"));
        BufferedReader lit = new BufferedReader(new FileReader("LITTAB.txt"));

        // Symbol and Literal tables for lookup
        Map<String, Integer> SYMTAB = new HashMap<>();
        Map<Integer, Integer> LITTAB = new HashMap<>();

        String line;

        // Load SYMTAB
        while ((line = sym.readLine()) != null) {
            String[] p = line.split("\\s+");
            SYMTAB.put(p[0], Integer.parseInt(p[1]));
        }

        // Load LITTAB
        int i = 1;
        while ((line = lit.readLine()) != null) {
            String[] p = line.split("\\s+");
            LITTAB.put(i++, Integer.parseInt(p[1]));
        }

        // Output file for machine code
        PrintWriter mc = new PrintWriter("MACHINECODE.txt");

        // -------- Translate Intermediate Code to Machine Code --------
        while ((line = ic.readLine()) != null) {
            if (line.contains("(IS,")) { // Only for Imperative Statements
                String opcode = line.substring(4, 6);  // Extract opcode
                String reg = line.substring(8, 9);     // Extract register
                String addr = "000";                   // Default address

                // If operand is symbol
                if (line.contains("(S,")) {
                    String symName = line.substring(line.indexOf("(S,") + 3, line.lastIndexOf(")"));
                    addr = String.format("%03d", SYMTAB.getOrDefault(symName, 0));
                } 
                // If operand is literal
                else if (line.contains("(L,")) {
                    int litIdx = Integer.parseInt(line.substring(line.indexOf("(L,") + 3, line.lastIndexOf(")")));
                    addr = String.format("%03d", LITTAB.getOrDefault(litIdx, 0));
                }

                // Construct final machine instruction
                String out = "+ " + opcode + " " + reg + " " + addr;
                System.out.println(out);
                mc.println(out);
            }
        }

        mc.close();
        System.out.println("Pass2 complete. MACHINECODE.txt created.");
    }
}

//____Create input.txt_______
START 200
MOVER AREG, A
ADD AREG, =5
MOVEM AREG, RESULT
RESULT DS 1
END
  
