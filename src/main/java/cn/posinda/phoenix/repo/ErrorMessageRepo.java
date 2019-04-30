package cn.posinda.phoenix.repo;

import cn.posinda.phoenix.base.PhoenixCrudRepo;
import cn.posinda.phoenix.entity.ErrorMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorMessageRepo extends PhoenixCrudRepo<ErrorMessage> {

    int insert(ErrorMessage errorMessage);
}
