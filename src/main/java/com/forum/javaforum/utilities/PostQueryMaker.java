package com.forum.javaforum.utilities;

import java.util.*;

public class PostQueryMaker {

    private static final String GET_POSTS = "SELECT id, author_id, title, content, date FROM posts";
    private static final String BY_AUTHOR = "(author_id IN (SELECT id FROM users WHERE LOWER(username) LIKE LOWER(:author)))";
    private static final String BY_CONTENT = "(LOWER(title) LIKE LOWER(:keyword) OR LOWER(content) LIKE LOWER(:keyword))";
    private static final String BY_TAG = "(id IN (SELECT post_id FROM posts_tags WHERE tag_id IN (SELECT id FROM tags WHERE LOWER(name) LIKE LOWER(:tag))))";

    private static final String JOIN_LIKES = " LEFT JOIN (SELECT post_id, COUNT(*) AS count FROM posts_likes GROUP BY post_id) as l ON posts.id = l.post_id ";
    private static final String JOIN_COMMENTS = " LEFT JOIN (SELECT post_id, COUNT(*) AS count FROM comments GROUP BY post_id) as c ON posts.id = c.post_id ";
    private static final String ORDER_BY_LIKES = " ORDER BY l.count";
    private static final String ORDER_BY_COMMENTS = " ORDER BY c.count";
    private static final String ORDER_BY_DATE = " ORDER BY date";
    private static final String DESC = " DESC";

    private static final String INVALID_SORT = "Invalid sort request. Allowed parameters: 'date' (default), 'comments','likes'.";
    private static final String INVALID_ORDER = "Invalid order request. Allowed parameters: 'asc' (default) and 'desc'.";

    private final StringBuilder query;
    private final List<String> filters;
    private final Map<String, Object> params;

    public PostQueryMaker() {
        query = new StringBuilder(GET_POSTS);
        filters = new ArrayList<>();
        params = new HashMap<>();
    }

    public void setFilterClause(Optional<String> author, Optional<String> keyword, Optional<String> tag) {
        author.ifPresent(value -> {
            filters.add(BY_AUTHOR);
            params.put("author", "%" + value + "%");
        });

        keyword.ifPresent(value -> {
            filters.add(BY_CONTENT);
            params.put("keyword", "%" + value + "%");
        });

        tag.ifPresent(value -> {
            filters.add(BY_TAG);
            params.put("tag", "%" + value + "%");
        });

        if (!filters.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", filters));
        }
    }

    public void setSortClause(Optional<String> sortBy, Optional<String> order, Optional<Long> limit) {
        if (sortBy.isEmpty() || (sortBy.get().equalsIgnoreCase("date"))) {
            query.append(ORDER_BY_DATE);
            setOrder(order);
        } else if (parameterIsLikes(sortBy)) {
            updateSortClause(query, JOIN_LIKES, ORDER_BY_LIKES);
            setOrder(order);
            showNewBeforeOld();
        } else if (parameterIsComments(sortBy)) {
            updateSortClause(query, JOIN_COMMENTS, ORDER_BY_COMMENTS);
            setOrder(order);
            showNewBeforeOld();
        } else {
            throw new IllegalArgumentException(INVALID_SORT);
        }
        setLimit(limit);
    }

    public String getQuery() {
        return query.toString();
    }

    public Map<String, Object> getParams() {
        return params;
    }

    private void updateSortClause(StringBuilder sorted, String joinAttributeTable, String orderByAttribute) {
        if (sorted.toString().contains("WHERE")) sorted.insert(sorted.indexOf("WHERE"), joinAttributeTable);
        else sorted.append(joinAttributeTable);
        sorted.append(orderByAttribute);
    }

    private void setOrder(Optional<String> order) {
        if (order.isPresent()) {
            if (order.get().equalsIgnoreCase("desc")) {
                query.append(DESC);
            } else if (!order.get().equalsIgnoreCase("asc")) {
                throw new IllegalArgumentException(INVALID_ORDER);
            }
        }
    }

    private void showNewBeforeOld() {
        query.append(", date DESC");
    }

    private void setLimit(Optional<Long> limit) {
        if (limit.isPresent()) {
            query.append(" LIMIT :limit");
            params.put("limit", limit.get());
        }
    }

    private boolean parameterIsComments(Optional<String> sortBy) {
        return sortBy.isPresent() && sortBy.get().equalsIgnoreCase("comments");
    }

    private boolean parameterIsLikes(Optional<String> sortBy) {
        return sortBy.isPresent() && sortBy.get().equalsIgnoreCase("likes");
    }
}
