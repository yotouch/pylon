package com.yotouch.base.service;

public interface SmsService {

    //boolean sendSms(String[] phones, String content);

    //boolean sendSms(String phone, String content);

    boolean sendTemplateSms(String phone, String tplId, String[] args);

    boolean sendTemplateSms(String phone[], String tplId, String[] args);

}
