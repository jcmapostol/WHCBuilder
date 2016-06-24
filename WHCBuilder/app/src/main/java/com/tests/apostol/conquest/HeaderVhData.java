package com.tests.apostol.conquest;

import java.util.List;

public class HeaderVhData implements ParentObject {
    private List<Object> _children;
    private String _text;
    private boolean _isExpanded;

    public HeaderVhData(List<Object> children, String text) {
        _children = children;
        _text = text;
    }

    public String getText() { return new String(_text); }

    @Override
    public List<Object> getChildren() {
        return _children;
    }

    @Override
    public void setChildren(List<Object> children) {
        _children = children;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        _isExpanded = isExpanded;
    }

    @Override
    public boolean getExpanded() {
        return _isExpanded;
    }
}
