package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import model.value.IValue;
import state.PrgState;
import utils.IDict;
import utils.IStack;
import utils.MyStack;

public class ForkStmt implements IStmt {
    private IStmt stmt;

    public ForkStmt(IStmt stmt) {
        this.stmt = stmt;
    }

    @Override
    public PrgState execute(PrgState state) {
        IStack<IStmt> newStack = new MyStack<>();
        //newStack.push(stmt);

        IDict<String, IValue> newSymTable = state.getSymTable().deepCopy();

        return new PrgState(
                newStack,
                newSymTable,
                state.getOutput(),     // shared
                state.getFileTable(),  // shared
                state.getHeap(),       // shared
                stmt
        );
    }


    @Override
    public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        stmt.typecheck(typeEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public IStmt deepCopy() {
        return new ForkStmt(stmt);
    }

    @Override
    public String toString() {
        return "fork(" + stmt + ")";
    }
}
