package cn.posinda.phoenix.service.impl;

import cn.posinda.phoenix.entity.ErrorMessage;
import cn.posinda.phoenix.service.ErrorMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ErrorMessageServiceImpl extends ErrorMessageService {

    @Transactional(transactionManager = "phoenixTransactionManager",isolation = Isolation.READ_COMMITTED,timeout = 60,rollbackFor = Exception.class)
    @Override
    public int insert(ErrorMessage errorMessage) {
        return this.repo.insert(errorMessage);
    }
}
