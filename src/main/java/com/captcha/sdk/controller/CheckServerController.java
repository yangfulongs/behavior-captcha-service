package com.captcha.sdk.controller;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author wangshichun
 * @Description 必须的基础接口
 * @Date 2018/3/27 20:39
 */
@RestController
public class CheckServerController implements ApplicationListener<SpringApplicationEvent> {

    private static AtomicReference<HttpStatus> status = new AtomicReference<>(HttpStatus.SERVICE_UNAVAILABLE);

    @Override
    public void onApplicationEvent(SpringApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            status.set(HttpStatus.OK);
        } else if (event instanceof ApplicationFailedEvent) {
            status.set(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = {"/checkServer"})
    public ResponseEntity<String> checkServer() { // checkServer 是Jenkins发版的时候检查状态用的，slbcheck.txt是阿里云健康检查用的（返回200状态码表示成功）
        return ResponseEntity.status(status.get()).contentType(MediaType.TEXT_PLAIN).body(status.get().getReasonPhrase());
    }

    @GetMapping(path = {"/jlccmscheck.txt"})
    public ResponseEntity<String> slbcheck() {
        if (slbCheckFlag.get()) {
            return checkServer();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_PLAIN).body(HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    private static AtomicBoolean slbCheckFlag = new AtomicBoolean(false);

    @GetMapping(path = {"/jlccmscheckStatus"})
    public ResponseEntity<String> slbcheckStatus(@RequestParam("status") String s) {
        slbCheckFlag.set("on".equalsIgnoreCase(s));
        return ResponseEntity.status(status.get()).contentType(MediaType.TEXT_PLAIN).body(status.get().getReasonPhrase());
    }
}
