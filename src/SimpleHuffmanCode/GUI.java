package SimpleHuffmanCode;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import sun.security.util.BitArray;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private void doCode() throws IOException {
        CodeClass coder = new CodeClass(firstFile, secondFile);
        byte[] a = coder.getFileInputStream().readAllBytes();
        coder.getFileInputStream().close();
        printInputFileLength(a);

        coder.setMap(coder.doCOdeSymbolsMap(a));
        List<Boolean> codeList = new ArrayList<>(a.length);
        for (byte anArray : a) {
            for (Map.Entry newMap : coder.getMap().entrySet()) {
                byte symbol = (byte) newMap.getKey();
                if (symbol == anArray) {
                    BitArray bitArray = (BitArray) newMap.getValue();
                    boolean[] tmpCodeBool = bitArray.toBooleanArray();
                    for (boolean aTmpCodeBool : tmpCodeBool) codeList.add(codeList.size(), aTmpCodeBool);
                }
            }
        }
        byte count = 0;
        while (codeList.size() % 8 != 0) {
            codeList.add(false);
            count++;
        }
        boolean[] newTmpCodeBool = new boolean[codeList.size()];
        double k = newTmpCodeBool.length;
        double s = 1.0;
        for (int i = 0; i < codeList.size(); i++) {
            newTmpCodeBool[i] = codeList.get(i);
            double k1 = (s / k) * 100.0;
            System.out.println(k1);
            s++;
        }
        BitArray bitArray = new BitArray(newTmpCodeBool);
        byte[] otherByteReturnArray = bitArray.toByteArray();
        if (count != 0) {
            otherByteReturnArray = Arrays.copyOf(otherByteReturnArray, otherByteReturnArray.length + 1);
            otherByteReturnArray[otherByteReturnArray.length - 1] = count;
        } else {
            otherByteReturnArray = Arrays.copyOf(otherByteReturnArray, otherByteReturnArray.length + 1);
            otherByteReturnArray[otherByteReturnArray.length - 1] = 0;
        }
        coder.getFileOutputStream().write(otherByteReturnArray);
        coder.getFileOutputStream().close();
        printCodedLength(otherByteReturnArray);
        printCoderDone();
        printPercentsOfCompression(a, otherByteReturnArray);
    }

    @FXML
    private void doDecode() throws IOException {
        CodeClass decoder = new CodeClass(firstFile, secondFile);
        byte[] array = decoder.getFileInputStream().readAllBytes();
        decoder.getFileInputStream().close();

        List<Byte> list = new ArrayList<>(array.length * 8);
        byte count = array[array.length - 1];
        array = Arrays.copyOf(array, array.length - 1);
        BitArray bitArray = new BitArray((array.length * 8) - count, array);
        boolean[] codeBoolArray = bitArray.toBooleanArray();
        int tmpCount = 0;
        int fff = 0;
        for (boolean aCodeBoolArray : codeBoolArray) {
            System.out.println(aCodeBoolArray + " " + fff);
            fff++;
            for (Map.Entry<Byte, BitArray> tmp : decoder.getMap().entrySet()) {
                boolean abc = true;
                BitArray tmpBitArray = tmp.getValue();
                boolean[] tmpBoolArray = tmpBitArray.toBooleanArray();
                for (int j = 0; j < tmpBoolArray.length; j++) {
                    if (tmpCount + tmpBoolArray.length > codeBoolArray.length) {
                        abc = false;
                        break;
                    }
                    if (tmpBoolArray[j] != codeBoolArray[j + tmpCount]) {
                        abc = false;
                        break;
                    }
                }
                if (abc) {
                    tmpCount += tmpBoolArray.length;
                    list.add(tmp.getKey());
                }
            }
        }
        byte[] result = new byte[list.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = list.get(i);

        decoder.getFileOutputStream().write(result);
        decoder.getFileOutputStream().close();
        printDecoderLength(result);
        printDecoderDone();
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
}
