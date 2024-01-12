package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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


    @Transactional(readOnly = true)
    long countAllBuses() throws BusException;


    @Transactional
    List<Bus> searchBusByRoute(Optional<String> routeFrom, Optional<String> routeTo, LocalDate journeyDate) throws BusException;

    @Transactional
    String delayBusDeparture(Long busId, Duration delayDuration) throws BusException;
}
