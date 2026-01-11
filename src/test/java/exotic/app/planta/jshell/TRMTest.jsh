// TRMTest.jsh
//
// JShell script para probar la consulta a la API de datos.gov.co
// Ejecutar con:
//   jshell src\test\java\lacosmetics\planta\lacmanufacture\jshell\TRMTest.jsh
//

// 1) Imports necesarios
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

// 2) Variables de configuración
String appToken = "967n4ffx7pkpp9jcnqedkopwa";
String baseUrl = "https://www.datos.gov.co/resource/32sa-8pi3.json";

// 3) Construir URL con parámetros codificados
String url = baseUrl + "?$limit=1&$order=vigenciadesde+DESC";
System.out.println("URL de consulta: " + url);

// 4) Crear el cliente HTTP usando variables intermedias
var builder = HttpClient.newBuilder();
builder = builder.version(HttpClient.Version.HTTP_2);
builder = builder.connectTimeout(Duration.ofSeconds(10));
HttpClient client = builder.build();

// 5) Crear la petición con header X-App-Token usando variables intermedias
var reqBuilder = HttpRequest.newBuilder();
reqBuilder = reqBuilder.uri(URI.create(url));
reqBuilder = reqBuilder.header("X-App-Token", appToken);
reqBuilder = reqBuilder.GET();
HttpRequest request = reqBuilder.build();

// 6) Enviar la petición y procesar la respuesta
try {
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    System.out.println("Código de estado: " + response.statusCode());
    System.out.println("Cuerpo de la respuesta:\n" + response.body());
} catch (Exception e) {
    System.err.println("Error al realizar la consulta: " + e.getMessage());
    e.printStackTrace();
}

// 7) Prueba con URL sin codificar (espacio real)
String urlBad = baseUrl + "?$limit=1&$order=vigenciadesde DESC";
System.out.println("\nURL sin codificar: " + urlBad);
try {
    var badReqBuilder = HttpRequest.newBuilder();
    badReqBuilder = badReqBuilder.uri(URI.create(urlBad));
    badReqBuilder = badReqBuilder.header("X-App-Token", appToken);
    badReqBuilder = badReqBuilder.GET();
    HttpRequest badReq = badReqBuilder.build();

    HttpResponse<String> badResp = client.send(badReq, BodyHandlers.ofString());
    System.out.println("Bad status: " + badResp.statusCode());
    System.out.println("Bad body:\n" + badResp.body());
} catch (Exception e) {
    System.err.println("Error con espacio sin codificar: " + e.getMessage());
}

// 8) Salir de jshell
/exit
