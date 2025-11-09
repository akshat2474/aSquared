public class Token {
    public enum TokenType {
        // Literals and Identifiers
        NUMBER, IDENTIFIER, STRING,
        
        // Keywords
        LET, PRINT, IF, ELSE, WHILE, END,
        
        // Operators
        PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
        ASSIGN, EQUALS, NOT_EQUALS, LESS, GREATER, 
        LESS_EQUAL, GREATER_EQUAL,
        
        // Delimiters
        LPAREN, RPAREN, LBRACE, RBRACE,
        SEMICOLON, COMMA,
        
        // Special
        EOF, NEWLINE
    }
    
    private TokenType type;
    private String value;
    private int line;
    
    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLine() {
        return line;
    }
    
    @Override
    public String toString() {
        return "Token{" + type + ", '" + value + "', line=" + line + "}";
    }
}
