package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.model.Bus;
import com.himalayanbus.persistence.IBusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Bus updateBus(Bus bus) throws BusException {
        Optional<Bus> findBus = busRepository.findById(bus.getBusId());
        if(findBus.isEmpty()){
            throw new BusException("There is no bus available with this id: "+ bus.getBusId());
        }
        return busRepository.save(bus);
    }
    public Bus deleteBus(Integer busId) throws BusException{

        Optional<Bus> bus = busRepository.findById(busId);

        if(bus.isPresent()){
            Bus existingBus = bus.get();
            busRepository.delete(existingBus);
            return existingBus;

        } else throw  new BusException("There is no such bus with this busId: "+busId);

    }





}
