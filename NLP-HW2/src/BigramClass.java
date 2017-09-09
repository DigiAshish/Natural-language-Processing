import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class BigramClass {
	BufferedReader reader;
	Map<String, Integer> bigramMap;
	Map<String, Integer> unigramMap;
	Map<Integer, Integer> numFreqMap;
	String allLines = "";
	String token1, token2;
	int totalWords;

	enum operation {
		NO_SMOOTHING, ADD_ONE_SMOOTHING
	};

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		BigramClass obj = new BigramClass();
		obj.Execute();
	}

	/*
	 * EXECUTOR
	 */
	void Execute() {
		IsFilePresent();
		ReadFile();
		numFreq();
		calcSentence();

	}

	// Check file is present
	void IsFilePresent() {
		try {
			reader = new BufferedReader(new FileReader(
					"corpus.txt"));
		} catch (Exception e) {
			System.out.println("File not found");
		}
	}

	void ReadFile() {
		try {
			bigramMap = new HashMap<String, Integer>();
			unigramMap = new HashMap<String, Integer>();
			// join all lines
			String singleLine = reader.readLine();

			while (singleLine != null) {
				allLines = allLines + " " + singleLine;
				singleLine = reader.readLine();
			}
			allLines = allLines.replaceAll("[^a-zA-Z0-9. ]", "");
			allLines = allLines.replaceAll("\\s+", " ").toLowerCase();

			StringTokenizer tokenizer = new StringTokenizer(allLines);
			// System.out.println(tokenizer.countTokens());

			// loop through the words.
			if (tokenizer.hasMoreTokens()) {
				token1 = tokenizer.nextToken();
			}

			while (tokenizer.hasMoreTokens()) {
				String token2 = tokenizer.nextToken();
				String word = token1 + " " + token2;

				if (bigramMap.containsKey(word)) {
					bigramMap.put(word, bigramMap.get(word) + 1);
				} else {
					bigramMap.put(word, 1);
				}
				totalWords++;
				if (unigramMap.containsKey(token1)) {
					unigramMap.put(token1, unigramMap.get(token1) + 1);
				} else {
					unigramMap.put(token1, 1);
				}

				token1 = token2;
			}


		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("Error parsing file");
		}
	}

	double doSmoothing(String sentence, int smoothingType) {
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

			boolean isBigramPresent = bigramMap.containsKey(bigramToken);
			boolean isUnigramPresent = unigramMap.containsKey(tempToken1);

			bigramCount = (isBigramPresent) ? bigramMap.get(bigramToken) : 0;
			unigramCount = (isUnigramPresent) ? unigramMap.get(tempToken1) : 0;

			if (smoothingType == 0) {
				if (isBigramPresent) {
					conditionalProb = (double) (bigramCount)
							/ (double) (unigramCount);
					sentenceProb *= conditionalProb;
				}
			}

			else if (smoothingType == 1) {
				conditionalProb = (double) (bigramCount + 1)
						/ (double) (unigramCount + unigramMap.size());
				sentenceProb *= conditionalProb;
			} else if (smoothingType == 2) {
				if (isBigramPresent) {
					double c = 0, cStar, n = 0;
					if (bigramMap.containsKey(bigramToken)) {
						c = (double) bigramMap.get(bigramToken);
						if (numFreqMap.containsKey((int) (c + 1))) {
							n = numFreqMap.get((int) (c + 1));
							cStar = ((c + 1) * (double) n)
									/ (double) numFreqMap.get((int) c);
							conditionalProb = cStar / (double) totalWords;
							sentenceProb = sentenceProb * conditionalProb;
						}
					}
				} else
					sentenceProb *= (double) numFreqMap.get(1)
							/ (double) totalWords;

			}
			tempToken1 = tempToken2;
		}
		System.out.println("SP >> " + sentenceProb);
		return sentenceProb;
	}

	void compareSentence(String sentence1, String sentence2,
			BigramClass.operation operation) {
		System.out.println(operation);
		double comp1 = doSmoothing(sentence1, operation.ordinal());
		double comp2 = doSmoothing(sentence2, operation.ordinal());
		String output = comp1 > comp2 ? "Sentence 1 is preferred"
				: "Sentence 2 is preferred";
		System.out.println(output);
		System.out.println(" ");
	}

	void calcSentence() {
		String sentence1 = ". Paul Allen and Bill Gates are the founders of Microsoft software company ."
				.toLowerCase();
		String sentence2 = ". Windows Phone Microsoft Office and Microsoft Surface are the products of the\n" + 
				"company ."
				.toLowerCase();

		compareSentence(sentence1, sentence2, operation.NO_SMOOTHING);
		compareSentence(sentence1, sentence2, operation.ADD_ONE_SMOOTHING);

	
		displayFreqNoSmoothing(sentence1);
		displayFreqNoSmoothing(sentence2);
		
		displayProb(sentence1);
		displayProb(sentence2);
	
		displayFreqAddOneSmoothing(sentence1);
		displayFreqAddOneSmoothing(sentence2);		

	}

	public void displayFreqNoSmoothing(String sentence) {
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tokenz = sentence.split(" ");
		System.out.print(buildSpaceTab(" "));
		for (int cntr = 0; cntr < tokenz.length; cntr++) {
			System.out.print("\t" + tokenz[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tokenz.length; cntr++) {
			System.out.print(buildSpaceTab(tokenz[cntr]));
			for (int cntr1 = 0; cntr1 < tokenz.length; cntr1++) {
				String bigram = tokenz[cntr] + " " + tokenz[cntr1];
				if (bigramMap.containsKey(bigram)) {
					System.out.print("\t" + dF.format(bigramMap.get(bigram)));
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
		System.out.println("***********************");
	}

	public void displayFreqAddOneSmoothing(String sentence) {
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tokens = sentence.split(" ");
		System.out.print(buildSpaceTab(" "));
		for (int cntr = 0; cntr < tokens.length; cntr++) {
			System.out.print("\t" + tokens[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tokens.length; cntr++) {
			System.out.print(buildSpaceTab(tokens[cntr]));
			for (int cntr1 = 0; cntr1 < tokens.length; cntr1++) {
				String bigram = tokens[cntr] + " " + tokens[cntr1];
				if (bigramMap.containsKey(bigram)) {
					System.out
							.print("\t"
									+ dF.format(((double) (bigramMap
											.get(bigram) + 1) / ((double) unigramMap
											.get(tokens[cntr1]) + unigramMap
											.size()))));

				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
		System.out.println("***********************");
	}

	

	void displayProb(String sentence) {
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tempToken = sentence.split(" ");
		System.out.print(buildSpaceTab(" "));
		for (int cntr = 0; cntr < tempToken.length; cntr++) {
			System.out.print("\t" + tempToken[cntr]);
		}
		System.out.println("\n");
		for (int cntr = 0; cntr < tempToken.length; cntr++) {
			System.out.print(buildSpaceTab(tempToken[cntr]));
			for (int cntr1 = 0; cntr1 < tempToken.length; cntr1++) {
				String bigram = tempToken[cntr] + " " + tempToken[cntr1];
				if (bigramMap.containsKey(bigram)) {
					System.out
							.print("\t"
									+ dF.format(((double) bigramMap.get(bigram) / (double) unigramMap
											.get(tempToken[cntr1]))));
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
		System.out.println("***********************");
	}

	public static String buildSpaceTab(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append(s);
		for (int i = 1; i < 10 - s.length(); i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	void numFreq() {
		numFreqMap = new HashMap<Integer, Integer>();
		for (String key : bigramMap.keySet()) {
			int val = bigramMap.get(key);
			if (numFreqMap.containsKey(val)) {
				int count = numFreqMap.get(val);
				numFreqMap.put(val, count + 1);
			} else {
				numFreqMap.put(val, 1);
			}
		}
	}

}