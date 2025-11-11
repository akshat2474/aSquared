import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.BufferedReader; 
import java.io.InputStreamReader; 

public class ASquared {
    
    private static final Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException { 
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

            try {
                run(line);
            } catch (RuntimeException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    public static void run(String source) {
        // Lexical analysis
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        // Parsing
        Parser parser = new Parser(tokens);
        AST.Program program = parser.parse();
        
        interpreter.interpret(program);
    }
}