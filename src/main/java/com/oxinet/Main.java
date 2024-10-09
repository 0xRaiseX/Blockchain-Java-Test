package com.oxinet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.PublicKey;
import java.security.Signature;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.KeyFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// class DataReceiver implements Runnable {
//     @Override
//     public void run() {
//         try {
//             while (true) {
//                 JSONObject message = Main.queue.take();
//                 System.out.println(message);
                
//             }
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//     }
// }

@SpringBootApplication
@EnableAsync
public class Main {
    // Map<User, List<WebSocketChannel>> userChannelMap = new HashMap<>();
    public static Blockchain blockchain = new Blockchain(); 
    public static BlockingQueue<JSONObject> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        // Thread springBootApplicationThread = new Thread(() -> {
            // SpringApplication.run(Main.class, args);
        // });
        SpringApplication.run(Main.class, args);
        // Thread secondThread = new Thread(new DataReceiver());

        // springBootApplicationThread.start();
        // secondThread.start();
    }
}


class Data {

    public static boolean verifySignature(String data, String publicKeyStr, String signatureStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);

            KeyFactory keyFactory = KeyFactory.getInstance("EC"); // ECDSA ключи
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Signature signature = Signature.getInstance("SHA256withECDSA");

            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String publicKeyToString(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
    public static String privateKeyToString(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public static String CurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");    
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    
}
@Service
class MyService {

    @Async
    public static CompletableFuture<String> performAsyncTask(String message, String header) {
        CompletableFuture<String> future = new CompletableFuture<>();
        String result;
        try {
            JSONObject data = new JSONObject(message);
            String PublicKeySender;
            String PublicKeyRecipient;
            double amount_transaction;

            try {
                PublicKeySender = data.getString("Sender");
            } catch (JSONException e) {
                result = "{\"msg\":\"Sender field is empty\",\"status\":\"Error\"}";
                future.complete(result);
                return future;
            }

            try {
                PublicKeyRecipient = data.getString("Recipient");
            } catch (JSONException e) {
                result = "{\"msg\":\"Recipient field is empty\",\"status\":\"Error\"}";
                future.complete(result);
                return future;
            }

            try {
                amount_transaction = data.getDouble("amount");
            } catch (JSONException e) {
                result = "{\"msg\":\"Amount field is empty\",\"status\":\"Error\"}";
                future.complete(result);
                return future;
            }

            boolean flag = Data.verifySignature(message, PublicKeySender, header);
            System.out.println(Data.CurrentDateTime() + "  OxiNet INFO:  Transaction signature OK");

            if (flag) {
                Main.blockchain.addTransaction(PublicKeySender, PublicKeyRecipient, amount_transaction);
                result = "{\"msg\":\"Transaction add OK\",\"status\":\"OK\"}";
                future.complete(result);
                return future;
            }
            else {
                result = "{\"msg\":\"Signature Error\",\"status\":\"Error\"}";
                future.complete(result);
                return future;
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}


// class MyWebSocketHandler implements WebSocketHandler {

//     @Override
//     public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//         // Реализация после успешного установления соединения
//     }

//     @Override
//     public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//         // Реализация после закрытия соединения
//     }

//     @Override
//     public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//         // Реализация обработки ошибок передачи
//     }


//     @Override
//     public boolean supportsPartialMessages() {
//         return false; // Измените на true, если поддерживаете частичные сообщения
//     }

//     @Override
//     public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//         // throw new UnsupportedOperationException("Unimplemented method 'handleMessage'");
//         if (message instanceof TextMessage) {
//             TextMessage textMessage = (TextMessage) message;
//             String payload = textMessage.getPayload();

//             if (payload.equals("subscribe")) {
//                 // session.getAttributes().put("subscribedChannels", subscribedChannels);
//             }
//         }
//     } 

// @Configuration
// @EnableWebSocketMessageBroker
// class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//     @Override
//     public void configureMessageBroker(MessageBrokerRegistry config) {
//         config.enableSimpleBroker("/topic", "/queue");
//         config.setApplicationDestinationPrefixes("/app");
//     }

//     @Override
//     public void registerStompEndpoints(StompEndpointRegistry registry) {
//         registry
//                 .addEndpoint("/ws")
//                 .withSockJS();
//     }
   
// }


// @Component
// class WelcomeMessageEventListener {

//     @EventListener
//     public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//         SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
//         System.out.println("НОВОЕ ПОДКЛЮЧЕНИЕ");
//         System.out.println(headers);
//          // Получаем информацию о пользователе (например, имя пользователя)
//         String username = headers.getUser().getName();

//         // Отправляем приветственное сообщение пользователю
//         String welcomeMessage = "Добро пожаловать, " + username + "!";
//         sendWelcomeMessageToUser(username, welcomeMessage);
//     }

//     @SendToUser("/queue/welcome") // Указываем, куда отправить сообщение пользователю
//     public String sendWelcomeMessageToUser(String username, String message) {
//         return message;
//     }
// }


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Конфигурируем брокер сообщений с каналом "/topic"
        config.setApplicationDestinationPrefixes("/app"); // Префикс для обработки сообщений от клиентов
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").withSockJS(); // Регистрируем конечную точку WebSocket
    }
}
@Controller
class WebSocketController {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Извлекаем информацию о сессии WebSocket
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());

        // Получаем идентификатор сессии пользователя
        String sessionId = headers.getSessionId();

        // Отправляем сообщение о новом подключении
        System.out.println("Новое подключение. Session ID: " + sessionId);
    }
}
// @Controller
// class WebSocketController {

//     private final SimpMessageSendingOperations messagingTemplate;

//     public WebSocketController(SimpMessageSendingOperations messagingTemplate) {
//         this.messagingTemplate = messagingTemplate;
//     }

//     @MessageMapping("/private")
//     public void subscribeToChannels(@Payload String json) {
//         System.out.println(json);
//         // Распарсите JSON и определите каналы, на которые пользователь хочет подписаться
//         // Добавьте пользователя к соответствующим каналам
//         // Отправьте пользователю подтверждение подписки, если необходимо
//     }
// }

@RestController
class PostController {

    @PostMapping("add/transaction")
    @Async   
    public CompletableFuture<String> handleAsyncPostRequest(@RequestBody String message, @RequestHeader("X-Signature") String headerValue) {
        return MyService.performAsyncTask(message, headerValue);
    }

    @GetMapping("/keys")
    public String getKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
            keyPairGenerator.initialize(ecSpec);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();

            PublicKey publicKey = keyPair.getPublic();
            return "{\"msg\":\"Generation complited\",\"status\":\"OK\",\"publicKey\":\"" + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\",\"privateKey\":\"" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\"}";
        } catch (Exception e) {

            return "{\"msg\":\"Error\",\"status\":\"Error\"}";
        }
    }
    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
        
    }

    @GetMapping("/stream/{blockNumber}")
    public ResponseEntity<StreamingResponseBody> streamBlock(@PathVariable int blockNumber) {
        File blockchainDirectory = new File(Config.directoryPath);
        String blockFileName = "block" + blockNumber + ".dat";

        File blockFile = new File(blockchainDirectory, blockFileName);

        if (blockFile.exists() && blockFile.isFile()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", blockFileName);

            StreamingResponseBody responseBody = outputStream -> {
                try (InputStream fileInputStream = new FileInputStream(blockFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
