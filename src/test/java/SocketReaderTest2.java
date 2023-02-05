import org.junit.Test;

public class SocketReaderTest2 {

    @Test
    public void pipeTest() {
        SocketReader reader = new SocketReader();
        reader
                .stream()
                .map(line -> line)
                .zipWithIndex()
                .foreach(t -> {
                    System.out.println(t);
                    return null;
                });
    }
}
