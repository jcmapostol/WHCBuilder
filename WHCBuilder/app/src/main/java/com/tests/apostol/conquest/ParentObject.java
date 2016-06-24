package com.tests.apostol.conquest;

import java.util.List;

public interface ParentObject {
    List<Object> getChildren();
    void setChildren(List<Object> children);
    void setExpanded(boolean isExpanded);
    boolean getExpanded();
}
