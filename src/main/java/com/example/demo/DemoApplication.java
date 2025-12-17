@RestController
@SpringBootApplication
public class DemoApplication {

    @GetMapping("/")
    public String home() {
        return "CI/CD Pipeline Success";
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

