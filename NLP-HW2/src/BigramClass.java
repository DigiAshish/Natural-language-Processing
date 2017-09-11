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

	enum operation {
		NO_SMOOTHING, ADD_ONE_SMOOTHING
	};

	public static void main(String[] args) throws FileNotFoundException {
		
		try {
			reader = new BufferedReader(new FileReader(
					"corpus.txt"));
		} catch (Exception e) {
			System.out.println("File not found");
		}

		get_tokens();
		FreqCalculator();
		calcSentence();
	}

	
	static void get_tokens() {
		try {
			bigrams_Map = new HashMap<String, Integer>();
			unigrams_Map = new HashMap<String, Integer>();
			String singleLine = reader.readLine();

			while (singleLine != null) {
				allLines = allLines + " " + singleLine;
				singleLine = reader.readLine();
			}
			allLines = allLines.replaceAll("[^a-zA-Z0-9. ]", "");
			allLines = allLines.replaceAll("\\s+", " ").toLowerCase();

			StringTokenizer tokenizer = new StringTokenizer(allLines);

			if (tokenizer.hasMoreTokens()) {
				token1 = tokenizer.nextToken();
			}

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

			System.out.println("Error parsing file");
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
	
	
	static void calcSentence() {
		String sentence1 = ". Paul Allen and Bill Gates are the founders of Microsoft software company ."
				.toLowerCase();
		String sentence2 = ". Windows Phone Microsoft Office and Microsoft Surface are the products of the\n" + 
				"company ."
				.toLowerCase();

		compareSentence(sentence1, sentence2, operation.NO_SMOOTHING);
		compareSentence(sentence1, sentence2, operation.ADD_ONE_SMOOTHING);

	
		noSmoothingFreq(sentence1);
		noSmoothingFreq(sentence2);
		
		probability(sentence1);
		probability(sentence2);
	
		addOneSmoothingFreq(sentence1);
		addOneSmoothingFreq(sentence2);		

	}
	
	static void compareSentence(String sentence1, String sentence2,
			BigramClass.operation operation) {
		System.out.println(operation);
		double comp1 = doSmoothing(sentence1, operation.ordinal());
		double comp2 = doSmoothing(sentence2, operation.ordinal());
		String output = comp1 > comp2 ? "Sentence 1 is preferred"
				: "Sentence 2 is preferred";
		System.out.println(output);
		System.out.println(" ");
	}
	
	
	static double doSmoothing(String sentence, int smoothingType) {
		String tempToken1 = "", tempToken2 = "";
		int unigramCount, bigramCount;

		double sentenceProb = 1, conditionalProb;
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

			if (smoothingType == 0) {
				if (isBigramPresent) {
					conditionalProb = (double) (bigramCount)
							/ (double) (unigramCount);
					sentenceProb *= conditionalProb;
				}
			}

			else if (smoothingType == 1) {
				conditionalProb = (double) (bigramCount + 1)
						/ (double) (unigramCount + unigrams_Map.size());
				sentenceProb *= conditionalProb;
			} else if (smoothingType == 2) {
				if (isBigramPresent) {
					double c = 0, cStar, n = 0;
					if (bigrams_Map.containsKey(bigramToken)) {
						c = (double) bigrams_Map.get(bigramToken);
						if (FreqCalculatorMap.containsKey((int) (c + 1))) {
							n = FreqCalculatorMap.get((int) (c + 1));
							cStar = ((c + 1) * (double) n)
									/ (double) FreqCalculatorMap.get((int) c);
							conditionalProb = cStar / (double) totalWords;
							sentenceProb = sentenceProb * conditionalProb;
						}
					}
				} else
					sentenceProb *= (double) FreqCalculatorMap.get(1)
							/ (double) totalWords;

			}
			tempToken1 = tempToken2;
		}
		System.out.println("SP >> " + sentenceProb);
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

	static void probability(String sentence) {
		System.out.println("************************ Probability ***************************** ");
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