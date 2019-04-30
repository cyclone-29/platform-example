package cn.posinda.phoenix.base;

import java.util.List;

public interface PhoenixCrudRepo<T> {
    T get(String var1);

    T get(T var1);

    List<T> findList(T var1);

    int delete(T var1);

    int update(T var1);

    List<T> select(String var1);

    int insert(T var1);
    
    Long getNumberOfRow(String var1);
    
    Long getNumberOfRow();
}
