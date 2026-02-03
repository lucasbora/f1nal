package model.exp;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.BoolType;
import model.type.IType;
import model.value.BoolValue;
import model.value.IValue;
import utils.IDict;
import utils.IHeap;

public class NotExp implements IExp {
    private IExp exp;

    public NotExp(IExp exp) {
        this.exp = exp;
    }

    @Override
    public IValue eval(IDict<String, IValue> symTable, IHeap<Integer, IValue> heap) throws MyException, ExpressionException {
        IValue val = exp.eval(symTable, heap);
        if (!val.getType().equals(new BoolType())) {
            throw new MyException("NotExp: Expression is not boolean!");
        }
        return new BoolValue(!((BoolValue) val).getVal());
    }

    @Override
    public IType typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        if (!exp.typecheck(typeEnv).equals(new BoolType())) {
            throw new MyException("NotExp: Operand is not boolean!");
        }
        return new BoolType();
    }

    @Override
    public IExp deepCopy() { return new NotExp(exp.deepCopy()); }

    @Override
    public String toString() { return "!(" + exp + ")"; }
}