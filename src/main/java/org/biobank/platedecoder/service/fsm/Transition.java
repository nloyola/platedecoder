package org.biobank.platedecoder.service.fsm;

import java.util.Optional;

// base class for transitions
abstract class Transition {
   final Component fromComponent;
   final Component toComponent;
   final Optional<TransitionRunner> runnerMaybe;

   <A extends Component, B extends Component> Transition(
      A                          fromComponent,
      B                          toComponent,
      Optional<TransitionRunner> runnerMaybe) {

      this.fromComponent = fromComponent;
      this.toComponent   = toComponent;
      this.runnerMaybe   =  runnerMaybe;
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(fromComponent);
      buf.append(", ");
      buf.append(toComponent);
      return buf.toString();
   }
}
