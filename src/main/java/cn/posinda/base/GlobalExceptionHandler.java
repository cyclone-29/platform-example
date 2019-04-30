package cn.posinda.base;

import cn.posinda.phoenix.entity.ErrorMessage;
import cn.posinda.phoenix.service.ErrorMessageService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    private ErrorMessageService errorMessageService;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public WebResult exceptionHandler(HttpServletRequest request, Exception exception) throws Exception {
        return handleErrorInfo(request, exception);
    }

    private WebResult handleErrorInfo(HttpServletRequest request, Exception exception) {
        WebResult webResult = new WebResult();
        webResult.put("msg", exception.getMessage());
        webResult.put("code", WebResult.ERROR);
        webResult.put("requestUrl", request.getRequestURL().toString());
        exception.printStackTrace();
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setOccursInstant(format.format(new Date()));
        errorMessage.setMessage(exception.getMessage());
        errorMessage.setUrl(request.getRequestURL().toString());
        errorMessageService.insert(errorMessage);
        return webResult;
    }
}
