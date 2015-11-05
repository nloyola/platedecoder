package org.biobank.platedecoder.service.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// represents an FSM state
class State<S, E> extends Component {
   final S id;
   final Optional<State<S, E>> parentStateMaybe;
   final Map<E, Transition> transitions = new HashMap<>();

   public State(S id, Optional<State<S, E>> parentStateMaybe) {
      this.id = id;
      this.parentStateMaybe = parentStateMaybe;
   }

   public void addTransition(StateTransition<S, E> transition) {
      transitions.put(transition.event, transition);
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("state id: ");
      buf.append(id);
      return buf.toString();
   }
}
