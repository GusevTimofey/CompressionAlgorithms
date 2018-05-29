package AdaptiveHuffmanCode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AdaptiveCode {

    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;
    private int count;
    private HashMap<Integer, Node> hashMap;
    private Node root;

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public void setFileOutputStream(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    private ArrayList<Node> getNodeBlock(int weight) {
        ArrayList<Node> list = new ArrayList<>();
        return getNodeBlock(weight, root, list);
    }

    private ArrayList<Node> getNodeBlock(int weight, Node node, ArrayList<Node> list) {
        if (node.getWeight() == weight) {
            list.add(node);
        }
        if (node.getLeft() != null) {
            getNodeBlock(weight, node.getLeft(), list);
        }
        if (node.getRight() != null) {
            getNodeBlock(weight, node.getRight(), list);
        }
        return list;
    }

    private static Node getHighestNode(ArrayList<Node> list) {
        Node node = null;
        int i = 0;
        for (Node n : list) {
            if (n.getIndex() > i) {
                node = n;
                i = n.getIndex();
            }
        }
        return node;
    }

    public HashMap<Integer, Node> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<Integer, Node> hashMap) {
        this.hashMap = hashMap;
    }

    public int bit = 8;
    public int MAX_BUFFER_SIZE = 64;

    public AdaptiveCode(String inputData, String outputData, int count) throws FileNotFoundException {
        this.fileInputStream = new FileInputStream(inputData);
        this.fileOutputStream = new FileOutputStream(outputData);
        this.count = count;
        this.hashMap = new HashMap<>();
        this.root = new Node(-1, this.count--, null);
    }

    public Node appendCode(Node NYT, int c, StringBuilder writeBuffer) {
        if (hashMap.containsKey(c)) {
            writeBuffer.append(buildCode(hashMap.get(c)));
        } else {
            writeBuffer.append(buildCode(NYT));
            writeBuffer.append(getUncompressed(c));
            NYT = insert(c, NYT);
        }
        updateTree(hashMap.get(c));

        return NYT;
    }

    public void writeOut(StringBuilder code) throws NumberFormatException, IOException {
        while(code.length() >= bit) {
            String writeOut = code.substring(0, bit);
            code = code.delete(0, bit);
            fileOutputStream.write(Integer.parseInt(writeOut, 2));
        }
    }

    public String buildCode(Node node) {
        StringBuilder code = new StringBuilder();
        while(node.getParent() != null) {
            if (node.getParent().getLeft() == node) {
                code.insert(0, "0");
            } else {
                code.insert(0, "1");
            }
            node = node.getParent();
        }
        return code.toString();
    }

    public void updateTree(Node node) {
        if (node == root) {
            node.incrementWeight();
        } else {
            Node highestNode = getHighestNode(getNodeBlock(node.getWeight()));
            if (highestNode != null && highestNode != node.getParent() && highestNode != node) {
                swap(node, highestNode);
            }
            node.incrementWeight();
            updateTree(node.getParent());
        }
    }

    public String getUncompressed(int c) {
        return String.format("%" + bit + "s", Integer.toBinaryString(c)).replace(' ', '0');
    }

    public Node insert(int c, Node NYT) {
        Node newNode = new Node(c, count--, NYT);
        hashMap.put(c, newNode);

        Node newNYT = new Node(-1, count--, NYT);

        NYT.setLeft(newNYT);
        NYT.setRight(newNode);

        return newNYT;
    }

    private static void swap(Node a, Node b) {
        if (a.getSibling() == b) {
            if (a.getParent().getLeft() == a) {
                a.getParent().setLeft(b);
                a.getParent().setRight(a);
            } else {
                a.getParent().setLeft(a);
                a.getParent().setRight(b);
            }
        } else {
            swapParents(a, b);
            swapParents(b, a);
        }

        Node temp = new Node(-1, -1, null);
        swapProperties(temp, a);
        swapProperties(a, b);
        swapProperties(b, temp);

    }

    private static void swapParents(Node a, Node b) {
        if (a.getParent() != null) {
            if (a.getParent().getLeft() == a) {
                a.getParent().setLeft(b);
            } else {
                a.getParent().setRight(b);
            }
        }
    }

    private static void swapProperties(Node a, Node b) {
        a.setParent(b.getParent());
        a.setIndex(b.getIndex());
    }
}

