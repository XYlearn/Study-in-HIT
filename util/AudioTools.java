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
	private static stopListener sl;
	private static AudioFormat af;
	private static TargetDataLine td;
	private static SourceDataLine sd;
	private static ByteArrayOutputStream baos;
	private static ByteArrayInputStream bais;
	private static AudioInputStream ais;
	private enum Status{STOPPED,CAPTURING,PLAYING};
	private static Status status=Status.STOPPED;
	private static final String CLASSPATH=AudioTools.class.getResource("").getPath();
	AudioTools(){}

	/*public static void main(String[] args)
	{
		Thread a=new Thread(){
			public void run()
			{
				AudioTools.startCapture();
				try
				{
					sleep(20000);//test time of capture
				}
				catch(Exception e)
				{
					System.out.println("error!");
				}
				String name=AudioTools.stopAndSaveCapture();
				AudioTools.playAudio(CLASSPATH+name+".wav");
			}
		};
		try
		{
			a.start();
			a.join();
		}
		catch(Exception e)
		{
			System.out.println("Error!");
		}
	}*/

	/*public static void main(String[] args)
	{
		AudioTools.playAudio(CLASSPATH+"test.mp3");
	}*/

	//To test this class,run one of the main() above,using(firstly cd to the folder)
	//javac -cp ".;jl1.0.1.jar;mp3spi1.9.5.jar;tritonus_share.jar" AudioTools.java
	//java -cp ".;jl1.0.1.jar;mp3spi1.9.5.jar;tritonus_share.jar" AudioTools

	public static boolean startCapture()
	{
		if(status==Status.CAPTURING) return true;
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

	public static void playAudio(String filepath,stopListener mysl)
	{
		playAudio(new File(filepath),sl);
	}

	public static void playAudio(File audioFile,stopListener mysl)
	{
		stopAudio();
		sl.stop();
		try{Thread.sleep(10);}catch(Exception e){}
		sl=mysl;
		status=Status.PLAYING;
		try
		{
			ais=AudioSystem.getAudioInputStream(audioFile);
		}
		catch(Exception e)
		{
			System.out.println("errorrrrrrrrr!");
			return;
		}
		af=ais.getFormat();
		//转换MP3文件编码
		if(af.getEncoding()!=AudioFormat.Encoding.PCM_SIGNED)
		{
			af=new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				af.getSampleRate(),16,
				af.getChannels(),af.getChannels()*2,
				af.getSampleRate(),false);
			ais=AudioSystem.getAudioInputStream(af,ais);
		}
		DataLine.Info info=new DataLine.Info(
			SourceDataLine.class,af,AudioSystem.NOT_SPECIFIED);
		try
		{
			sd=(SourceDataLine)AudioSystem.getLine(info);
			sd.open(af);
			sd.start();
		}
		catch(Exception e)
		{
			System.out.println("error!!");
			return;
		}
		new Thread(new MyPlay()).start();
	}

	public static void stopAudio() //stop playing or capture
	{
		status=Status.STOPPED;
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

	private static class MyPlay implements Runnable
	{
		byte[] byteArray=new byte[320];

		public void run()
		{
			int tmpn;
			try
			{
				while(status==Status.PLAYING&&
					(tmpn=ais.read(byteArray,0,byteArray.length))!=-1)
					if(tmpn>0) sd.write(byteArray,0,tmpn);
			}
			catch(Exception e){}
			finally
			{
				sl.stop();
				sd.drain();
				sd.close();
			}
		}
	}
}