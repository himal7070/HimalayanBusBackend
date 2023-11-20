package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.RouteException;
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
import static org.mockito.Mockito.when;

class RouteServiceTest {

    @Mock
    private IRouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRoute() {
        Route route = new Route("From", "To", 100);
        when(routeRepository.findByRouteFromAndRouteTo("From", "To")).thenReturn(null);
        when(routeRepository.save(route)).thenReturn(route);

        try {
            Route addedRoute = routeService.addRoute(route);
            assertNotNull(addedRoute);
            assertEquals("From", addedRoute.getRouteFrom());
            assertEquals("To", addedRoute.getRouteTo());
            assertEquals(100, addedRoute.getDistance());
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testViewAllRoutes() {
        List<Route> routeList = new ArrayList<>();
        routeList.add(new Route("From1", "To1", 100));
        routeList.add(new Route("From2", "To2", 200));

        when(routeRepository.findAll()).thenReturn(routeList);

        try {
            List<Route> routes = routeService.viewAllRoutes();
            assertFalse(routes.isEmpty());
            assertEquals(2, routes.size());
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testUpdateRoute() {
        int routeId = 1;
        Route existingRoute = new Route("From", "To", 100);
        Route updatedRoute = new Route("UpdatedFrom", "UpdatedTo", 200);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(existingRoute));
        when(routeRepository.save(existingRoute)).thenReturn(existingRoute);

        try {
            Route updated = routeService.updateRoute(routeId, updatedRoute);
            assertEquals("UpdatedFrom", updated.getRouteFrom());
            assertEquals("UpdatedTo", updated.getRouteTo());
            assertEquals(200, updated.getDistance());
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }

    @Test
    void testDeleteRoute() {
        int routeId = 1;
        Route route = new Route("From", "To", 100);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        try {
            Route deleted = routeService.deleteRoute(routeId);
            assertEquals("From", deleted.getRouteFrom());
            assertEquals("To", deleted.getRouteTo());
            assertEquals(100, deleted.getDistance());
        } catch (RouteException e) {
            fail("RouteException should not be thrown");
        }
    }
}
