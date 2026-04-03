package ast.expression;
import runtime.Environment;

public class BinaryOpNode implements Expression {

    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryOpNode(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object evaluate(Environment env) {
        Object l = left.evaluate(env);
        Object r = right.evaluate(env);

        if (l instanceof Double && r instanceof Double) {
            double a = (Double) l;
            double b = (Double) r;

            return switch (operator) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> a / b;
                case ">" -> a > b;
                case "<" -> a < b;
                case "==" -> a == b;
                default -> throw new RuntimeException("Unknown operator: " + operator);
            };
        }

        throw new RuntimeException("Invalid operands for operator: " + operator);
    }
}