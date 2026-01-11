package exotic.app.planta.service.commons;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.SubTypes;

/**
 * Service for providing information about the backend structure, including endpoints and model classes.
 * This service is designed to help frontend developers understand the backend API.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BackendInformationService {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private static final Set<String> PRIMITIVE_OR_BUILTIN = Set.of(
            "int", "long", "double", "float", "boolean", "char", "byte", "short",
            "Integer", "Long", "Double", "Float", "Boolean", "Character", "Byte", "Short",
            "String", "LocalDate", "LocalDateTime", "Date", "Object", "Authentication"
    );


    /**
     * Get a list of all endpoints in the application.
     * 
     * @return A list of maps containing basic information about each endpoint
     */
    public List<Map<String, Object>> getAllEndpoints() {
        List<Map<String, Object>> endpoints = new ArrayList<>();
        
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            
            // Skip the backend information endpoints to avoid recursion
            if (handlerMethod.getBeanType().getSimpleName().equals("BackendInformationResource")) {
                continue;
            }
            
            Map<String, Object> endpointInfo = new HashMap<>();
            
            // Get HTTP methods
            Set<String> httpMethods = mappingInfo.getMethodsCondition().getMethods().stream()
                    .map(method -> method.name())
                    .collect(Collectors.toSet());
            
            // Get paths
            Set<String> paths = mappingInfo.getPatternValues();
            
            // Get controller and method names
            String controllerName = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            
            endpointInfo.put("httpMethods", httpMethods);
            endpointInfo.put("paths", paths);
            endpointInfo.put("controller", controllerName);
            endpointInfo.put("method", methodName);
            
            endpoints.add(endpointInfo);
        }

        // Add full model details to each summary
        for (Map<String, Object> endpoint : endpoints) {
            Set<String> paths = (Set<String>) endpoint.get("paths");
            Set<String> methods = (Set<String>) endpoint.get("httpMethods");

            for (String path : paths) {
                for (String method : methods) {
                    Map<String, Object> fullDetail = getEndpointDetails(path, method);
                    if (fullDetail != null) {
                        endpoint.put("parameters", fullDetail.get("parameters"));
                        endpoint.put("models", fullDetail.get("models"));
                    }
                }
            }
        }

        return endpoints;
    }
    
    /**
     * Get detailed information about a specific endpoint.
     * 
     * @param path The path of the endpoint
     * @param httpMethod The HTTP method of the endpoint
     * @return A map containing detailed information about the endpoint, or null if not found
     */
    public Map<String, Object> getEndpointDetails(String path, String httpMethod) {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            boolean pathMatches = mappingInfo.getPatternValues().contains(path);
            boolean methodMatches = mappingInfo.getMethodsCondition().getMethods().stream()
                    .anyMatch(method -> method.name().equalsIgnoreCase(httpMethod));

            if (pathMatches && methodMatches) {
                Map<String, Object> endpointDetails = new HashMap<>();

                endpointDetails.put("path", path);
                endpointDetails.put("httpMethod", httpMethod);
                endpointDetails.put("controller", handlerMethod.getBeanType().getSimpleName());
                endpointDetails.put("methodName", handlerMethod.getMethod().getName());

                Method method = handlerMethod.getMethod();
                List<Map<String, Object>> parameters = new ArrayList<>();
                Map<String, Object> models = new HashMap<>();

                for (Parameter parameter : method.getParameters()) {
                    Map<String, Object> paramInfo = new HashMap<>();
                    String typeName = parameter.getType().getSimpleName();
                    String fullClassName = parameter.getType().getName();

                    paramInfo.put("name", parameter.getName());
                    paramInfo.put("type", typeName);

                    List<String> annotations = new ArrayList<>();
                    for (Annotation annotation : parameter.getAnnotations()) {
                        annotations.add(annotation.annotationType().getSimpleName());
                    }
                    paramInfo.put("annotations", annotations);
                    parameters.add(paramInfo);

                    // if it's a custom class, get its field info
                    if (!PRIMITIVE_OR_BUILTIN.contains(typeName)) {
                        Map<String, Object> modelInfo = getModelClassInfo(fullClassName);
                        if (modelInfo != null) {
                            models.put(typeName, modelInfo);
                        }
                    }
                }

                endpointDetails.put("parameters", parameters);
                endpointDetails.put("models", models);

                endpointDetails.put("returnType", method.getReturnType().getSimpleName());

                List<String> methodAnnotations = new ArrayList<>();
                for (Annotation annotation : method.getAnnotations()) {
                    methodAnnotations.add(annotation.annotationType().getSimpleName());
                }
                endpointDetails.put("methodAnnotations", methodAnnotations);

                return endpointDetails;
            }
        }

        return null;
    }


    /**
     * Get information about a model class.
     *
     * @param className The fully qualified name of the class
     * @return A map containing information about the class, or null if not found
     */
    public Map<String, Object> getModelClassInfo(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Map<String, Object> classInfo = new HashMap<>();

            // Basic class info
            classInfo.put("name", clazz.getSimpleName());
            classInfo.put("package", clazz.getPackage().getName());

            // Class annotations
            List<Map<String, Object>> classAnnotations = new ArrayList<>();
            for (Annotation annotation : clazz.getAnnotations()) {
                Map<String, Object> annotationInfo = new HashMap<>();
                annotationInfo.put("name", annotation.annotationType().getSimpleName());
                annotationInfo.put("type", annotation.annotationType().getName());
                classAnnotations.add(annotationInfo);
            }
            classInfo.put("annotations", classAnnotations);

            // Is it a JPA entity?
            boolean isEntity = Arrays.stream(clazz.getAnnotations())
                    .anyMatch(a -> a.annotationType().getSimpleName().equals("Entity"));
            classInfo.put("isEntity", isEntity);

            // Fields
            List<Map<String, Object>> fields = new ArrayList<>();
            for (Field field : clazz.getDeclaredFields()) {
                Map<String, Object> fieldInfo = new HashMap<>();
                fieldInfo.put("name", field.getName());
                fieldInfo.put("type", field.getType().getSimpleName());

                // Field annotations
                List<Map<String, Object>> fieldAnnotations = new ArrayList<>();
                for (Annotation annotation : field.getAnnotations()) {
                    Map<String, Object> annotationInfo = new HashMap<>();
                    annotationInfo.put("name", annotation.annotationType().getSimpleName());
                    annotationInfo.put("type", annotation.annotationType().getName());
                    fieldAnnotations.add(annotationInfo);
                }
                fieldInfo.put("annotations", fieldAnnotations);

                // Is it a primary key?
                boolean isPrimaryKey = Arrays.stream(field.getAnnotations())
                        .anyMatch(a -> a.annotationType().getSimpleName().equals("Id"));
                fieldInfo.put("isPrimaryKey", isPrimaryKey);

                // Is it a foreign key?
                boolean isForeignKey = Arrays.stream(field.getAnnotations())
                        .anyMatch(a -> {
                            String name = a.annotationType().getSimpleName();
                            return name.equals("ManyToOne") || name.equals("OneToOne") ||
                                   name.equals("JoinColumn");
                        });
                fieldInfo.put("isForeignKey", isForeignKey);

                fields.add(fieldInfo);
            }
            classInfo.put("fields", fields);

            // Methods (excluding getters and setters for brevity)
            List<Map<String, Object>> methods = new ArrayList<>();
            for (Method method : clazz.getDeclaredMethods()) {
                // Skip getters and setters
                if (method.getName().startsWith("get") || method.getName().startsWith("set")) {
                    continue;
                }

                Map<String, Object> methodInfo = new HashMap<>();
                methodInfo.put("name", method.getName());
                methodInfo.put("returnType", method.getReturnType().getSimpleName());

                // Method parameters
                List<Map<String, Object>> methodParams = new ArrayList<>();
                for (Parameter param : method.getParameters()) {
                    Map<String, Object> paramInfo = new HashMap<>();
                    paramInfo.put("name", param.getName());
                    paramInfo.put("type", param.getType().getSimpleName());
                    methodParams.add(paramInfo);
                }
                methodInfo.put("parameters", methodParams);

                methods.add(methodInfo);
            }
            classInfo.put("methods", methods);

            return classInfo;
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", className, e);
            return null;
        }
    }

    /**
     * Get a list of all model classes in the specified package.
     *
     * @param packageName The package name to search in (e.g., "lacosmetics.planta.lacmanufacture.model")
     * @return A list of class names
     */
    public List<String> getModelClasses(String packageName) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(packageName)
                        .addScanners(Scanners.TypesAnnotated, SubTypes)
        );

        Set<Class<?>> classes = reflections.get(SubTypes.of(Object.class).asClass());

        return classes.stream()
                .map(Class::getName)
                .filter(name -> name.startsWith(packageName))
                .collect(Collectors.toList());
    }



}