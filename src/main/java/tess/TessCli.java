package tess;

import java.util.logging.Logger;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.tesseract.global.tesseract;


@Properties(inherit = tesseract.class, value = @Platform(executable = "tesseract"))
public class TessCli {

    private static final Logger LOG = Logger.getLogger(TessApi.class.getName());


    public static void main(String[] args) throws Exception {
        System.out.println(">>> Starting");

        String tesseract = Loader.load(org.bytedeco.tesseract.program.tesseract.class);
        ProcessBuilder pb = new ProcessBuilder(tesseract, "sample01s.png", "sample01s.png", "--oem 1", "-l chi_tra+eng");
        Process tessProc = pb.inheritIO().start();
        tessProc.waitFor();

        System.out.println(">>> End");
    }
}
