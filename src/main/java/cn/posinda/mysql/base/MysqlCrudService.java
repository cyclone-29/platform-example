package cn.posinda.mysql.base;


import cn.posinda.base.PageQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("all")
public  class MysqlCrudService<R extends MysqlCrudRepo<T>, T extends MysqlDataEntity>{

    @Autowired
    protected R repo;

    @Transactional(transactionManager = "mysqlTransactionManager",readOnly = true)
    public T get(String id) {
        return this.repo.get(id);
    }

    @Transactional(transactionManager = "mysqlTransactionManager",readOnly = true)
    public T get(T entity) {
        return this.repo.get(entity);
    }

    @Transactional(transactionManager = "mysqlTransactionManager",readOnly = true)
    public List<T> findList(T entity) {
        return this.repo.findList(entity);
    }

    @Transactional(transactionManager = "mysqlTransactionManager", readOnly = true)
    public PageInfo<T> findPage(T entity, PageQuery queryVO) {
        if (StringUtils.isBlank(queryVO.getOrderBy())) {
            PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        } else {
            PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize(), queryVO.getOrderBy() + " " + queryVO.getOrderFlag());
        }
        return new PageInfo<>(this.findList(entity));
    }

    @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, timeout = 5, rollbackFor = Exception.class)
    public int delete(String id) {
        return this.repo.delete(id);
    }

    @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, timeout = 5, rollbackFor = Exception.class)
    public int delete(T entity) {
        return this.repo.delete(entity);
    }

    @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, timeout = 5, rollbackFor = Exception.class)
    public int update(T entity) {
        return this.repo.update(entity);
    }

    @Transactional(transactionManager = "mysqlTransactionManager", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, timeout = 5, rollbackFor = Exception.class)
    public int insert(T entity){
        return this.repo.insert(entity);}

}