package view.gui;

import controller.Controller;
import state.PrgState;

import java.util.List;

public class ProgramStateService {

    private final Controller controller;

    public ProgramStateService(Controller controller) {
        this.controller = controller;
    }

//    public void oneStep() throws Exception {
//        controller.oneStepForAllPrg(
//                controller.getRepo().getPrgList()
//        );
//    }
    public void oneStep() { // pt GUI
        try {
            controller.oneStepForAllPrg();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public List<PrgState> getPrgStates() {
        return controller.getRepo().getPrgList();
    }

    public PrgState getCurrentPrg() {
        return controller.getRepo().getPrgList().get(0);
    }
}
