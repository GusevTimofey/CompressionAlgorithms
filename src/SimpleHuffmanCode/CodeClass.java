package SimpleHuffmanCode;

import sun.security.util.BitArray;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class CodeClass {

    private Map<Byte, Integer> mapForFrequency;
    private Map<Byte, BitArray> map;

    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

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

    public Map<Byte, Integer> getMapForFrequency() {
        return mapForFrequency;
    }

    public void setMapForFrequency(Map<Byte, Integer> mapForFrequency) {
        this.mapForFrequency = mapForFrequency;
    }

    public Map<Byte, BitArray> getMap() {
        return map;
    }

    public void setMap(Map<Byte, BitArray> map) {
        this.map = map;
    }

    public CodeClass(String name1, String name2) throws FileNotFoundException {
        setFileInputStream(new FileInputStream(name1));
        setFileOutputStream(new FileOutputStream(name2));
        mapForFrequency = new TreeMap<>();
    }

    public Map doCOdeSymbolsMap(byte[] array) {
        for (byte symbol : array) {
            int frequency = 1;
            if (mapForFrequency.containsKey(symbol)) {
                frequency = (mapForFrequency.get(symbol) + 1);
                mapForFrequency.put(symbol, frequency);
            } else
                mapForFrequency.put(symbol, frequency);
        }
        List<Map.Entry<Byte, Integer>> sortedList = mapForFrequency.entrySet().
                stream().sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue())).collect(Collectors.toList());
        Collectors.toList();

        List<Node> otherList = new ArrayList<>();
        for (Map.Entry<Byte, Integer> characterIntegerEntry : sortedList) {
            Node node = new Node(null, null);
            node.setByteSymbol(characterIntegerEntry.getKey());
            node.setFrequency(characterIntegerEntry.getValue());
            otherList.add(0, node);
        }
        for (int i = 0; i < otherList.size(); i++) {
            if (otherList.size() == 1)
                break;
            Node left = otherList.get(i);
            Node right = otherList.get(i + 1);
            otherList.remove(i);
            otherList.remove(i);
            i--;
            Node parent = new Node(left, right);
            parent.setFrequency(left.getFrequency() + right.getFrequency());
            otherList.add(parent);
            otherList.sort(Node.COMPARE_BY_FREQUENCY);
        }
        Map<Character, BitArray> mapForCodes = new TreeMap();
        otherList.get(0).setCodeList(new ArrayList<>());
        setBitCode(otherList.get(0));
        fillMapByCodes(otherList.get(0), mapForCodes);
        for (Map.Entry tmp : mapForCodes.entrySet())
            System.out.println(tmp.getKey() + " " + tmp.getValue().toString());
        System.out.println(mapForCodes.size());
        return mapForCodes;
    }

    private void fillMapByCodes(Node node, Map map) {
        if (node != null) {
            if (node.getByteSymbol() != null)
                map.put(node.getByteSymbol(), node.getBitArray());
            fillMapByCodes(node.getLeft(), map);
            fillMapByCodes(node.getRight(), map);
        }
    }

    private void setBitCode(Node node) {

        if (node != null) {
            if (node.getLeft() != null) {
                List<Boolean> codeList = new ArrayList<>(node.getCodeList());
                codeList.add(false);
                node.getLeft().setCodeList(codeList);
                boolean[] tmp = new boolean[codeList.size()];
                for (int i = 0; i < tmp.length; i++)
                    tmp[i] = codeList.get(i);
                node.getLeft().setBitArray(new BitArray(tmp));
                setBitCode(node.getLeft());
            }
            if (node.getRight() != null) {
                List<Boolean> codeList = new ArrayList<>(node.getCodeList());
                codeList.add(true);
                node.getRight().setCodeList(codeList);
                boolean[] tmp = new boolean[codeList.size()];
                for (int i = 0; i < tmp.length; i++)
                    tmp[i] = codeList.get(i);
                node.getRight().setBitArray(new BitArray(tmp));
                setBitCode(node.getRight());
            }
        }
    }
}

