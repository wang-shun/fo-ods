import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;

/**
 * Created by roman on 20/07/2017.
 */
public class SimpleMain {

    public static void main(String[] args) {
        System.out.println("Hello from SimpleMain");
        System.out.println("You said: " + Arrays.asList(args));
        System.out.println("Exiting now... bye!...");

        for (int i = 0; i < 200; i++) {
            byte[] bbb = Bytes.toBytes(i);
            dump(i, bbb);
        }
    }

    private static void dump(int i, byte[] bbb) {
        System.out.print(i + " = ");
        for (byte b: bbb) {
            System.out.print(conv(b) + ", ");
        }
        System.out.println();
    }

    public static short conv(byte b) {
        short res = b;
        if (res < 0) {
            res += 127;
        }
        return res;
    }
}
