package j262.webflux.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.http.server.HttpServer;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class DemoApplication {

    @Value("${server.port:8080}")
    private int port = 8080;

    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                DemoApplication.class)) {
            context.getBean(HttpServer.class).bindNow().onDispose().block();
        }
    }

    @Profile("default")
    @Bean
    public HttpServer nettyHttpServer(ApplicationContext context) {

        HandlerFunction hello = request -> ServerResponse.ok().body(fromObject("Hello"));
        HandlerFunction posts = request -> ServerResponse.ok().body(fromObject("This is the posts..."));
        RouterFunction<ServerResponse> router = route().
                GET("/", hello).
                GET("/posts", posts).
                build();

        HttpHandler httpHandler = RouterFunctions.toHttpHandler(router);

        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer httpServer = HttpServer.create().host("localhost").port(this.port);
        return httpServer.handle(adapter);
    }

}

