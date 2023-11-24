package net.thumbtack.school.market.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataWithCountsDtoResponse<T> {

    private List<T> data;

    private long elements;

    private long pages;

}
