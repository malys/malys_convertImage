import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;

public class Main implements IConvertImage {

	static Mode mode=Mode.NORMAL;
	static Properties properties;
	
	static Boolean isNoelia = false;
	static String imageMagick = exe;
	static String font = "Arial";
	static String fontSize = "18";
	static String pathRoot = root;
	static String copyright = "Noelia";
	static String annotate = "10";
	
	static String pathIn;
	static String pathOut;
	
	static public  Integer xResolution = 800;
	static public  Integer yResolution = 800;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {


		if (args.length == 1) {

			if(args[0].indexOf(".properties")>-1){

				mode = Mode.EXTERNAL;
				properties = new Properties();

				File prop = new File(args[0]);
				if (prop.exists()) {
					try {
						properties.load(new FileReader(prop));
						
						pathRoot = (String) properties.get("pathRoot");
						
						copyright =  (String) properties.get("copyright");
						
						font = (String) properties.get("font");
						
						fontSize = (String) properties.get("fontSize");
						
						annotate = ((String) properties.get("annotateSup"));
						annotate = annotate.replaceAll("%annotate%",(String)properties.get("annotate"));
						imageMagick = (String) properties.get("exe");
						xResolution = Integer.valueOf((String) properties.get("xResolution"));
						yResolution = Integer.valueOf((String) properties.get("yResolution"));
						
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			
			}
			else{
				pathRoot = args[1];	
			}
			

		}else if (args.length > 2) {
			isNoelia = true;
			int i = 0;
			pathRoot = args[i];
			i++;
			copyright = args[i];
			i++;
			font = args[i];
			i++;
			fontSize = args[i];
			i++;
			annotate = args[i];
		}

		 pathIn = pathRoot + "in";
		 pathOut = pathRoot + "out/new";

		// liste des fichiers
		try {

			if (isNoelia) {
				imageMagick = exeNoelia;
			} else if (mode == Mode.NORMAL){
				imageMagick = exe;

			}

			List<String> list = new ArrayList<String>();
			FileUtility.getFilesRec(list, pathIn, ".jpg");

			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String fileName = iter.next().replace("\\", "/");
				// B:\media\image\in\cris\22\1024_0000.jpg
				
				normalMode(fileName);
				
				
				

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void normalMode(String fileName) throws InterruptedException{
		String fileNameClean = fileName.replace(pathIn, "");
		String fileOut = pathOut + fileNameClean;
		String nameThumb = new File(fileName).getName();
		String thumb = pathOut
				+ fileNameClean.replace(nameThumb, "thumbnail/TN-"
						+ nameThumb);
		String bat = imageMagick.replace("%resolution%",
				readExif(fileName)).replace("%1",
				"\"" + fileName + "\"").replace("%reduced%",
				"\"" + fileOut + "\"").replace("%thumb%",
				"\"" + thumb + "\"").replace("%annotate%", annotate).replace("%font%", font).replace(
						"%fontSize%", fontSize).replace("%copyright%",
								copyright);
		// System.out.println(root+"ImageMagick/convert.exe"+bat.replace(root,"").replace("/",
		// "\\"));
		try {
			Runtime r = Runtime.getRuntime();

			new File(pathOut).delete();
			new File(thumb).getParentFile().mkdirs();
			String cmd = pathRoot + "ImageMagick/convert.exe "
					+ bat.replace(pathRoot, "").replace("/", "\\");
			Process p =r.exec(cmd);
			System.out.println(cmd );
			
			if (p.exitValue() !=0){
				System.out.println(fileName + " erreur");
			}else{
				System.out.println(fileName + " traite");
			}
			Thread.sleep(frequency);
			// System.out.println(process.exitValue());

		} catch (Exception e) {
			 System.out.println("erreur d'execution "+e.getMessage());
			Thread.sleep(frequency);
		}
	}

	private static String readExif(String name) {
		String resolution = xResolution + "x" + yResolution;
		String x = null;
		String y = null;
		try {
			Metadata metadata = new Metadata();
			new ExifReader(new File(name)).extract(metadata);
			Iterator<Directory> directories = metadata.getDirectoryIterator();

			while (directories.hasNext()) {
				Directory directory = directories.next();
				Iterator<Tag> tags = directory.getTagIterator();
				while (tags.hasNext()) {
					Tag tag = tags.next();
					if (tag.getTagName().toLowerCase().indexOf("width") != -1) {
						x = tag.getDescription();
					} else if (tag.getTagName().toLowerCase().indexOf("heigh") != -1) {
						y = tag.getDescription();
					}
					if (x != null && y != null) {
						return computeResolution(x.replace(" pixels", ""), y
								.replace(" pixels", ""));
					}
				}
			}
		} catch (JpegProcessingException e) {
			System.out.println(e.getMessage());
		} catch (MetadataException e) {
			System.out.println(e.getMessage());
		}
		return resolution;

	}

	private static String computeResolution(String x, String y) {
		Double ratio = null;
		if (Integer.valueOf(x) > Integer.valueOf(y)) {
			ratio = yResolution / Double.valueOf(x);
			return xResolution + "x" + Double.valueOf(y) * ratio;
		} else {
			ratio = xResolution / Double.valueOf(y);
			return Double.valueOf(x) * ratio + "x" + yResolution;
		}
	}
}
