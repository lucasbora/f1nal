package view;

import controller.Controller;
import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.statement.IStmt;
import model.type.IType;
import repository.Repository;
import state.PrgState;
import utils.*;
import model.statement.*;
import model.exp.*;
import model.type.*;
import model.value.*;
import repository.*;
import utils.*;

import javax.management.ValueExp;
import java.io.BufferedReader;

public class Main5 {
    public static void main(String[] args) {
        TextMenu menu = new TextMenu();
        menu.addCommand(new ExitCommand("0", "exit"));

        IStmt ex1 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v",
                                new ArithExp('+',
                                        new ConstantValue(new IntValue(2)),
                                        new ArithExp('*', new ConstantValue(new IntValue(3)), new ConstantValue(new IntValue(5)))
                                )
                        ),
                        new PrintStmt(new VariableExp("v"))
                )
        );
        // ex2: Ref int v; new(v,20); Ref Ref int a; new(a,v); Print(rH(rH(a))
// Expected output: 20
        IType refInt = new RefType(new IntType());
        IType refRefInt = new RefType(refInt);

        IStmt ex2 = new CompStmt(
                new VarDeclStmt("v", refInt),
                new CompStmt(
                        new NewStmt("v", new ConstantValue(new IntValue(20))),
                        new CompStmt(
                                new VarDeclStmt("a", refRefInt),
                                new CompStmt(
                                        new NewStmt("a", new VariableExp("v")),
                                        new PrintStmt(new ReadHeapExp(new ReadHeapExp(new VariableExp("a"))))
                                )
                        )
                )
        );
        // ex3: int v; v = true; Print(v)
        IStmt ex3 = new CompStmt(
                new VarDeclStmt("v", new IntType()),
                new CompStmt(
                        new AssignStmt("v", new ConstantValue(new BoolValue(true))), // Type error here
                        new PrintStmt(new VariableExp("v"))
                )
        );

        // ex4: if 10 then Print(1) else Print(0)
        IStmt ex4 = new IfStmt(
                new ConstantValue(new IntValue(10)), // Type error here: IntType instead of BoolType
                new PrintStmt(new ConstantValue(new IntValue(1))),
                new PrintStmt(new ConstantValue(new IntValue(0)))
        );

        // ex5: Ref int v; new(v, true); Print(v)
        IStmt ex5 = new CompStmt(
                new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(
                        new NewStmt("v", new ConstantValue(new BoolValue(true))), // Type error here: IntType != BoolType
                        new PrintStmt(new VariableExp("v"))
                )
        );
        // ex6: Ref int a; new(a, 0); int v; v = 10;
        // while (v != 0) { wH(a, rH(a) + v); v = v - 1; }
        // Print(rH(a));
        // ex7: fork example (A5 mandatory)
        IStmt ex7 = new CompStmt(
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

        IStmt ex6 = new CompStmt(
                new VarDeclStmt("a", new RefType(new IntType())), // Ref int a
                new CompStmt(
                        new NewStmt("a", new ConstantValue(new IntValue(0))), // new(a, 0)
                        new CompStmt(
                                new VarDeclStmt("v", new IntType()), // int v
                                new CompStmt(
                                        new AssignStmt("v", new ConstantValue(new IntValue(10))), // v = 10
                                        new CompStmt(
                                                new WhileStmt(
                                                        // Condition: v != 0
                                                        new RelationalExp("!=", new VariableExp("v"), new ConstantValue(new IntValue(0))),
                                                        // Body: { wH(a, rH(a) + v); v = v - 1; }
                                                        new CompStmt(
                                                                new WriteHeapStmt("a",
                                                                        new ArithExp('+', new ReadHeapExp(new VariableExp("a")), new VariableExp("v"))
                                                                ), // wH(a, rH(a) + v)
                                                                new AssignStmt("v",
                                                                        new ArithExp('-', new VariableExp("v"), new ConstantValue(new IntValue(1)))
                                                                ) // v = v - 1
                                                        )
                                                ),
                                                new PrintStmt(new ReadHeapExp(new VariableExp("a"))) // Print(rH(a))
                                        )
                                )
                        )
                )
        );

// Expected Output (Result of 10 + 9 + ... + 1): 55

        addExample(menu, "1", ex1, "log1.txt");
        addExample(menu, "2", ex2, "log2.txt");
        addExample(menu, "3", ex3, "log3.txt");
        addExample(menu, "4", ex4, "log4.txt");
        addExample(menu, "5", ex5, "log5.txt");
        addExample(menu, "6", ex6, "log6.txt");
        addExample(menu, "7", ex7, "log7.txt");
        menu.show();
    }

    private static void addExample(TextMenu menu, String key, IStmt stmt, String logFile) {
        try {
            IDict<String, IType> typeEnv = new MyDict<>();
            stmt.typecheck(typeEnv);   // A6 CHECK

            PrgState prg = new PrgState(
                    new MyStack<>(),
                    new MyDict<>(),
                    new MyList<>(),
                    new MyDict<>(),
                    new MyHeap<>(),
                    stmt
            );

            Controller ctrl = new Controller(new Repository(prg, logFile));
            menu.addCommand(new RunExample(key, stmt.toString(), ctrl));

        } catch (MyException e) {
            System.out.println("Typecheck error in example " + key + ": " + e.getMessage());
        } catch (DictionaryException | ExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
