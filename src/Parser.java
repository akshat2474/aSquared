import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int position;
    private Token currentToken;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.currentToken = tokens.get(0);
    }
    
    private void advance() {
        position++;
        if (position < tokens.size()) {
            currentToken = tokens.get(position);
        }
    }
    
    private void skipNewlines() {
        while (currentToken.getType() == Token.TokenType.NEWLINE) {
            advance();
        }
    }
    
    private void expect(Token.TokenType type) {
        if (currentToken.getType() != type) {
            throw new RuntimeException("Expected " + type + " but got " + currentToken.getType() + 
                                     " at line " + currentToken.getLine());
        }
        advance();
    }
    
    public AST.Program parse() {
        List<AST.ASTNode> statements = new ArrayList<>();
        
        skipNewlines();
        
        while (currentToken.getType() != Token.TokenType.EOF) {
            statements.add(statement());
            skipNewlines();
        }
        
        return new AST.Program(statements);
    }
    
    private AST.ASTNode statement() {
        skipNewlines();
        
        switch (currentToken.getType()) {
            case LET:
                AST.ASTNode assignment = assignment();
                expect(Token.TokenType.SEMICOLON);
                return assignment;
            case PRINT:
                AST.ASTNode print = printStatement();
                expect(Token.TokenType.SEMICOLON);
                return print;
            case IF:
                return ifStatement();
            case WHILE:
                return whileLoop();
            default:
                throw new RuntimeException("Unexpected token: " + currentToken.getType() + 
                                         " at line " + currentToken.getLine());
        }
    }
    
    private AST.Assignment assignment() {
        expect(Token.TokenType.LET);
        
        if (currentToken.getType() != Token.TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected identifier at line " + currentToken.getLine());
        }
        
        String name = currentToken.getValue();
        advance();
        
        expect(Token.TokenType.ASSIGN);
        
        AST.ASTNode value = expression();
        
        return new AST.Assignment(name, value);
    }
    
    private AST.PrintStatement printStatement() {
        expect(Token.TokenType.PRINT);
        AST.ASTNode expr = expression();
        return new AST.PrintStatement(expr);
    }
    
    private AST.IfStatement ifStatement() {
        expect(Token.TokenType.IF);
        expect(Token.TokenType.LPAREN);
        AST.ASTNode condition = expression();
        expect(Token.TokenType.RPAREN);
        skipNewlines();
        
        expect(Token.TokenType.LBRACE);
        skipNewlines();
        
        List<AST.ASTNode> thenBlock = new ArrayList<>();
        
        while (currentToken.getType() != Token.TokenType.RBRACE &&
               currentToken.getType() != Token.TokenType.EOF) {
            thenBlock.add(statement());
            skipNewlines();
        }
        
        expect(Token.TokenType.RBRACE);
        skipNewlines();
        
        List<AST.ASTNode> elseBlock = new ArrayList<>();
        
        if (currentToken.getType() == Token.TokenType.ELSE) {
            advance();
            skipNewlines();
            
            expect(Token.TokenType.LBRACE);
            skipNewlines();
            
            while (currentToken.getType() != Token.TokenType.RBRACE &&
                   currentToken.getType() != Token.TokenType.EOF) {
                elseBlock.add(statement());
                skipNewlines();
            }
            
            expect(Token.TokenType.RBRACE);
        }
        
        return new AST.IfStatement(condition, thenBlock, elseBlock);
    }
    
    private AST.WhileLoop whileLoop() {
        expect(Token.TokenType.WHILE);
        expect(Token.TokenType.LPAREN);
        AST.ASTNode condition = expression();
        expect(Token.TokenType.RPAREN);
        skipNewlines();
        
        expect(Token.TokenType.LBRACE);
        skipNewlines();
        
        List<AST.ASTNode> body = new ArrayList<>();
        
        while (currentToken.getType() != Token.TokenType.RBRACE &&
               currentToken.getType() != Token.TokenType.EOF) {
            body.add(statement());
            skipNewlines();
        }
        
        expect(Token.TokenType.RBRACE);
        
        return new AST.WhileLoop(condition, body);
    }
    
    private AST.ASTNode expression() {
        return comparison();
    }
    
    private AST.ASTNode comparison() {
        AST.ASTNode left = additive();
        
        while (currentToken.getType() == Token.TokenType.EQUALS ||
               currentToken.getType() == Token.TokenType.NOT_EQUALS ||
               currentToken.getType() == Token.TokenType.LESS ||
               currentToken.getType() == Token.TokenType.GREATER ||
               currentToken.getType() == Token.TokenType.LESS_EQUAL ||
               currentToken.getType() == Token.TokenType.GREATER_EQUAL) {
            
            Token.TokenType operator = currentToken.getType();
            advance();
            AST.ASTNode right = additive();
            left = new AST.BinaryOp(left, operator, right);
        }
        
        return left;
    }
    
    private AST.ASTNode additive() {
        AST.ASTNode left = multiplicative();
        
        while (currentToken.getType() == Token.TokenType.PLUS ||
               currentToken.getType() == Token.TokenType.MINUS) {
            
            Token.TokenType operator = currentToken.getType();
            advance();
            AST.ASTNode right = multiplicative();
            left = new AST.BinaryOp(left, operator, right);
        }
        
        return left;
    }
    
    private AST.ASTNode multiplicative() {
        AST.ASTNode left = unary();
        
        while (currentToken.getType() == Token.TokenType.MULTIPLY ||
               currentToken.getType() == Token.TokenType.DIVIDE ||
               currentToken.getType() == Token.TokenType.MODULO) {
            
            Token.TokenType operator = currentToken.getType();
            advance();
            AST.ASTNode right = unary();
            left = new AST.BinaryOp(left, operator, right);
        }
        
        return left;
    }
    
    private AST.ASTNode unary() {
        if (currentToken.getType() == Token.TokenType.MINUS) {
            advance();
            return new AST.UnaryOp(Token.TokenType.MINUS, unary());
        }
        
        return primary();
    }
    
    private AST.ASTNode primary() {
        Token token = currentToken;
        
        switch (token.getType()) {
            case NUMBER:
                advance();
                return new AST.NumberLiteral(Double.parseDouble(token.getValue()));
                
            case STRING:
                advance();
                return new AST.StringLiteral(token.getValue());
                
            case IDENTIFIER:
                advance();
                return new AST.Variable(token.getValue());
                
            case LPAREN:
                advance();
                AST.ASTNode expr = expression();
                expect(Token.TokenType.RPAREN);
                return expr;
                
            default:
                throw new RuntimeException("Unexpected token: " + token.getType() + 
                                         " at line " + token.getLine());
        }
    }
}