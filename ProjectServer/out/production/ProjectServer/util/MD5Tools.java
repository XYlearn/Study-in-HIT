package util;

import java.security.MessageDigest;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class MD5Tools
{
	private static MessageDigest md;
	MD5Tools(){}

	public static String FileToMD5(File f)
	{
		byte[] byteArray;
		try
		{
			md=MessageDigest.getInstance("MD5");
			FileInputStream fis=new FileInputStream(f);
			ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
			byte[] tmp=new byte[1000];
			int tmpn;
			while((tmpn=fis.read(tmp))!=-1)
				baos.write(tmp,0,tmpn);
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
		try{
			md=MessageDigest.getInstance("MD5");
		}
		catch(Exception e)
		{
			str="";
		}
		md.update(str.getBytes());
		return byteArrayToHex(md.digest());
	}

	private static String byteArrayToHex(byte[] byteArray)
	{
		char[] hexDigits=
		{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] result=new char[byteArray.length*2];
		int i=0;
		for(byte b:byteArray)
		{
			result[i++]=hexDigits[0xf&(b>>4)];
			result[i++]=hexDigits[b&0xf];
		}
		return new String(result);
	}
}