/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author mizoguch-ken
 */
public class LadderHistoryManager {

    private Deque<LadderHistory> pasts;
    private Deque<LadderHistory> futures;

    /**
     *
     */
    public LadderHistoryManager() {
        pasts = null;
        futures = null;
    }

    /**
     *
     */
    public void clear() {
        clearPasts();
        clearFutures();
    }

    /**
     *
     * @param historyManager
     * @param generation
     */
    public void set(LadderHistoryManager historyManager, int generation) {
        clear();
        pasts = historyManager.getPasts();
        futures = historyManager.getFutures();

        if (pasts != null) {
            if ((generation == 0) || (pasts.isEmpty())) {
                clearPasts();
            } else {
                generationManagementPasts(generation);
            }
        }
        if (futures != null) {
            if ((generation == 0) || (futures.isEmpty())) {
                clearFutures();
            } else {
                generationManagementFutures(generation);
            }
        }
    }

    /**
     *
     * @return
     */
    public Deque<LadderHistory> getPasts() {
        return pasts;
    }

    /**
     *
     * @return
     */
    public Deque<LadderHistory> getFutures() {
        return futures;
    }

    /**
     *
     * @param history
     * @param generation
     */
    public void push(LadderHistory history, int generation) {
        pushPastNonClearFutures(history, generation);
        clearFutures();
    }

    /**
     *
     * @param generation
     * @return
     */
    public LadderHistory undo(int generation) {
        LadderHistory history = null;

        if (generation != 0) {
            if (pasts != null) {
                if (!pasts.isEmpty()) {
                    history = pasts.pop();
                    if (futures == null) {
                        futures = new ArrayDeque<>();
                    }
                    futures.push(history);
                    generationManagementFutures(generation);

                    if (pasts.isEmpty()) {
                        pasts = null;
                    }
                }
            }
        }
        return history;
    }

    /**
     *
     * @param generation
     * @return
     */
    public LadderHistory redo(int generation) {
        LadderHistory history = null;

        if (generation != 0) {
            if (futures != null) {
                if (!futures.isEmpty()) {
                    history = futures.pop();
                    pushPastNonClearFutures(history, generation);

                    if (futures.isEmpty()) {
                        futures = null;
                    }
                }
            }
        }
        return history;
    }

    private void clearPasts() {
        if (pasts != null) {
            pasts.clear();
            pasts = null;
        }
    }

    private void clearFutures() {
        if (futures != null) {
            futures.clear();
            futures = null;
        }
    }

    private void pushPastNonClearFutures(LadderHistory history, int generation) {
        if (pasts == null) {
            pasts = new ArrayDeque<>();
        }
        pasts.push(history);
        generationManagementPasts(generation);
    }

    private void generationManagementPasts(int generation) {
        if ((generation > -1) && (pasts.size() > generation)) {
            for (int index = 0, size = (pasts.size() - generation); index < size; index++) {
                pasts.removeLast();
            }
        }
    }

    private void generationManagementFutures(int generation) {
        if ((generation > -1) && (futures.size() > generation)) {
            for (int index = 0, size = (futures.size() - generation); index < size; index++) {
                futures.removeLast();
            }
        }
    }
}
