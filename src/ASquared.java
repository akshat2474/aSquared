import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ASquared {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("A² Language Interpreter");
            System.out.println("Usage: java ASquared <filename.a2>");
            System.out.println("\nExample program:");
            System.out.println("  let x = 10");
            System.out.println("  let y = 20");
            System.out.println("  print x + y");
            return;
        }
        
        String filename = args[0];
        
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
    
    public static void run(String source) {
        // Lexical analysis
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        // Parsing
        Parser parser = new Parser(tokens);
        AST.Program program = parser.parse();
        
        // Interpretation
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(program);
    }
}

