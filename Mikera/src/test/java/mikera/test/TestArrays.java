package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import mikera.util.Arrays;
import mikera.util.Rand;
import mikera.util.Tools;

import org.junit.Test;

public class TestArrays {
	@Test public void testMarge() {
		double[] a=new double[100];
		double[] b=new double[100];
		
		a[0]=10;
		b[1]=10;
		b[10]=100;
		Arrays.mergeCopy(a, 0, b, 0, 10, 0.7);
		
	
		assertEquals(10,a[0],0.0001);
		assertEquals(7,b[0],0.0001);
		assertEquals(3,b[1],0.0001);
		assertEquals(100,b[10],0.0001);
	}

}