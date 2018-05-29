package SimpleHuffmanCode;

import sun.security.util.BitArray;

import java.util.Comparator;
import java.util.List;

public class Node {
    private Node right;
    private Node left;
    private Byte byteSymbol;
    private Integer frequency;
    private List<Boolean> codeList;
    private BitArray bitArray;

    public BitArray getBitArray() {
        return bitArray;
    }

    public void setBitArray(BitArray bitArray) {
        this.bitArray = bitArray;
    }

    public List<Boolean> getCodeList() {
        return codeList;
    }

    public void setCodeList(List<Boolean> codeList) {
        this.codeList = codeList;
    }

    public Byte getByteSymbol() {
        return byteSymbol;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setByteSymbol(Byte byteSymbol) {
        this.byteSymbol = byteSymbol;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node(Node right, Node left) {
        this.right = right;
        this.left = left;
    }

    public static final Comparator<Node> COMPARE_BY_FREQUENCY = Comparator.comparingInt(Node::getFrequency);
}
