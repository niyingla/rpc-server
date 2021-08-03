package com.example.demo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;

/**
 * @Description
 * @Author toms
 * @Date 2019/7/2
 */
@Data
public class CompareDto implements Serializable {

    @NotBlank
    private String excelPathTo;
    @NotBlank
    private String excelPathFrom;
    @NotBlank
    private String type;

    /**
     * 比较方式
     */
    private String way;

}
