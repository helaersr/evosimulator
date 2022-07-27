package evosimulator;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JSlider;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Code of the old web Applet, not maintained.
 * See EvoSimulatorApp for the current version
 * 
 * @author Raphaël Helaers
 *
 */
public class EvoSimulator extends Applet {
	public enum Language {en, fr}
	
	private static final String chars =
		"aàAbBcçCdDéeèEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuùUvVwWxXyYzZ 0123456789,.?!'\"()";
	private static final int nchars = chars.length();
	private static final char[] characters = chars.toCharArray();
	private static final Map<Character, Integer> charPos = new HashMap<Character, Integer>();

	private boolean running = false;
	private boolean done = true;
	private boolean fineScore = false;
	private char[][] population = null;
	private double[] scores = null;
	private char[] goal;
	private double scoreSum = -1;
	private int generation = 0;

	private GraphPanel graph;

	private JTextField txtGoal;
	private JTextField textFieldNbrStepsBefPausing;
	private JCheckBox chckbxBigMutations;
	private JCheckBox chckbxRecombination;
	private JCheckBox chckbxSelectionReproduction;
	private JProgressBar progressBarBestIndividual;
	private JLabel labelStep;
	private JLabel labelMeanScore;
	private JButton btnStartPause;
	private JCheckBox chckbxSmallMutations;
	private JSlider sliderMutationRatio;
	private JCheckBox chckbxSelectionSurvival;
	private JLabel labelBigMutPercent = new JLabel("(100%)");
	private JLabel labelSmallMutPercent = new JLabel("(0%)");
	private JSlider sliderPropMut;
	private JSlider sliderPropRep;
	private JSpinner spinnerPopSize;
	private JSlider sliderRecombinationProb;
	
	public EvoSimulator() throws HeadlessException {
	}

