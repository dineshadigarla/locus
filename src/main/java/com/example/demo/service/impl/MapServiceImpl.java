package com.example.demo.service.impl;

import com.example.demo.entity.Coordinate;
import com.example.demo.service.IService;
import com.example.demo.util.CommonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.ws.Response;
import java.text.DecimalFormat;
import java.util.*;

@Component
public class MapServiceImpl implements IService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapServiceImpl.class);

    @Autowired
    private CommonUtil commonUtil;

    @Value("${maps.apikey}")
    private String apiKey;

    @Value("${maps.color}")
    private String color;
    
    @Value("${maps.url}")
    private String url;

    @Override
    public ResponseEntity<List<Coordinate>> plotResponse(String origin, String destination){

        RestTemplate restTemplate = new RestTemplate();
        Double orLat = Double.parseDouble(origin.split(",")[0]);
        Double orLon = Double.parseDouble(origin.split(",")[1]);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("key", apiKey);
        Set<Coordinate> coordinates = new LinkedHashSet<>();
        Set<Coordinate> overviewCoordinates = new LinkedHashSet<>();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(5);
        coordinates.add(new Coordinate(Double.parseDouble(df.format(orLat)), Double.parseDouble(df.format(orLon))));
        HttpEntity<JsonNode> response = restTemplate.getForEntity(builder.build().encode().toUri(), JsonNode.class);
        JsonNode node = response.getBody();
        if(Objects.nonNull(node)) {
            ArrayNode routes = (ArrayNode) node.get("routes");
            if(Objects.nonNull(routes) && routes.size()>0){
                JsonNode overviewPolyline = routes.get(0).get("overview_polyline");
                if(Objects.nonNull(overviewPolyline) && Objects.nonNull(overviewPolyline.get("points"))){
                    overviewCoordinates = commonUtil.decode(overviewPolyline.get("points").asText());
                }
            }
        }
        List<Coordinate> finalCoordinates = calculatePoints(df, overviewCoordinates);
        List<Coordinate> finalResponse = new ArrayList<>();
        finalResponse.add(new Coordinate(finalCoordinates.get(0).getLatitude(), finalCoordinates.get(0).getLongitude()));
        for(int i=0;i<finalCoordinates.size()-1;i++){
            double lat1 = finalCoordinates.get(i).getLatitude();
            double lon1 = finalCoordinates.get(i).getLongitude();
            double lat2 = finalCoordinates.get(i+1).getLatitude();
            double lon2 = finalCoordinates.get(i+1).getLongitude();
            Double d = commonUtil.calculateDistance(lat1, lon1, lat2, lon2);
            if(Math.abs(d.intValue())<=10){
            }else{
                finalResponse.add(new Coordinate(lat2, lon2));
            }
        }
        String str = "";
        for(int k=0;k<finalCoordinates.size();k++){
            Coordinate coordinate = finalResponse.get(k);
            str+=coordinate.getLatitude()+","+coordinate.getLongitude()+","+color+"\n";
        }
        LOGGER.info("{}", str);

        return new ResponseEntity<>(finalResponse, HttpStatus.OK);

    }


    private List<Coordinate> calculatePoints(DecimalFormat df, Set<Coordinate> overviewCoordinates){
        List<Coordinate> overviewCoordinateList = new ArrayList<>(overviewCoordinates);
        Set<Coordinate> finalCoordinates = new LinkedHashSet<>();
        for(int j=0;j<overviewCoordinateList.size()-1;j++){
            double startLat = overviewCoordinateList.get(j).getLatitude();
            double startLon = overviewCoordinateList.get(j).getLongitude();
            finalCoordinates.add(new Coordinate(startLat, startLon));
            double endLat = overviewCoordinateList.get(j+1).getLatitude();
            double endLon = overviewCoordinateList.get(j+1).getLongitude();
            commonUtil.calculateandplot(df, finalCoordinates, startLat, startLon, endLat, endLon,1);
            finalCoordinates.add(new Coordinate(endLat, endLon));
        }
        return new ArrayList<>(finalCoordinates);
    }
}
