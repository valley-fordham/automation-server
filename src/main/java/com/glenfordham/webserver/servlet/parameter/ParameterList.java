package com.glenfordham.webserver.servlet.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ParameterList extends ArrayList<String> {

    /**
     * Extends a plain ArrayList of Strings to provide convenience methods
     */
    ParameterList() {
        super();
    }

    /**
     * Gets the first value in the ParameterList
     *
     * @return the first String in the ParamaterList
     * @throws ParameterException if the ParameterList is empty
     */
    public String getFirst() throws ParameterException {
        if (this.size() > 0) {
            return this.get(0);
        } else {
            throw new ParameterException("Parameter List is empty");
        }
    }

    @Override @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        ListIterator<String> e1 = listIterator();
        ListIterator<String> e2 = ((List<String>) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            String o1 = e1.next();
            Object o2 = e2.next();
            if (!(Objects.equals(o1, o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (String e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }
}
