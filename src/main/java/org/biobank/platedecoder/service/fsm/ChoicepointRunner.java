package org.biobank.platedecoder.service.fsm;

/**
 * A block of code that is executed when a choicepoint is executed by the state machine.
 *
 * @see Fsm#addChoicepoint
 */
public interface ChoicepointRunner {

   /**
    * Invoked when a choicepoint is taken by the state machine as a result of processing an event.
    *
    * @return a boolean value that corrsponds with the transition taken from the choicepoint.
    */
   public boolean run();
}
