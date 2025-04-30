package com.example.application.aop.selfCall;

import com.example.application.aop.CustomTransaction;
import com.example.application.aop.selfCall.entity.Bus;
import com.example.application.aop.selfCall.entity.Transportation;
import com.example.application.aop.selfCall.repository.BusRepository;
import com.example.application.aop.selfCall.repository.SubwayRepository;
import com.example.application.aop.selfCall.repository.TransportationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("Service1")
public class Service1Impl implements Handler{

    @Autowired
    private BusRepository busRepository;
    @Autowired
    private TransportationRepository transportationRepository;

//    @Transactional(rollbackFor = Exception.class)
    @CustomTransaction
    @Override
    public Object run() {
        Map<String, Object> returnMap = new HashMap<>();
        try{
            Transportation transportation = new Transportation();
            transportation.setCapacity(10);
            transportation.setName("testTransportation");
            transportation.setRegDnt(LocalDateTime.now());
            transportation.setRouteNumber("route");

            Bus bus = new Bus();
            bus.setBusType("type1");
            bus.setLowFloor(true);

//            save(transportation,bus);
            transportationRepository.save(transportation);
            busRepository.saveBus(bus);

            returnMap.put("code", "1");

        }catch(Exception e){
            log.error("Service1 Error : {}",e);
            throw new RuntimeException("Service1 Error", e);
        }

        return returnMap;
    }

    /*
    @Transactional
    public void save(Transportation transportation, Bus bus){
        transportationRepository.save(transportation);
        busRepository.saveBus(bus);
    }

     */


}
