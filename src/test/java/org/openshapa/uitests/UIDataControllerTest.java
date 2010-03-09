package org.openshapa.uitests;

import static org.fest.reflect.core.Reflection.method;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;

import org.fest.swing.fixture.DataControllerFixture;
import org.fest.swing.fixture.JFileChooserFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.fest.swing.fixture.SpreadsheetCellFixture;
import org.fest.swing.fixture.SpreadsheetColumnFixture;
import org.fest.swing.fixture.SpreadsheetPanelFixture;
import org.fest.swing.timing.Timeout;
import org.fest.swing.util.Platform;
import org.openshapa.util.UIUtils;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.OpenSHAPAFileChooser;
import org.openshapa.views.continuous.PluginManager;
import org.openshapa.views.discrete.SpreadsheetPanel;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for the DataController.
 */
public final class UIDataControllerTest extends OpenSHAPATestClass {
    /**
     * Nominal test input.
     */
    private String[] nominalTestInput = {"Subject stands )up ", "$10,432" };

    /**
     * Nominal test output.
     */
    private String[] expectedNominalTestOutput =
            {"Subject stands up", "$10432"};

    /**
     * Text test input.
     */
    private String[] textTestInput = {"Subject stands up ", "$10,432"};

    /**
     * Integer test input.
     */
    private String[] integerTestInput = {"1a9", "10-432"};

    /**
     * Integer test output.
     */
    private String[] expectedIntegerTestOutput = {"19", "-43210"};

    /**
     * Float test input.
     */
    private String[] floatTestInput = {"1a.9", "10-43.2"};

    /**
     * Float test output.
     */
    private String[] expectedFloatTestOutput = {"1.90", "-43.2100"};

    /**
     * Standard test sequence focussing on jogging.
     * @param varName
     *            variable name
     * @param varType
     *            variable type
     * @param testInputArray
     *            test input values as array
     * @param testExpectedArray
     *            test expected values as array
     */
    private void standardSequence1(final String varName, final String varType,
            final String[] testInputArray, final String[] testExpectedArray) {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());
        ssPanel.deselectAll();
        UIUtils.createNewVariable(mainFrameFixture, varName, varType);

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
                "Data Viewer Controller");
        DataControllerFixture dcf =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog()
                        .component());
        // 3. Create new cell - so we have something to send key to because
        SpreadsheetColumnFixture column = ssPanel.column(varName);
        column.click();
        mainFrameFixture.clickMenuItemWithPath("Spreadsheet", "New Cell");
        // 4. Test Jogging back and forth.
        for (int i = 0; i < 5; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        }
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:200");

        for (int i = 0; i < 2; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD1);
        }
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:120");

        // 5. Test Create New Cell with Onset.
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);
        SpreadsheetCellFixture cell1 = column.cell(1);
        SpreadsheetCellFixture cell2 = column.cell(2);

        Assert.assertEquals(column.numOfCells(), 2);
        Assert.assertEquals(cell1.onsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(cell1.offsetTimestamp().text(), "00:00:00:119");

        Assert.assertEquals(cell2.onsetTimestamp().text(), "00:00:00:120");
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:000");

        // 6. Insert text into both cells.
        cell1.cellValue().enterText(testInputArray[0]);
        cell2.cellValue().enterText(testInputArray[1]);

        Assert.assertEquals(cell1.cellValue().text(), testExpectedArray[0]);
        Assert.assertEquals(cell2.cellValue().text(), testExpectedArray[1]);
        cell2.fillSelectCell(true);

        // 7. Jog forward 5 times and change cell onset.
        for (int i = 0; i < 5; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        }
        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:320");

        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD3);
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_DIVIDE);
        Assert.assertEquals(cell2.onsetTimestamp().text(), "00:00:00:360");

        // 8. Change cell offset.
        dcf.pressSetOffsetButton();
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:360");

        // 9. Jog back and forward, then create a new cell with onset
        for (int i = 0; i < 2; i++) {
            mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD1);
        }

        Assert.assertEquals(dcf.getCurrentTime(), "00:00:00:280");
        mainFrameFixture.robot.pressAndReleaseKeys(KeyEvent.VK_NUMPAD0);

        SpreadsheetCellFixture cell3 = column.cell(3);
        Assert.assertEquals(column.numOfCells(), 3);
        Assert.assertEquals(cell2.offsetTimestamp().text(), "00:00:00:360");
        Assert.assertEquals(cell3.offsetTimestamp().text(), "00:00:00:000");
        Assert.assertEquals(cell3.onsetTimestamp().text(), "00:00:00:280");

        // 10. Test data controller view onset, offset and find.
        for (int cellId = 1; cellId <= column.numOfCells(); cellId++) {
            cell1 = column.cell(cellId);
            //ssPanel.deselectAll();
            column.click();
            cell1.fillSelectCell(true);
            Assert.assertEquals(dcf.getFindOnset(), cell1.onsetTimestamp()
                    .text());
            Assert.assertEquals(dcf.getFindOffset(), cell1.offsetTimestamp()
                    .text());
            dcf.pressFindButton();
            Assert.assertEquals(dcf.getCurrentTime(), cell1.onsetTimestamp()
                    .text());
            dcf.pressShiftFindButton();
            Assert.assertEquals(dcf.getCurrentTime(), cell1.offsetTimestamp()
                    .text());
        }

        dcf.close();
    }

    /**
     * Runs standardsequence1 for different variable types (except matrix and
     * predicate), side by side.
     * @throws Exception
     *             any exception
     */
    @Test
    public void testStandardSequence1() throws Exception {
        mainFrameFixture.clickMenuItemWithPath("Controller",
                "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));
        final DataControllerFixture dcf =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog()
                        .component());
        // c. Open video
        String root = System.getProperty("testPath");
        final File videoFile = new File(root + "/ui/head_turns.mov");
        Assert.assertTrue(videoFile.exists());

        if (Platform.isOSX()) {
            final PluginManager pm = PluginManager.getInstance();

            GuiActionRunner.execute(new GuiTask() {
                public void executeInEDT() {
                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
                    fc.setVisible(false);
                    for (FileFilter f : pm.getPluginFileFilters()) {
                        fc.addChoosableFileFilter(f);
                    }
                    fc.setSelectedFile(videoFile);
                    method("openVideo").withParameterTypes(
                            OpenSHAPAFileChooser.class)
                            .in((DataControllerV) dcf.component()).invoke(fc);
                }
            });
        } else {
            dcf.button("addDataButton").click();
            JFileChooserFixture jfcf = dcf.fileChooser(Timeout.timeout(30000));
            jfcf.selectFile(videoFile).approve();
        }
        dcf.close();
        // Text
        standardSequence1("t", "text", textTestInput, textTestInput);
        // Integer
        standardSequence1("i", "integer", integerTestInput,
                expectedIntegerTestOutput);
        // Float
        standardSequence1("f", "float", floatTestInput,
                expectedFloatTestOutput);
        // Nominal
        standardSequence1("n", "nominal", nominalTestInput,
                expectedNominalTestOutput);
    }

    /**
     * Bug720.
     * Go Back should contain default value of 00:00:05:000.
     */
    @Test
    public void testBug720() {
        System.err.println(new Exception().getStackTrace()[0].getMethodName());
        // 1. Get Spreadsheet
        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
        SpreadsheetPanelFixture ssPanel =
                new SpreadsheetPanelFixture(mainFrameFixture.robot,
                        (SpreadsheetPanel) jPanel.component());

        // 2. Open Data Viewer Controller and get starting time
        mainFrameFixture.clickMenuItemWithPath("Controller",
                "Data Viewer Controller");
        mainFrameFixture.dialog().moveTo(new Point(300, 300));
        DataControllerFixture dcf =
                new DataControllerFixture(mainFrameFixture.robot,
                        (DataControllerV) mainFrameFixture.dialog()
                        .component());

        // 3. Confirm that Go Back text field is 00:00:05:000
        Assert.assertEquals("00:00:05:000",
                dcf.textBox("goBackTextField").text());
    }

    /**
     * Bug778.
     * If you are playing a movie, and you shuttle backwards (such that you
     * have a negative speed), your speed hits 0 when you reach the start of
     * the file. The stored shuttle speed does not get reset to zero though,
     * resulting in multiple forward shuttle presses being necessary to get
     * a positive playback speed again.
     */
