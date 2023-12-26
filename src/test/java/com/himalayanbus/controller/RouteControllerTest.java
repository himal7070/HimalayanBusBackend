package com.himalayanbus.controller;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IRouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {


    @Mock
    private IRouteService routeService;

    @InjectMocks
    private RouteController routeController;

    @Test
    void testAddRoute() throws RouteException {
        // Arrange
        Route routeToAdd = createSampleRoute();
        when(routeService.addRoute(any())).thenReturn(routeToAdd);

        // Act
        ResponseEntity<Route> responseEntity = routeController.addRoute(routeToAdd);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(routeToAdd, responseEntity.getBody());
    }

    @Test
    void testViewAllRoutes() throws RouteException {
        // Arrange
        List<Route> routeList = new ArrayList<>();
        // Add sample routes to the list
        when(routeService.viewAllRoutes()).thenReturn(routeList);

        // Act
        ResponseEntity<List<Route>> responseEntity = routeController.viewAllRoutes();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(routeList, responseEntity.getBody());
    }

    @Test
    void testUpdateRoute() throws RouteException {
        // Arrange
        Route updatedRoute = createSampleRoute();
        when(routeService.updateRoute(any())).thenReturn(updatedRoute);

        // Act
        ResponseEntity<Route> responseEntity = routeController.updateRoute(updatedRoute);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedRoute, responseEntity.getBody());
    }

    @Test
    void testDeleteRoute() throws RouteException {
        // Arrange
        Long routeIdToDelete = 1L;
        Route deletedRoute = createSampleRoute();
        when(routeService.deleteRoute(routeIdToDelete)).thenReturn(deletedRoute);

        // Act
        ResponseEntity<Route> responseEntity = routeController.deleteRoute(routeIdToDelete);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedRoute, responseEntity.getBody());
    }

    @Test
    void testViewRoute() throws RouteException {
        // Arrange
        Long routeIdToView = 1L;
        Route viewedRoute = createSampleRoute();
        when(routeService.viewRoute(routeIdToView)).thenReturn(viewedRoute);

        // Act
        ResponseEntity<Route> responseEntity = routeController.viewRoute(routeIdToView);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(viewedRoute, responseEntity.getBody());
    }

    @Test
    void testGetTotalRouteCountSuccess() throws RouteException {
        // Arrange
        long expectedCount = 5;
        when(routeService.getTotalRouteCount()).thenReturn(expectedCount);

        // Act
        ResponseEntity<String> responseEntity = routeController.getTotalRouteCount();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Total : " + expectedCount, responseEntity.getBody());
    }

    @Test
    void testGetTotalRouteCountFailure() throws RouteException {
        // Arrange
        when(routeService.getTotalRouteCount()).thenThrow(new RouteException("exception"));

        // Act
        ResponseEntity<String> responseEntity = routeController.getTotalRouteCount();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    private Route createSampleRoute() {
        Route route = new Route();
        route.setRouteFrom("Nepal");
        route.setRouteTo("Netherlands");
        route.setDistance(100);
        return route;
    }

}