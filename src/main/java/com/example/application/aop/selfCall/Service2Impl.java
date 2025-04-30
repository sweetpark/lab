package com.example.application.aop.selfCall;

import com.example.application.aop.CustomTransaction;
import com.example.application.aop.selfCall.entity.Bus;
import com.example.application.aop.selfCall.entity.Subway;
import com.example.application.aop.selfCall.entity.Transportation;
import com.example.application.aop.selfCall.repository.SubwayRepository;
import com.example.application.aop.selfCall.repository.TransportationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service("Service2")
public class Service2Impl implements Handler{

    @Autowired
    private SubwayRepository subwayRepository;
    @Autowired
    private TransportationRepository transportationRepository;

    @CustomTransaction
    @Override
    public Object run() {
        Transportation transportation = new Transportation();
        transportation.setCapacity(20);
        transportation.setName("subwayTransportation");
        transportation.setRegDnt(LocalDateTime.now());
        transportation.setRouteNumber("route2");

        Subway subway = new Subway();
        subway.setLineName("2");
        subway.setNumberOfCars(10);

        transportationRepository.save(transportation);
        subwayRepository.save(subway);


        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("code", "1");

        return returnMap;
    }
}
