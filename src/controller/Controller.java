package controller;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import model.value.IValue;
import model.value.RefValue;
import repository.IRepository;
import utils.IStack;
import exceptions.MyException;
import exceptions.StackException;
import state.PrgState;
import model.statement.IStmt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller {
    private IRepository repo;
    private boolean displayFlag;
    private ExecutorService executor;
    public Controller(IRepository repo) {
        this.repo = repo;
        this.displayFlag = true;
    }
    public Controller(IRepository repo, boolean displayFlag) {
        this.repo = repo;
        this.displayFlag = displayFlag;
    }
    private List<PrgState> removeCompletedPrg(List<PrgState> prgList) {
        return prgList.stream()
                .filter(PrgState::isNotCompleted)
                .collect(Collectors.toList());
    }

    private Map<Integer, IValue> unsafeGarbageCollector(List<Integer> symTableAddr, Map<Integer, IValue> heap) {
        return heap.entrySet().stream()
                .filter(e -> symTableAddr.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<Integer> getAddrFromValues(Collection<IValue> symTableValues) {
        return symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddr())
                .collect(Collectors.toList());
    }

//    public PrgState oneStep(PrgState prg) throws MyException, ExpressionException, DictionaryException {
//        IStack<IStmt> stk = prg.getExeStack();
//        if (stk.isEmpty()) {
//            throw new MyException("Empty execution stack");
//        }
//
//        IStmt currentStmt;
//        try {
//            currentStmt = stk.pop();
//        } catch (StackException e) {
//            throw new MyException(e.getMessage());
//        }
//        return currentStmt.execute(prg);
//    }

//    public void oneStepForAllPrg(List<PrgState> prgList) throws MyException {
//        prgList.forEach(prg -> {
//            try {
//                repo.logPrgStateExec(prg);
//            } catch (MyException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        List<Callable<PrgState>> callList = prgList.stream()
//                .map(prg -> (Callable<PrgState>) prg::oneStep)
//                .toList();
//
//        try {
//            List<PrgState> newPrgList = executor.invokeAll(callList).stream()
//                    .map(f -> {
//                        try { return f.get(); }
//                        catch (Exception e) { return null; }
//                    })
//                    .filter(Objects::nonNull)
//                    .toList();
//
//            prgList.addAll(newPrgList);
//            repo.setPrgList(prgList);
//
//        } catch (InterruptedException e) {
//            throw new MyException("Execution error");
//        }
//    }


    public void oneStepForAllPrg() throws InterruptedException { // pt GUI
        //executor = Executors.newFixedThreadPool(2);

        if (executor == null || executor.isShutdown()) {
            executor = Executors.newFixedThreadPool(2);
        }
        List<PrgState> prgList = removeCompletedPrg(repo.getPrgList());

        List<Callable<PrgState>> callList = prgList.stream()
                .map((PrgState p) -> (Callable<PrgState>) p::oneStep)
                .toList();

        List<PrgState> newPrgList = executor.invokeAll(callList).stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(p -> p != null)
                .toList();

        prgList.addAll(newPrgList);
        repo.setPrgList(prgList);

        Map<Integer, IValue> heap = prgList.get(0).getHeap().getContent();

        List<Integer> usedAddresses = getAllReachableAddresses(prgList);

        prgList.get(0).getHeap().setContent(
                unsafeGarbageCollector(usedAddresses, heap)
        );

        executor.shutdownNow();
    }


    private List<Integer> getAllReachableAddresses(List<PrgState> prgList) {
        return prgList.stream()
                .flatMap(prg ->
                        getReachableAddresses(
                                prg.getSymTable().getContent().values(),
                                prg.getHeap().getContent()
                        ).stream()
                )
                .distinct()
                .collect(Collectors.toList());
    }

    // CHANGE 2: Add the iterative garbage collection logic
    private List<Integer> getReachableAddresses(Collection<IValue> symTableValues, Map<Integer, IValue> heap) {
        // 1. Start with addresses directly from the root set (Symbol Table)
        Collection<Integer> reachable = new java.util.HashSet<>(getAddrFromValues(symTableValues));

        int sizeBefore = 0;

        // Iterate until no new addresses are found (the set size stops growing)
        while (sizeBefore != reachable.size()) {
            sizeBefore = reachable.size();

            // Collect all IValue objects that are currently known to be reachable
            Collection<IValue> currentReachableValues = reachable.stream()
                    .filter(heap::containsKey) // Ensure the address is still in the current heap
                    .map(heap::get)
                    .collect(Collectors.toList());

            // Find any *new* addresses referenced by these reachable values
            List<Integer> newAddresses = getAddrFromValues(currentReachableValues);

            // Add the new addresses to the set
            reachable.addAll(newAddresses);
        }

        // Return the final list of all reachable addresses
        return new java.util.ArrayList<>(reachable);
    }

//    public void allSteps() throws MyException, ExpressionException, DictionaryException {
//        PrgState currentPrg = repo.getCurrentPrg();
//        repo.logPrgStateExec(); // log initial state
//
//        while (!currentPrg.getExeStack().isEmpty()) {
//            oneStep(currentPrg);
//            // NEW GARBAGE COLLECTION LOGIC:
//            currentPrg.getHeap().setContent(
//                    unsafeGarbageCollector(
//                            getReachableAddresses( // Use the new method to find all addresses
//                                    currentPrg.getSymTable().getContent().values(),
//                                    currentPrg.getHeap().getContent()
//                            ),
//                            currentPrg.getHeap().getContent()
//                    )
//            );
//            repo.logPrgStateExec(); // log after each step
//        }
//    }

    public void allSteps() throws MyException, InterruptedException {
        executor = Executors.newFixedThreadPool(2);

        List<PrgState> prgList = removeCompletedPrg(repo.getPrgList());

//        while (!prgList.isEmpty()) {
//            oneStepForAllPrg(prgList);
//            prgList = removeCompletedPrg(repo.getPrgList());
//        }

        while (!prgList.isEmpty()) {
            List<Integer> reachableAddresses = getAllReachableAddresses(prgList);

            prgList.forEach(prg ->
                    prg.getHeap().setContent(
                            unsafeGarbageCollector(
                                    reachableAddresses,
                                    prg.getHeap().getContent()
                            )
                    )
            );

            //oneStepForAllPrg(prgList);
            oneStepForAllPrg();
            prgList = removeCompletedPrg(repo.getPrgList());
        }

        executor.shutdownNow();
        repo.setPrgList(prgList);
    }
//public void allSteps() throws MyException { // ALA BUN!
//    executor = Executors.newFixedThreadPool(2);
//
//    List<PrgState> prgList = removeCompletedPrg(repo.getPrgList());
//
//    while (!prgList.isEmpty()) {
//
//        // 1️⃣ Compute reachable addresses from ALL symbol tables
//        List<Integer> reachableAddresses = getAllReachableAddresses(prgList);
//
//        // 2️⃣ Apply GC ONCE (heap is shared)
//        Map<Integer, IValue> newHeap =
//                unsafeGarbageCollector(
//                        reachableAddresses,
//                        prgList.get(0).getHeap().getContent()
//                );
//
//        prgList.forEach(prg -> prg.getHeap().setContent(newHeap));
//
//        // 3️⃣ Execute one step for all programs
//        oneStepForAllPrg(prgList);
//
//        prgList.forEach(prg -> {
//            try {
//                repo.logPrgStateExec(prg);
//            } catch (MyException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        // 4️⃣ Remove completed programs AFTER stepping
//        prgList = removeCompletedPrg(repo.getPrgList());
//    }
//
//    executor.shutdownNow();
//    repo.setPrgList(prgList);
//}



    public void setDisplayFlag(boolean displayFlag) {
        this.displayFlag = displayFlag;
    }

    public IRepository getRepo() {
        return repo;
    }
}