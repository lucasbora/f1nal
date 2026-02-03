package model.exp;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import model.type.RefType;
import model.value.RefValue;
import model.value.IValue;
import utils.IDict;
import utils.IHeap;

public class ReadHeapExp implements IExp {
    private final IExp exp;

    public ReadHeapExp(IExp exp) {
        this.exp = exp;
    }

    @Override
    public IValue eval(IDict<String, IValue> symTable, IHeap<Integer, IValue> heap) throws MyException, ExpressionException {
        IValue value = exp.eval(symTable, heap);
        if (!(value instanceof RefValue))
            throw new MyException("ReadHeap expects RefValue");

        int addr = ((RefValue) value).getAddr();
        if (!heap.isDefined(addr))
            throw new MyException("Invalid heap address: " + addr);

        return heap.get(addr);
    }

    @Override
    public IExp deepCopy() {
        return null;
    }

    @Override
    public IType typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        IType t = exp.typecheck(typeEnv);

        if (t instanceof RefType)
            return ((RefType) t).getInner();
        else
            throw new MyException("rH argument is not a RefType");
    }


    @Override
    public String toString() { return "rH(" + exp + ")"; }
}
