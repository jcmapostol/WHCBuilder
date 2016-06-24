package com.tests.apostol.conquest.pages.armies;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.ParentObject;

import java.util.ArrayList;
import java.util.List;

public class ArmyVhData implements ParentObject {
    private List<Object> _children;
    private String _name;
    private String _desc;
    private Card _warlord;
    private boolean _isExpanded;

    public ArmyVhData(String name, String desc, Card warlord) {
        _name = name;
        _desc = desc;
        _warlord = warlord;
        _children = new ArrayList<>();
        _children.add(desc);
    }

    public String getName() { return _name; }
    public String getDescription() { return _desc; }
    public Card getWarlord() { return _warlord; }

    @Override
    public List<Object> getChildren() {
        return _children;
    }

    @Override
    public void setChildren(List<Object> children) {
        _children = children;
    }

    @Override
    public boolean getExpanded() {
        return _isExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        _isExpanded = isExpanded;
    }
}
