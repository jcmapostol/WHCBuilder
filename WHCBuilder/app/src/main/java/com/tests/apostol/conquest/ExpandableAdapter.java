package com.tests.apostol.conquest;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected static final int PARENT_TYPE = 0;
    protected static final int CHILD_TYPE = 1;
    private List<Object> _dataSet;

    public ExpandableAdapter(List<ParentObject> parentList) {
        _dataSet = new ArrayList<>();
        _dataSet.addAll(parentList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return onCreateParentViewHolder(parent);
        } else {
            return onCreateChildViewHolder(parent);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateParentViewHolder(ViewGroup parent);
    public abstract RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object data = _dataSet.get(position);

        if (_dataSet.get(position) instanceof ParentObject)
            onBindParent(holder, position, data);
        else
            onBindChild(holder, position, data);
    }
    public abstract void onBindParent(RecyclerView.ViewHolder holder, int position, Object data);
    public abstract void onBindChild(RecyclerView.ViewHolder holder, int position, Object data);

    public void addParent(ParentObject po) {
        _dataSet.add(po);
        notifyItemInserted(_dataSet.size() - 1);
    }
    public void removeParent(ParentObject po) {
        if (_dataSet.contains(po)) {
            removeChildren(po);
            int index = _dataSet.indexOf(po);
            _dataSet.remove(index);
            notifyItemRemoved(index);
        }
    }
    public void overwriteParents(List<ParentObject> newParents) {
        _dataSet.clear();
        _dataSet.addAll(newParents);
        notifyDataSetChanged();
    }
    public void expandAll() {
        List<ParentObject> parents = new ArrayList<>();

        for (Object obj : _dataSet) {
            if (obj instanceof ParentObject) {
                ParentObject po = (ParentObject) obj;
                parents.add(po);
            }
        }

        for (ParentObject po : parents) {
            addChildren(po);
        }
    }
    public void collapseAll() {
        List<ParentObject> parents = new ArrayList<>();

        for (Object obj : _dataSet) {
            if (obj instanceof ParentObject) {
                ParentObject po = (ParentObject) obj;
                parents.add(po);
            }
        }

        for (ParentObject po : parents) {
            removeChildren(po);
        }
    }

    protected void addChildren(ParentObject po) {
        int index = _dataSet.indexOf(po);
        int ctr = index + 1;
        po.setExpanded(true);
        for (Object child : po.getChildren()) {
            _dataSet.add(ctr++, child);
        }

        notifyItemRangeInserted(index + 1, po.getChildren().size());
    }
    protected void removeChildren(ParentObject po) {
        int index = _dataSet.indexOf(po);
        int next = index + 1;
        int removeCount = 0;
        po.setExpanded(false);
        while (next < _dataSet.size() && !(_dataSet.get(next) instanceof ParentObject)) {
            _dataSet.remove(next);
            removeCount++;
        }

        notifyItemRangeRemoved(index + 1, removeCount);
    }
    protected boolean hasChildren(ParentObject po) {
        int position = _dataSet.indexOf(po);
        return _dataSet.size() > position + 1 && !(_dataSet.get(position + 1) instanceof ParentObject);
    }
    protected Object getDataAt(int position) { return _dataSet.get(position); }

    @Override
    public int getItemViewType(int position) {
        if (_dataSet.get(position) instanceof ParentObject) {
            return PARENT_TYPE;
        } else {
            return CHILD_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return _dataSet.size();
    }
}