//----------------------------------------------------------------------------//
//                                                                            //
//                         S c o r e E x p o r t e r                          //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © Hervé Bitteur and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.score;

import omr.WellKnowns;

import omr.constant.Constant;
import omr.constant.ConstantSet;

import omr.glyph.Shape;
import static omr.glyph.Shape.*;

import omr.math.Rational;
import static omr.score.MusicXML.*;
import omr.score.entity.Arpeggiate;
import omr.score.entity.Articulation;
import omr.score.entity.Barline;
import omr.score.entity.Beam;
import omr.score.entity.Chord;
import omr.score.entity.ChordSymbol;
import omr.score.entity.Clef;
import omr.score.entity.Coda;
import omr.score.entity.DirectionStatement;
import omr.score.entity.Dynamics;
import omr.score.entity.Fermata;
import omr.score.entity.KeySignature;
import omr.score.entity.LyricsItem;
import omr.score.entity.Measure;
import omr.score.entity.MeasureId.MeasureRange;
import omr.score.entity.Notation;
import omr.score.entity.Ornament;
import omr.score.entity.Page;
import omr.score.entity.Pedal;
import omr.score.entity.ScorePart;
import omr.score.entity.ScoreSystem;
import omr.score.entity.Segno;
import omr.score.entity.Slot;
import omr.score.entity.Slur;
import omr.score.entity.Staff;
import omr.score.entity.SystemPart;
import omr.score.entity.Text;
import omr.score.entity.Text.CreatorText.CreatorType;
import omr.score.entity.TimeSignature;
import omr.score.entity.TimeSignature.InvalidTimeSignature;
import omr.score.entity.Tuplet;
import omr.score.entity.Voice;
import omr.score.entity.Voice.VoiceChord;
import omr.score.entity.Wedge;
import omr.score.midi.MidiAbstractions;
import omr.score.visitor.AbstractScoreVisitor;

import omr.sheet.Scale;

import omr.text.FontInfo;

import omr.util.OmrExecutors;
import omr.util.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Node;

import com.audiveris.proxymusic.AboveBelow;
import com.audiveris.proxymusic.Accidental;
import com.audiveris.proxymusic.Articulations;
import com.audiveris.proxymusic.Attributes;
import com.audiveris.proxymusic.Backup;
import com.audiveris.proxymusic.BackwardForward;
import com.audiveris.proxymusic.BarStyle;
import com.audiveris.proxymusic.Bass;
import com.audiveris.proxymusic.BassAlter;
import com.audiveris.proxymusic.BassStep;
import com.audiveris.proxymusic.BeamValue;
import com.audiveris.proxymusic.ClefSign;
import com.audiveris.proxymusic.Credit;
import com.audiveris.proxymusic.Defaults;
import com.audiveris.proxymusic.Degree;
import com.audiveris.proxymusic.DegreeAlter;
import com.audiveris.proxymusic.DegreeType;
import com.audiveris.proxymusic.DegreeValue;
import com.audiveris.proxymusic.Direction;
import com.audiveris.proxymusic.DirectionType;
import com.audiveris.proxymusic.Empty;
import com.audiveris.proxymusic.EmptyPrintStyleAlign;
import com.audiveris.proxymusic.Encoding;
import com.audiveris.proxymusic.FontStyle;
import com.audiveris.proxymusic.FontWeight;
import com.audiveris.proxymusic.FormattedText;
import com.audiveris.proxymusic.Forward;
import com.audiveris.proxymusic.Harmony;
import com.audiveris.proxymusic.Identification;
import com.audiveris.proxymusic.Key;
import com.audiveris.proxymusic.Kind;
import com.audiveris.proxymusic.Lyric;
import com.audiveris.proxymusic.LyricFont;
import com.audiveris.proxymusic.MarginType;
import com.audiveris.proxymusic.MeasureNumberingValue;
import com.audiveris.proxymusic.MidiInstrument;
import com.audiveris.proxymusic.Notations;
import com.audiveris.proxymusic.NoteType;
import com.audiveris.proxymusic.Notehead;
import com.audiveris.proxymusic.NoteheadValue;
import com.audiveris.proxymusic.Ornaments;
import com.audiveris.proxymusic.OverUnder;
import com.audiveris.proxymusic.PageLayout;
import com.audiveris.proxymusic.PageMargins;
import com.audiveris.proxymusic.PartList;
import com.audiveris.proxymusic.PartName;
import com.audiveris.proxymusic.Pitch;
import com.audiveris.proxymusic.Repeat;
import com.audiveris.proxymusic.Rest;
import com.audiveris.proxymusic.RightLeftMiddle;
import com.audiveris.proxymusic.Root;
import com.audiveris.proxymusic.RootStep;
import com.audiveris.proxymusic.RootAlter;
import com.audiveris.proxymusic.Scaling;
import com.audiveris.proxymusic.ScoreInstrument;
import com.audiveris.proxymusic.ScorePartwise;
import com.audiveris.proxymusic.Sound;
import com.audiveris.proxymusic.StaffDetails;
import com.audiveris.proxymusic.StaffLayout;
import com.audiveris.proxymusic.StartStop;
import com.audiveris.proxymusic.StartStopChangeContinue;
import com.audiveris.proxymusic.StartStopContinue;
import com.audiveris.proxymusic.Stem;
import com.audiveris.proxymusic.StemValue;
import com.audiveris.proxymusic.SystemLayout;
import com.audiveris.proxymusic.SystemMargins;
import com.audiveris.proxymusic.TextElementData;
import com.audiveris.proxymusic.Tie;
import com.audiveris.proxymusic.Tied;
import com.audiveris.proxymusic.Time;
import com.audiveris.proxymusic.TimeModification;
import com.audiveris.proxymusic.TimeSymbol;
import com.audiveris.proxymusic.TypedText;
import com.audiveris.proxymusic.UprightInverted;
import com.audiveris.proxymusic.WedgeType;
import com.audiveris.proxymusic.Work;
import com.audiveris.proxymusic.YesNo;

import com.audiveris.proxymusic.util.Marshalling;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

/**
 * Class {@code ScoreExporter} visits the score hierarchy to export
 * the score to a MusicXML file, stream or DOM.
 *
 * @author Hervé Bitteur
 */
