package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import state.PrgState;
import model.exp.IExp;
import model.value.IValue;
import utils.IDict;

public class PrintStmt implements IStmt {
    private IExp exp;

    public PrintStmt(IExp exp) {
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState prg) throws MyException {
        var heap = prg.getHeap();
        IValue val;
        try {
            val = this.exp.eval(prg.getSymTable(), heap);
        } catch (ExpressionException | MyException e) {
            throw new MyException(e.getMessage());
        }
        prg.getOutput().add(val);
        return null;
    }

    @Override
    public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        exp.typecheck(typeEnv);
        return typeEnv;
    }


    @Override
    public String toString() {
        return "print(" + this.exp.toString() + ")";
    }

    @Override
    public IStmt deepCopy() {
        return new PrintStmt(this.exp.deepCopy());
    }
}