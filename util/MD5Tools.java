package util;

import java.security.MessageDigest;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class MD5Tools
{
	private static MessageDigest md=MessageDigest.getInstance("MD5");
	MD5Tools(){}

	public static String FileToMD5(File f)
	{
		md.reset();
		try
		{
			byte[] byteArray;
			FileInputStream fis=new FileInputStream(f);
			ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
			byte[] tmp=new byte[1000];
			int tmpn;
			while((n=fis.read(tmp))!=-1)
				baos.write(b,0,n);
			fis.close();
			baos.close();
			byteArray=baos.toByteArray();
		}
		catch(Exception e)
		{byteArray=new byte[0];}
		md.update(byteArray);
		byteArray=md.digest();
		return byteArrayToHex(byteArray);
	}

	public static String FileToMD5(String path)
	{
		File f=new File(path);
		return FileToMD5(f);
	}

	public static String StringToMD5(String str)
	{
		md.reset();
		md.update(str.getBytes());
		return byteArrayToHex(md.digest());
	}

	private static String byteArrayToHex(byte[] byteArray)
	{
		char[] hexDigits=
		{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] result=new char[byteArray.length*2];
		int i=0;
		for(b:byteArray)
		{
			result[i++]=hexDights[b>>4];
			result[i++]=hexDights[b];
		}
		return new String(result);
	}
}