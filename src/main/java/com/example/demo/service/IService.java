package com.example.demo.service;

import com.example.demo.entity.Coordinate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface IService {
    ResponseEntity<List<Coordinate>> plotResponse(String origin, String destination);
}
