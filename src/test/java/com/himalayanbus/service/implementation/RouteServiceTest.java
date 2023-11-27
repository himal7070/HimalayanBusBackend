package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.persistence.repository.IRouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteServiceTest {

    @Mock
    private IRouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRoute() {
        Route newRoute = new Route();
        newRoute.setRouteFrom("City A");
        newRoute.setRouteTo("City B");

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(null);
        when(routeRepository.save(newRoute)).thenReturn(newRoute);

        try {
            Route result = routeService.addRoute(newRoute);
            assertNotNull(result);
            verify(routeRepository, times(1)).findByRouteFromAndRouteTo("City A", "City B");
            verify(routeRepository, times(1)).save(newRoute);
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testAddRoute_RouteAlreadyExists() {
        Route existingRoute = new Route();
        existingRoute.setRouteID(1L);
        existingRoute.setRouteFrom("City A");
        existingRoute.setRouteTo("City B");

        Route newRoute = new Route();
        newRoute.setRouteFrom("City A");
        newRoute.setRouteTo("City B");

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(existingRoute);

        assertThrows(RouteException.class, () -> routeService.addRoute(newRoute));
        verify(routeRepository, times(1)).findByRouteFromAndRouteTo("City A", "City B");
        verify(routeRepository, never()).save(any());
    }

    @Test
    void testViewAllRoutes() {
        List<Route> routes = new ArrayList<>();
        routes.add(new Route());
        when(routeRepository.findAll()).thenReturn(routes);

        try {
            List<Route> routeList = routeService.viewAllRoutes();
            assertEquals(1, routeList.size());
            verify(routeRepository, times(1)).findAll();
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testViewAllRoutes_EmptyList() {
        when(routeRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(RouteException.class, () -> routeService.viewAllRoutes());
        verify(routeRepository, times(1)).findAll();
    }

    @Test
    void testUpdateRoute() {
        Route updatedRoute = new Route();
        updatedRoute.setRouteID(1L);
        updatedRoute.setRouteFrom("City A");
        updatedRoute.setRouteTo("City B");

        Route existingRoute = new Route();
        existingRoute.setRouteID(1L);
        existingRoute.setRouteFrom("City X");
        existingRoute.setRouteTo("City Y");

        when(routeRepository.findById(1L)).thenReturn(Optional.of(existingRoute));
        when(routeRepository.save(existingRoute)).thenReturn(existingRoute);

        try {
            Route result = routeService.updateRoute(updatedRoute);
            assertNotNull(result);
            assertEquals("City A", result.getRouteFrom());
            assertEquals("City B", result.getRouteTo());
            verify(routeRepository, times(1)).findById(1L);
            verify(routeRepository, times(1)).save(existingRoute);
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testUpdateRoute_BusesScheduled() {
        Route updatedRoute = new Route();
        updatedRoute.setRouteID(1L);
        updatedRoute.setRouteFrom("City A");
        updatedRoute.setRouteTo("City B");

        Route existingRoute = new Route();
        existingRoute.setRouteID(1L);
        existingRoute.setRouteFrom("City X");
        existingRoute.setRouteTo("City Y");

        List<Bus> busList = new ArrayList<>();
        busList.add(new Bus());

        existingRoute.setBusList(busList);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(existingRoute));

        assertThrows(RouteException.class, () -> routeService.updateRoute(updatedRoute));
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, never()).save(any());
    }


    @Test
    void testDeleteRoute() {
        Route routeToDelete = new Route();
        routeToDelete.setRouteID(1L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(routeToDelete));
        doNothing().when(routeRepository).delete(routeToDelete);

        try {
            Route result = routeService.deleteRoute(1L);
            assertNotNull(result);
            assertEquals(routeToDelete, result);
            verify(routeRepository, times(1)).findById(1L);
            verify(routeRepository, times(1)).delete(routeToDelete);
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }


    @Test
    void testDeleteRoute_NotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RouteException.class, () -> routeService.deleteRoute(1L));
        verify(routeRepository, times(1)).findById(1L);
        verify(routeRepository, never()).delete(any());
    }

    @Test
    void testViewRoute() {
        Route route = new Route();
        route.setRouteID(1L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        try {
            Route result = routeService.viewRoute(1L);
            assertNotNull(result);
            assertEquals(route, result);
            verify(routeRepository, times(1)).findById(1L);
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testViewRoute_NotFound() {
        when(routeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RouteException.class, () -> routeService.viewRoute(1L));
        verify(routeRepository, times(1)).findById(1L);
    }
}


