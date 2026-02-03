package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import state.PrgState;
import utils.IDict;

public interface IStmt {
    PrgState execute(PrgState prg) throws MyException, ExpressionException, DictionaryException;

    IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException, DictionaryException, ExpressionException;

    IStmt deepCopy();
}