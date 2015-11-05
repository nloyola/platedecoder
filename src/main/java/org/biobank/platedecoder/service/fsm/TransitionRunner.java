package org.biobank.platedecoder.service.fsm;

/**
 * A block of code that is executed when a transition is taken by the state machine.
 *
 * @see Fsm#addTransition
 */
public interface TransitionRunner {

   /**
    * Invoked when the transition is taken by the state machine as a result of processing an event.
    */
   public void run();
}
