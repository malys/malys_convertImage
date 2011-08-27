public interface IConvertImage {

	/* Commun */
	public final String POLOROID = "";
	// -bordercolor white -border 6 -bordercolor grey60 -border 1 -background
	// none -background black ( +clone -shadow 60x4+4+4 ) +swap -background
	// white -flatten

	public final String root = "B:/media/image/";

	public final String pathIn = root + "in";
	public final String pathOut = root + "out/new";
	public final Integer frequency = 1500;

	public final Integer xResolution = 800;
	public final Integer yResolution = 800;

	/* Mes cliches */
	public final String copyright = " \"copyright cmolla\" ";
	public final String annotate = " -gravity southeast -stroke \"#000C\" -strokewidth 2 -annotate +10+10  "
			+ copyright
			+ " -stroke none -fill white -annotate +10+10 "
			+ copyright;
	public final String exe = " %1 ( +clone -resize %resolution% -auto-orient "
			+ annotate
			+ " -compress JPEG -quality 90 -write %reduced% +delete ) -resize x128 -auto-orient "
			+ POLOROID
			+ " -compress JPEG -quality 90 -sampling-factor 2x1 -strip %thumb%";

	/*Noelia */
	public final String copyrightNoelia = " \"Noelia\" ";
	public final String annotateNoelia = " -gravity southeast -stroke \"#000C\" -strokewidth 2 -font %font% -pointsize %fontSize% -annotate +10+10  "
			+ copyrightNoelia
			+ " -stroke none -fill white -annotate +10+10 "
			+ copyrightNoelia;
	public final String exeNoelia = " %1 ( +clone -resize %resolution% -auto-orient "
			+ annotateNoelia
			+ " -compress JPEG -quality 90 -write %reduced% +delete ) -resize x128 -auto-orient "
			+ POLOROID
			+ " -compress JPEG -quality 90 -sampling-factor 2x1 -strip %thumb%";

}
