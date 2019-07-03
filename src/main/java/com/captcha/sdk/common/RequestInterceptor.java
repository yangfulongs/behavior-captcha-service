package com.captcha.sdk.common;

import com.alibaba.fastjson.JSON;
import com.captcha.sdk.enums.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;


@Aspect
@Component
@Slf4j
@RestControllerAdvice
public class RequestInterceptor {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
            "||@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void request() {
    }

    @Around("request()")
    public Object interception(ProceedingJoinPoint joinPoint) throws Throwable {
        WebRequestParamWatch paramWatch = WebRequestParamWatch.start();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        // 阿里云健康检查使用
        if (request.getRequestURL().toString().endsWith("/slbcheck.txt")) {
            return joinPoint.proceed();
        }
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            Object result = joinPoint.proceed();
            if (result != null && result.toString().startsWith("redirect")) {
                return result;
            }
            responseMessage.setData(result);
        } catch (WebApiException ex) {
            responseMessage.setCode(ex.getCode());
            responseMessage.setMessage(ex.getMessage());
            paramWatch.setMessage(ex.getInfo());
        } catch (Exception ex) {
            responseMessage.setWebResult(ResultCode.ERROR_SYSTEM);
            paramWatch.setException(ex);
        } finally {
            log.info("参数：{},时间：{}ms,响应结果：{}", paramWatch.toLogString(), paramWatch.processTime(),
                    responseMessage.toLogString(), paramWatch.getException());
        }

        responseJsonData(response, responseMessage);
        return null;
    }

    /**
     * 用于处理通用参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseMessage bindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();

        String errorMesssage = "";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMesssage += fieldError.getDefaultMessage();
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setCode(-1);
        responseMessage.setMessage(errorMesssage);
        return responseMessage;
    }

    private void responseJsonData(HttpServletResponse response, Object data) {
        String jsonData = JSON.toJSONStringWithDateFormat(data,"yyyy-MM-dd HH:mm:ss");
        writeResponse(response, jsonData);
    }

    private void writeResponse(HttpServletResponse response, String data) {
        PrintWriter pw = null;
        try {
            response.setContentType("application/json;charset=utf-8");
            if (StringUtils.isNotBlank(data)) {
                pw = response.getWriter();
                pw.write(data);
            }
        } catch (Exception e) {
            log.warn("请求响应时出错，请确认网络是否正常：{}", e.getMessage());
        } finally {
            if (pw != null) {
                try {
                    pw.close();
                } catch (Exception e) {
                    log.warn("请求响应时出错，请确认网络是否正常：{}", e.getMessage());
                }

            }
        }
    }
    /**
     * 获取方法中声明的注解
     *
     * @return
     * @throws NoSuchMethodException
     */
/*    public AuthIgnore getDeclaredAnnotation(ProceedingJoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // 拿到方法对应的参数类型
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        // 根据类、方法、参数类型（重载）获取到方法的具体信息
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        // 拿到方法定义的注解信息
        AuthIgnore annotation = objMethod.getDeclaredAnnotation(AuthIgnore.class);
        // 返回
        return annotation;
    }*/

    @Data
    class ResponseMessage {

        private int code;

        private String message;
        private Object data;

        public ResponseMessage() {
            this.code =  ResultCode.SUCCESS.code();
            this.message = ResultCode.SUCCESS.message();
        }

        public void setWebResult(ResultCode result) {
            this.setCode(result.code());
            this.setMessage(result.message());
        }

        public String toLogString() {
            return "code="+code+",message=" + message+" data=" + data;
        }
    }

    @Data
    class ResponseMessageControl {

        private long serverTime = System.currentTimeMillis();

        private int error;

        private String message;

        public ResponseMessageControl(ResultCode resultCode) {
            this.error = resultCode.code();
            this.message = resultCode.message();
        }

        public String toLogString() {
            return "serverTime=" + serverTime + ", error=" + error + ", message=" + message;
        }
    }

}

