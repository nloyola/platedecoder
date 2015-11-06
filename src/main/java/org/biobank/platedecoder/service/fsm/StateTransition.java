package org.biobank.platedecoder.service.fsm;

import java.util.Optional;

// used for transitions originating at a state
class StateTransition<S, E> extends Transition {
   final E event;

   <A extends Component> StateTransition(E                          event,
                                         State<S, E>                fromState,
                                         A                          toComponent,
                                         Optional<TransitionRunnable> runnableMaybe) {
      super(fromState, toComponent, runnableMaybe);
      this.event = event;
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("event: ");
      buf.append(event);
      buf.append(", ");
      buf.append(super.toString());
      return buf.toString();
   }
}
