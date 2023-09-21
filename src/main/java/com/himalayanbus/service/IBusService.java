package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.model.Bus;

import java.util.List;

public interface IBusService {
    public Bus addBus(Bus bus) throws BusException;

    public List<Bus> viewAllBuses() throws BusException;

    public Bus updateBus(Bus bus) throws BusException;
    public Bus deleteBus(Integer busId) throws BusException;



}
