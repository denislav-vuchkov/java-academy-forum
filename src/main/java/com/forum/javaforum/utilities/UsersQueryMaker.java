package com.forum.javaforum.utilities;

import com.forum.javaforum.exceptions.InvalidParameter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

public class UsersQueryMaker {

    public static final String[] PERMITTED_SORT_CRITERIA = {"username", "id", "first_name",
            "last_name", "email", "registration_date"};

    private final StringBuilder query;
    private final List<String> filters;
    private final HashMap<String, Object> propertiesMap;

    public UsersQueryMaker(String query) {
        this.query = new StringBuilder(query);
        filters = new ArrayList<>();
        propertiesMap = new HashMap<>();
    }


    public String buildHQLSearchAndSortQuery(Optional<String> username, Optional<String> firstName, Optional<String> email,
                                             Optional<String> sortBy, Optional<String> sortOrder) {
        buildSearch(filters, propertiesMap, "username", username);
        buildSearch(filters, propertiesMap, "first_name", firstName);
        buildSearch(filters,propertiesMap, "email", email);

        joinFilters();

        sortBy.ifPresent(value -> {
            query.append(addSortCriteria(sortBy, sortOrder));
        });

        return query.toString();
    }

    public HashMap<String, Object> getProperties() {
        return propertiesMap;
    }

    private void joinFilters() {
        if (!filters.isEmpty() && !query.toString().contains(" where ")) {
            query.append(" where ").append(String.join(" and ", filters));
        }

        if (!filters.isEmpty() && query.toString().contains(" where ")) {
            query.append(" and ").append(String.join(" and ", filters));;
        }
    }

    private void buildSearch(List<String> query, HashMap<String, Object> properties,
                            String column, Optional<String> parameter) {
        parameter.ifPresent(value -> {
            query.add(String.format(" %s like :%s ", column, column));
            properties.put(column, "%" + value + "%");
        });
    }

    private String addSortCriteria(Optional<String> sortBy, Optional<String> sortOrder) {
        if (sortBy.isEmpty()) {
            return "";
        }

        StringBuilder addToQuery = new StringBuilder(" order by ");

        if (!checkIfValidSortCriteria(sortBy)) {
            throw new InvalidParameter(String.format("The sort criteria chosen is not a valid one. Please " +
                    "enter one of the following criteria: %s.", String.join(", ", PERMITTED_SORT_CRITERIA)));
        }

        addToQuery.append(sortBy.get());

        if (sortOrder.isEmpty() || sortOrder.get().equals("asc")) {
            return addToQuery.toString();
        } else if (sortOrder.get().equalsIgnoreCase("desc")) {
            addToQuery.append(" desc");
        } else {
            throw new InvalidParameter("Invalid sort order has been selected. You can only select 'asc' or 'desc' " +
                    "sort order. If nothing is selected results will be returned in ascending order.");
        }

        return addToQuery.toString();
    }

    private boolean checkIfValidSortCriteria(Optional<String> sortBy) {
        List<String> result = Arrays.stream(PERMITTED_SORT_CRITERIA)
                .filter(e -> e.equals(sortBy.get()))
                .collect(Collectors.toList());

        return !result.isEmpty();
    }

}
