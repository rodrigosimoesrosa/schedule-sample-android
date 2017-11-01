package br.com.mirabilis.schedulesample.api.model;

import java.io.Serializable;

public class ExistsPromotion implements Serializable {

    public int promotions;
    public String notification;

    public String getNotificationMessage() {
        return notification == null ? "" : notification;
    }

    public int getPromotions() {
        return promotions;
    }
}
