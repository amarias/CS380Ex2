import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.zip.CRC32;

public class Ex2Client {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("codebank.xyz", 38102);
			System.out.println("Connected to server");

			BufferedInputStream br = new BufferedInputStream(socket.getInputStream());
			PrintStream out = new PrintStream(socket.getOutputStream());

			byte completeByte;
			byte[] bytes = new byte[100];

			System.out.print("Received bytes:");
			for (int i = 0; i < bytes.length; i++) {
				if (i % 20 == 0)
					System.out.println();
				completeByte = (byte) br.read();
				completeByte <<= 4;
				completeByte = (byte) (completeByte | br.read());
				bytes[i++] = completeByte;
				System.out.print(String.format("%X", completeByte));
			}

			CRC32 crc32 = new CRC32();

			crc32.update(bytes);
			System.out.println(String.format("\nGenerated CRC32 code: %S", Long.toHexString(crc32.getValue())));

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeLong(crc32.getValue()); // writes long as 8 bytes
			byte[] longAsBytes = os.toByteArray();

			out.write(longAsBytes, 0, 4);
			out.write(longAsBytes, 4, 4);

			byte response;
			if ((response = (byte) br.read()) == 0x0)
				System.out.println("Response not good.");
			else
				System.out.println("Response good.");

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Disconnected from server.");

	}
}
