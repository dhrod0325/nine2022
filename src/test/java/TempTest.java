import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TempTest {
    public static void main(String[] args) {
        List<Test> list = new ArrayList<>();
        list.add(new Test("test1", 1));
        list.add(new Test("test2", 2));

        list.stream().sorted().collect(Collectors.toList()).forEach(test -> System.out.println(test.getName()));
    }

    public static class Test implements Comparable<Test> {
        private String name;
        private int age;

        public Test(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public int compareTo(Test o) {
            return getAge() - o.getAge();
        }
    }
}
