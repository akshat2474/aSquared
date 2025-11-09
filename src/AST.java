import java.util.List;


public class AST {
    // Base class for all AST nodes
    public static abstract class ASTNode {
        public abstract Object accept(Visitor visitor);
    }
    
    // Program node (root)
    public static class Program extends ASTNode {
        public List<ASTNode> statements;
        
        public Program(List<ASTNode> statements) {
            this.statements = statements;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitProgram(this);
        }
    }
    
    // Number literal
    public static class NumberLiteral extends ASTNode {
        public double value;
        
        public NumberLiteral(double value) {
            this.value = value;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitNumberLiteral(this);
        }
    }
    
    // String literal
    public static class StringLiteral extends ASTNode {
        public String value;
        
        public StringLiteral(String value) {
            this.value = value;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitStringLiteral(this);
        }
    }
    
    // Variable reference
    public static class Variable extends ASTNode {
        public String name;
        
        public Variable(String name) {
            this.name = name;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitVariable(this);
        }
    }
    
    // Binary operation
    public static class BinaryOp extends ASTNode {
        public ASTNode left;
        public Token.TokenType operator;
        public ASTNode right;
        
        public BinaryOp(ASTNode left, Token.TokenType operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitBinaryOp(this);
        }
    }
    
    // Unary operation
    public static class UnaryOp extends ASTNode {
        public Token.TokenType operator;
        public ASTNode operand;
        
        public UnaryOp(Token.TokenType operator, ASTNode operand) {
            this.operator = operator;
            this.operand = operand;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitUnaryOp(this);
        }
    }
    
    // Assignment statement
    public static class Assignment extends ASTNode {
        public String name;
        public ASTNode value;
        
        public Assignment(String name, ASTNode value) {
            this.name = name;
            this.value = value;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitAssignment(this);
        }
    }
    
    // Print statement
    public static class PrintStatement extends ASTNode {
        public ASTNode expression;
        
        public PrintStatement(ASTNode expression) {
            this.expression = expression;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitPrintStatement(this);
        }
    }
    
    // If statement
    public static class IfStatement extends ASTNode {
        public ASTNode condition;
        public List<ASTNode> thenBlock;
        public List<ASTNode> elseBlock;
        
        public IfStatement(ASTNode condition, List<ASTNode> thenBlock, List<ASTNode> elseBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
            this.elseBlock = elseBlock;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitIfStatement(this);
        }
    }
    
    // While loop
    public static class WhileLoop extends ASTNode {
        public ASTNode condition;
        public List<ASTNode> body;
        
        public WhileLoop(ASTNode condition, List<ASTNode> body) {
            this.condition = condition;
            this.body = body;
        }
        
        @Override
        public Object accept(Visitor visitor) {
            return visitor.visitWhileLoop(this);
        }
    }
    
    // Visitor interface for traversing the AST
    public interface Visitor {
        Object visitProgram(Program node);
        Object visitNumberLiteral(NumberLiteral node);
        Object visitStringLiteral(StringLiteral node);
        Object visitVariable(Variable node);
        Object visitBinaryOp(BinaryOp node);
        Object visitUnaryOp(UnaryOp node);
        Object visitAssignment(Assignment node);
        Object visitPrintStatement(PrintStatement node);
        Object visitIfStatement(IfStatement node);
        Object visitWhileLoop(WhileLoop node);
    }
}

