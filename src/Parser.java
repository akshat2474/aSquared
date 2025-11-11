import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private List<Token> tokens;
    private int position;
    private Token currentToken;
    private Map<String, Object> variables;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.currentToken = tokens.get(0);
        this.variables = new HashMap<>();
    }

    private void advance() {
        if (++position < tokens.size()) currentToken = tokens.get(position);
    }

    private void skipNewlines() {
        while (currentToken.getType() == Token.TokenType.NEWLINE) {
            advance();
        }
    }

    private void expect(Token.TokenType type) {
        if (currentToken.getType() != type) {
            throw new RuntimeException("Parse error: Expected " + type + " but got " + currentToken.getType());
        }
        advance();
    }

    public void parseAndExecute() {
        skipNewlines();
        while (currentToken.getType() != Token.TokenType.EOF) {
            statement();
            skipNewlines();
        }
    }

    private void statement() {
        skipNewlines();
        switch (currentToken.getType()) {
            case LET:
                expect(Token.TokenType.LET);
                String name = currentToken.getValue();
                advance();
                expect(Token.TokenType.ASSIGN);
                variables.put(name, expression());
                expect(Token.TokenType.SEMICOLON);
                break;
            case PRINT:
                expect(Token.TokenType.PRINT);
                Object value = expression();
                if (value instanceof Double) {
                    double d = (Double) value;
                    if (d == Math.floor(d)) {
                        System.out.println((int) d);
                    } else {
                        System.out.println(d);
                    }
                } else {
                    System.out.println(value);
                }
                expect(Token.TokenType.SEMICOLON);
                break;
            case IF:
                expect(Token.TokenType.IF);
                expect(Token.TokenType.LPAREN);
                boolean condition = isTrue(expression());
                expect(Token.TokenType.RPAREN);
                skipNewlines();
                expect(Token.TokenType.LBRACE);
                skipNewlines();

                if (condition) {
                    while (currentToken.getType() != Token.TokenType.RBRACE) {
                        statement();
                        skipNewlines();
                    }
                    expect(Token.TokenType.RBRACE);
                    skipNewlines();
                    if (currentToken.getType() == Token.TokenType.ELSE) {
                        advance();
                        skipBlock();
                    }
                } else {
                    skipTo(Token.TokenType.RBRACE);
                    expect(Token.TokenType.RBRACE);
                    skipNewlines();
                    if (currentToken.getType() == Token.TokenType.ELSE) {
                        advance();
                        expect(Token.TokenType.LBRACE);
                        skipNewlines();
                        while (currentToken.getType() != Token.TokenType.RBRACE) {
                            statement();
                            skipNewlines();
                        }
                        expect(Token.TokenType.RBRACE);
                    }
                }
                break;
            default:
                throw new RuntimeException("Unexpected token: " + currentToken.getType() + 
                                         " at line " + currentToken.getLine());
        }
    }

    private boolean isTrue(Object v) {
        return v instanceof Double && (Double) v != 0.0;
    }

    private void skipTo(Token.TokenType type) {
        int depth = 1;
        while (depth > 0) {
            if (currentToken.getType() == Token.TokenType.LBRACE) depth++;
            else if (currentToken.getType() == Token.TokenType.RBRACE) depth--;
            if (depth > 0) advance();
        }
    }

    private void skipBlock() {
        expect(Token.TokenType.LBRACE);
        int depth = 1;
        while (depth > 0) {
            if (currentToken.getType() == Token.TokenType.LBRACE) depth++;
            else if (currentToken.getType() == Token.TokenType.RBRACE) depth--;
            advance();
        }
    }

    private Object expression() {
        Object left = term();
        while (currentToken.getType() == Token.TokenType.PLUS ||
               currentToken.getType() == Token.TokenType.MINUS ||
               currentToken.getType() == Token.TokenType.EQUALS ||
               currentToken.getType() == Token.TokenType.NOT_EQUALS ||
               currentToken.getType() == Token.TokenType.LESS ||
               currentToken.getType() == Token.TokenType.LESS_EQUAL ||
               currentToken.getType() == Token.TokenType.GREATER ||
               currentToken.getType() == Token.TokenType.GREATER_EQUAL) {
            Token.TokenType op = currentToken.getType();
            advance();
            left = eval(left, op, term());
        }
        return left;
    }

    private Object term() {
        Object left = factor();
        while (currentToken.getType() == Token.TokenType.MULTIPLY ||
               currentToken.getType() == Token.TokenType.DIVIDE ||
               currentToken.getType() == Token.TokenType.MODULO) {
            Token.TokenType op = currentToken.getType();
            advance();
            left = eval(left, op, factor());
        }
        return left;
    }

    private Object factor() {
        Token token = currentToken;
        if (token.getType() == Token.TokenType.NUMBER) {
            advance();
            return Double.parseDouble(token.getValue());
        } else if (token.getType() == Token.TokenType.STRING) {
            advance();
            return token.getValue();
        } else if (token.getType() == Token.TokenType.IDENTIFIER) {
            advance();
            Object val = variables.get(token.getValue());
            if (val == null) {
                throw new RuntimeException("Undefined variable: " + token.getValue());
            }
            return val;
        } else if (token.getType() == Token.TokenType.LPAREN) {
            advance();
            Object result = expression();
            expect(Token.TokenType.RPAREN);
            return result;
        }
        throw new RuntimeException("Parse error at token: " + token.getType());
    }

    private Object eval(Object l, Token.TokenType op, Object r) {
        if (l instanceof Double && r instanceof Double) {
            double left = (Double) l, right = (Double) r;
            switch (op) {
                case PLUS: return left + right;
                case MINUS: return left - right;
                case MULTIPLY: return left * right;
                case DIVIDE:
                    if (right == 0) throw new RuntimeException("Division by zero");
                    return left / right;
                case MODULO: return left % right;
                case EQUALS: return left == right ? 1.0 : 0.0;
                case NOT_EQUALS: return left != right ? 1.0 : 0.0;
                case LESS: return left < right ? 1.0 : 0.0;
                case LESS_EQUAL: return left <= right ? 1.0 : 0.0;
                case GREATER: return left > right ? 1.0 : 0.0;
                case GREATER_EQUAL: return left >= right ? 1.0 : 0.0;
            }
        }

        // String concatenation with + operator
        if (op == Token.TokenType.PLUS) {
            return l.toString() + r.toString();
        }

        // String comparison
        if (l instanceof String && r instanceof String) {
            String left = (String) l;
            String right = (String) r;
            switch (op) {
                case EQUALS: return left.equals(right) ? 1.0 : 0.0;
                case NOT_EQUALS: return !left.equals(right) ? 1.0 : 0.0;
            }
        }

        throw new RuntimeException("Type error");
    }
}
