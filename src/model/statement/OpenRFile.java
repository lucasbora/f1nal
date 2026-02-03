package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import model.exp.IExp;
import model.type.IType;
import model.type.StringType;
import model.value.*;
import state.PrgState;
import exceptions.MyException;
import utils.IDict;

import java.io.*;

public class OpenRFile implements IStmt {
    private IExp exp;

    public OpenRFile(IExp e) {
        this.exp = e;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException, ExpressionException {
        var heap = state.getHeap();
        IValue val = exp.eval(state.getSymTable(), heap);
        if (!(val.getType() instanceof StringType))
            throw new MyException("OpenRFile: expression not string type");

        StringValue filename = (StringValue) val;
        if (state.getFileTable().isDefined(filename))
            throw new MyException("File already open: " + filename.getVal());

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename.getVal()));
            state.getFileTable().put(filename, br);
        } catch (IOException e) {
            throw new MyException("Cannot open file: " + filename.getVal());
        }

        return null;
    }

    @Override
    public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, ExpressionException, DictionaryException {
        IType t = exp.typecheck(typeEnv);

        if (t.equals(new StringType()))
            return typeEnv;
        else
            throw new MyException("openRFile argument is not string");
    }


    @Override
    public IStmt deepCopy() {
        return null;
    }

    @Override
    public String toString() {
        return "openRFile(" + exp + ")";
    }
}
