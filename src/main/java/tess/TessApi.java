package tess;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.tesseract.ETEXT_DESC;
import org.bytedeco.tesseract.TessBaseAPI;
import org.bytedeco.tesseract.global.tesseract;


/**
 * Launch steps:
 *
 * export TESSDATA_PREFIX=<PATH_TO_TRAINEDDATA_DIR> (e.g.: export TESSDATA_PREFIX=/tmp/tessdata)
 * mvn clean install exec:java
 */ 

public class TessApi {

    private static final Logger LOG = Logger.getLogger(TessApi.class.getName());


    public static void main(String[] args) {
        TessBaseAPI tessBaseApi = null;
        ETEXT_DESC tessMonitor = null;
        Mat imageMat = null;
        BytePointer outText = null;

        try (InputStream is = TessApi.class.getClassLoader().getResourceAsStream("sample01s.png")) {

            tessBaseApi = new TessBaseAPI();
            
            int initResult = tessBaseApi.Init(System.getenv("TESSDATA_PREFIX"), "chi_tra+eng");

            // TrainData listed below which contains "tessedit_load_sublangs" param gives the following message when used in conjunction with another language:
            //    Error opening data file /local/tessData/tessdata_best-4.1.0/.traineddata
            //    Please make sure the TESSDATA_PREFIX environment variable is set to your "tessdata" directory.
            //    Failed loading language ''
            // As reslt tessBaseApi.Init() crashes when glibc version is 2.28-216.el8 or later
            // aze
            // aze_cyrl
            // chi_sim
            // chi_tra
            // ell
            // jpn
            // srp
            // uzb
            // uzb_cyrl
            
            // TrainData listed below which contains "tessedit_load_sublangs" works fine, most likely because of the followign reasons:
            // ben           - works fine but in has param set as: tessedit_load_sublangs	eng            
            // mal           - works fine but in has param set as: tessedit_load_sublangs	eng            
            // srp_latn      - works fine seems because param is commented out: # tessedit_load_sublangs srp
            // tel           - works fine seems because param is commented out: #tessedit_load_sublangs	eng            
           
            if (initResult == 0) {
                LOG.info("TessAPI initialization SUCCESS with langs: " + tessBaseApi.GetInitLanguagesAsString().getString());
            } else {
                LOG.severe("TessAPI initialization FAILED, initCode=" + initResult);
            }

            tessBaseApi.SetPageSegMode(tesseract.PSM_AUTO);

            BufferedImage image = ImageIO.read(is);
            imageMat = Java2DFrameUtils.toMat(image);
            tessBaseApi.SetImage(imageMat.data().asBuffer(), imageMat.size().width(), imageMat.size().height(), imageMat.channels(), (int) imageMat.step1());

            tessMonitor = tesseract.TessMonitorCreate();
            tessBaseApi.Recognize(tessMonitor);

            outText = tessBaseApi.GetUTF8Text();
            System.out.println("OCR output:\n" + outText.getString());

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            close(imageMat);
            close(outText);
            close(tessMonitor);
            close(tessBaseApi);
        }
    }


    public static void close(Mat mat) {
        if (mat != null && !mat.isNull()) {
            mat.release();
            mat.deallocate();
            mat.close();
        }
    }


    public static void close(Pointer pointer) {
        if (pointer != null && !pointer.isNull()) {
            pointer.deallocate();
            pointer.close();
        }
    }


    public static void close(TessBaseAPI tessEngine) {
        if (tessEngine != null) {
            tessEngine.End();
            tessEngine.deallocate();
            tessEngine.close();
        }
    }


    public static void close(ETEXT_DESC tessMonitor) {
        if (tessMonitor != null) {
            tessMonitor.deallocate();
            tessMonitor.close();
        }
    }
}
