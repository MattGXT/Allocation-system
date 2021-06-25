package com.example.demo.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationVO {
    private Integer pageSize;

    private Integer currentPage;

    private Long total;
}
