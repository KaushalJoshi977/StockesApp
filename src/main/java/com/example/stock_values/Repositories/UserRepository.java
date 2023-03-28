package com.example.stock_values.Repositories;

import com.example.stock_values.Models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity,String> {

}
