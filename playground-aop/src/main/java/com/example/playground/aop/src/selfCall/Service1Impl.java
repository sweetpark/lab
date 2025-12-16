package com.example.playground.aop.src.selfCall;

import com.example.playground.aop.CustomTransaction;
import com.example.playground.aop.src.selfCall.entity.Bus;
import com.example.playground.aop.src.selfCall.entity.Transportation;
import com.example.playground.aop.src.selfCall.repository.BusRepository;
import com.example.playground.aop.src.selfCall.repository.TransportationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("Service1")
public class Service1Impl implements com.example.project.aop.src.selfCall.Handler {

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
