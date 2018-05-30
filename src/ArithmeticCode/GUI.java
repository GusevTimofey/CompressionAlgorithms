package ArithmeticCode;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GUI extends Component {
    private String firstFile;
    private String secondFile;

    @FXML
    private TextField InputFile;
    @FXML
    private TextField OutputFile;
    @FXML
    private TextField InputFieldLength;
    @FXML
    private TextField percentsOfCompression;
    @FXML
    private TextField codedLength;
    @FXML
    private TextField coderDone;
    @FXML
    private TextField decodedLength;
    @FXML
    private TextField decoderDone;

    @FXML
    private void printFirsFilePath(String str) {
        InputFile.setText(str);
    }

    @FXML
    private void PrintSecondFilePath(String str) {
        OutputFile.setText(str);
    }

    @FXML
    private File chooseFirstFile() {
        FileChooser fileChooser = new FileChooser();
        File inputDataFile = fileChooser.showOpenDialog(null);
        printFirsFilePath(inputDataFile.getPath());
        firstFile = inputDataFile.getPath();
        return inputDataFile;
    }

    @FXML
    private File chooseSecondFile() {
        FileChooser fileChooser = new FileChooser();
        File inputDataFile = fileChooser.showOpenDialog(null);
        PrintSecondFilePath(inputDataFile.getPath());
        secondFile = inputDataFile.getPath();
        return inputDataFile;
    }

    @FXML
    private void printDecoderLength(byte[] a) {
        decodedLength.setText(String.valueOf(a.length) + " bytes");
    }

    @FXML
    private void printDecoderDone() {
        decoderDone.setText("End of decoding");
    }

    @FXML
    private void printCodedLength(byte[] a) {
        codedLength.setText(String.valueOf(a.length) + " bytes");
    }

    @FXML
    private void printCoderDone() {
        coderDone.setText("End of coding");
    }

    @FXML
    private void printInputFileLength(byte[] a) {
        InputFieldLength.setText(String.valueOf(a.length) + " bytes");
    }

    @FXML
    private void printPercentsOfCompression(byte[] a, byte[] b) {
        percentsOfCompression.setText(String.valueOf(((a.length - b.length) * 100) / a.length) + "% percents");
    }

    @FXML
    private void doCode() throws IOException {
        ArithmeticCode coder = new ArithmeticCode(firstFile, secondFile);
        byte[] a = coder.getFileInputStream().readAllBytes();
        coder.getFileInputStream().close();
        printInputFileLength(a);

        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++)
            b[i] = Byte.toUnsignedInt(a[i]);

        coder.setEncodedBits(new ArrayList<>());
        coder.setBorder_L(0);
        coder.setBorder_H((1 << coder.getBITS()) - 1);
        coder.setAdd_bits(0);
        coder.setFreq(coder.build_tree(coder.getEND() + 1));
        for (int k : b)
            coder.code_char(k);
        coder.code_char(coder.getEND());
        coder.outputBit(true);

        int[] result = new int[coder.getEncodedBits().size()];
        for (int i = 0; i < result.length; i++)
            result[i] = coder.getEncodedBits().get(i) ? 1 : 0;
        byte[] encodedBytes = coder.encodeToByteArray(result);

        coder.getFileOutputStream().write(encodedBytes);
        coder.getFileOutputStream().close();
        printCodedLength(encodedBytes);
        printCoderDone();
        printPercentsOfCompression(a, encodedBytes);
    }

    @FXML
    private void doDecode() throws IOException {
        ArithmeticCode decoder = new ArithmeticCode(firstFile, secondFile);
        byte[] a = decoder.getFileInputStream().readAllBytes();
        decoder.getFileInputStream().close();
        int[] b = decoder.encodeToBitArray(a);

        decoder.setFreq(decoder.build_tree(decoder.getEND() + 1));
        decoder.setBits(b);
        decoder.setDecodedBytes(new ArrayList<>());
        decoder.setValue(0);
        int[] bits = decoder.getBits();
        for (int i = 0; i < decoder.getBITS(); i++)
            decoder.setValue((decoder.getValue() << 1) + (i < bits.length ? bits[i] : 0));

        decoder.setBorder_L(0);
        decoder.setBorder_H((1 << decoder.getBITS()) - 1);
        while (true) {
            int c = decoder.decode_char();
            if (c == decoder.getEND())
                break;
            decoder.getDecodedBytes().add(c);
            decoder.increment(decoder.getFreq(), c);
        }

        int[] bytes = new int[decoder.getDecodedBytes().size()];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = decoder.getDecodedBytes().get(i);

        FileOutputStream fileOutputStream = decoder.getFileOutputStream();
        for (int i = 0; i < bytes.length; i++)
            fileOutputStream.write(bytes[i]);

        fileOutputStream.close();
        decoder.getFileOutputStream().close();
//        printDecoderLength(bytes);
        printDecoderDone();

    }

}
