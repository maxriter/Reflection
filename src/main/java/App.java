import model.Human;
import service.Json;

import java.time.LocalDate;


public class App {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        Human human=new Human("maxim","lysenko","run", LocalDate.of(1987, 10, 3));
        Json json=new Json();
        String str="{\"firstName\":\"maxim\",\"lastName\":\"lysenko\",\"hobby\":\"run\",\"birthDate\":\"1987-10-03\"}";
        System.out.println("To JSON: "+json.toJson(human));
        System.out.println("From JSON: "+json.fromJson(str, (Class) human.getClass()));
    }
}
