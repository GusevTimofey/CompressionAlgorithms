package ArithmeticCode;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GUI extends Component {
    private String firstFile;
    private String secondFile;
    private ArithmeticCode arithmeticCode = new ArithmeticCode();

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
    private void printDecodedFileLength(int[] a) {
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
        FileInputStream fileInputStream = new FileInputStream(firstFile);
        FileOutputStream fileOutputStream = new FileOutputStream(secondFile);

        byte[] inputData = fileInputStream.readAllBytes();
        fileInputStream.close();
        printInputFileLength(inputData);

        int[] b = new int[inputData.length];
        for (int i = 0; i < inputData.length; i++)
            b[i] = Byte.toUnsignedInt(inputData[i]);

        int[] encodedBits = arithmeticCode.encode(b);
        byte[] encodedBytes = arithmeticCode.encodeToByteArray(encodedBits);

        fileOutputStream.write(encodedBytes);
        fileOutputStream.close();
        printCodedLength(encodedBytes);
        printCoderDone();
        printPercentsOfCompression(inputData, encodedBytes);
    }

    @FXML
    private void doDecode() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(firstFile);
        FileOutputStream fileOutputStream = new FileOutputStream(secondFile);

        byte[] inputData = fileInputStream.readAllBytes();
        fileInputStream.close();
        int[] b = arithmeticCode.encodeToBitArray(inputData);
        int[] result = arithmeticCode.decode(b);

        for (int i = 0; i < result.length; i++)
            fileOutputStream.write(result[i]);

        fileOutputStream.close();

        printDecodedFileLength(result);
        printDecoderDone();

    }

}
