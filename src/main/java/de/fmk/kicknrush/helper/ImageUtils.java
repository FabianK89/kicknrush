package de.fmk.kicknrush.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


/**
 * @author FabianK
 */
public class ImageUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);


	public static Optional<String> decodeBase64AndWriteToFS(final String  encoded,
	                                                        final String  rootDir,
	                                                        final String  team,
	                                                        final String  fileType,
	                                                        final boolean small) {
		final byte[] bytes;
		final Path   path;
		final String fileName;

		if(encoded == null || rootDir == null || team == null || fileType == null)
			return Optional.empty();

		bytes    = Base64Utils.decodeFromUrlSafeString(encoded);
		fileName = createFileName(team, fileType, small);
		path     = Paths.get(rootDir, ".kicknrush", "teams");

		try {
			Files.createDirectories(path);

			try (OutputStream os = Files.newOutputStream(path.resolve(fileName))) {
				os.write(bytes);
			}

			if (Files.isRegularFile(path.resolve(fileName)))
				return Optional.of(path.resolve(fileName).toString());
		}
		catch (IOException ioex) {
			LOGGER.error("Could not decode and write the logo of the team {} to {}.", team, path.toString(), ioex);
		}

		return Optional.empty();
	}


	public static Optional<String> encodeBase64(final String imagePath) {
		final byte[] bytes;
		final Path   path;

		if (imagePath == null)
			return Optional.empty();

		path = Paths.get(imagePath);

		if (!Files.isRegularFile(path))
			return Optional.empty();

		try (InputStream is = Files.newInputStream(path)) {
			bytes = StreamUtils.copyToByteArray(is);

			return Optional.of(Base64Utils.encodeToUrlSafeString(bytes));
		}
		catch (IOException ioex) {
			LOGGER.error("Could not encode the image at path {}.", imagePath, ioex);
			return Optional.empty();
		}
	}


	public static Optional<String> storeTeamLogo(final String  rootDir,
	                                             final String  url,
	                                             final String  team,
	                                             final boolean small)
			throws IOException {
		final Path   path;
		final String fileName;

		if (rootDir == null || url == null || team == null)
			return Optional.empty();

		fileName = createFileName(team, url.substring(url.lastIndexOf('.')), small);
		path     = Paths.get(rootDir, ".kicknrush", "teams");

		if (!Files.isDirectory(path))
			Files.createDirectories(path);

		return Optional.of(storeImage(url, path.resolve(fileName)).toString());
	}


	private static Path storeImage(final String url, final Path destination) throws IOException {
		final URLConnection connection;

		byte[] buf;
		int    length;

		buf = new byte[1024];

		connection = new URL(url).openConnection();

		try (InputStream  is = connection.getInputStream();
		     OutputStream os = new FileOutputStream(destination.toFile())) {
			while ((length = is.read(buf)) > 0)
				os.write(buf, 0, length);
		}

		return destination;
	}


	private static String createFileName(final String name, final String fileType, final boolean small) {
		String fileName = name.toLowerCase();

		fileName = fileName.replace(" ", "_");
		fileName = fileName.replace("ä", "ae");
		fileName = fileName.replace("ö", "oe");
		fileName = fileName.replace("ü", "ue");
		fileName = fileName.replace("ß", "ss");
		fileName = fileName.replace(".", "");

		if (small)
			fileName = fileName.concat("_small");

		return fileName.concat(fileType);
	}
}
