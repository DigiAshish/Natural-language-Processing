import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class BigramClass {
	static BufferedReader reader;
	static Map<String, Integer> bigrams_Map;
	static Map<String, Integer> unigrams_Map;
	static Map<Integer, Integer> FreqCalcMap;
	static String lines = "";static int totalWords;
	static String word1;static String word2;
	

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
				lines = lines + " " + singleLine;
				singleLine = reader.readLine();
			}
			lines = lines.replaceAll("[^a-zA-Z0-9. ]", "");
			lines = lines.replaceAll("\\s+", " ").toLowerCase();
			tokenize(lines);
			
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
		
		FreqCalc();
		ProcessSentences(sentence1,sentence2);
	}

	
	static void tokenize(String lines) {
		try {
			bigrams_Map = new HashMap<String, Integer>();
			unigrams_Map = new HashMap<String, Integer>();
			
			StringTokenizer tokenizer = new StringTokenizer(lines);
			word1 = tokenizer.nextToken();

			while (tokenizer.hasMoreTokens()) {
				String word2 = tokenizer.nextToken();
				String word = word1 + " " + word2;

				if (bigrams_Map.containsKey(word)) {
					bigrams_Map.put(word, bigrams_Map.get(word) + 1);
				} else {
					bigrams_Map.put(word, 1);
				}
				totalWords++;
				if (unigrams_Map.containsKey(word1)) {
					unigrams_Map.put(word1, unigrams_Map.get(word1) + 1);
				} else {
					unigrams_Map.put(word1, 1);
				}
				word1 = word2;
			}

		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("Error parsing the file");
		}
	}
	
	
	static void FreqCalc() {
		FreqCalcMap = new HashMap<Integer, Integer>();
		for (String key : bigrams_Map.keySet()) {
			int val = bigrams_Map.get(key);
			if (FreqCalcMap.containsKey(val)) {
				int count = FreqCalcMap.get(val);
				FreqCalcMap.put(val, count + 1);
			} else {
				FreqCalcMap.put(val, 1);
			}
		}
	}
	
	
	static void ProcessSentences(String sentence1,String sentence2) {

		compareSentence(sentence1, sentence2, "NO_SMOOTHING");
		compareSentence(sentence1, sentence2, "ADD_ONE_SMOOTHING");

		noSmoothingFreq(sentence1, "Sentence-1");
		noSmoothingFreq(sentence2, "Sentence-2");
		
		noSmoothingprobability(sentence1, "Sentence-1");
		noSmoothingprobability(sentence2, "Sentence-2");
		
		addOneSmoothingFreq(sentence1, "Sentence-1");
		addOneSmoothingFreq(sentence2, "Sentence-2");
	
		addOneSmoothingProb(sentence1, "Sentence-1");
		addOneSmoothingProb(sentence2, "Sentence-2");		

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
		String tempword1 = "", tempword2 = "";
		int unigramCount, bigramCount;

		double sentenceProb = 1.0, conditionalProb;
		StringTokenizer tempTokenize = new StringTokenizer(sentence);

		if (tempTokenize.hasMoreTokens()) {
			tempword1 = tempTokenize.nextToken();
		}

		while (tempTokenize.hasMoreTokens()) {
			tempword2 = tempTokenize.nextToken();
			String bigramToken = tempword1 + " " + tempword2;

			boolean isBigramPresent = bigrams_Map.containsKey(bigramToken);
			boolean isUnigramPresent = unigrams_Map.containsKey(tempword1);

			bigramCount = (isBigramPresent) ? bigrams_Map.get(bigramToken) : 0;
			unigramCount = (isUnigramPresent) ? unigrams_Map.get(tempword1) : 0;

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
			tempword1 = tempword2;
		}
		System.out.print("Sentence# " + whichSentence);
		System.out.println(" >> " + sentenceProb);
		return sentenceProb;
	}
	
	
	public static void noSmoothingFreq(String sentence, String whichSentence) {
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tempToken = sentence.split(" ");
		System.out.format("********************  %s -> No Smoothing Frequency***************************** ",whichSentence);
		System.out.println();
		System.out.print(addSpace(" "));
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print("\t" + tempToken[iCounter]);
		}
		System.out.println("\n");
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print(addSpace(tempToken[iCounter]));
			for (int jCounter = 0; jCounter < tempToken.length; jCounter++) {
				String bigram = tempToken[iCounter] + " " + tempToken[jCounter];
				if (bigrams_Map.containsKey(bigram)) {
					System.out.print("\t" + dF.format(bigrams_Map.get(bigram)));
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}

	public static void addOneSmoothingFreq(String sentence, String whichSentence) {
		
		System.out.format("********************  %s -> Add One Smoothing Frequency***************************** ",whichSentence);
		System.out.println();
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tempToken = sentence.split(" ");
		System.out.print(addSpace(" "));
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print("\t" + tempToken[iCounter]);
		}
		System.out.println("\n");
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print(addSpace(tempToken[iCounter]));
			for (int jCounter = 0; jCounter < tempToken.length; jCounter++) {
				String bigram = tempToken[iCounter] + " " + tempToken[jCounter];
				if (bigrams_Map.containsKey(bigram)) {
					System.out.print("\t" + dF.format(bigrams_Map.get(bigram)+1));

				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}
	
	static void noSmoothingprobability(String sentence, String whichSentence) {
		System.out.format("********************  %s -> No Smoothing Probability ***************************** ",whichSentence);
		System.out.println();
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tempToken = sentence.split(" ");
		System.out.print(addSpace(" "));
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print("\t" + tempToken[iCounter]);
		}
		System.out.println("\n");
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print(addSpace(tempToken[iCounter]));
			for (int jCounter = 0; jCounter < tempToken.length; jCounter++) {
				String bigram = tempToken[iCounter] + " " + tempToken[jCounter];
				if (bigrams_Map.containsKey(bigram)) {
					System.out
							.print("\t"
									+ dF.format(((double) bigrams_Map.get(bigram) / (double) unigrams_Map
											.get(tempToken[jCounter]))));
				} else {
					System.out.print("\t0");
				}
			}
			System.out.println("\n");
		}
	}
	
	
	public static void addOneSmoothingProb(String sentence, String whichSentence) {
		System.out.format("********************  %s -> Add One Smoothing Probability ***************************** ",whichSentence);
		System.out.println();
		DecimalFormat dF = new DecimalFormat("#.###");
		String[] tempToken = sentence.split(" ");
		System.out.print(addSpace(" "));
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print("\t" + tempToken[iCounter]);
		}
		System.out.println("\n");
		for (int iCounter = 0; iCounter < tempToken.length; iCounter++) {
			System.out.print(addSpace(tempToken[iCounter]));
			for (int jCounter = 0; jCounter < tempToken.length; jCounter++) {
				String bigram = tempToken[iCounter] + " " + tempToken[jCounter];
				if (bigrams_Map.containsKey(bigram)) {
					System.out
							.print("\t"
									+ dF.format(((double) (bigrams_Map
											.get(bigram) + 1) / ((double) unigrams_Map
											.get(tempToken[jCounter]) + unigrams_Map
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