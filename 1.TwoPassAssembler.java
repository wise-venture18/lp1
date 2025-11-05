// Steps To Execute (Terminal Commands) :
// javac Pass1.java
// java Pass1 input.txt
// javac Pass2.java
// java Pass2 IC.txt

//Create Pass1.java
import java.io.*;
import java.util.*;

public class Pass1 {
    static Map<String, Integer> SYMTAB = new LinkedHashMap<>();
    static List<String> LITTAB = new ArrayList<>();
    static List<String> IC = new ArrayList<>();
    static int LC = 0;

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java Pass1 input.txt");
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.replace(",", "").split("\\s+");
            String op = parts[0].toUpperCase();

            if (op.equals("START")) {
                LC = Integer.parseInt(parts[1]);
                IC.add("(AD,01)(C," + LC + ")");
                continue;
            }

            if (!isMnemonic(op)) {                 // label
                SYMTAB.put(parts[0], LC);
                op = parts[1].toUpperCase();
            }

            if (op.equals("END")) {
                IC.add("(AD,02)");
                break;
            }

            if (op.equals("MOVER") || op.equals("ADD") || op.equals("MOVEM")) {
                String reg = parts[1].equalsIgnoreCase("AREG") ? "1" : "0";
                String operand = parts[2];

                if (operand.startsWith("=")) {
                    if (!LITTAB.contains(operand)) LITTAB.add(operand);
                    IC.add("(IS," + code(op) + ")(" + reg + ")(L," + (LITTAB.indexOf(operand) + 1) + ")");
                } else {
                    if (!SYMTAB.containsKey(operand)) SYMTAB.put(operand, 0);
                    IC.add("(IS," + code(op) + ")(" + reg + ")(S," + operand + ")");
                }
                LC++;
            }

            if (op.equals("DS")) {
                int size = Integer.parseInt(parts[2]);
                IC.add("(DL,02)(C," + size + ")");
                LC += size;
            }
        }

        // Write files
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

    static boolean isMnemonic(String s) {
        String[] m = {"STOP","ADD","SUB","MULT","MOVER","MOVEM","COMP","BC","DIV","READ","PRINT","START","END","DS","DC"};
        for (String x : m) if (x.equalsIgnoreCase(s)) return true;
        return false;
    }

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
import java.io.*;
import java.util.*;

public class Pass2 {
    public static void main(String[] args) throws Exception {
        BufferedReader ic = new BufferedReader(new FileReader("IC.txt"));
        BufferedReader sym = new BufferedReader(new FileReader("SYMTAB.txt"));
        BufferedReader lit = new BufferedReader(new FileReader("LITTAB.txt"));

        Map<String, Integer> SYMTAB = new HashMap<>();
        Map<Integer, Integer> LITTAB = new HashMap<>();

        String line;
        while ((line = sym.readLine()) != null) {
            String[] p = line.split("\\s+");
            SYMTAB.put(p[0], Integer.parseInt(p[1]));
        }
        int i = 1;
        while ((line = lit.readLine()) != null) {
            String[] p = line.split("\\s+");
            LITTAB.put(i++, Integer.parseInt(p[1]));
        }

        PrintWriter mc = new PrintWriter("MACHINECODE.txt");

        while ((line = ic.readLine()) != null) {
            if (line.contains("(IS,")) {
                String opcode = line.substring(4, 6);
                String reg = line.substring(8, 9);
                String addr = "000";

                if (line.contains("(S,")) {
                    String symName = line.substring(line.indexOf("(S,") + 3, line.lastIndexOf(")"));
                    addr = String.format("%03d", SYMTAB.getOrDefault(symName, 0));
                } else if (line.contains("(L,")) {
                    int litIdx = Integer.parseInt(line.substring(line.indexOf("(L,") + 3, line.lastIndexOf(")")));
                    addr = String.format("%03d", LITTAB.getOrDefault(litIdx, 0));
                }

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
  
