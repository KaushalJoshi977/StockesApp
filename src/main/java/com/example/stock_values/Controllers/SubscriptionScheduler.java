package com.example.stock_values.Controllers;

import com.example.stock_values.Models.SubscriptionEntity;
import com.example.stock_values.Models.UserEntity;
import com.example.stock_values.Models.Notification;
import com.example.stock_values.Repositories.SubscriptionRepository;
import com.example.stock_values.Repositories.UserRepository;
import com.example.stock_values.Services.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Component
public class SubscriptionScheduler {

    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StocksController stocksController;
    @Autowired
    SubscriptionRepository subscriptionRepository;

    //Schedules according to type of subscription
    @Scheduled(cron = "0 00 12 * * ?") // run at 12pm every day
    public void sendDailySubscriptionEmails() {
        sendSubscriptionEmails(Notification.Daily);
    }

    @Scheduled(cron = "0 0 12 ? * MON") // run at 12pm every Monday
    public void sendWeeklySubscriptionEmails() {
        sendSubscriptionEmails(Notification.Weekly);
    }

    @Scheduled(cron = "0 0 12 1,15 * ?") // run at 12pm on the 1st and 15th day of the month
    public void sendBiweeklySubscriptionEmails() {
        sendSubscriptionEmails(Notification.Biweekly);
    }

    @Scheduled(cron = "0 0 12 1 * ?") // run at 12pm on the 1st day of the month
    public void sendMonthlySubscriptionEmails() {
        sendSubscriptionEmails(Notification.Monthly);
    }


    //Sending Mail
    private void sendSubscriptionEmails(Notification frequency) {
        List<UserEntity> userList = userRepository.findAll();
        for (UserEntity user : userList) {
            List<String> subscriptionList = user.getSubscriptionList();
            // Send email to users
            for (String sub : subscriptionList) {
                SubscriptionEntity subscription = subscriptionRepository.findById(sub).get();
                if (subscription.getNotification_frequency() == frequency) {
                    LocalDate end = LocalDate.now();
                    LocalDate start = end.minusDays(1);
                    Mono<JsonNode> data = stocksController.getStockData(subscription.getStock_symbol(), start.toString(), end.toString());
                    JsonNode node = data.block();
                    String d = node.toString();
                    String emailText = "Hello " + user.getFirst_name() + ",\n\nThis is your " + frequency.toString() + " subscription email." + d;
                    emailService.sendEmail(user.getEmail(), frequency.toString() + " Subscription", emailText);
                }
            }
        }
        System.out.println("mail sent");

    }
}