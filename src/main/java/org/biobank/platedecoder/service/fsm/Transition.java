package org.biobank.platedecoder.service.fsm;

import java.util.Optional;

// base class for transitions
abstract class Transition {
   final Component fromComponent;
   final Component toComponent;
   final Optional<TransitionRunnable> runnableMaybe;

   <A extends Component, B extends Component> Transition(
      A                          fromComponent,
      B                          toComponent,
      Optional<TransitionRunnable> runnableMaybe) {

      this.fromComponent = fromComponent;
      this.toComponent   = toComponent;
      this.runnableMaybe   =  runnableMaybe;
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
