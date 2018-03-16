/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ken.mizoguch.ladders;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mizoguch-ken
 */
public class LadderJson {

    private List<LadderJsonLadder> ladders;
    private List<LadderJsonComment> comments;
    private LadderHistoryManager historys;

    /**
     *
     */
    public LadderJson() {
        this.ladders = null;
        this.comments = null;
        this.historys = null;
    }

    /**
     *
     * @return
     */
    public List<LadderJsonLadder> getLadders() {
        return ladders;
    }

    /**
     *
     * @param ladder
     */
    public void addLadder(LadderJsonLadder ladder) {
        if (ladder != null) {
            if (ladders == null) {
                ladders = new ArrayList<>();
            }
            ladders.add(ladder);
        }
    }

    /**
     *
     */
    public void sortLadders() {
        if (ladders != null) {
            ladders.sort((o1, o2) -> {
                return o1.getIdx() - o2.getIdx();
            });
        }
    }

    /**
     *
     * @return
     */
    public List<LadderJsonComment> getComments() {
        return comments;
    }

    /**
     *
     * @param comment
     */
    public void addComment(LadderJsonComment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }

    /**
     *
     */
    public void sortComments() {
        if (comments != null) {
            comments.sort((o1, o2) -> {
                return o1.getAddress().compareTo(o2.getAddress());
            });
        }
    }

    /**
     *
     * @return
     */
    public LadderHistoryManager getHistoryManager() {
        return historys;
    }

    /**
     *
     * @param historyManager
     * @param generation
     */
    public void setHistoryManager(LadderHistoryManager historyManager, int generation) {
        if (historys == null) {
            historys = new LadderHistoryManager();
        }
        historys.set(historyManager, generation);
    }
}
