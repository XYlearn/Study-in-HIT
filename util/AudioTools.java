package util;

import java.lang.Thread;
import java.lang.Runnable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import javax.sound.sampled.Clip;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

class AudioTools
{
	private static AudioFormat af;
	private static TargetDataLine td;
	private static ByteArrayOutputStream baos;
	private static ByteArrayInputStream bais;
	private static AudioInputStream ais;
	private enum Status{STOPPED,CAPTURING,PLAYING};
	private static Status status=STOPPED;
	private static final String CLASSPATH=AudioTools.class.getResource("").getPath();
	AudioTools(){}

	public static boolean startCapture()
	{
		if(status==CAPTURING) return true;
		try
		{
			DataLine.Info info=new DataLine.Info(TargetDataLine.class,getAudioFormat());
			td=(TargetDataLine)AudioSystem.getLine(info);
			td.open(getAudioFormat());
			td.start();
			new Thread(new MyRecord()).start();
		}
		catch(LineUnavailableException e)
		{
			return false;
		}
	}

	public static String stopAndSaveCapture()
	{
		status=STOPPED;
		byte[] byteArray=baos.toByteArray();
		bais=new ByteArrayInputStream(byteArray);
		ais=new AudioInputStream(bais,getAudioFormat(),
			byteArray.length/getAudioFormat().getFrameSize());
		File audioFile=new File(CLASSPATH+"tmpCapture.mp3");
		try
		{
			AudioSystem.write(ais,AudioFileFormat.Type.WAVE,audioFile);
		}
		catch(IOException e){}
		finally
		{
			try
			{
				if(bais!=null) bais.close();
				if(ais!=null) ais.close();
			}
			catch(Exception e){}
		}
		audioFile.renameTo(new File(CLASSPATH+MD5Tools.FileToMD5(audioFile)+".mp3"));
		if(audioFile.exists()) audioFile.delete();
	}

	public static boolean cancelCapture()
	{
		status=STOPPED;
	}

	private static getAudioFormat()
	{
		if(af==null) af=new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED, //encoding
			8000f, //rate
			16, //sampleSize
			1, //channels
			2, //(sampleSize/8)*channels
			8000f,
			true); //bigEndian
		return af;
	}

	private static class MyRecord implements Runnable
	{
		byte[] byteArray=new byte[10000];
		public void run()
		{
			int cnt;
			baos=new ByteArrayOutputSystem();
			status=CAPTURING;
			try
			{
				while(status==CAPTURING)
					if((cnt=td.read(byteArray,0,byteArray.length))>0)
						baos.write(byteArray,0,cnt);
			}
			catch(Exception e){}
			finally
			{
				try
				{
					if(baos!=null) baos.close();
				}
				catch(IOException e){}
				finally
				{
					td.drain();
					td.close();
				}
			}
		}
	}
}