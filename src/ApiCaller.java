import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ApiCaller {

    private final String url;
    private String method;
    private String authorization;
    private int timeout;
    private int connectionTimeout;
    private String contentType;
    private String body;

    public ApiCaller(String url, String method, String authorization, int timeout, int connectionTimeout, String contentType, String body) {
        this.url = url;
        this.method = method;
        this.authorization = authorization;
        this.timeout = timeout;
        this.connectionTimeout = connectionTimeout;
        this.contentType = contentType;
        this.body = body;
    }

    public String callApi() throws IOException {
        URL urlObj = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        connection.setRequestMethod(this.method);
        connection.setConnectTimeout(this.connectionTimeout);
        connection.setReadTimeout(this.timeout);
        connection.setRequestProperty("Content-Type", this.contentType);
        connection.setDoInput(true);
//        connection.setDoOutput(true);

        if (authorization != null && !authorization.isEmpty()) {
            connection.setRequestProperty("Authorization", this.authorization);
        }

        if (this.method.equals("POST") || this.method.equals("PUT")) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = this.body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int status = connection.getResponseCode();
        BufferedReader br;
        if (status > 299) {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        connection.disconnect();

        return response.toString();
    }

    public static void main(String[] args) {
        try {
            // Thay đổi giá trị đầu vào phù hợp với API của bạn
            ApiCaller apiCaller = new ApiCaller(
                    "https://abc.com",
                    "POST",
                    "",
                    9000,
                    10000,
                    "application/json",
                    "{\"key\":\"value\"}"
            );
            String response = apiCaller.callApi();
            System.out.println("Response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
