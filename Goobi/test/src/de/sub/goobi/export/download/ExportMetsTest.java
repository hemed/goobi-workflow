package de.sub.goobi.export.download;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information.
 *          - https://goobi.io
 *          - https://www.intranda.com
 *          - https://github.com/intranda/goobi-workflow
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.easymock.EasyMock;
import org.goobi.beans.Process;
import org.goobi.beans.Ruleset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.sub.goobi.config.ConfigProjectsTest;
import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.helper.JwtHelper;
import de.sub.goobi.metadaten.MetadatenHelper;
import de.sub.goobi.mock.MockProcess;
import ugh.fileformats.mets.MetsMods;
import ugh.fileformats.mets.MetsModsImportExport;


@RunWith(PowerMockRunner.class)
@PrepareForTest({MetadatenHelper.class, JwtHelper.class})
@PowerMockIgnore("javax.management.*")
public class ExportMetsTest {

    private Process testProcess = null;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        Path template = Paths.get(ConfigProjectsTest.class.getClassLoader().getResource(".").getFile());
        String goobiFolder = template.getParent().getParent().getParent().toString() + "/test/resources/";
        ConfigurationHelper.CONFIG_FILE_NAME = goobiFolder + "config/goobi_config.properties";
        ConfigurationHelper.resetConfigurationFile();
        ConfigurationHelper.getInstance().setParameter("goobiFolder", goobiFolder);
        testProcess = MockProcess.createProcess();

        PowerMock.mockStatic(MetadatenHelper.class);
        EasyMock.expect(MetadatenHelper.getMetaFileType(EasyMock.anyString())).andReturn("metsmods").anyTimes();
        EasyMock.expect(MetadatenHelper.getFileformatByName(EasyMock.anyString(), EasyMock.anyObject(Ruleset.class))).andReturn(new MetsMods(testProcess.getRegelsatz().getPreferences())).anyTimes();
        EasyMock.expect(MetadatenHelper.getExportFileformatByName(EasyMock.anyString(), EasyMock.anyObject(Ruleset.class))).andReturn(new MetsModsImportExport(testProcess.getRegelsatz().getPreferences())).anyTimes();
        PowerMock.replay(MetadatenHelper.class);

        PowerMock.mockStatic(JwtHelper.class);
        EasyMock.expect(JwtHelper.createApiToken(EasyMock.anyString(), EasyMock.anyObject())).andReturn("12356").anyTimes();
        PowerMock.replay(JwtHelper.class);
    }

    @Test
    public void testStartExport() throws Exception {
        Path destination = folder.newFolder("export").toPath();
        Files.createDirectories(destination);
        ExportMets exportMets = new ExportMets();
        assertNotNull(exportMets);
        exportMets.startExport(testProcess, destination.toString());
    }

}
