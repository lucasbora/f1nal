//package view.gui;
//
//import controller.Controller;
//import javafx.collections.FXCollections;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.control.ListView;
//import javafx.stage.Stage;
//import model.exp.ConstantValue;
//import model.exp.ReadHeapExp;
//import model.exp.VariableExp;
//import model.statement.*;
//import model.type.IntType;
//import model.type.RefType;
//import model.value.IntValue;
//import repository.Repository;
//import state.PrgState;
//import utils.*;
//
//import java.util.List;
//
//public class ProgramChooserController {
//
//    @FXML
//    private ListView<IStmt> programList;
//
//    @FXML
//    public void initialize() {
//        programList.setItems(FXCollections.observableArrayList(getPrograms()));
//    }
//
//    private List<IStmt> getPrograms() {
//        // ADD YOUR A6 EXAMPLES HERE
//        IStmt exFork = new CompStmt(
//                new VarDeclStmt("v", new IntType()),
//                new CompStmt(
//                        new VarDeclStmt("a", new RefType(new IntType())),
//                        new CompStmt(
//                                new AssignStmt("v", new ConstantValue(new IntValue(10))),
//                                new CompStmt(
//                                        new NewStmt("a", new ConstantValue(new IntValue(22))),
//                                        new CompStmt(
//                                                new ForkStmt(
//                                                        new CompStmt(
//                                                                new WriteHeapStmt("a", new ConstantValue(new IntValue(30))),
//                                                                new CompStmt(
//                                                                        new AssignStmt("v", new ConstantValue(new IntValue(32))),
//                                                                        new CompStmt(
//                                                                                new PrintStmt(new VariableExp("v")),
//                                                                                new PrintStmt(
//                                                                                        new ReadHeapExp(new VariableExp("a"))
//                                                                                )
//                                                                        )
//                                                                )
//                                                        )
//                                                ),
//                                                new CompStmt(
//                                                        new PrintStmt(new VariableExp("v")),
//                                                        new PrintStmt(
//                                                                new ReadHeapExp(new VariableExp("a"))
//                                                        )
//                                                )
//                                        )
//                                )
//                        )
//                )
//        );
//        //IStmt ex1 = ExamplePrograms.simpleExample();
//
//        return List.of(exFork);
//    }
//
//    @FXML
//    private void handleSelect() throws Exception {
//        IStmt selected = programList.getSelectionModel().getSelectedItem();
//        if (selected == null) return;
//
//        PrgState prg = new PrgState(
//                new MyStack<>(),
//                new MyDict<>(),
//                new MyList<>(),
//                new MyDict<>(),
//                new MyHeap<>(),
//                selected
//        );
//
//        Repository repo = new Repository(prg, "log.txt");
//        Controller ctrl = new Controller(repo);
//
//        ProgramStateService service = new ProgramStateService(ctrl);
//
//        FXMLLoader loader =
//                new FXMLLoader(getClass().getResource("/interpreter.fxml"));
//        Scene scene = new Scene(loader.load(), 900, 600);
//
//        InterpreterController ic = loader.getController();
//        ic.setService(service);
//
//        Stage stage = new Stage();
//        stage.setTitle("Interpreter");
//        stage.setScene(scene);
//        stage.show();
//    }
//}
package view.gui;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import model.exp.*;
import model.statement.*;
import model.type.BoolType;
import model.type.IntType;
import model.type.RefType;
import model.type.StringType;
import model.value.BoolValue;
import model.value.IntValue;
import model.value.StringValue;
import repository.Repository;
import state.PrgState;
import utils.*;

import java.util.List;

public class ProgramChooserController {

    @FXML
    private ListView<IStmt> programList;

