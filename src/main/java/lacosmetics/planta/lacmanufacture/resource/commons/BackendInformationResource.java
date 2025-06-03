package lacosmetics.planta.lacmanufacture.resource.commons;

import lacosmetics.planta.lacmanufacture.service.commons.BackendInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for providing information about the backend structure.
 * This resource is designed to help frontend developers understand the backend API.
 */
@RestController
@RequestMapping("/api/backend-info")
@RequiredArgsConstructor
public class BackendInformationResource {

    private final BackendInformationService backendInformationService;

    /**
     * Get a list of all endpoints in the application.
     * 
     * @return A list of maps containing basic information about each endpoint
     */
    @GetMapping("/endpoints")
    public ResponseEntity<List<Map<String, Object>>> getAllEndpoints() {
        return ResponseEntity.ok(backendInformationService.getAllEndpoints());
    }

    /**
     * Get detailed information about a specific endpoint.
     * 
     * @param path The path of the endpoint
     * @param httpMethod The HTTP method of the endpoint
     * @return Detailed information about the endpoint
     */
    @GetMapping("/endpoints/details")
    public ResponseEntity<?> getEndpointDetails(
            @RequestParam("path") String path,
            @RequestParam("method") String httpMethod) {
        
        Map<String, Object> endpointDetails = backendInformationService.getEndpointDetails(path, httpMethod);
        
        if (endpointDetails == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(endpointDetails);
    }



}