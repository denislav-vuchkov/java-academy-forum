package com.forum.javaforum.utilities;

import java.util.*;

public class CommentQueryMaker {

    private static final String GET_COMMENTS = "SELECT id, author_id, post_id, content, date FROM comments";
    private static final String BY_AUTHOR = "(author_id IN (SELECT id FROM users WHERE LOWER(username) LIKE LOWER(:author)))";
    private static final String BY_POST = "(post_id = :postID)";
    private static final String BY_CONTENT = "(LOWER(content) LIKE LOWER(:keyword))";

    private static final String ORDER_BY_DATE = " ORDER BY date";
    private static final String DESC = " DESC";

    private static final String INVALID_SORT = "Invalid sort request. Allowed parameters: 'date' (default).";
    private static final String INVALID_ORDER = "Invalid order request. Allowed parameters: 'asc' (default) and 'desc'.";

    private final StringBuilder query;
    private final List<String> filters;
    private final Map<String, Object> params;

    public CommentQueryMaker() {
        query = new StringBuilder(GET_COMMENTS);
        filters = new ArrayList<>();
        params = new HashMap<>();
    }

    public void setFilterClause(Optional<String> author, Optional<Integer> postID, Optional<String> keyword) {
        author.ifPresent(value -> {
            filters.add(BY_AUTHOR);
            params.put("author", "%" + value + "%");
        });

        postID.ifPresent(value -> {
            filters.add(BY_POST);
            params.put("postID", value);
        });

        keyword.ifPresent(value -> {
            filters.add(BY_CONTENT);
            params.put("keyword", "%" + value + "%");
        });

        if (!filters.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", filters));
        }
    }

    public void setSortClause(Optional<String> sortBy, Optional<String> order) {
        if (sortBy.isEmpty() || (sortBy.get().equalsIgnoreCase("date"))) {
            query.append(ORDER_BY_DATE);
        } else {
            throw new IllegalArgumentException(INVALID_SORT);
        }

        if (order.isPresent()) {
            if (order.get().equalsIgnoreCase("desc")) {
                query.append(DESC);
            } else if (!order.get().equalsIgnoreCase("asc")) {
                throw new IllegalArgumentException(INVALID_ORDER);
            }
        }
    }

    public String getQuery() {
        return query.toString();
    }

    public Map<String, Object> getParams() {
        return params;
    }

}