    @FXML
    public void initialize() {
        programList.setItems(FXCollections.observableArrayList(getPrograms()));
        // Allow selecting only one program at a time
        programList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private List<IStmt> getPrograms() {
        // Example 1: int v; v=2; print(v)
        IStmt ex1 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ConstantValue(new IntValue(2))),
                        new PrintStmt(new VariableExp("v"))
                )
        );

        // Example 2: int a; int b; a=2+3*5; b=a+1; print(b)
        IStmt ex2 = new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ArithExp('+', new ConstantValue(new IntValue(2)),
                                        new ArithExp('*', new ConstantValue(new IntValue(3)), new ConstantValue(new IntValue(5))))),
                                new CompStmt(
                                        new AssignStmt("b", new ArithExp('+', new VariableExp("a"), new ConstantValue(new IntValue(1)))),
                                        new PrintStmt(new VariableExp("b"))
                                )
                        )
                )
        );

        // Example 3: bool a; int v; a=true; (IF a THEN v=2 ELSE v=3); print(v)
        IStmt ex3 = new CompStmt(
                new VarDeclStmt("a", new BoolType()),
                new CompStmt(
                        new VarDeclStmt("v", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ConstantValue(new BoolValue(true))),
                                new CompStmt(
                                        new IfStmt(
                                                new VariableExp("a"),
                                                new AssignStmt("v", new ConstantValue(new IntValue(2))),
                                                new AssignStmt("v", new ConstantValue(new IntValue(3)))
                                        ),
                                        new PrintStmt(new VariableExp("v"))
                                )
                        )
                )
        );

        // Example 4: string varf; varf="test.in"; openRFile(varf); int varc; readFile(varf,varc); print(varc); readFile(varf,varc); print(varc); closeRFile(varf)
        IStmt ex4 = new CompStmt(
                new VarDeclStmt("varf", new StringType()),
                new CompStmt(
                        new AssignStmt("varf", new ConstantValue(new StringValue("test.in"))),
                        new CompStmt(
                                new OpenRFile(new VariableExp("varf")),
                                new CompStmt(
                                        new VarDeclStmt("varc", new IntType()),
                                        new CompStmt(
                                                new ReadFile(new VariableExp("varf"), "varc"),
                                                new CompStmt(
                                                        new PrintStmt(new VariableExp("varc")),
                                                        new CompStmt(
                                                                new ReadFile(new VariableExp("varf"), "varc"),
                                                                new CompStmt(
                                                                        new PrintStmt(new VariableExp("varc")),
                                                                        new CloseRFile(new VariableExp("varf"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        // Example 5: Ref int v; new(v,20); Ref Ref int a; new(a,v); print(rH(v)); print(rH(rH(a)))
        IStmt ex5 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ConstantValue(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(
                                        new NewStmt("a", new VariableExp("v")),
                                        new CompStmt(
                                                new PrintStmt(new ReadHeapExp(new VariableExp("v"))),
                                                new PrintStmt(
                                                        new ReadHeapExp(new ReadHeapExp(new VariableExp("a")))
                                                )
                                        )
                                )
                        )
                )
        );

        // Example 6: int v; v=4; (while (v>0) print(v); v=v-1); print(v)
        IStmt ex6 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ConstantValue(new IntValue(4))),
                        new CompStmt(
                                new WhileStmt(
                                        new RelationalExp(">", new VariableExp("v"), new ConstantValue(new IntValue(0))),
                                        new CompStmt(
                                                new PrintStmt(new VariableExp("v")),
                                                new AssignStmt("v", new ArithExp('-', new VariableExp("v"), new ConstantValue(new IntValue(1))))
                                        )
                                ),
                                new PrintStmt(new VariableExp("v"))
                        )
                )
        );

        // Example 7 (The Fork Example): Ref int a; int v; ....
        IStmt exFork = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new VarDeclStmt("a", new RefType(new IntType())),
                        new CompStmt(
                                new AssignStmt("v", new ConstantValue(new IntValue(10))),
                                new CompStmt(
                                        new NewStmt("a", new ConstantValue(new IntValue(22))),
                                        new CompStmt(
                                                new ForkStmt(
                                                        new CompStmt(
                                                                new WriteHeapStmt("a", new ConstantValue(new IntValue(30))),
                                                                new CompStmt(
                                                                        new AssignStmt("v", new ConstantValue(new IntValue(32))),
                                                                        new CompStmt(
                                                                                new PrintStmt(new VariableExp("v")),
                                                                                new PrintStmt(
                                                                                        new ReadHeapExp(new VariableExp("a"))
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompStmt(
                                                        new PrintStmt(new VariableExp("v")),
                                                        new PrintStmt(
                                                                new ReadHeapExp(new VariableExp("a"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

        return List.of(ex1, ex2, ex3, ex4, ex5, ex6, exFork);
    }

    @FXML
    private void handleSelect() {
        IStmt selected = programList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error encountered!");
            alert.setContentText("No program selected!");
            alert.showAndWait();
            return;
        }

        try {
            // Create the backend structure
            PrgState prg = new PrgState(
                    new MyStack<>(),
                    new MyDict<>(),
                    new MyList<>(),
                    new MyDict<>(),
                    new MyHeap<>(),
                    selected
            );

            Repository repo = new Repository(prg, "log.txt");
            Controller ctrl = new Controller(repo);

            // Your custom wrapper service
            ProgramStateService service = new ProgramStateService(ctrl);

            // Load the Interpreter window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interpreter.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            InterpreterController ic = loader.getController();
            ic.setService(service);

            Stage stage = new Stage();
            stage.setTitle("Interpreter");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not launch interpreter: " + e.getMessage());
            alert.showAndWait();
        }
    }
}