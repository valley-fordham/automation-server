package com.glenfordham.webserver.automation.handler;

public enum Constant {
    MAIL_HOST(
            "mail.smtp.host"
    ),
    MAIL_PORT(
            "mail.smtp.port"
    ),
    MAIL_SSL(
            "mail.smtp.ssl.enable"
    ),
    MAIL_TLS(
            "mail.smtp.starttls.enable"
    ),
    MAIL_AUTH(
            "mail.smtp.auth"
    );

    private final String text;

    Constant(String text) {
        this.text = text;
    }

    /**
     * Returns the text value of the Constant
     *
     * @return text value
     */
    public String getText() {
        return text;
    }
}