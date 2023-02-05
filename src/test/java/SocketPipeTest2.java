import org.junit.Test;

public class SocketPipeTest2 {

    @Test
    public void pipeTest() {
        SocketPipe pipe = new SocketPipe();
        pipe
                .stream()
                .zipWithIndex()
                .foreach(t -> {
                    System.out.println(t);
                    return "";
                });





    }
}
