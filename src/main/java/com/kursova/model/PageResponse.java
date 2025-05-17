package com.kursova.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int number;
    private int size;
}
