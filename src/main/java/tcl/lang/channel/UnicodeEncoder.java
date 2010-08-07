package tcl.lang.channel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * Encodes Unicode characters into the requested encoding.  Supports Tcl's 'binary' encoding.
 * Similar to a OutputStreamWriter, but prevents any internal buffering.
 * 
 * @author Dan Bodoh
 */
class UnicodeEncoder extends Writer {
	private String encoding = null;
	private OutputStream out;
	private CharsetEncoder cse = null;
	
	
	/**
	 * Create a new UnicodeEncoder with the specified encoding
	 * 
	 * @param out
	 *            the underlying OutputStream that will be encoded
	 * @param encoding
	 *            a Java encoding string name, or null for no encoding
	 * @throws UnsupportedEncodingException
	 */
	UnicodeEncoder(OutputStream out, String encoding)  {
		this.out = out;
		setEncoding(encoding);
	}
	
	
	/**
	 * Set the encoding for this encoder
	 * 
	 * @param encoding a Java encoding string name, or null for no encoding
	 * 
	 * @throws UnsupportedEncodingException
	 */
	void setEncoding(String encoding)  {
		if (this.encoding == null && encoding == null) return;
		if (this.encoding != null && this.encoding.equals(encoding)) return;
		
		this.encoding = encoding;
		if (encoding == null)
			cse = null;
		else {
			cse = Charset.forName(encoding).newEncoder();
			cse.onMalformedInput(CodingErrorAction.REPLACE);
			cse.onUnmappableCharacter(CodingErrorAction.REPLACE);
		}
	}

	
	@Override
	public void close() throws IOException {
		if (cse != null) {
			CharBuffer cb = CharBuffer.allocate(0);
			ByteBuffer bb = ByteBuffer.allocate(1024);
			cse.encode(cb, bb, true);
			cse.flush(bb);
			bb.flip();
			out.write(bb.array(), bb.position(), bb.limit());
		}
		out.close();
		out = null;
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		if (cse != null) {
			byte [] bbuf = new byte[(int)(Math.ceil(len * cse.averageBytesPerChar()))];
			ByteBuffer bb = ByteBuffer.wrap(bbuf);
			CharBuffer cb = CharBuffer.wrap(cbuf, off, len);
			
			CoderResult result = CoderResult.OVERFLOW;
			
			while (result == CoderResult.OVERFLOW) {
				result = cse.encode(cb, bb, false);
				bb.flip();
				out.write(bb.array(), bb.position(), bb.limit());
				bb.clear();
			}
			
			if (cb.remaining() > 0) {
				/* JTCL's original TclOutputStream didn't test for this condition.  It's probably not possible,
				 * since there there is no multi-character unicode characters.  But we'll throw an exception
				 * in case it does 
				 */
				throw new RuntimeException("Unicode Encoder did not consume all of the input, this is unexpected: pos="
						+bb.position()+" limit="+bb.limit()+" result="+result);
			}
			
		} else {
			/*
			 * cse is null, which means we are doing a binary encoding - just move chars to bytes
			 * 
			 */
			byte [] bbuf = new byte[len];
			for (int i=0; i<len; i++) {
				bbuf[i] = (byte)(cbuf[i+off] & 0xff);
			}
			out.write(bbuf, 0, len);
		}
	}

}
