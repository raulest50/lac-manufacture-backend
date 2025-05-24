# Scan Endpoints Script

This script scans all endpoints in the LA Cosmetics backend API and generates a comprehensive documentation file that can be used by frontend developers to make API requests.

## How to Run

1. Make sure you have JShell installed (comes with JDK 9+)
2. Navigate to the project root directory
3. Run the script using JShell:

```bash
jshell scanEndpoints.jsh
```

4. The script will generate a file called `endpoints-summary.txt` in the project root directory

## Output

The `endpoints-summary.txt` file contains:

1. Introduction with examples of how to use the API with Axios
2. List of all endpoints in the API, grouped by controller
3. For each endpoint:
   - HTTP method (GET, POST, PUT, DELETE)
   - Path
   - Method name
   - Return type
   - Parameters
   - Response entity fields (when available)
   - Request entity fields (when available)

## Use Cases

This script is particularly useful for:

1. Frontend developers who need to understand the backend API without access to the codebase
2. Documentation purposes
3. API testing
4. Generating API documentation for external teams

## Customization

If you need to customize the script:

- Change the output file name by modifying the `outFile` variable
- Adjust the regex patterns to match different code styles
- Add or remove information from the output by modifying the writer.write() calls

## Troubleshooting

If the script fails to run:

1. Make sure you're running it from the project root directory
2. Check that you have JDK 9 or higher installed
3. Verify that the source code follows standard Spring Boot conventions

## Related Scripts

- `scanEntities.jsh`: Scans all entities in the project and generates a summary file