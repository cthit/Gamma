package it.chalmers.delta.views;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.FKITSuperGroup;

import java.util.ArrayList;
import java.util.List;

public class FKITGroupView {
    private final FKITSuperGroup superGroup;
    private final List<FKITGroup> groups;

    public FKITGroupView(FKITSuperGroup superGroup) {
        this.superGroup = superGroup;
        this.groups = new ArrayList<>();
    }

    public List<FKITGroup> getGroups() {
        return this.groups;
    }

    public void addGroup(FKITGroup groups) {
        this.groups.add(groups);
    }

    public FKITSuperGroup getSuperGroup() {
        return this.superGroup;
    }
}
