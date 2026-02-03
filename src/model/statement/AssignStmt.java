package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import state.PrgState;
import model.exp.IExp;
import model.value.IValue;
import utils.IDict;
import utils.IHeap;

public class AssignStmt implements IStmt {
    private String id;
    private IExp exp;

    public AssignStmt(String id, IExp exp) {
        this.id = id;
        this.exp = exp;
    }

    @Override
    public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, DictionaryException, ExpressionException {
        IType tVar = typeEnv.get(id);
        IType tExp = exp.typecheck(typeEnv);

        if (tVar.equals(tExp))
            return typeEnv;
        else
            throw new MyException("Assignment: types do not match");
    }

    @Override
    public PrgState execute(PrgState prg) throws MyException {
        IDict<String, IValue> symTable = prg.getSymTable();
        IHeap<Integer, IValue> heap = prg.getHeap();
        if (symTable.isDefined(id)) {
            IValue val;
            try {
                val = this.exp.eval(symTable, heap);
            } catch (ExpressionException | MyException e) {
                throw new MyException(e.getMessage());
            }
            try {
                if (val.getType().equals(symTable.get(id).getType())) {
                    symTable.put(id, val);
                } else {
                    throw new MyException(
                            "Declared type of variable " + id + " and type of the assigned expression do not match");
                }
            } catch (DictionaryException | MyException e) {
                throw new MyException(e.getMessage());
            }
        } else {
            throw new MyException("The used variable " + id + " was not declared before");
        }
        return null;
    }

    @Override
    public String toString() {
        return id + " = " + exp.toString();
    }

    @Override
    public IStmt deepCopy() {
        return new AssignStmt(this.id, this.exp.deepCopy());
    }

}