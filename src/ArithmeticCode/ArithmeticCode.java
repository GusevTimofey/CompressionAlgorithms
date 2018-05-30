package ArithmeticCode;

import java.util.ArrayList;
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

    public int[] encode(int[] in_bytes) {
        encodedBits = new ArrayList<>();
        border_L = 0;
        border_H = (1 << BITS) - 1;
        add_bits = 0;
        freq = build_tree(END + 1);
        for (int data : in_bytes)
            code_char(data);
        code_char(END);
        outputBit(true);
        int[] result = new int[encodedBits.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = encodedBits.get(i) ? 1 : 0;
        return result;
    }

    private void code_char(int c) {
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

    private void outputBit(boolean bit) {
        encodedBits.add(bit);
        for (; add_bits > 0; add_bits--)
            encodedBits.add(!bit);
    }

    public int[] decode(int[] bits) {
        freq = build_tree(END + 1);
        this.bits = bits;
        decodedBytes = new ArrayList<>();
        value = 0;
        for (bitsPos = 0; bitsPos < BITS; bitsPos++)
            value = (value << 1) + (bitsPos < bits.length ? bits[bitsPos] : 0);
        border_L = 0;
        border_H = (1 << BITS) - 1;
        while (true) {
            int c = decode_char();
            if (c == END)
                break;
            decodedBytes.add(c);
            increment(freq, c);
        }
        int[] bytes = new int[decodedBytes.size()];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = decodedBytes.get(i);
        return bytes;
    }

    private int decode_char() {
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

    private int[] build_tree(int num) {
        int[] data = new int[num];
        for (int i = 0; i < num; i++) {
            ++data[i];
            int j = i | (i + 1);
            if (j < num)
                data[j] += data[i];
        }
        return data;
    }

    private void increment(int[] t, int i) {
        for (; i < t.length; i |= i + 1)
            ++t[i];
    }

    private static int calc_summ(int[] t, int i) {
        int summ = 0;
        for (; i >= 0; i = (i & (i + 1)) - 1)
            summ += t[i];
        return summ;
    }

    private int upper_bound(int[] t, int sum) {
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

    byte[] encodeToByteArray(int[] bits) {
        BitSet bitSet = new BitSet(bits.length);
        for (int index = 0; index < bits.length; index++)
            bitSet.set(index, bits[index] > 0);
        return bitSet.toByteArray();
    }

    int[] encodeToBitArray(byte[] in_bytes) {
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
