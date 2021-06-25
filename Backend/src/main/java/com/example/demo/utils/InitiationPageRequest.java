package com.example.demo.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class InitiationPageRequest {

    public static PageRequest createPageRequest(Integer currentPage, Integer pageSize, String sortOrder) {
//        Sort sort;
//        List<Sort.Order> orders = new ArrayList<>();
//        orders.add(new Sort.Order(Sort.Direction.DESC, "isTop"));
//        orders.add(new Sort.Order(Sort.Direction.DESC, "isTopTime"));
//        if(StringUtils.isBlank(sortOrder)) {
//            sort = Sort.by(orders);
//        }else {
//            switch (sortOrder) {
//                case "initialDateDESC":
//                    orders.add(0,new Sort.Order(Sort.Direction.DESC, "initialDate"));
//                    break;
//                case "initialDateASC":
//                    orders.add(0,new Sort.Order(Sort.Direction.ASC, "initialDate"));
//                    break;
//                case "publishDateDESC":
//                    orders.add(0,new Sort.Order(Sort.Direction.DESC, "publishDate"));
//                    break;
//                case "publishDateASC":
//                    orders.add(0,new Sort.Order(Sort.Direction.ASC, "publishDate"));
//                    break;
//                case "processTaskNameDESC":
//                    orders.add(0,new Sort.Order(Sort.Direction.DESC, "state"));
//                    break;
//                case "processTaskNameASC":
//                    orders.add(0,new Sort.Order(Sort.Direction.ASC, "state"));
//                    break;
//                default:
//                    break;
//            }
//            sort = Sort.by(orders);
//        }
        return PageRequest.of(currentPage - 1, pageSize);

    }

}
