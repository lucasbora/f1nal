package repository;

import exceptions.MyException;
import state.PrgState;
import java.util.List;

public interface IRepository {
    //PrgState getCurrentPrg(); we will have more programs running in parallel
    List<PrgState> getPrgList();
    void setPrgList(List<PrgState> prgList);
    void addPrg(PrgState prg);
    void logPrgStateExec(PrgState prg) throws MyException;
}
