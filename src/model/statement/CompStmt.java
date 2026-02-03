package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import state.PrgState;
import utils.IDict;
import utils.IStack;

public class CompStmt implements IStmt {
    private IStmt first;
    private IStmt second;

    public CompStmt(IStmt first, IStmt second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public PrgState execute(PrgState prg) throws MyException {
        IStack<IStmt> stk = prg.getExeStack();

        stk.push(second);
        stk.push(first);

        return null;
    }

    @Override
    public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        return second.typecheck(first.typecheck(typeEnv));
    }


    @Override
    public String toString() {
        return "(" + first.toString() + ";" + second.toString() + ")";
    }

    @Override
    public IStmt deepCopy() {
        return new CompStmt(this.first.deepCopy(), this.second.deepCopy());
    }
}