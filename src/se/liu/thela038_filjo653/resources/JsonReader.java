package se.liu.thela038_filjo653.resources;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that reads json files. New files can be added by creating a new value in VALUES enum and map
 */
public class JsonReader
{
    public enum VALUES
    {
	WALKER, COP, BOSS, PISTOL, UZI, RIFLE, RPG, MELEE, MELEE_BOSS
    }

    private static final Logger LOGGER = Logger.getLogger("");

    private static final Map<VALUES, String> VALUES_MAP =
	    Map.of(VALUES.PISTOL, "pistol", VALUES.UZI, "uzi", VALUES.RIFLE, "ak", VALUES.RPG, "rpg", VALUES.WALKER, "walker", VALUES.COP,
		   "cop", VALUES.BOSS, "boss", VALUES.MELEE, "melee", VALUES.MELEE_BOSS, "meleeBoss");

    public static Map<?, ?> readJson(VALUES value) throws IOException, FileNotFoundException {
	String fileName = VALUES_MAP.get(value);

	Gson gson = new Gson();

	// The slash triggers a warning in the automatic code inspection. That can be ignored because this is a resource URL.
	final String name = "json/" + fileName + ".json";
	InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(name);

	if (inputStream == null) {
	    throw new FileNotFoundException("Could not find resource " + name);
	}

	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

	Map<?, ?> map = gson.fromJson(reader, Map.class);
	reader.close();

	return map;
    }

    /**
     * Tries to load json data. If it fails, the program will be terminated. This should only be used where the json data is critical for
     * the program and it wont work without it.
     *
     * @param value Value to load
     *
     * @return Map with json data.
     */
    public static Map<?, ?> readJsonCritical(VALUES value) {
	try {
	    return readJson(value);
	} catch (IOException e) {
	    // This catch clause triggers two warnings in the automatic code inspection.
	    // "CatchReturnNull" can be ignored because it does not actually return null, since the program is terminated before that.
	    // "CatchWithExit" can be ignored because this exception must be caused by a bug or installation problem, and this function is
	    // used where the program won't work without the json.

	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    System.exit(1);

	    // To avoid "missing return statement". The code will never reach here, because the program is terminated.
	    return null;
	}
    }
}
