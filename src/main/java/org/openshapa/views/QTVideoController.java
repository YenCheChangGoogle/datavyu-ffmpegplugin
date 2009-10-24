package org.openshapa.views;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SimpleTimeZone;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.CreateNewCellC;
import org.openshapa.controllers.SetNewCellStopTimeC;
import org.openshapa.controllers.SetSelectedCellStartTimeC;
import org.openshapa.util.FloatUtils;
import org.openshapa.util.ClockTimer;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.PluginManager;

/**
 * Quicktime video controller.
 */
public final class QTVideoController
        extends OpenSHAPADialog
        implements org.openshapa.util.ClockTimer.Listener {

    //--------------------------------------------------------------------------
    // [static]
    //

    /** Logger for this class. */
    private static Logger logger = Logger.getLogger(QTVideoController.class);

    /** One second in milliseconds. */
    private static final long ONE_SECOND = 1000L;

    /** Rate of playback for rewinding. */
    private static final float REWIND_RATE = -32F;

    /** Rate of normal playback. */
    private static final float PLAY_RATE = 1F;

    /** Rate of playback for fast forwarding. */
    private static final float FFORWARD_RATE = 32F;

    /** Sequence of allowable shuttle rates. */
    private static final float[] SHUTTLE_RATES;

    // Initialize SHUTTLE_RATES
    // values: [ (2^-5), ..., (2^0), ..., (2^5) ]
    static {
        int POWER = 5;
        SHUTTLE_RATES = new float[2 * POWER + 1];
        float value = 1;
        SHUTTLE_RATES[POWER] = value;
        for (int i = 1; i <= POWER; ++i) {
            value *= 2;
            SHUTTLE_RATES[POWER + i] = value;
            SHUTTLE_RATES[POWER - i] = 1F / value;
        }
    }

    /**
     * Enumeration of shuttle directions.
     */
    enum ShuttleDirection {
        BACKWARDS(-1),
        UNDEFINED(0),
        FORWARDS(1);

        private int parameter;

        ShuttleDirection(final int p) { this.parameter = p; }

        public int getParameter() { return parameter; }
    }

    /** Format for representing time. */
    private static final DateFormat CLOCK_FORMAT;

    // initialize standard date format for clock display.
    static {
        CLOCK_FORMAT = new SimpleDateFormat("HH:mm:ss:SSS");
        CLOCK_FORMAT.setTimeZone(new SimpleTimeZone(0, "NO_ZONE"));
    }


    //--------------------------------------------------------------------------
    //
    //

    /** The list of viewers associated with this controller. */
    private Set<DataViewer> viewers;

    /** Stores the highest frame rate for all available viewers. */
    private float currentFPS = 1F;

    /** Shuttle status flag. */
    private ShuttleDirection shuttleDirection = ShuttleDirection.UNDEFINED;

    /** Index of current shuttle rate. */
    private int shuttleRate;

    /** Clock timer. */
    private ClockTimer clock = new ClockTimer();


    //--------------------------------------------------------------------------
    // [initialization]
    //

    /**
     * Constructor. Creates a new QTVideoController.
     *
     * @param parent The parent of this form.
     * @param modal Should the dialog be modal or not?
     */
    public QTVideoController(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        clock.registerListener(this);

        initComponents();
        setName(this.getClass().getSimpleName());
        viewers = new HashSet<DataViewer>();

        // Hide unsupported features.
        this.syncVideoButton.setEnabled(false);
        this.syncButton.setEnabled(false);
        this.syncCtrlButton.setEnabled(false);
        this.timestampSetupButton.setEnabled(false);
    }

    //--------------------------------------------------------------------------
    // [interface] org.openshapa.util.ClockTimer.Listener
    //

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStart(final long time) {
        setCurrentTime(time);
        for (DataViewer viewer : viewers) {
            viewer.seekTo(time);
            viewer.play();
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockTick(final long time) {
        setCurrentTime(time);
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStop(final long time) {
        setCurrentTime(time);
        for (DataViewer viewer : viewers) {
            viewer.stop();
            viewer.seekTo(time);
        }
    }

    /**
     * @param rate Current (updated) clock rate.
     */
    public void clockRate(final float rate) {
        lblSpeed.setText(FloatUtils.doubleToFractionStr(new Double(rate)));
        for (DataViewer viewer : viewers) {
            viewer.setPlaybackSpeed(rate);
            if (!clock.isStopped()) {
                viewer.play();
            }
        }
    }

    /**
     * @param time Current clock time in milliseconds.
     */
    public void clockStep(final long time) {
        setCurrentTime(time);
        for (DataViewer viewer : viewers) { viewer.seekTo(time); }
    }

    //--------------------------------------------------------------------------
    //
    //

    /**
     * Set time location for data streams.
     *
     * @param milliseconds The millisecond time.
     */
    public void setCurrentTime(final long milliseconds) {
        timestampLabel.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Get the current master clock time for the controller.
     *
     * @return Time in milliseconds.
     */
    private long getCurrentTime() {
        return clock.getTime();
    }

    /**
     * Remove the specifed viewer form the controller.
     *
     * @param viewer The viewer to shutdown.
     * @return True if the controller contained this viewer.
     */
    public boolean shutdown(final DataViewer viewer) {
        return viewers.remove(viewer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        timestampLabel = new javax.swing.JLabel();
        openVideoButton = new javax.swing.JButton();
        lblSpeed = new javax.swing.JLabel();
        gridButtonPanel = new javax.swing.JPanel();
        syncCtrlButton = new javax.swing.JButton();
        syncButton = new javax.swing.JButton();
        setCellOnsetButton = new javax.swing.JButton();
        setCellOffsetButton = new javax.swing.JButton();
        rewindButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        goBackButton = new javax.swing.JButton();
        shuttleBackButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        shuttleForwardButton = new javax.swing.JButton();
        findButton = new javax.swing.JButton();
        jogBackButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        jogForwardButton = new javax.swing.JButton();
        rightTimePanel = new javax.swing.JPanel();
        syncVideoButton = new javax.swing.JButton();
        goBackTextField = new javax.swing.JTextField();
        findTextField = new javax.swing.JTextField();
        bottomPanel = new javax.swing.JPanel();
        leftButtonPanel = new javax.swing.JPanel();
        createNewCellButton = new javax.swing.JButton();
        setNewCellOnsetButton = new javax.swing.JButton();
        fillerPanel = new javax.swing.JPanel();
        timestampSetupButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quicktime Video Controller");
        setName(""); // NOI18N

        mainPanel.setBackground(java.awt.Color.white);
        mainPanel.setLayout(new java.awt.BorderLayout(2, 0));

        topPanel.setBackground(java.awt.Color.white);
        topPanel.setLayout(new java.awt.BorderLayout());

        timestampLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timestampLabel.setText("00:00:00:000");
        timestampLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        topPanel.add(timestampLabel, java.awt.BorderLayout.CENTER);

        openVideoButton.setBackground(java.awt.Color.white);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getResourceMap(QTVideoController.class);
        openVideoButton.setText(resourceMap.getString("openVideoButton.text")); // NOI18N
        openVideoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openVideoButtonActionPerformed(evt);
            }
        });
        topPanel.add(openVideoButton, java.awt.BorderLayout.LINE_START);

        lblSpeed.setText("0");
        lblSpeed.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 2));
        topPanel.add(lblSpeed, java.awt.BorderLayout.LINE_END);

        mainPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        gridButtonPanel.setBackground(java.awt.Color.white);
        gridButtonPanel.setLayout(new java.awt.GridLayout(4, 4));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.openshapa.OpenSHAPA.class).getContext().getActionMap(QTVideoController.class, this);
        syncCtrlButton.setAction(actionMap.get("syncCtrlAction")); // NOI18N
        syncCtrlButton.setMaximumSize(new java.awt.Dimension(32, 32));
        syncCtrlButton.setMinimumSize(new java.awt.Dimension(32, 32));
        syncCtrlButton.setPreferredSize(new java.awt.Dimension(32, 32));
        gridButtonPanel.add(syncCtrlButton);

        syncButton.setAction(actionMap.get("syncAction")); // NOI18N
        gridButtonPanel.add(syncButton);

        setCellOnsetButton.setAction(actionMap.get("setCellOnsetAction")); // NOI18N
        setCellOnsetButton.setIcon(resourceMap.getIcon("setCellOnsetButton.icon")); // NOI18N
        gridButtonPanel.add(setCellOnsetButton);

        setCellOffsetButton.setAction(actionMap.get("setCellOffsetAction")); // NOI18N
        setCellOffsetButton.setIcon(resourceMap.getIcon("setCellOffsetButton.icon")); // NOI18N
        gridButtonPanel.add(setCellOffsetButton);

        rewindButton.setAction(actionMap.get("rewindAction")); // NOI18N
        rewindButton.setIcon(resourceMap.getIcon("rewindButton.icon")); // NOI18N
        gridButtonPanel.add(rewindButton);

        playButton.setAction(actionMap.get("playAction")); // NOI18N
        playButton.setIcon(resourceMap.getIcon("playButton.icon")); // NOI18N
        gridButtonPanel.add(playButton);

        forwardButton.setAction(actionMap.get("forwardAction")); // NOI18N
        forwardButton.setIcon(resourceMap.getIcon("forwardButton.icon")); // NOI18N
        gridButtonPanel.add(forwardButton);

        goBackButton.setAction(actionMap.get("goBackAction")); // NOI18N
        goBackButton.setIcon(resourceMap.getIcon("goBackButton.icon")); // NOI18N
        gridButtonPanel.add(goBackButton);

        shuttleBackButton.setAction(actionMap.get("shuttleBackAction")); // NOI18N
        shuttleBackButton.setIcon(resourceMap.getIcon("shuttleBackButton.icon")); // NOI18N
        gridButtonPanel.add(shuttleBackButton);

        pauseButton.setAction(actionMap.get("pauseAction")); // NOI18N
        pauseButton.setIcon(resourceMap.getIcon("pauseButton.icon")); // NOI18N
        gridButtonPanel.add(pauseButton);

        shuttleForwardButton.setAction(actionMap.get("shuttleForwardAction")); // NOI18N
        shuttleForwardButton.setIcon(resourceMap.getIcon("shuttleForwardButton.icon")); // NOI18N
        gridButtonPanel.add(shuttleForwardButton);

        findButton.setAction(actionMap.get("findAction")); // NOI18N
        findButton.setIcon(resourceMap.getIcon("findButton.icon")); // NOI18N
        gridButtonPanel.add(findButton);

        jogBackButton.setAction(actionMap.get("jogBackAction")); // NOI18N
        jogBackButton.setIcon(resourceMap.getIcon("jogBackButton.icon")); // NOI18N
        gridButtonPanel.add(jogBackButton);

        stopButton.setAction(actionMap.get("stopAction")); // NOI18N
        stopButton.setIcon(resourceMap.getIcon("stopButton.icon")); // NOI18N
        gridButtonPanel.add(stopButton);

        jogForwardButton.setAction(actionMap.get("jogForwardAction")); // NOI18N
        jogForwardButton.setIcon(resourceMap.getIcon("jogForwardButton.icon")); // NOI18N
        gridButtonPanel.add(jogForwardButton);

        mainPanel.add(gridButtonPanel, java.awt.BorderLayout.CENTER);

        rightTimePanel.setBackground(java.awt.Color.white);
        rightTimePanel.setLayout(new java.awt.GridLayout(4, 1));
        rightTimePanel.add(syncVideoButton);

        goBackTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        goBackTextField.setText("00:00:00:000");
        rightTimePanel.add(goBackTextField);

        findTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        findTextField.setText("00:00:00:000");
        rightTimePanel.add(findTextField);

        mainPanel.add(rightTimePanel, java.awt.BorderLayout.EAST);

        bottomPanel.setBackground(java.awt.Color.white);
        bottomPanel.setLayout(new java.awt.BorderLayout());

        leftButtonPanel.setBackground(java.awt.Color.white);
        leftButtonPanel.setLayout(new java.awt.GridBagLayout());

        createNewCellButton.setAction(actionMap.get("createNewCellAction")); // NOI18N
        createNewCellButton.setIcon(resourceMap.getIcon("createNewCellButton.icon")); // NOI18N
        leftButtonPanel.add(createNewCellButton, new java.awt.GridBagConstraints());

        setNewCellOnsetButton.setAction(actionMap.get("setNewCellStopTime")); // NOI18N
        setNewCellOnsetButton.setIcon(resourceMap.getIcon("setNewCellOnsetButton.icon")); // NOI18N
        leftButtonPanel.add(setNewCellOnsetButton, new java.awt.GridBagConstraints());

        bottomPanel.add(leftButtonPanel, java.awt.BorderLayout.WEST);

        fillerPanel.setBackground(java.awt.Color.white);
        fillerPanel.setLayout(new java.awt.BorderLayout());
        fillerPanel.add(timestampSetupButton, java.awt.BorderLayout.CENTER);

        bottomPanel.add(fillerPanel, java.awt.BorderLayout.EAST);

        mainPanel.add(bottomPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Action to invoke when the user clicks on the open button.
     *
     * @param evt The event that triggered this action.
     */
    private void openVideoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openVideoButtonActionPerformed
        JFileChooser jd = new JFileChooser();

        // Add file filters for each of the supported plugins.
        List<FileFilter> filters = PluginManager.getInstance()
                                                .getPluginFileFilters();
        for (FileFilter f : filters) {
            jd.addChoosableFileFilter(f);
        }
        int result = jd.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File f = jd.getSelectedFile();

            // Build the data viewer for the file.
            DataViewer viewer = PluginManager.getInstance()
                                             .buildViewerFromFile(f);
            if (viewer == null) {
                logger.error("No DataViewer available.");
                return;
            }

            viewer.setDataFeed(f);
            OpenSHAPA.getApplication().show(viewer.getParentJFrame());

            // adjust the overall frame rate.
            float fps = viewer.getFrameRate();
            if (fps > currentFPS) { currentFPS = fps; }

            // Add the QTDataViewer to the list of viewers we are controlling.
            this.viewers.add(viewer);
        }
    }//GEN-LAST:event_openVideoButtonActionPerformed

    /**
     * Action to invoke when the user clicks on the sync ctrl button.
     */
    @Action
    public void syncCtrlAction() {
        //for (DataViewer viewer : viewers) { /* @todo */; }
    }

    /**
     * Action to invoke when the user clicks on the sync button.
     */
    @Action
    public void syncAction() {
        //for (DataViewer viewer : viewers) { viewer.seekTo(getCurrentTime()); }
    }

    /**
     * Action to invoke when the user clicks the set cell onset button.
     */
    @Action
    public void setCellOnsetAction() {
        new SetSelectedCellStartTimeC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the set cell offest button.
     */
    @Action
    public void setCellOffsetAction() {
        //new SetCellOffsetC(getCurrentTime());
    }


    //--------------------------------------------------------------------------
    // Playback actions
    //

    /**
     * Action to invoke when the user clicks on the play button.
     */
    @Action
    public void playAction() {
        playAt(PLAY_RATE);
    }

    /**
     * Action to invoke when the user clicks on the fast foward button.
     */
    @Action
    public void forwardAction() {
        playAt(FFORWARD_RATE);
    }

    /**
     * Action to invoke when the user clicks on the rewind button.
     */
    @Action
    public void rewindAction() {
        playAt(REWIND_RATE);
    }

    /**
     * Action to invoke when the user clicks on the pause button.
     *
     * @todo pauses current playback but does not reset playback rate?
     */
    @Action
    public void pauseAction() {
        if (clock.isStopped()) { clock.start(); }
        else                   { clock.stop(); }
    }

    /**
     * Action to invoke when the user clicks on the stop button.
     *
     * @todo Stops current playback and resets rate?
     */
    @Action
    public void stopAction() {
        clock.stop();
        clock.setRate(PLAY_RATE);
        shuttleDirection = ShuttleDirection.UNDEFINED;
    }

    /**
     * Action to invoke when the user clicks on the shuttle forward button.
     *
     * @todo proper behaviour for reversing shuttle direction?
     */
    @Action
    public void shuttleForwardAction() {
        shuttle(ShuttleDirection.FORWARDS);
    }

    /**
     * Action to inovke when the user clicks on the shuttle back button.
     */
    @Action
    public void shuttleBackAction() {
        shuttle(ShuttleDirection.BACKWARDS);
    }

    /**
     * Populates the find time in the controller.
     *
     * @param milliseconds The time to use when populating the find field.
     */
    public void setFindTime(final long milliseconds) {
        this.findTextField.setText(CLOCK_FORMAT.format(milliseconds));
    }

    /**
     * Action to invoke when the user clicks on the find button.
     */
    @Action
    public void findAction() {
        try {
            jumpTo(CLOCK_FORMAT
                    .parse(this.findTextField.getText())
                    .getTime()
                );

        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }

    /**
     * Action to invoke when the user clicks on the go back button.
     */
    @Action
    public void goBackAction() {
        try {
            jump(-CLOCK_FORMAT
                    .parse(this.goBackTextField.getText())
                    .getTime()
                );

        } catch (ParseException e) {
            logger.error("unable to find within video", e);
        }
    }


    /**
     * Action to invoke when the user clicks on the jog backwards button.
     */
    @Action
    public void jogBackAction() { jump((long) (-ONE_SECOND / currentFPS)); }

    /**
     * Action to invoke when the user clicks on the jog forwards button.
     */
    @Action
    public void jogForwardAction() { jump((long) (ONE_SECOND / currentFPS)); }


    //--------------------------------------------------------------------------
    // [private] play back action helper functions
    //

    /**
     *
     * @param rate Rate of play.
     */
    private void playAt(final float rate) {
        shuttleDirection = ShuttleDirection.UNDEFINED;
        shuttleAt(rate);
    }

    /**
     *
     * @param direction The required direction of the shuttle.
     */
    private void shuttle(final ShuttleDirection direction) {
        if (direction == shuttleDirection) {
            ++shuttleRate;
            if (SHUTTLE_RATES.length == shuttleRate) { --shuttleRate; }

        } else {
            if (ShuttleDirection.UNDEFINED == shuttleDirection) {
                shuttleRate = -1;
            } else if (direction != shuttleDirection) {
                --shuttleRate;
            }

            if (0 > shuttleRate) {
                shuttleDirection = direction;
                shuttleRate = 0;
            }
        }

        shuttleAt(
                shuttleDirection.getParameter()
                * SHUTTLE_RATES[shuttleRate]
            );
    }

    /**
     *
     * @param rate Rate of shuttle.
     */
    private void shuttleAt(final float rate) {
        clock.setRate(rate);
        clock.start();
    }

    /**
     * @param step Milliseconds to jump.
     */
    private void jump(final long step) {
        clock.stop();
        clock.setRate(PLAY_RATE);
        clock.stepTime(step);
    }

    /**
     * @param time Absolute time to jump to.
     */
    private void jumpTo(final long time) {
        clock.stop();
        clock.setRate(PLAY_RATE);
        clock.setTime(time);
    }


    //--------------------------------------------------------------------------
    //
    //

    /**
     * Action to invoke when the user clicks on the new cell button.
     */
    @Action
    public void createNewCellAction() {
        new CreateNewCellC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the new cell onset button.
     */
    @Action
    public void setNewCellStopTime() {
        new SetNewCellStopTimeC(getCurrentTime());
    }

    /**
     * Action to invoke when the user clicks on the sync video button.
     */
    @Action
    public void syncVideoAction() {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton createNewCellButton;
    private javax.swing.JPanel fillerPanel;
    private javax.swing.JButton findButton;
    private javax.swing.JTextField findTextField;
    private javax.swing.JButton forwardButton;
    private javax.swing.JButton goBackButton;
    private javax.swing.JTextField goBackTextField;
    private javax.swing.JPanel gridButtonPanel;
    private javax.swing.JButton jogBackButton;
    private javax.swing.JButton jogForwardButton;
    private javax.swing.JLabel lblSpeed;
    private javax.swing.JPanel leftButtonPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton openVideoButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JButton rewindButton;
    private javax.swing.JPanel rightTimePanel;
    private javax.swing.JButton setCellOffsetButton;
    private javax.swing.JButton setCellOnsetButton;
    private javax.swing.JButton setNewCellOnsetButton;
    private javax.swing.JButton shuttleBackButton;
    private javax.swing.JButton shuttleForwardButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JButton syncButton;
    private javax.swing.JButton syncCtrlButton;
    private javax.swing.JButton syncVideoButton;
    private javax.swing.JLabel timestampLabel;
    private javax.swing.JButton timestampSetupButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

}
