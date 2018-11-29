package de.intranda.goobi.plugins;

import lombok.EqualsAndHashCode;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
@EqualsAndHashCode(callSuper = false)
public class DanteLicencePlugin extends AbstractDantePlugin {

    // class name for goobi
    private static final String pluginTitle = "DanteLicence";
    // name of database
    private static final String vocabulary = "license";
    // name of main field containing the representative value 
    private static final String label = "NORM_LABEL_en";

    @Override
    public String getVocabulary() {
        return vocabulary;
    }

    @Override
    public String getTitle() {
        return pluginTitle;
    }

    @Override
    public String getLabel() {
        return label;
    }
}