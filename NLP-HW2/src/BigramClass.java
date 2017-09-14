import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class BigramClass {
	static BufferedReader reader;
	static Map<String, Integer> bigrams_Map;
	static Map<String, Integer> unigrams_Map;
	static Map<Integer, Integer> FreqCalculatorMap;
	static String allLines = "";
	static String token1;
	static String token2;
	static int totalWords;

	public static void main(String[] args) throws FileNotFoundException {
		try {
			String fileName = "corpus.txt";
			if(args.length > 0 )
			{
				fileName = args[0].toString();	
			}
			reader = new BufferedReader(new FileReader(fileName));
			String singleLine = reader.readLine();

			while (singleLine != null) {
				allLines = allLines + " " + singleLine;
				singleLine = reader.readLine();
			}
			allLines = allLines.replaceAll("[^a-zA-Z0-9. ]", "");
			allLines = allLines.replaceAll("\\s+", " ").toLowerCase();
			tokenize(allLines);
			
		} catch (Exception e) {
			System.out.println("File not found or Error parsing the file");
		}
		
		String sentence1 = "Paul Allen and Bill Gates are the founders of Microsoft software company"
				.toLowerCase();
		String sentence2 = "Windows Phone Microsoft Office and Microsoft Surface are the products of the company"
				.toLowerCase();	
		if(args.length > 0 )
		{
			sentence1 = args[1].toString().toLowerCase();
			sentence2 = args[2].toString().toLowerCase();
		}
		
		FreqCalculator();
		calcSentence(sentence1,sentence2);
	}

	
	static void tokenize(String allLines) {
		try {
			bigrams_Map = new HashMap<String, Integer>();
			unigrams_Map = new HashMap<String, Integer>();
			
			StringTokenizer tokenizer = new StringTokenizer(allLines);
			token1 = tokenizer.nextToken();

			while (tokenizer.hasMoreTokens()) {
				String token2 = tokenizer.nextToken();
				String word = token1 + " " + token2;

				if (bigrams_Map.containsKey(word)) {
					bigrams_Map.put(word, bigrams_Map.get(word) + 1);
				} else {
					bigrams_Map.put(word, 1);
				}
				totalWords++;
				if (unigrams_Map.containsKey(token1)) {
					unigrams_Map.put(token1, unigrams_Map.get(token1) + 1);
				} else {
					unigrams_Map.put(token1, 1);
				}
				token1 = token2;
			}

		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("Error parsing the file");
		}
	}
	
	
	static void FreqCalculator() {
		FreqCalculatorMap = new HashMap<Integer, Integer>();
		for (String key : bigrams_Map.keySet()) {
			int val = bigrams_Map.get(key);
			if (FreqCalculatorMap.containsKey(val)) {
				int count = FreqCalculatorMap.get(val);
				FreqCalculatorMap.put(val, count + 1);
			} else {
				FreqCalculatorMap.put(val, 1);
			}
		}
	}
	
	
	static void calcSentence(String sentence1,String sentence2) {

		compareSentence(sentence1, sentence2, "NO_SMOOTHING");
		compareSentence(sentence1, sentence2, "ADD_ONE_SMOOTHING");

		noSmoothingFreq(sentence1);
		noSmoothingFreq(sentence2);
		
		noSmoothingprobability(sentence1);
		noSmoothingprobability(sentence2);
		
		addOneSmoothingFreq(sentence1);
		addOneSmoothingFreq(sentence2);
	
		addOneSmoothingProb(sentence1);
		addOneSmoothingProb(sentence2);		

	}
	
	static void compareSentence(String sentence1, String sentence2,
			String smoothingType) {
		System.out.println(smoothingType);
		double sent1Prob = doSmoothing(sentence1, 1,smoothingType);
		double sent2Prob = doSmoothing(sentence2, 2,smoothingType);
		System.out.println(sent1Prob > sent2Prob ? "Sentence 1 is preferred"
				: "Sentence 2 is preferred");
		System.out.println();
	}
	
	
	static double doSmoothing(String sentence, int whichSentence, String smoothingType) {
		String tempToken1 = "", tempToken2 = "";
		int unigramCount, bigramCount;

		double sentenceProb = 1.0, conditionalProb;
		StringTokenizer tempTokenize = new StringTokenizer(sentence);

		if (tempTokenize.hasMoreTokens()) {
			tempToken1 = tempTokenize.nextToken();
		}

		while (tempTokenize.hasMoreTokens()) {
			tempToken2 = tempTokenize.nextToken();
			String bigramToken = tempToken1 + " " + tempToken2;

			boolean isBigramPresent = bigrams_Map.containsKey(bigramToken);
			boolean isUnigramPresent = unigrams_Map.containsKey(tempToken1);

			bigramCount = (isBigramPresent) ? bigrams_Map.get(bigramToken) : 0;
			unigramCount = (isUnigramPresent) ? unigrams_Map.get(tempToken1) : 0;

			if (smoothingType == "NO_SMOOTHING") {
				if (isBigramPresent) {
					conditionalProb = (double) (bigramCount)
							/ (double) (unigramCount);
					sentenceProb *= conditionalProb;
				}
			}

			else if (smoothingType == "ADD_ONE_SMOOTHING") {
				conditionalProb = (double) (bigramCount + 1)
						/ (double) (unigramCount + unigrams_Map.size());
				sentenceProb *= conditionalProb;
			} 
			tempToken1 = tempToken2;
		}
		System.out.print("Sentence# " + whichSentence);
		System.out.println(" >> " + sentenceProb);
		return sentenceProb;
	}
	
	
	public static void noSmoothingFreq(String sentence) {
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tokenz = sentence.split(" ");
		System.out.println("************************ Frequency No Smoothing***************************** ");
		System.out.print(addSpace(" "));
		for (int cntr = 0; cntr < tokenz.length; cntr++) {
			System.out.print("\t" + tokenz[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tokenz.length; cntr++) {
			System.out.print(addSpace(tokenz[cntr]));
			for (int cntr1 = 0; cntr1 < tokenz.length; cntr1++) {
				String bigram = tokenz[cntr] + " " + tokenz[cntr1];
				if (bigrams_Map.containsKey(bigram)) {
					System.out.print("\t" + dF.format(bigrams_Map.get(bigram)));
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}

	public static void addOneSmoothingFreq(String sentence) {
		System.out.println("************************ Frequency Add One Smoothing***************************** ");
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tokens = sentence.split(" ");
		System.out.print(addSpace(" "));
		for (int cntr = 0; cntr < tokens.length; cntr++) {
			System.out.print("\t" + tokens[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tokens.length; cntr++) {
			System.out.print(addSpace(tokens[cntr]));
			for (int cntr1 = 0; cntr1 < tokens.length; cntr1++) {
				String bigram = tokens[cntr] + " " + tokens[cntr1];
				if (bigrams_Map.containsKey(bigram)) {
					System.out.print("\t" + dF.format(bigrams_Map.get(bigram)+1));

				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}
	
	static void noSmoothingprobability(String sentence) {
		System.out.println("************************ Probability for No Smoothing ***************************** ");
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tempToken = sentence.split(" ");
		System.out.print(addSpace(" "));
		for (int cntr = 0; cntr < tempToken.length; cntr++) {
			System.out.print("\t" + tempToken[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tempToken.length; cntr++) {
			System.out.print(addSpace(tempToken[cntr]));
			for (int cntr1 = 0; cntr1 < tempToken.length; cntr1++) {
				String bigram = tempToken[cntr] + " " + tempToken[cntr1];
				if (bigrams_Map.containsKey(bigram)) {
					System.out
							.print("\t"
									+ dF.format(((double) bigrams_Map.get(bigram) / (double) unigrams_Map
											.get(tempToken[cntr1]))));
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}
	
	
	public static void addOneSmoothingProb(String sentence) {
		System.out.println("************************ Probability for Add One Smoothing***************************** ");
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tokens = sentence.split(" ");
		System.out.print(addSpace(" "));
		for (int cntr = 0; cntr < tokens.length; cntr++) {
			System.out.print("\t" + tokens[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tokens.length; cntr++) {
			System.out.print(addSpace(tokens[cntr]));
			for (int cntr1 = 0; cntr1 < tokens.length; cntr1++) {
				String bigram = tokens[cntr] + " " + tokens[cntr1];
				if (bigrams_Map.containsKey(bigram)) {
					System.out
							.print("\t"
									+ dF.format(((double) (bigrams_Map
											.get(bigram) + 1) / ((double) unigrams_Map
											.get(tokens[cntr1]) + unigrams_Map
											.size()))));

				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}


	public static String addSpace(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append(s);
		for (int i = 1; i < 10 - s.length(); i++) {
			sb.append(" ");
		}
		return sb.toString();
	}


}