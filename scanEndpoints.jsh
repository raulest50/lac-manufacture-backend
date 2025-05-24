import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;
import java.util.stream.*;
import java.util.*;

// Define source and output paths
var srcDir = Paths.get("src/main/java");
var outFile = Paths.get("endpoints-summary.txt");

// Prepare writer (creates or truncates file)
var writer = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8);

// Write header with instructions for frontend developers
writer.write("# LA Cosmetics Backend API Documentation\n\n");
writer.write("This document contains information about all endpoints in the LA Cosmetics backend API.\n");
writer.write("It is intended for frontend developers who need to make API requests using Axios.\n\n");
writer.write("## How to use this document with Axios\n\n");
writer.write("For each endpoint, you'll find the following information:\n");
writer.write("- HTTP method (GET, POST, PUT, DELETE)\n");
writer.write("- Path\n");
writer.write("- Method name\n");
writer.write("- Return type\n");
writer.write("- Parameters\n");
writer.write("- Related entity fields\n\n");
writer.write("### Example Axios request\n\n");
writer.write("```javascript\n");
writer.write("// For a GET request\n");
writer.write("axios.get('/api/endpoint', { params: { param1: 'value1' } })\n");
writer.write("  .then(response => {\n");
writer.write("    console.log(response.data);\n");
writer.write("  })\n");
writer.write("  .catch(error => {\n");
writer.write("    console.error(error);\n");
writer.write("  });\n\n");
writer.write("// For a POST request with JSON body\n");
writer.write("axios.post('/api/endpoint', {\n");
writer.write("  field1: 'value1',\n");
writer.write("  field2: 'value2'\n");
writer.write("})\n");
writer.write("  .then(response => {\n");
writer.write("    console.log(response.data);\n");
writer.write("  })\n");
writer.write("  .catch(error => {\n");
writer.write("    console.error(error);\n");
writer.write("  });\n\n");
writer.write("// For a multipart/form-data request\n");
writer.write("const formData = new FormData();\n");
writer.write("formData.append('file', fileObject);\n");
writer.write("formData.append('jsonData', JSON.stringify({ field1: 'value1' }));\n\n");
writer.write("axios.post('/api/endpoint', formData, {\n");
writer.write("  headers: {\n");
writer.write("    'Content-Type': 'multipart/form-data'\n");
writer.write("  }\n");
writer.write("})\n");
writer.write("  .then(response => {\n");
writer.write("    console.log(response.data);\n");
writer.write("  })\n");
writer.write("  .catch(error => {\n");
writer.write("    console.error(error);\n");
writer.write("  });\n");
writer.write("```\n\n");
writer.write("## Endpoints\n\n");

// Map to store entity information for reference
Map<String, List<String>> entityInfo = new HashMap<>();

