package ast.instruction;

import ast.expression.Expression;
import runtime.Environment;

import java.util.List;

public class IfInstruction implements Instruction {

    private final Expression condition;
    private final List<Instruction> body;

    public IfInstruction(Expression condition, List<Instruction> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void execute(Environment env) {
        Object result = condition.evaluate(env);

        if (result instanceof Boolean && (Boolean) result) {
            for (Instruction instr : body) {
                instr.execute(env);
            }
        }
    }
}