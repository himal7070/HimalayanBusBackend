package com.himalayanbus.service;

import com.himalayanbus.model.Bus;
import com.himalayanbus.persistence.IBusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusService {

    @Autowired
    private IBusRepository busRepository;

    public Bus addBus(Bus bus)  {

        //saving bus
        return busRepository.save(bus);
    }


}