	private void createGUI(Language l){
		final String TIT_BIRTH_DEATH;
		final String CHK_BOX_RECOMBINATION;
		final String BIRTH_DEATH_VAL;
		switch(l){
		case fr :
			TIT_BIRTH_DEATH = "Naissances et décès";
			CHK_BOX_RECOMBINATION = "Reproduction sexuée avec probabilité de recombinaison de ";
			BIRTH_DEATH_VAL = " naissances & décès";
			break;
		case en :
		default:
			TIT_BIRTH_DEATH = "Births & deaths";
			CHK_BOX_RECOMBINATION = "Sexual reproduction with probability of recombination of ";
			BIRTH_DEATH_VAL = " births & deaths";
			break;
		}
		
		for (int i=0 ; i < nchars ; i++){
			charPos.put(characters[i], i);
		}
		
		setLayout(new BorderLayout());

		JPanel main_panel = new JPanel();
		add(main_panel, BorderLayout.CENTER);
		main_panel.setLayout(new BorderLayout());

		graph = new GraphPanel(l);
		main_panel.add(graph, BorderLayout.CENTER);

		JPanel panel_N = new JPanel();
		main_panel.add(panel_N, BorderLayout.NORTH);
		GridBagLayout gbl_panel_N = new GridBagLayout();
		panel_N.setLayout(gbl_panel_N);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		panel_N.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		panel.setLayout(gbl_panel);

		JLabel lblOptimalIndividual = new JLabel();		
		GridBagConstraints gbc_lblOptimalIndividual = new GridBagConstraints();
		gbc_lblOptimalIndividual.anchor = GridBagConstraints.WEST;
		gbc_lblOptimalIndividual.insets = new Insets(5, 5, 5, 5);
		gbc_lblOptimalIndividual.gridx = 0;
		gbc_lblOptimalIndividual.gridy = 0;
		panel.add(lblOptimalIndividual, gbc_lblOptimalIndividual);

		txtGoal = new JTextField();
		txtGoal.setPreferredSize(new Dimension(450, 20));
		GridBagConstraints gbc_txtGoal = new GridBagConstraints();
		gbc_txtGoal.insets = new Insets(5, 5, 5, 5);
		gbc_txtGoal.weightx = 1.0;
		gbc_txtGoal.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtGoal.anchor = GridBagConstraints.NORTHWEST;
		gbc_txtGoal.gridx = 1;
		gbc_txtGoal.gridy = 0;
		panel.add(txtGoal, gbc_txtGoal);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		panel_N.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		panel_2.setLayout(gbl_panel_2);

		JLabel lblPopulationSize = new JLabel();
		GridBagConstraints gbc_lblPopulationSize = new GridBagConstraints();
		gbc_lblPopulationSize.anchor = GridBagConstraints.WEST;
		gbc_lblPopulationSize.insets = new Insets(5, 5, 0, 5);
		gbc_lblPopulationSize.gridx = 0;
		gbc_lblPopulationSize.gridy = 0;
		panel_2.add(lblPopulationSize, gbc_lblPopulationSize);

		spinnerPopSize = new JSpinner();
		spinnerPopSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int val = Integer.parseInt(spinnerPopSize.getValue().toString());
				sliderPropMut.setMaximum(val);
				sliderPropMut.setValue((int)(val*0.05));
				sliderPropRep.setMaximum(val);
				sliderPropRep.setValue((int)(val*0.5));				
			}
		});
		spinnerPopSize.setModel(new SpinnerNumberModel(1000, 10, 1000000000, 10));
		GridBagConstraints gbc_spinnerPopSize = new GridBagConstraints();
		gbc_spinnerPopSize.weightx = 1.0;
		gbc_spinnerPopSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerPopSize.insets = new Insets(5, 5, 5, 5);
		gbc_spinnerPopSize.gridx = 1;
		gbc_spinnerPopSize.gridy = 0;
		panel_2.add(spinnerPopSize, gbc_spinnerPopSize);

		JLabel lblNbrOfSteps = new JLabel();
		GridBagConstraints gbc_lblNbrOfSteps = new GridBagConstraints();
		gbc_lblNbrOfSteps.anchor = GridBagConstraints.WEST;
		gbc_lblNbrOfSteps.insets = new Insets(5, 5, 0, 5);
		gbc_lblNbrOfSteps.gridx = 2;
		gbc_lblNbrOfSteps.gridy = 0;
		panel_2.add(lblNbrOfSteps, gbc_lblNbrOfSteps);

		textFieldNbrStepsBefPausing = new JTextField();
		textFieldNbrStepsBefPausing.setText("0");
		GridBagConstraints gbc_textFieldNbrStepsBefPausing = new GridBagConstraints();
		gbc_textFieldNbrStepsBefPausing.weightx = 1.0;
		gbc_textFieldNbrStepsBefPausing.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldNbrStepsBefPausing.insets = new Insets(5, 5, 0, 0);
		gbc_textFieldNbrStepsBefPausing.anchor = GridBagConstraints.NORTHWEST;
		gbc_textFieldNbrStepsBefPausing.gridx = 3;
		gbc_textFieldNbrStepsBefPausing.gridy = 0;
		panel_2.add(textFieldNbrStepsBefPausing, gbc_textFieldNbrStepsBefPausing);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Mutations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		panel_N.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		panel_1.setLayout(gbl_panel_1);

		chckbxBigMutations = new JCheckBox(); 
		chckbxBigMutations.setSelected(true);
		chckbxBigMutations.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				mutationsCheckBoxChange();
			}
		});
		GridBagConstraints gbc_chckbxBigMutations = new GridBagConstraints();
		gbc_chckbxBigMutations.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxBigMutations.insets = new Insets(5, 5, 5, 5);
		gbc_chckbxBigMutations.gridx = 0;
		gbc_chckbxBigMutations.gridy = 0;
		panel_1.add(chckbxBigMutations, gbc_chckbxBigMutations);
		labelBigMutPercent.setVisible(false);

		labelBigMutPercent.setHorizontalAlignment(SwingConstants.TRAILING);
		labelBigMutPercent.setPreferredSize(new Dimension(70, 14));

		GridBagConstraints gbc_labelBigMutPercent = new GridBagConstraints();
		gbc_labelBigMutPercent.anchor = GridBagConstraints.WEST;
		gbc_labelBigMutPercent.insets = new Insets(5, 5, 5, 5);
		gbc_labelBigMutPercent.gridx = 1;
		gbc_labelBigMutPercent.gridy = 0;
		panel_1.add(labelBigMutPercent, gbc_labelBigMutPercent);

		sliderMutationRatio = new JSlider();
		sliderMutationRatio.setVisible(false);

		sliderMutationRatio.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int val = sliderMutationRatio.getValue();
				labelBigMutPercent.setText("("+val+"%)");
				labelSmallMutPercent.setText("("+(100-val)+"%)");
			}
		});
		sliderMutationRatio.setSnapToTicks(false);
		sliderMutationRatio.setPaintLabels(false);
		sliderMutationRatio.setPaintTicks(false);
		sliderMutationRatio.setMinimum(0);
		sliderMutationRatio.setMaximum(100);
		sliderMutationRatio.setValue(100);
		sliderMutationRatio.setMajorTickSpacing(10);
		sliderMutationRatio.setEnabled(false);

		GridBagConstraints gbc_sliderMutationRatio = new GridBagConstraints();
		gbc_sliderMutationRatio.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderMutationRatio.weightx = 1.0;
		gbc_sliderMutationRatio.anchor = GridBagConstraints.NORTHWEST;
		gbc_sliderMutationRatio.insets = new Insets(5, 5, 5, 5);
		gbc_sliderMutationRatio.gridx = 2;
		gbc_sliderMutationRatio.gridy = 0;
		panel_1.add(sliderMutationRatio, gbc_sliderMutationRatio);

		chckbxSmallMutations = new JCheckBox();
		chckbxSmallMutations.setVisible(false);
		chckbxSmallMutations.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				mutationsCheckBoxChange();
			}
		});
		labelSmallMutPercent.setVisible(false);

		labelSmallMutPercent.setPreferredSize(new Dimension(70, 14));

		GridBagConstraints gbc_labelSmallMutPercent = new GridBagConstraints();
		gbc_labelSmallMutPercent.anchor = GridBagConstraints.WEST;
		gbc_labelSmallMutPercent.insets = new Insets(5, 5, 5, 5);
		gbc_labelSmallMutPercent.gridx = 3;
		gbc_labelSmallMutPercent.gridy = 0;
		panel_1.add(labelSmallMutPercent, gbc_labelSmallMutPercent);

		GridBagConstraints gbc_chckbxSmallMutations = new GridBagConstraints();
		gbc_chckbxSmallMutations.insets = new Insets(5, 5, 5, 0);
		gbc_chckbxSmallMutations.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxSmallMutations.gridx = 4;
		gbc_chckbxSmallMutations.gridy = 0;
		panel_1.add(chckbxSmallMutations, gbc_chckbxSmallMutations);

		JPanel panel_6 = new JPanel();
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.weightx = 1.0;
		gbc_panel_6.gridwidth = 5;
		gbc_panel_6.insets = new Insets(0, 0, 0, 5);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 0;
		gbc_panel_6.gridy = 1;
		panel_1.add(panel_6, gbc_panel_6);
		GridBagLayout gbl_panel_6 = new GridBagLayout();
		panel_6.setLayout(gbl_panel_6);

		JLabel lblProportionOfPopulation = new JLabel();
		GridBagConstraints gbc_lblProportionOfPopulation = new GridBagConstraints();
		gbc_lblProportionOfPopulation.anchor = GridBagConstraints.WEST;
		gbc_lblProportionOfPopulation.insets = new Insets(0, 5, 5, 5);
		gbc_lblProportionOfPopulation.gridx = 0;
		gbc_lblProportionOfPopulation.gridy = 0;
		panel_6.add(lblProportionOfPopulation, gbc_lblProportionOfPopulation);

		final JLabel lblMutations = new JLabel();
		lblMutations.setHorizontalAlignment(SwingConstants.TRAILING);
		lblMutations.setPreferredSize(new Dimension(130, 14));
		GridBagConstraints gbc_lblMutations = new GridBagConstraints();
		gbc_lblMutations.anchor = GridBagConstraints.WEST;
		gbc_lblMutations.insets = new Insets(0, 5, 5, 5);
		gbc_lblMutations.gridx = 1;
		gbc_lblMutations.gridy = 0;
		panel_6.add(lblMutations, gbc_lblMutations);

		sliderPropMut = new JSlider();
		sliderPropMut.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lblMutations.setText(sliderPropMut.getValue()+" mutations");
			}
		});
		sliderPropMut.setMinimum(1);
		sliderPropMut.setMaximum(1000);
		sliderPropMut.setPaintTicks(false);
		sliderPropMut.setValue(50);
		sliderPropMut.setSnapToTicks(false);
		sliderPropMut.setMajorTickSpacing(10);
		GridBagConstraints gbc_sliderPropMut = new GridBagConstraints();
		gbc_sliderPropMut.weightx = 1.0;
		gbc_sliderPropMut.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderPropMut.insets = new Insets(0, 5, 5, 5);
		gbc_sliderPropMut.anchor = GridBagConstraints.NORTHWEST;
		gbc_sliderPropMut.gridx = 2;
		gbc_sliderPropMut.gridy = 0;
		panel_6.add(sliderPropMut, gbc_sliderPropMut);

		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new TitledBorder(null, TIT_BIRTH_DEATH, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_11 = new GridBagConstraints();
		gbc_panel_11.insets = new Insets(0, 0, 5, 0);
		gbc_panel_11.fill = GridBagConstraints.BOTH;
		gbc_panel_11.gridx = 0;
		gbc_panel_11.gridy = 3;
		panel_N.add(panel_11, gbc_panel_11);
		GridBagLayout gbl_panel_11 = new GridBagLayout();
		panel_11.setLayout(gbl_panel_11);

		chckbxRecombination = new JCheckBox();
		GridBagConstraints gbc_chckbxRecombination = new GridBagConstraints();
		gbc_chckbxRecombination.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxRecombination.insets = new Insets(5, 5, 5, 5);
		gbc_chckbxRecombination.gridx = 0;
		gbc_chckbxRecombination.gridy = 0;
		panel_11.add(chckbxRecombination, gbc_chckbxRecombination);
		chckbxRecombination.setSelected(true);

		sliderRecombinationProb = new JSlider();
		sliderRecombinationProb.setValue(3);
		sliderRecombinationProb.setMajorTickSpacing(1);
		sliderRecombinationProb.setMinimum(1);
		sliderRecombinationProb.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				chckbxRecombination.setText(CHK_BOX_RECOMBINATION + sliderRecombinationProb.getValue()+"%");
			}
		});
		GridBagConstraints gbc_sliderRecombinationProb = new GridBagConstraints();
		gbc_sliderRecombinationProb.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderRecombinationProb.weightx = 1.0;
		gbc_sliderRecombinationProb.insets = new Insets(0, 0, 5, 5);
		gbc_sliderRecombinationProb.gridx = 1;
		gbc_sliderRecombinationProb.gridy = 0;
		panel_11.add(sliderRecombinationProb, gbc_sliderRecombinationProb);

		JPanel panel_7 = new JPanel();
		GridBagConstraints gbc_panel_7 = new GridBagConstraints();
		gbc_panel_7.anchor = GridBagConstraints.WEST;
		gbc_panel_7.weightx = 1.0;
		gbc_panel_7.insets = new Insets(0, 0, 5, 0);
		gbc_panel_7.gridwidth = 2;
		gbc_panel_7.gridx = 0;
		gbc_panel_7.gridy = 1;
		panel_11.add(panel_7, gbc_panel_7);
		GridBagLayout gbl_panel_7 = new GridBagLayout();
		panel_7.setLayout(gbl_panel_7);
		
		JLabel lblSelection = new JLabel();
		GridBagConstraints gbc_lblSelection = new GridBagConstraints();
		gbc_lblSelection.anchor = GridBagConstraints.WEST;
		gbc_lblSelection.insets = new Insets(0, 5, 0, 5);
		gbc_lblSelection.gridx = 0;
		gbc_lblSelection.gridy = 0;
		panel_7.add(lblSelection, gbc_lblSelection);

		chckbxSelectionReproduction = new JCheckBox();
		GridBagConstraints gbc_chckbxSelectionReproduction = new GridBagConstraints();
		gbc_chckbxSelectionReproduction.anchor = GridBagConstraints.WEST;
		gbc_chckbxSelectionReproduction.insets = new Insets(0, 5, 0, 5);
		gbc_chckbxSelectionReproduction.gridx = 1;
		gbc_chckbxSelectionReproduction.gridy = 0;
		panel_7.add(chckbxSelectionReproduction, gbc_chckbxSelectionReproduction);
		chckbxSelectionReproduction.setSelected(true);

		chckbxSelectionSurvival = new JCheckBox();
		GridBagConstraints gbc_chckbxSelectionSurvival = new GridBagConstraints();
		gbc_chckbxSelectionSurvival.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxSelectionSurvival.insets = new Insets(0, 5, 0, 5);
		gbc_chckbxSelectionSurvival.anchor = GridBagConstraints.WEST;
		gbc_chckbxSelectionSurvival.gridx = 2;
		gbc_chckbxSelectionSurvival.gridy = 0;
		panel_7.add(chckbxSelectionSurvival, gbc_chckbxSelectionSurvival);
		chckbxSelectionSurvival.setSelected(true);

		JPanel panel_12 = new JPanel();
		GridBagConstraints gbc_panel_12 = new GridBagConstraints();
		gbc_panel_12.weightx = 1.0;
		gbc_panel_12.gridwidth = 2;
		gbc_panel_12.fill = GridBagConstraints.BOTH;
		gbc_panel_12.gridx = 0;
		gbc_panel_12.gridy = 2;
		panel_11.add(panel_12, gbc_panel_12);
		GridBagLayout gbl_panel_12 = new GridBagLayout();
		panel_12.setLayout(gbl_panel_12);

		JLabel lblProportionOfPopulation_1 = new JLabel();
		GridBagConstraints gbc_lblProportionOfPopulation_1 = new GridBagConstraints();
		gbc_lblProportionOfPopulation_1.anchor = GridBagConstraints.WEST;
		gbc_lblProportionOfPopulation_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblProportionOfPopulation_1.gridx = 0;
		gbc_lblProportionOfPopulation_1.gridy = 0;
		panel_12.add(lblProportionOfPopulation_1, gbc_lblProportionOfPopulation_1);

		final JLabel lblRep = new JLabel();
		GridBagConstraints gbc_lblRep = new GridBagConstraints();
		gbc_lblRep.anchor = GridBagConstraints.WEST;
		gbc_lblRep.insets = new Insets(0, 5, 5, 5);
		gbc_lblRep.gridx = 1;
		gbc_lblRep.gridy = 0;
		panel_12.add(lblRep, gbc_lblRep);
		lblRep.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRep.setPreferredSize(new Dimension(150, 14));

		sliderPropRep = new JSlider();
		sliderPropRep.setMinimum(1);
		sliderPropRep.setMaximum(1000);
		sliderPropRep.setPaintTicks(false);
		sliderPropRep.setValue(500);
		sliderPropRep.setSnapToTicks(false);
		sliderPropRep.setMajorTickSpacing(10);		
		sliderPropRep.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lblRep.setText(sliderPropRep.getValue()+BIRTH_DEATH_VAL);
			}
		});
		GridBagConstraints gbc_sliderPropRep = new GridBagConstraints();
		gbc_sliderPropRep.weightx = 1.0;
		gbc_sliderPropRep.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderPropRep.insets = new Insets(0, 5, 5, 5);
		gbc_sliderPropRep.anchor = GridBagConstraints.NORTHWEST;
		gbc_sliderPropRep.gridx = 2;
		gbc_sliderPropRep.gridy = 0;
		panel_12.add(sliderPropRep, gbc_sliderPropRep);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 4;
		panel_N.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		panel_3.setLayout(gbl_panel_3);

		JPanel panel_8 = new JPanel();
		GridBagConstraints gbc_panel_8 = new GridBagConstraints();
		gbc_panel_8.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_8.gridwidth = 2;
		gbc_panel_8.gridx = 0;
		gbc_panel_8.gridy = 0;
		panel_3.add(panel_8, gbc_panel_8);
		GridBagLayout gbl_panel_8 = new GridBagLayout();
		panel_8.setLayout(gbl_panel_8);

		JLabel lblBestIndividual = new JLabel();
		GridBagConstraints gbc_lblBestIndividual = new GridBagConstraints();
		gbc_lblBestIndividual.anchor = GridBagConstraints.WEST;
		gbc_lblBestIndividual.insets = new Insets(10, 5, 10, 5);
		gbc_lblBestIndividual.gridx = 0;
		gbc_lblBestIndividual.gridy = 0;
		panel_8.add(lblBestIndividual, gbc_lblBestIndividual);

		progressBarBestIndividual = new JProgressBar();
		progressBarBestIndividual.setMinimum(0);
		progressBarBestIndividual.setMaximum(100);
		GridBagConstraints gbc_progressBarBestIndividual = new GridBagConstraints();
		gbc_progressBarBestIndividual.weightx = 1.0;
		gbc_progressBarBestIndividual.fill = GridBagConstraints.BOTH;
		gbc_progressBarBestIndividual.insets = new Insets(5, 5, 5, 5);
		gbc_progressBarBestIndividual.anchor = GridBagConstraints.NORTHWEST;
		gbc_progressBarBestIndividual.gridx = 1;
		gbc_progressBarBestIndividual.gridy = 0;
		panel_8.add(progressBarBestIndividual, gbc_progressBarBestIndividual);

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 0, 5);
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 1;
		panel_3.add(panel_4, gbc_panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnStartPause = new JButton("");
		panel_4.add(btnStartPause);
		btnStartPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (running){
					new Thread(new Runnable(){
						public void run(){
							pauseSimulator();
						}
					}).start();
				}else{
					new Thread(new Runnable(){
						public void run(){
							try {
								startSimulator();
							} catch (Exception e) {
								e.printStackTrace();
								String message = e.getMessage(); 
								message += "\n Java exception : "+e.getCause();
								for (StackTraceElement el : e.getStackTrace()){
									message += "\n\tat " + el.toString();
								}	
								JOptionPane.showMessageDialog(new JFrame(), "Cannot start the simulator: " + message, "Start", JOptionPane.ERROR_MESSAGE);
							}
						}
					}).start();
				}
			}
		});
		btnStartPause.setIcon(new ImageIcon(EvoSimulator.class.getResource("/evosimulator/resources/player_play.png")));

		JButton button = new JButton("");
		panel_4.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable(){
					public void run(){
						resetSimulator();
					}
				}).start();
			}
		});
		button.setIcon(new ImageIcon(EvoSimulator.class.getResource("/evosimulator/resources/quick_restart.png")));

		JPanel panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(0, 0, 5, 0);
		gbc_panel_5.weightx = 1.0;
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.anchor = GridBagConstraints.WEST;
		gbc_panel_5.gridx = 1;
		gbc_panel_5.gridy = 1;
		panel_3.add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[]{0, 0};
		gbl_panel_5.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_5.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_5.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel_5.setLayout(gbl_panel_5);

		JPanel panel_9 = new JPanel();
		GridBagConstraints gbc_panel_9 = new GridBagConstraints();
		gbc_panel_9.insets = new Insets(0, 0, 5, 0);
		gbc_panel_9.fill = GridBagConstraints.BOTH;
		gbc_panel_9.gridx = 0;
		gbc_panel_9.gridy = 1;
		panel_5.add(panel_9, gbc_panel_9);
		GridBagLayout gbl_panel_9 = new GridBagLayout();
		gbl_panel_9.columnWidths = new int[]{63, 80, 0};
		gbl_panel_9.rowHeights = new int[]{14, 0};
		gbl_panel_9.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_9.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_9.setLayout(gbl_panel_9);

		JLabel lblStep = new JLabel();
		GridBagConstraints gbc_lblStep = new GridBagConstraints();
		gbc_lblStep.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblStep.insets = new Insets(5, 5, 5, 5);
		gbc_lblStep.gridx = 0;
		gbc_lblStep.gridy = 0;
		panel_9.add(lblStep, gbc_lblStep);

		labelStep = new JLabel("0");
		GridBagConstraints gbc_labelStep = new GridBagConstraints();
		gbc_labelStep.insets = new Insets(5, 5, 5, 5);
		gbc_labelStep.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelStep.weightx = 1.0;
		gbc_labelStep.anchor = GridBagConstraints.NORTHWEST;
		gbc_labelStep.gridx = 1;
		gbc_labelStep.gridy = 0;
		panel_9.add(labelStep, gbc_labelStep);

		JPanel panel_10 = new JPanel();
		GridBagConstraints gbc_panel_10 = new GridBagConstraints();
		gbc_panel_10.fill = GridBagConstraints.BOTH;
		gbc_panel_10.gridx = 0;
		gbc_panel_10.gridy = 2;
		panel_5.add(panel_10, gbc_panel_10);
		GridBagLayout gbl_panel_10 = new GridBagLayout();
		gbl_panel_10.columnWidths = new int[]{118, 150, 0};
		gbl_panel_10.rowHeights = new int[]{14, 0};
		gbl_panel_10.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_10.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_10.setLayout(gbl_panel_10);

		JLabel lblMeanScore = new JLabel();
		GridBagConstraints gbc_lblMeanScore = new GridBagConstraints();
		gbc_lblMeanScore.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblMeanScore.insets = new Insets(5, 5, 5, 5);
		gbc_lblMeanScore.gridx = 0;
		gbc_lblMeanScore.gridy = 0;
		panel_10.add(lblMeanScore, gbc_lblMeanScore);

		labelMeanScore = new JLabel("0");
		GridBagConstraints gbc_labelMeanScore = new GridBagConstraints();
		gbc_labelMeanScore.weightx = 1.0;
		gbc_labelMeanScore.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelMeanScore.insets = new Insets(5, 5, 5, 5);
		gbc_labelMeanScore.anchor = GridBagConstraints.NORTHWEST;
		gbc_labelMeanScore.gridx = 1;
		gbc_labelMeanScore.gridy = 0;
		panel_10.add(labelMeanScore, gbc_labelMeanScore);

		JPanel panel_S = new JPanel();
		panel_S.setBackground(Color.WHITE);
		FlowLayout flowLayout = (FlowLayout) panel_S.getLayout();
		flowLayout.setHgap(20);
		main_panel.add(panel_S, BorderLayout.SOUTH);

		JLabel label_3 = new JLabel("");
		label_3.setIcon(new ImageIcon(EvoSimulator.class.getResource("/evosimulator/resources/evolution.jpg")));
		panel_S.add(label_3);

		JLabel lblEvosimulatorByRaphal = new JLabel();
		panel_S.add(lblEvosimulatorByRaphal);

		JLabel label_2 = new JLabel("");
		label_2.setIcon(new ImageIcon(EvoSimulator.class.getResource("/evosimulator/resources/lane_40.png")));
		panel_S.add(label_2);
		
		switch(l){
		case fr:
			lblOptimalIndividual.setText("Individu optimal");
			txtGoal.setText("Le 'Dessein Intelligent' est vraiment ABSURDE !");
			lblPopulationSize.setText("Taille de la population");
			lblNbrOfSteps.setText("Nbr de générations avant pause");
			//chckbxBigMutations.setText("Mutations importantes");
			chckbxBigMutations.setText("Mutations"); //TODO remettre Mutations importantes
			chckbxSmallMutations.setText("Mutations mineures");
			lblProportionOfPopulation.setText("Nbr de mutations par génération : ");
			lblMutations.setText("50 mutations");
			chckbxRecombination.setText("Reproduction sexuée avec probabilité de recombinaison de 3%     ");
			lblSelection.setText("Sélection");
			chckbxSelectionReproduction.setText("pour la reproduction");
			chckbxSelectionSurvival.setText("pour la survie");
			lblProportionOfPopulation_1.setText("Nbr de naissances & décès par génération : ");
			lblRep.setText("500 naissances & décès");
			lblBestIndividual.setText("Individu le plus apte : ");
			lblStep.setText("Génération : ");
			lblMeanScore.setText("Score moyen de la population : ");
			lblEvosimulatorByRaphal.setText("EvoSimulator par Rapha\u00EBl Helaers & Michel Milinkovitch");
			break;
		case en:
		default:
			lblOptimalIndividual.setText("Optimal individual");
			txtGoal.setText("'Intelligent Design' is really ABSURD !");
			lblPopulationSize.setText("Population size");
			lblNbrOfSteps.setText("Nbr of generations before pausing");
			//chckbxBigMutations.setText("Major mutations");
			chckbxBigMutations.setText("Mutations"); //TODO remettre Major mutations
			chckbxSmallMutations.setText("Minor mutations");
			lblProportionOfPopulation.setText("Nbr of mutations per generation: ");
			lblMutations.setText("50 mutations");
			chckbxRecombination.setText("Sexual reproduction with probability of recombination of 3%     ");
			lblSelection.setText("Selection");
			chckbxSelectionReproduction.setText("for reproduction");
			chckbxSelectionSurvival.setText("for survival");
			lblProportionOfPopulation_1.setText("Nbr of births & deaths per generation: ");
			lblRep.setText("500 births & deaths");
			lblBestIndividual.setText("Fittest individual: ");
			lblStep.setText("Generation: ");
			lblMeanScore.setText("Mean population score: ");
			lblEvosimulatorByRaphal.setText("EvoSimulator by Rapha\u00EBl Helaers & Michel Milinkovitch");
			break;
		}

	}
	
	private void mutationsCheckBoxChange(){
		sliderMutationRatio.setEnabled(chckbxBigMutations.isSelected() && chckbxSmallMutations.isSelected());
		if (chckbxBigMutations.isSelected() && chckbxSmallMutations.isSelected()){
			sliderMutationRatio.setMinimum(1);
			sliderMutationRatio.setMaximum(99);
			sliderMutationRatio.setValue(50);
		}else if (chckbxBigMutations.isSelected() && !chckbxSmallMutations.isSelected()){
			sliderMutationRatio.setMinimum(0);
			sliderMutationRatio.setMaximum(100);
			sliderMutationRatio.setValue(100);
		}else if (!chckbxBigMutations.isSelected() && chckbxSmallMutations.isSelected()){
			sliderMutationRatio.setMinimum(0);
			sliderMutationRatio.setMaximum(100);
			sliderMutationRatio.setValue(0);
		}else{
			sliderMutationRatio.setMinimum(1);
			sliderMutationRatio.setMaximum(99);
			sliderMutationRatio.setValue(50);		
			labelBigMutPercent.setText("(0%)");
			labelSmallMutPercent.setText("(0%)");
		}
	}

	private void startSimulator() throws Exception {
		btnStartPause.setIcon(new ImageIcon(EvoSimulator.class.getResource("/evosimulator/resources/player_pause.png")));
		running = true;
		done = false;
		goal = txtGoal.getText().toCharArray();
		for (int i=0 ; i < goal.length ; i++){
			if (!charPos.containsKey(goal[i])){
				throw new Exception("The character '" + goal[i] + "' is not permitted.");
			}
		}
		boolean mutations = chckbxBigMutations.isSelected() || chckbxSmallMutations.isSelected();
		fineScore = chckbxSmallMutations.isSelected();
		double rationBigMut = sliderMutationRatio.getValue() / 100.0;
		boolean recombination = chckbxRecombination.isSelected();
		double probaRecombination = sliderRecombinationProb.getValue() / 100.0;
		boolean selectionReproduction = chckbxSelectionReproduction.isSelected();
		boolean selectionSurvival = chckbxSelectionSurvival.isSelected();
		int popSize = Integer.parseInt(spinnerPopSize.getValue().toString());
		int indLength = goal.length;
		int nbrMutations = sliderPropMut.getValue();
		int nbrReproduction = sliderPropRep.getValue();
		int maxSteps = Integer.parseInt(textFieldNbrStepsBefPausing.getText());
		if (maxSteps <= 0) maxSteps = Integer.MAX_VALUE;
		if (population == null || population.length != popSize){
			initPopulation(popSize, indLength);
			generation = 0;
			labelStep.setText(""+0);
			progressBarBestIndividual.setValue(0);
			progressBarBestIndividual.setString("");
			progressBarBestIndividual.setStringPainted(true);
			labelMeanScore.setText("0");
			graph.reset();
		}
		for (int step = 0 ; step < maxSteps && running && scores[0] < 1.0 ; step++){
			generation++;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					labelStep.setText(""+generation);
				}
			});
			if (mutations){
				for (int i=0 ; i < nbrMutations ; i++){
					if (Math.random() < rationBigMut){
						mutate(randInt(popSize-1)+1, true);						
					}else{
						mutate(randInt(popSize-1)+1, false);
					}
				}
			}
			for (int i=0 ; i < nbrReproduction ; i++){
				int mate1 = (selectionReproduction) ? select(true) : randInt(popSize);
				int victim = (selectionSurvival) ? select(false) : randInt(popSize);
				if (recombination){
					int mate2;
					do{
						mate2 = (selectionReproduction) ? select(true) : randInt(popSize);
					}while(mate2 == mate1);
					if (selectionReproduction){
						if (scores[mate1] < scores[mate2]){
							int temp = mate1;
							mate1 = mate2;
							mate2 = temp;
						}
					}
					population[victim] = recombine(population[mate1], population[mate2], probaRecombination);
				}else{
					population[victim] = new String(population[mate1]).toCharArray();
				}
				scores[victim] = score(population[victim]);
			}	
			putBestIndividualFirst();
			scoreSum = 0;
			for (int i=0 ; i < popSize ; i++){
				double score = scores[i];
				scoreSum += score;
			}			
			final double meanScorePop = scoreSum/(double)popSize;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBarBestIndividual.setValue((int)(100*scores[0]));
					progressBarBestIndividual.setString(new String(population[0]));
					progressBarBestIndividual.setStringPainted(true);
					labelMeanScore.setText(doubleToPercent(meanScorePop,0));
					graph.addPoint(scores[0]);
				}
			});
		}
		done = true;
		pauseSimulator();
	}

	private void pauseSimulator(){
		btnStartPause.setIcon(new ImageIcon(EvoSimulator.class.getResource("/evosimulator/resources/player_play.png")));
		running = false;
	}

	private void resetSimulator(){
		if (running){
			pauseSimulator();
		}
		while (!done){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		population = null;
		generation = 0;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				labelStep.setText(""+0);
				progressBarBestIndividual.setValue(0);
				progressBarBestIndividual.setString("");
				progressBarBestIndividual.setStringPainted(true);
				labelMeanScore.setText("0");
				graph.reset();
			}
		});
	}

	private void initPopulation(int popSize, int goalLength){
		generation = 0;
		population = new char[popSize][goalLength];
		scores = new double[popSize];
		for (int i=0 ; i < popSize ; i++){
			for (int j=0 ; j < goalLength ; j++){
				population[i][j] = getRandomChar();
			}
		}
		scoreSum = 0;
		for (int i=0 ; i < popSize ; i++){
			scores[i] = score(population[i]);
			scoreSum += scores[i];
		}
	}

	private char getRandomChar(){
		return characters[(int) (Math.random() * nchars)];		
	}

	public int randInt(int max){
		return (int)(Math.random()*max);
	}

	private void putBestIndividualFirst() {
		//Put the best individual in the position 0 of the population
		//Best individual will NOT be mutated at next generation
		int bestSol = 0;
		for (int ind=1 ; ind < population.length ; ind++) {
			if (scores[ind] > scores[bestSol]){
				bestSol = ind;
			}
		}
		if (bestSol != 0) {
			char[] bestIndividual = population[bestSol];
			double bestScore = scores[bestSol];
			population[bestSol] = population[0];
			scores[bestSol] = scores[0];
			population[0] = bestIndividual;
			scores[0] = bestScore;
		}
	}

	public double score(char[] individual){
		if (fineScore){
			double score = 0.0;
			for (int i=0 ; i < individual.length ; i++){
				int posInd = charPos.get(individual[i]);
				int posGoal = charPos.get(goal[i]);
				int dist = Math.abs(posGoal - posInd);
				if (dist == 0) score += 1.0;
				else if (dist < 10) score += 0.5;
				else if (dist < 20) score += 0.2;
				else score += 0;
			}
			return score/(double)individual.length;
		}else{
			int equals = 0;
			for (int i=0 ; i < individual.length ; i++){
				if (individual[i] == goal[i]) equals++;
			}
			return (double)equals/(double)individual.length;
		}
	}

	private void mutate(int posIndividual, boolean bigMutation){
		char[] individual = population[posIndividual];
		if (bigMutation){
			individual[randInt(individual.length)] = getRandomChar();
		}else{
			int ind = randInt(individual.length);
			char c = individual[ind];
			int pos = charPos.get(c);
			if (Math.random() < 0.5){
				if (pos == 0) individual[ind] = characters[nchars-1];
				else individual[ind] = characters[pos-1];
			}else{
				if (pos == nchars-1) individual[ind] = characters[0];
				else individual[ind] = characters[pos+1];
			}
		}
		scores[posIndividual] = score(individual);
	}

	private int select(boolean fittest){
		double random = Math.random() * (fittest?scoreSum:population.length-scoreSum);
		double current = 0.0;
		for (int i=0 ; i < population.length ; i++){
			double next = current + (fittest?scores[i]:1.0-scores[i]);
			if (random >= current && random < next) return i;
			current = next;
		}
		return population.length-1;
	}

	@SuppressWarnings("unused")
	private String recombine(String s1, String s2, int start, int end) {
		if (start > end)
			return recombine(s2, s1, end, start);

		String s = "";

		if (start > 0)
			s = s1.substring(0, start);
		while (start <= end)
			s = s + s2.charAt(start++);
		if (end < s1.length())
			s = s + s1.substring(end + 1);

		return s;
	}

	private char[] recombine(char[] mate1, char[] mate2, double probaRecombination){
		char[] offspring = new char[mate1.length];
		boolean parent = true;
		for (int i=0 ; i < offspring.length ; i++){
			offspring[i] = (parent) ? mate1[i] : mate2[i];
			if (Math.random() < probaRecombination) parent = !parent;
		}
		return offspring;
	}
	
	public String doubleToPercent(double x, int d) {
		x *= 100;
		NumberFormat fmt = NumberFormat.getInstance(Locale.US);
		if (fmt instanceof DecimalFormat) { 		 
			DecimalFormatSymbols symb = new DecimalFormatSymbols(Locale.US);
			symb.setGroupingSeparator(' ');
			((DecimalFormat) fmt).setDecimalFormatSymbols(symb);
			((DecimalFormat) fmt).setMaximumFractionDigits(d);
			//((DecimalFormat) fmt).setMinimumFractionDigits(d);
			((DecimalFormat) fmt).setGroupingUsed(true);
		}
		String s = fmt.format(x) + "%";		
		return s;		
	}

	public class GraphPanel extends JPanel {
		private final String AXIS_X;
		private final String AXIS_Y;
		private List<Double> points = new ArrayList<Double>();

		public GraphPanel(Language l){
			super();
			switch (l) {
			case fr:
				AXIS_X = "Générations";
				AXIS_Y = "Score du plus apte";
				break;
			case en:
			default:
				AXIS_X = "Generations";
				AXIS_Y = "Score of fittest";
				break;
			}
		}
		
		public void addPoint(double point){
			points.add(point);
			repaint();
		}

		public void reset(){
			points.clear();
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {	
			int offset = 15;
			int W = this.getWidth()-2*offset;
			int H = this.getHeight()-2*offset;
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, W+2*offset, H+2*offset);
			int step = 1;
			while (points.size() / step > W){
				step++;
			}
			int x = offset;
			g.setColor(Color.BLACK);
			g.drawLine(x, offset, x, offset+H);
			Graphics2D g2 = (Graphics2D)g;
			g2.rotate(-Math.toRadians(90), x-2, offset+H);
			g.drawString(AXIS_Y, x-2, offset+H);
			g2.rotate(Math.toRadians(90), x-2, offset+H);
			g.setColor(Color.BLUE);
			for (int i=0 ; i < points.size()-step ; i+=step){
				int y =  offset+H-(int)(points.get(i)*H);
				int nextY = offset+H-(int)(points.get(i+step)*H);
				g.drawLine(x, y, x+1, nextY);
				x++;
			}
			g.setColor(Color.BLACK);
			g.drawLine(offset, offset+H, offset+W, offset+H);
			g.drawString(AXIS_X, offset+4, offset+H+12);
		}
	}

	@Override
	public void init(){
		final Language language;
		if (getParameter("language") != null && Language.valueOf(getParameter("language")) != null){
			language = Language.valueOf(getParameter("language"));
		}else{
    	language = Language.en;
    }
    try {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                createGUI(language);
            }
        });
    } catch (Exception e) {
        System.err.println("createGUI didn't successfully complete");
    }	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
