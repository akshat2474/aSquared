import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.BufferedReader; // Added for REPL
import java.io.InputStreamReader; // Added for REPL

public class ASquared {
    
    // We need the Interpreter to be persistent for the REPL
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException { // Added throws
        if (args.length > 0) {
            String filename = args[0];
            runFile(filename);
        } else {
            runREPL();
        }
    }
    
    private static void runFile(String filename) throws IOException {
        try {
            String source = new String(Files.readAllBytes(Paths.get(filename)));
            run(source);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.err.println("Runtime error: " + e.getMessage());
            System.exit(1);
        }
    }

    // --- New Method for REPL ---
    private static void runREPL() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("A² Language REPL");
        System.out.println("Type 'exit' to quit.");

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            
            if (line == null || line.equals("exit")) {
                break;
            }
            
            if (line.trim().isEmpty()) {
                continue;
            }

            // Run the line, but catch errors and continue
            try {
                run(line);
            } catch (RuntimeException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    // The 'run' method is now used by both the file runner and the REPL.
    // It uses the single, persistent 'interpreter' instance.
    public static void run(String source) {
        // Lexical analysis
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        // Parsing
        Parser parser = new Parser(tokens);
        AST.Program program = parser.parse();
        
        // Interpretation
        // Note: We use the static 'interpreter' instance
        interpreter.interpret(program);
    }
}