public class ScoreExporter
        extends AbstractScoreVisitor
{
    //~ Static fields/initializers ---------------------------------------------

    /** Specific application parameters */
    private static final Constants constants = new Constants();

    /** Usual logger utility */
    private static final Logger logger = LoggerFactory.getLogger(ScoreExporter.class);

    /** A future which reflects whether JAXB has been initialized * */
    private static final Future<Void> loading = OmrExecutors.
            getCachedLowExecutor().submit(
            new Callable<Void>()
    {
        @Override
        public Void call ()
                throws Exception
        {
            try {
                Marshalling.getContext();
            } catch (JAXBException ex) {
                logger.warn("Error preloading JaxbContext", ex);
                throw ex;
            }

            return null;
        }
    });

    /** Default page horizontal margin */
    private static final BigDecimal pageHorizontalMargin =
            new BigDecimal(constants.pageHorizontalMargin.getValue());

    /** Default page vertical margin */
    private static final BigDecimal pageVerticalMargin =
            new BigDecimal(constants.pageVerticalMargin.getValue());

    //~ Instance fields --------------------------------------------------------
    /** The related score */
    private final Score score;

    /** The score proxy built precisely for export via JAXB */
    private final ScorePartwise scorePartwise = new ScorePartwise();

    /** Current context */
    private Current current = new Current();

    /** Current flags */
    private IsFirst isFirst = new IsFirst();

    /** Map of Slur numbers, reset for every scorePart */
    private Map<Slur, Integer> slurNumbers = new HashMap<>();

    /** Map of Tuplet numbers, reset for every measure */
    private Map<Tuplet, Integer> tupletNumbers = new HashMap<>();

    /** Potential range of selected measures */
    private MeasureRange measureRange;

    /** Factory for proxymusic entities */
    private final com.audiveris.proxymusic.ObjectFactory factory = new com.audiveris.proxymusic.ObjectFactory();

    //~ Constructors -----------------------------------------------------------
    //---------------//
    // ScoreExporter //
    //---------------//
    /**
     * Create a new ScoreExporter object, on a related score instance.
     *
     * @param score the score to export (cannot be null)
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public ScoreExporter (Score score)
            throws InterruptedException, ExecutionException
    {
        if (score == null) {
            throw new IllegalArgumentException("Trying to export a null score");
        }

        // Make sure the JAXB context is ready
        loading.get();

        this.score = score;
    }

    //~ Methods ----------------------------------------------------------------
    //
    //--------//
    // export //
    //--------//
    /**
     * Export the score to a file.
     *
     * @param xmlFile         the xml file to write (cannot be null)
     * @param injectSignature should we inject out signature?
     */
    public void export (File xmlFile,
                        boolean injectSignature)
            throws Exception
    {
        export(new FileOutputStream(xmlFile), injectSignature);
    }

    //--------//
    // export //
    //--------//
    /**
     * Export the score to an output stream.
     *
     * @param os              the output stream where XML data is written
     *                        (cannot be null)
     * @param injectSignature should we inject our signature?
     * @throws IOException
     * @throws Exception
     */
    public void export (OutputStream os,
                        boolean injectSignature)
            throws IOException, Exception
    {
        if (os == null) {
            throw new IllegalArgumentException(
                    "Trying to export a score to a null output stream");
        }

        // Let visited nodes fill the scorePartWise proxy
        try {
            score.accept(this);
        } finally {
            //  Marshal the proxy with what we've got
            Marshalling.marshal(scorePartwise, os, injectSignature);
        }
    }

    //--------//
    // export //
    //--------//
    /**
     * Export the score to DOM node.
     * (No longer used, it was meant for Audiveris->Zong pure java transfer)
     *
     * @param node            the DOM node to export to (cannot be null)
     * @param injectSignature should we inject our signature?
     * @throws java.io.IOException
     * @throws java.lang.Exception
     */
    public void export (Node node,
                        boolean injectSignature)
            throws IOException, Exception
    {
        if (node == null) {
            throw new IllegalArgumentException(
                    "Trying to export a score to a null DOM Node");
        }

        try {
            // Let visited nodes fill the scorePartwise proxy
            buildScorePartwise();
        } finally {
            //  Finally, marshal the proxy with what we've got
            Marshalling.marshal(scorePartwise, node, injectSignature);
        }
    }

    //---------//
    // preload //
    //---------//
    /**
     * Empty static method, just to trigger class elaboration.
     */
    public static void preload ()
    {
    }

    //-----------------//
    // setMeasureRange //
    //-----------------//
    /**
     * Set a specific range of measures to export.
     *
     * @param measureRange the range of desired measures
     */
    public void setMeasureRange (MeasureRange measureRange)
    {
        this.measureRange = measureRange;
    }

    //- All Visiting Methods ---------------------------------------------------
    //------------------//
    // visit Arpeggiate //
    //------------------//
    @Override
    public boolean visit (Arpeggiate arpeggiate)
    {
        try {
            logger.debug("Visiting {}", arpeggiate);

            com.audiveris.proxymusic.Arpeggiate pmArpeggiate = factory.createArpeggiate();

            // relative-x
            pmArpeggiate.setRelativeX(
                    toTenths(
                    arpeggiate.getReferencePoint().x
                    - current.note.getCenterLeft().x));

            // number ???
            // TODO
            //
            getNotations().getTiedOrSlurOrTuplet().add(pmArpeggiate);
        } catch (Exception ex) {
            logger.warn("Error visiting " + arpeggiate, ex);
        }

        return false;
    }

    //--------------------//
    // visit Articulation //
    //--------------------//
    @Override
    public boolean visit (Articulation articulation)
    {
        try {
            logger.debug("Visiting {}", articulation);

            JAXBElement<?> element = getArticulationObject(
                    articulation.getShape());

            // Staff ?
            Staff staff = current.note.getStaff();

            // Placement
            Class<?> classe = element.getDeclaredType();

            Method method = classe.getMethod(
                    "setPlacement",
                    AboveBelow.class);
            method.invoke(
                    element.getValue(),
                    (articulation.getReferencePoint().y < current.note.
                    getCenter().y)
                    ? AboveBelow.ABOVE : AboveBelow.BELOW);

            // Default-Y
            method = classe.getMethod("setDefaultY", BigDecimal.class);
            method.invoke(
                    element.getValue(),
                    yOf(articulation.getReferencePoint(), staff));

            // Include in Articulations
            getArticulations().getAccentOrStrongAccentOrStaccato().add(element);
        } catch (Exception ex) {
            logger.warn("Error visiting " + articulation, ex);
        }

        return false;
    }

    //---------------//
    // visit Barline //
    //---------------//
    @Override
    public boolean visit (Barline barline)
    {
        try {
            if (barline == null) {
                return false;
            }
            logger.debug("Visiting {}", barline);

            Shape shape = barline.getShape();

            if ((shape != omr.glyph.Shape.THIN_BARLINE)
                && (shape != omr.glyph.Shape.PART_DEFINING_BARLINE)) {
                try {
                    com.audiveris.proxymusic.Barline pmBarline = factory.createBarline();
                    com.audiveris.proxymusic.BarStyleColor barStyleColor = factory.
                            createBarStyleColor();

                    if (barline == current.measure.getBarline()) {
                        // The bar is on right side
                        pmBarline.setLocation(RightLeftMiddle.RIGHT);

                        if ((shape == RIGHT_REPEAT_SIGN)
                            || (shape == BACK_TO_BACK_REPEAT_SIGN)) {
                            barStyleColor.setValue(BarStyle.LIGHT_HEAVY);

                            Repeat repeat = factory.createRepeat();
                            repeat.setDirection(BackwardForward.BACKWARD);
                            pmBarline.setRepeat(repeat);
                        }
                    } else {
                        // Inside barline (on left)
                        // Or bar is on left side
                        pmBarline.setLocation(RightLeftMiddle.LEFT);

                        if ((shape == LEFT_REPEAT_SIGN)
                            || (shape == BACK_TO_BACK_REPEAT_SIGN)) {
                            barStyleColor.setValue(BarStyle.HEAVY_LIGHT);

                            Repeat repeat = factory.createRepeat();
                            repeat.setDirection(BackwardForward.FORWARD);
                            pmBarline.setRepeat(repeat);
                        }
                    }

                    // Default: use style inferred from shape
                    // TODO: improve error handling here !!!!!!!!!
                    if (barStyleColor.getValue() == null) {
                        if (barline.getShape() != null) {
                            barStyleColor.setValue(
                                    barStyleOf(barline.getShape()));
                        }
                    }

                    // Everything is now OK
                    pmBarline.setBarStyle(barStyleColor);
                    current.pmMeasure.getNoteOrBackupOrForward().add(pmBarline);
                } catch (Exception ex) {
                    logger.warn("Cannot visit barline", ex);
                }
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + barline, ex);
        }

        return true;
    }

    //-------------//
    // visit Chord //
    //-------------//
    @Override
    public boolean visit (Chord chord)
    {
        logger.error("Chord objects should not be visited by ScoreExporter");

        return false;
    }

    //------------//
    // visit Clef //
    //------------//
    @Override
    public boolean visit (Clef clef)
    {
        try {
            logger.debug("Visiting {}", clef);

            if (isNewClef(clef)) {
                getAttributes().getClef().add(buildClef(clef));
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + clef, ex);
        }

        return true;
    }

    //------------//
    // visit Coda //
    //------------//
    @Override
    public boolean visit (Coda coda)
    {
        try {
            logger.debug("Visiting {}", coda);

            Direction direction = factory.createDirection();

            // Staff ?
            Staff staff = current.note.getStaff();
            insertStaffId(direction, staff);

            com.audiveris.proxymusic.EmptyPrintStyleAlign pmCoda = factory.createEmptyPrintStyleAlign();
            // default-x
            pmCoda.setDefaultX(
                    toTenths(
                    coda.getReferencePoint().x - current.measure.getLeftX()));

            // default-y
            pmCoda.setDefaultY(yOf(coda.getReferencePoint(), staff));

            DirectionType directionType = new DirectionType();
            directionType.getCoda().add(pmCoda);
            direction.getDirectionType().add(directionType);

            // Need also a Sound element
            Sound sound = factory.createSound();
            direction.setSound(sound);
            sound.setCoda("" + current.measure.getScoreId());
            sound.setDivisions(
                    new BigDecimal(
                    score.simpleDurationOf(
                    omr.score.entity.Note.QUARTER_DURATION)));

            // Everything is now OK
            current.pmMeasure.getNoteOrBackupOrForward().add(direction);
        } catch (Exception ex) {
            logger.warn("Error visiting " + coda, ex);
        }

        return true;
    }

    //--------------------------//
    // visit DirectionStatement //
    //--------------------------//
    @Override
    public boolean visit (DirectionStatement words)
    {
        try {
            logger.debug("Visiting {}", words);

            String content = words.getText().getContent();

            if (content != null) {
                Direction direction = factory.createDirection();
                DirectionType directionType = factory.createDirectionType();
                FormattedText pmWords = factory.createFormattedText();

                pmWords.setValue(content);

                // Staff
                Staff staff = current.note.getStaff();
                insertStaffId(direction, staff);

                // Placement
                direction.
                        setPlacement(
                        (words.getReferencePoint().y < current.note.getCenter().y)
                        ? AboveBelow.ABOVE : AboveBelow.BELOW);

                // default-y
                pmWords.setDefaultY(yOf(words.getReferencePoint(), staff));

                // Font information
                setFontInfo(pmWords, words.getText());

                // relative-x
                pmWords.setRelativeX(
                        toTenths(
                        words.getReferencePoint().x
                        - current.note.getCenterLeft().x));

                // Everything is now OK
                directionType.getWords().add(pmWords);
                direction.getDirectionType().add(directionType);
                current.pmMeasure.getNoteOrBackupOrForward().add(direction);
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + words, ex);
        }

        return true;
    }

    //-------------------//
    // visit ChordSymbol //
    //-------------------//
    @Override
    public boolean visit (ChordSymbol symbol)
    {
        try {
            logger.debug("Visiting {}", symbol);

            omr.score.entity.ChordInfo info = symbol.getInfo();
            Staff staff = current.note.getStaff();
            Harmony harmony = factory.createHarmony();

            // default-y
            harmony.setDefaultY(yOf(symbol.getReferencePoint(), staff));

            // font-size
            harmony.setFontSize("" + symbol.getText().getExportedFontSize());

            // relative-x
            harmony.setRelativeX(
                    toTenths(
                    symbol.getReferencePoint().x
                    - current.note.getCenterLeft().x));

            // Placement
            harmony.setPlacement(
                    (symbol.getReferencePoint().y < current.note.getCenter().y)
                    ? AboveBelow.ABOVE : AboveBelow.BELOW);

            // Staff
            insertStaffId(harmony, staff);

            // Root
            Root root = factory.createRoot();
            RootStep rootStep = factory.createRootStep();
            rootStep.setValue(stepOf(info.getRoot().step));
            root.setRootStep(rootStep);

            if (info.getRoot().alter != 0) {
                RootAlter alter = factory.createRootAlter();
                alter.setValue(new BigDecimal(info.getRoot().alter));
                root.setRootAlter(alter);
            }
            harmony.getHarmonyChord().add(root);

            // Kind
            Kind kind = factory.createKind();
            kind.setValue(kindOf(info.getKind().type));
            kind.setText(info.getKind().text);
            if (info.getKind().paren) {
                kind.setParenthesesDegrees(YesNo.YES);
            }
            if (info.getKind().symbol) {
                kind.setUseSymbols(YesNo.YES);
            }
            harmony.getHarmonyChord().add(kind);

            // Bass
            if (info.getBass() != null) {
                Bass bass = factory.createBass();
                BassStep bassStep = factory.createBassStep();
                bassStep.setValue(stepOf(info.getBass().step));
                bass.setBassStep(bassStep);

                if (info.getBass().alter != 0) {
                    BassAlter bassAlter = factory.createBassAlter();
                    bassAlter.setValue(new BigDecimal(info.getBass().alter));
                    bass.setBassAlter(bassAlter);
                }
                harmony.getHarmonyChord().add(bass);
            }

            // Degrees?
            for (omr.score.entity.ChordInfo.Degree deg : info.getDegrees()) {
                Degree degree = factory.createDegree();

                DegreeValue value = factory.createDegreeValue();
                value.setValue(new BigInteger("" + deg.value));
                degree.setDegreeValue(value);

                DegreeAlter alter = factory.createDegreeAlter();
                alter.setValue(new BigDecimal(deg.alter));
                degree.setDegreeAlter(alter);

                DegreeType type = factory.createDegreeType();
                type.setValue(typeOf(deg.type));
                degree.setDegreeType(type);

                harmony.getHarmonyChord().add(degree);
            }

            // Everything is now OK
            current.pmMeasure.getNoteOrBackupOrForward().add(harmony);
        } catch (Exception ex) {
            logger.warn("Error visiting " + symbol, ex);
        }

        return true;
    }

    //----------------//
    // visit Dynamics //
    //----------------//
    @Override
    public boolean visit (Dynamics dynamics)
    {
        try {
            logger.debug("Visiting {}", dynamics);

            // No point to export incorrect dynamics
            if (dynamics.getShape() == null) {
                return false;
            }

            Direction direction = factory.createDirection();
            DirectionType directionType = factory.createDirectionType();
            com.audiveris.proxymusic.Dynamics pmDynamics = factory.createDynamics();

            // Precise dynamic signature
            pmDynamics.getPOrPpOrPpp().add(
                    getDynamicsObject(dynamics.getShape()));

            // Staff ?
            Staff staff = current.note.getStaff();
            insertStaffId(direction, staff);

            // Placement
            if (dynamics.getReferencePoint().y < current.note.getCenter().y) {
                direction.setPlacement(AboveBelow.ABOVE);
            } else {
                direction.setPlacement(AboveBelow.BELOW);
            }

            // default-y
            pmDynamics.setDefaultY(yOf(dynamics.getReferencePoint(), staff));

            // Relative-x (No offset for the time being) using note left side
            pmDynamics.setRelativeX(
                    toTenths(
                    dynamics.getReferencePoint().x
                    - current.note.getCenterLeft().x));

            // Related sound level, if available
            Integer soundLevel = dynamics.getSoundLevel();

            if (soundLevel != null) {
                Sound sound = factory.createSound();
                sound.setDynamics(new BigDecimal(soundLevel));
                direction.setSound(sound);
            }

            // Everything is now OK
            directionType.getDynamics().add(pmDynamics);
            direction.getDirectionType().add(directionType);
            current.pmMeasure.getNoteOrBackupOrForward().add(direction);
        } catch (Exception ex) {
            logger.warn("Error visiting " + dynamics, ex);
        }

        return false;
    }

    //---------------//
    // visit Fermata //
    //---------------//
    @Override
    public boolean visit (Fermata fermata)
    {
        try {
            logger.debug("Visiting {}", fermata);

            com.audiveris.proxymusic.Fermata pmFermata = factory.createFermata();

            // default-y (of the fermata dot)
            // For upright we use bottom of the box, for inverted the top of the box
            Rectangle box = fermata.getBox();
            Point dot;

            if (fermata.getShape() == Shape.FERMATA_BELOW) {
                dot = new Point(box.x + (box.width / 2), box.y);
            } else {
                dot = new Point(
                        box.x + (box.width / 2),
                        box.y + box.height);
            }

            pmFermata.setDefaultY(yOf(dot, current.note.getStaff()));

            // Type
            pmFermata.
                    setType(
                    (fermata.getShape() == Shape.FERMATA) ? UprightInverted.UPRIGHT
                    : UprightInverted.INVERTED);
            // Everything is now OK
            getNotations().getTiedOrSlurOrTuplet().add(pmFermata);
        } catch (Exception ex) {
            logger.warn("Error visiting " + fermata, ex);
        }

        return false;
    }

    //--------------------//
    // visit KeySignature //
    //--------------------//
    @Override
    public boolean visit (KeySignature keySignature)
    {
        try {
            logger.debug("Visiting {}", keySignature);

            if (isNewKeySignature(keySignature)) {
                Key key = factory.createKey();
                key.setFifths(new BigInteger("" + keySignature.getKey()));

                // Trick: add this key signature only if it does not already exist
                List<Key> keys = getAttributes().getKey();

                for (Key k : keys) {
                    if (areEqual(k, key)) {
                        return true; // Already inserted, so give up
                    }
                }

                keys.add(key);
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + keySignature, ex);
        }

        return true;
    }

    //---------------//
    // visit Measure //
    //---------------//
    @Override
    public boolean visit (Measure measure)
    {
        try {
            logger.debug("Visiting {}", measure);

            // Make sure this measure is within the range to be exported
            if (!isDesired(measure)) {
                logger.debug("{} skipped.", measure);
                return false;
            }

            ///logger.info("Visiting " + measure);
            logger.debug("{} : {}", measure, isFirst);

            current.measure = measure;
            tupletNumbers.clear();

            // Allocate Measure
            current.pmMeasure = factory.createScorePartwisePartMeasure();
            current.pmMeasure.setNumber(measure.getScoreId());

            if (measure.getWidth() != null) {
                current.pmMeasure.setWidth(toTenths(measure.getWidth()));
            }

            if (measure.isImplicit()) {
                current.pmMeasure.setImplicit(YesNo.YES);
            }

            // Do we need to create & export a dummy initial measure?
            if (((measureRange != null) && !measure.isTemporary()
                 && (measure.getIdValue() > 1))
                && // TODO: Following line is illegal
                    (measure.getScoreId().equals(measureRange.getFirstId()))) {
                insertCurrentContext(measure);
            }

            // Print?
            new MeasurePrint(measure).process();

            // Inside barline?
            visit(measure.getInsideBarline());

            // Right Barline
            if (!measure.isDummy()) {
                visit(measure.getBarline());
            }

            // Left barline ?
            Measure prevMeasure = (Measure) measure.getPreviousSibling();
            if ((prevMeasure != null) && !prevMeasure.isDummy()) {
                visit(prevMeasure.getBarline());
            }

            // Divisions?
            if (isFirst.page && isFirst.system && isFirst.measure) {
                try {
                    getAttributes().setDivisions(
                            new BigDecimal(
                            score.simpleDurationOf(
                            omr.score.entity.Note.QUARTER_DURATION)));
                } catch (Exception ex) {
                    if (score.getDurationDivisor() == null) {
                        logger.warn(
                                "Not able to infer division value for part {}",
                                current.scorePart.getPid());
                    } else {
                        logger.warn("Error on divisions", ex);
                    }
                }
            }

            // Number of staves, if > 1
            if (isFirst.page && isFirst.system && isFirst.measure
                && current.scorePart.isMultiStaff()) {
                getAttributes().setStaves(
                        new BigInteger("" + current.scorePart.getStaffCount()));

            }

            // Tempo?
            if (isFirst.page && isFirst.system && isFirst.measure
                && !measure.isDummy()) {
                Direction direction = factory.createDirection();
                current.pmMeasure.getNoteOrBackupOrForward().add(direction);

                DirectionType directionType = factory.createDirectionType();
                direction.getDirectionType().add(directionType);

                // Use a dummy words element
                FormattedText pmWords = factory.
                        createFormattedText();
                directionType.getWords().add(pmWords);
                pmWords.setValue("");

                Sound sound = factory.createSound();
                sound.setTempo(
                        new BigDecimal(score.getTempoParam().getTarget()));
                direction.setSound(sound);
            }

            // Specific browsing down the measure
            // Insert KeySignatures, TimeSignatures
            measure.getKeySigList().acceptChildren(this);
            measure.getTimeSigList().acceptChildren(this);

            // Clefs may be inserted further down the measure
            ClefIterators clefIters = new ClefIterators(measure);

            // Insert clefs that occur before first time slot
            List<Slot> slots = measure.getSlots();

            if (slots.isEmpty()) {
                clefIters.push(null, null);
            } else {
                clefIters.push(slots.get(0).getX(), null);
            }

            // Now voice per voice
            Rational timeCounter = Rational.ZERO;

            for (Voice voice : measure.getVoices()) {
                current.voice = voice;

                // Need a backup ?
                if (!timeCounter.equals(Rational.ZERO)) {
                    insertBackup(timeCounter);
                    timeCounter = Rational.ZERO;
                }

                if (voice.isWhole()) {
                    // Delegate to the chord children directly
                    Chord chord = voice.getWholeChord();
                    clefIters.push(measure.getRightX(), chord.getStaff());
                    chord.acceptChildren(this);
                    timeCounter = measure.getExpectedDuration();
                } else {
                    for (Slot slot : measure.getSlots()) {
                        VoiceChord info = voice.getSlotInfo(slot);

                        if ((info != null) && // Skip free slots
                                (info.getStatus() == Voice.Status.BEGIN)) {
                            Chord chord = info.getChord();
                            clefIters.push(
                                    chord.getCenter().x,
                                    chord.getStaff());

                            // Need a forward before this chord ?
                            Rational startTime = chord.getStartTime();

                            if (timeCounter.compareTo(startTime) < 0) {
                                insertForward(
                                        startTime.minus(timeCounter),
                                        chord);
                                timeCounter = startTime;
                            }

                            // Delegate to the chord children directly
                            chord.acceptChildren(this);
                            timeCounter = timeCounter.plus(chord.getDuration());
                        }
                    }

                    // Need an ending forward ?
                    if (!measure.isImplicit() && !measure.isFirstHalf()) {
                        Rational termination = voice.getTermination();

                        if ((termination != null)
                            && (termination.compareTo(Rational.ZERO) < 0)) {
                            Rational delta = termination.opposite();
                            insertForward(delta, voice.getLastChord());
                            timeCounter = timeCounter.plus(delta);
                        }
                    }
                }
            }

            // Clefs that occur after time slots, if any
            clefIters.push(null, null);

            // Everything is now OK
            current.pmPart.getMeasure().add(current.pmMeasure);
        } catch (Exception ex) {
            logger.warn("Error visiting " + measure + " in " + current.page, ex);
        }

        // Safer...
        current.endMeasure();
        tupletNumbers.clear();
        isFirst.measure = false;

        return false; // Not this way
    }

    //------------//
    // visit Note //
    //------------//
    @Override
    public boolean visit (omr.score.entity.Note note)
    {
        try {
            logger.debug("Visiting {}", note);

            current.note = note;

            Chord chord = note.getChord();

            // For first note in chord
            if (chord.getNotes().indexOf(note) == 0) {
                // Chord direction events 
                for (omr.score.entity.Direction node : chord.getDirections()) {
                    node.accept(this);
                }
                // Chord symbol, if any
                if (chord.getChordSymbol() != null) {
                    chord.getChordSymbol().accept(this);
                }
            }

            current.pmNote = factory.createNote();

            Staff staff = note.getStaff();

            // Chord notation events for first note in chord
            if (chord.getNotes().indexOf(note) == 0) {
                for (Notation node : chord.getNotations()) {
                    node.accept(this);
                }
            } else {
                // Chord indication for every other note
                current.pmNote.setChord(new Empty());

                // Arpeggiate also?
                for (Notation node : chord.getNotations()) {
                    if (node instanceof Arpeggiate) {
                        node.accept(this);
                    }
                }
            }

            // Rest ?
            if (note.isRest()) {
                Rest rest = factory.createRest();

                // Rest for the whole measure?
                if (chord.isWholeDuration()) {
                    rest.setMeasure(YesNo.YES);
                }

                /// TODO ??? Set Step or Octave ???
                current.pmNote.setRest(rest);
            } else {
                // Pitch
                Pitch pitch = factory.createPitch();
                pitch.setStep(stepOf(note.getStep()));
                pitch.setOctave(note.getOctave());

                if (note.getAlter() != 0) {
                    pitch.setAlter(new BigDecimal(note.getAlter()));
                }

                current.pmNote.setPitch(pitch);
            }

            // Default-x (use left side of the note wrt measure)
            if (!note.getMeasure().isDummy()) {
                int noteLeft = note.getCenterLeft().x;
                current.pmNote.setDefaultX(
                        toTenths(noteLeft - note.getMeasure().getLeftX()));
            }

            // Tuplet factor ?
            if (chord.getTupletFactor() != null) {
                TimeModification timeModification = factory.
                        createTimeModification();
                timeModification.setActualNotes(
                        new BigInteger("" + chord.getTupletFactor().actualDen));
                timeModification.setNormalNotes(
                        new BigInteger("" + chord.getTupletFactor().actualNum));
                current.pmNote.setTimeModification(timeModification);
            }

            // Duration
            try {
                Rational dur;

                if (chord.isWholeDuration()) {
                    dur = chord.getMeasure().getActualDuration();
                } else {
                    dur = chord.getDuration();
                }

                current.pmNote.setDuration(
                        new BigDecimal(score.simpleDurationOf(dur)));
            } catch (Exception ex) {
                if (score.getDurationDivisor() != null) {
                    logger.warn("Not able to get duration of note", ex);
                }
            }

            // Voice
            current.pmNote.setVoice("" + chord.getVoice().getId());

            // Type
            if (!note.getMeasure().isDummy()) {
                NoteType noteType = factory.createNoteType();
                noteType.setValue("" + getNoteTypeName(note));

                if (!chord.isWholeDuration()) {
                    current.pmNote.setType(noteType);
                }
            }

            // For specific mirrored note
            if (note.getMirroredNote() != null) {
                int fbn = note.getChord().getFlagsNumber()
                          + note.getChord().getBeams().size();

                if ((fbn > 0) && (note.getShape() == NOTEHEAD_VOID)) {
                    // Indicate that the head should not be filled
                    //   <notehead filled="no">normal</notehead>
                    Notehead notehead = factory.createNotehead();
                    notehead.setFilled(YesNo.NO);
                    notehead.setValue(NoteheadValue.NORMAL);
                    current.pmNote.setNotehead(notehead);
                }
            }

            // Stem ?
            if (chord.getStem() != null) {
                Stem pmStem = factory.createStem();
                Point tail = chord.getTailLocation();
                pmStem.setDefaultY(yOf(tail, staff));

                if (tail.y < note.getCenter().y) {
                    pmStem.setValue(StemValue.UP);
                } else {
                    pmStem.setValue(StemValue.DOWN);
                }

                current.pmNote.setStem(pmStem);
            }

            // Staff ?
            if (current.scorePart.isMultiStaff()) {
                current.pmNote.setStaff(new BigInteger("" + staff.getId()));
            }

            // Dots
            for (int i = 0; i < chord.getDotsNumber(); i++) {
                current.pmNote.getDot().add(factory.createEmptyPlacement());
            }

            // Accidental ?
            if (note.getAccidental() != null) {
                Accidental accidental = factory.createAccidental();
                accidental.setValue(
                        accidentalValueOf(note.getAccidental().getShape()));
                current.pmNote.setAccidental(accidental);
            }

            // Beams ?
            for (Beam beam : chord.getBeams()) {
                com.audiveris.proxymusic.Beam pmBeam = factory.createBeam();
                pmBeam.setNumber(1 + chord.getBeams().indexOf(beam));

                if (beam.isHook()) {
                    if (beam.getCenter().x > chord.getStem().getLocation().x) {
                        pmBeam.setValue(BeamValue.FORWARD_HOOK);
                    } else {
                        pmBeam.setValue(BeamValue.BACKWARD_HOOK);
                    }
                } else {
                    List<Chord> chords = beam.getChords();
                    if (chords.get(0) == chord) {
                        pmBeam.setValue(BeamValue.BEGIN);
                    } else if (chords.get(chords.size() - 1) == chord) {
                        pmBeam.setValue(BeamValue.END);
                    } else {
                        pmBeam.setValue(BeamValue.CONTINUE);
                    }
                }

                current.pmNote.getBeam().add(pmBeam);
            }

            // Ties / Slurs
            for (Slur slur : note.getSlurs()) {
                slur.accept(this);
            }

            // Lyrics ?
            if (note.getSyllables() != null) {
                for (LyricsItem syllable : note.getSyllables()) {
                    if (syllable.getContent() != null) {
                        Lyric pmLyric = factory.createLyric();
                        pmLyric.setDefaultY(
                                yOf(syllable.getReferencePoint(), staff));
                        pmLyric.setNumber(
                                "" + syllable.getLyricsLine().getId());

                        TextElementData pmText = factory.createTextElementData();
                        pmText.setValue(syllable.getContent());
                        pmLyric.getElisionAndSyllabicAndText().
                                add(getSyllabic(syllable.
                                getSyllabicType()));
                        pmLyric.getElisionAndSyllabicAndText().add(pmText);

                        current.pmNote.getLyric().add(pmLyric);
                    }
                }
            }

            // Everything is OK
            current.pmMeasure.getNoteOrBackupOrForward().add(current.pmNote);
        } catch (Exception ex) {
            logger.warn("Error visiting " + note, ex);
        }

        // Safer...
        current.endNote();

        return true;
    }

    //----------------//
    // visit Ornament //
    //----------------//
    @Override
    @SuppressWarnings("unchecked")
    public boolean visit (Ornament ornament)
    {
        try {
            logger.debug("Visiting {}", ornament);

            JAXBElement<?> element = getOrnamentObject(ornament.getShape());

            // Placement?
            Class<?> classe = element.getDeclaredType();
            Method method = classe.getMethod(
                    "setPlacement",
                    AboveBelow.class);
            method.invoke(
                    element.getValue(),
                    (ornament.getReferencePoint().y < current.note.getCenter().y)
                    ? AboveBelow.ABOVE : AboveBelow.BELOW);
            // Everything is OK
            // Include in ornaments
            getOrnaments().getTrillMarkOrTurnOrDelayedTurn().add(element);
        } catch (Exception ex) {
            logger.warn("Error visiting " + ornament, ex);
        }

        return false;
    }

    //------------//
    // visit Page //
    //------------//
    @Override
    public boolean visit (Page page)
    {
        try {
            logger.debug("Visiting {}", page);

            isFirst.page = (page == score.getFirstPage());
            isFirst.system = true;
            isFirst.measure = true;
            current.page = page;

            Page prevPage = (Page) page.getPreviousSibling();
            current.pageMeasureIdOffset = (prevPage == null) ? 0
                    : (current.pageMeasureIdOffset
                       + prevPage.getDeltaMeasureId());
            current.scale = page.getScale();
        } catch (Exception ex) {
            logger.warn("Error visiting " + page, ex);
        }

        return true;
    }

    //-------------//
    // visit Pedal //
    //-------------//
    @Override
    public boolean visit (Pedal pedal)
    {
        try {
            logger.debug("Visiting {}", pedal);

            Direction direction = new Direction();
            DirectionType directionType = new DirectionType();
            com.audiveris.proxymusic.Pedal pmPedal = new com.audiveris.proxymusic.Pedal();

            // No line (for the time being)
            pmPedal.setLine(YesNo.NO);

            // Start / Stop type
            pmPedal.setType(
                    pedal.isStart()
                    ? StartStopChangeContinue.START
                    : StartStopChangeContinue.STOP);

            // Staff ?
            Staff staff = current.note.getStaff();
            insertStaffId(direction, staff);

            // default-x
            pmPedal.setDefaultX(
                    toTenths(
                    pedal.getReferencePoint().x - current.measure.getLeftX()));

            // default-y
            pmPedal.setDefaultY(yOf(pedal.getReferencePoint(), staff));

            // Placement
            direction.setPlacement(
                    (pedal.getReferencePoint().y < current.note.getCenter().y)
                    ? AboveBelow.ABOVE : AboveBelow.BELOW);
            // Everything is OK
            directionType.setPedal(pmPedal);
            direction.getDirectionType().add(directionType);
            current.pmMeasure.getNoteOrBackupOrForward().add(direction);
        } catch (Exception ex) {
            logger.warn("Error visiting " + pedal, ex);
        }

        return true;
    }

    //-------------//
    // visit Score //
    //-------------//
    /**
     * Allocate/populate everything that directly relates to the score
     * instance.
     * The rest of processing is delegated to the score children, that is to
     * say pages (TBI), then systems, etc...
     *
     * @param score visit the score to export
     * @return false, since no further processing is required after this node
     */
    @Override
    public boolean visit (Score score)
    {
        try {
            logger.debug("Visiting {}", score);

            // Reset durations for the score
            score.setDurationDivisor(null);

            // No version inserted
            // Let the marshalling class handle it

            // Identification
            Identification identification = factory.createIdentification();

            // Source
            identification.setSource(score.getImagePath());

            // Encoding
            Encoding encoding = factory.createEncoding();
            scorePartwise.setIdentification(identification);

            // [Encoding]/Software
            encoding.getEncodingDateOrEncoderOrSoftware().add(
                    factory.createEncodingSoftware(
                    WellKnowns.TOOL_NAME + " " + WellKnowns.TOOL_REF));

            // [Encoding]/EncodingDate
            // Let the Marshalling class handle it
            identification.setEncoding(encoding);

            // Defaults
            Defaults defaults = new Defaults();

            // [Defaults]/Scaling (using first page)
            Page firstPage = score.getFirstPage();

            if (current.scale == null) {
                current.scale = firstPage.getScale();
            }

            if (current.scale != null) {
                Scaling scaling = factory.createScaling();
                defaults.setScaling(scaling);
                scaling.setMillimeters(
                        new BigDecimal(
                        String.format("%.4f", (current.scale.getInterline() * 25.4 * 4) / 300))); // Assuming 300 DPI
                scaling.setTenths(new BigDecimal(40));

                // [Defaults]/PageLayout (using first page)
                if (firstPage.getDimension() != null) {
                    PageLayout pageLayout = factory.createPageLayout();
                    defaults.setPageLayout(pageLayout);
                    pageLayout.setPageHeight(
                            toTenths(firstPage.getDimension().height));
                    pageLayout.setPageWidth(
                            toTenths(firstPage.getDimension().width));

                    PageMargins pageMargins = factory.createPageMargins();
                    pageMargins.setType(MarginType.BOTH);
                    pageMargins.setLeftMargin(pageHorizontalMargin);
                    pageMargins.setRightMargin(pageHorizontalMargin);
                    pageMargins.setTopMargin(pageVerticalMargin);
                    pageMargins.setBottomMargin(pageVerticalMargin);
                    pageLayout.getPageMargins().add(pageMargins);
                }
            }

            // [Defaults]/LyricFont
            Font lyricFont = omr.score.entity.Text.getLyricsFont();
            LyricFont pmLyricFont = factory.createLyricFont();
            pmLyricFont.setFontFamily(lyricFont.getName());
            pmLyricFont.setFontSize(
                    "" + omr.score.entity.Text.getLyricsFontSize());
            if (lyricFont.isItalic()) {
                pmLyricFont.setFontStyle(FontStyle.ITALIC);
            }
            defaults.getLyricFont().add(pmLyricFont);
            scorePartwise.setDefaults(defaults);

            // PartList & sequence of parts
            if (score.getPartList() != null) {
                PartList partList = factory.createPartList();
                scorePartwise.setPartList(partList);

                // Here we browse the score hierarchy once for each score scorePart
                isFirst.scorePart = true;

                for (ScorePart p : score.getPartList()) {
                    partList.getPartGroupOrScorePart().add(getScorePart(p));
                    isFirst.scorePart = false;
                }
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + score, ex);
        }

        return false; // We don't go this way
    }

    //-------------------//
    // visit ScoreSystem //
    //-------------------//
    /**
     * Allocate/populate everything that directly relates to this
     * system in the current scorePart.
     * The rest of processing is directly delegated to the measures
     *
     * @param system the system to export
     * @return false
     */
    @Override
    public boolean visit (ScoreSystem system)
    {
        try {
            logger.debug("Visiting {}", system);

            current.system = system;
            isFirst.measure = true;

            SystemPart systemPart = system.getPart(current.scorePart.getId());

            if (systemPart != null) {
                systemPart.accept(this);
            } else {
                // Need to build an artificial system scorePart
                // Or simply delegating to the series of artificial measures
                SystemPart dummyPart = system.getFirstRealPart().
                        createDummyPart(
                        current.scorePart.getId());
                dummyPart.accept(this);
            }

            // If we have exported a measure, we are no longer in the first system
            if (!isFirst.measure) {
                isFirst.system = false;
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + system, ex);
        }

        return false; // No default browsing this way
    }

    //-------------//
    // visit Segno //
    //-------------//
    @Override
    public boolean visit (Segno segno)
    {
        try {
            logger.debug("Visiting {}", segno);

            Direction direction = new Direction();
            DirectionType directionType = factory.createDirectionType();

            EmptyPrintStyleAlign empty = factory.createEmptyPrintStyleAlign();

            // Staff ?
            Staff staff = current.note.getStaff();
            insertStaffId(direction, staff);

            // default-x
            empty.setDefaultX(
                    toTenths(
                    segno.getReferencePoint().x - current.measure.getLeftX()));

            // default-y
            empty.setDefaultY(yOf(segno.getReferencePoint(), staff));

            // Need also a Sound element (TODO: We don't do anything with sound!)
            Sound sound = factory.createSound();
            sound.setSegno("" + current.measure.getScoreId());
            sound.setDivisions(
                    new BigDecimal(
                    score.simpleDurationOf(
                    omr.score.entity.Note.QUARTER_DURATION)));

            // Everything is OK
            directionType.getSegno().add(empty);
            direction.getDirectionType().add(directionType);
            current.pmMeasure.getNoteOrBackupOrForward().add(direction);
        } catch (Exception ex) {
            logger.warn("Error visiting " + segno, ex);
        }

        return true;
    }

    //------------//
    // visit Slur //
    //------------//
    @Override
    public boolean visit (Slur slur)
    {
        try {
            logger.debug("Visiting {}", slur);

            // Make sure we have notes (or extension) on both sides
            // TODO: Make an exception for slurs at beginning of page!
            if ((slur.getLeftNote() == null)
                && (slur.getLeftExtension() == null)) {
                slur.addError("Non left-connected slur is not exported");

                return false;
            }

            // TODO: Make an exception for slurs at end of page!
            if ((slur.getRightNote() == null)
                && (slur.getRightExtension() == null)) {
                slur.addError("Non right-connected slur is not exported");

                return false;
            }

            // Note contextual data
            boolean isStart = slur.getLeftNote() == current.note;
            int noteLeft = current.note.getCenterLeft().x;
            Staff staff = current.note.getStaff();

            if (slur.isTie()) {
                // Tie element
                Tie tie = factory.createTie();
                tie.setType(isStart ? StartStop.START : StartStop.STOP);
                current.pmNote.getTie().add(tie);

                // Tied element
                Tied tied = factory.createTied();

                // Type
                tied.setType(isStart ? StartStopContinue.START : StartStopContinue.STOP);

                // Orientation
                if (isStart) {
                    tied.setOrientation(
                            slur.isBelow() ? OverUnder.UNDER : OverUnder.OVER);
                }

                // Bezier
                if (isStart) {
                    tied.setDefaultX(
                            toTenths(slur.getCurve().getX1() - noteLeft));
                    tied.setDefaultY(yOf(slur.getCurve().getY1(), staff));
                    tied.setBezierX(
                            toTenths(slur.getCurve().getCtrlX1() - noteLeft));
                    tied.setBezierY(yOf(slur.getCurve().getCtrlY1(), staff));
                } else {
                    tied.setDefaultX(
                            toTenths(slur.getCurve().getX2() - noteLeft));
                    tied.setDefaultY(yOf(slur.getCurve().getY2(), staff));
                    tied.setBezierX(
                            toTenths(slur.getCurve().getCtrlX2() - noteLeft));
                    tied.setBezierY(yOf(slur.getCurve().getCtrlY2(), staff));
                }

                getNotations().getTiedOrSlurOrTuplet().add(tied);
            } else {
                // Slur element
                com.audiveris.proxymusic.Slur pmSlur = factory.createSlur();

                // Number attribute
                Integer num = slurNumbers.get(slur);

                if (num != null) {
                    pmSlur.setNumber(num);
                    slurNumbers.remove(slur);

                    logger.debug("{} last use {} -> {}",
                            current.note.getContextString(),
                            num, slurNumbers.toString());
                } else {
                    // Determine first available number
                    for (num = 1; num <= 6; num++) {
                        if (!slurNumbers.containsValue(num)) {
                            if (slur.getRightExtension() != null) {
                                slurNumbers.put(slur.getRightExtension(), num);
                            } else {
                                slurNumbers.put(slur, num);
                            }

                            pmSlur.setNumber(num);

                            logger.debug("{} first use {} -> {}",
                                    current.note.getContextString(),
                                    num, slurNumbers.toString());

                            break;
                        }
                    }
                }

                // Type
                pmSlur.
                        setType(
                        isStart ? StartStopContinue.START : StartStopContinue.STOP);

                // Placement
                if (isStart) {
                    pmSlur.
                            setPlacement(
                            slur.isBelow() ? AboveBelow.BELOW : AboveBelow.ABOVE);
                }

                // Bezier
                if (isStart) {
                    pmSlur.setDefaultX(
                            toTenths(slur.getCurve().getX1() - noteLeft));
                    pmSlur.setDefaultY(yOf(slur.getCurve().getY1(), staff));
                    pmSlur.setBezierX(
                            toTenths(slur.getCurve().getCtrlX1() - noteLeft));
                    pmSlur.setBezierY(yOf(slur.getCurve().getCtrlY1(), staff));
                } else {
                    pmSlur.setDefaultX(
                            toTenths(slur.getCurve().getX2() - noteLeft));
                    pmSlur.setDefaultY(yOf(slur.getCurve().getY2(), staff));
                    pmSlur.setBezierX(
                            toTenths(slur.getCurve().getCtrlX2() - noteLeft));
                    pmSlur.setBezierY(yOf(slur.getCurve().getCtrlY2(), staff));
                }

                getNotations().getTiedOrSlurOrTuplet().add(pmSlur);
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + slur, ex);
        }

        return true;
    }

    //------------------//
    // visit SystemPart //
    //------------------//
    @Override
    public boolean visit (SystemPart systemPart)
    {
        try {
            logger.debug("Visiting {}", systemPart);

            // Delegate to texts
            for (TreeNode node : systemPart.getTexts()) {
                ((Text) node).accept(this);
            }

            // Delegate to measures
            for (TreeNode node : systemPart.getMeasures()) {
                ((Measure) node).accept(this);
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + systemPart, ex);
        }

        return false; // No default browsing this way
    }

    //------------//
    // visit Text //
    //------------//
    @Override
    public boolean visit (Text text)
    {
        try {
            logger.debug("Visiting {}", text);

            switch (text.getSentence().getRole().role) {
            case Title:
                getWork().setWorkTitle(text.getContent());

                break;

            case Number:
                getWork().setWorkNumber(text.getContent());

                break;

            case Rights: {
                TypedText typedText = factory.createTypedText();
                typedText.setValue(text.getContent());
                scorePartwise.getIdentification().getRights().add(typedText);
            }

            break;

            case Creator: {
                TypedText typedText = factory.createTypedText();
                typedText.setValue(text.getContent());

                CreatorType type = text.getSentence().getRole().creatorType;

                if (type != null) {
                    typedText.setType(type.toString());
                }

                scorePartwise.getIdentification().getCreator().add(typedText);
            }

            break;

            case UnknownRole:
                break;

            default: // LyricsItem, Direction, Chord

                // Handle them through related Note
                return false;
            }

            // Credits
            Credit pmCredit = factory.createCredit();
            // For MusicXML, page # is counted from 1, whatever the pageIndex
            pmCredit.setPage(
                    new BigInteger("" + (1 + current.page.getChildIndex())));

            FormattedText creditWords = factory.createFormattedText();
            creditWords.setValue(text.getContent());

            // Font information
            setFontInfo(creditWords, text);

            // Position is wrt page
            Point pt = text.getReferencePoint();
            creditWords.setDefaultX(toTenths(pt.x));
            creditWords.setDefaultY(
                    toTenths(current.page.getDimension().height - pt.y));

            pmCredit.getCreditTypeOrLinkOrBookmark().add(creditWords);
            scorePartwise.getCredit().add(pmCredit);
        } catch (Exception ex) {
            logger.warn("Error visiting " + text, ex);
        }

        return true;
    }

    //---------------------//
    // visit TimeSignature //
    //---------------------//
    @Override
    public boolean visit (TimeSignature timeSignature)
    {
        try {
            logger.debug("Visiting {}", timeSignature);

            try {
                Time time = factory.createTime();

                // Beats
                time.getTimeSignature().add(
                        factory.createTimeBeats(
                        "" + timeSignature.getNumerator()));

                // BeatType
                time.getTimeSignature().add(
                        factory.createTimeBeatType(
                        "" + timeSignature.getDenominator()));

                // Symbol ?
                if (timeSignature.getShape() != null) {
                    switch (timeSignature.getShape()) {
                    case COMMON_TIME:
                        time.setSymbol(TimeSymbol.COMMON);

                        break;

                    case CUT_TIME:
                        time.setSymbol(TimeSymbol.CUT);

                        break;
                    }
                }

                // Trick: add this time signature only if it does not already exist
                List<Time> times = getAttributes().getTime();

                for (Time t : times) {
                    if (areEqual(t, time)) {
                        return true; // Already inserted, so give up
                    }
                }

                times.add(time);
            } catch (InvalidTimeSignature ex) {
            }
        } catch (Exception ex) {
            logger.warn("Error visiting " + timeSignature, ex);
        }

        return true;
    }

    //--------------//
    // visit Tuplet //
    //--------------//
    @Override
    public boolean visit (Tuplet tuplet)
    {
        try {
            logger.debug("Visiting {}", tuplet);

            com.audiveris.proxymusic.Tuplet pmTuplet = factory.createTuplet();

            // Brackets
            if (constants.avoidTupletBrackets.isSet()) {
                pmTuplet.setBracket(YesNo.NO);
            }

            // Placement
            if (tuplet.getChord() == current.note.getChord()) { // i.e. start
                pmTuplet.setPlacement(
                        (tuplet.getCenter().y <= current.note.getCenter().y)
                        ? AboveBelow.ABOVE : AboveBelow.BELOW);
            }

            // Type
            pmTuplet.setType(
                    (tuplet.getChord() == current.note.getChord())
                    ? StartStop.START : StartStop.STOP);

            // Number
            Integer num = tupletNumbers.get(tuplet);

            if (num != null) {
                pmTuplet.setNumber(num);
                tupletNumbers.remove(tuplet); // Release the number
            } else {
                // Determine first available number
                for (num = 1; num <= 6; num++) {
                    if (!tupletNumbers.containsValue(num)) {
                        tupletNumbers.put(tuplet, num);
                        pmTuplet.setNumber(num);

                        break;
                    }
                }
            }

            getNotations().getTiedOrSlurOrTuplet().add(pmTuplet);
        } catch (Exception ex) {
            logger.warn("Error visiting " + tuplet, ex);
        }

        return false;
    }

    //-------------//
    // visit Wedge //
    //-------------//
    @Override
    public boolean visit (Wedge wedge)
    {
        try {
            logger.debug("Visiting {}", wedge);

            Direction direction = factory.createDirection();
            DirectionType directionType = factory.createDirectionType();
            com.audiveris.proxymusic.Wedge pmWedge = factory.createWedge();

            // Spread
            pmWedge.setSpread(toTenths(wedge.getSpread()));

            // Staff ?
            Staff staff = current.note.getStaff();
            insertStaffId(direction, staff);

            // Start or stop ?
            if (wedge.isStart()) {
                // Type
                pmWedge.
                        setType(
                        (wedge.getShape() == Shape.CRESCENDO) ? WedgeType.CRESCENDO
                        : WedgeType.DIMINUENDO);

                // Placement
                direction.
                        setPlacement(
                        (wedge.getReferencePoint().y < current.note.getCenter().y)
                        ? AboveBelow.ABOVE : AboveBelow.BELOW);

                // default-y
                pmWedge.setDefaultY(yOf(wedge.getReferencePoint(), staff));
            } else { // It's a stop
                pmWedge.setType(WedgeType.STOP);
            }

            //        // Relative-x (No offset for the time being) using note left side
            //        pmWedge.setRelativeX(
            //            toTenths(wedge.getReferencePoint().x - current.note.getCenterLeft().x));

            //        // default-x
            //        pmWedge.setDefaultX(
            //            toTenths(wedge.getReferencePoint().x - current.measure.getLeftX()));

            // Everything is OK
            directionType.setWedge(pmWedge);
            direction.getDirectionType().add(directionType);
            current.pmMeasure.getNoteOrBackupOrForward().add(direction);
        } catch (Exception ex) {
            logger.warn("Error visiting " + wedge, ex);
        }

        return true;
    }

    //- Utilities --------------------------------------------------------------
    //
    //----------//
    // toTenths //
    //----------//
    /**
     * Convert a distance expressed in pixels to a string value
     * expressed in tenths of interline.
     *
     * @param dist the distance in pixels
     * @return the number of tenths as a string
     */
    private BigDecimal toTenths (double dist)
    {
        return new BigDecimal(
                "" + (int) Math.
                rint((10f * dist) / current.scale.getInterline()));
    }

    //-------------//
    // setFontInfo //
    //-------------//
    private void setFontInfo (FormattedText formattedText,
                              Text text)
    {
        FontInfo fontInfo = text.getSentence().getFirstWord().getFontInfo();
        formattedText.setFontSize("" + text.getExportedFontSize());

        // Family
        if (fontInfo.isSerif) {
            formattedText.setFontFamily("serif");
        } else if (fontInfo.isMonospace) {
            formattedText.setFontFamily("monospace");
        } else {
            formattedText.setFontFamily("sans-serif");
        }

        // Italic?
        if (fontInfo.isItalic) {
            formattedText.setFontStyle(FontStyle.ITALIC);
        }

        // Bold?
        if (fontInfo.isBold) {
            formattedText.setFontWeight(FontWeight.BOLD);
        }

    }

    //-----//
    // yOf //
    //-----//
    /**
     * Report the musicXML staff-based Y value of a Point ordinate.
     *
     * @param ordinate the ordinate (page-based, in pixels)
     * @param staff    the related staff
     * @return the upward-oriented ordinate wrt staff top line (in tenths)
     */
    private BigDecimal yOf (double ordinate,
                            Staff staff)
    {
        return toTenths(staff.getTopLeft().y - ordinate);
    }

    //-----//
    // yOf //
    //-----//
    /**
     * Report the musicXML staff-based Y value of a Point.
     * This method is safer than the other one which simply accepts a (detyped)
     * double ordinate.
     *
     * @param point the pixel point
     * @param staff the related staff
     * @return the upward-oriented ordinate wrt staff top line (in tenths)
     */
    private BigDecimal yOf (Point point,
                            Staff staff)
    {
        return yOf(point.y, staff);
    }

    //----------//
    // areEqual //
    //----------//
    private static boolean areEqual (Time left,
                                     Time right)
    {
        return (getNum(left).equals(getNum(right)))
               && (getDen(left).equals(getDen(right)));
    }

    //----------//
    // areEqual //
    //----------//
    private static boolean areEqual (Key left,
                                     Key right)
    {
        return left.getFifths().equals(right.getFifths());
    }

    //------------------//
    // getArticulations //
    //------------------//
    /**
     * Report (after creating it if necessary) the articulations
     * elements in the notations element of the current note.
     *
     * @return the note notations articulations element
     */
    private Articulations getArticulations ()
    {
        for (Object obj : getNotations().getTiedOrSlurOrTuplet()) {
            if (obj instanceof Articulations) {
                return (Articulations) obj;
            }
        }

        // Need to allocate articulations
        Articulations articulations = factory.createArticulations();
        getNotations().getTiedOrSlurOrTuplet().add(articulations);

        return articulations;
    }

    //--------//
    // getDen // A VERIFIER A VERIFIER A VERIFIER A VERIFIER A VERIFIER
    //--------//
    private static java.lang.String getDen (Time time)
    {
        for (JAXBElement<java.lang.String> elem : time.getTimeSignature()) {
            if (elem.getName().getLocalPart().equals("beat-type")) {
                return elem.getValue();
            }
        }

        logger.error("No denominator found in {}", time);

        return "";
    }

    //--------------//
    // getNotations //
    //--------------//
    /**
     * Report (after creating it if necessary) the notations element
     * of the current note.
     *
     * @return the note notations element
     */
    private Notations getNotations ()
    {
        // Notations allocated?
        if (current.pmNotations == null) {
            current.pmNotations = factory.createNotations();
            current.pmNote.getNotations().add(current.pmNotations);
        }

        return current.pmNotations;
    }

    //--------//
    // getNum // A VERIFIER A VERIFIER A VERIFIER A VERIFIER A VERIFIER
    //--------//
    private static java.lang.String getNum (Time time)
    {
        for (JAXBElement<java.lang.String> elem : time.getTimeSignature()) {
            if (elem.getName().getLocalPart().equals("beats")) {
                return elem.getValue();
            }
        }

        logger.error("No numerator found in {}", time);

        return "";
    }

    //-----------//
    // buildClef //
    //-----------//
    private com.audiveris.proxymusic.Clef buildClef (Clef clef)
    {
        com.audiveris.proxymusic.Clef pmClef = factory.createClef();

        // Staff number (only for multi-staff parts)
        if (current.scorePart.isMultiStaff()) {
            pmClef.setNumber(new BigInteger("" + (clef.getStaff().getId())));
        }

        // Line (General computation that could be overridden by more
        // specific shape test below)
        pmClef.setLine(
                new BigInteger(
                "" + (3 - (int) Math.rint(clef.getPitchPosition() / 2.0))));

        Shape shape = clef.getShape();

        switch (shape) {
        case G_CLEF:
        case G_CLEF_SMALL:
            pmClef.setSign(ClefSign.G);

            break;

        case G_CLEF_8VA:
            pmClef.setSign(ClefSign.G);
            pmClef.setClefOctaveChange(new BigInteger("1"));

            break;

        case G_CLEF_8VB:
            pmClef.setSign(ClefSign.G);
            pmClef.setClefOctaveChange(new BigInteger("-1"));

            break;

        case C_CLEF:
            pmClef.setSign(ClefSign.C);

            break;

        case F_CLEF:
        case F_CLEF_SMALL:
            pmClef.setSign(ClefSign.F);

            break;

        case F_CLEF_8VA:
            pmClef.setSign(ClefSign.F);
            pmClef.setClefOctaveChange(new BigInteger("1"));

            break;

        case F_CLEF_8VB:
            pmClef.setSign(ClefSign.F);
            pmClef.setClefOctaveChange(new BigInteger("-1"));

            break;

        case PERCUSSION_CLEF:
            pmClef.setSign(ClefSign.PERCUSSION);

            break;

        default:
            logger.error("Clef shape not exported {}", shape);
        }

        return pmClef;
    }

    //---------------//
    // getAttributes //
    //---------------//
    /**
     * Report (after creating it if necessary) the measure attributes
     * element.
     *
     * @return the measure attributes element
     */
    private Attributes getAttributes ()
    {
        if (current.pmAttributes == null) {
            current.pmAttributes = new Attributes();
            current.pmMeasure.getNoteOrBackupOrForward().add(current.pmAttributes);
        }

        return current.pmAttributes;
    }

    //--------------//
    // getOrnaments //
    //--------------//
    /**
     * Report (after creating it if necessary) the ornaments elements
     * in the notations element of the current note.
     *
     * @return the note notations ornaments element
     */
    private Ornaments getOrnaments ()
    {
        for (Object obj : getNotations().getTiedOrSlurOrTuplet()) {
            if (obj instanceof Ornaments) {
                return (Ornaments) obj;
            }
        }

        // Need to allocate ornaments
        Ornaments ornaments = factory.createOrnaments();
        getNotations().getTiedOrSlurOrTuplet().add(ornaments);

        return ornaments;
    }

    //--------------------//
    // buildScorePartwise //
    //--------------------//
    /**
     * Fill a ScorePartwise with the Score information.
     *
     * @return the filled document
     */
    private ScorePartwise buildScorePartwise ()
    {
        // Let visited nodes fill the scorePartwise proxy
        score.accept(this);

        return scorePartwise;
    }

    //--------------//
    // getScorePart //
    //--------------//
    /**
     * Generate the proxymusic ScorePart instance that relates to the
     * Audiveris provided ScorePart.
     *
     * @param scorePart provided ScorePart
     * @return the newly built proxymusic ScorePart instance
     */
    private com.audiveris.proxymusic.ScorePart getScorePart (ScorePart scorePart)
    {
        current.scorePart = scorePart;

        ///logger.info("Processing " + scorePart);

        // Scorepart in partList
        com.audiveris.proxymusic.ScorePart pmScorePart = factory.createScorePart();
        pmScorePart.setId(scorePart.getPid());

        PartName partName = factory.createPartName();
        pmScorePart.setPartName(partName);
        partName.setValue(
                (scorePart.getName() != null) ? scorePart.getName()
                : scorePart.getDefaultName());

        // Score instrument
        Integer midiProgram = scorePart.getMidiProgram();

        if (midiProgram == null) {
            midiProgram = scorePart.getDefaultProgram();
        }

        ScoreInstrument scoreInstrument = new ScoreInstrument();
        pmScorePart.getScoreInstrument().add(scoreInstrument);
        scoreInstrument.setId(pmScorePart.getId() + "-I1");
        scoreInstrument.setInstrumentName(
                MidiAbstractions.getProgramName(midiProgram));

        // Midi instrument
        MidiInstrument midiInstrument = factory.createMidiInstrument();
        pmScorePart.getMidiDeviceAndMidiInstrument().add(midiInstrument);
        midiInstrument.setId(scoreInstrument);
        midiInstrument.setMidiChannel(scorePart.getId());
        midiInstrument.setMidiProgram(midiProgram);
        midiInstrument.setVolume(new BigDecimal(score.getVolume()));

        // ScorePart in scorePartwise
        current.pmPart = factory.createScorePartwisePart();
        scorePartwise.getPart().add(current.pmPart);
        current.pmPart.setId(pmScorePart);

        // Delegate to children the filling of measures
        logger.debug("Populating {}", current.scorePart);
        isFirst.system = true;
        slurNumbers.clear(); // Reset slur numbers

        // Browse the whole score hierarchy for this score scorePart
        score.acceptChildren(this);

        return pmScorePart;
    }

    //---------//
    // getWork //
    //---------//
    private Work getWork ()
    {
        if (current.pmWork == null) {
            current.pmWork = factory.createWork();
            scorePartwise.setWork(current.pmWork);
        }

        return current.pmWork;
    }

    //--------------//
    // insertBackup //
    //--------------//
    private void insertBackup (Rational delta)
    {
        try {
            Backup backup = factory.createBackup();
            backup.setDuration(new BigDecimal(score.simpleDurationOf(delta)));
            current.pmMeasure.getNoteOrBackupOrForward().add(backup);
        } catch (Exception ex) {
            if (score.getDurationDivisor() != null) {
                logger.warn("Not able to insert backup", ex);
            }
        }
    }

    //----------------------//
    // insertCurrentContext //
    //----------------------//
    private void insertCurrentContext (Measure measure)
    {
        // Browse measure, staff per staff
        SystemPart part = measure.getPart();

        for (TreeNode sn : part.getStaves()) {
            Staff staff = (Staff) sn;
            int right = measure.getLeftX(); // Right of dummy = Left of current
            int midY = (staff.getTopLeft().y + (staff.getHeight() / 2))
                       - measure.getSystem().getTopLeft().y;
            Point staffPoint = new Point(right, midY);

            // Clef?
            Clef clef = measure.getClefBefore(staffPoint, staff);

            if (clef != null) {
                clef.accept(this);
            }

            // Key?
            KeySignature key = measure.getKeyBefore(staffPoint, staff);

            if (key != null) {
                key.accept(this);
            }

            // Time?
            TimeSignature time = measure.getCurrentTimeSignature();

            if (time != null) {
                time.accept(this);
            }
        }
    }

    //---------------//
    // insertForward //
    //---------------//
    private void insertForward (Rational delta,
                                Chord chord)
    {
        try {
            Forward forward = factory.createForward();
            forward.setDuration(new BigDecimal(score.simpleDurationOf(delta)));
            forward.setVoice("" + current.voice.getId());
            current.pmMeasure.getNoteOrBackupOrForward().add(forward);

            // Staff ? (only if more than one staff in scorePart)
            insertStaffId(forward, chord.getStaff());
        } catch (Exception ex) {
            if (score.getDurationDivisor() != null) {
                logger.warn("Not able to insert forward", ex);
            }
        }
    }

    //---------------//
    // insertStaffId //
    //---------------//
    /**
     * If needed (if current scorePart contains more than one staff),
     * we insert the id of the staff related to the element at hand.
     *
     * @param obj the element at hand
     * @staff the related score staff
     */
    @SuppressWarnings("unchecked")
    private void insertStaffId (Object obj,
                                Staff staff)
    {
        if (current.scorePart.isMultiStaff()) {
            Class<?> classe = obj.getClass();

            try {
                Method method = classe.getMethod("setStaff", BigInteger.class);
                method.invoke(obj, new BigInteger("" + staff.getId()));
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Could not setStaff for element {}", classe);
            }
        }
    }

    //- Utility Methods --------------------------------------------------------
    //-----------//
    // isDesired //
    //-----------//
    /**
     * Check whether the provided measure is to be exported
     *
     * @param measure the measure to check
     * @return true is desired
     */
    private boolean isDesired (Measure measure)
    {
        return (measureRange == null) || // No range : take all of them
                (measure.isTemporary()) || // A temporary measure for export
                measureRange.contains(measure.getPageId()); // Part of the range
    }

    //-----------//
    // isNewClef //
    //-----------//
    /**
     * Make sure we have a NEW clef, not already assigned.
     * We have to go back (on the same staff) in current measure, then in
     * previous measures, then in same staff in previous systems, until we find
     * a previous clef.
     * And we compare the two shapes.
     *
     * @param clef the potentially new clef
     * @return true if this clef is really new
     */
    private boolean isNewClef (Clef clef)
    {
        if (current.measure.isDummy()) {
            return true;
        }

        // Perhaps another clef before this one ?
        Clef previousClef = current.measure.getClefBefore(
                new Point(clef.getCenter().x - 1, clef.getCenter().y),
                clef.getStaff());

        if (previousClef != null) {
            return previousClef.getShape() != clef.getShape();
        }

        return true; // Since no previous clef found
    }

    //-------------------//
    // isNewKeySignature //
    //-------------------//
    /**
     * Make sure we have a NEW key, not already assigned.
     * We have to go back in current measure, then in current staff, then in
     * same staff in previous systems, until we find a previous key.
     * And we compare the two shapes.
     *
     * @param key the potentially new key
     * @return true if this key is really new
     */
    private boolean isNewKeySignature (KeySignature key)
    {
        if (current.measure.isDummy()) {
            return true;
        }

        // Perhaps another key before this one ?
        KeySignature previousKey = current.measure.getKeyBefore(
                key.getCenter(),
                key.getStaff());

        if (previousKey != null) {
            return !previousKey.getKey().equals(key.getKey());
        }

        return true; // Since no previous key found
    }

    //~ Inner Classes ----------------------------------------------------------
    //
    //---------//
    // Current //
    //---------//
    /** Keep references of all current entities. */
    private static class Current
    {
        //~ Instance fields ----------------------------------------------------

        // Score dependent
        com.audiveris.proxymusic.Work pmWork;

        // Part dependent
        ScorePart scorePart;

        com.audiveris.proxymusic.ScorePartwise.Part pmPart;

        // Page dependent
        Page page;

        int pageMeasureIdOffset = 0;

        Scale scale;

        // System dependent
        ScoreSystem system;

        // Measure dependent
        Measure measure;

        com.audiveris.proxymusic.ScorePartwise.Part.Measure pmMeasure;

        Voice voice;

        // Note dependent
        omr.score.entity.Note note;

        com.audiveris.proxymusic.Note pmNote;

        com.audiveris.proxymusic.Notations pmNotations;

        com.audiveris.proxymusic.Attributes pmAttributes;

        //~ Methods ------------------------------------------------------------
        // Cleanup at end of measure
        void endMeasure ()
        {
            measure = null;
            pmMeasure = null;
            voice = null;

            endNote();
        }

        // Cleanup at end of note
        void endNote ()
        {
            note = null;
            pmNote = null;
            pmNotations = null;
            pmAttributes = null;
        }
    }

    //---------//
    // IsFirst //
    //---------//
    /** Composite flag to help drive processing of any entity. */
    private static class IsFirst
    {
        //~ Instance fields ----------------------------------------------------

        /** We are writing the first score part of the score */
        boolean scorePart;

        /** We are writing the first page of the score */
        Boolean page;

        /** We are writing the first system in the current page */
        boolean system;

        /** We are writing the first measure in current system (in current
         * scorePart) */
        boolean measure;

        //~ Methods ------------------------------------------------------------
        @Override
        public java.lang.String toString ()
        {
            StringBuilder sb = new StringBuilder();

            if (scorePart) {
                sb.append(" firstScorePart");
            }

            if (page == null) {
                sb.append(" noPage");
            } else if (page) {
                sb.append(" firstPage");
            }

            if (system) {
                sb.append(" firstSystem");
            }

            if (measure) {
                sb.append(" firstMeasure");
            }

            return sb.toString();
        }
    }

    //---------------//
    // ClefIterators //
    //---------------//
    /**
     * Class to handle the insertion of clefs in a measure.
     * If needed, this class could be reused for some attribute other than clef,
     * such as key signature or time signature (if these attributes can indeed
     * occur in the middle of a mesure. To be checked).
     */
    private class ClefIterators
    {
        //~ Instance fields ----------------------------------------------------

        /** Containing measure */
        private final Measure measure;

        /** Staves of the containing part */
        private final List<TreeNode> staves;

        /** Per staff, iterator on Clefs sorted by abscissa */
        private final Map<Staff, ListIterator<Clef>> iters;

        //~ Constructors -------------------------------------------------------
        public ClefIterators (Measure measure)
        {
            this.measure = measure;

            staves = measure.getPart().getStaves();

            Map<Staff, List<Clef>> map = new HashMap<>();

            for (TreeNode tn : measure.getClefList().getChildren()) {
                Clef clef = (Clef) tn;
                Staff staff = clef.getStaff();
                List<Clef> list = map.get(staff);

                if (list == null) {
                    map.put(staff, list = new ArrayList<>());
                }

                list.add(clef);
            }

            iters = new HashMap<>();

            for (Entry<Staff, List<Clef>> entry : map.entrySet()) {
                List<Clef> list = entry.getValue();
                Collections.sort(
                        list,
                        new Comparator<Clef>()
                {
                    @Override
                    public int compare (Clef o1,
                                        Clef o2)
                    {
                        return Integer.signum(
                                o1.getCenter().x - o2.getCenter().x);
                    }
                });
                iters.put(entry.getKey(), list.listIterator());
            }
        }

        //~ Methods ------------------------------------------------------------
        /**
         * Push as far as possible the relevant clefs iterators,
         * according to the current abscissa.
         *
         * @param abscissa      the abscissa of chord to be exported, if any
         * @param specificStaff a specific staff, or null for all staves
         */
        public void push (Integer abscissa,
                          Staff specificStaff)
        {
            if (abscissa != null) {
                for (TreeNode node : staves) {
                    Staff staff = (Staff) node;

                    if ((specificStaff == null) || (staff == specificStaff)) {
                        final ListIterator<Clef> it = iters.get(staff);

                        // Check pending clef WRT current abscissa
                        if ((it != null) && it.hasNext()) {
                            final Clef clef = it.next();

                            if (measure.isDummy()
                                || measure.isTemporary()
                                || (clef.getCenter().x <= abscissa)) {
                                // Consume this clef
                                clef.accept(ScoreExporter.this);
                            } else {
                                // Reset iterator
                                it.previous();
                            }
                        }
                    }
                }
            } else {
                // Flush all iterators
                for (ListIterator<Clef> it : iters.values()) {
                    while (it.hasNext()) {
                        it.next().accept(ScoreExporter.this);
                    }
                }
            }
        }
    }

    //--------------//
    // MeasurePrint //
    //--------------//
    /**
     * Handles the print element for a measure.
     */
    private class MeasurePrint
    {

        private final Measure measure;

        private final com.audiveris.proxymusic.Print pmPrint;

        /** Needed to remove the element if not actually used. */
        private boolean used = false;

        public MeasurePrint (Measure measure)
        {
            this.measure = measure;

            // Allocate and insert Print immediately
            pmPrint = factory.createPrint();
            current.pmMeasure.getNoteOrBackupOrForward().add(pmPrint);
        }

        private com.audiveris.proxymusic.Print getPrint ()
        {
            used = true;
            return pmPrint;
        }

        public void process ()
        {
            populatePrint();

            // Something to print actually?
            if (!used) {
                current.pmMeasure.getNoteOrBackupOrForward().remove(pmPrint);
            }
        }

        private void populatePrint ()
        {
            // New system?
            if (isFirst.measure) {
                if (!isFirst.system) {
                    getPrint().setNewSystem(YesNo.YES);
                }
            } else {
                getPrint().setNewSystem(YesNo.NO);
            }

            // New page?
            if (!isFirst.page && isFirst.system && isFirst.measure) {
                getPrint().setNewPage(YesNo.YES);
            }

            // SystemLayout?
            if (isFirst.measure && !measure.isDummy()) {
                SystemLayout systemLayout = factory.createSystemLayout();

                // SystemMargins
                SystemMargins systemMargins = factory.createSystemMargins();
                systemLayout.setSystemMargins(systemMargins);
                systemMargins.setLeftMargin(
                        toTenths(current.system.getTopLeft().x)
                        .subtract(pageHorizontalMargin));
                systemMargins.setRightMargin(
                        toTenths(
                        current.page.getDimension().width
                        - current.system.getTopLeft().x
                        - current.system.getDimension().width)
                        .subtract(pageHorizontalMargin));

                if (isFirst.system) {
                    // TopSystemDistance
                    systemLayout.setTopSystemDistance(
                            toTenths(current.system.getTopLeft().y)
                            .subtract(pageVerticalMargin));

                } else {
                    // SystemDistance
                    ScoreSystem prevSystem = (ScoreSystem) current.system.
                            getPreviousSibling();
                    systemLayout.setSystemDistance(
                            toTenths(
                            current.system.getTopLeft().y
                            - prevSystem.getTopLeft().y
                            - prevSystem.getDimension().height
                            - prevSystem.getLastPart().getLastStaff().
                            getHeight()));
                }

                getPrint().setSystemLayout(systemLayout);
            }

            // StaffLayout for all staves in this scorePart, except 1st system staff
            if (isFirst.measure && !measure.isDummy()) {
                for (TreeNode sNode : measure.getPart().getStaves()) {
                    Staff staff = (Staff) sNode;

                    if (!isFirst.scorePart || (staff.getId() > 1)) {
                        try {
                            StaffLayout staffLayout = factory.createStaffLayout();
                            staffLayout.setNumber(new BigInteger("" + staff.getId()));

                            Staff prevStaff = (Staff) staff.getPreviousSibling();

                            if (prevStaff == null) {
                                SystemPart prevPart = (SystemPart) measure.
                                        getPart().getPreviousSibling();

                                if (!prevPart.isDummy()) {
                                    prevStaff = prevPart.getLastStaff();
                                }
                            }

                            if (prevStaff != null) {
                                staffLayout.setStaffDistance(
                                        toTenths(
                                        staff.getTopLeft().y
                                        - prevStaff.getTopLeft().y
                                        - prevStaff.getHeight()));
                                getPrint().getStaffLayout().add(staffLayout);
                            }
                        } catch (Exception ex) {
                            logger.warn(
                                    "Error exporting staff layout system#"
                                    + current.system.getId() + " part#"
                                    + current.scorePart.getId() + " staff#"
                                    + staff.getId(),
                                    ex);
                        }
                    }
                }
            }

            // Do not print artificial parts
            if (isFirst.measure) {
                StaffDetails staffDetails = factory.createStaffDetails();
                staffDetails.setPrintObject(measure.isDummy() ? YesNo.NO : YesNo.YES);
                getAttributes().getStaffDetails().add(staffDetails);
            }

            // Measure numbering?
            if (isFirst.system && isFirst.measure) {
                com.audiveris.proxymusic.MeasureNumbering pmNumbering =
                        factory.createMeasureNumbering();
                if (isFirst.scorePart) {
                    pmNumbering.setValue(MeasureNumberingValue.SYSTEM);
                } else {
                    pmNumbering.setValue(MeasureNumberingValue.NONE);
                }
                getPrint().setMeasureNumbering(pmNumbering);
            }
        }
    }

    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
            extends ConstantSet
    {
        //~ Instance fields ----------------------------------------------------

        Constant.Integer pageHorizontalMargin = new Constant.Integer(
                "tenths",
                80,
                "Page horizontal margin");

        Constant.Integer pageVerticalMargin = new Constant.Integer(
                "tenths",
                80,
                "Page vertical margin");

        Constant.Boolean avoidTupletBrackets = new Constant.Boolean(
                false,
                "Should we avoid brackets for all tuplets");

    }
}
