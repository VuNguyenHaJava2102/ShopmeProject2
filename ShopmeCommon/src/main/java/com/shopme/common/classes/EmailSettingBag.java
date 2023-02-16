package com.shopme.common.classes;

import com.shopme.common.entity.Setting;

import java.util.List;

public class EmailSettingBag extends SettingBag {

    public EmailSettingBag(List<Setting> settings) {
        super(settings);
    }

    public String getHost() {
        return super.getSettingValueByKey("MAIL_HOST");
    }

    public int getPort() {
        return Integer.parseInt(super.getSettingValueByKey("MAIL_PORT"));
    }

    public String getUserName() {
        return super.getSettingValueByKey("MAIL_USERNAME");
    }

    public String getPassword() {
        return super.getSettingValueByKey("MAIL_PASSWORD");
    }

    public String getSmtpAuth() {
        return super.getSettingValueByKey("SMTP_AUTH");
    }

    public String getSmtpSecured() {
        return super.getSettingValueByKey("SMTP_SECURED");
    }

    public String getFromAddress() {
        return super.getSettingValueByKey("MAIL_FROM");
    }

    public String getSenderName() {
        return super.getSettingValueByKey("MAIL_SENDER_NAME");
    }

    public String getCustomerVerificationSubject() {
        return super.getSettingValueByKey("CUSTOMER_VERIFY_SUBJECT");
    }

    public String getCustomerVerificationContent() {
        return super.getSettingValueByKey("CUSTOMER_VERIFY_CONTENT");
    }

}
