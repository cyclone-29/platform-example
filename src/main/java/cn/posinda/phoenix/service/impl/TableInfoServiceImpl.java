package cn.posinda.phoenix.service.impl;

import cn.posinda.phoenix.entity.TableInfo;
import cn.posinda.phoenix.service.TableInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TableInfoServiceImpl extends TableInfoService {

    @Transactional(transactionManager = "phoenixTransactionManager", isolation = Isolation.READ_COMMITTED, timeout = 60, rollbackFor = Exception.class)
    @Override
    public void updateDescription(TableInfo tableInfo) {
        this.repo.updateDescription(tableInfo);
    }

    @Transactional(transactionManager = "phoenixTransactionManager", isolation = Isolation.READ_COMMITTED, timeout = 60, rollbackFor = Exception.class)
    @Override
    public void updateRowNum(TableInfo tableInfo) {
        this.repo.updateRowNum(tableInfo);
    }

    @Override
    @Transactional(transactionManager = "phoenixTransactionManager",readOnly = true)
    public List<String> getAllTableNames() {
        return this.repo.getAllTableNames();
    }

    @Override
    @Transactional(transactionManager = "phoenixTransactionManager",readOnly = true)
    public Long getRowSumOfAllTables() {
        return this.repo.getRowSumOfAllTables();
    }


}
