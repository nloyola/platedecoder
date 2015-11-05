package org.biobank.platedecoder.service.fsm;

import java.util.Optional;

// used for transitions originating at a choicepoint
class ChoicepointTransition<C> extends Transition {
   final boolean startChoiceBranch;

   <A extends Component> ChoicepointTransition(Choicepoint<C> fromChoicepoint,
                                               boolean        startChoiceBranch,
                                               A              toComponent) {
      super(fromChoicepoint, toComponent, Optional.empty());
      this.startChoiceBranch = startChoiceBranch;
   }

   <A extends Component> ChoicepointTransition(Choicepoint<C>             fromChoicepoint,
                                               boolean                    startChoiceBranch,
                                               A                          toComponent,
                                               Optional<TransitionRunner> runnerMaybe) {
      super(fromChoicepoint, toComponent, runnerMaybe);
      this.startChoiceBranch = startChoiceBranch;
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("branch: ");
      buf.append(startChoiceBranch);
      buf.append(", ");
      buf.append(super.toString());
      return buf.toString();
   }
}
