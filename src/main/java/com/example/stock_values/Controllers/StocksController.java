package com.example.stock_values.Controllers;

import com.example.stock_values.Models.UserEntity;
import com.example.stock_values.Services.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;

@RestController()
public class StocksController {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    UserService userService;


    //for new user Registrations
    @PostMapping("/register")
    public String register(@RequestBody UserEntity user){

        return userService.registration(user);

    }



    //For subscription
    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String userId,String notificationFrequency,String stock){
        String x = userService.Subscribe(userId, notificationFrequency, stock);
        if (x != null) return x;
        else return "Subscription unsuccessful";
    }




    //To get data of a specific stock according to date interval
    @GetMapping("/getStocks")//to get the data of a stock within specific dates
    public Mono<JsonNode> getStockData(@RequestParam String stockName,@RequestParam String startDate,@RequestParam String endDate) {
        WebClient client = WebClient.create("https://www.alphavantage.co/query");
        LocalDate date = LocalDate.parse(startDate);
        System.out.println(date);
        LocalDate date2 = LocalDate.parse(endDate);


        return client.get()
                .uri(uriBuilder -> addParams(uriBuilder, stockName))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> stocksInDateRange(date, date2, stockName, jsonNode))
                .onErrorResume(error -> Mono.just(getError(error)));
    }

    private static JsonNode getError(Throwable error) {
        return JsonNodeFactory.instance.objectNode()
                .set("error", JsonNodeFactory.instance.textNode("Error retrieving stock data: " + error.getMessage()));
    }

    private static JsonNode stocksInDateRange(LocalDate date, LocalDate date2, String stock_symbol, JsonNode jsonNode) {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        JsonNode timeSeriesNode = jsonNode.get("Time Series (Daily)");
        if (timeSeriesNode == null) {
            return result;
        }
        long days = ChronoUnit.DAYS.between(date,date2);
        LongStream.range(0, days+1).forEach(day -> {
            LocalDate localDate = date.plusDays(day);
            String dateString = localDate.format(DATE_FORMATTER);
            JsonNode dataNode = timeSeriesNode.get(dateString);
            if (dataNode != null) {
                result.set(dateString, dataNode);
            }
        });

        ObjectNode symbolNode = JsonNodeFactory.instance.objectNode();
        symbolNode.set(stock_symbol, result);
        return symbolNode;
    }

    private static URI addParams(UriBuilder uriBuilder, String stock_symbol) {
        return uriBuilder
                .queryParam("function", "TIME_SERIES_DAILY_ADJUSTED")
                .queryParam("symbol", stock_symbol)
                .queryParam("apikey", "ZOCG1SLUMZH7R8TC")
                .build();
    }

}
