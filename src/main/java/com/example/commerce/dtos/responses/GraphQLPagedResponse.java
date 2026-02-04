package com.example.commerce.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphQLPagedResponse<T> {
    private List<T> content;
    private GraphQLPageInfo pageInfo;

    public static <T> GraphQLPagedResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return new GraphQLPagedResponse<>(
            page.getContent(),
            GraphQLPageInfo.from(page)
        );
    }

    public static <T> GraphQLPagedResponse<T> of(List<T> content, GraphQLPageInfo pageInfo) {
        return new GraphQLPagedResponse<>(content, pageInfo);
    }
}
