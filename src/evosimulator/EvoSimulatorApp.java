package evosimulator;

import java.awt.HeadlessException;
import java.awt.BorderLayout;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JCheckBox;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import java.awt.FlowLayout;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLEditorKit;

import javax.swing.event.ChangeEvent;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
*
* @author Raphael Helaers
*
*/

public class EvoSimulatorApp extends JFrame {
	public enum Language {en, fr}
	
	private static final String chars =
		"aàAbBcçCdDéeèEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuùUvVwWxXyYzZ 0123456789,.?!'’\"()";
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
	private JCheckBox chckbxSexualReproduction;
	private JCheckBox chckbxAsexualReproduction;
	private JCheckBox chckbxSelectionReproduction;
	private JProgressBar progressBarBestIndividual;
	private JLabel labelStep;
	private JLabel labelMeanScore;
	private JLabel labelBestScore;
	private JLabel lblUseMutations;
	private JLabel lblRateMutation;
	private JButton btnStartPause;
	private JCheckBox chckbxSmallMutations;
	private JSlider sliderMutationRatio;
	private JCheckBox chckbxSelectionSurvival;
	private JLabel labelBigMutPercent = new JLabel("(100%)");
	private JLabel labelSmallMutPercent = new JLabel("(0%)");
	private JSlider sliderRateMutation;
	private JSlider sliderNbrBirthsDeaths;
	private JSpinner spinnerPopSize;
	private JSlider sliderPercentRecombination;
	private JCheckBox lblBestScore;
	private JCheckBox lblMeanScore;
	
	//Default values of parameters
	final int DEFAULT_POP_SIZE = 1000;
	final int DEFAULT_NBR_STEP_PAUSE = 0;
	final boolean DEFAULT_USE_BIG_MUTATIONS = true;
	final int DEFAULT_RATE_MUTATION = 200;
	final boolean DEFAULT_USE_ASEXUAL_REPRODUCTION = false;
	final boolean DEFAULT_USE_SEXUAL_REPRODUCTION = true;
	final int DEFAULT_PERCENT_RECOMBINATION = 3;
	final boolean DEFAULT_SELECTION_FOR_REPRODUCTION = true;
	final boolean DEFAULT_SELECTION_FOR_SURVIVAL = true;
	final int DEFAULT_NBR_BIRTHS_DEATHS = 500;

	//Language dependant text
	final String TIT_BIRTH_DEATH;
	final String TIT_MUTATIONS;
	final String TIT_SELECTION;
	final String LBL_OPTIMAL_IND;
	final String LBL_POP_SIZE;
	final String LBL_NBR_STEPS;
	final String LBL_MUTATION_RATE;
	final String LBL_PROP_POP;
	final String LBL_NBR_BIRTHS_DEATHS;
	final String LBL_BEST_IND;
	final String LBL_GENERATION;
	final String LBL_BEST_SCORE;
	final String LBL_MEAN_SCORE;
	final String LBL_ABOUT;
	final String TXTFIELD_GOAL;
	final String CHKBOX_SEXUAL_REPRODUCTION;
	final String CHKBOX_ASEXUAL_REPRODUCTION;
	final String CHKBOX_BIG_MUT;
	final String CHKBOX_SMALL_MUT;
	final String CHKBOX_SELECTION_REPRODUCTION;
	final String CHKBOX_SELECTION_SURVIVAL;
	final String BUTTON_START;
	final String BUTTON_RESET;
	final String BUTTON_DEFAULT;
	final String BUTTON_HELP;

	private Language language; 
	
	public EvoSimulatorApp(Language l) throws HeadlessException {
		language = l;
		switch(language){
		case fr :
			TXTFIELD_GOAL = "Cette phrase est COMPLEXE et n'est pas due au hasard";
			TIT_BIRTH_DEATH = "Naissances et décès";
			TIT_MUTATIONS = "Mutations";
			TIT_SELECTION = "Sélection";
			LBL_OPTIMAL_IND= "Individu optimal";
			LBL_POP_SIZE= "Taille de la population";
			LBL_NBR_STEPS= "Nbr de générations avant pause";
			LBL_MUTATION_RATE= "Taux de mutation à la reproduction : ";
			LBL_PROP_POP= "Nbr de naissances & décès par génération : ";
			LBL_NBR_BIRTHS_DEATHS= " naissances & décès";
			LBL_BEST_IND= "Individu le plus apte : ";
			LBL_GENERATION= "Génération : ";
			LBL_BEST_SCORE= "Meilleur score de la population : ";
			LBL_MEAN_SCORE= "Moyenne (± écart type) de la population : ";
			LBL_ABOUT= "EvoSimulator par Rapha\u00EBl Helaers & Michel Milinkovitch";
			CHKBOX_ASEXUAL_REPRODUCTION = "Reproduction asexuée";
			CHKBOX_SEXUAL_REPRODUCTION = "Reproduction sexuée avec probabilité de recombinaison de ";
			CHKBOX_BIG_MUT= "Des mutations peuvent se produire à la reproduction";
			CHKBOX_SMALL_MUT= "Mutations mineures";
			CHKBOX_SELECTION_REPRODUCTION= "Sélection lors de la reproduction";
			CHKBOX_SELECTION_SURVIVAL= "Sélection lors de la survie";
			BUTTON_START= "Démarrer / Mettre en pause les naissances et décès";
			BUTTON_RESET= "Réinitialiser la génération à 0";
			BUTTON_DEFAULT= "Réinitialiser les paramètres aux valeurs par défaut";
			BUTTON_HELP= "Aide";
			break;
		case en :
		default:
			TXTFIELD_GOAL = "This sentence is COMPLEX and cannot emerge by chance";
			TIT_BIRTH_DEATH = "Births & deaths";
			TIT_MUTATIONS = "Mutations";
			TIT_SELECTION = "Selection";
			LBL_OPTIMAL_IND= "Optimal individual";
			LBL_POP_SIZE= "Population size";
			LBL_NBR_STEPS= "Nbr of generations before pausing";
			LBL_MUTATION_RATE= "Mutation rate at reproduction: ";
			LBL_PROP_POP= "Nbr of births & deaths per generation: ";
			LBL_NBR_BIRTHS_DEATHS= " births & deaths";
			LBL_BEST_IND= "Fittest individual: ";
			LBL_GENERATION= "Generation: ";
			LBL_BEST_SCORE= "Population best score: ";
			LBL_MEAN_SCORE= "Population mean (± SD) score: ";
			LBL_ABOUT= "EvoSimulator by Rapha\u00EBl Helaers & Michel Milinkovitch";
			CHKBOX_ASEXUAL_REPRODUCTION = "Asexual reproduction";
			CHKBOX_SEXUAL_REPRODUCTION = "Sexual reproduction with probability of recombination of ";
			CHKBOX_BIG_MUT= "Mutations can happen at reproduction";
			CHKBOX_SMALL_MUT= "Minor mutations";
			CHKBOX_SELECTION_REPRODUCTION= "Selection for reproduction";
			CHKBOX_SELECTION_SURVIVAL= "Selection for survival";
			BUTTON_START= "Start / Pause generation births and deaths";
			BUTTON_RESET= "Reset to generation 0";
			BUTTON_DEFAULT= "Reset parameters to default";
			BUTTON_HELP= "Help";
			break;
		}
		createGUI();		
		pack();
	}