//    @Test
//    public void testBug778() {
//        System.err.println(new Exception().getStackTrace()[0].getMethodName());
//        // 1. Get Spreadsheet
//        JPanelFixture jPanel = UIUtils.getSpreadsheet(mainFrameFixture);
//        SpreadsheetPanelFixture ssPanel =
//                new SpreadsheetPanelFixture(mainFrameFixture.robot,
//                        (SpreadsheetPanel) jPanel.component());
//
//        // 2. Open Data Viewer Controller and get starting time
//        mainFrameFixture.clickMenuItemWithPath("Controller",
//                "Data Viewer Controller");
//        mainFrameFixture.dialog().moveTo(new Point(300, 300));
//        DataControllerFixture dcf =
//                new DataControllerFixture(mainFrameFixture.robot,
//                        (DataControllerV) mainFrameFixture.dialog()
//                        .component());
//
//        // c. Open video
//        String root = System.getProperty("testPath");
//        final File videoFile = new File(root + "/ui/head_turns.mov");
//        Assert.assertTrue(videoFile.exists());
//
//        if (Platform.isOSX()) {
//            final PluginManager pm = PluginManager.getInstance();
//
//            GuiActionRunner.execute(new GuiTask() {
//                public void executeInEDT() {
//                    OpenSHAPAFileChooser fc = new OpenSHAPAFileChooser();
//                    fc.setVisible(false);
//                    for (FileFilter f : pm.getPluginFileFilters()) {
//                        fc.addChoosableFileFilter(f);
//                    }
//                    fc.setSelectedFile(videoFile);
//                    method("openVideo").withParameterTypes(
//                            OpenSHAPAFileChooser.class)
//                            .in((DataControllerV) dcf.component()).invoke(fc);
//                }
//            });
//        } else {
//            dcf.button("addDataButton").click();
//
//            JFileChooserFixture jfcf = dcf.fileChooser();
//            jfcf.selectFile(videoFile).approve();
//        }
//
//        // 2. Get window
//        Iterator it = dcf.getDataViewers().iterator();
//
//        Frame vid = ((Frame) it.next());
//        FrameFixture vidWindow = new FrameFixture(mainFrameFixture.robot, vid);
//    }
}
