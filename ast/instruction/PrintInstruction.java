package ast.instruction;

import ast.expression.Expression;
import runtime.Environment;

public class PrintInstruction implements Instruction {

    private final Expression expression;

    public PrintInstruction(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(Environment env) {
        Object value = expression.evaluate(env);
        System.out.println(value);
    }
}