package view.gui;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import model.statement.IStmt;
import model.value.IValue;
import state.PrgState;

import java.util.Map;

public class InterpreterController {

    @FXML private TextField nrPrgStates;

    @FXML private TableView<Map.Entry<Integer, IValue>> heapTable;
    @FXML private TableColumn<Map.Entry<Integer, IValue>, String> heapAddrCol;
    @FXML private TableColumn<Map.Entry<Integer, IValue>, String> heapValCol;

    @FXML private TableView<Map.Entry<String, IValue>> symTable;
    @FXML private TableColumn<Map.Entry<String, IValue>, String> symVarCol;
    @FXML private TableColumn<Map.Entry<String, IValue>, String> symValCol;

    @FXML private ListView<IValue> outList;
    @FXML private ListView<Integer> prgIdList;
    @FXML private ListView<IStmt> exeStackList;

    private ProgramStateService service;

    // JavaFX calls this AFTER injection
    @FXML
    public void initialize() {
        heapAddrCol.setCellValueFactory(
                e -> new SimpleStringProperty(e.getValue().getKey().toString()));
        heapValCol.setCellValueFactory(
                e -> new SimpleStringProperty(e.getValue().getValue().toString()));

        symVarCol.setCellValueFactory(
                e -> new SimpleStringProperty(e.getValue().getKey()));
        symValCol.setCellValueFactory(
                e -> new SimpleStringProperty(e.getValue().getValue().toString()));
    }

    public void setService(ProgramStateService service) {
        this.service = service;
        refresh(); // SAFE NOW
    }

    @FXML
    public void handleRunOneStep() throws Exception {
        service.oneStep();
        refresh();
    }

    private void refresh() {
        //nrPrgStates.setText("" + service.getPrgStates().size());

        nrPrgStates.setText(
                String.valueOf(
                        Math.max(1, service.getPrgStates().size())
                )
        );

        PrgState prg = service.getCurrentPrg();
        if (prg == null) return;

        heapTable.setItems(FXCollections.observableArrayList(
                prg.getHeap().getContent().entrySet()));

        symTable.setItems(FXCollections.observableArrayList(
                prg.getSymTable().getContent().entrySet()));
        symTable.refresh();

        outList.setItems(FXCollections.observableArrayList(
                prg.getOutput().getList()));

        exeStackList.setItems(FXCollections.observableArrayList(
                prg.getExeStack().toList()));

        prgIdList.setItems(FXCollections.observableArrayList(
                service.getPrgStates().stream().map(PrgState::getId).toList()));
    }
}
