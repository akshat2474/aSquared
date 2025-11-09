import java.util.HashMap;
import java.util.Map;

public class Interpreter implements AST.Visitor {
    private Map<String, Object> variables;
    
    public Interpreter() {
        this.variables = new HashMap<>();
    }
    
    public void interpret(AST.Program program) {
        program.accept(this);
    }
    
    @Override
    public Object visitProgram(AST.Program node) {
        Object result = null;
        for (AST.ASTNode statement : node.statements) {
            result = statement.accept(this);
        }
        return result;
    }
    
    @Override
    public Object visitNumberLiteral(AST.NumberLiteral node) {
        return node.value;
    }
    
    @Override
    public Object visitStringLiteral(AST.StringLiteral node) {
        return node.value;
    }
    
    @Override
    public Object visitVariable(AST.Variable node) {
        if (!variables.containsKey(node.name)) {
            throw new RuntimeException("Undefined variable: " + node.name);
        }
        return variables.get(node.name);
    }
    
    @Override
    public Object visitBinaryOp(AST.BinaryOp node) {
        Object leftVal = node.left.accept(this);
        Object rightVal = node.right.accept(this);
        
        // Arithmetic operations
        if (leftVal instanceof Double && rightVal instanceof Double) {
            double left = (Double) leftVal;
            double right = (Double) rightVal;
            
            switch (node.operator) {
                case PLUS:
                    return left + right;
                case MINUS:
                    return left - right;
                case MULTIPLY:
                    return left * right;
                case DIVIDE:
                    if (right == 0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return left / right;
                case MODULO:
                    return left % right;
                case EQUALS:
                    return left == right ? 1.0 : 0.0;
                case NOT_EQUALS:
                    return left != right ? 1.0 : 0.0;
                case LESS:
                    return left < right ? 1.0 : 0.0;
                case GREATER:
                    return left > right ? 1.0 : 0.0;
                case LESS_EQUAL:
                    return left <= right ? 1.0 : 0.0;
                case GREATER_EQUAL:
                    return left >= right ? 1.0 : 0.0;
            }
        }
        
        // String concatenation
        if (node.operator == Token.TokenType.PLUS) {
            return leftVal.toString() + rightVal.toString();
        }
        
        throw new RuntimeException("Invalid operation: " + node.operator);
    }
    
    @Override
    public Object visitUnaryOp(AST.UnaryOp node) {
        Object value = node.operand.accept(this);
        
        if (node.operator == Token.TokenType.MINUS && value instanceof Double) {
            return -(Double) value;
        }
        
        throw new RuntimeException("Invalid unary operation");
    }
    
    @Override
    public Object visitAssignment(AST.Assignment node) {
        Object value = node.value.accept(this);
        variables.put(node.name, value);
        return value;
    }
    
    @Override
    public Object visitPrintStatement(AST.PrintStatement node) {
        Object value = node.expression.accept(this);
        
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
        
        return null;
    }
    
    @Override
    public Object visitIfStatement(AST.IfStatement node) {
        Object conditionValue = node.condition.accept(this);
        
        boolean condition = false;
        if (conditionValue instanceof Double) {
            condition = (Double) conditionValue != 0.0;
        }
        
        if (condition) {
            for (AST.ASTNode statement : node.thenBlock) {
                statement.accept(this);
            }
        } else if (node.elseBlock != null) {
            for (AST.ASTNode statement : node.elseBlock) {
                statement.accept(this);
            }
        }
        
        return null;
    }
    
    @Override
    public Object visitWhileLoop(AST.WhileLoop node) {
        while (true) {
            Object conditionValue = node.condition.accept(this);
            
            boolean condition = false;
            if (conditionValue instanceof Double) {
                condition = (Double) conditionValue != 0.0;
            }
            
            if (!condition) {
                break;
            }
            
            for (AST.ASTNode statement : node.body) {
                statement.accept(this);
            }
        }
        
        return null;
    }
}

