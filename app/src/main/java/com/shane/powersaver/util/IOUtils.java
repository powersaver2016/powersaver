/**
 * Copyright (C) 2013, Xiaomi Inc. All rights reserved.
 */

package com.shane.powersaver.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * IO utils class to process {@link InputStream}, {@link OutputStream},
 * {@link Reader} and {@link Writer}.
 */
public class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static final ThreadLocal<SoftReference<byte[]>> THREAD_LOCAL_BYTE_BUFFER =
            new ThreadLocal<SoftReference<byte[]>>();

    private static final ThreadLocal<SoftReference<char[]>> THREAD_LOCAL_CHAR_BUFFER =
            new ThreadLocal<SoftReference<char[]>>();

    private static final Pools.Pool<ByteArrayOutputStream> BYTE_ARRAY_OUTPUT_STREAM_POOL =
            Pools.createSoftReferencePool(new Pools.Manager<ByteArrayOutputStream>() {
                @Override
                public ByteArrayOutputStream createInstance() {
                    return new ByteArrayOutputStream();
                }

                @Override
                public void onRelease(ByteArrayOutputStream element) {
                    element.reset();
                }
            }, 2);

    private static final Pools.Pool<CharArrayWriter> CHAR_ARRAY_WRITER_POOL =
            Pools.createSoftReferencePool(new Pools.Manager<CharArrayWriter>() {
                @Override
                public CharArrayWriter createInstance() {
                    return new CharArrayWriter();
                }

                @Override
                public void onRelease(CharArrayWriter element) {
                    element.reset();
                }
            }, 2);

    private static final Pools.Pool<StringWriter> STRING_WRITER_POOL =
            Pools.createSoftReferencePool(new Pools.Manager<StringWriter>() {
                @Override
                public StringWriter createInstance() {
                    return new StringWriter();
                }

                @Override
                public void onRelease(StringWriter element) {
                    element.getBuffer().setLength(0);
                }
            }, 2);

    private static final String LINE_SEPARATOR;
    static {
        StringWriter buf = STRING_WRITER_POOL.acquire();
        PrintWriter out = new PrintWriter(buf);
        out.println();
        out.flush();
        LINE_SEPARATOR = buf.toString();
        out.close();
        STRING_WRITER_POOL.release(buf);
    }

    /**
     * Only for extends, please don't instantiate this class.
     * @throws InstantiationException always when this class is instantiated.
     */
    protected IOUtils() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    /**
     * Closes a reader ignoring null and exception.
     * @param reader the reader to close
     */
    public static void closeQuietly(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes a writer ignoring null and exception.
     * @param writer the writer to close
     */
    public static void closeQuietly(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes an input stream ignoring null and exception.
     * @param is the input stream to close
     */
    public static void closeQuietly(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes an output stream ignoring null and exception.
     * @param os the output stream to close
     */
    public static void closeQuietly(OutputStream os) {
        if (os != null) {
            try {
                os.flush();
            } catch (IOException e) {
                // ignore
            }
            try {
                os.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Closes a closeable resource ignoring null and exception.
     * @param c the resource to close
     */
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Reads an input stream into a byte array.
     * @param input The input stream to read.
     * @return A byte array contains all content in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream baos = BYTE_ARRAY_OUTPUT_STREAM_POOL.acquire();
        copy(input, baos);
        byte[] ret = baos.toByteArray();
        BYTE_ARRAY_OUTPUT_STREAM_POOL.release(baos);
        return ret;
    }

    /**
     * Reads a reader into a byte array.
     * @param input The reader to read.
     * @return A byte array contains all content in {@code input}.
     * @throws IOException Exception happened when reading reader.
     */
    public static byte[] toByteArray(Reader input) throws IOException {
        ByteArrayOutputStream baos = BYTE_ARRAY_OUTPUT_STREAM_POOL.acquire();
        copy(input, baos);
        byte[] ret = baos.toByteArray();
        BYTE_ARRAY_OUTPUT_STREAM_POOL.release(baos);
        return ret;
    }


    /**
     * Reads a reader into a byte array.
     * @param input The reader to read.
     * @param encoding The encoding charset name of the {@code input}.
     * @return A byte array contains all content in {@code input}.
     * @throws IOException Exception happened when reading reader.
     */
    public static byte[] toByteArray(Reader input, String encoding) throws IOException {
        ByteArrayOutputStream baos = BYTE_ARRAY_OUTPUT_STREAM_POOL.acquire();
        copy(input, baos, encoding);
        byte[] ret = baos.toByteArray();
        BYTE_ARRAY_OUTPUT_STREAM_POOL.release(baos);
        return ret;
    }

    /**
     * Reads an input stream into a char array.
     * @param input The input stream to read.
     * @return A char array contains all content in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static char[] toCharArray(InputStream input) throws IOException {
        CharArrayWriter caw = CHAR_ARRAY_WRITER_POOL.acquire();
        copy(input, caw);
        char[] ret = caw.toCharArray();
        CHAR_ARRAY_WRITER_POOL.release(caw);
        return ret;
    }

    /**
     * Reads an input stream into a char array.
     * @param input The input stream to read.
     * @param encoding The encoding charset name of the {@code input}.
     * @return A char array contains all content in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static char[] toCharArray(InputStream input, String encoding) throws IOException {
        CharArrayWriter caw = CHAR_ARRAY_WRITER_POOL.acquire();
        copy(input, caw, encoding);
        char[] ret = caw.toCharArray();
        CHAR_ARRAY_WRITER_POOL.release(caw);
        return ret;
    }

    /**
     * Reads a reader into a char array.
     * @param input The reader to read.
     * @return A char array contains all content in {@code input}.
     * @throws IOException Exception happened when reading reader.
     */
    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter caw = CHAR_ARRAY_WRITER_POOL.acquire();
        copy(input, caw);
        char[] ret = caw.toCharArray();
        CHAR_ARRAY_WRITER_POOL.release(caw);
        return ret;
    }

    /**
     * Reads an input stream into a string.
     * @param input The input stream to read.
     * @return A string contains all content in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static String toString(InputStream input) throws IOException {
        StringWriter sw = STRING_WRITER_POOL.acquire();
        copy(input, sw);
        String ret = sw.toString();
        STRING_WRITER_POOL.release(sw);
        return ret;
    }

    /**
     * Reads an input stream into a string.
     * @param input The input stream to read.
     * @param encoding The encoding charset name of the {@code input}.
     * @return A string contains all content in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static String toString(InputStream input, String encoding) throws IOException {
        StringWriter sw = STRING_WRITER_POOL.acquire();
        copy(input, sw, encoding);
        String ret = sw.toString();
        STRING_WRITER_POOL.release(sw);
        return ret;
    }

    /**
     * Reads a reader into a string.
     * @param input The reader to read.
     * @return A string contains all content in {@code input}.
     * @throws IOException Exception happened when reading reader.
     */
    public static String toString(Reader input) throws IOException {
        StringWriter sw = STRING_WRITER_POOL.acquire();
        copy(input, sw);
        String ret = sw.toString();
        STRING_WRITER_POOL.release(sw);
        return ret;
    }

    /**
     * Reads an input stream into a string list by lines.
     * @param input The input stream to read.
     * @return A string list contains all lines in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static List<String> readLines(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input);
        return readLines(reader);
    }

    /**
     * Reads an input stream into a string list by lines.
     * @param input The input stream to read.
     * @param encoding The encoding charset name of the {@code input}.
     * @return A string list contains all lines in {@code input}.
     * @throws IOException Exception happened when reading input stream.
     */
    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        InputStreamReader reader = encoding == null || encoding.length() == 0 ? new InputStreamReader(input)
                : new InputStreamReader(input, encoding);
        return readLines(reader);
    }

    /**
     * Reads a reader into a string list by lines.
     * @param input The reader to read.
     * @return A string list contains all lines in {@code input}.
     * @throws IOException Exception happened when reading reader.
     */
    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = (input instanceof BufferedReader) ? (BufferedReader) input
                : new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            list.add(line);
        }
        return list;
    }

    /**
     * Converts a string to a input stream.
     * @param input The given string.
     * @return A input stream to read the string content.
     */
    public static InputStream toInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    /**
     * Converts a string to a input stream.
     * @param input The given string.
     * @param encoding The encoding charset name for the input stream.
     * @return A input stream to read the string content.
     * @throws UnsupportedEncodingException When encoding is unsupported.
     */
    public static InputStream toInputStream(String input, String encoding) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(encoding == null || encoding.length() == 0 ?
                input.getBytes() : input.getBytes(encoding));
    }

    /**
     * Writes byte array into output stream.
     * @param output The given output stream to write.
     * @param data The byte array to write.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void write(OutputStream output, byte[] data) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes byte array into writer.
     * @param output The given writer to write.
     * @param data The byte array to write.
     * @throws IOException Exception happened when writing writer.
     */
    public static void write(Writer output, byte[] data) throws IOException {
        if (data != null) {
            output.write(new String(data));
        }
    }

    /**
     * Writes byte array into writer.
     * @param output The given writer to write.
     * @param data The byte array to write.
     * @param encoding the encoding charset name of the {@code data}.
     * @throws IOException Exception happened when writing writer.
     */
    public static void write(Writer output, byte[] data, String encoding) throws IOException {
        if (data != null) {
            output.write(encoding == null || encoding.length() == 0 ? new String(data) : new String(data, encoding));
        }
    }

    /**
     * Writes char array into writer.
     * @param output The given writer to write.
     * @param data The char array to write.
     * @throws IOException Exception happened when writing writer.
     */
    public static void write(Writer output, char[] data) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes char array into output stream.
     * @param output The given output stream to write.
     * @param data The char array to write.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void write(OutputStream output, char[] data) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes());
        }
    }

    /**
     * Writes char array into output stream.
     * @param output The given output stream to write.
     * @param data The char array to write.
     * @param encoding the encoding charset name of the {@code data}.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void write(OutputStream output, char[] data, String encoding) throws IOException {
        if (data != null) {
            output.write(encoding == null || encoding.length() == 0 ? new String(data).getBytes()
                    : new String(data).getBytes(encoding));
        }
    }

    /**
     * Writes string into writer.
     * @param output The given writer to write.
     * @param data The string to write.
     * @throws IOException Exception happened when writing writer.
     */
    public static void write(Writer output, String data) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes string into output stream.
     * @param output The given output stream to write.
     * @param data The string to write.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void write(OutputStream output, String data) throws IOException {
        if (data != null) {
            output.write(data.getBytes());
        }
    }

    /**
     * Writes string into output stream.
     * @param output The given output stream to write.
     * @param data The string to write.
     * @param encoding the encoding charset name to encode {@code data}.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void write(OutputStream output, String data, String encoding) throws IOException {
        if (data != null) {
            output.write(encoding == null || encoding.length() == 0 ? data.getBytes() : data.getBytes(encoding));
        }
    }

    /**
     * Writes lines into output stream.
     * @param output The given output stream to write.
     * @param lines The lines collection to write.
     * @param lineEnding The ending of the line, {@code null} to use default system setting.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void writeLines(OutputStream output, Collection<Object> lines, String lineEnding) throws IOException {
        if (lines == null) {
            return;
        }

        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }

        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes());
            }
            output.write(lineEnding.getBytes());
        }
    }

    /**
     * Writes lines into output stream.
     * @param output The given output stream to write.
     * @param lines The lines collection to write.
     * @param lineEnding The ending of the line, {@code null} to use default system setting.
     * @param encoding the encoding charset name to encode {code lines}.
     * @throws IOException Exception happened when writing output stream.
     */
    public static void writeLines(OutputStream output, Collection<Object> lines, String lineEnding, String encoding)
            throws IOException {
        if (lines == null) {
            return;
        }

        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }

        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(encoding));
            }
            output.write(lineEnding.getBytes(encoding));
        }
    }

    /**
     * Writes lines into writer.
     * @param output The given writer to write.
     * @param lines The lines collection to write.
     * @param lineEnding The ending of the line, {@code null} to use default system setting.
     * @throws IOException Exception happened when writing writer.
     */
    public static void writeLines(Writer output, Collection<Object> lines, String lineEnding) throws IOException {
        if (lines == null) {
            return;
        }

        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }

        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString());
            }
            output.write(lineEnding);
        }
    }

    /**
     * Copies data from input stream to an output stream.
     * @param input The input stream to read.
     * @param output The output stream to write.
     * @return The bytes count copied.
     * @throws IOException Exception happened when reading/writing.
     */
    public static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = getByteArrayBuffer();

        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        output.flush();
        return count;
    }

    /**
     * Copies data from input stream to an writer.
     * @param input The input stream to read.
     * @param output The writer to write.
     * @return The chars count copied.
     * @throws IOException Exception happened when reading/writing.
     */
    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    /**
     * Copies data from input stream to an writer.
     * @param input The input stream to read.
     * @param output The writer to write.
     * @param encoding the encoding for the input stream.
     * @return The chars count copied.
     * @throws IOException Exception happened when reading/writing.
     */
    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        InputStreamReader in = encoding == null || encoding.length() == 0 ? new InputStreamReader(input)
                : new InputStreamReader(input, encoding);
        copy(in, output);
    }

    /**
     * Copies data from reader to an output stream.
     * @param input The reader to read.
     * @param output The output stream to write.
     * @return The chars count copied.
     * @throws IOException Exception happened when reading/writing.
     */
    public static void copy(Reader input, OutputStream output) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out);
    }

    /**
     * Copies data from reader to an output stream.
     * @param input The reader to read.
     * @param output The output stream to write.
     * @param encoding the encoding for the output stream.
     * @return The chars count copied.
     * @throws IOException Exception happened when reading/writing.
     */
    public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
        OutputStreamWriter out = encoding == null || encoding.length() == 0 ? new OutputStreamWriter(output)
                : new OutputStreamWriter(output, encoding);
        copy(input, out);
    }

    /**
     * Copies data from reader to an writer.
     * @param input The reader to read.
     * @param output The writer to write.
     * @return The chars count copied.
     * @throws IOException Exception happened when reading/writing.
     */
    public static long copy(Reader input, Writer output) throws IOException {
        long count = 0;
        int n;
        char[] buffer = getCharArrayBuffer();

        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += n;
        }
        output.flush();
        return count;
    }

    /**
     * @return The byte array buffer.
     */
    private static byte[] getByteArrayBuffer() {
        byte[] buffer = null;
        SoftReference<byte[]> sr = THREAD_LOCAL_BYTE_BUFFER.get();
        if (sr != null) {
            buffer = sr.get();
        }

        if (buffer == null) {
            buffer = new byte[DEFAULT_BUFFER_SIZE];
            THREAD_LOCAL_BYTE_BUFFER.set(new SoftReference<byte[]>(buffer));
        }
        return buffer;
    }

    /**
     * @return The char array buffer.
     */
    private static char[] getCharArrayBuffer() {
        char[] buffer = null;
        SoftReference<char[]> sr = THREAD_LOCAL_CHAR_BUFFER.get();
        if (sr != null) {
            buffer = sr.get();
        }

        if (buffer == null) {
            buffer = new char[DEFAULT_BUFFER_SIZE];
            THREAD_LOCAL_CHAR_BUFFER.set(new SoftReference<char[]>(buffer));
        }
        return buffer;
    }
}
