package com.oxinet;
// import javax.websocket.*;
// import javax.websocket.server.ServerEndpoint;
// import java.io.IOException;
// import org.eclipse.jetty.server.Server;
// import org.eclipse.jetty.servlet.ServletContextHandler;
// import org.eclipse.jetty.servlet.ServletHolder;

// public class Server {

//     public static void main(String[] args) throws Exception {
//         // Создаем сервер Jetty
//         Server server = new Server(8080); // Порт, на котором будет работать сервер

//         // Создаем контекст сервлетов
//         ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//         context.setContextPath("/");

//         // Регистрируем наш WebSocket-сервер в контексте
//         context.addServlet(new ServletHolder(new SimpleWebSocketServer()), "/websocket");

//         // Добавляем контекст в сервер Jetty
//         server.setHandler(context);

//         // Запускаем сервер Jetty
//         server.start();
//         server.join(); // Ожидаем завершения работы сервера
//     }
// }

// @ServerEndpoint("/websocket")
// class Start {

//     @OnOpen
//     public void onOpen(Session session) {
//         System.out.println("Новое соединение установлено: " + session.getId());
//         try {
//             session.getBasicRemote().sendText("Добро пожаловать!");
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     @OnMessage
//     public void onMessage(String message, Session session) {
//         System.out.println("Получено сообщение от " + session.getId() + ": " + message);
//     }

//     @OnClose
//     public void onClose(Session session, CloseReason closeReason) {
//         System.out.println("Соединение закрыто: " + session.getId() + " Причина: " + closeReason);
//     }

//     @OnError
//     public void onError(Throwable throwable) {
//         System.err.println("Произошла ошибка: " + throwable.getMessage());
//     }
// }
