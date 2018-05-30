package ArithmeticCode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.BitSet;
import java.util.List;

public class ArithmeticCode {
    private final int BITS = 30;
    private final int HIGHEST_BIT = 1 << (BITS - 1);
    private final int MASK = (1 << BITS) - 1;
    private final int END = 256;
    private long border_L, border_H;
    private int add_bits;
    private long value;
    private int[] freq;
    private int[] bits;
    private int bitsPos;
    private List<Boolean> encodedBits;
    private List<Integer> decodedBytes;

    private FileInputStream fileInputStream;
    private FileOutputStream fileOutputStream;

    public int getBITS() {
        return BITS;
    }

    public int getHIGHEST_BIT() {
        return HIGHEST_BIT;
    }

    public int getMASK() {
        return MASK;
    }

    public int getEND() {
        return END;
    }

    public int getAdd_bits() {
        return add_bits;
    }

    public void setAdd_bits(int add_bits) {
        this.add_bits = add_bits;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public int[] getFreq() {
        return freq;
    }

    public void setFreq(int[] freq) {
        this.freq = freq;
    }

    public int[] getBits() {
        return bits;
    }

    public void setBits(int[] bits) {
        this.bits = bits;
    }

    public int getBitsPos() {
        return bitsPos;
    }

    public void setBitsPos(int bitsPos) {
        this.bitsPos = bitsPos;
    }

    public long getBorder_L() {
        return border_L;
    }

    public void setBorder_L(long border_L) {
        this.border_L = border_L;
    }

    public long getBorder_H() {
        return border_H;
    }

    public void setBorder_H(long border_H) {
        this.border_H = border_H;
    }

    public List<Boolean> getEncodedBits() {
        return encodedBits;
    }

    public void setEncodedBits(List<Boolean> encodedBits) {
        this.encodedBits = encodedBits;
    }

    public List<Integer> getDecodedBytes() {
        return decodedBytes;
    }

    public void setDecodedBytes(List<Integer> decodedBytes) {
        this.decodedBytes = decodedBytes;
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

    public ArithmeticCode(String name1, String name2) throws FileNotFoundException {
        setFileInputStream(new FileInputStream(name1));
        setFileOutputStream(new FileOutputStream(name2));
    }

    public void code_char(int c) {
        long range = border_H - border_L + 1;
        border_H = border_L + range * calc_summ(freq, c) / calc_summ(freq, END) - 1;
        border_L = border_L + range * calc_summ(freq, c - 1) / calc_summ(freq, END);
        while (true) {
            if ((border_L & HIGHEST_BIT) == (border_H & HIGHEST_BIT)) {
                outputBit((border_H & HIGHEST_BIT) != 0);
                border_L = (border_L << 1) & MASK;
                border_H = ((border_H << 1) + 1) & MASK;
            } else if (border_H - border_L < calc_summ(freq, END)) {
                border_L = (border_L - (1 << (BITS - 2))) << 1;
                border_H = ((border_H - (1 << (BITS - 2))) << 1) + 1;
                ++add_bits;
            } else {
                break;
            }
        }
        increment(freq, c);
    }

    public void outputBit(boolean bit) {
        encodedBits.add(bit);
        for (; add_bits > 0; add_bits--)
            encodedBits.add(!bit);
    }

    public int decode_char() {
        int cum = (int) (((value - border_L + 1) * calc_summ(freq, END) - 1) / (border_H - border_L + 1));
        int c = upper_bound(freq, cum);
        long range = border_H - border_L + 1;
        border_H = border_L + range * calc_summ(freq, c) / calc_summ(freq, END) - 1;
        border_L = border_L + range * calc_summ(freq, c - 1) / calc_summ(freq, END);
        while (true) {
            if ((border_L & HIGHEST_BIT) == (border_H & HIGHEST_BIT)) {
                border_L = (border_L << 1) & MASK;
                border_H = ((border_H << 1) + 1) & MASK;
                int b = bitsPos < bits.length ? bits[bitsPos++] : 0;
                value = ((value << 1) + b) & MASK;
            } else if (border_H - border_L < calc_summ(freq, END)) {
                border_L = (border_L - (1 << (BITS - 2))) << 1;
                border_H = ((border_H - (1 << (BITS - 2))) << 1) + 1;
                int b = bitsPos < bits.length ? bits[bitsPos++] : 0;
                value = ((value - (1 << (BITS - 2))) << 1) + b;
            } else {
                break;
            }
        }
        return c;
    }

    public int[] build_tree(int num) {
        int[] data = new int[num];
        for (int i = 0; i < num; i++) {
            ++data[i];
            int j = i | (i + 1);
            if (j < num)
                data[j] += data[i];
        }
        return data;
    }

    public void increment(int[] t, int i) {
        for (; i < t.length; i |= i + 1)
            ++t[i];
    }

    private static int calc_summ(int[] t, int i) {
        int summ = 0;
        for (; i >= 0; i = (i & (i + 1)) - 1)
            summ += t[i];
        return summ;
    }

    private static int upper_bound(int[] t, int sum) {
        int position = -1;
        for (int blockSize = Integer.highestOneBit(t.length); blockSize != 0; blockSize >>= 1) {
            int nextPos = position + blockSize;
            if (nextPos < t.length && sum >= t[nextPos]) {
                sum -= t[nextPos];
                position = nextPos;
            }
        }
        return position + 1;
    }

    public byte[] encodeToByteArray(int[] bits) {
        BitSet bitSet = new BitSet(bits.length);
        for (int index = 0; index < bits.length; index++) {
            bitSet.set(index, bits[index] > 0);
        }

        return bitSet.toByteArray();
    }

    public int[] encodeToBitArray(byte[] in_bytes) {
        int[] res = new int[in_bytes.length * 8];
        int counter = 0;
        for (int data : in_bytes) {
            for (byte m = 1; m != 0; m <<= 1) {
                int bit = ((data & m) != 0) ? 1 : 0;
                res[counter] = bit;
                counter++;
            }
        }
        return res;
    }
}
