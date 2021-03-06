package jlox;

import java.util.List;

import static jlox.TokenType.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    public Object interpret(Expr expression) {
        Object object = null;

        try {
            object = expression.accept(this);
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }

        return object;
    }

    public Void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement: statements) {
                statement.accept(this);
            }
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }

        return null;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        switch (expr.operator.type) {
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left > (Double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left >= (Double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left < (Double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left <= (Double) right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left - (Double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left * (Double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (Double) left / (Double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }

                checkPlusOperands(expr.operator, left, right);
        }

        return null;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object object = expr.accept(this);

        if (expr.operator.type == BANG) {
            return isTruthy(object);
        } else if (expr.operator.type == MINUS) {
            checkNumberOperand(expr.operator, object);
            return -(double) object;
        }

        return object;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Void visitExpression(Stmt.Expression expression) {
        expression.expression.accept(this);
        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print printStatement) {
        Object object = printStatement.expression.accept(this);
        System.out.println(stringify(object));
        return null;
    }

    private String stringify(Object object) {
        if (object == null) return "nil";
        return object.toString();
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number. Found: " + operandName(operand));
    }

    private void checkNumberOperands(Token operator, Object operand1, Object operand2) {
        if (operand1 instanceof Double && operand2 instanceof Double) return;

        throw new RuntimeError(operator, "Operands must be a number. Found: " + operandName(operand1) + " and " + operandName(operand2));
    }

    private void checkPlusOperands(Token operator, Object operand1, Object operand2) {
        throw new RuntimeError(operator, "Operand must be both numbers or both string. Found: " + operandName(operand1) + " and " + operandName(operand2));
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;

        return true;
    }

    private boolean isEqual(Object object1, Object object2) {
        if (object1 == null && object2 == null) return true;
        if (object1 == null) return false;
        return object1.equals(object2);
    }

    private String operandName(Object operand) {
        if (operand instanceof Double) return "number";
        if (operand instanceof String) return "string";
        return "unknown";
    }
}
