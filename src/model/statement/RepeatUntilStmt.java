package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.exp.IExp;
import model.exp.NotExp;
import model.type.BoolType;
import model.type.IType;
import state.PrgState;
import utils.IDict;

public class RepeatUntilStmt implements IStmt {
    private IStmt stmt;
    private IExp exp;

    public RepeatUntilStmt(IStmt stmt, IExp exp) {
        this.stmt = stmt;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        // Semantic rule: stmt1; while(!exp2) stmt1
        IStmt converted = new CompStmt(
                stmt,
                new WhileStmt(new NotExp(exp), stmt)
        );
        state.getExeStack().push(converted);
        return null;
    }

    @Override
    public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        IType type = exp.typecheck(typeEnv);
        if (type.equals(new BoolType())) {
            stmt.typecheck(typeEnv.deepCopy());
            return typeEnv;
        } else {
            throw new MyException("RepeatUntil: Condition is not boolean");
        }
    }

    @Override
    public IStmt deepCopy() {
        return new RepeatUntilStmt(stmt.deepCopy(), exp.deepCopy());
    }

    @Override
    public String toString() {
        return "repeat (" + stmt + ") until (" + exp + ")";
    }
}