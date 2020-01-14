package net.pwall.json.stream.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.pwall.util.pipeline.AbstractAcceptor;

public class TestListAcceptor<A> extends AbstractAcceptor<A, List<A>> {

    private List<A> list;

    public TestListAcceptor() {
        list = new ArrayList<>();
    }

    @Override
    public void acceptObject(A value) {
        list.add(value);
    }

    @Override
    public List<A> getResult() {
        return Collections.unmodifiableList(list);
    }

}
