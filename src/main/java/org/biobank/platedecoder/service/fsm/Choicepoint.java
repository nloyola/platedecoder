package org.biobank.platedecoder.service.fsm;

// represents a choicepoint
class Choicepoint<C> extends Component {
   final C                   id;
   final ChoicepointRunnable runnable;
   ChoicepointTransition<C>  trueBranchTransition;
   ChoicepointTransition<C>  falseBranchTransition;

   public Choicepoint(C id, ChoicepointRunnable runnable) {
      this.id = id;
      this.runnable = runnable;
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
