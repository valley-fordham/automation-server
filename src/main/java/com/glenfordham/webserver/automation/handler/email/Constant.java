package com.glenfordham.webserver.automation.handler.email;

/**
 * Defines constants used bv the {@link com.glenfordham.webserver.automation.handler.email} package.
 */
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
     * Gets the text value of the Constant.
     *
     * @return The text value of the Constant.
     */
    public String get() {
        return text;
    }
}