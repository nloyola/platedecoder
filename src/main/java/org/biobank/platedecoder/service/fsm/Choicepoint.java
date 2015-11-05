package org.biobank.platedecoder.service.fsm;

// represents a choicepoint
class Choicepoint<C> extends Component {
   final C                  id;
   final ChoicepointRunner  runner;
   ChoicepointTransition<C> trueBranchTransition;
   ChoicepointTransition<C> falseBranchTransition;

   public Choicepoint(C id, ChoicepointRunner runner) {
      this.id = id;
      this.runner = runner;
   }

   public void addTransition(boolean branch, ChoicepointTransition<C> transition) {
      if (branch) {
         trueBranchTransition = transition;
      } else {
         falseBranchTransition = transition;}
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("choicepoint id: ");
      buf.append(id);
      return buf.toString();
   }
}