	private void createGUI(){
		setTitle("EvoSimulator");
		setIconImage(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/evolution_32.png")).getImage()	);
		
		for (int i=0 ; i < nchars ; i++){
			charPos.put(characters[i], i);
		}
		
		setLayout(new BorderLayout());

		JPanel main_panel = new JPanel();
		add(main_panel, BorderLayout.CENTER);
		main_panel.setLayout(new BorderLayout());

		graph = new GraphPanel(language);
		main_panel.add(graph, BorderLayout.CENTER);

		JPanel panel_N = new JPanel();
		main_panel.add(panel_N, BorderLayout.NORTH);
		GridBagLayout gbl_panel_N = new GridBagLayout();
		panel_N.setLayout(gbl_panel_N);

		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		panel_N.add(getPanelOptimalIndividual(), gbc_panel);

		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		panel_N.add(getPanelPopulation(), gbc_panel_2);

		GridBagConstraints gbc_panel_11 = new GridBagConstraints();
		gbc_panel_11.insets = new Insets(0, 0, 5, 0);
		gbc_panel_11.fill = GridBagConstraints.BOTH;
		gbc_panel_11.gridx = 0;
		gbc_panel_11.gridy = 2;
		panel_N.add(getPanelBirthsAndDeaths(), gbc_panel_11);

		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		panel_N.add(getPanelMutations(), gbc_panel_1);

		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 4;
		panel_N.add(getPanelSelection(), gbc_panel_4);
		
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 5;
		panel_N.add(getPanelSimulationControls(), gbc_panel_3);
		
		JPanel panel_S = new JPanel();
		panel_S.setBackground(Color.WHITE);
		FlowLayout flowLayout = (FlowLayout) panel_S.getLayout();
		flowLayout.setHgap(20);
		main_panel.add(panel_S, BorderLayout.SOUTH);

		JLabel label_3 = new JLabel("");
		label_3.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/evolution.jpg")));
		panel_S.add(label_3);

		JLabel lblEvosimulatorByRaphal = new JLabel(LBL_ABOUT);
		panel_S.add(lblEvosimulatorByRaphal);

		JLabel label_2 = new JLabel("");
		label_2.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/lane_40.png")));
		label_2.setToolTipText("https://www.lanevol.org/");
		label_2.addMouseListener(new MouseAdapter() {
    	@Override
    	public void mouseClicked(MouseEvent arg0) {
    		openURL("https://www.lanevol.org/");
    	}
    });
		panel_S.add(label_2);
	}	
	
	private JPanel getPanelOptimalIndividual() {
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		panel.setLayout(gbl_panel);

		JLabel lblOptimalIndividual = new JLabel(LBL_OPTIMAL_IND);		
		GridBagConstraints gbc_lblOptimalIndividual = new GridBagConstraints();
		gbc_lblOptimalIndividual.anchor = GridBagConstraints.WEST;
		gbc_lblOptimalIndividual.insets = new Insets(5, 5, 5, 5);
		gbc_lblOptimalIndividual.gridx = 0;
		gbc_lblOptimalIndividual.gridy = 0;
		panel.add(lblOptimalIndividual, gbc_lblOptimalIndividual);

		txtGoal = new JTextField(TXTFIELD_GOAL);
		txtGoal.setPreferredSize(new Dimension(450, 30));
		GridBagConstraints gbc_txtGoal = new GridBagConstraints();
		gbc_txtGoal.insets = new Insets(5, 5, 5, 5);
		gbc_txtGoal.weightx = 1.0;
		gbc_txtGoal.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtGoal.anchor = GridBagConstraints.NORTHWEST;
		gbc_txtGoal.gridx = 1;
		gbc_txtGoal.gridy = 0;
		panel.add(txtGoal, gbc_txtGoal);

		return panel;
	}
	
	private JPanel getPanelPopulation() {
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		panel.setLayout(gbl_panel_2);

		JLabel lblPopulationSize = new JLabel(LBL_POP_SIZE);
		GridBagConstraints gbc_lblPopulationSize = new GridBagConstraints();
		gbc_lblPopulationSize.anchor = GridBagConstraints.WEST;
		gbc_lblPopulationSize.insets = new Insets(5, 5, 0, 5);
		gbc_lblPopulationSize.gridx = 0;
		gbc_lblPopulationSize.gridy = 0;
		panel.add(lblPopulationSize, gbc_lblPopulationSize);

		spinnerPopSize = new JSpinner();
		spinnerPopSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				int val = Integer.parseInt(spinnerPopSize.getValue().toString());
				sliderNbrBirthsDeaths.setMaximum(val);
				sliderNbrBirthsDeaths.setValue((int)(val*0.5));				
			}
		});
		spinnerPopSize.setModel(new SpinnerNumberModel(DEFAULT_POP_SIZE, 10, 1000000000, 10));
		GridBagConstraints gbc_spinnerPopSize = new GridBagConstraints();
		gbc_spinnerPopSize.weightx = 1.0;
		gbc_spinnerPopSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinnerPopSize.insets = new Insets(5, 5, 5, 5);
		gbc_spinnerPopSize.gridx = 1;
		gbc_spinnerPopSize.gridy = 0;
		panel.add(spinnerPopSize, gbc_spinnerPopSize);

		JLabel lblNbrOfSteps = new JLabel(LBL_NBR_STEPS);
		GridBagConstraints gbc_lblNbrOfSteps = new GridBagConstraints();
		gbc_lblNbrOfSteps.anchor = GridBagConstraints.WEST;
		gbc_lblNbrOfSteps.insets = new Insets(5, 5, 0, 5);
		gbc_lblNbrOfSteps.gridx = 2;
		gbc_lblNbrOfSteps.gridy = 0;
		panel.add(lblNbrOfSteps, gbc_lblNbrOfSteps);

		textFieldNbrStepsBefPausing = new JTextField(""+DEFAULT_NBR_STEP_PAUSE);
		GridBagConstraints gbc_textFieldNbrStepsBefPausing = new GridBagConstraints();
		gbc_textFieldNbrStepsBefPausing.weightx = 1.0;
		gbc_textFieldNbrStepsBefPausing.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldNbrStepsBefPausing.insets = new Insets(5, 5, 0, 0);
		gbc_textFieldNbrStepsBefPausing.anchor = GridBagConstraints.NORTHWEST;
		gbc_textFieldNbrStepsBefPausing.gridx = 3;
		gbc_textFieldNbrStepsBefPausing.gridy = 0;
		panel.add(textFieldNbrStepsBefPausing, gbc_textFieldNbrStepsBefPausing);

		return panel;
	}
	
	private JPanel getPanelBirthsAndDeaths() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(TIT_BIRTH_DEATH));

		JPanel panel_12 = new JPanel();
		GridBagConstraints gbc_panel_12 = new GridBagConstraints();
		gbc_panel_12.weightx = 1.0;
		gbc_panel_12.gridwidth = 2;
		gbc_panel_12.fill = GridBagConstraints.BOTH;
		gbc_panel_12.gridx = 0;
		gbc_panel_12.gridy = 0;
		panel.add(panel_12, gbc_panel_12);
		GridBagLayout gbl_panel_12 = new GridBagLayout();
		panel_12.setLayout(gbl_panel_12);


		JLabel lblProportionOfPopulation_1 = new JLabel(LBL_PROP_POP);
		GridBagConstraints gbc_lblProportionOfPopulation_1 = new GridBagConstraints();
		gbc_lblProportionOfPopulation_1.anchor = GridBagConstraints.WEST;
		gbc_lblProportionOfPopulation_1.insets = new Insets(0, 5, 5, 5);
		gbc_lblProportionOfPopulation_1.gridx = 0;
		gbc_lblProportionOfPopulation_1.gridy = 0;
		panel_12.add(lblProportionOfPopulation_1, gbc_lblProportionOfPopulation_1);

		final JLabel lblRep = new JLabel(DEFAULT_NBR_BIRTHS_DEATHS + LBL_NBR_BIRTHS_DEATHS);
		GridBagConstraints gbc_lblRep = new GridBagConstraints();
		gbc_lblRep.anchor = GridBagConstraints.WEST;
		gbc_lblRep.insets = new Insets(0, 5, 5, 5);
		gbc_lblRep.gridx = 1;
		gbc_lblRep.gridy = 0;
		panel_12.add(lblRep, gbc_lblRep);
		lblRep.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRep.setPreferredSize(new Dimension(150, 14));

		sliderNbrBirthsDeaths = new JSlider();
		sliderNbrBirthsDeaths.setMinimum(1);
		sliderNbrBirthsDeaths.setMaximum(DEFAULT_POP_SIZE);
		sliderNbrBirthsDeaths.setPaintTicks(false);
		sliderNbrBirthsDeaths.setValue(DEFAULT_NBR_BIRTHS_DEATHS);
		sliderNbrBirthsDeaths.setSnapToTicks(false);
		sliderNbrBirthsDeaths.setMajorTickSpacing(10);		
		sliderNbrBirthsDeaths.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lblRep.setText(sliderNbrBirthsDeaths.getValue()+LBL_NBR_BIRTHS_DEATHS);
			}
		});
		GridBagConstraints gbc_sliderPropRep = new GridBagConstraints();
		gbc_sliderPropRep.weightx = 1.0;
		gbc_sliderPropRep.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderPropRep.insets = new Insets(0, 5, 5, 5);
		gbc_sliderPropRep.anchor = GridBagConstraints.NORTHWEST;
		gbc_sliderPropRep.gridx = 2;
		gbc_sliderPropRep.gridy = 0;
		panel_12.add(sliderNbrBirthsDeaths, gbc_sliderPropRep);
		
		chckbxAsexualReproduction = new JCheckBox(CHKBOX_ASEXUAL_REPRODUCTION);
		GridBagConstraints gbc_chckbxAsexualRep = new GridBagConstraints();
		gbc_chckbxAsexualRep.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxAsexualRep.insets = new Insets(5, 5, 5, 5);
		gbc_chckbxAsexualRep.gridx = 0;
		gbc_chckbxAsexualRep.gridy = 1;
		panel.add(chckbxAsexualReproduction, gbc_chckbxAsexualRep);
		chckbxAsexualReproduction.setSelected(DEFAULT_USE_ASEXUAL_REPRODUCTION);

		chckbxSexualReproduction = new JCheckBox(CHKBOX_SEXUAL_REPRODUCTION + DEFAULT_PERCENT_RECOMBINATION + "%");
		GridBagConstraints gbc_chckbxRecombination = new GridBagConstraints();
		gbc_chckbxRecombination.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxRecombination.insets = new Insets(5, 5, 5, 5);
		gbc_chckbxRecombination.gridx = 0;
		gbc_chckbxRecombination.gridy = 2;
		panel.add(chckbxSexualReproduction, gbc_chckbxRecombination);
		chckbxSexualReproduction.setSelected(DEFAULT_USE_SEXUAL_REPRODUCTION);
		
		ButtonGroup group = new ButtonGroup();
		group.add(chckbxAsexualReproduction);
		group.add(chckbxSexualReproduction);
		
		sliderPercentRecombination = new JSlider();
		sliderPercentRecombination.setValue(DEFAULT_PERCENT_RECOMBINATION);
		sliderPercentRecombination.setMajorTickSpacing(1);
		sliderPercentRecombination.setMinimum(1);
		sliderPercentRecombination.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				chckbxSexualReproduction.setText(CHKBOX_SEXUAL_REPRODUCTION + sliderPercentRecombination.getValue()+"%");
			}
		});
		GridBagConstraints gbc_sliderRecombinationProb = new GridBagConstraints();
		gbc_sliderRecombinationProb.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderRecombinationProb.weightx = 1.0;
		gbc_sliderRecombinationProb.insets = new Insets(0, 0, 5, 5);
		gbc_sliderRecombinationProb.gridx = 1;
		gbc_sliderRecombinationProb.gridy = 2;
		panel.add(sliderPercentRecombination, gbc_sliderRecombinationProb);

		JPanel panel_7 = new JPanel();
		GridBagConstraints gbc_panel_7 = new GridBagConstraints();
		gbc_panel_7.anchor = GridBagConstraints.WEST;
		gbc_panel_7.weightx = 1.0;
		gbc_panel_7.insets = new Insets(0, 0, 5, 0);
		gbc_panel_7.gridwidth = 2;
		gbc_panel_7.gridx = 0;
		gbc_panel_7.gridy = 1;
		panel.add(panel_7, gbc_panel_7);
		GridBagLayout gbl_panel_7 = new GridBagLayout();
		panel_7.setLayout(gbl_panel_7);
		
		return panel;
	}
	
	private JPanel getPanelMutations() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(TIT_MUTATIONS));

		chckbxBigMutations = new JCheckBox(CHKBOX_BIG_MUT); 
		chckbxBigMutations.setSelected(DEFAULT_USE_BIG_MUTATIONS);
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
		panel.add(chckbxBigMutations, gbc_chckbxBigMutations);
		labelBigMutPercent.setVisible(false);

		labelBigMutPercent.setHorizontalAlignment(SwingConstants.TRAILING);
		labelBigMutPercent.setPreferredSize(new Dimension(70, 14));

		GridBagConstraints gbc_labelBigMutPercent = new GridBagConstraints();
		gbc_labelBigMutPercent.anchor = GridBagConstraints.WEST;
		gbc_labelBigMutPercent.insets = new Insets(5, 5, 5, 5);
		gbc_labelBigMutPercent.gridx = 1;
		gbc_labelBigMutPercent.gridy = 0;
		panel.add(labelBigMutPercent, gbc_labelBigMutPercent);

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
		panel.add(sliderMutationRatio, gbc_sliderMutationRatio);

		chckbxSmallMutations = new JCheckBox(CHKBOX_SMALL_MUT);
		chckbxSmallMutations.setVisible(false);
		chckbxSmallMutations.setSelected(false);
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
		panel.add(labelSmallMutPercent, gbc_labelSmallMutPercent);

		GridBagConstraints gbc_chckbxSmallMutations = new GridBagConstraints();
		gbc_chckbxSmallMutations.insets = new Insets(5, 5, 5, 0);
		gbc_chckbxSmallMutations.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxSmallMutations.gridx = 4;
		gbc_chckbxSmallMutations.gridy = 0;
		panel.add(chckbxSmallMutations, gbc_chckbxSmallMutations);

		JPanel panel_6 = new JPanel();
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.weightx = 1.0;
		gbc_panel_6.gridwidth = 5;
		gbc_panel_6.insets = new Insets(0, 0, 0, 5);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 0;
		gbc_panel_6.gridy = 1;
		panel.add(panel_6, gbc_panel_6);
		GridBagLayout gbl_panel_6 = new GridBagLayout();
		panel_6.setLayout(gbl_panel_6);

		lblUseMutations = new JLabel(LBL_MUTATION_RATE);
		GridBagConstraints gbc_lblProportionOfPopulation = new GridBagConstraints();
		gbc_lblProportionOfPopulation.anchor = GridBagConstraints.WEST;
		gbc_lblProportionOfPopulation.insets = new Insets(0, 5, 5, 5);
		gbc_lblProportionOfPopulation.gridx = 0;
		gbc_lblProportionOfPopulation.gridy = 0;
		panel_6.add(lblUseMutations, gbc_lblProportionOfPopulation);

		lblRateMutation = new JLabel((DEFAULT_RATE_MUTATION/100.0) + "%");
		lblRateMutation.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRateMutation.setPreferredSize(new Dimension(130, 14));
		GridBagConstraints gbc_lblMutations = new GridBagConstraints();
		gbc_lblMutations.anchor = GridBagConstraints.WEST;
		gbc_lblMutations.insets = new Insets(0, 5, 5, 5);
		gbc_lblMutations.gridx = 1;
		gbc_lblMutations.gridy = 0;
		panel_6.add(lblRateMutation, gbc_lblMutations);

		sliderRateMutation = new JSlider();
		sliderRateMutation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				lblRateMutation.setText((sliderRateMutation.getValue()/100.0)+"%");
			}
		});
		sliderRateMutation.setMinimum(1);
		sliderRateMutation.setMaximum(1000);
		//sliderPropMut.setPaintTicks(false);
		sliderRateMutation.setValue(DEFAULT_RATE_MUTATION);
		//sliderPropMut.setSnapToTicks(false);
		sliderRateMutation.setMajorTickSpacing(1);
		GridBagConstraints gbc_sliderPropMut = new GridBagConstraints();
		gbc_sliderPropMut.weightx = 1.0;
		gbc_sliderPropMut.fill = GridBagConstraints.HORIZONTAL;
		gbc_sliderPropMut.insets = new Insets(0, 5, 5, 5);
		gbc_sliderPropMut.anchor = GridBagConstraints.NORTHWEST;
		gbc_sliderPropMut.gridx = 2;
		gbc_sliderPropMut.gridy = 0;
		panel_6.add(sliderRateMutation, gbc_sliderPropMut);

		return panel;
	}
	
	private JPanel getPanelSelection() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(TIT_SELECTION));
		
		chckbxSelectionReproduction = new JCheckBox(CHKBOX_SELECTION_REPRODUCTION);
		GridBagConstraints gbc_chckbxSelectionReproduction = new GridBagConstraints();
		gbc_chckbxSelectionReproduction.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxSelectionReproduction.weightx = 1.0;
		gbc_chckbxSelectionReproduction.anchor = GridBagConstraints.WEST;
		gbc_chckbxSelectionReproduction.insets = new Insets(5, 5, 5, 5);
		gbc_chckbxSelectionReproduction.gridx = 0;
		gbc_chckbxSelectionReproduction.gridy = 0;
		panel.add(chckbxSelectionReproduction, gbc_chckbxSelectionReproduction);
		chckbxSelectionReproduction.setSelected(DEFAULT_SELECTION_FOR_REPRODUCTION);

		chckbxSelectionSurvival = new JCheckBox(CHKBOX_SELECTION_SURVIVAL);
		GridBagConstraints gbc_chckbxSelectionSurvival = new GridBagConstraints();
		gbc_chckbxSelectionSurvival.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxSelectionSurvival.weightx = 1.0;
		gbc_chckbxSelectionSurvival.insets = new Insets(5, 5, 5, 5);
		gbc_chckbxSelectionSurvival.anchor = GridBagConstraints.WEST;
		gbc_chckbxSelectionSurvival.gridx = 0;
		gbc_chckbxSelectionSurvival.gridy = 1;
		panel.add(chckbxSelectionSurvival, gbc_chckbxSelectionSurvival);
		chckbxSelectionSurvival.setSelected(DEFAULT_SELECTION_FOR_SURVIVAL);

		return panel;
	}
	
	private JPanel getPanelSimulationControls() {
		JPanel panel = new JPanel();

		GridBagLayout gbl_panel_3 = new GridBagLayout();
		panel.setLayout(gbl_panel_3);

		JPanel panel_8 = new JPanel();
		GridBagConstraints gbc_panel_8 = new GridBagConstraints();
		gbc_panel_8.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_8.gridwidth = 2;
		gbc_panel_8.gridx = 0;
		gbc_panel_8.gridy = 0;
		panel.add(panel_8, gbc_panel_8);
		GridBagLayout gbl_panel_8 = new GridBagLayout();
		panel_8.setLayout(gbl_panel_8);

		JLabel lblBestIndividual = new JLabel(LBL_BEST_IND);
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
		panel.add(panel_4, gbc_panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnStartPause = new JButton("");
		btnStartPause.setToolTipText(BUTTON_START);
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
		btnStartPause.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/player_play.png")));

		JButton buttonResetGeneration = new JButton("");
		buttonResetGeneration.setToolTipText(BUTTON_RESET);
		panel_4.add(buttonResetGeneration);
		buttonResetGeneration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable(){
					public void run(){
						resetSimulator();
					}
				}).start();
			}
		});
		buttonResetGeneration.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/quick_restart.png")));

		JButton buttonResetParameters = new JButton("");
		buttonResetParameters.setToolTipText(BUTTON_DEFAULT);
		panel_4.add(buttonResetParameters);
		buttonResetParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable(){
					public void run(){
						resetParameters();
					}
				}).start();
			}
		});
		buttonResetParameters.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/default_param.png")));
		
		JButton buttonHelp = new JButton("");
		buttonHelp.setToolTipText(BUTTON_HELP);
		panel_4.add(buttonHelp);
		buttonHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable(){
					public void run(){
						help();
					}
				}).start();
			}
		});
		buttonHelp.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/help.png")));
		
		JPanel panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(0, 0, 5, 0);
		gbc_panel_5.weightx = 1.0;
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.anchor = GridBagConstraints.WEST;
		gbc_panel_5.gridx = 1;
		gbc_panel_5.gridy = 1;
		panel.add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[]{0, 0};
		gbl_panel_5.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel_5.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_5.rowWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel_5.setLayout(gbl_panel_5);

		JPanel panel_10 = new JPanel();
		GridBagConstraints gbc_panel_10 = new GridBagConstraints();
		gbc_panel_10.fill = GridBagConstraints.BOTH;
		gbc_panel_10.gridx = 0;
		gbc_panel_10.gridy = 2;
		panel_5.add(panel_10, gbc_panel_10);
		GridBagLayout gbl_panel_10 = new GridBagLayout();
		gbl_panel_10.columnWidths = new int[]{118, 150, 0};
		gbl_panel_10.rowHeights = new int[]{20, 20, 20};
		gbl_panel_10.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_10.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_10.setLayout(gbl_panel_10);

		JLabel lblStep = new JLabel(LBL_GENERATION);
		GridBagConstraints gbc_lblStep = new GridBagConstraints();
		gbc_lblStep.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblStep.insets = new Insets(0, 5, 0, 5);
		gbc_lblStep.gridx = 0;
		gbc_lblStep.gridy = 0;
		panel_10.add(lblStep, gbc_lblStep);

		labelStep = new JLabel("0");
		GridBagConstraints gbc_labelStep = new GridBagConstraints();
		gbc_labelStep.insets = new Insets(0, 5, 0, 5);
		gbc_labelStep.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelStep.weightx = 1.0;
		gbc_labelStep.anchor = GridBagConstraints.NORTHWEST;
		gbc_labelStep.gridx = 1;
		gbc_labelStep.gridy = 0;
		panel_10.add(labelStep, gbc_labelStep);

		lblBestScore = new JCheckBox(LBL_BEST_SCORE);
		lblBestScore.setSelected(true);
		lblBestScore.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				graph.repaint();
			}
		});
		GridBagConstraints gbc_lblBestScore = new GridBagConstraints();
		gbc_lblBestScore.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblBestScore.insets = new Insets(0, 5, 0, 5);
		gbc_lblBestScore.gridx = 0;
		gbc_lblBestScore.gridy = 1;
		panel_10.add(lblBestScore, gbc_lblBestScore);
		
		labelBestScore = new JLabel("0");
		GridBagConstraints gbc_labelBestScore = new GridBagConstraints();
		gbc_labelBestScore.weightx = 1.0;
		gbc_labelBestScore.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelBestScore.insets = new Insets(0, 5, 0, 5);
		gbc_labelBestScore.anchor = GridBagConstraints.NORTHWEST;
		gbc_labelBestScore.gridx = 1;
		gbc_labelBestScore.gridy = 1;
		panel_10.add(labelBestScore, gbc_labelBestScore);
		
		lblMeanScore = new JCheckBox(LBL_MEAN_SCORE);
		lblMeanScore.setSelected(true);
		lblMeanScore.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				graph.repaint();
			}
		});
		GridBagConstraints gbc_lblMeanScore = new GridBagConstraints();
		gbc_lblMeanScore.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblMeanScore.insets = new Insets(0, 5, 0, 5);
		gbc_lblMeanScore.gridx = 0;
		gbc_lblMeanScore.gridy = 2;
		panel_10.add(lblMeanScore, gbc_lblMeanScore);

		labelMeanScore = new JLabel("0");
		GridBagConstraints gbc_labelMeanScore = new GridBagConstraints();
		gbc_labelMeanScore.weightx = 1.0;
		gbc_labelMeanScore.fill = GridBagConstraints.HORIZONTAL;
		gbc_labelMeanScore.insets = new Insets(0, 5, 0, 5);
		gbc_labelMeanScore.anchor = GridBagConstraints.NORTHWEST;
		gbc_labelMeanScore.gridx = 1;
		gbc_labelMeanScore.gridy = 2;
		panel_10.add(labelMeanScore, gbc_labelMeanScore);

		return panel;
	}
	
	private void mutationsCheckBoxChange(){
		lblUseMutations.setEnabled(chckbxBigMutations.isSelected());
		lblRateMutation.setEnabled(chckbxBigMutations.isSelected());
		sliderRateMutation.setEnabled(chckbxBigMutations.isSelected());
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

	private boolean getMutations() {
		return chckbxBigMutations.isSelected() || chckbxSmallMutations.isSelected();
	}
	
	private double getMutationRate() {
		return sliderRateMutation.getValue() / 10000.0;
	}
	
	private boolean getRecombination() { 
		return chckbxSexualReproduction.isSelected();
	}
	
	private double getProbaRecombination() {
		return sliderPercentRecombination.getValue() / 100.0;
	}

	private boolean getSelectionReproduction() {
		return chckbxSelectionReproduction.isSelected();
	}
	
	private boolean getSelectionSurvival() {
		return chckbxSelectionSurvival.isSelected();
	}
	
	private int getNbrReproduction() {
		return sliderNbrBirthsDeaths.getValue();
	}
	
	private void startSimulator() throws Exception {
		btnStartPause.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/player_pause.png")));
		running = true;
		done = false;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				spinnerPopSize.setEnabled(false);
				textFieldNbrStepsBefPausing.setEnabled(false);
			}
		});
		goal = txtGoal.getText().toCharArray();
		for (int i=0 ; i < goal.length ; i++){
			if (!charPos.containsKey(goal[i])){
				throw new Exception("The character '" + goal[i] + "' is not permitted.");
			}
		}
		fineScore = chckbxSmallMutations.isSelected();
		
		int popSize = Integer.parseInt(spinnerPopSize.getValue().toString());
		int indLength = goal.length;
		;
		
		int maxSteps = Integer.parseInt(textFieldNbrStepsBefPausing.getText());
		if (maxSteps <= 0) maxSteps = Integer.MAX_VALUE;
		if (population == null || population.length != popSize){
			initPopulation(popSize, indLength);
			generation = 0;
			labelStep.setText(""+0);
			progressBarBestIndividual.setValue(0);
			progressBarBestIndividual.setString("");
			progressBarBestIndividual.setStringPainted(true);
			labelBestScore.setText("0");
			labelMeanScore.setText("0");
			graph.reset();
		}
		for (int step = 0 ; step < maxSteps && running && scores[getBestIndividual()] < 1.0 ; step++){
			generation++;
			boolean[] offsprings = new boolean[popSize];
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					labelStep.setText(""+generation);
				}
			});
			for (int i=0 ; i < getNbrReproduction() ; i++){
				int mate1;
				do {
					mate1 = (getSelectionReproduction()) ? select(true) : randInt(popSize);
				}while(offsprings[mate1]);
				int victim = (getSelectionSurvival()) ? select(false) : randInt(popSize);
				if (getRecombination()){
					int mate2;
					do{
						mate2 = (getSelectionReproduction()) ? select(true) : randInt(popSize);
					}while(offsprings[mate2] && mate2 == mate1);
					if (getSelectionReproduction()){
						if (scores[mate1] < scores[mate2]){
							int temp = mate1;
							mate1 = mate2;
							mate2 = temp;
						}
					}
					population[victim] = recombine(population[mate1], population[mate2], getProbaRecombination(), getMutations(), getMutationRate());
				}else{
					population[victim] = duplicate(population[mate1], getMutations(), getMutationRate());
				}
				offsprings[victim] = true;
				scores[victim] = score(population[victim]);
			}	
			scoreSum = 0;
			for (int i=0 ; i < popSize ; i++){
				double score = scores[i];
				scoreSum += score;
			}			
			final double meanScorePop = scoreSum/(double)popSize;
			double forsd = 0;
			for (int i=0 ; i < popSize ; i++){
				double score = scores[i];
				forsd += Math.pow((score-meanScorePop),2);
			}	
			final double sd = Math.sqrt(forsd / (double)popSize);
			final int bestInd = getBestIndividual();
			final double bestScore = scores[bestInd]; 
			final String bestSentence = new String(population[bestInd]);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					progressBarBestIndividual.setValue((int)(100*bestScore));
					progressBarBestIndividual.setString(bestSentence);
					progressBarBestIndividual.setStringPainted(true);
					labelBestScore.setText(doubleToPercent(bestScore,0));
					labelMeanScore.setText(doubleToPercent(meanScorePop,0) + " (±"+doubleToPercent(sd,0)+")");
					graph.addBest(bestScore);
					graph.addMean(meanScorePop);
					graph.addSD(sd);
				}
			});
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				spinnerPopSize.setEnabled(true);
				textFieldNbrStepsBefPausing.setEnabled(true);
			}
		});
		done = true;
		pauseSimulator();
	}

	private void pauseSimulator(){
		btnStartPause.setIcon(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/player_play.png")));
		running = false;
	}

	private void resetParameters() {
		spinnerPopSize.setValue(DEFAULT_POP_SIZE);
		textFieldNbrStepsBefPausing.setText(""+DEFAULT_NBR_STEP_PAUSE);
		chckbxBigMutations.setSelected(DEFAULT_USE_BIG_MUTATIONS);
		sliderRateMutation.setValue(DEFAULT_RATE_MUTATION);
		chckbxAsexualReproduction.setSelected(DEFAULT_USE_ASEXUAL_REPRODUCTION);
		chckbxSexualReproduction.setSelected(DEFAULT_USE_SEXUAL_REPRODUCTION);
		sliderPercentRecombination.setValue(DEFAULT_PERCENT_RECOMBINATION);
		chckbxSelectionReproduction.setSelected(DEFAULT_SELECTION_FOR_REPRODUCTION);
		chckbxSelectionSurvival.setSelected(DEFAULT_SELECTION_FOR_SURVIVAL);
		sliderNbrBirthsDeaths.setValue(DEFAULT_NBR_BIRTHS_DEATHS);
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
				labelBestScore.setText("0");
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

	private int getBestIndividual() {
		int bestSol = 0;
		for (int ind=1 ; ind < population.length ; ind++) {
			if (scores[ind] > scores[bestSol]){
				bestSol = ind;
			}
		}
		return bestSol;
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

	/* Used when (1) mutations where done before reproduction and (2) with a "small mutations" option
	  
	private double getRatioBigMut() {
		return sliderMutationRatio.getValue() / 100.0;
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
	*/
	
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

	private char[] duplicate(char[] parent, boolean mutation, double probaMutation) {
		char[] offspring = new char[parent.length];
		for (int i=0 ; i < offspring.length ; i++){
			if (mutation && Math.random() < probaMutation) {
				offspring[i] = getRandomChar();
			}else {
				offspring[i] = parent[i];
			}
		}
		return offspring;
	}
	
	private char[] recombine(char[] mate1, char[] mate2, double probaRecombination, boolean mutation, double probaMutation){
		char[] offspring = new char[mate1.length];
		boolean parent = true;
		for (int i=0 ; i < offspring.length ; i++){
			if (mutation && Math.random() < probaMutation) {
				offspring[i] = getRandomChar();
			}else {
				offspring[i] = (parent) ? mate1[i] : mate2[i];
			}
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
		private final String AXIS_Y_BLACK_1;
		private final String AXIS_Y_BLACK_2;
		private final String AXIS_Y_BLACK_3;
		private final String AXIS_Y_RED;
		private final String AXIS_Y_BLUE;
		private final String AXIS_Y_GREEN;
		private List<Double> pointsBest = new ArrayList<Double>();
		private List<Double> pointsMean = new ArrayList<Double>();
		private List<Double> pointsSD = new ArrayList<Double>();

		public GraphPanel(Language l){
			super();
			switch (l) {
			case fr:
				AXIS_X = "Générations";
				AXIS_Y_BLACK_1 = "Score ";
				AXIS_Y_RED = "du plus apte";
				AXIS_Y_BLACK_2 = "Score ";
				AXIS_Y_BLUE = "moyen";
				AXIS_Y_BLACK_3 = " de la population";
				AXIS_Y_GREEN = " ± écart-type";
				break;
			case en:
			default:
				AXIS_X = "Generations";
				AXIS_Y_BLACK_1 = "Score ";
				AXIS_Y_RED = "of fittest";
				AXIS_Y_BLACK_2 = "Population ";
				AXIS_Y_BLUE = "mean";
				AXIS_Y_BLACK_3 = " score";
				AXIS_Y_GREEN = " ± SD";
				break;
			}
		}
		
		public void addBest(double point){
			pointsBest.add(point);
			repaint();
		}

		public void addMean(double point){
			pointsMean.add(point);
			repaint();
		}
		
		public void addSD(double point){
			pointsSD.add(point);
			repaint();
		}
		
		public void reset(){
			pointsBest.clear();
			pointsMean.clear();
			pointsSD.clear();
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
			while (pointsBest.size() / step > W){
				step++;
			}
			int x = offset;
			Graphics2D g2 = (Graphics2D)g;
			if (lblBestScore.isSelected()) {
				g.setColor(Color.BLACK);
				g.drawLine(x, offset, x, offset+H);
				/* Y axis is colored following the score, but Michel doesn't like it :-)
				if (!pointsBest.isEmpty()) {
					g.setColor(Color.RED);
					double fill = (double)H * pointsBest.get(pointsBest.size()-1);
					g.drawLine(x, offset+H-(int)fill, x, offset+H);
					g.drawLine(x+1, offset+H-(int)fill, x+1, offset+H);
				}
				*/
				g2.rotate(-Math.toRadians(90), x-2, offset+H);
				g.drawString(AXIS_Y_BLACK_1, x-2, offset+H);
				g.setColor(Color.RED);
				int colorOffset = (int)g.getFontMetrics().getStringBounds(AXIS_Y_BLACK_1,g).getWidth();
				g.drawString(AXIS_Y_RED, x-2+colorOffset, offset+H);
				g2.rotate(Math.toRadians(90), x-2, offset+H);
			}
			if (lblMeanScore.isSelected()) {
				for (int i=0 ; i < pointsSD.size()-step ; i+=step){
					int y =  offset+H-(int)((pointsMean.get(i)+pointsSD.get(i))*H);
					int nextY = offset+H-(int)((pointsMean.get(i+step)+pointsSD.get(i+step))*H);
					int y2 =  offset+H-(int)((pointsMean.get(i)-pointsSD.get(i))*H);
					int nextY2 = offset+H-(int)((pointsMean.get(i+step)-pointsSD.get(i+step))*H);
					g.setColor(new Color(0f,1.0f,0f,0.5f));
					g.drawLine(x, y, x+1, y2);
					g.setColor(Color.GREEN);
					g.drawLine(x, y, x+1, nextY);
					g.drawLine(x, y2, x+1, nextY2);
					x++;
				}
				x = offset;
				g.setColor(Color.BLUE);
				for (int i=0 ; i < pointsMean.size()-step ; i+=step){
					int y =  offset+H-(int)(pointsMean.get(i)*H);
					int nextY = offset+H-(int)(pointsMean.get(i+step)*H);
					g.drawLine(x, y, x+1, nextY);
					x++;
				}
			}
			x = offset;
			if (lblBestScore.isSelected()) {
				g.setColor(Color.RED);
				for (int i=0 ; i < pointsBest.size()-step ; i+=step){
					int y =  offset+H-(int)(pointsBest.get(i)*H);
					int nextY = offset+H-(int)(pointsBest.get(i+step)*H);
					g.drawLine(x, y, x+1, nextY);
					x++;
				}
			}
			if (lblMeanScore.isSelected()) {
				g.setColor(Color.BLACK);
				g.drawLine(offset+W, offset, offset+W, offset+H);
				g2.rotate(-Math.toRadians(90), offset+W+10, offset+H);
				g.setColor(Color.BLACK);
				g.drawString(AXIS_Y_BLACK_2, offset+W+10, offset+H);
				int colorOffset = (int)g.getFontMetrics().getStringBounds(AXIS_Y_BLACK_2,g).getWidth();
				g.setColor(Color.BLUE);
				g.drawString(AXIS_Y_BLUE, offset+W+10+colorOffset, offset+H);
				colorOffset += (int)g.getFontMetrics().getStringBounds(AXIS_Y_BLUE,g).getWidth();
				g.setColor(Color.BLACK);
				g.drawString(AXIS_Y_BLACK_3, offset+W+10+colorOffset, offset+H);
				colorOffset += (int)g.getFontMetrics().getStringBounds(AXIS_Y_BLACK_3,g).getWidth();
				g.setColor(Color.GREEN);
				g.drawString(AXIS_Y_GREEN,  offset+W+10+colorOffset, offset+H);
				g2.rotate(Math.toRadians(90), offset+W+10, offset+H);
				/* Y axis is colored following the score, but Michel doesn't like it :-)
				if (!pointsMean.isEmpty()) {
					g.setColor(Color.BLUE);
					double fill = (double)H * pointsMean.get(pointsMean.size()-1);
					g.drawLine(offset+W, offset+H-(int)fill, offset+W, offset+H);
					g.drawLine(offset+W-1, offset+H-(int)fill, offset+W-1, offset+H);
				}
				*/
			}
			g.setColor(Color.BLACK);
			g.drawLine(offset, offset+H, offset+W, offset+H);
			g.drawString(AXIS_X, offset+4, offset+H+12);
		}
	}
	
	public static void openURL(String url){
		try{
			openURL(new URI(url));
		}catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot open web browser" + ":\n" + ex.getLocalizedMessage(), "Opening web browser",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void openURL(String url, String param){
		try{
			String encodedParam = URLEncoder.encode(param,"UTF-8");
			openURL(new URI(url+encodedParam));
		}catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot open web browser" + ":\n" + ex.getLocalizedMessage(), "Opening web browser",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void openURL(URI url) {
		try {
			java.awt.Desktop.getDesktop().browse(url);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot open web browser" + ":\n" + ex.getLocalizedMessage(), "Opening web browser",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void help() {
		JFrame dlg = new JFrame();
		dlg.setTitle(BUTTON_HELP + " EvoSimulator");
		dlg.setIconImage(new ImageIcon(EvoSimulatorApp.class.getResource("/evosimulator/resources/help.png")).getImage());
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);	
		JTextPane helpTxt = new JTextPane();
		helpTxt.setEditorKit(new HTMLEditorKit());
		helpTxt.setFont(new java.awt.Font("Geneva", 0, 12));
		helpTxt.setOpaque(true);
		helpTxt.setCaretPosition(0);
		helpTxt.setEditable(false);
		String filename = "README_" + language + ".html";
		File file = new File(filename);
		try {
		try (FileReader fr = new FileReader(file)){
			try (BufferedReader br = new BufferedReader(fr)){
				helpTxt.read(br, null);				
			}
		}
		}catch (IOException ioex) {
			helpTxt.setText("Help file " + filename + " not found");
		}
		scrollPane.setViewportView(helpTxt);	
		dlg.getContentPane().add(scrollPane, BorderLayout.CENTER);
		dlg.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dlg.getSize();
		frameSize.height = screenSize.height * 5 / 6;
		frameSize.width = screenSize.width * 2 / 3;
		dlg.setSize(frameSize);
		dlg.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		//dlg.setExtendedState(EvoSimulatorApp.MAXIMIZED_BOTH);
		dlg.setVisible(true);
	}
	
  public static void exit(int exitCode) {
    System.exit(exitCode);
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      exit(0);
    }
  }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {			
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
					im.put( KeyStroke.getKeyStroke( "ENTER" ), "pressed" );
					im.put( KeyStroke.getKeyStroke( "released ENTER" ), "released" );
					//Set Cut/Copy/Paste/SelectAll shortcuts with Command on Mac instead of Control
					if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0){
						for (String map : new String[]{"EditorPane.focusInputMap","FormattedTextField.focusInputMap","PasswordField.focusInputMap","TextArea.focusInputMap","TextField.focusInputMap","TextPane.focusInputMap"}){
							im = (InputMap) UIManager.get(map);
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
						}
						//IMPORTANT: JTable.processKeyBinding has a bug (it doesn't check if the meta key is pressed before triggering the cell editor)
						//Don't forget to override it in each JTable to allow the meta+V to work correctly on MacOSX 
						for (String map : new String[]{"List.focusInputMap","Table.ancestorInputMap","Tree.focusInputMap"}){
							im = (InputMap) UIManager.get(map);
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), "copy");
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), "paste");
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), "cut");
							im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), "selectAll");
						}			
					}	
					break;
				}
			}
		} catch (Exception ex) {
			try{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}catch (Exception ex1) {
				ex1.printStackTrace();
			}
		}		
		final EvoSimulatorApp simulator = new EvoSimulatorApp(Language.en);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				simulator.validate();
				//Center the window
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Dimension frameSize = simulator.getSize();
				if (frameSize.height > screenSize.height) {
					frameSize.height = screenSize.height;
				}else{
					frameSize.height = screenSize.height * 5 / 6;
				}
				if (frameSize.width > screenSize.width) {
					frameSize.width = screenSize.width;
				}
				simulator.setSize(frameSize);
				simulator.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
				simulator.setVisible(true);
			}
		});
	}

}
