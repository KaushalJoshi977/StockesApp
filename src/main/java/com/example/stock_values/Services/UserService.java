package com.example.stock_values.Services;

import com.example.stock_values.Models.Notification;
import com.example.stock_values.Models.SubscriptionEntity;
import com.example.stock_values.Models.UserEntity;
import com.example.stock_values.Repositories.SubscriptionRepository;
import com.example.stock_values.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    public String Subscribe(String userId, String notifications, String stock) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            // handle error - user not found
            return "user not found";
        }
        List<String> ls = user.getSubscriptionList();
        SubscriptionEntity sub = new SubscriptionEntity();
        sub.setUserID(userId);
        sub.setNotification_frequency(Notification.valueOf(notifications));
        sub.setStock_symbol(stock);
        ls.add(sub.getId());
        user.setSubscriptionList(ls);
        userRepository.save(user);
        subscriptionRepository.save(sub);
        return "Subscribed";
    }

    public String registration(UserEntity user) {
        userRepository.save(user);
        return "User registered";
    }
}
