import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private String input;
    private int position;
    private int line;
    private char currentChar;
    
    private static final Map<String, Token.TokenType> KEYWORDS = new HashMap<>();
    static {
        KEYWORDS.put("let", Token.TokenType.LET);
        KEYWORDS.put("print", Token.TokenType.PRINT);
        KEYWORDS.put("if", Token.TokenType.IF);
        KEYWORDS.put("else", Token.TokenType.ELSE);
    }
    
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }
    
    private void advance() {
        position++;
        if (position >= input.length()) {
            currentChar = '\0';
        } else {
            currentChar = input.charAt(position);
            if (currentChar == '\n') {
                line++;
            }
        }
    }

    private char peek() {
        int peekPos = position + 1;
        if (peekPos >= input.length()) {
            return '\0';
        }
        return input.charAt(peekPos);
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar) && currentChar != '\n') {
            advance();
        }
    }
    
    private void skipComment() {
        if (currentChar == '#') {
            while (currentChar != '\0' && currentChar != '\n') {
                advance();
            }
        }
    }
    
    private Token number() {
        StringBuilder result = new StringBuilder();
        int startLine = line;
        
        while (currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')) {
            result.append(currentChar);
            advance();
        }
        
        return new Token(Token.TokenType.NUMBER, result.toString(), startLine);
    }
    
    private Token identifier() {
        StringBuilder result = new StringBuilder();
        int startLine = line;
        
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        
        String value = result.toString();
        Token.TokenType type = KEYWORDS.getOrDefault(value, Token.TokenType.IDENTIFIER);
        
        return new Token(type, value, startLine);
    }
    
    private Token string() {
        StringBuilder result = new StringBuilder();
        int startLine = line;

        
        advance(); // Skip opening quote
        
        while (currentChar != '\0' && currentChar != '"') {
            if (currentChar == '\\' && peek() == '"') {
                advance();
                result.append('"');
                advance();
            } else {
                result.append(currentChar);
                advance();
            }
        }
        
        if (currentChar == '"') {
            advance(); // Skip closing quote
        }
        
        return new Token(Token.TokenType.STRING, result.toString(), startLine);
    }
    
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (currentChar != '\0') {
            if (currentChar == '#') {
                skipComment();
                continue;
            }
            
            if (Character.isWhitespace(currentChar) && currentChar != '\n') {
                skipWhitespace();
                continue;
            }
            
            if (currentChar == '\n') {
                tokens.add(new Token(Token.TokenType.NEWLINE, "\n", line));
                advance();
                continue;
            }
            
            if (Character.isDigit(currentChar)) {
                tokens.add(number());
                continue;
            }
            
            if (Character.isLetter(currentChar) || currentChar == '_') {
                tokens.add(identifier());
                continue;
            }
            
            if (currentChar == '"') {
                tokens.add(string());
                continue;
            }
            
            int currentLine = line;
            
            switch (currentChar) {
                case '+':
                    tokens.add(new Token(Token.TokenType.PLUS, "+", currentLine));
                    advance();
                    break;
                case '-':
                    tokens.add(new Token(Token.TokenType.MINUS, "-", currentLine));
                    advance();
                    break;
                case '*':
                    tokens.add(new Token(Token.TokenType.MULTIPLY, "*", currentLine));
                    advance();
                    break;
                case '/':
                    tokens.add(new Token(Token.TokenType.DIVIDE, "/", currentLine));
                    advance();
                    break;
                case '%':
                    tokens.add(new Token(Token.TokenType.MODULO, "%", currentLine));
                    advance();
                    break;
                case '(':
                    tokens.add(new Token(Token.TokenType.LPAREN, "(", currentLine));
                    advance();
                    break;
                case ')':
                    tokens.add(new Token(Token.TokenType.RPAREN, ")", currentLine));
                    advance();
                    break;
                case '{':
                    tokens.add(new Token(Token.TokenType.LBRACE, "{", currentLine));
                    advance();
                    break;
                case '}':
                    tokens.add(new Token(Token.TokenType.RBRACE, "}", currentLine));
                    advance();
                    break;
                case ';':
                    tokens.add(new Token(Token.TokenType.SEMICOLON, ";", currentLine));
                    advance();
                    break;
                case ',':
                    tokens.add(new Token(Token.TokenType.COMMA, ",", currentLine));
                    advance();
                    break;
                case '=':
                    if (peek() == '=') {
                        tokens.add(new Token(Token.TokenType.EQUALS, "==", currentLine));
                        advance();
                        advance();
                    } else {
                        tokens.add(new Token(Token.TokenType.ASSIGN, "=", currentLine));
                        advance();
                    }
                    break;
                case '!':
                    if (peek() == '=') {
                        tokens.add(new Token(Token.TokenType.NOT_EQUALS, "!=", currentLine));
                        advance();
                        advance();
                    } else {
                        throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
                    }
                    break;
                case '<':
                    if (peek() == '=') {
                        tokens.add(new Token(Token.TokenType.LESS_EQUAL, "<=", currentLine));
                        advance();
                        advance();
                    } else {
                        tokens.add(new Token(Token.TokenType.LESS, "<", currentLine));
                        advance();
                    }
                    break;
                case '>':
                    if (peek() == '=') {
                        tokens.add(new Token(Token.TokenType.GREATER_EQUAL, ">=", currentLine));
                        advance();
                        advance();
                    } else {
                        tokens.add(new Token(Token.TokenType.GREATER, ">", currentLine));
                        advance();
                    }
                    break;
                default:
                    throw new RuntimeException("Unexpected character: " + currentChar + " at line " + line);
            }
        }
        
        tokens.add(new Token(Token.TokenType.EOF, "", line));
        return tokens;
    }
}