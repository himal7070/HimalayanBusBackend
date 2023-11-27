package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IBusService {
    @Transactional
    Bus addBus(Bus bus) throws BusException;

    @Transactional
    Bus updateBus(Long busId, Bus newBusDetails) throws BusException;


    @Transactional
    Bus deleteBus(Long busId) throws BusException;

    @Transactional
    Bus viewBus(Long busId) throws BusException;

    @Transactional
    List<Bus> viewBusByType(String busType) throws BusException;

    @Transactional
    List<Bus> viewAllBus() throws BusException;


}
