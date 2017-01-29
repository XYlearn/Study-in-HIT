//package util;

import java.lang.Thread;
import java.lang.Runnable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionListener;
import javax.sound.sampled.Clip;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine;
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
	private static Status status=Status.STOPPED;
	private static final String CLASSPATH=AudioTools.class.getResource("").getPath();
	AudioTools(){}

	public static boolean startCapture()
	{
		if(status==Status.CAPTURING) return true;
		if(status==Status.PLAYING)
		{
			//stop();
		}
		status=Status.CAPTURING;
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
		return true;
	}

	public static String stopAndSaveCapture()
	{
		String filename;
		status=Status.STOPPED;
		byte[] byteArray=baos.toByteArray();
		bais=new ByteArrayInputStream(byteArray);
		ais=new AudioInputStream(bais,getAudioFormat(),
			byteArray.length/getAudioFormat().getFrameSize());
		File audioFile=new File(CLASSPATH+"tmpCapture.wav");
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
		filename=MD5Tools.FileToMD5(audioFile);
		audioFile.renameTo(new File(CLASSPATH+filename+".wav"));
		if(audioFile.exists()) audioFile.delete();
		return filename;
	}

	public static void cancelCapture()
	{
		status=Status.STOPPED;
	}

	public static void playAudio(String filepath)
	{
		playAudio(new File(filepath));
	}

	public static void playAudio(File audioFile)
	{
		//if(status==Status.PLAYING)
			//stop();
		//new
	}

	public static void stopAudio()
	{
		if(status==Status.STOPPED) return;
			//stop();
	}

	private static AudioFormat getAudioFormat()
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
			baos=new ByteArrayOutputStream();
			status=Status.CAPTURING;
			try
			{
				while(status==Status.CAPTURING)
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