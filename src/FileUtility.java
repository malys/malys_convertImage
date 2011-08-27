

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Ensemble de méthodes pour le traitement de fichier
 * 
 * @author symal
 * 
 */
public class FileUtility {

	// static TLogger log = TLogger.getLogger("File");


	/**
	 * Transforme un fichier en Liste
	 * 
	 * @param file
	 * @return Liste correspondant au ligne
	 */
	public static List<String> convertFileToList(File file) {

		List<String> total = null;

		String ligne = null;

		BufferedReader ficTexte = null;

		try {
			ficTexte = new BufferedReader(new FileReader(file));
			total = new ArrayList<String>();

			if (ficTexte == null) {

			throw new FileNotFoundException();

			}

			while ((ligne = ficTexte.readLine()) != null) {

				total.add(ligne);
			}
			ficTexte.close();

			return total;

		}
		catch (FileNotFoundException e) {
			System.out.println("[ source = FileUtility ][ id = " + file.getName() + " ][ code = ][label = fichier introuvable ]");
			return null;
		}
		catch (Exception e) {
			System.out.println("[ source = FileUtility ][ id = " + file.getName() + " ][ code = ][label = convertion en liste impossible ]");
			return null;
		}

	}

	/**
	 * Copie un fichier
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFile(File src, String chemin) throws IOException {
		try {
			if (!src.exists()) throw new IOException("File not found '" + src.getAbsolutePath() + "'");

			if (!chemin.endsWith("/")) {
				chemin = chemin + "/";
			}

			File dest = new File(chemin + src.getName());

			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));

			byte[] read = new byte[128];
			int len = 128;
			while ((len = in.read(read)) > 0)
				out.write(read, 0, len);

			out.flush();
			out.close();
			in.close();
		}
		catch (Exception e) {
			System.out.println("[ source = XSLT ][ id = " + src + " ][ code = ][label = fichier ne peut pas être copié " + e.getMessage() + "]");
		}
	}

	/**
	 * Crée un fichier
	 * 
	 * @param name
	 */
	public static boolean createFile(String name) throws Exception {
		try {
			FileWriter temp = new FileWriter(name, false);
			String texte = new Date().toString();
			temp.write(texte);
			temp.close();

			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * Coupe un fichier vers la destination et supprime le répertoire s'il vide
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void cutAndClean(File src, String dest) throws Exception {
		cutFile(src, dest);
	
			src.getParentFile().delete();
		
	}

	/**
	 * Coupe un fichier vers la destination
	 * 
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void cutFile(File src, String dest) throws Exception {
		try {
			copyFile(src, dest);
			src.delete();
		}
		catch (Exception e) {
			throw new Exception("Couper le fichier " + src.getAbsolutePath() + " vers " + dest + " impossible");
		}
	}

	public static void deleteDirectory(File dir) {
		dir.delete();
	}

	public static boolean emptyDirectory(File dir) {
		String[] children = dir.list();
		if (children == null || children.length < 1) {
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Donne la liste des fichiers des sous répertoires
	 * 
	 * @param root
	 */
	public static void getFilesRec(List<String> allFiles, String root, final String suffixe) throws Exception {

		// On filtre les fichiers xml
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return !name.endsWith("temp");
			}
		};
		File f = new File(root);
		File[] listFiles = f.listFiles(filter);

		if (listFiles == null) { throw new Exception("répertoire manquant pour " + root); }

		for (int i = 0; i < listFiles.length; i++) {
			if (listFiles[i].isDirectory()) getFilesRec(allFiles, listFiles[i].toString(), suffixe);
			else {
				// on récupére seulement les messeagexxxx.xml
				if (listFiles[i].getName().toLowerCase().endsWith(suffixe) ) {
					allFiles.add(listFiles[i].toString());
				}
			}

		}
	}

	/**
	 * Donne la liste des fichier des sous répertoires
	 * 
	 * @param root
	 */
	public static Object[] getFilesRec(String root, String suffixe) throws Exception {
		List<String> list = new ArrayList<String>();
		getFilesRec(list, root, suffixe);

		return list.toArray();
	}

	/**
	 * Donne le fichier le plus ancien dans l'arborescence du répertoire fourni
	 * 
	 * @param directoryPath
	 * @param prefix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String oldFile(String directoryPath, final String prefix) throws Exception {
		// Tri la liste
		try {
			Object[] children = getFilesRec(directoryPath, prefix);

			if (children.length != 0 && children[0] != null) {
				Arrays.sort(children, new AlphabeticComparator());
				return children[0].toString();
			}
			else return null;
		}
		catch (Exception e) {
			System.out.println("[ source =Fichier ][ id = " + directoryPath + " ][ code = ][label = fichiers introuvables " + e.getMessage() + "]");
			return null;
		}
	}

	/**
	 * Découpe en n fichiers de nbrLigne
	 * 
	 * @param fileName
	 * @param nbrLigne
	 * @return
	 * @throws IOException
	 */
	public static List<File> splitTextFile(File fileName, int nbrLigne, String header) throws IOException {

		List<File> splittedFile = new ArrayList<File>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			StringBuffer fileContent = new StringBuffer();
			if (header != null) {
				fileContent.append(header);
			}

			String line = null;
			int counter = 0;
			int nameTag = 0;
			String name = null;

			while ((line = reader.readLine()) != null) {
				fileContent.append(line + "\n");
				counter++;

				if (counter > nbrLigne) {
					name = fileName.getParentFile().getAbsolutePath() + "/splt" + nameTag + "_" + fileName.getName();
					splittedFile.add(new File(name));
					writeFile(new File(name), String.valueOf(fileContent));
					counter = 0;
					nameTag++;
					fileContent = new StringBuffer();
					if (header != null) {
						fileContent.append(header + "\n");
					}

				}

			}

			if (fileContent.length() > 0) {
				name = fileName.getParentFile().getAbsolutePath() + "/splt" + nameTag + "_" + fileName.getName();
				splittedFile.add(new File(name));
				writeFile(new File(name), String.valueOf(fileContent));
			}

		}
		catch (Exception e) {
			System.out.println("[ source =Fichier ][ id = " + fileName.getName() + " ][ code = ][label = Découpe du fichier impossible " + e.getMessage() + "]");
			splittedFile = new ArrayList<File>();
			splittedFile.add(fileName);
		}

		return splittedFile;

	}

	/**
	 * Enregistre le fichier en mémoire
	 * 
	 * @param destFile
	 * @param content
	 * @throws IOException
	 */
	private static void writeFile(File destFile, String content) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
		writer.write(content);
		writer.flush();
		writer.close();
		writer = null;
	}

	/**
	 * Vérifie la présence de fichier
	 * 
	 * @param name
	 * @return
	 */
	public static boolean controlFile(String name) {
		File file = new File(name);
		if (file.exists()) {
			return true;
		}
		else {
			return false;
		}
	}

}
