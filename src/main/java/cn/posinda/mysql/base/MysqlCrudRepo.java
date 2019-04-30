package cn.posinda.mysql.base;

import java.util.List;

public interface MysqlCrudRepo<T> {

    T get(String var1);

    T get(T var1);

    List<T> findList(T var1);

    int delete(String id);

    int delete(T entity);

    int update(T entity);

    int insert(T entity);
}
