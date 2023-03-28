package com.example.stock_values.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
@Data
@NoArgsConstructor
public class SubscriptionEntity {

    @Id
    private String id;

    private String stock_symbol;

    private Notification notification_frequency;

    private String userID;
}
