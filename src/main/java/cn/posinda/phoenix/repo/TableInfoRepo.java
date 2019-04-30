package cn.posinda.phoenix.repo;

import cn.posinda.phoenix.base.PhoenixCrudRepo;
import cn.posinda.phoenix.entity.TableInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableInfoRepo extends PhoenixCrudRepo<TableInfo> {

    void updateDescription(TableInfo tableInfo);

    void updateRowNum(TableInfo tableInfo);

    List<String> getAllTableNames();

    Long getRowSumOfAllTables();
}
