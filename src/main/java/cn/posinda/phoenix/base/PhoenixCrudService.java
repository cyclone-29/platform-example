package cn.posinda.phoenix.base;


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
public class PhoenixCrudService<R extends PhoenixCrudRepo<T>, T extends PhoenixDataEntity>{

    @Autowired
    protected R repo;

    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public T get(String id) {
        return this.repo.get(id);
    }

    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,propagation = Propagation.REQUIRED,isolation = Isolation.READ_COMMITTED,rollbackFor = Exception.class)
    public int insert(T var1) {
        return this.repo.insert(var1);
    }


    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public T get(T entity) {
        return this.repo.get(entity);
    }

    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public List<T> findList(T entity) {
        return this.repo.findList(entity);
    }

    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public List<T> select(String entity) {
        return this.repo.select(entity);
    }

    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public PageInfo<T> findPage(PageQuery queryVO, T entity) {
        if (StringUtils.isBlank(queryVO.getOrderBy())) {
            PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        } else {
            PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize(), queryVO.getOrderBy() + " " + queryVO.getOrderFlag());
        }
        return new PageInfo<>(this.findList(entity));
    }


    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public PageInfo<T> findPageBy(PageQuery queryVO, String entity) {
        if (StringUtils.isBlank(queryVO.getOrderBy())) {
            PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize());
        } else {
            PageHelper.startPage(queryVO.getPageNum(), queryVO.getPageSize(), queryVO.getOrderBy() + " " + queryVO.getOrderFlag());
        }
        return new PageInfo<>(this.select(entity));
    }

    @Transactional(transactionManager = "phoenixTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 60, rollbackFor = Exception.class)
    public int delete(T entity) {
        return this.repo.delete(entity);

    }

    @Transactional(transactionManager = "phoenixTransactionManager", propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, timeout = 60, rollbackFor = Exception.class)
    public int update(T entity) {
        return this.repo.update(entity);
    }
   
    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public Long getNumberOfRow(String entity) {
        return this.repo.getNumberOfRow(entity);
    }
    
    @Transactional(transactionManager = "phoenixTransactionManager",timeout = 60,readOnly = true)
    public Long getNumberOfRow() {
        return this.repo.getNumberOfRow();
    }
}
