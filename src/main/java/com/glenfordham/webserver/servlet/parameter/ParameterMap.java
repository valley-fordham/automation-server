package com.glenfordham.webserver.servlet.parameter;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ParameterMap extends HashMap<String, ParameterList> {

    /**
     * Extends the default Servlet request parameter map to remove the need to handle String[]
     *
     * @param requestParams the servlet request parameter map to create the ParameterMap from
     */
    public ParameterMap(Map<String, String[]> requestParams) {
        super();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            ParameterList paramList = new ParameterList();
            for (String param : requestParams.get(entry.getKey())) {
                if (StringUtils.isNotBlank(param)) {
                    paramList.add(param);
                }
            }
            this.put(entry.getKey(), paramList);
        }
    }

    @Override @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        Map<String, ParameterList> m = (Map<String, ParameterList>) o;
        if (m.size() != size()) {
            return false;
        }
        try {
            for (Entry<String, ParameterList> e : this.entrySet()) {
                String key = e.getKey();
                ParameterList value = e.getValue();
                if (value == null) {
                    if (!(m.get(key) == null && m.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(m.get(key))) {
                        return false;
                    }
                    for (int i = 0; i < value.size(); i++) {
                        if (!this.get(key).get(i).equals(m.get(key).get(i))) {
                            return false;
                        }
                    }
                }
            }
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (Entry<String, ParameterList> entry : this.entrySet())
            h += entry.hashCode();
        return h;
    }
}
