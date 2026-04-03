package ast.expression;
import runtime.Environment;

public interface Expression {
    Object evaluate(Environment env);
}