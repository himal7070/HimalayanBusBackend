package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.model.Bus;
import com.himalayanbus.persistence.IBusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BusService {

    @Autowired
    private IBusRepository busRepository;

    public Bus addBus(Bus bus)  {

        //saving bus
        return busRepository.save(bus);
    }

    public List<Bus> viewAllBuses() throws BusException {
        List<Bus> busList = busRepository.findAll();
        if(busList.isEmpty()){
            throw new BusException("There is no bus available at the moment");
        }
        return busList;
    }


}
