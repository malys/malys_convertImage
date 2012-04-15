import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;

public class Main implements IConvertImage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Boolean isNoelia = false;
		String imageMagick = exe;
		String font = "Arial";
		String fontSize = "18";
		String pathRoot = root;
		String copyrigth = "Noelia";
		String annotate = "10";

		if (args.length == 1) {

			pathRoot = args[1];

		}

		if (args.length > 2) {
			isNoelia = true;
			int i = 0;
			pathRoot = args[i];
			i++;
			copyrigth = args[i];
			i++;
			font = args[i];
			i++;
			fontSize = args[i];
			i++;
			annotate = args[i];
		}

		String pathIn = pathRoot + "in";
		String pathOut = pathRoot + "out/new";

		// liste des fichiers
		try {

			if (isNoelia) {
				imageMagick = exeNoelia;
			} else {
				imageMagick = exe;

			}

			List<String> list = new ArrayList<String>();
			FileUtility.getFilesRec(list, pathIn, ".jpg");

			Iterator<String> iter = list.iterator();
			while (iter.hasNext()) {
				String fileName = iter.next().replace("\\", "/");
				// B:\media\image\in\cris\22\1024_0000.jpg
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
						"\"" + thumb + "\"").replace("%font%", font).replace(
						"%fontSize%", fontSize).replace("%copyrigth%",
						copyrigth).replace("%annotate%", annotate);
				// System.out.println(root+"ImageMagick/convert.exe"+bat.replace(root,"").replace("/",
				// "\\"));
				try {
					Runtime r = Runtime.getRuntime();

					new File(pathOut).delete();
					new File(thumb).getParentFile().mkdirs();
					String cmd = pathRoot + "ImageMagick/convert.exe"
							+ bat.replace(pathRoot, "").replace("/", "\\");
					r.exec(cmd);
					System.out.println(cmd);
					System.out.println(fileName + " traite");
					Thread.sleep(frequency);
					// System.out.println(process.exitValue());

				} catch (Exception e) {
					// System.out.println("erreur d'execution "+e.getMessage());
					Thread.sleep(frequency);
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
