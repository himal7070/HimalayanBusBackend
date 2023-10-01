package com.himalayanbus.service.IService;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;

import java.util.List;

public interface IBusService {
    Bus addBus(Bus bus) throws BusException;
    List<Bus> viewAllBuses() throws BusException;
    Bus updateBus(Bus bus) throws BusException;
    Bus deleteBus(Integer busId) throws BusException;
    List<Bus> viewBusType(String busType) throws BusException;

}
