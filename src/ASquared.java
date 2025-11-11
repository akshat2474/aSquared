import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ASquared {
    

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String filename = args[0];
            runFile(filename);
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

    
    public static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();
        
        Parser parser = new Parser(tokens);
        parser.parseAndExecute(); 
    }
}