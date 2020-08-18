package com.glenfordham.webserver.servlet.parameter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                if (!param.isBlank()) {
                    paramList.add(param);
                }
            }
            this.put(entry.getKey(), paramList);
        }
    }

    /**
     * Builds a new ParameterMap from the existing map, and then filters it by the parameter list provided.
     * Only URL parameters in the filter list will be present in the new ParameterMap
     *
     * @param filterList the list with which to filter the ParameterMap by
     * @return a new ParameterMap, filtered to the required URL parameters
     */
    public ParameterMap filterByList(List<String> filterList) {
        ParameterMap filteredParameterMap = (ParameterMap) this.clone();
        for (String key : this.keySet()) {
            if (filterList.stream().noneMatch(e->e.equalsIgnoreCase(key))) {
                filteredParameterMap.remove(key);
            }
        }
        return filteredParameterMap;
    }

    /**
     * Translates ParameterMap object to a string which can be used for another HTTP request
     */
    public String getAsUrlString() {
        if (this.size() == 0) {
            return "";
        }
        List<String> tempList = new ArrayList<>();
        for (Entry<String, ParameterList> entry : this.entrySet()) {
            StringBuilder urlPiece = new StringBuilder();
            if (entry.getValue().isEmpty()) {
                urlPiece.append(entry.getKey());
            } else {
                for (String parameterPiece : entry.getValue()) {
                    urlPiece.append(entry.getKey()).append(parameterPiece.isBlank() ? "" : "=" + URLEncoder.encode(parameterPiece, StandardCharsets.UTF_8));
                }
            }
            tempList.add(urlPiece.toString());
        }
        return "?" + String.join("&", tempList);
    }

    @Override
    @SuppressWarnings("unchecked")
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

    // ensure provided constructor is used
    private ParameterMap() {
    }
}
