package com.example.demo.controllers;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.entity.Coordinate;
import com.example.demo.service.IService;
import com.example.demo.util.CommonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DecimalFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class MapController {

    @Autowired
    private IService mapService;

    @GetMapping(value="/coordinates")
    public ResponseEntity<?> getCoordinates(@RequestParam("origin") String origin, @RequestParam("destination") String destination){
        try {
            return mapService.plotResponse(origin, destination);
        }catch(Exception e){
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
