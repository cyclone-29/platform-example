package cn.posinda.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1540063487067274436L;

    private int pageNum;

    private int pageSize;

    private String orderBy;

    private String orderFlag;

}
