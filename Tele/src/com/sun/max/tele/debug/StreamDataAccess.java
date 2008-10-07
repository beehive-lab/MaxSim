/*
 * Copyright (c) 2007 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation, these intellectual property
 * rights may include one or more of the U.S. patents listed at http://www.sun.com/patents and one or
 * more additional patents or pending patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software. Government users are subject to the Sun
 * Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its
 * supplements.
 *
 * Use is subject to license terms. Sun, Sun Microsystems, the Sun logo, Java and Solaris are trademarks or
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All SPARC trademarks
 * are used under license and are trademarks or registered trademarks of SPARC International, Inc. in the
 * U.S. and other countries.
 *
 * UNIX is a registered trademark in the U.S. and other countries, exclusively licensed through X/Open
 * Company, Ltd.
 */
/*VCSID=c9c3715b-1748-47e8-a689-d12dc0c64106*/
package com.sun.max.tele.debug;

import java.io.*;

import com.sun.max.lang.*;
import com.sun.max.program.*;
import com.sun.max.unsafe.*;

/**
 * @author Bernd Mathiske
 */
public class StreamDataAccess implements DataAccess {

    private final DataStreamFactory _inspectorStreamFactory;
    private final Endianness _endianness;
    private final WordWidth _wordWidth;

    public StreamDataAccess(DataStreamFactory inspectorStreamFactory, DataModel dataModel) {
        _inspectorStreamFactory = inspectorStreamFactory;
        _endianness = dataModel.endianness();
        _wordWidth = dataModel.wordWidth();
    }

    public InputStream createInputStream(Address address, int size) {
        return _inspectorStreamFactory.createInputStream(address, size);
    }

    public byte[] readFully(Address address, int length) {
        return DataIO.Static.readFully(this, address, length);
    }

    public void readFully(Address address, byte[] buffer) {
        DataIO.Static.readFully(this, address, buffer);
    }

