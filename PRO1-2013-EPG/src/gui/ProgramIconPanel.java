/**
 * 
 */
package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import tvdata.Program;

/**
 * Panel pro vykresloveni obrazku aktualne vybraneho programu
 * @author Pavel Janecka
 */
public class ProgramIconPanel extends JComponent {
	// cache pro obrazky aby se nemuseli nacitat znovu a znovu
	private Map<URL, BufferedImage> imageCache = new HashMap<URL, BufferedImage>();
	private Program currentProgram;
	private final String STR_NO_IMAGE = "No image";
	
	@Override
	protected void paintComponent(Graphics g) {
		// smazani plochy komponenty
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		// pokud je nastaveny program a ma nejakou URL obrazku
		if (currentProgram != null && currentProgram.getImageURL() != null) {
			// pokus o ziskani obrazku z cache (pokud nejde nacist je null)
			BufferedImage img = getFromCache(currentProgram.getImageURL());
			if (img != null) {
				// vykresleni na stred komponenty
				g.drawImage(img, this.getWidth() / 2 - img.getWidth() / 2, this.getHeight() / 2 - img.getHeight() / 2, null);
			}
		// program nema obrazek, nebo je chybna URL, vypsani chybovych hlasek
		} else {
			String str = Utils.STR_NO_PROGRAM;
			if (currentProgram != null) str = STR_NO_IMAGE;

			g.setFont(Utils.DEFAULT_APP_FONT);
			g.setColor(Utils.DEFAULT_APP_FONT_COLOR);
			Utils.drawAntialiasedStringInMiddle(str, g, this);
		}
	}

	/**
	 * Pokusi se ziskat obrazek z cache. 
	 * Pokud neni v cache nahraje obrazek z uvedene URL a ulozi do cache pro priste.
	 * @param url {@link URL} instance jako klic do cache
	 * @return {@link BufferedImage} instance
	 */
	private BufferedImage getFromCache(URL url) {
		BufferedImage img = imageCache.get(url);
		if (img == null) {
			try {
				img = ImageIO.read(url);
				imageCache.put(url, img);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}
	
	/**
	 * Set selected program as current focused program
	 * @param program {@link Program} instance
	 */
	public void setProgram(Program program) {
		this.currentProgram = program;
	}
}