// First pass: collect all entity information
try (var stream = Files.walk(srcDir)) {
    stream.filter(p -> p.toString().endsWith(".java"))
          .forEach(path -> {
        try {
            var lines = Files.readAllLines(path);
            // Detect @Entity in source
            if (lines.stream().anyMatch(l -> l.contains("@Entity"))) {
                var className = "";
                var fields = new ArrayList<String>();

                // Patterns for class and field declarations
                var classPat = Pattern.compile("\\bclass\\s+(\\w+)");
                var fieldPat = Pattern.compile("\\b(private|protected|public)\\s+([\\w<>\\[\\]]+)\\s+(\\w+)\\s*;");

                for (var line : lines) {
                    var cm = classPat.matcher(line);
                    if (cm.find()) className = cm.group(1);

                    var fm = fieldPat.matcher(line.trim());
                    if (fm.find()) {
                        fields.add("  • " + fm.group(3) + " : " + fm.group(2));
                    }
                }

                if (!className.isEmpty()) {
                    entityInfo.put(className, fields);
                }
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
    });
}

// Second pass: scan for endpoints
try (var stream = Files.walk(srcDir)) {
    stream.filter(p -> p.toString().endsWith("Resource.java"))
          .forEach(path -> {
        try {
            var lines = Files.readAllLines(path);

            // Check if it's a REST controller
            if (lines.stream().anyMatch(l -> l.contains("@RestController"))) {
                writer.write("=== " + srcDir.relativize(path) + " ===\n");

                // Extract class name
                var className = "";
                var classPat = Pattern.compile("\\bclass\\s+(\\w+)");
                for (var line : lines) {
                    var cm = classPat.matcher(line);
                    if (cm.find()) {
                        className = cm.group(1);
                        writer.write("Controller: " + className + "\n");
                        break;
                    }
                }

                // Extract base path
                var basePath = "";
                var basePathPat = Pattern.compile("@RequestMapping\\(\"([^\"]+)\"\\)");
                for (var line : lines) {
                    var bpm = basePathPat.matcher(line);
                    if (bpm.find()) {
                        basePath = bpm.group(1);
                        writer.write("Base Path: " + basePath + "\n\n");
                        break;
                    }
                }

                // Extract endpoints
                var endpointPat = Pattern.compile("@(Get|Post|Put|Delete|Patch)Mapping(?:\\((?:value\\s*=\\s*)?\"([^\"]+)\"(?:,\\s*consumes\\s*=\\s*([^\\)]+))?\\))?");
                var methodPat = Pattern.compile("public\\s+(?:ResponseEntity<([^>]+)>|([^\\s<]+))\\s+(\\w+)\\s*\\(([^\\)]*)\\)");
                var paramPat = Pattern.compile("@(?:RequestParam|PathVariable|RequestBody|RequestPart)(?:\\([^\\)]*\\))?\\s+(?:([^\\s]+)\\s+)?([^\\s,]+)");

                for (int i = 0; i < lines.size(); i++) {
                    var line = lines.get(i);
                    var em = endpointPat.matcher(line);

                    if (em.find()) {
                        var httpMethod = em.group(1);
                        var endpointPath = em.group(2) != null ? em.group(2) : "";
                        var consumes = em.group(3);

                        // Look for method signature
                        String methodSignature = "";
                        String returnType = "";
                        String methodName = "";
                        List<String> parameters = new ArrayList<>();

                        // Look ahead for method signature
                        for (int j = i; j < Math.min(i + 10, lines.size()); j++) {
                            var mm = methodPat.matcher(lines.get(j));
                            if (mm.find()) {
                                // Group 1 is ResponseEntity<T>, Group 2 is other return type
                                returnType = mm.group(1) != null ? mm.group(1) : mm.group(2);
                                methodName = mm.group(3);
                                methodSignature = mm.group(4);
                                break;
                            }
                        }

                        // Extract parameters
                        var paramMatcher = paramPat.matcher(methodSignature);
                        while (paramMatcher.find()) {
                            String paramType = paramMatcher.group(1);
                            String paramName = paramMatcher.group(2);
                            if (paramType != null) {
                                parameters.add(paramType + " " + paramName);
                            } else {
                                parameters.add(paramName);
                            }
                        }

                        // Write endpoint information
                        writer.write("Endpoint: " + httpMethod + " " + basePath + endpointPath + "\n");
                        writer.write("  Method: " + methodName + "\n");
                        writer.write("  Returns: " + returnType + "\n");

                        if (consumes != null) {
                            writer.write("  Consumes: " + consumes + "\n");
                        }

                        if (!parameters.isEmpty()) {
                            writer.write("  Parameters:\n");
                            for (var param : parameters) {
                                writer.write("    - " + param + "\n");
                            }
                        }

                        // Add related entity information if available
                        Set<String> processedEntities = new HashSet<>();

                        // Extract entity from return type
                        if (returnType != null && !returnType.isEmpty()) {
                            // Handle various return type patterns
                            String entityName = returnType;
                            if (entityName.contains("ResponseEntity<")) {
                                entityName = entityName.replace("ResponseEntity<", "").replace(">", "");
                            }
                            if (entityName.contains("List<")) {
                                entityName = entityName.replace("List<", "").replace(">", "");
                            }
                            if (entityName.contains("Page<")) {
                                entityName = entityName.replace("Page<", "").replace(">", "");
                            }
                            entityName = entityName.trim();

                            if (entityInfo.containsKey(entityName)) {
                                writer.write("  Response Entity: " + entityName + "\n");
                                writer.write("  Response Fields:\n");
                                for (var field : entityInfo.get(entityName)) {
                                    writer.write("    " + field + "\n");
                                }
                                processedEntities.add(entityName);
                            }
                        }

                        // Extract entities from parameters
                        for (String param : parameters) {
                            // Try to extract entity name from parameter type
                            String[] parts = param.split(" ");
                            if (parts.length > 1) {
                                String paramType = parts[0];
                                // Remove generics if present
                                if (paramType.contains("<")) {
                                    paramType = paramType.substring(0, paramType.indexOf("<"));
                                }

                                if (entityInfo.containsKey(paramType) && !processedEntities.contains(paramType)) {
                                    writer.write("  Request Entity: " + paramType + "\n");
                                    writer.write("  Request Fields:\n");
                                    for (var field : entityInfo.get(paramType)) {
                                        writer.write("    " + field + "\n");
                                    }
                                    processedEntities.add(paramType);
                                }
                            }
                        }

                        writer.write("\n");
                    }
                }

                writer.write("\n");
            }
        } catch (Exception e) {
            // Log the error for debugging
            try {
                writer.write("Error processing " + path + ": " + e.getMessage() + "\n");
            } catch (IOException ioe) {
                System.err.println("Error writing to file: " + ioe.getMessage());
            }
        }
    });
} finally {
    writer.close();  // Close writer to flush output
}

System.out.println("Done. See endpoints-summary.txt");

/exit
