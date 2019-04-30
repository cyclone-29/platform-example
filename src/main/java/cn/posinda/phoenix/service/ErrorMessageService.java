package cn.posinda.phoenix.service;

import cn.posinda.phoenix.base.PhoenixCrudService;
import cn.posinda.phoenix.entity.ErrorMessage;
import cn.posinda.phoenix.repo.ErrorMessageRepo;

public abstract class ErrorMessageService extends PhoenixCrudService<ErrorMessageRepo,ErrorMessage> {
     
	@Override
    public abstract int insert(ErrorMessage errorMessage);
}
