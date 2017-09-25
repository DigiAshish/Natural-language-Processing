import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HMM {
	private final String	 INITIAL	= "InitialState";
	private final List<String> states;
	private final Map<String, Map<String, Double>>	transitions;
	private final Map<String, Map<Integer, Double>>	emissions;

	public HMM() {
		this.states = new ArrayList<>();
		this.transitions = new HashMap<>();
		this.emissions = new HashMap<>();
	}

	private void add(final String state, final Map<String, Double> transitionMap, final Map<Integer, Double> emissionMap) {
		this.states.add(state);

		// Build transition matrix
		for (final Entry<String, Double> entry : transitionMap.entrySet()) {
			this.transitions.putIfAbsent(state, new HashMap<>());
			this.transitions.get(state).put(entry.getKey(), entry.getValue());
		}

		// Build emission matrix
		for (final Entry<Integer, Double> entry : emissionMap.entrySet()) {
			this.emissions.putIfAbsent(state, new HashMap<>());
			this.emissions.get(state).put(entry.getKey(), entry.getValue());
		}
	}


	private List<String> predict(final char[] observations) {
		// Initialize the resulting states
		final List<String> result = new ArrayList<>();
		int index = -1;

		// Initialize the probabilities and backpointers
		final Map<Integer, Map<String, String>> pointers = new HashMap<>();
		final Map<Integer, Map<String, Double>> probs = new HashMap<>();
		probs.putIfAbsent(index, new HashMap<>());
		probs.get(index).put(this.INITIAL, 1.0);

		// Update probabilities for each observation
		for (final Character observation : observations) {
			index++;
			for (final String state : this.states) {
				// Calculate probabilities of transition from previous state to this state and emission of the current observation
				final Map<String, Double> pathProbs = this.calculateTransitionProb(index, probs, observation, state);

				// Select previous state with maximum probability
				this.setBestState(index, pointers, probs, state, pathProbs);
			}
		}

		// Get the best final state
		String bestState = probs.get(index).entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();

		// Follow the back pointers to get the sequence
		for (; index > -1; index--) {
			result.add(bestState);
			bestState = pointers.get(index).get(bestState);
		}

		// Reverse and return the result
		Collections.reverse(result);
		return result;
	}

	private void setBestState(final int index,
			final Map<Integer, Map<String, String>> pointers,
			final Map<Integer, Map<String, Double>> probs,
			final String state,
			final Map<String, Double> pathProbs) {
		final String bestState = pathProbs.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
		probs.putIfAbsent(index, new HashMap<>());
		probs.get(index).put(state, pathProbs.get(bestState));
		pointers.putIfAbsent(index, new HashMap<>());
		pointers.get(index).put(state, bestState);
	}

	private Map<String, Double> calculateTransitionProb(final int index,
			final Map<Integer, Map<String, Double>> probs,
			final Character observation,
			final String state) throws NumberFormatException {
		final Map<String, Double> pathProbs = new HashMap<>();
		for (final String prevState : this.states) {
			pathProbs.put(prevState,
					probs.getOrDefault(index - 1, new HashMap<>()).getOrDefault(prevState, 0.0) *
					this.transitions.get(prevState).getOrDefault(state, 0.0) *
					this.emissions.getOrDefault(state, new HashMap<>()).getOrDefault(Integer.parseInt(observation.toString()), 0.0));
		}

		return pathProbs;
	}

	public static void main(final String[] args) {
		// Initialize and create the transition and emission matrix with static values
		final HMM hmm = setup();

		// Validate the command line argument
		if (args.length > 1) {
			System.out.println("Only one argument allowed!!");
		} else if (args.length == 1) {
			// If provided, use that as input and predict for it
			final String input = args[0];
			try {
				System.out.println(input + ": " + String.join("", hmm.predict(input.toCharArray())));
			} catch (final NumberFormatException exception) {
				System.out.println(input + " invalid! Should consist of numbers only");
			}
		} else {
			// Predict for each value in the question stated and print the result
			System.out.println("1112223332: " + String.join("", hmm.predict("1112223332".toCharArray())));
			System.out.println("1231231232: " + String.join("", hmm.predict("1231231232".toCharArray())));
		}
	}

	private static HMM setup() {
		final HMM hmm = new HMM();
		Map<String, Double> transitionMap = new HashMap<>();
		transitionMap.put("H", 0.8);
		transitionMap.put("C", 0.2);
		hmm.add(hmm.INITIAL, transitionMap, new HashMap<>());

		transitionMap = new HashMap<>();
		transitionMap.put("H", 0.7);
		transitionMap.put("C", 0.3);
		Map<Integer, Double> emissionMap = new HashMap<>();
		emissionMap.put(1, 0.4);
		emissionMap.put(2, 0.3);
		emissionMap.put(3, 0.3);
		hmm.add("H", transitionMap, emissionMap);

		transitionMap = new HashMap<>();
		transitionMap.put("H", 0.4);
		transitionMap.put("C", 0.6);
		emissionMap = new HashMap<>();
		emissionMap.put(1, 0.4);
		emissionMap.put(2, 0.4);
		emissionMap.put(3, 0.2);
		hmm.add("C", transitionMap, emissionMap);
		return hmm;
	}
}