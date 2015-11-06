package org.biobank.platedecoder.service.fsm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class used to define a finite state machine (FSM) structure. It includes the standard FSM
 * resources of:
 * <ul>
 *   <li>{@link #addState(java.lang.Object, java.lang.Object) states},</li>
 *   <li>{@link #addChoicepoint choicepoints},</li>
 *   <li>{@link #addTransition transitions},</li>
 *   <li>and {@link #addTransition events}.
 * </ul>
 *
 * <p>This class can be subclassed to create an instance FSM, and then use its methods to set up
 * states, choicepoints, transitions, and events. Only one object of this derived class need exist,
 * since the FSM object only defines the FSM structure, and does not contain the state of the
 * individual instances of the FSM. Subclasses of {@link Fsm} may themselves be subclassed, in order
 * to reconfigure some of the states, choicepoints, transitions, or events, or to change the
 * functions bound to transitions or choicepoints.
 *
 * @param <S>  is an enumerated type used to represent the states of the state machine.
 *
 * @param <C>  is an enumerated type used to represent the choice points used in the state machine.
 *
 * @param <E>  is an enumerated type used to represent the events used by the state machine.
 *
 * @author Nelson Loyola
 *
 */
public class Fsm<S, C, E> {

   // Some of the ideas comes from here:
   //   - http://www.java2s.com/Code/Java/Collections-Data-Structure/AprogrammableFiniteStateMachineimplementation.htm

   // TODO: separate FSM structure from it's data. When this is done the structure, can be
   // generated once for many instances of the same FSM type.

   // TODO: throw an error if more than one transition is defined out of a state for the same event

   //@SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(Fsm.class);

   /**
    * The current state the state machine is in. The current state is set to the first state added
    * to the state machine. If no states have been added, then the value of the current state is
    * empty.
    */
   protected Optional<State<S, E>> currentStateMaybe = Optional.empty();

   /**
    * Used to keep track of all the states in the state machine.
    */
   protected Map<S, State<S, E>> states = new HashMap<>();

   /**
    * Used to keep track of all the choice points in the state machine.
    */
   protected Map<C, Choicepoint<C>> choicepoints = new HashMap<>();

   /**
    * Returns the ID associated with the state machines's current state.
    *
    * @return the ID corresponding to the sate the state machine is currently in. If no states have
    * been added, returns an empty value.
    *
    */
   public Optional<S> getStateId() {
      return currentStateMaybe.map(s -> s.id);
   }

   /**
    * Adds a new state to the state machine.
    *
    * <p> States are inert places where the FSM stops when it is not executing code. There is no
    * code attached to a state. States may be nested inside one another to any level. If this is a
    * substate, then a parent state is also specified. If the parent state is not already present,
    * then an exception is thrown.
    *
    * <p> When the fist state is added, it becomes the current state.
    *
    * <p><em>It is possible to add substates to this state.</em>
    *
    * @param stateId  the ID for the new state.
    *
    * @param parentStateId  the ID of the parent state.
    *
    * @throws NoSuchElementException If the state associated with {@code parentStateId} does not
    * exist.
    *
    * @see #addState(java.lang.Object) addState(S)
    */
   public void addState(S stateId, S parentStateId) {
      State<S, E> parentState = getState(parentStateId);
      addState(stateId, Optional.of(parentState));
   }

   /**
    * Adds a new state to the state machine.
    *
    * <p> If this is the first state to be added, then it is assigned to be the current state.
    *
    * <p>It is possible to add substates to this state.
    *
    * @param stateId  the ID for the new state.
    *
    * @see #addState(java.lang.Object, java.lang.Object) addState(S, S)
    */
   public void addState(S stateId) {
      addState(stateId, Optional.empty());
   }

   /**
    * Adds a choicepoint to the state machine.
    *
    * <p>Choicepoints are points of execution where the FSM decides two go one of two directions,
    * along the {@code true} branch or the {@code false} branch. A callback handler,
    * {@code runnable}, is attached to the choicepoint, and it is the block of code which makes
    * the decision which direction the choicepoint should take. Choicepoints may be chained
    * together, with intermediate transitions, to any level.
    *
    * @param choicepointId  The ID associated with this choicepoint.
    *
    * @param runnable The block of code to execute when this choicepoint is transitioned to. A lambda
    * expression can be used or a method reference.
    */
   public void addChoicepoint(C choicepointId, ChoicepointRunnable runnable) {
      Choicepoint<C> choicePointExists = choicepoints.get(choicepointId);
      if (choicePointExists != null) {
         throw new IllegalArgumentException("choice point already exists with id " + choicepointId);
      }
      choicepoints.put(choicepointId, new Choicepoint<C>(choicepointId, runnable));
   }

   /**
    * Adds a transition, from a state to a state, to the state machine.
    *
    * <p>Transitions connect states and choicepoints to other states and choicepoints. Transitions
    * usually, but not always, have a block of code attached, {@code runnable}, which executes
    * actions required of the FSM as it changes state. Transitions are not restricted to going
    * between states at the same level, and so may cross states' parent-child boundaries.
    *
    * A special case of transition is the self-transition, which a transition that begins and ends
    * on the same state. If a self-transition is traversed the FSM does not change state. An
    * extension of this is if a transition leads to a choicepoint (or choicepoint chain) -- if the
    * final transition ends up on the originating state it does not result in a change of state.
    *
    * <p>Events cause transitions to be traversed, resulting in changes of state. A transition may
    * have multiple events attached. Only transitions originating from states may have events.
    *
    * @param event   The event that triggers this transition.
    *
    * @param fromStateId  The state this transition originates from.
    *
    * @param toStateId  the state this transition terminates at.
    *
    * @param runnable  The block of code to execute when this transition is triggered.
    *
    * @throws NoSuchElementException If the state associated with {@code fromStateId} does not
    * exist, or if the state assiciated with {@code toStateId} does not exist.
    *
    */
   public void addTransition(E event,
                             S fromStateId,
                             S toStateId,
                             TransitionRunnable runnable) {
      State<S, E> fromState = getState(fromStateId);
      State<S, E> toState   = getState(toStateId);
      fromState.addTransition(new StateTransition<S, E>(event,
                                                        fromState,
                                                        toState,
                                                        Optional.of(runnable)));
   }

   /**
    * Adds a transition, from a state to a choicepoint, to the state machine, with a block of
    * code.
    *
    * @param event   The event that triggers this transition.
    *
    * @param fromStateId  The state this transition originates from.
    *
    * @param toChoicepointId  the choicepoint this transition terminates at.
    *
    * @param runnable  The block of code to execute when this transition is triggered.
    *
    * @throws NoSuchElementException If the state associated with {@code fromStateId} does not
    * exist, or if the choicepoint assiciated with {@code toChoicepointId} does not exist.
    *
    * @see #addTransition addTransition
    */
   public void addTransitionToChoice(E event,
                                     S fromStateId,
                                     C toChoicepointId,
                                     TransitionRunnable runnable) {
      addTransitionToChoice(event,
                            fromStateId,
                            toChoicepointId,
                            Optional.of(runnable));
   }

   /**
    * Adds a transition, from a state to a choicepoint, to the state machine, without a block of
    * code to execute.
    *
    * @param event   The event that triggers this transition.
    *
    * @param fromStateId  The state this transition originates from.
    *
    * @param toChoicepointId  the choicepoint this transition terminates at.
    *
    * @throws NoSuchElementException If the state associated with {@code fromStateId} does not
    * exist, or if the choicepoint assiciated with {@code toChoicepointId} does not exist.
    *
    * @see #addTransition addTransition
    */
   public void addTransitionToChoice(E event,
                                     S fromStateId,
                                     C toChoicepointId) {
      addTransitionToChoice(event,
                            fromStateId,
                            toChoicepointId,
                            Optional.empty());
   }

   /**
    * Adds a transition, from a choicepoint to a state, to the state machine, with a block of
    * code to execute.
    *
    * @param fromChoicepointId  The choicepoint this transition originates from.
    *
    * @param startChoiceBranch Either the {@code true} or {@code false} branch of the choicepoint.
    *
    * @param toStateId  the state this transition terminates at.
    *
    * @param runnable  The block of code to execute when this transition is triggered.
    *
    * @throws NoSuchElementException If the choicepoint associated with {@code fromStateId} does not
    * exist, or if the state assiciated with {@code toStateId} does not exist.
    *
    * @see #addTransition addTransition
    */
   public void addTransitionFromChoiceToState(C fromChoicepointId,
                                              boolean startChoiceBranch,
                                              S toStateId,
                                              TransitionRunnable runnable) {

      addTransitionFromChoiceToState(fromChoicepointId,
                                     startChoiceBranch,
                                     toStateId,
                                     Optional.of(runnable));
   }

   /**
    * Adds a transition, from a choicepoint to a state, to the state machine, without a block of
    * code to execute.
    *
    * @param fromChoicepointId  The choicepoint this transition originates from.
    *
    * @param startChoiceBranch Either the {@code true} or {@code false} branch of the
    * choicepoint.
    *
    * @param toStateId  the state this transition terminates at.
    *
    * @throws NoSuchElementException If the choicepoint associated with {@code fromStateId} does not
    * exist, or if the state assiciated with {@code toStateId} does not exist.
    *
    * @see #addTransition addTransition
    */
   public void addTransitionFromChoiceToState(C fromChoicepointId,
                                              boolean startChoiceBranch,
                                              S toStateId) {
      addTransitionFromChoiceToState(fromChoicepointId,
                                     startChoiceBranch,
                                     toStateId,
                                     Optional.empty());
   }

   /**
    * Adds a transition, from a choicepoint to a choicepoint, to the state machine.
    *
    * @param fromChoicepointId  The choicepoint this transition originates from.
    *
    * @param startChoiceBranch Either the {@code true} or {@code false} branch of the choicepoint.
    *
    * @param toChoicepointId  the choicepoint this transition terminates at.
    *
    * @throws NoSuchElementException If the choicepoint associated with {@code fromStateId} does not
    * exist, or if the choicepoing assiciated with {@code toChoicepointId} does not exist.
    *
    * @see #addTransition addTransition
    */
   public void addTransitionFromChoiceToChoice(C fromChoicepointId,
                                               boolean startChoiceBranch,
                                               C toChoicepointId) {
      Choicepoint<C> fromChoicepoint = getChoicepoint(fromChoicepointId);
      Choicepoint<C> toChoicepoint = getChoicepoint(toChoicepointId);
      fromChoicepoint.addTransition(startChoiceBranch,
                                    new ChoicepointTransition<C>(fromChoicepoint,
                                                                 startChoiceBranch,
                                                                 toChoicepoint));
   }

   /**
    * Validates that the state machine is valid.
    *
    * <p>This method should be called by subclasses to validate that the FSM has been built
    * correctly. If the validation fails, then an exception is thrown.
    *
    * <p>Ensures the following:
    *   <ul>
    *     <li>all child states have at least one transition.</li>
    *     <li>all choicepoints have a transition into them</li>
    *   </ul>
    *
    * @throws IllegalStateException when the validation fails.
    */
   public void validate() {
      Set<State<S, E>> parentStates = states.values().stream()
         .filter(state -> state.parentStateMaybe.isPresent())
         .map(state -> state.parentStateMaybe.get())
         .collect(Collectors.toSet());

      String stateIdsNoTransitions = states.values().stream()
         .filter(state -> state.transitions.isEmpty() && !parentStates.contains(state))
         .map(state -> state.id.toString())
         .collect(Collectors.joining(", "));

      if (!stateIdsNoTransitions.isEmpty()) {
         throw new IllegalStateException(
            "no transitions lead to these states: " + stateIdsNoTransitions);
      }

      validateChoicepoints();
   }

   // ensures that all choicepoints have at least one transition that leads to them
   @SuppressWarnings("unchecked")
   private void validateChoicepoints() {
      Set<Choicepoint<C>> transitionedTo = new HashSet<>();

      for (State<S, E> state : states.values()) {
         for (Transition transition : state.transitions.values()) {
            if (transition.toComponent instanceof Choicepoint) {
               transitionedTo.add((Choicepoint<C>) transition.toComponent);
            }
         }
      }

      for (Choicepoint<C> choicepoint : choicepoints.values()) {
         if (choicepoint.trueBranchTransition.toComponent instanceof Choicepoint) {
            transitionedTo.add((Choicepoint<C>) choicepoint.trueBranchTransition.toComponent);
         }
         if (choicepoint.falseBranchTransition.toComponent instanceof Choicepoint) {
            transitionedTo.add((Choicepoint<C>) choicepoint.falseBranchTransition.toComponent);
         }
      }

      Set<Choicepoint<C>> notTransitionedTo = new HashSet<>(choicepoints.values());
      notTransitionedTo.removeAll(transitionedTo);

      if (!notTransitionedTo.isEmpty()) {
         throw new IllegalStateException(
            "no transitions lead to these choicepoints: " +
            notTransitionedTo.stream().map(cp -> cp.id.toString()).collect(Collectors.joining(", ")));
      }
   }

   /**
    * Invokes the FSM with the given event.
    *
    * @param event  The event to feed to the state machine.
    */
   public void feedEvent(E event) {
      if (!currentStateMaybe.isPresent()) {
         throw new IllegalStateException("FSM has no states");
      }

      Transition transition = findTransitionForCurrentState(event);
      if (transition == null) {
         LOG.error("transition not found: state: {}, event: {}", currentStateMaybe.get(), event);
         return;
      }
      invokeTransitionComponent(transition);
   }

   // adds a state to the state machine.
   private void addState(S stateId, Optional<State<S, E>> parentStateMaybe) {
      State<S, E> stateExists = states.get(stateId);
      if (stateExists != null) {
         throw new IllegalArgumentException("state already exists with id " + stateId);
      }

      boolean initialState = states.isEmpty();

      State<S, E> state = new State<S, E>(stateId, parentStateMaybe);
      states.put(stateId, state);

      if (initialState) {
         currentStateMaybe = Optional.of(state);
      }
   }

   // returns the state with the associated ID
   private State<S, E> getState(S stateId) {
      State<S, E> state = states.get(stateId);
      if (state == null) {
         throw new NoSuchElementException("invalid state id requested: " + stateId);
      }
      return state;
   }

   // returns the choice point with the associated ID
   private Choicepoint<C> getChoicepoint(C choicepointId) {
      Choicepoint<C> choicepoint = choicepoints.get(choicepointId);
      if (choicepoint == null) {
         throw new NoSuchElementException("invalid choice point id requested: " + choicepointId);
      }
      return choicepoint;
   }

   // internal method to add a transition
   private void addTransitionToChoice(E event,
                                      S fromStateId,
                                      C toChoicepointId,
                                      Optional<TransitionRunnable> runnableMaybe) {
      State<S, E> fromState = getState(fromStateId);
      Choicepoint<C> choicepoint = getChoicepoint(toChoicepointId);
      fromState.addTransition(new StateTransition<S, E>(event,
                                                        fromState,
                                                        choicepoint,
                                                        runnableMaybe));
   }

   private void addTransitionFromChoiceToState(C fromChoicepointId,
                                               boolean startChoiceBranch,
                                               S toStateId,
                                               Optional<TransitionRunnable> runnableMaybe) {
      Choicepoint<C> choicepoint = getChoicepoint(fromChoicepointId);
      State<S, E> toState = getState(toStateId);
      choicepoint.addTransition(startChoiceBranch,
                                new ChoicepointTransition<C>(choicepoint,
                                                             startChoiceBranch,
                                                             toState,
                                                             runnableMaybe));
   }

   /*
    * Traverses through the transitions, through all choice points, till the next state is found.
    */
   @SuppressWarnings("unchecked")
   private void invokeTransitionComponent(Transition transition) {
      LOG.debug("transition found: {}", transition);
      transition.runnableMaybe.ifPresent(runnable -> runnable.run());
      if (transition.toComponent instanceof State) {
         currentStateMaybe = Optional.of((State<S, E>) transition.toComponent);
      } else if (transition.toComponent instanceof Choicepoint) {
         Choicepoint<C> choicepoint = (Choicepoint<C>) transition.toComponent;

         Transition nextTransition = choicepoint.runnable.getAsBoolean()
            ? choicepoint.trueBranchTransition : choicepoint.falseBranchTransition;

         invokeTransitionComponent(nextTransition);
      }
   }

   /**
    * Attemtps to find a transition with matching event for the current state. If no transition
    * is found, then the parent states are searched.
    */
   private Transition findTransition(Optional<State<S, E>> startStateMaybe, E event) {
      if (!startStateMaybe.isPresent()) {
         throw new IllegalArgumentException("cannot find transition for invalid state: "
                                            + startStateMaybe);
      }

      Optional<State<S, E>> stateMaybe = currentStateMaybe;
      while (stateMaybe.isPresent()) {
         State<S, E> state = stateMaybe.get();
         Transition t = state.transitions.get(event);
         if (t != null) {
            return t;
         }
         stateMaybe = state.parentStateMaybe;
      }
      LOG.warn("state does not handle event: [ state: {}, event: {} ]",
               currentStateMaybe.get(), event);
      return null;
   }

   private Transition findTransitionForCurrentState(E event) {
      return findTransition(currentStateMaybe, event);
   }
}
