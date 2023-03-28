package com.example.stock_values.Repositories;

import com.example.stock_values.Models.SubscriptionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionRepository extends MongoRepository<SubscriptionEntity,String> {
}
