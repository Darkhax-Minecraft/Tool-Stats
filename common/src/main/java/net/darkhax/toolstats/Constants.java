package net.darkhax.toolstats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

public class Constants {

    public static final String MOD_ID = "toolstats";
    public static final String MOD_NAME = "Tool Stats";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
}