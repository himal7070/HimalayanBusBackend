package com.himalayanbus.controller;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.security.token.IAccessControlService;
import com.himalayanbus.security.token.impl.AccessTokenImpl;
import com.himalayanbus.service.IRouteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {


    @Mock
    private IRouteService routeService;
    @Mock
    private IAccessControlService accessControlService;

    @InjectMocks
    private RouteController routeController;

    @Test
    void testAddRoute() throws RouteException {
        // Arrange
        Route routeToAdd = createSampleRoute();
        when(routeService.addRoute(any())).thenReturn(routeToAdd);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<Route> responseEntity = routeController.addRoute(routeToAdd, "Bearer mockToken");

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

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<List<Route>> responseEntity = routeController.viewAllRoutes("Bearer mockToken");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(routeList, responseEntity.getBody());
    }


    @Test
    void testUpdateRoute() throws RouteException {
        // Arrange
        Route updatedRoute = createSampleRoute();
        when(routeService.updateRoute(any())).thenReturn(updatedRoute);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<Route> responseEntity = routeController.updateRoute(updatedRoute, "Bearer mockToken");

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

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<Route> responseEntity = routeController.deleteRoute(routeIdToDelete, "Bearer mockToken");

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

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<Route> responseEntity = routeController.viewRoute(routeIdToView, "Bearer mockToken");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(viewedRoute, responseEntity.getBody());
    }

    @Test
    void testGetTotalRouteCountSuccess() throws RouteException {
        // Arrange
        long expectedCount = 5;
        when(routeService.getTotalRouteCount()).thenReturn(expectedCount);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<String> responseEntity = routeController.getTotalRouteCount( "Bearer mockToken");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Total : " + expectedCount, responseEntity.getBody());
    }

    @Test
    void testGetTotalRouteCountFailure() throws RouteException {
        // Arrange
        when(routeService.getTotalRouteCount()).thenThrow(new RouteException("exception"));

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<String> responseEntity = routeController.getTotalRouteCount( "Bearer mockToken");

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