    public int read(Address address, byte[] buffer, int offset, int length) {
        try {
            return createInputStream(address, buffer.length).read(buffer, offset, length);
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public byte readByte(Address address) {
        try {
            return (byte) createInputStream(address, Bytes.SIZE).read();
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public byte readByte(Address address, Offset offset) {
        return readByte(address.plus(offset));
    }

    public byte readByte(Address address, int offset) {
        return readByte(address.plus(offset));
    }

    public byte getByte(Address address, int displacement, int index) {
        return readByte(address.plus(displacement).plus(index));
    }

    public boolean readBoolean(Address address) {
        return readByte(address) != (byte) 0;
    }

    public boolean readBoolean(Address address, Offset offset) {
        return readBoolean(address.plus(offset));
    }

    public boolean readBoolean(Address address, int offset) {
        return readBoolean(address.plus(offset));
    }

    public boolean getBoolean(Address address, int displacement, int index) {
        return readBoolean(address.plus(displacement).plus(index));
    }

    public short readShort(Address address) {
        try {
            return _endianness.readShort(createInputStream(address, Shorts.SIZE));
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public short readShort(Address address, Offset offset) {
        return readShort(address.plus(offset));
    }

    public short readShort(Address address, int offset) {
        return readShort(address.plus(offset));
    }

    public short getShort(Address address, int displacement, int index) {
        return readShort(address.plus(Offset.fromInt(index).times(2).plus(displacement)));
    }

    public char readChar(Address address) {
        return (char) readShort(address);
    }

    public char readChar(Address address, Offset offset) {
        return readChar(address.plus(offset));
    }

    public char readChar(Address address, int offset) {
        return readChar(address.plus(offset));
    }

    public char getChar(Address address, int displacement, int index) {
        return readChar(address.plus(Offset.fromInt(index).times(2).plus(displacement)));
    }

    public int readInt(Address address) {
        try {
            return _endianness.readInt(createInputStream(address, Ints.SIZE));
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public int readInt(Address address, Offset offset) {
        return readInt(address.plus(offset));
    }

    public int readInt(Address address, int offset) {
        return readInt(address.plus(offset));
    }

    public int getInt(Address address, int displacement, int index) {
        return readInt(address.plus(Offset.fromInt(index).times(4).plus(displacement)));
    }

    public float readFloat(Address address) {
        return UnsafeLoophole.intToFloat(readInt(address));
    }

    public float readFloat(Address address, Offset offset) {
        return readFloat(address.plus(offset));
    }

    public float readFloat(Address address, int offset) {
        return readFloat(address.plus(offset));
    }

    public float getFloat(Address address, int displacement, int index) {
        return readFloat(address.plus(Offset.fromInt(index).times(4).plus(displacement)));
    }

    public long readLong(Address address) {
        try {
            return _endianness.readLong(createInputStream(address, Longs.SIZE));
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public long readLong(Address address, Offset offset) {
        return readLong(address.plus(offset));
    }

    public long readLong(Address address, int offset) {
        return readLong(address.plus(offset));
    }

    public long getLong(Address address, int displacement, int index) {
        return readLong(address.plus(Offset.fromInt(index).times(8).plus(displacement)));
    }

    public double readDouble(Address address) {
        return UnsafeLoophole.longToDouble(readLong(address));
    }

    public double readDouble(Address address, Offset offset) {
        return readDouble(address.plus(offset));
    }

    public double readDouble(Address address, int offset) {
        return readDouble(address.plus(offset));
    }

    public double getDouble(Address address, int displacement, int index) {
        return readDouble(address.plus(Offset.fromInt(index).times(8).plus(displacement)));
    }

    public Word readWord(Address address) {
        switch (_wordWidth) {
            case BITS_32:
                return Address.fromInt(readInt(address));
            case BITS_64:
                return Address.fromLong(readLong(address));
            default:
                ProgramError.unknownCase();
                return Word.zero();
        }
    }

    public Word readWord(Address address, Offset offset) {
        return readWord(address.plus(offset));
    }

    public Word readWord(Address address, int offset) {
        return readWord(address.plus(offset));
    }

    public Word getWord(Address address, int displacement, int index) {
        return readWord(address.plus(Offset.fromInt(index).times(_wordWidth.numberOfBytes()).plus(displacement)));
    }

    public OutputStream createOutputStream(Address address, int size) {
        return _inspectorStreamFactory.createOutputStream(address, size);
    }

    @Override
    public int write(byte[] buffer, int offset, int length, Address toAddress) throws DataIOError {
        try {
            createOutputStream(toAddress, length).write(buffer, offset, length);
            return length;
        } catch (IOException ioException) {
            throw new DataIOError(toAddress);
        }
    }

    public void writeBytes(Address address, byte[] bytes) {
        try {
            createOutputStream(address, bytes.length).write(bytes);
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public void writeByte(Address address, byte value) {
        try {
            createOutputStream(address, Bytes.SIZE).write(value);
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public void writeByte(Address address, Offset offset, byte value) {
        writeByte(address.plus(offset), value);
    }

    public void writeByte(Address address, int offset, byte value) {
        writeByte(address.plus(offset), value);
    }

    public void setByte(Address address, int displacement, int index, byte value) {
        writeByte(address, displacement + index, value);
    }

    public void writeBoolean(Address address, boolean value) {
        writeByte(address, (byte) (value ? 1 : 0));
    }

    public void writeBoolean(Address address, Offset offset, boolean value) {
        writeByte(address, offset, (byte) (value ? 1 : 0));
    }

    public void writeBoolean(Address address, int offset, boolean value) {
        writeByte(address, offset, (byte) (value ? 1 : 0));
    }

    public void setBoolean(Address address, int displacement, int index, boolean value) {
        setByte(address, displacement, index, (byte) (value ? 1 : 0));
    }

    public void writeShort(Address address, short value) {
        try {
            _endianness.writeShort(createOutputStream(address, Shorts.SIZE), value);
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public void writeShort(Address address, Offset offset, short value) {
        writeShort(address.plus(offset), value);
    }

    public void writeShort(Address address, int offset, short value) {
        writeShort(address.plus(offset), value);
    }

    public void setShort(Address address, int displacement, int index, short value) {
        writeShort(address.plus(index * Shorts.SIZE + displacement), value);
    }

    public void writeChar(Address address, char value) {
        writeShort(address, (short) value);
    }

    public void writeChar(Address address, Offset offset, char value) {
        writeShort(address, offset, (short) value);
    }

    public void writeChar(Address address, int offset, char value) {
        writeShort(address, offset, (short) value);
    }

    public void setChar(Address address, int displacement, int index, char value) {
        setShort(address, displacement, index, (short) value);
    }

    public void writeInt(Address address, int value) {
        try {
            _endianness.writeInt(createOutputStream(address, Ints.SIZE), value);
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public void writeInt(Address address, Offset offset, int value) {
        writeInt(address.plus(offset), value);
    }

    public void writeInt(Address address, int offset, int value) {
        writeInt(address.plus(offset), value);
    }

    public void setInt(Address address, int displacement, int index, int value) {
        writeInt(address.plus(index * Ints.SIZE + displacement), value);
    }

    public void writeFloat(Address address, float value) {
        writeInt(address, Float.floatToIntBits(value));
    }

    public void writeFloat(Address address, Offset offset, float value) {
        writeInt(address, offset, Float.floatToIntBits(value));
    }

    public void writeFloat(Address address, int offset, float value) {
        writeInt(address, offset, Float.floatToIntBits(value));
    }

    public void setFloat(Address address, int displacement, int index, float value) {
        setInt(address, displacement, index, Float.floatToIntBits(value));
    }

    public void writeLong(Address address, long value) {
        try {
            _endianness.writeLong(createOutputStream(address, Longs.SIZE), value);
        } catch (IOException ioException) {
            throw new DataIOError(address);
        }
    }

    public void writeLong(Address address, Offset offset, long value) {
        writeLong(address.plus(offset), value);
    }

    public void writeLong(Address address, int offset, long value) {
        writeLong(address.plus(offset), value);
    }

    public void setLong(Address address, int displacement, int index, long value) {
        writeLong(address.plus(index * Longs.SIZE + displacement), value);
    }

    public void writeDouble(Address address, double value) {
        writeLong(address, Double.doubleToLongBits(value));
    }

    public void writeDouble(Address address, Offset offset, double value) {
        writeLong(address, offset, Double.doubleToLongBits(value));
    }

    public void writeDouble(Address address, int offset, double value) {
        writeLong(address, offset, Double.doubleToLongBits(value));
    }

    public void setDouble(Address address, int displacement, int index, double value) {
        setLong(address, displacement, index, Double.doubleToLongBits(value));
    }

    public void writeWord(Address address, Word value) {
        switch (_wordWidth) {
            case BITS_32:
                writeInt(address, value.asAddress().toInt());
                break;
            case BITS_64:
                writeLong(address, value.asAddress().toLong());
                break;
            default:
                ProgramError.unknownCase();
                break;
        }
    }

    public void writeWord(Address address, Offset offset, Word value) {
        writeWord(address.plus(offset), value);
    }

    public void writeWord(Address address, int offset, Word value) {
        writeWord(address.plus(offset), value);
    }

    public void setWord(Address address, int displacement, int index, Word value) {
        writeWord(address.plus(index * _wordWidth.numberOfBytes() + displacement), value);
    }

}
