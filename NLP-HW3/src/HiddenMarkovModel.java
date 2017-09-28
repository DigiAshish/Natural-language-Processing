import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HiddenMarkovModel {
	private final String	 Start	= "Start State";
	private final List<String> states;
	private final Map<String, Map<String, Double>>	transitionsProbMatrix;
	private final Map<String, Map<Integer, Double>>	emissionsProbMatrix;

	public static void main(final String[] args) {
		final HiddenMarkovModel hmm = setup();
		if (args.length > 1) {
			System.out.println("Please input a Number consisting of only 1,2,3. Should be max 10 digit. Ex : 321232");
		} 
		else
		{
			final String input = args[0];
			System.out.println(input + ": " + String.join("", hmm.computeLikelihood(input.toCharArray())));
		} 
	}

	private static HiddenMarkovModel setup() {
		final HiddenMarkovModel hmm = new HiddenMarkovModel();
		Map<String, Double> transitionMap = new HashMap<>();
		transitionMap.put("H", 0.8);
		transitionMap.put("C", 0.2);
		hmm.addToState(hmm.Start, transitionMap, new HashMap<>());
		
		transitionMap = new HashMap<>();
		transitionMap.put("H", 0.7);
		transitionMap.put("C", 0.3);		
		Map<Integer, Double> emissionMap = new HashMap<>();
		emissionMap.put(1, 0.4);
		emissionMap.put(2, 0.3);
		emissionMap.put(3, 0.3);
		hmm.addToState("H", transitionMap, emissionMap);

		transitionMap = new HashMap<>();
		transitionMap.put("H", 0.4);
		transitionMap.put("C", 0.6);
		emissionMap = new HashMap<>();
		emissionMap.put(1, 0.4);
		emissionMap.put(2, 0.4);
		emissionMap.put(3, 0.2);
		hmm.addToState("C", transitionMap, emissionMap);
		return hmm;
	}
	
	public HiddenMarkovModel() {
		this.states = new ArrayList<>();
		this.transitionsProbMatrix = new HashMap<>();
		this.emissionsProbMatrix = new HashMap<>();
	}

	private void addToState(final String state, final Map<String, Double> transitionMap, final Map<Integer, Double> emissionMap) {
		this.states.add(state);

		for (final Entry<String, Double> entry : transitionMap.entrySet()) {
			this.transitionsProbMatrix.putIfAbsent(state, new HashMap<>());
			this.transitionsProbMatrix.get(state).put(entry.getKey(), entry.getValue());
		}

		for (final Entry<Integer, Double> entry : emissionMap.entrySet()) {
			this.emissionsProbMatrix.putIfAbsent(state, new HashMap<>());
			this.emissionsProbMatrix.get(state).put(entry.getKey(), entry.getValue());
		}
	}


	private List<String> computeLikelihood(final char[] observations) {
		final List<String> result = new ArrayList<>();
		int index = -1;

		final Map<Integer, Map<String, String>> pointers = new HashMap<>();
		final Map<Integer, Map<String, Double>> probs = new HashMap<>();
		probs.putIfAbsent(index, new HashMap<>());
		probs.get(index).put(this.Start, 1.0);

		for (final Character observation : observations) {
			index++;
			for (final String state : this.states) {
				final Map<String, Double> pathProbs = this.TransitionProbability(index, probs, observation, state);
				this.setBestState(index, pointers, probs, state, pathProbs);
			}
		}
		String bestState = probs.get(index).entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
		for (; index > -1; index--) {
			result.add(bestState);
			bestState = pointers.get(index).get(bestState);
		}
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

	private Map<String, Double> TransitionProbability(final int index,
			final Map<Integer, Map<String, Double>> probs,
			final Character observation,
			final String state) throws NumberFormatException {
		final Map<String, Double> pathProbs = new HashMap<>();
		for (final String prevState : this.states) {
			pathProbs.put(prevState,
					probs.getOrDefault(index - 1, new HashMap<>()).getOrDefault(prevState, 0.0) *
					this.transitionsProbMatrix.get(prevState).getOrDefault(state, 0.0) *
					this.emissionsProbMatrix.getOrDefault(state, new HashMap<>()).getOrDefault(Integer.parseInt(observation.toString()), 0.0));
		}

		return pathProbs;
	}
}