package org.biobank.platedecoder.service.fsm;

import java.util.function.BooleanSupplier;

/**
 * A block of code that is executed when a choicepoint is reached by the state machine.
 *
 * @see Fsm#addChoicepoint
 */
public interface ChoicepointRunnable extends BooleanSupplier {}
