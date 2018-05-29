package AdaptiveHuffmanCode;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GUI extends Component {
    private String firstFile;
    private String secondFile;

    @FXML
    private TextField InputFile;
    @FXML
    private TextField OutputFile;
    @FXML
    private TextField coderDone;
    @FXML
    private TextField decoderDone;
    @FXML
    private TextField decodedLength;
    @FXML
    private TextField codedLength;
    @FXML
    private TextField InputFieldLength;
    @FXML
    private TextField percentsOfCompression;

    @FXML
    private void printInputFileLength(File file) {
        InputFieldLength.setText(String.valueOf(file.length() / 1024) + " bytes");
    }

    @FXML
    private void printPercentsOfCompression(File one, File two) {
        percentsOfCompression.setText(String.valueOf(((one.length() - two.length()) * 100) / one.length()) + "% percents");
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
    private void printFirsFilePath(String str) {
        InputFile.setText(str);
    }

    @FXML
    private void PrintSecondFilePath(String str) {
        OutputFile.setText(str);
    }

    @FXML
    private void printCodedLength(File file) {
        codedLength.setText(String.valueOf(file.length() / 1024) + " bytes");
    }

    @FXML
    private void printCoderDone() {
        coderDone.setText("End of coding");
    }

    @FXML
    private void doCode() throws IOException {
        AdaptiveCode coder = new AdaptiveCode(firstFile, secondFile, 100);
        Node node = coder.getRoot();
        StringBuilder stringBuilder = new StringBuilder();
        int a;
        printInputFileLength(new File(firstFile));
        while ((a = coder.getFileInputStream().read()) != -1) {
            node = coder.appendCode(node, a, stringBuilder);
            if (stringBuilder.length() > coder.MAX_BUFFER_SIZE)
                coder.writeOut(stringBuilder);
        }
        coder.writeOut(stringBuilder);
        if (stringBuilder.length() > 0) {
            char[] nodesCode = coder.buildCode(node).toCharArray();
            int i = 0;
            while (stringBuilder.length() < coder.bit && i < nodesCode.length) {
                stringBuilder.append(nodesCode[i]);
                i++;
            }
            while (stringBuilder.length() < coder.bit)
                stringBuilder.append("0");
            coder.getFileOutputStream().write(Integer.parseInt(stringBuilder.toString(), 2));
        }
        coder.getFileOutputStream().flush();
        printCodedLength(new File(secondFile));
        printCoderDone();
        printPercentsOfCompression(new File(firstFile), new File(secondFile));
    }

    @FXML
    private void doDecode() throws IOException {
        AdaptiveCode decoder = new AdaptiveCode(firstFile, secondFile, 100);
        Node currentNode = decoder.getRoot();
        Node node = decoder.getRoot();
        int c = 0;
        int ch = 0;
        StringBuilder stringBuilder = new StringBuilder();

        while (c != -1 && stringBuilder.length() < decoder.MAX_BUFFER_SIZE) {
            c = decoder.getFileInputStream().read();
            stringBuilder.append(decoder.getUncompressed(c));
        }
        while (c != -1 || !(stringBuilder.length() == 0)) {
            if (currentNode.getRight() == null && currentNode.getLeft() == null) {
                if (currentNode == node) {
                    try {
                        ch = Integer.parseInt(stringBuilder.substring(0, decoder.bit), 2);
                        stringBuilder.delete(0, decoder.bit);
                        decoder.getFileOutputStream().write(ch);
                        node = decoder.insert(ch, node);
                        if (stringBuilder.length() < decoder.MAX_BUFFER_SIZE && (c = decoder.getFileInputStream().read()) != -1)
                            stringBuilder.append(decoder.getUncompressed(c));
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println(e);
                    }
                } else {
                    ch = currentNode.getData();
                    decoder.getFileOutputStream().write(currentNode.getData());
                }
                decoder.updateTree(decoder.getHashMap().get(ch));
                currentNode = decoder.getRoot();
            } else {
                String bit = stringBuilder.substring(0, 1);
                currentNode = (bit.equals("0")) ? currentNode.getLeft() : currentNode.getRight();
                stringBuilder = stringBuilder.delete(0, 1);
                if (stringBuilder.length() < decoder.MAX_BUFFER_SIZE && (c = decoder.getFileInputStream().read()) != -1) {
                    stringBuilder.append(decoder.getUncompressed(c));
                }
            }
        }
        printDecoderLength(new File(secondFile));
        printDecoderDone();
    }

    @FXML
    private void printDecoderLength(File file) {
        decodedLength.setText(String.valueOf(file.length() / 1024) + " bytes");
    }

    @FXML
    private void printDecoderDone() {
        decoderDone.setText("End of decoding");
    }

}

