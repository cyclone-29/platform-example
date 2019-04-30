package cn.posinda.phoenix.service;

import cn.posinda.phoenix.base.PhoenixCrudService;
import cn.posinda.phoenix.entity.TableInfo;
import cn.posinda.phoenix.repo.TableInfoRepo;

import java.util.List;

public abstract class TableInfoService extends PhoenixCrudService<TableInfoRepo,TableInfo> {

    public abstract void updateDescription(TableInfo tableInfo);

    public abstract void updateRowNum(TableInfo tableInfo);

    public abstract List<String> getAllTableNames();

    public abstract Long getRowSumOfAllTables();
}
