package com.suki.bansachOnline.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmWebhook {
    private String webhookUrl;

    public ConfirmWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}
